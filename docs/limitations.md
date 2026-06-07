# Limitations and Claim Boundaries

## Scientific Scope

Civitas Lab v0.1 is an implementation and reproducibility study of one abstract
spatial evolutionary game. It does not predict or explain cooperation in human,
animal, economic, or institutional systems.

Terms such as "cooperator" and "defector" identify mathematical strategies.
They are not psychological traits or moral categories.

## Model Limitations

- Strategies are binary, pure, and memoryless.
- Sites do not move, communicate, learn, reproduce, or observe globally.
- The interaction network is a static square lattice.
- Payoffs are limited to the weak Prisoner's Dilemma.
- All sites update synchronously under a global model clock.
- Payoffs are raw totals, so bounded edge sites play fewer games.
- Population size, topology, and behavioral rules do not evolve.
- There is no mutation, implementation error, or observation noise.

The model contains no trust, reputation, punishment, institutions,
polarization, or information diffusion despite those topics appearing in the
long-term project description.

## Historical-Fidelity Limitations

The accessible Nowak-May descriptions do not expose every implementation
detail, exact random initial state, or tie-resolution behavior. Civitas Lab
documents its own deterministic tie policy and seed instead of claiming an
exact reconstruction of unpublished source code.

The frequency profile is based on one declared random lattice, as a historical
illustration. Aggregate scientific interpretation should use the robustness
suite rather than that single trajectory.

## Statistical Limitations

The robustness grid samples selected values rather than continuously exploring
the parameter space. Fifty replicates may still be inadequate near sharp phase
transitions or rare absorbing outcomes.

The reported summaries are descriptive. No correction for multiple
comparisons, formal hypothesis test, or model calibration is performed.
Multimodal outcomes require inspecting quantiles and state frequencies, not
only means.

## Validation Limitations

Passing tests demonstrates conformance to the documented implementation.
Qualitative agreement with a published model does not validate the model
against empirical observations.

Synchronous scheduling is known to affect outcomes. Asynchronous scheduling is
explicitly deferred to v0.2 and conclusions from v0.1 must remain conditional
on synchrony.

## Software Limitations

- The experiment schema is version `0.1` and has no compatibility promise
  before `1.0`.
- Large sweeps retain run time series until aggregation, increasing memory use.
- PNG appearance can vary with fonts and graphics implementations even when
  numeric outputs are identical.
- Checksums verify artifact integrity; they do not provide authenticity or
  long-term archival guarantees.
- Performance observations are hardware-specific and are not CI requirements.
