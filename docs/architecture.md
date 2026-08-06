# Architecture

## Modules

The Gradle build has two modules:

- `core`: dependency-free mathematical model, scheduling, and simulation
  engines;
- `app`: CLI, strict JSON, experiment orchestration, artifacts, reporting, and
  visualization.

The core has no knowledge of files, JSON, charts, threads, or experiment
batches. This keeps payoff rules, update rules, schedules, and transition
semantics independently testable and reusable.

Schema `0.1` instantiates the verified synchronous weak Prisoner's Dilemma.
Schema `0.2` additionally selects an explicit update schedule while preserving
the same binary state space, payoff model, and local strategy-update rule.

## State Representation

Strategies are stored in flat row-major `byte[]` arrays. A precomputed
neighborhood table removes boundary calculations from transition loops.

Both engines own:

- immutable simulation configuration;
- a binary payoff rule;
- a local strategy-update rule;
- precomputed Moore neighborhoods;
- current strategy state and immutable public snapshots.

The synchronous engine additionally owns a next-state buffer and one reusable
payoff buffer. The random-sequential engine updates its current state in place
and recomputes candidate payoffs at the instant each focal site is evaluated.
Internal steps allocate no per-site objects.

## Engine Contract

The application depends on the small `SimulationEngine` contract:

```text
step() -> StepMetrics
metrics() -> StepMetrics
snapshot() -> SimulationSnapshot
```

Two implementations currently exist:

- `SpatialGameEngine`: deterministic synchronous double buffering;
- `RandomSequentialSpatialGameEngine`: shuffled in-place sweeps without
  replacement.

This separation preserves the verified synchronous implementation instead of
adding schedule branches inside its transition loop.

## Synchronous Data Flow

```text
current strategies
       |
       v
accumulate all payoffs from generation t
       |
       v
select every next strategy from generation t
       |
       v
swap current and next buffers
```

Reading and writing different buffers prevents update-order artifacts.

## Random-Sequential Data Flow

```text
current strategies
       |
       v
shuffle all site indices using the schedule RNG
       |
       v
for each site: recompute local candidate payoffs
       |
       v
select and write the focal strategy immediately
```

Every site is visited exactly once per public tick. Later sites in a sweep can
observe changes made earlier in that same sweep.

## Determinism and Random Streams

Civitas Lab owns its SplitMix64 implementation and tests it against fixed
vectors. `ShuffledSiteScheduler` uses a stable Fisher-Yates shuffle with fixed
regression vectors and permutation checks.

Initialization and scheduling use separate seed domains:

- the initialization seed retains the schema `0.1` derivation;
- the schedule seed is derived in the explicit `schedule` domain.

Scenarios sharing a seed group and replicate index therefore start from the
same lattice even when their update schedules differ. The synchronous engine
consumes no schedule randomness. Both supported engines are bit-repeatable from
their declared inputs.

Separate best payoffs are tracked for `C` and `D`; unconditional imitation
retains the focal strategy on exact cross-strategy ties. The decision rule does
not depend on neighbor iteration order.

## Concurrency

One simulation engine executes on one thread. Experiment-level concurrency
uses a bounded platform-thread executor and shared-nothing tasks. Results are
consumed in configuration order, making scientific output independent of task
completion order.

Virtual threads are not useful for the CPU-bound engine, and distributed
execution remains outside the current compatibility surface.

## Artifact Pipeline

Each run writes only inside its unique directory through `RunArtifactWriter`.
After all runs complete, `ExperimentArtifactWriter` writes experiment-level
artifacts in deterministic order:

1. run summaries and aggregate CSV files;
2. experiment-level PNG figures;
3. schema `0.2` self-contained `report.html`.

The HTML report uses inline CSS, embeds generated PNG figures as Base64 data
URIs, includes the resolved configuration and selected aggregate statistics,
and contains no JavaScript or remote resources. Volatile provenance is excluded
from the report so the report remains part of the deterministic scientific
artifact set. Schema `0.1` does not generate the report, preserving its
published output contract.

`ChecksumManifest` runs after report generation and therefore includes
`report.html` together with all other deterministic scientific artifacts.
`provenance.json` remains intentionally excluded.

## Transactional Publication

An experiment is assembled in a unique staging directory beside the requested
output path. The final path is not created or replaced until every run,
aggregate, figure, report, and checksum manifest has completed successfully.
Publication then moves the staged directory into place.

When overwrite is enabled, the previous output is moved aside first and
restored if publication fails. Closing an unpublished workspace removes its
staging directory. A failed experiment therefore leaves neither a partial
result at the requested path nor a staging tree that appears complete.

## Dependencies

The application uses:

- Picocli for command parsing;
- Jackson for strict JSON binding;
- XChart and Java ImageIO for figures;
- JUnit for tests;
- Shadow for executable packaging.

The HTML report is generated directly by the application and adds no browser,
JavaScript, template-engine, or frontend-build dependency.

Versions are pinned through the Gradle version catalog and dependency lockfiles.

## Deferred Architecture

The project deliberately avoids a generic `Agent` hierarchy, universal event
scheduler, graph abstraction, plugin framework, database, GUI, JPMS modules,
and distributed execution. Current extension points remain narrow and tied to
explicit scientific questions: binary payoff rules, local binary update rules,
and documented update schedules for the existing lattice model.
