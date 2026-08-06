# Scheduling Robustness Results

## Predeclared Design

The comparison follows the fixed protocol in
[Scheduling Robustness Protocol](scheduling-study-protocol.md). It contains 400
runs: ten temptation values, 20 paired initial conditions, and two update
schedules on the same `15 x 15` toroidal lattice.

The paired delta reported below is
`random-sequential - synchronous`. Negative values therefore indicate less
cooperation under random-sequential updating for the same initial lattice.

## Main Result

Within this bounded grid, final cooperation was schedule-sensitive.
Random-sequential updating produced a lower median final cooperator fraction at
all ten tested temptation values.

![Median final cooperation by update schedule](results/v0.2/scheduling-scenario-comparison.svg)

| `b` | Synchronous median | Random-sequential median | Paired median delta | Synchronous all-defect | Random-sequential all-defect |
|---:|---:|---:|---:|---:|---:|
| 1.05 | 0.978 | 0.960 | -0.018 | 0.00 | 0.00 |
| 1.15 | 0.942 | 0.896 | -0.042 | 0.00 | 0.00 |
| 1.25 | 0.922 | 0.884 | -0.047 | 0.00 | 0.00 |
| 1.35 | 0.878 | 0.787 | -0.098 | 0.00 | 0.00 |
| 1.45 | 0.853 | 0.780 | -0.087 | 0.00 | 0.00 |
| 1.55 | 0.771 | 0.647 | -0.144 | 0.00 | 0.00 |
| 1.65 | 0.720 | 0.680 | -0.027 | 0.00 | 0.00 |
| 1.75 | 0.700 | 0.447 | -0.244 | 0.00 | 0.00 |
| 1.85 | 0.280 | 0.000 | -0.280 | 0.15 | 1.00 |
| 1.95 | 0.280 | 0.000 | -0.280 | 0.15 | 1.00 |

The largest observed differences occur at high temptation. At `b=1.85` and
`b=1.95`, every random-sequential replicate reached all-defect by tick 100,
whereas 85% of synchronous runs remained mixed and the synchronous median was
`0.28`.

The effect is not monotonic. At `b=1.65`, the paired median delta is only
`-0.027`, and the upper quartile of paired deltas is positive (`0.021`). This
overlap is one reason the study reports distributions and paired summaries
rather than presenting one global effect size.

## Late-Window Dynamics

The late-window mean cooperator fraction follows the same broad pattern as the
final state. Median paired differences range from `-0.018` at `b=1.05` to
approximately `-0.318` at `b=1.85` and `b=1.95`.

Random-sequential trajectories also generally show fewer late strategy changes
than synchronous trajectories in this design. This does not imply that
asynchronous models are inherently less dynamic; it describes the attractors
reached by these rules and parameters.

## Reproduction

```bash
./gradlew build --no-daemon

jar_path="$(find app/build/libs -maxdepth 1 -type f \
  -name 'civitas-lab-*.jar' ! -name '*-plain.jar' | sort | head -1)"

java -jar "$jar_path" run \
  configs/scheduling-robustness.json \
  --output artifacts/scheduling-robustness

java -jar "$jar_path" validate artifacts/scheduling-robustness
```

The predeclared GitHub Actions execution completed all 400 runs and validated
the generated SHA-256 manifest. It ran with Java `25.0.3` on Linux from the PR
merge revision recorded in the output provenance.

Compact committed outputs:

- [aggregate distributions](results/v0.2/scheduling-aggregate.csv)
- [paired delta summaries](results/v0.2/scheduling-paired-deltas.csv)

The complete run-level time series, metadata, figures, provenance, and manifest
are produced by the `Scheduling Study` workflow artifact.

## Claim Boundary

These results show that the implemented spatial game is sensitive to the two
specified schedules under the declared parameter grid. They do not establish
that random-sequential updating always reduces cooperation, that either timing
mechanism is universally appropriate, or that the model predicts real social
systems. Other lattice sizes, densities, update rules, and asynchronous
formulations require separate experiments.
