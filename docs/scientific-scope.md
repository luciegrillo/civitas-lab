# Scientific Scope

## Purpose

Civitas Lab is a software engineering and computational social science
laboratory for implementing transparent, reproducible agent-based models.

Version 0.1 reproduces a canonical synchronous spatial Prisoner's Dilemma
associated with Nowak and May and documents how selected implementation choices
affect its behavior. Version 0.2 adds one precisely specified
random-sequential schedule and measures sensitivity to update timing without
changing the verified synchronous baseline.

## Claims We Can Make

- The software implements its documented mathematical rules.
- A run can be reproduced from its configuration, seeds, code revision, and
  runtime metadata.
- The baseline exhibits or does not exhibit patterns reported for the selected
  published model under the tested conditions.
- Results can be sensitive to boundaries, self-interaction, lattice size,
  initial conditions, and update scheduling.
- Under the predeclared scheduling grid, the implemented random-sequential
  schedule produces lower cooperation than the synchronous schedule for most
  paired runs, with the largest differences at high temptation.

## Claims We Do Not Make

- The model predicts cooperation in real societies.
- Spatial structure universally promotes cooperation.
- Random-sequential updating always reduces cooperation.
- Either implemented schedule is universally correct for social systems.
- Binary lattice strategies represent the full complexity of human behavior.
- Civitas Lab replaces mature platforms such as NetLogo, MASON, Repast, or
  GAMA.
- The current model contains trust, reputation, punishment, or institutions.

## Spatial Game

Each lattice site holds one of two strategies: cooperate or defect. Sites play
the weak Prisoner's Dilemma against their Moore neighborhood, optionally
including themselves. Payoffs are raw accumulated totals, and strategy changes
use deterministic unconditional imitation with an explicit cross-strategy tie
policy.

The verified baseline uses synchronous updates: all decisions are computed from
generation `t` and published together. Schema `0.2` additionally supports
random-sequential sweeps without replacement: each site is visited exactly once
in a shuffled order, candidate payoffs are recomputed from the current lattice,
and the selected strategy is written immediately.

The model uses:

- reward `R = 1`;
- sucker's payoff `S = 0`;
- temptation `T = b`;
- punishment `P = 0`;
- synchronous or declared random-sequential updates;
- deterministic cross-strategy tie handling;
- raw accumulated payoff, without degree normalization.

Every variation used in an experiment must be explicit in the versioned
configuration and resulting provenance.

## Validation Boundary

Version 0.1 performs model-to-paper validation: it checks whether the
implementation reproduces qualitative and analytically described behavior of
the published synchronous model. Version 0.2 performs model-to-model
sensitivity analysis between two declared update mechanisms. Neither is
model-to-world validation.
