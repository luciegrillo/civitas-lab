# Scientific Scope

## Purpose

Civitas Lab is a software engineering and computational social science
laboratory for implementing transparent, reproducible agent-based models.

Version 0.1 has one purpose: reproduce a canonical synchronous spatial
Prisoner's Dilemma associated with Nowak and May and document how selected
implementation choices affect its behavior.

## Claims We Can Make

- The software implements its documented mathematical rules.
- A run can be reproduced from its configuration, seed, code revision, and
  runtime metadata.
- The baseline exhibits or does not exhibit patterns reported for the selected
  published model under the tested conditions.
- Results can be sensitive to boundaries, self-interaction, lattice size, and
  initial conditions.

## Claims We Do Not Make

- The model predicts cooperation in real societies.
- Spatial structure universally promotes cooperation.
- Binary lattice strategies represent the full complexity of human behavior.
- Civitas Lab replaces mature platforms such as NetLogo, MASON, Repast, or
  GAMA.
- Version 0.1 models trust, reputation, punishment, or institutions.

## Initial Model

Each lattice site holds one of two strategies: cooperate or defect. During a
generation, sites play the weak Prisoner's Dilemma against their Moore
neighborhood, optionally including themselves. Payoffs are accumulated, then
all sites synchronously adopt a strategy represented among the locally
highest-scoring candidates.

The baseline uses:

- reward `R = 1`;
- sucker's payoff `S = 0`;
- temptation `T = b`;
- punishment `P = 0`;
- synchronous updates;
- deterministic cross-strategy tie handling;
- raw accumulated payoff, without degree normalization.

Every deviation used in robustness experiments must be explicit in the
experiment configuration and resulting provenance.

## Validation Boundary

Version 0.1 performs model-to-paper validation: it checks whether the
implementation reproduces qualitative and analytically described behavior of
the published model. It does not perform model-to-world validation.
