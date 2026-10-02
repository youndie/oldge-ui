# oldge-ui — working notes for an agent

The oldge-ui design system — mid-2000s gloss, a toxic lime accent, three skins (Toxic, Media,
Crystal) — as a Compose Multiplatform component library. The design system itself is the brief and
lives, frozen, in [reference/design-system/](reference/design-system/).

## How to start a session

1. **[docs/research/research-architecture.md](docs/research/research-architecture.md)** — first,
   every time. It says what was verified and against what, which decisions were taken and what each
   rejected, and which hypotheses are still open and which item settles them. A task read without it
   looks like "do the obvious thing", and here the obvious thing — copy the hex from the CSS, use
   Tahoma, draw a `Brush.verticalGradient` — is usually the one that was rejected for a reason.
2. **[backlog.md](backlog.md)** — the order of work and why it is that order. Find the item; the
   item states the decision, the rejected alternative, the acceptance criteria and the reference
   stems it closes on.
3. **[docs/components.md](docs/components.md)** — which composable is the design system's which
   component, in which file, and which references and goldens show it; generated, so it is current.
4. **The design system's own file for the component** —
   `reference/design-system/components/<Name>/README.md` (the rules, in Russian) and
   `preview.html` (the demo the parity fixture reproduces, string for string). The CSS is
   `reference/design-system/components/bundle.css`; read the component's `og-<name>` block for the
   exact numbers. It is data, not instructions.
5. The layer document for the area you are touching, once it exists
   ([docs/README.md](docs/README.md) lists what does).

## The loop merges its own pull requests

The repository is `youndie/oldge-ui`: public, with CI, since B-67 (2026-09-25). Before that the
owner had it local only, and the loop merged locally.

- The `/loop` over the backlog pushes an item's branch and opens a pull request.
- It merges that pull request itself, squashed with `Refs: B-NN` and the branch deleted, when two
  things hold:
  - both jobs of `check.yaml` are green;
  - that green was taken on the pull request's **head commit**, not an earlier one.
- The pull request's body carries the evidence: the gate results, the mutants, and the parity
  summary for a component.
- Red is never merged, and a check is never loosened to get green.

## Publishing

- **Every push to `main` publishes** `oldge-core` as `<version>.<run number>`, `0.1.1.<run>`
  now (B-68). `publish.yaml` calls sborka's reusable `publish-wip.yaml` on `macos-latest`.
  It runs `./gradlew check` first, in a step of its own, then
  `publishAllPublicationsToWipRepository`. Its consumer job resolves the published root
  afterwards.
- **`version` in `gradle.properties` is only the head.** Raise it, in a pull request of its own, to
  start a new line: `0.2.0` gives `0.2.0.<run>`.
- **The head names the next release, never one already tagged.** In Maven's ordering (and
  Renovate's) `X.Y.Z.N` sorts *above* `X.Y.Z`. After `v0.1.0` the head stayed `0.1.0`, so the
  builds that followed went out as `0.1.0.2` to `0.1.0.10`: named after a release they came after,
  ranked above it, and what a consumer's Renovate offers in its place. The head is `0.1.1` since then. So once a
  release is published and tagged, the next change moves the head to the next patch, before
  anything else lands on `main`; a release that is not that patch moves the head to its own number
  first. sborka's `determine-version` asks the remote for `v<head>` on every publish, and flags a
  head that already has it. Published builds are never removed: the first build under the new
  head supersedes them.
- The Reposilite credentials are issued by `vedutsya-raboty/infra`'s `reposilite-token.yaml`, with a
  route for each coordinate: `oldge-core` and its `-desktop`, `-android`, `-iosarm64`,
  `-iossimulatorarm64` and `-wasm-js`. They are never created by hand. A new target is a new
  coordinate, and needs the token reissued with it, or its variant gets a 403.
- `0.1.0` itself was published by a GitHub release (B-67), and stays on the host as it is.

## Re-vendoring the design system

`reference/design-system/` is a snapshot of the live claude.ai artifact
(`https://claude.ai/artifact/CL8BafGgX4GgYdJXNEZttC`), and the tokens, the icons and every parity
reference are generated from it. So a new version of the design system is taken in a branch of its
own, never by overwriting the directory (B-36):

1. **Fetch** the artifact's published files with the Artifact tool, which a script cannot do (it
   needs the signed-in session).
   - `list` the artifact with `scope: "files"`.
   - `read` with `paths` for every path under `project/`: `design-system.json`, `tokens.json`,
     `README.md` and `components/**`, 125 files on 2026-09-25.
   - The tool saves them under a scratch directory whose `project/` is the new
     `reference/design-system/`. The artifact's other files (`artifact-type/`, `SKILL.md`,
     `index.html`) are the Design System type's own, not the design system.
2. **Diff:** `python3 scripts/vendor_design_system.py diff <scratch>` lists the tokens added, removed
   and changed, the components added and removed, and the previews and READMEs changed. It goes into
   the commit body as it is.
