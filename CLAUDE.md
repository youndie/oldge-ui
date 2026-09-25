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

## The loop merges its own branches, locally

Said by the owner on 2026-09-25: **this repository has no remote and nothing is pushed anywhere.**
The `/loop` over the backlog merges an item's branch into `main` itself when the item is done and
the gates are green on the branch's head commit — `git merge --squash`, one commit per item with
`Refs: B-NN`, the branch deleted afterwards. The "pull request" of the backlog-item workflow is
therefore the branch plus the gate log (and the parity summary for a component) in the squash
commit's body. Red is never merged; a check is never loosened to get green.

## Gates

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 25)
make check          # the documentation gate (docs-bootstrap checkers)
./gradlew check     # the code gate: tests, ktlint, viddikVerify, the token --check
```

Both must be green before a merge. There is no CI; the log of the local run is the evidence and it
goes into the squash commit's body.

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
