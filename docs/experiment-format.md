# Experiment Format

Civitas Lab experiments are strict JSON documents. Unknown properties,
duplicate keys, missing required values, and invalid scientific parameters are
rejected before any output directory is created.

The machine-readable schema is available at
[`schemas/experiment-0.1.schema.json`](../schemas/experiment-0.1.schema.json).

## Top-Level Properties

- `schemaVersion`: configuration contract version; currently `0.1`.
- `experimentId`: lowercase identifier used in provenance.
- `masterSeed`: signed 64-bit seed from which run seeds are derived.
- `parallelism`: maximum number of independent runs executed concurrently.
- `scenarios`: one or more scenario definitions.

## Scenario Properties

- `id`: unique lowercase scenario identifier.
- `seedGroup`: controls paired initial conditions. Runs with the same master
  seed, seed group, and replicate index receive the same derived seed.
- `lattice`: width, height, and `TOROIDAL` or `BOUNDED` edges.
- `initialization`: `CENTRAL_DEFECTOR`, or `BERNOULLI` with `pCooperator`.
- `selfInteraction`: whether a site plays against itself when accumulating
  payoff. The focal site always remains an imitation candidate.
- `temptationValues`: unique weak Prisoner's Dilemma values in `(1, 2]`.
- `replicates`: independent initial conditions per temptation value.
- `ticks`: final simulated generation. Generation zero is the initial state.
- `measurementStart`: first generation included in run-level means.
- `snapshotTicks`: generations rendered as transition-colored PNG files.

## Output Contract

```text
output/
├── resolved-experiment.json
├── provenance.json
├── summary.csv
├── aggregate.csv
├── checksums.sha256
├── figures/
└── runs/
    └── <scenario>__b-<temptation>__r-<replicate>/
        ├── metadata.json
        ├── timeseries.csv
        └── snapshots/
```

`provenance.json` contains volatile runtime metadata and is intentionally
excluded from `checksums.sha256`. All scientific artifacts are hashed.

## Paired Runs

Run seeds depend on the master seed, `seedGroup`, and replicate index, but not
on the temptation value. This makes parameter comparisons use the same initial
lattice without sharing mutable simulation state.
