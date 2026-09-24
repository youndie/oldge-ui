# The documentation gate. `./gradlew check` is the code gate (B-01 creates it); both are green before
# a branch is merged, and there is no CI, so the log of a local run goes into the squash commit.
#
# Whatever is not in `make check` is not a gate.

PY ?= python3

.PHONY: check gate report fix help

help:
	@echo "make check   - the documentation gate plus the reports"
	@echo "make gate    - blocking: the backlog index, the documents, the coverage map"
	@echo "make report  - non-blocking: BDD coverage, code anchors"
	@echo "make fix     - regenerate the backlog index, fill in missing coverage-map lines"

check: gate report

gate:
	$(PY) scripts/backlog_index.py --check
	$(PY) scripts/docs_check.py
	$(PY) scripts/coverage_map.py --check

# Non-blocking, read by a person. Sibling repositories are searched because anchors point at
# kvadrant-ui and viddik; addresses inside a published artefact are written with `!/`.
report:
	$(PY) scripts/bdd_report.py
	$(PY) scripts/code_anchors.py --repos ..

fix:
	$(PY) scripts/backlog_index.py
	$(PY) scripts/coverage_map.py --fix
