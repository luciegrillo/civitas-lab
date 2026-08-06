# ODD Model Description

This document describes the Civitas Lab spatial Prisoner's Dilemma using the
Overview, Design concepts, and Details (ODD) protocol. It covers the verified
v0.1 synchronous baseline and the v0.2 random-sequential scheduling extension.

## Overview

### Purpose and Patterns

The model reproduces a canonical spatial evolutionary game associated with
Nowak and May. Its purpose is to verify the simulation engine against
documented local rules and study how cooperation-like spatial patterns depend
on temptation, self-interaction, boundaries, lattice size, initial conditions,
and update scheduling.

The target patterns are coexistence of cooperators and defectors, oscillating
strategy frequencies, invasion fronts, reflection-symmetric "kaleidoscopic"
transients, and sensitivity to scheduling. These are model-level patterns, not
empirical targets for a real society.

### Entities, State Variables, and Scales

The only entities are sites on a rectangular two-dimensional lattice. Each site
contains one pure strategy:

- `C`: cooperate;
- `D`: defect.

Sites have no identity, memory, age, location change, or private information.
Location is fixed by the row-major lattice coordinate.

Time is discrete. Generation zero is the initialized lattice. One public tick
represents one complete population-scale update: either one synchronous
generation or one random-sequential shuffled sweep containing exactly one visit
to every site.

### Process Overview and Scheduling

The schedule is part of the model specification.

Under `SYNCHRONOUS` updating:

1. Every site accumulates payoff from generation `t`.
2. Every site selects its next strategy from candidates evaluated in that same
   state.
3. Selected strategies are written to a separate buffer.
4. Buffers are swapped only after every decision is complete.

Iteration order cannot affect a synchronous transition.

Under `RANDOM_SEQUENTIAL` updating:

1. All row-major site indices are shuffled without replacement.
2. Sites are visited once in that order.
3. Immediately before each decision, focal and candidate payoffs are computed
   from the current in-place lattice.
4. The selected strategy is written immediately.

Later sites in a sweep can therefore observe changes made earlier in the same
tick.

## Design Concepts

### Basic Principles

The model implements unconditional imitation in a weak Prisoner's Dilemma on a
regular spatial lattice. Spatial locality allows strategies to form persistent
domains even though every site follows the same rule. Scheduling determines
whether decisions share one frozen generation state or observe earlier changes
within a shuffled sweep.

### Emergence

Global cooperator frequency, oscillation, absorbing states, invasion fronts,
and spatial domains emerge from local payoff and imitation rules. No global
cooperation target is encoded.

### Adaptation and Objectives

A site adapts by adopting the strategy associated with the greatest payoff in
its local candidate set. The site does not optimize a long-term objective,
forecast future generations, or reason about other sites.

### Learning, Prediction, and Sensing

There is no learning, memory, or prediction. A site effectively senses only the
strategies and resulting payoffs in its local neighborhood at the instant its
update rule is evaluated.

### Interaction

The interaction neighborhood is the range-one Moore neighborhood: up to eight
surrounding sites. With toroidal boundaries every site has eight neighbors.
With bounded edges, out-of-bounds neighbors are absent.

When self-interaction is enabled, a site additionally plays the game against
its own strategy. Self-interaction changes payoff accumulation only. The focal
site is always an imitation candidate, regardless of this setting.

### Stochasticity

Bernoulli initialization uses Civitas Lab's owned SplitMix64 implementation so
an initialization seed identifies the same lattice independently of JDK random
providers. Central-defector initialization is deterministic.

The synchronous transition engine consumes no randomness. Random-sequential
updating uses a separately derived schedule seed and a stable Fisher-Yates
shuffle. The same configuration and seeds produce bit-identical trajectories.
Changing only the schedule seed cannot change generation zero.

### Collectives

The model has no explicit groups, institutions, or collective entities. Spatial
clusters are emergent patterns, not represented objects.

### Observation

The engine observes:

- cooperator and defector counts;
- cooperator fraction;
- number and fraction of strategy changes per tick;
- lattice snapshots at requested ticks.

The experiment layer derives run-level means, final-state distributions,
quantiles, and rates of all-cooperate, all-defect, and mixed outcomes. Schema
`0.2` outputs additionally identify the update schedule and record both random
streams.

## Details

### Initialization

`BERNOULLI` independently assigns `C` with configured probability
`pCooperator`. An initialization seed is derived from the experiment master
seed, seed group, and replicate index. Temptation and schedule are intentionally
absent from this derivation, enabling paired comparisons.

Schema `0.2` separately derives a schedule seed in the `schedule` domain. Paired
synchronous and random-sequential scenarios with the same seed group and
replicate index receive the same initial lattice and schedule-seed identity,
although the synchronous engine does not consume the latter.

`CENTRAL_DEFECTOR` requires odd lattice dimensions and places one `D` at the
unique center of an otherwise cooperative lattice.

### Input Data

The model uses no external empirical input data. All parameters are declared in
a strict, versioned JSON experiment document. Schema `0.1` retains implicit
synchronous semantics; schema `0.2` requires an explicit update schedule.

### Payoff Submodel

The weak Prisoner's Dilemma payoff matrix is:

| Focal / Opponent | C | D |
|---|---:|---:|
| C | 1 | 0 |
| D | `b` | 0 |

The temptation parameter is finite and restricted to `1 < b ≤ 2`. Payoffs are
raw accumulated totals; they are not normalized by the number of active
neighbors.

For synchronous updates, every site's payoff is computed once from generation
`t`. For random-sequential updates, a candidate's payoff is recomputed when it
is considered for the current focal decision, using the current in-place
lattice.

### Strategy-Update Submodel

For each focal site, the engine finds the largest cooperator payoff and largest
defector payoff among the focal site and all present Moore neighbors.

- If the cooperator maximum is larger, the next strategy is `C`.
- If the defector maximum is larger, the next strategy is `D`.
- If the maxima are exactly equal, the focal strategy is retained.

This deterministic cross-strategy tie policy is an explicit Civitas Lab
decision because accessible historical descriptions do not fully specify tie
resolution. It avoids directional bias and preserves symmetry under the
synchronous baseline.

### Boundary Submodel

`TOROIDAL` wraps both axes. `BOUNDED` omits out-of-bounds sites from interaction
and imitation. Bounded sites therefore play fewer games, and their payoffs
remain raw totals.

## References

The model target and methodological protocol are documented in
[References](references.md) and [`references.bib`](../references.bib).
Scheduling semantics, the predeclared study, and its results are documented in
[Update Schedules](update-schedules.md),
[Scheduling Robustness Protocol](scheduling-study-protocol.md), and
[Scheduling Robustness Results](scheduling-results.md).
