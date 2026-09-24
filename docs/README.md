# docs — oldge-ui

The oldge-ui design system as a Compose Multiplatform component library. The documentation is
layered; links run top to bottom, and today only two layers exist.

```
[ Research (why the architecture is what it is) ]
                     │
[ Service (the library module: what it owns, how it is built) ]      — from B-01
```

| Layer | Directory | Answers | Source of truth |
|---|---|---|---|
| Research | `research/` | *why* it is built this way; what is verified, what is a hypothesis | the artefacts each fact names |
| Service | `services/` | what the module owns, its targets, how it is built and tested, its quirks | this repository |

**Deliberately absent:** `features/`, `screens/`, `api/`. A component library has no API layer, and
what a component is and how it looks is already specified — in the design system this repository
vendors in [`reference/design-system/`](../reference/design-system/), one README and one preview per
component. Copying that into feature and screen documents would produce 52 documents that repeat a
brief nobody here maintains. The component catalogue (B-42) is generated instead.

**Backlog** — [backlog.md](../backlog.md): the index and the order of work; the items themselves are
one file each in [`backlog/`](backlog/), cited as `[B-12](backlog/B-12-button-and-orb-button.md)`.

## Conventions

- **`id`** in the frontmatter is unique and equals the filename.
- Cross-layer links are ids in the frontmatter and ordinary markdown links in the body.
- **The primary consumer is a coding agent.** Every document carries code anchors, so that the
  reader reaches the code in one hop. Do not duplicate what lives in code or in the design system;
  give the path.
- Language: **English**. The design system is in Russian and is quoted, not translated, where a
  string matters (the fixtures render its strings verbatim).

## Templates

`templates/` holds a copy of the document templates, so the format travels with the repository.

## Checks

```bash
pip install pyyaml
make check
```

## Coverage map

The list below is **checked** against the files on disk: a document missing here, or an entry with
no file behind it, fails `coverage_map.py`. The grouping and the descriptions are written by a
person — the machine only guards the membership.

### Research (1)

- [x] [research-architecture](research/research-architecture.md) — what the design system is, what can and cannot be shipped as specified (fonts), how references are rendered, the toolchain, decisions D1–D10, risks

### Services (1/1)

- [x] [oldge-core](services/oldge-core.md) — the library module: targets, the two catalogs, the suite, the viddik #44 workaround
