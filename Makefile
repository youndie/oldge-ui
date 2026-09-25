# The documentation gate. `./gradlew check` is the code gate (B-01 creates it); both are green before
# a branch is merged, and there is no CI, so the log of a local run goes into the squash commit.
#
# Whatever is not in `make check` is not a gate.

PY ?= python3

.PHONY: check gate report fix help references

help:
	@echo "make check   - the documentation gate plus the reports"
	@echo "make gate    - blocking: the backlog index, the documents, the coverage map"
	@echo "make report  - non-blocking: BDD coverage, code anchors"
	@echo "make fix     - regenerate the backlog index and the component catalogue, fill in missing coverage-map lines"
	@echo "make references - render the parity references from the design system (node, Chrome)"

check: gate report

gate:
	$(PY) scripts/backlog_index.py --check
	$(PY) scripts/docs_check.py
	$(PY) scripts/coverage_map.py --check
	$(PY) scripts/preview_deps.py --check
	$(PY) scripts/component_catalog.py --check
	node --test 'scripts/*.test.mjs'
	$(PY) -m unittest discover -s scripts -p 'test_*.py'

# Non-blocking, read by a person. Sibling repositories are searched because anchors point at
# kvadrant-ui and viddik; addresses inside a published artefact are written with `!/`.
report:
	$(PY) scripts/bdd_report.py
	$(PY) scripts/code_anchors.py --repos ..

fix:
	$(PY) scripts/backlog_index.py
	$(PY) scripts/coverage_map.py --fix
	$(PY) scripts/component_catalog.py

# B-03. Not in the gate: it needs Chrome and rewrites 177 PNGs. Render twice and compare before
# committing a change to the renderer; `git status` shows which references moved.
references:
	node scripts/design-references.mjs
