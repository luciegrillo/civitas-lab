# Civitas Lab

Agent-based social simulation laboratory built in modern Java for reproducible
computational experiments on emergence, cooperation, polarization, trust, and
institutional dynamics.

## About

Civitas Lab is an experimental computational social science platform. It
explores how population-level patterns can emerge from simple local
interactions between rule-based agents.

The first release focuses narrowly on reproducing the synchronous spatial
Prisoner's Dilemma studied by Nowak and May. That baseline is intentionally
small: it gives the project a published target, makes implementation choices
visible, and provides a foundation for later models without claiming to explain
real societies.

## Project Principles

- **Reproducible:** configurations, seeds, provenance, and checksums accompany
  experiment outputs.
- **Deterministic where specified:** synchronous model updates do not depend on
  iteration order or thread scheduling.
- **Scientifically modest:** claims are limited to the implemented model and
  its documented assumptions.
- **Engineering-focused:** the mathematical core is isolated from CLI, storage,
  and visualization concerns.
- **Incremental:** reputation, institutions, and complex networks arrive only
  after the spatial baseline is verified.

## Status

Civitas Lab is under active development toward `v0.1.0`.

See the [scientific scope](docs/scientific-scope.md) and
[roadmap](docs/roadmap.md) for the planned first release.

## License

Licensed under the [Apache License 2.0](LICENSE).
