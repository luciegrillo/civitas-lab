# Architecture

## Modules

The Gradle build has two modules:

- `core`: dependency-free mathematical model and engine;
- `app`: CLI, strict JSON, orchestration, artifacts, and visualization.

The core has no knowledge of files, JSON, charts, threads, or experiment
batches. This keeps the payoff rules, update rules, and transition engine
independently testable and reusable.

The v0.1 experiment schema still instantiates the documented weak Prisoner's
Dilemma. Internally, the engine accepts any finite two-strategy payoff matrix
and a local update rule with the same C/D state space.

## State Representation

Strategies are stored in flat row-major `byte[]` arrays. A precomputed
neighborhood table removes boundary calculations from the generation loop.

The engine owns:

- current and next strategy buffers;
- one reusable payoff buffer;
- immutable configuration;
- a binary payoff rule;
- a local strategy-update rule;
- precomputed Moore neighborhoods.

Public snapshots copy the strategy array. Internal generation steps allocate no
per-site objects.

## Synchronous Data Flow

```text
current strategies
       |
       v
accumulate payoffs from the configured binary game
       |
       v
select every next strategy with the configured update rule
       |
       v
swap current and next buffers
```

Reading and writing different buffers prevents update-order artifacts. The
published v0.1 model uses weak Prisoner's Dilemma payoffs and deterministic
unconditional imitation with focal-strategy retention on exact cross-strategy
ties.

## Determinism

The transition engine contains no random operations. Bernoulli initialization
uses the project-owned SplitMix64 implementation, tested against fixed vectors.
Run seed derivation is stable and explicit.

Separate best payoffs are tracked for `C` and `D`; the v0.1 update rule retains
the focal strategy on exact cross-strategy ties. The rule avoids dependence on
neighbor iteration order.

## Concurrency

One engine executes on one thread. Experiment-level concurrency uses a bounded
platform-thread executor and shared-nothing tasks. Results are consumed in
configuration order, making output independent of completion scheduling.

Virtual threads are not useful for the CPU-bound engine, and Structured
Concurrency remains outside the v0.1 compatibility surface.

## Artifact Pipeline

Each run writes only inside its unique directory. After all runs complete, the
coordinator writes aggregate CSV files and charts in deterministic group order.

Volatile provenance is separated from hashed scientific output. The
`validate` command recomputes every listed SHA-256 digest and rejects missing,
modified, or escaping paths.

## Dependencies

The application uses:

- Picocli for command parsing;
- Jackson for strict JSON binding;
- XChart and Java ImageIO for figures;
- JUnit for tests;
- Shadow for executable packaging.

Versions are pinned through the Gradle version catalog and dependency lockfiles.

## Deferred Architecture

v0.1 deliberately avoids a generic `Agent` hierarchy, event scheduler, graph
abstraction, plugin system, database, GUI, JPMS modules, and distributed
execution. The current extension point is intentionally narrower: binary payoff
rules and local binary update rules for the existing lattice engine.
