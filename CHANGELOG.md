# Changelog

All notable changes to Civitas Lab will be documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and the project uses [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.0] - 2026-08-05

### Added

- A deterministic random-sequential spatial-game engine using shuffled sweeps
  without replacement and immediate in-place updates.
- A common simulation-engine contract that preserves the verified synchronous
  implementation as a separate engine.
- Stable bounded integers and Fisher-Yates schedule vectors based on the
  project-owned SplitMix64 generator.
- Versioned experiment schema `0.2` with explicit `SYNCHRONOUS` and
  `RANDOM_SEQUENTIAL` update schedules.
- Separate initialization and scheduling seed domains with paired initial
  conditions across schedules.
- Schedule-aware run metadata, summary CSV files, aggregate CSV files, and
  experiment grouping.
- A predeclared 400-run paired scheduling-robustness study with validated
  outputs, compact result tables, and documented claim boundaries.
- Self-contained schema `0.2` HTML reports with inline CSS, embedded figures,
  resolved configuration, aggregate summaries, and checksum coverage.
- Dedicated scheduling-study and release-candidate workflows.

### Changed

- Repositioned the README around the verified research-software baseline,
  visual results, scientific scope, and implemented-versus-planned features.
- Expanded the ODD description, experiment protocol, architecture, roadmap,
  scientific scope, and limitations for explicit update scheduling.
- Release result packages now include frequency, kaleidoscope, implementation
  robustness, and scheduling robustness outputs.

### Fixed

- Experiment outputs are now assembled in an isolated staging directory and
  published only after all deterministic artifacts and checksums succeed.
- Existing outputs remain intact during overwrite runs and are restored when
  publication fails.
- Successful publication is no longer reported as failed solely because an old
  backup directory could not be removed.

### Security

- Self-contained HTML reports contain no JavaScript or remote resources and
  HTML-escape configuration and identifier values.
- Transactional output publication retains existing symlink and unsafe-output
  protections.

## [0.1.1] - 2026-06-22

### Added

- Binary payoff and local update-rule extension points inside the spatial
  engine while preserving the v0.1 weak Prisoner's Dilemma experiment schema.
- Across-replicate standard deviations in aggregate experiment outputs.

### Security

- Artifact validation and output preparation now reject symlinked artifact,
  manifest, and output paths without following links.

## [0.1.0] - 2026-06-07

### Added

- Dependency-free deterministic engine for the synchronous spatial Prisoner's
  Dilemma.
- Flat lattice state, bounded and toroidal Moore neighborhoods, optional
  self-interaction, synchronous double buffering, and explicit tie semantics.
- Strict JSON experiment format with stable seed derivation and parallel
  execution of independent runs.
- `run`, `validate`, and `version` CLI commands in a self-contained Java 25
  executable JAR.
- CSV and JSON data, SHA-256 manifests, PNG snapshots, and aggregate charts.
- Frequency-profile and bounded kaleidoscope reproductions associated with the
  Nowak-May model.
- Predeclared 1,200-run robustness study covering self-interaction, boundaries,
  lattice size, initial cooperation, and temptation.
- Unit, integration, property, and scientific regression tests.
- ODD model description, experiment protocol, architecture, results,
  limitations, references, and citation metadata.
- CI and release automation for complete, validated experiment artifacts.

[Unreleased]: https://github.com/luciegrillo/civitas-lab/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/luciegrillo/civitas-lab/compare/v0.1.1...v0.2.0
[0.1.1]: https://github.com/luciegrillo/civitas-lab/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/luciegrillo/civitas-lab/releases/tag/v0.1.0
