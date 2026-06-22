# Experiment Protocol

## Reproducibility Contract

Every public experiment is defined by version-controlled JSON. A completed
execution records:

- the resolved experiment definition;
- master and derived run seeds;
- model parameters for every run;
- runtime and source-revision provenance;
- run-level time series and requested snapshots;
- aggregate tables and figures;
- SHA-256 checksums for deterministic scientific artifacts.

Unknown JSON properties, duplicate keys, missing required fields, and invalid
parameter ranges fail before simulation starts.

## Seed Management

Run seeds are derived from:

```text
master seed + seed group + replicate index
```

Temptation and scenario ID are excluded deliberately. Scenarios that share a
seed group therefore use paired random streams for the same replicate. Each run
still owns its lattice, engine, and output directory; no mutable simulation
state is shared.

Clock-time seeding is never used.

## Execution

Each simulation engine is single-threaded. Independent runs may execute on a
bounded platform-thread pool. Output aggregation follows configuration order,
not task-completion order, so changing parallelism does not change scientific
artifacts.

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
- horizon: 200 generations;
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

Fifty replicates are a practical predeclared budget for this portfolio study,
not a universal Monte Carlo stopping rule.

## Verification

Software verification covers:

- all four payoff outcomes;
- bounded and toroidal neighborhood indexing;
- self-interaction semantics;
- synchronous double buffering;
- deterministic tie handling;
- SplitMix64 reference vectors;
- immutable public lattice state;
- seed pairing;
- configuration validation;
- CLI exit behavior;
- checksums and PNG transition colors;
- invariance to experiment parallelism.

The `b=9/8` isolated-defector breakpoint is checked analytically. The
kaleidoscope is checked for reflection symmetry and against an internal raw
lattice hash.

## Validation

Validation in v0.1 is model-to-paper, not model-to-world. The project compares
implemented behavior with published qualitative regimes and known sensitivity
to scheduling and self-interaction.

The internal generation-179 hash protects Civitas Lab from accidental code
changes. It is not a claim that the exact unpublished historical lattice has
been recovered.

## Reporting

For each scenario and temptation, Civitas Lab reports final means, medians,
5th/25th/75th/95th percentiles, measurement-window means, and final population
state rates. Aggregate tables also include across-replicate standard deviations
for final cooperation, measurement-window cooperation, and measurement-window
flip rates. Means are never treated as sufficient when distributions are
multimodal or contain absorbing states.
