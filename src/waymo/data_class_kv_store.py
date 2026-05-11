"""Python DataClass: Basic Key-Value Store with get/set.

Problem (Waymo)
---------------
Implement a "data class" that behaves like a tiny in-memory database:

    obj = DataClass()
    obj.set('a', '1')
    obj.get('a')   # -> '1'
    obj.get('b')   # -> None

Both keys and values are strings.  Provide at least three test cases.

The spec also shows a stdin command format used by the autograder:

    set c 3
    get c

so this file ALSO contains a tiny REPL driver that reads `set <k> <v>` and
`get <k>` commands from stdin and prints the result of every `get`.

Design notes
------------
* `@dataclass` is the natural Python idiom for "a class that mostly holds
  data".  `field(default_factory=dict)` gives every instance its own dict
  (a mutable default like `= {}` would be shared across instances and is
  a classic Python footgun).
* We keep the methods minimal and Pythonic: `dict.get(key)` already
  returns `None` for missing keys, which matches the spec exactly.
* Type hints make the "strings only" contract explicit.  We don't enforce
  it at runtime — the spec says "assume" — but the hints document intent
  and let mypy/IDE checks catch misuse.
* Bonus operations (`delete`, `__contains__`, `__len__`, `__getitem__`,
  `__setitem__`, `__iter__`) are included because they're tiny and turn
  the data class into a drop-in dict-like that's easy to write
  follow-up tests against (e.g. "now add delete", "now add iteration").

Complexity
----------
* `set`, `get`, `delete`, `__contains__`, `__len__`: amortised O(1)
  (Python dict).
* `__iter__`: O(n).
"""

from __future__ import annotations

from dataclasses import dataclass, field
from typing import Dict, Iterator, Optional
import sys


@dataclass
class DataClass:
    """Tiny in-memory key/value store.  Keys and values are strings."""

    # Per-instance dict; do NOT use `= {}` (shared mutable default).
    _store: Dict[str, str] = field(default_factory=dict, repr=False)

    # --- Required API --------------------------------------------------

    def set(self, key: str, value: str) -> None:
        """Store/overwrite ``key`` -> ``value``."""
        self._store[key] = value

    def get(self, key: str) -> Optional[str]:
        """Return the value for ``key``, or ``None`` if absent."""
        return self._store.get(key)

    # --- Bonus dict-like helpers (handy for follow-ups) ----------------

    def delete(self, key: str) -> bool:
        """Remove ``key``.  Returns True if it existed, False otherwise."""
        return self._store.pop(key, None) is not None

    def __contains__(self, key: object) -> bool:
        return key in self._store

    def __len__(self) -> int:
        return len(self._store)

    def __getitem__(self, key: str) -> str:
        # Raises KeyError on miss — different from get(), matching dict semantics.
        return self._store[key]

    def __setitem__(self, key: str, value: str) -> None:
        self.set(key, value)

    def __iter__(self) -> Iterator[str]:
        return iter(self._store)


# ---------------------------------------------------------------------------
# Tests
# ---------------------------------------------------------------------------

def _run_tests() -> None:
    """At least three test cases per the spec — actually a dozen."""

    # 1. Spec example.
    db = DataClass()
    db.set("a", "1")
    assert db.get("a") == "1", "spec set/get"
    assert db.get("b") is None, "missing key returns None"

    # 2. Overwriting an existing key.
    db.set("a", "2")
    assert db.get("a") == "2", "overwrite"

    # 3. Independent values.
    db.set("name", "Waymo")
    db.set("city", "Mountain View")
    assert db.get("name") == "Waymo"
    assert db.get("city") == "Mountain View"

    # 4. Empty string key and value (both are valid strings).
    db.set("", "empty-key")
    assert db.get("") == "empty-key"
    db.set("blank-val", "")
    assert db.get("blank-val") == ""

    # 5. Each DataClass instance is independent (regression for shared
    #    mutable default).
    a, b = DataClass(), DataClass()
    a.set("k", "from-a")
    assert b.get("k") is None, "instances must not share state"

    # 6. delete() returns the right boolean and removes the entry.
    assert db.delete("name") is True
    assert db.get("name") is None
    assert db.delete("nope") is False

    # 7. dict-like sugar.
    db["sugar"] = "ok"
    assert "sugar" in db
    assert db["sugar"] == "ok"
    try:
        _ = db["does-not-exist"]
    except KeyError:
        pass
    else:
        raise AssertionError("__getitem__ should raise KeyError on miss")

    # 8. __len__ and iteration.
    fresh = DataClass()
    for i in range(5):
        fresh.set(f"k{i}", str(i))
    assert len(fresh) == 5
    assert sorted(fresh) == [f"k{i}" for i in range(5)]

    # 9. Many writes followed by lookup — sanity.
    big = DataClass()
    for i in range(10_000):
        big.set(str(i), str(i * 2))
    assert big.get("9999") == "19998"
    assert big.get("10000") is None

    print(f"All tests passed  ({len(fresh)} entries in iter test, "
          f"{len(big)} in stress test)")


# ---------------------------------------------------------------------------
# stdin REPL driver — matches the spec's "Example Input" format:
#   set c 3
#   get c
# Prints the value (or "None") for every `get`.
# Supports values that contain spaces by treating everything after the key as
# the value: `set greeting hello world` -> stores "hello world".
# ---------------------------------------------------------------------------

def _run_stdin() -> None:
    db = DataClass()
    for raw in sys.stdin:
        line = raw.strip()
        if not line:
            continue
        parts = line.split(None, 2)
        cmd = parts[0].lower()
        if cmd == "set" and len(parts) >= 3:
            db.set(parts[1], parts[2])
        elif cmd == "get" and len(parts) == 2:
            val = db.get(parts[1])
            print("None" if val is None else val)
        elif cmd == "del" and len(parts) == 2:
            db.delete(parts[1])
        else:
            print(f"# ignored: {line!r}", file=sys.stderr)


if __name__ == "__main__":
    # Always run tests as a smoke check.  Pass `--stdin` (or anything else)
    # to additionally read `set k v` / `get k` commands from stdin afterwards.
    # We use an explicit flag rather than `sys.stdin.isatty()` because
    # autograders and CI shells often have no controlling TTY, which would
    # make an `isatty()`-based check block forever on empty stdin.
    _run_tests()
    if len(sys.argv) > 1 and sys.argv[1] == "--stdin":
        _run_stdin()
