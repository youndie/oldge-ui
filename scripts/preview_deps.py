#!/usr/bin/env python3
"""Hold each component item's `blocked_by` to the components its previews actually use (B-20).

A component item's acceptance is its design-system preview, ported string for string. A preview
often shows another component inside it (Panel's shows a Meter, ListItem's a Badge), and that one
belongs to another item. If that item is not a blocker, the loop picks this one first and cannot
finish it. B-17 had to split SearchBar off, and B-20 found two more.

For every open item with a **References** line, this reads `window.OldgeUI` components off each
preview (`B.<Name>`), maps them to the items whose references name them, and reports any that are
not done and not in `blocked_by`.

    python3 scripts/preview_deps.py            # report
    python3 scripts/preview_deps.py --check    # exit 1 when anything is missing (make gate)
"""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parent.parent
BACKLOG = ROOT / "docs" / "backlog"
COMPONENTS = ROOT / "reference" / "design-system" / "components"
REFERENCES = re.compile(r"\*\*References\*\*: (.*)")
STEM = re.compile(r"`([A-Za-z]+)_\{")
USE = re.compile(r"\bB\.([A-Z][A-Za-z]+)")


def items() -> dict[str, tuple[dict, list[str]]]:
    out = {}
    for path in sorted(BACKLOG.glob("B-*.md")):
        text = path.read_text(encoding="utf-8")
        front = yaml.safe_load(text.split("---")[1])
        refs = REFERENCES.search(text)
        out[front["id"]] = (front, STEM.findall(refs.group(1)) if refs else [])
    return out


def missing() -> dict[str, list[tuple[str, str]]]:
    backlog = items()
    owner = {}
    for iid, (_, comps) in backlog.items():
        for c in comps:
            owner.setdefault(c, iid)
    report = {}
    for iid, (front, comps) in backlog.items():
        if front["status"] in ("done", "dropped") or not comps:
            continue
        blocked = set(front.get("blocked_by") or [])
        gaps = set()
        for c in comps:
            preview = COMPONENTS / c / "preview.html"
            if not preview.exists():
                continue
            for used in USE.findall(preview.read_text(encoding="utf-8")):
                other = owner.get(used)
                if other and other != iid and other not in blocked and backlog[other][0]["status"] != "done":
                    gaps.add((used, other))
        if gaps:
            report[iid] = sorted(gaps, key=lambda g: (int(g[1][2:]), g[0]))
    return report


def cycles() -> list[list[str]]:
    """Cycles in `blocked_by` among items that are not done: a loop that would wait for ever."""
    backlog = items()
    edges = {
        iid: [b for b in (front.get("blocked_by") or []) if b in backlog and backlog[b][0]["status"] != "done"]
        for iid, (front, _) in backlog.items()
        if front["status"] != "done"
    }
    found, state = [], {}

    def visit(node: str, path: list[str]) -> None:
        state[node] = "open"
        for nxt in edges.get(node, []):
            if state.get(nxt) == "open":
                found.append(path[path.index(nxt) :] + [nxt] if nxt in path else [node, nxt])
            elif nxt not in state:
                visit(nxt, path + [nxt])
        state[node] = "closed"

    for node in edges:
        if node not in state:
            visit(node, [node])
    return found


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--check", action="store_true")
    a = ap.parse_args(argv)
    report = missing()
    for iid, gaps in sorted(report.items(), key=lambda r: int(r[0][2:])):
        print(f"{iid}: its previews use " + ", ".join(f"{c} ({other})" for c, other in gaps) + ", not in blocked_by")
    loops = cycles()
    for loop in loops:
        print("blocked_by cycle: " + " -> ".join(loop))
    if not report and not loops:
        print("every open component item is blocked by the items its previews use, and nothing waits on itself")
    return 1 if (report or loops) and a.check else 0


if __name__ == "__main__":
    sys.exit(main())