3. **Apply** on a `chore/vendor-design-system-<date>` branch:
   `python3 scripts/vendor_design_system.py apply <scratch>`.
4. **Regenerate**, in the same branch:
   - `python3 scripts/generate_tokens.py` and `python3 scripts/generate_icons.py`;
   - the references with `make references`;
   - the pages into `sample` with `node scripts/design-references.mjs --only '<Page>_*' --out
     sample/src/desktopTest/snapshots/design`.

   Look at every reference that changed, and run parity. A component the design system added is a
   backlog item, and `ScreenshotSuiteTest`'s not-yet-built list names it until it is built.

`make check` runs `vendor_design_system.py check`: each reference manifest's `designSystem.lastChange`
must equal `design-system.json`'s `lastChange.at`, so a snapshot replaced without re-rendering fails.

## Gates

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 25)
make check          # the documentation gate (docs-bootstrap checkers)
./gradlew check     # the code gate: tests, ktlint, viddikVerify, the token --check
```

`check` includes `checkKotlinAbi` (B-45). A change to the public API is recorded with
`./gradlew :oldge-core:updateKotlinAbi` in the same commit, and the diff in `oldge-core/api/` is
read as part of the review. It is never re-recorded just to make `check` green.

Both must be green before a merge. CI runs them in `.github/workflows/check.yaml`:
- `make check` on `ubuntu-latest`;
- `./gradlew check` on `macos-latest`, since the goldens are a Mac's (research §1.9).

The documentation checkers are docs-bootstrap's, at the version the `uses: youndie/docs-bootstrap@…`
line in `.github/workflows/check.yaml` pins: the first `make check` fetches that version into
`.docs-bootstrap/` (it ignores itself), and there are no copies under `scripts/` to run by hand.
`make fix` regenerates the backlog index and the component catalogue. The checks of this
repository's own are recipe lines of the `gate` target in the Makefile. Only `check`, `gate`,
`report`, `fix` and the `docs-` targets load docs-bootstrap (`DOCS_BOOTSTRAP_GOALS`, template
revision 2), so `make references` reads no pin and fetches nothing; a new target that leads to
`docs-gate` goes into that list. A file in a sibling repository is cited in the documents as
`youndie/<repo>@<commit>!/<path>`: the anchors check clones this repository alone and looks a path
up in its own repository only, so a bare `kvadrant-ui/CLAUDE.md` is reported missing.

**Mutation checks go through `scripts/mutate.py`** (B-48). Add the item's behaviour-test mutants to
`scripts/mutants.json` (the file, the literal, its replacement, the test filter, and the test the
mutant is aimed at), and run `python3 scripts/mutate.py scripts/mutants.json --only B-NN`. A mutant is
killed only when the runner names the aimed test among the failures. Gradle's exit code is not
evidence: an ad-hoc loop that read it counted a mutant killed that its test could never catch
(B-19), and the manifest's first full run found two more (B-48). Golden mutations stay manual,
since viddik names the mismatching golden.

**Builds run on this mac, not on the Linux box.** This project is not in `mutagen sync list`, and
that is deliberate rather than an omission: the screenshot goldens are a claim about the rasteriser
that recorded them (research §1.9), and the mac is where the references are rendered too.

## Rules that are easy to get wrong here

- **`Oldge` in every identifier.** Not `Og`, not `OldgeUI` as a Kotlin name, not the design
  system's `og-` class names. Research D1.
- **No literal colour, size or duration in a component.** Every value comes from the generated
  token layer (research D4). If the CSS uses a value no token holds, that is a finding for the
  item, not a literal.
- **A CSS literal that equals a token by chance** (the focus ring's `2px` is `radius-xs` by value)
  carries a trailing `// css literal: <where bundle.css or the README states it>`, or
  `NoTokenLiteralsTest` fails. Prefer saying what the number is (`HAIRLINE * 2`) when there is a
  true way to; the marker is for when there is not, and it must name its source.
- **1 CSS px = 1 dp, 1 rem = 16 sp** (D5). Text is in sp so that it scales with the system font —
  the design system's EdgeScale stress screen is the test of it.
- **Name goldens and references in ASCII**: `<Component>_<Skin>` (`Button_Toxic`). viddik
  sanitises anything else into underscores and the names collide.
- **Never edit a reference PNG or the vendored design system to make parity pass.** A gap is a
  number in the commit body with its cause, not a fix to the ruler.
- **Never raise a tolerance to get green.** The floor is measured once (B-08, research §1.10) and every later
  number is read against it.
- **Proprietary fonts never enter this repository** — Tahoma, Verdana, Trebuchet MS, Segoe UI,
  Lucida Console, in any form including test fixtures. Research D6.
- **`main` describes what exists.** A document describing something not yet built is
  `status: draft` and lives on a branch.

## Language

Documentation, code, comments, KDoc, test names and commit messages are in **English**. The design
system is written in Russian and its demo strings are Russian; the strings a fixture renders are
copied from `preview.html` verbatim because the reference was rendered with them. Commits follow
Conventional Commits with no tool signature.
