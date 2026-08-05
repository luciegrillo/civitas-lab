# Experiment Format

Civitas Lab experiments are strict JSON documents. Unknown properties,
duplicate keys, missing required values, and invalid scientific parameters are
rejected before any output directory is created.

Machine-readable schemas are available for both supported contracts:

- [`experiment-0.1.schema.json`](../schemas/experiment-0.1.schema.json)
- [`experiment-0.2.schema.json`](../schemas/experiment-0.2.schema.json)

## Schema Compatibility

Schema `0.1` remains the published synchronous baseline. It does not contain an
`updateSchedule` property and continues to select the verified double-buffered
engine without consuming scheduling randomness.

Schema `0.2` requires every scenario to declare an update schedule explicitly:

```json
{
  "updateSchedule": {
    "type": "RANDOM_SEQUENTIAL"
  }
}
```

Supported values are:

- `SYNCHRONOUS`: compute all next strategies from generation `t`, then swap
  buffers after the complete generation;
- `RANDOM_SEQUENTIAL`: shuffle all sites, visit each site exactly once, and
  write each selected strategy immediately.

The detailed time, payoff, and sweep semantics are specified in
[Update Schedules](update-schedules.md).

## Top-Level Properties

- `schemaVersion`: configuration contract version, `0.1` or `0.2`.
- `experimentId`: lowercase identifier used in provenance.
- `masterSeed`: signed 64-bit seed from which run seeds are derived.
- `parallelism`: maximum number of independent runs executed concurrently.
- `scenarios`: one or more scenario definitions.

## Scenario Properties

- `id`: unique lowercase scenario identifier.
- `seedGroup`: controls paired random streams across scenarios.
- `lattice`: width, height, and `TOROIDAL` or `BOUNDED` edges.
- `initialization`: `CENTRAL_DEFECTOR`, or `BERNOULLI` with `pCooperator`.
- `selfInteraction`: whether a site plays against itself when accumulating
  payoff. The focal site always remains an imitation candidate.
- `updateSchedule`: required by schema `0.2`; selects `SYNCHRONOUS` or
  `RANDOM_SEQUENTIAL` updates.
- `temptationValues`: unique weak Prisoner's Dilemma values in `(1, 2]`.
- `replicates`: independent initial conditions per temptation value.
- `ticks`: final simulated generation. Generation zero is the initial state.
- `measurementStart`: first generation included in run-level means.
- `snapshotTicks`: generations rendered as transition-colored PNG files.

## Paired Random Streams

For each seed group and replicate index, schema `0.2` derives two independent
streams:

- the initialization seed creates generation zero and retains the exact schema
  `0.1` derivation;
- the schedule seed uses a separate `schedule` domain and controls shuffled
  site orders.

Changing only the update schedule cannot change generation zero. Scenarios with
the same master seed, seed group, and replicate index receive the same pair of
seeds, which enables direct synchronous-versus-asynchronous comparisons.

Run metadata retains the legacy `seed` field for the initialization seed and
adds `updateSchedule` and `scheduleSeed` for schema `0.2` runs.

## Minimal Scheduling Comparison

```json
{
  "schemaVersion": "0.2",
  "experimentId": "scheduling-smoke",
  "masterSeed": 19920359,
  "parallelism": 2,
  "scenarios": [
    {
      "id": "synchronous",
      "seedGroup": "paired-schedule",
      "lattice": {
        "width": 25,
        "height": 25,
        "boundary": "TOROIDAL"
      },
      "initialization": {
        "type": "BERNOULLI",
        "pCooperator": 0.9
      },
      "selfInteraction": true,
      "updateSchedule": {
        "type": "SYNCHRONOUS"
      },
      "temptationValues": [1.85],
      "replicates": 2,
      "ticks": 25,
      "measurementStart": 10,
      "snapshotTicks": [0, 25]
    },
    {
      "id": "random-sequential",
      "seedGroup": "paired-schedule",
      "lattice": {
        "width": 25,
        "height": 25,
        "boundary": "TOROIDAL"
      },
      "initialization": {
        "type": "BERNOULLI",
        "pCooperator": 0.9
      },
      "selfInteraction": true,
      "updateSchedule": {
        "type": "RANDOM_SEQUENTIAL"
      },
      "temptationValues": [1.85],
      "replicates": 2,
      "ticks": 25,
      "measurementStart": 10,
      "snapshotTicks": [0, 25]
    }
  ]
}
```

The same document is available at
[`configs/scheduling-smoke.json`](../configs/scheduling-smoke.json).

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

`aggregate.csv` reports means, across-replicate standard deviations, quantiles,
and final-state rates for each scenario and temptation value.

`provenance.json` contains volatile runtime metadata and is intentionally
excluded from `checksums.sha256`. All scientific artifacts are hashed.
