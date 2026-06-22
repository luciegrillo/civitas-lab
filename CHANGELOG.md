# Changelog

All notable changes to Civitas Lab will be documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and the project uses [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Binary payoff and local update-rule extension points inside the spatial
  engine while preserving the v0.1 weak Prisoner's Dilemma experiment schema.

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

[Unreleased]: https://github.com/luciegrillo/civitas-lab/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/luciegrillo/civitas-lab/releases/tag/v0.1.0
