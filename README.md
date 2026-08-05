# Civitas Lab

[![CI](https://github.com/luciegrillo/civitas-lab/actions/workflows/ci.yml/badge.svg)](https://github.com/luciegrillo/civitas-lab/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)

Reproducible experimental software for spatial evolutionary games, built in
modern Java.

Civitas Lab turns a versioned experiment configuration into deterministic
simulation runs, aggregate statistics, figures, snapshots, provenance, and a
SHA-256 manifest that can be validated independently. The published v0.1
baseline reproduces documented synchronous spatial Prisoner's Dilemma regimes
and includes a predeclared 1,200-run robustness study.

## Published Baseline

Civitas Lab `v0.1.1` provides:

- a dependency-free deterministic lattice engine;
- synchronous double-buffered updates with explicit tie semantics;
- bounded and toroidal Moore neighborhoods with optional self-interaction;
- stable seed derivation and paired initial conditions;
- strict, versioned JSON experiment configurations;
- parallel execution with deterministic output ordering;
- CSV, JSON, PNG, aggregate charts, provenance, and SHA-256 manifests;
- historical frequency-profile and kaleidoscope scenarios;
- a 1,200-run robustness study over model implementation choices.

Download the executable and complete experiment outputs from the
[v0.1.1 release](https://github.com/luciegrillo/civitas-lab/releases/tag/v0.1.1).

## Why This Project Exists

Agent-based simulations can produce compelling patterns while hiding the
implementation choices that generated them. Civitas Lab makes those choices
explicit and testable: configurations, seeds, update semantics, runtime
metadata, and output checksums travel with each experiment.

The first release is deliberately narrow. It uses a published spatial game as
a validation target, exposes sensitivity to model choices, and establishes a
reproducible foundation for later scheduling and network experiments without
claiming to model real societies.

## Project Principles

- **Reproducible:** configurations, seeds, provenance, and checksums accompany
  experiment outputs.
- **Deterministic where specified:** synchronous model updates do not depend on
  iteration order or thread scheduling.
- **Scientifically modest:** claims are limited to the implemented model and
  its documented assumptions.
- **Engineering-focused:** the mathematical core is isolated from CLI, storage,
  and visualization concerns.
- **Incremental:** new mechanisms are added only after the existing baseline is
  specified and verified.

## Quick Start

Civitas Lab requires Java 25. The Gradle Wrapper downloads the pinned build
tool automatically.

```bash
./gradlew build
java -jar app/build/libs/civitas-lab-0.1.1.jar \
  run configs/smoke.json \
  --output artifacts/smoke
java -jar app/build/libs/civitas-lab-0.1.1.jar \
  validate artifacts/smoke
```

Each execution writes the resolved configuration, provenance, run-level time
series and snapshots, aggregate tables, charts, and a SHA-256 manifest.

See the [experiment format](docs/experiment-format.md) for the configuration
contract.

## Documentation

- [Scientific scope](docs/scientific-scope.md)
- [ODD model description](docs/model-odd.md)
- [Experiment protocol](docs/experiment-protocol.md)
- [Architecture](docs/architecture.md)
- [Baseline results](docs/results.md)
- [Robustness study](docs/robustness.md)
- [Limitations and claim boundaries](docs/limitations.md)
- [References](docs/references.md)
- [Roadmap](docs/roadmap.md)

## License

Licensed under the [Apache License 2.0](LICENSE).
