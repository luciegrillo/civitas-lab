# Experiment Protocol

## Reproducibility Contract

Every public experiment is defined by version-controlled JSON. A completed
execution records:

- the resolved experiment definition;
- master, initialization, and scheduling seeds where applicable;
- model parameters for every run;
- runtime and source-revision provenance;
- run-level time series and requested snapshots;
- aggregate tables and figures;
- SHA-256 checksums for deterministic scientific artifacts.

Unknown JSON properties, duplicate keys, missing required fields, and invalid
parameter ranges fail before simulation starts.

## Seed Management

Schema `0.1` initialization seeds retain the published derivation:

```text
master seed + seed group + replicate index
```

Schema `0.2` keeps that stream unchanged and derives scheduling randomness in a
separate named domain:

```text
master seed + "schedule" domain + seed group + replicate index
```

Temptation, scenario ID, and update schedule are excluded from initialization
seed derivation. Scenarios that share a seed group therefore start from the
same lattice for the same replicate. Changing only the schedule cannot change
generation zero.

Clock-time seeding is never used.

## Execution

Each simulation engine is single-threaded. Independent runs may execute on a
bounded platform-thread pool. Output aggregation follows configuration order,
not task-completion order, so changing parallelism does not change scientific
artifacts.

Artifacts are first written to an isolated sibling staging directory. The
completed directory is published at the requested path only after all runs,
aggregates, figures, and checksums succeed.

An existing output directory is rejected unless `--overwrite` is explicit.
Filesystem roots and the current working directory cannot be selected as an
output target.

## v0.1 Experiments

### Frequency Profile

- lattice: `20×20`, toroidal;
- initialization: Bernoulli `p(C)=0.9`;
- self-interaction: enabled;
- temptation: `1.13, 1.15, 1.17, 1.21, 1.26, 1.30, 1.35, 1.45,
  1.55, 1.65, 1.79, 1.85`;
- horizon: 200 synchronous generations;
- one paired initial lattice across temptation values.

This scenario illustrates the historical frequency profile. One seed is not an
uncertainty estimate.

### Kaleidoscope

- lattice: `49×49`, bounded;
- initialization: one central defector;
- self-interaction: enabled;
- temptation: `1.85`;
- horizon: generation 179.

The acceptance target is deterministic reflection symmetry and the documented
formation of changing spatial domains.

### Bounded Robustness Grid

The baseline is a `50×50` torus with self-interaction and `p(C)=0.9`.
One factor at a time changes:

- self-interaction: disabled;
- boundary: bounded;
- lattice size: `20×20` or `100×100`;
- initial cooperation: `0.3`, `0.5`, or `0.7`.

Each scenario runs at `b ∈ {1.15, 1.55, 1.85}` with 50 paired replicates and a
200-generation horizon. Generations 151–200 form the measurement window. The
suite contains 1,200 runs.

Fifty replicates are a practical predeclared budget, not a universal Monte
Carlo stopping rule.

## v0.2 Scheduling Experiment

The scheduling study compares `SYNCHRONOUS` and `RANDOM_SEQUENTIAL` while
holding all other declared model parameters fixed.

- lattice: `15×15`, toroidal;
- initialization: Bernoulli `p(C)=0.9`;
- self-interaction: enabled;
- temptation: `1.05` through `1.95` in increments of `0.10`;
- 20 paired replicates per temptation;
- horizon: 100 ticks;
- measurement window: ticks 50–100;
- total: 400 runs.

One synchronous tick is a double-buffered generation. One random-sequential
tick is a shuffled sweep without replacement in which each site is visited
exactly once and writes immediately. See
[Scheduling Robustness Protocol](scheduling-study-protocol.md).

## Verification

Software verification covers:

- all four payoff outcomes;
- bounded and toroidal neighborhood indexing;
- self-interaction semantics;
- synchronous double buffering;
- deterministic tie handling;
- SplitMix64 reference vectors and bounded integers;
- fixed shuffled-schedule vectors and permutation coverage;
- random-sequential absorbing states and bit-identical repeatability;
- immutable public lattice state;
- separated seed domains and paired initial conditions;
- schema `0.1` artifact compatibility and schema `0.2` validation;
- CLI exit behavior;
- checksums and PNG transition colors;
- invariance to experiment parallelism.

The `b=9/8` isolated-defector breakpoint is checked analytically. The
kaleidoscope is checked for reflection symmetry and against an internal raw
lattice hash.

## Validation

The synchronous baseline performs model-to-paper validation, not model-to-world
validation. The scheduling study is a model-to-model sensitivity analysis of
two explicit mechanisms. It does not validate either timing mechanism against
empirical observations.

The internal generation-179 hash protects Civitas Lab from accidental code
changes. It is not a claim that the exact unpublished historical lattice has
been recovered.

## Reporting

For each scenario, schedule, and temptation, schema `0.2` reports final means,
medians, 5th/25th/75th/95th percentiles, measurement-window means, and final
population-state rates. Aggregate tables include across-replicate standard
deviations. Run summaries identify the update schedule, initialization seed,
and schedule seed.

Schema `0.1` retains its original schedule-free CSV contract. Means are never
treated as sufficient when distributions are multimodal or contain absorbing
states.
