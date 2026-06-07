# ODD Model Description

This document describes the Civitas Lab v0.1 spatial Prisoner's Dilemma using
the Overview, Design concepts, and Details (ODD) protocol.

## Overview

### Purpose and Patterns

The model reproduces a canonical deterministic spatial evolutionary game
associated with Nowak and May. Its purpose is to verify the simulation engine
against documented local rules and study how cooperation-like spatial patterns
depend on temptation, self-interaction, boundaries, lattice size, and initial
conditions.

The target patterns are coexistence of cooperators and defectors, oscillating
strategy frequencies, invasion fronts, and reflection-symmetric
"kaleidoscopic" transients. These are model-level patterns, not empirical
targets for a real society.

### Entities, State Variables, and Scales

The only entities are sites on a rectangular two-dimensional lattice. Each site
contains one pure strategy:

- `C`: cooperate;
- `D`: defect.

Sites have no identity, memory, age, location change, or private information.
Location is fixed by the row-major lattice coordinate.

Time is discrete. Generation zero is the initialized lattice, and one tick
advances the entire population by one synchronous generation.

### Process Overview and Scheduling

Each generation has two complete phases:

1. Every site accumulates payoff from its current interaction partners.
2. Every site selects its next strategy from current-generation local
   candidates.

All next strategies are written to a separate buffer. The buffers are swapped
only after every decision is complete, so iteration order cannot affect the
model state.

## Design Concepts

### Basic Principles

The model implements unconditional imitation in a weak Prisoner's Dilemma on a
regular spatial lattice. Spatial locality allows strategies to form persistent
domains even though every site follows the same deterministic rule.

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
current strategies and resulting payoffs in its local neighborhood.

### Interaction

The interaction neighborhood is the range-one Moore neighborhood: up to eight
surrounding sites. With toroidal boundaries every site has eight neighbors.
With bounded edges, out-of-bounds neighbors are absent.

When self-interaction is enabled, a site additionally plays the game against
its own strategy. Self-interaction changes payoff accumulation only. The focal
site is always an imitation candidate, regardless of this setting.

### Stochasticity

The transition engine is deterministic. Stochasticity enters only through
Bernoulli initialization. Civitas Lab owns a stable SplitMix64 implementation
so a seed identifies the same initial lattice independently of JDK random
providers.

The central-defector initialization is fully deterministic and does not consume
random numbers.

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
quantiles, and rates of all-cooperate, all-defect, and mixed outcomes.

## Details

### Initialization

`BERNOULLI` independently assigns `C` with configured probability
`pCooperator`. A run seed is derived from the experiment master seed, seed
group, and replicate index. Temptation is intentionally absent from seed
derivation, enabling paired parameter comparisons.

`CENTRAL_DEFECTOR` requires odd lattice dimensions and places one `D` at the
unique center of an otherwise cooperative lattice.

### Input Data

The model uses no external empirical input data. All parameters are declared in
a strict, versioned JSON experiment document.

### Payoff Submodel

The weak Prisoner's Dilemma payoff matrix is:

| Focal / Opponent | C | D |
|---|---:|---:|
| C | 1 | 0 |
| D | `b` | 0 |

The temptation parameter is finite and restricted to `1 < b ≤ 2`. Payoffs are
raw accumulated totals; they are not normalized by the number of active
neighbors.

### Strategy-Update Submodel

For each focal site, the engine finds the largest cooperator payoff and largest
defector payoff among the focal site and all present Moore neighbors.

- If the cooperator maximum is larger, the next strategy is `C`.
- If the defector maximum is larger, the next strategy is `D`.
- If the maxima are exactly equal, the focal strategy is retained.

This deterministic cross-strategy tie policy is an explicit Civitas Lab
decision because accessible historical descriptions do not fully specify tie
resolution. It avoids directional bias and preserves symmetry.

### Boundary Submodel

`TOROIDAL` wraps both axes. `BOUNDED` omits out-of-bounds sites from interaction
and imitation. Bounded sites therefore play fewer games, and their payoffs
remain raw totals.

## References

The model target and methodological protocol are documented in
[References](references.md) and [`references.bib`](../references.bib).
