# Netflix Problem-Solving Interview Round

> Source: Netflix engineering hiring materials. Stored here as reference
> for the problems and approach style under `src/netflix/`.

The Netflix "Problem-Solving" round replaces the traditional single-answer
LeetCode-style technical interview with a more open-ended, real-world
scenario. The bar is not "produce the optimal `O(n)` solution"; it's how
you reason, communicate, scope, and trade off.

---

## Interview Structure

1. **Scenario Introduction** — interviewer presents a realistic engineering
   problem (e.g. designing a feature, troubleshooting a system).
2. **Initial Exploration & Clarification** — candidate asks questions,
   surfaces ambiguities, gathers requirements.
3. **Solution Brainstorming & Scoping** — propose multiple approaches, talk
   through trade-offs, break the problem into actionable parts. Think out
   loud; perfect code is not the goal.
4. **Deep Dive** — interviewer picks one part of the candidate's solution
   to drill into (pseudo-code, data structures, real working code).
5. **Refinement & Next Steps** — articulate a plan to ship: testing,
   rollout, monitoring, success metrics, anticipated challenges.

---

## What's Being Evaluated

| Focus Area | Signal |
|---|---|
| **Problem Understanding** | Grasping the core problem and its context. Handling ambiguity. Sound decisions despite incomplete info. |
| **Requirements Gathering** | Curiosity surfacing hidden requirements/constraints. Challenging requirements where it improves outcomes. Validating assumptions. |
| **Solution Scoping** | Decomposing a large problem into manageable, prioritized parts. Pragmatic real-world solutions to ambiguous problems. |
| **Trade-off Analysis (DS & Algos)** | Comparing approaches, justifying choices. Creative solutions. NOT adding complexity that isn't needed. |
| **Communication** | Clear articulation of thoughts. Asking clarifying questions. Receptiveness to feedback. |

---

## How to Prepare

- **Practice thinking aloud.** Verbalize while brainstorming — silence is
  the worst signal in this round.
- **Practice asking questions.** Always clarify before diving in. Treat
  the prompt as the start of a conversation, not the spec.
- **Lead with trade-offs.** Pros/cons of two competing approaches beats
  one polished answer.
- **Software engineering basics.** Some scenarios touch on testing,
  rollout, monitoring, capacity planning. Speak to those even if asked
  about an algorithm — bonus credit for thinking past code.
- **No "right" answer.** The approach and reasoning are scored above the
  final solution.

---

## Worked Example: DVD Delivery Route (1999)

**Scenario.** Netflix delivers DVDs across a city. A driver starts at the
warehouse, visits a list of delivery addresses, and returns. Plan the
route.

**Strong-candidate moves.**

1. **Clarify before solving.**
   - How many addresses today?
   - How many drivers?
   - What does the business optimize — total time, total cost, deliveries
     completed in a window?
   - Does the route loop back to the warehouse?
2. **Recognize the shape.** This is the **Traveling Salesman Problem**
   (NP-hard). State that up front so the conversation about trade-offs is
   grounded.

### Part 1 — 5 addresses, 1 driver

- **Brute force (all permutations)**: `O(n!)`. Fine at `n=5` (120 perms),
  produces optimal route. Doesn't scale.
- **Nearest-Neighbor heuristic**: at each step, go to the nearest unvisited
  address. `O(n²)`. Often within ~25% of optimal; not guaranteed.

```java
// Nearest-neighbor route from a starting index.
public static List<Integer> nearestNeighborRoute(int[][] distance, int startIdx) {
    int n = distance.length;
    boolean[] visited = new boolean[n];
    List<Integer> route = new ArrayList<>();
    int current = startIdx;
    route.add(current);
    visited[current] = true;

    for (int count = 1; count < n; count++) {
        int next = -1, minDist = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (!visited[i] && distance[current][i] < minDist) {
                minDist = distance[current][i];
                next = i;
            }
        }
        if (next == -1) break;
        route.add(next);
        visited[next] = true;
        current = next;
    }
    route.add(startIdx);    // close the loop
    return route;
}
```

### Part 2 — 15 addresses, 3 drivers (Vehicle Routing Problem)

This is **VRP**, a generalization of TSP. The strong move is to admit that
this is harder than TSP and propose multiple greedy assignment strategies,
then weigh them. Each strategy splits into **assign first, then TSP per
vehicle**.

| Strategy | Idea | Pros | Cons |
|---|---|---|---|
| **Nearest Neighbor Assignment** | Each step, give the next stop to the vehicle currently closest to it. | Simple, locally efficient. | Uneven workload across drivers; may not minimize total distance. |
| **Round-Robin** | Distribute stops 1, 2, …, k, 1, 2, …, k by stop order. | Even count per driver, trivial to implement. | Ignores geography — a driver may get stops far apart. |
| **Cluster-First, Route-Second** | k-means or region-based clustering, then run TSP within each cluster. | Geographically compact routes. | Clusters can be uneven if demand is skewed. |
| **Sweep Around Warehouse** | Sort stops by polar angle around the warehouse, slice into k arcs. | Compact routes, simple geometry. | Needs angle math; not obvious without prior exposure. |

A candidate isn't expected to enumerate *all* of these — but strong
candidates propose **at least two** with explicit trade-offs.

### Part 3 — Shipping the system

The interviewer asks "how do you imagine shipping this?" Strong answers
cover:

- **Testing.** Unit tests for the routing logic. Integration tests against
  a realistic distance matrix. Load / stress tests for the daily peak.
- **Operations.** Monitoring dashboards (deliveries completed, route
  duration vs. estimate). Driver-side support tooling. Continuous
  feedback loop from drivers.
- **User experience.** Customer-facing tracking. Convenience features
  (expedited delivery, time windows).
- **Rollout.** Pilot in one region, gather feedback, iterate. Scale
  incrementally with automation, route-quality metrics, and alerting.
- **Scaling realities.** Different geographies, traffic patterns,
  delivery windows, and revenue optimization — surface these as the next
  set of problems even if you don't solve them today.

---

## Cheat Sheet for the Day-Of

- **First five minutes are clarifying questions.** No code yet.
- **State the shape of the problem.** "This is TSP." "This is a producer-consumer." "This looks like an LRU cache." Naming it grounds the discussion.
- **Two approaches > one optimal answer.** Always say what you'd reach for *and* why you might not.
- **Acknowledge what scales and what breaks.** Even greedy heuristics have a runway you can defend.
- **Close with delivery.** Tests, rollout, observability, success metrics. Even a sentence each shows you've shipped before.
