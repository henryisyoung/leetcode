# System Design — Ego (and Multi-Agent) Behavior Prediction Model

> AV interview prompt: design a behavior-prediction model for autonomous vehicles
> that consumes real-time sensor signals (ego speed/heading/position plus the scene
> around the AV) and emits future trajectories suitable for downstream planning.
> Cover I/O, uncertainty quantification, and the contract with downstream modules.

This is the model that sits between **perception** (what's around me right now) and **planning** (what should I do next). Its job is to forecast, with calibrated uncertainty, what every dynamic agent — including the ego — is likely to do in the next 1–8 seconds.

---

## Phase 0: Pin Down the Scope

The phrase "ego behavior prediction" can mean two different things, and the answer drives the whole design:

| Variant | Predicts | When you'd build it |
| --- | --- | --- |
| **A. Ego-only forecast** | The AV's own trajectory given its current state and the *commanded plan*. | Sim / shadow-mode evaluation, "imitation learning" baseline, calibrating the planner's open-loop model. |
| **B. Multi-agent forecast (ego + others)** | Trajectories of all visible agents — pedestrians, cyclists, other vehicles, and optionally the ego. | The production prediction stack that planning consumes. |

> **Decision**: build variant **B**, because the prompt explicitly says "effective output for downstream modules such as path planning or decision systems." A planner that only knows where the ego will go is not useful — it needs to know what *everyone else* is doing too. The ego head is a free addition (same architecture, different agent slot).

### Functional requirements

- **Forecast every tracked agent** (≤ N=64) for a fixed horizon (8 s @ 10 Hz = 80 timesteps).
- Output is **multi-modal** — each agent gets K candidate futures (typical K=6) with probabilities that sum to 1.
- **Map-aware**: trajectories must respect lane geometry, drivable area, traffic light state, stop lines.
- **Interactive**: an agent's prediction can depend on what other agents are doing (a car yielding to the AV looks different from a car ignoring it).
- **Real-time**: every prediction is consumed by the next planner tick.

### Non-functional requirements

| Requirement | Target | Why |
| --- | --- | --- |
| End-to-end p99 latency | < 50 ms on AV compute | Planning runs at 10 Hz; budget is ~40 ms for prediction, ~40 ms for planning, ~20 ms for everything else. |
| Throughput | 10 Hz, on-vehicle | Real-time, fixed. |
| Horizon | 8 s | Long enough for lane changes / intersections; short enough that uncertainty doesn't blow up. |
| Calibration | NLL on validation set within 5% of train | Mis-calibrated uncertainty is worse than no uncertainty; the planner believes the probabilities. |
| Determinism | Bit-exact for replay | For debugging crashes; non-deterministic CUDA ops are disabled in release. |
| Memory | < 4 GB on the AV GPU/NPU | Coexists with perception, mapping, planning models. |
| Failure mode | Degrade gracefully | If the model OOM's or NaNs, fall back to a constant-velocity predictor with high uncertainty. |

### Out of scope for this design

- Perception (object detection / tracking) — assumed upstream.
- HD map building — assumed upstream and pre-loaded.
- Planning — described only at the interface boundary.
- Long-horizon route-level intent ("the car is going to the airport"). We forecast 8 s; intent over minutes is a different problem.

---

## Phase 1: Inputs and Outputs

This is the most important boundary in the system. Get the I/O contract right and the rest is implementation choice.

### Input — what the model sees at time *t*

For each tick at time *t*, the model receives:

**1. Ego state** (15 floats, exact frame)

```text
position           : x, y, z      (in map frame)
velocity           : vx, vy
acceleration       : ax, ay
heading + yaw_rate : theta, omega
current command    : steering, throttle, brake          (from the planner's last cycle)
chassis derived    : lateral_accel, longitudinal_jerk
```

The ego is just "agent 0" with privileged access to its own internal commands.

**2. Tracked agents** — for each of the N ≤ 64 visible agents, the last *H* = 2 s of history (20 timesteps @ 10 Hz):

```text
per agent, per timestep
├── x, y, z, vx, vy, ax, ay, theta, omega
├── bounding box: length, width, height
├── class: VEHICLE | PEDESTRIAN | CYCLIST | MOTORCYCLE | UNKNOWN
├── tracker confidence ∈ [0, 1]
└── valid_mask                  (history may be partial — agent appeared mid-window)
```

Masking matters: agents with < 0.5 s of history get a special "new track" embedding. We don't drop them — pedestrians frequently appear with one frame of history.

**3. Map context** — vector representation of static scene:

```text
lanes        : polyline of (x, y, heading, speed_limit, lane_type)
crosswalks   : polygons
stop_signs   : point + direction
traffic_lights : state ∈ {RED, YELLOW, GREEN, FLASHING_*, UNKNOWN}, time_since_change
drivable_area: polygon
road_edges   : polylines (curbs, fences)
```

We **don't** use a rasterized BEV image. Vector representations (à la VectorNet, SceneTransformer, Wayformer) are cheaper, scale-invariant, and give better lane-following behavior than rasters. Rasters are still fine if you have spare GPU memory and want a simpler architecture.

**4. Frame conventions**

- Everything is in a **local frame anchored on the ego at time t** (ego at origin, heading +x). Equivariance: the model behaves the same whether the AV is heading north or south.
- Time is normalized to seconds; positions in meters.

### Output — what planning consumes

```text
per agent (including ego):
  modes[1..K]                       (default K = 6)
  ├── trajectory_xy[T]              (T = 80 future timesteps, 0.1 s spacing, 8 s horizon)
  ├── trajectory_heading[T]         (optional but very useful for planner cost)
  ├── covariance[T]                 (2×2 Σ_t per timestep — see "Uncertainty" below)
  └── probability                   (∈ [0, 1], modes sum to 1)
  most_likely_mode                  (argmax for cheap consumers)
  scene_consistency_score           (optional; see SceneTransformer-style joint mode)
```

Two key choices worth defending:

1. **K=6 modes**, not 1 and not 64. In practice, ~3 modes capture "go straight / turn left / turn right" and 5–6 captures the long tail (lane change, double-park, slow down). More than 8 buys very little but explodes parameter count in the head. Industry standards (Waymo MotionLM, Argoverse benchmarks) cluster around K=6.
2. **Per-timestep covariance**, not just per-mode probability. The planner needs to know "this trajectory says the car is at (x, y) ± σ" because cost functions depend on overlap, not just point distance. A 2×2 covariance per timestep ≈ 3 extra floats per mode per step (Σxx, Σyy, Σxy with Σ symmetric).

### Auxiliary outputs

- **Occupancy grid** (optional, 20 × 20 cells × 8 s): for very dense urban scenes, a fixed-grid probability is more tractable for the planner than 100+ multi-modal trajectories. We can produce both heads from a shared encoder.
- **Interaction graph**: edges between agents that the model predicts will influence each other. Useful for explainability and for the planner's game-theoretic reasoning.

---

## Phase 2: Architecture

### Pipeline at a glance

```
                    ┌─────────────────────────┐
sensor → tracker →  │  agent histories (N×H)  │   →  ┐
HD map        →     │  vector map polylines    │   →  │  Encoder
traffic light →     │  TL state, road edges    │   →  │  (Transformer
ego planner   →     │  ego command, plan       │   →  │   over polylines)
                    └─────────────────────────┘     │
                                                     ↓
                                          Per-agent embedding  →  Decoder
                                                                  (K modes ×
                                                                   T steps)
                                                                  ↓
                                                          trajectories +
                                                          covariances +
                                                          mode probabilities
```

### Step 1 — Polyline encoder

Group every input modality into **polylines** (Wayformer / VectorNet trick):
- An agent's history is a polyline of state vectors.
- A lane center-line is a polyline of (x, y, heading) waypoints.
- A road edge is a polyline of curb points.

Each polyline → small MLP / 1-D CNN → fixed-dim embedding (e.g. 128). This unifies "I have 20 history points" and "I have 50 lane points" into a single "set of vectors".

### Step 2 — Scene Transformer

Run a 4–8 layer Transformer over the set of polyline embeddings. Self-attention learns:
- "agent A is following lane L" (agent ↔ map edge)
- "agent A is yielding to agent B" (agent ↔ agent edge)
- "the traffic light state matters for everyone in this lane group"

Why Transformer and not graph-conv: the relationships are not local. A car 50 m ahead is more relevant than one 2 m to the side. Attention naturally learns the "important neighbors" weighting.

### Step 3 — Per-agent decoder

For each agent we want to forecast (ego + the up-to-N others), the decoder is a small Transformer or MLP that:

1. Conditions on the agent's encoder embedding + a learned **mode query** (one per mode, K queries total).
2. Outputs the K trajectories autoregressively or in one shot.
3. Outputs the K probabilities via a small classifier head (softmax over modes).

Two main flavors:

| Decoder style | Pro | Con |
| --- | --- | --- |
| **One-shot regression** (predict all 80 points at once) | Fast, easy to parallelize across agents and modes. | Hard to maintain temporal consistency over long horizons. |
| **Autoregressive** (MotionLM-style: predict point t given 0..t-1) | Better long-horizon coherence, naturally generates samples. | 80× slower at inference unless you parallelize over modes. |

Production AV stacks (Wayformer, MotionLM, MTR) all use the autoregressive flavor with K modes in parallel — they pay the latency for the long-horizon quality.

### Step 4 — Heads

- **Trajectory head**: outputs `(T, 5)` — μ_x, μ_y, log σ_x, log σ_y, ρ — per mode. The `(μ, Σ)` parameterizes a bivariate Gaussian at each timestep.
- **Probability head**: outputs `(K,)` — softmax over modes.
- **(Optional) heading head**: outputs `(T,)` — needed for non-holonomic planning cost.

### Architectural alternatives worth knowing

- **Raster + CNN** (CoverNet, ChauffeurNet): take a top-down image of the scene, run a ResNet, regress trajectories. Simpler but loses lane topology and scales badly with scene size.
- **Vector + GNN** (VectorNet): graph convolution over polyline nodes. Earlier-generation; mostly superseded by Transformer attention.
- **Diffusion** (MotionDiffuser): generate trajectories via denoising. Great sample diversity, very expensive at inference (~10–50 denoising steps).

For 2026 production AVs, the Wayformer-family (vector + Transformer + GMM head) is the canonical answer.

---

## Phase 3: Uncertainty Quantification

This is the part most candidates breeze past. The planner cares more about **calibrated uncertainty** than point-estimate accuracy — a confident wrong answer is worse than a hesitant correct one.

### Two kinds of uncertainty to model separately

| Type | What it is | How we capture it |
| --- | --- | --- |
| **Aleatoric** | Inherent randomness in agent behavior — even a perfect predictor can't know if a pedestrian will jaywalk. | Per-mode, per-timestep Gaussian covariance `Σ_t`. The trajectory head emits `(μ_t, Σ_t)`. |
| **Epistemic** | The model's uncertainty about its own predictions — "I haven't seen this kind of scene in training." | Multi-modality (K modes), deep ensembles, Monte Carlo dropout. Mode probabilities approximate this. |

### Loss function (training signal for both)

The loss combines a **regression term** and a **classification term**:

```
L = -log p(mode*) + NLL_Gaussian(trajectory_mode*, ground_truth)
```

where `mode*` is the mode whose trajectory is closest to ground truth (the standard "winner-takes-all" trick from MultiPath / Wayformer).

Plus auxiliary terms in practice:
- **min-of-K** loss on FDE (final-displacement error) for one specific mode each batch.
- **Heading loss** (cosine) to keep yaw aligned with motion.
- **Lane-following / off-road penalty** — small auxiliary head saying "this trajectory leaves the drivable area" and we penalize it.

### Calibration

A model that says "60% confident" should be right 60% of the time. We measure this with:

- **Reliability diagrams** on the mode-probability head — bucket predictions by claimed confidence and check empirical accuracy.
- **ECE (Expected Calibration Error)** on a held-out set.
- **Per-class checks** — pedestrians vs. vehicles often have wildly different calibration profiles.

If calibration is off, we apply **temperature scaling** on the softmax (a single scalar fit on validation data) or **isotonic regression** per agent class. This is cheap and works.

### Epistemic uncertainty in production

- **Deep ensembles** (5 models trained from different seeds, average predictions) is the gold standard but 5× cost. Used offline / for the highest-stakes scenarios.
- **MC Dropout** (keep dropout on at inference, run M=10 passes) is cheaper but biased. Used for shadow-mode evaluation.
- **Single model + good calibration** is what ships on the AV. Cost reality wins.

### When to flag "I don't know"

Hard threshold: if **every mode has probability < 0.4** and the modes disagree by > 5 m of final-displacement, emit a degenerate "high-uncertainty" output and let the planner take a conservative action (slow, increase margin, hand to a human if available). This is the safety net for out-of-distribution scenes.

---

## Phase 4: Contract With Downstream

Two consumer types matter:

### 4a. Path planner

Planning runs at 10 Hz and consumes prediction output every tick. The interface needs to be:

**Stable** — schema can't change between AV release trains. We version the message and run old/new in parallel during transitions.

**Cheap to consume** — the planner does roll-outs over many candidate ego trajectories and needs to score each one against predicted agent trajectories *fast*. So:

- We emit **rasterized occupancy grids** alongside the trajectories. A grid lookup is much cheaper than computing point-to-trajectory distance for 64 agents × K modes × T timesteps.
- We emit **per-timestep covariances** so the planner can compute overlap probability analytically.
- We emit **most-likely mode + full distribution** so trivial consumers (early-out checks, latency-sensitive layers) can use the cheap version and only the optimizer needs the full distribution.

### 4b. Decision / behavior layer

The higher-level decision system (lane-change decisions, route planning, "should I yield or assert?") consumes a richer but lower-frequency view:

- Mode labels with semantic tags ("car_will_yield", "pedestrian_crossing", "lane_change_left") — useful for explainability and for game-theoretic decisions.
- The **interaction graph** edges — "I think these two agents will interact at t≈2 s, location (x, y)" — let the decision module reason about right-of-way.

### Interface example (Protobuf-ish)

```protobuf
message AgentPrediction {
  uint64 agent_id = 1;
  enum AgentClass { VEHICLE = 0; PEDESTRIAN = 1; CYCLIST = 2; ... }
  AgentClass agent_class = 2;

  repeated PredictionMode modes = 3;     // K modes, sum-to-1 probabilities

  message PredictionMode {
    float probability = 1;
    repeated TrajectoryPoint trajectory = 2;   // T = 80
    optional string semantic_label = 3;        // "yield", "go_straight", ...
  }

  message TrajectoryPoint {
    float x = 1; float y = 2;
    float heading = 3;
    float sigma_x = 4; float sigma_y = 5; float rho = 6;   // 2x2 covariance
    float speed = 7;
  }
}

message ScenePrediction {
  uint64 frame_id = 1;
  double timestamp_s = 2;
  uint64 model_version = 3;
  repeated AgentPrediction agents = 4;
  optional OccupancyGrid occupancy = 5;   // optional rasterized alternative
  optional InteractionGraph interactions = 6;
}
```

---

## Phase 5: Training and Evaluation

### Data

- **Logged real-world driving data** at 10 Hz. ~10–100 M scenarios for a production model. Each scenario is an 8 s clip + 2 s context.
- **Auto-labeled** with the future trajectory of every tracked agent (we replay perception offline with the agent's actual future).
- **Mined for hard cases**: cut-ins, jaywalkers, near-misses, intersection negotiations. The long tail is what makes or breaks AV models.
- **Augmented**: random rotations (since the model is supposed to be heading-invariant), horizontal flips (drive on the right vs. left), mild noise on agent positions to model perception uncertainty.

### Offline metrics

| Metric | What it measures |
| --- | --- |
| **minADE_K** | Average displacement error of the closest-to-GT mode. The standard. |
| **minFDE_K** | Final-displacement error of the closest mode. |
| **Miss rate @ 2 m FDE** | Fraction of agents where no mode lands within 2 m of GT. |
| **NLL** | Negative log-likelihood under the predicted GMM. The calibration-aware metric. |
| **Mode coverage** | Diversity — do modes meaningfully differ, or are they near-duplicates? |
| **Lane-IoU / off-road rate** | How often does a predicted trajectory leave the drivable area? |
| **mAP-prediction** (Waymo Open) | Combines accuracy and confidence. |

We monitor all of these by **agent class** (pedestrians are harder than highway vehicles), **horizon** (8 s is harder than 2 s), and **scenario type** (intersection / merge / cut-in).

### Online metrics (shadow mode / staged rollout)

- **Planner stability**: does the planner change its mind when fed our predictions? Excessive replanning = noisy predictions.
- **Safety interventions**: how often does the AV's safety layer (AEB, hard fallback) trigger? Predictions that are off → emergency interventions.
- **Comfort metrics**: jerk, lateral acceleration during prediction-driven planning.

### Continuous evaluation

- **Simulation regression**: a fixed set of ~10K curated scenarios re-run for every model candidate. Metrics published to a dashboard; a model that regresses on any safety-critical scenario is auto-blocked.
- **Counterfactual replay**: run the new model on the same logs the production model saw. Compare predictions; flag agents where the new model would have triggered a different planner action.

---

## Phase 6: Deployment

### On-vehicle stack

- Compiled to **TensorRT** or **OpenVINO** depending on the AV's NPU/GPU.
- **FP16** for everything except the final softmax (FP32 to avoid underflow on the K-way mode distribution).
- **Pre-allocated buffers** — no allocation in the hot path. Model gets a fixed budget for N=64 agents and pads when fewer.
- **Ring buffer** of last 2 s of history kept in shared memory so we don't reconstruct it every tick.

### Model versioning + rollout

1. **Train + offline evaluate** every candidate.
2. **Shadow mode** for ~1 month: run the new model in parallel with production, log predictions but don't act on them. Compare planner outputs.
3. **Staged rollout**: 1% → 10% → 50% → 100% of the fleet, gated on safety metrics at each stage.
4. **Rollback** must be 1-click. The previous N model versions stay on the AV (~50 MB each) so rollback doesn't need a fleet OTA.

### Disagreement detection

A small **ensemble check** runs in production: a much cheaper second model (a constant-velocity / unicycle baseline) runs in parallel. If the main model and the baseline disagree dramatically (e.g. main says "lane change", baseline says "straight" and the cheap one has been right 95% of the time on this scene class), we down-weight the main model's prediction and emit a "high uncertainty" flag.

---

## Phase 7: Failure Modes and Safety

| Failure | How it shows up | Mitigation |
| --- | --- | --- |
| **Out-of-distribution scene** (construction zone, weird intersection) | Mode probabilities flat, high NLL, modes disagree. | Emit high-uncertainty signal; planner pads margin and slows down. |
| **Adversarial input** (lidar spoofing, hostile actors) | Single mode at very high confidence pointing somewhere implausible. | Hard sanity checks (max acceleration ≤ vehicle physics; trajectory must stay within road bounds or be explicitly off-road class). |
| **NaN / inf in output** | Model numerical failure. | Output validator at the boundary; on detection, replace with constant-velocity prediction + 100% σ. |
| **Latency spike** (NPU contention) | Tick misses its 50 ms budget. | Hard timeout; reuse last tick's prediction with extra σ. Log + page on-call. |
| **Model OOM** (rare; agent count spiked) | Fall back to per-agent independent CV model. | Pre-allocated agent slots cap at N=64; agents beyond that get the CV fallback. |
| **Tracker handoff jitter** | Agent ID flips → "new track" embedding → bad short-horizon prediction. | Smooth the embedding via Kalman over the last few ticks if ID stability is below threshold. |

The model's outputs are **untrusted by default** at the safety layer. There's an independent rules-based safety monitor that overrides whenever the model and physics disagree.

---

## Phase 8: Trade-offs and Open Questions

### Trade-offs I'd flag in the interview

- **K (number of modes)**: bigger K → more diversity but more compute and harder to calibrate. K=6 is the sweet spot for production; K=64 only in research benchmarks.
- **Horizon (T)**: 8 s captures intersections but uncertainty grows quadratically. Some teams forecast 8 s for vehicles and only 3 s for pedestrians.
- **Per-agent independence vs. joint prediction**: independent agents are faster but miss interaction (one car yielding to another). Joint prediction (SceneTransformer) is correct but expensive — we get most of the benefit by sharing the encoder.
- **Vector vs. raster scene encoding**: vector wins for lane-following / scalability; raster wins for "I have spare CNN-shaped compute and want a simpler pipeline."
- **Multi-modal vs. samples**: K explicit modes (deterministic) vs. drawing M samples from a generative model (diffusion / MotionLM). Modes are easier to consume; samples capture multimodality more faithfully.

### What I'd want to discuss further with the interviewer

- **How does the planner handle K modes?** Some planners run a separate optimization per most-likely mode and combine; others marginalize. The right output format depends on the planner.
- **Closed-loop training vs. open-loop**: production models are trained open-loop (predict log futures from log pasts). Closed-loop training (predict what would happen if the AV took action X, then update X) requires a simulator and is much harder, but matches deployment better.
- **End-to-end models** (perception + prediction + planning in one network — Waymo's "MotionLM", Tesla's "occupancy networks") are the research direction. They eliminate the prediction/planning boundary, but lose interpretability and complicate validation. Most production stacks still keep the boundary.

---

## Appendix — Reference systems

| System | Key idea | Year |
| --- | --- | --- |
| **VectorNet** | Polyline encoding + GNN. Established the vector-based AV prediction direction. | 2020 |
| **CoverNet** | Trajectory set + classification (predict which trajectory from a pre-defined set). Simple, fast. | 2020 |
| **MultiPath / MultiPath++** | K-mode Gaussian mixture with anchor trajectories. Calibrated and tractable. | 2019, 2022 |
| **SceneTransformer** | Joint multi-agent prediction with attention; scene-consistent samples. | 2021 |
| **Wayformer** | Unified architecture — same encoder for agent / map / TL / interaction. The current canonical answer. | 2022 |
| **MotionLM** | Autoregressive language-model-style trajectory tokenization. | 2023 |
| **MTR / MTRA** | Mode query + iterative refinement decoder; won Waymo benchmarks. | 2022, 2023 |
| **Wayve / Tesla "end-to-end"** | Imitation-learned policy that predicts ego control directly from sensors. Doesn't fit this design's I/O shape but is the research frontier. | 2023+ |

---

## TL;DR for the interview

> "I'd build a **Wayformer-style** vector-based scene Transformer that consumes ego state, last 2 s of all tracked agents, and the vector HD map. The decoder emits **K=6 multi-modal trajectories per agent** with **per-timestep Gaussian covariance** and **softmax mode probabilities**. Uncertainty is split into **aleatoric (per-timestep Σ)** and **epistemic (mode probabilities, optionally deep ensembles)**, with **temperature scaling for calibration**. The output is a **stable Protobuf schema** the planner consumes at 10 Hz; we also emit an **optional occupancy grid** for cheap consumers. On the AV the model is **TensorRT/FP16**, **pre-allocated buffers**, **< 50 ms p99**, with **fallback to a constant-velocity predictor** on numerical failure and an **independent rules-based safety monitor** that overrides model output when physics says it must."
