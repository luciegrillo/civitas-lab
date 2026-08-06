# HTML Report Contract

Schema `0.2` experiments generate a top-level `report.html` after aggregate CSV
files and figures are complete.

## Purpose

The report provides one immediately viewable summary of an experiment without
requiring a web server, JavaScript runtime, notebook, or external asset host. It
is an additional presentation artifact; the CSV and JSON files remain the
full-precision machine-readable record.

## Contents

A report includes:

- experiment ID, schema version, run count, scenario count, and master seed;
- the complete scenario design, including update schedules and measurement
  windows;
- selected aggregate distribution statistics;
- every generated experiment-level PNG figure embedded as a Base64 data URI;
- the resolved JSON configuration;
- reproduction and checksum-validation commands;
- the Civitas Lab software version.

## Determinism

`report.html` is generated only from deterministic scientific artifacts and
stable configuration data. It intentionally excludes the timestamp, operating
system, Java vendor, and source revision stored in `provenance.json`.

The report is written before `checksums.sha256`, so its digest is included in
the manifest. Repeating an experiment with the same code, configuration, and
seeds on the same supported rendering environment must produce the same report
bytes regardless of experiment-level parallelism.

PNG rendering may still vary across font or graphics implementations. Because
figures are embedded, a platform-specific PNG difference also changes the
report digest. Numeric CSV artifacts remain the primary cross-platform
scientific comparison surface.

## Compatibility

Schema `0.1` does not generate `report.html`. This preserves the published v0.1
artifact set and checksum contract. The report is part of the schema `0.2`
output contract.

## Security and Portability

The report contains:

- inline CSS only;
- no JavaScript;
- no remote fonts, images, stylesheets, or analytics;
- HTML-escaped configuration and identifier values;
- embedded local PNG bytes only.

It can therefore be opened directly from disk and remains readable when copied
without the surrounding artifact directory. The sibling CSV, JSON, provenance,
and checksum files are still required for full reproducibility and validation.
