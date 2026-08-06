# Scheduling Robustness Protocol

## Research Question

How sensitive is final and late-window cooperation in the implemented spatial
weak Prisoner's Dilemma to replacing the verified synchronous update rule with
random-sequential sweeps without replacement?

This protocol is fixed before inspecting the generated results.

## Compared Mechanisms

The study compares exactly two schedules:

- `SYNCHRONOUS`: all payoffs and next strategies are evaluated from generation
  `t`, then the complete next-state buffer is published;
- `RANDOM_SEQUENTIAL`: every tick shuffles all sites, visits each site once,
  recomputes local candidate payoffs from the current lattice, and writes the
  selected strategy immediately.

No other asynchronous formulation is included.

## Fixed Conditions

Both schedules use:

- a `15 x 15` toroidal Moore lattice;
- self-interaction enabled;
- unconditional imitation;
- Bernoulli initialization with `pCooperator = 0.9`;
- temptation values `1.05` through `1.95` in increments of `0.10`;
- 20 paired replicates per temptation value;
- 100 ticks per run;
- a measurement window from tick 50 through tick 100;
- master seed `19920359`.

The complete design contains 400 runs. Synchronous and random-sequential runs
with the same replicate index share the same initialization seed and receive a
separately derived, recorded schedule seed.

## Outcomes

The primary descriptive outcomes are:

- median final cooperator fraction;
- final interquartile and 5--95 percent ranges;
- all-cooperate, all-defect, and mixed final-state rates;
- mean cooperator fraction over the late measurement window;
- mean strategy-change rate over the same window.

Results are reported as distributions. A mean alone is not used to characterize
multimodal outcomes.

## Interpretation

The study can establish whether the two implemented schedules produce similar
or different behavior under this bounded grid. It cannot establish that either
schedule is a universally correct representation of social time, that the
findings generalize to other lattice sizes or initial densities, or that they
predict real societies.

The configuration is stored in
[`configs/scheduling-robustness.json`](../configs/scheduling-robustness.json).
