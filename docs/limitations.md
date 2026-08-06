# Limitations and Claim Boundaries

## Scientific Scope

Civitas Lab is an implementation and reproducibility study of one abstract
spatial evolutionary game. It does not predict or explain cooperation in human,
animal, economic, or institutional systems.

Terms such as "cooperator" and "defector" identify mathematical strategies.
They are not psychological traits or moral categories.

## Model Limitations

- Strategies are binary, pure, and memoryless.
- Sites do not move, communicate, learn, reproduce, or observe globally.
- The interaction network is a static square lattice.
- Payoffs are limited to the weak Prisoner's Dilemma.
- Scheduling is limited to synchronous generations and one random-sequential
  shuffled-sweep formulation without replacement.
- Payoffs are raw totals, so bounded edge sites play fewer games.
- Population size, topology, and behavioral rules do not evolve.
- There is no mutation, implementation error, or observation noise.

The model contains no trust, reputation, punishment, institutions,
polarization, or information diffusion despite those topics appearing in the
long-term project description.

## Scheduling Limitations

The scheduling study compares only two mechanisms. It does not include random
asynchronous updating with replacement, continuous-time event scheduling,
partial activation, heterogeneous clocks, or empirically calibrated timing.

One public random-sequential tick is a complete shuffled sweep, while one
synchronous tick is a complete double-buffered generation. Equal tick indices
therefore align population-scale update opportunities, not identical causal
histories.

The observed reduction in cooperation under random-sequential updating is
conditional on the tested `15 x 15` torus, initial density, payoff rule,
self-interaction setting, temptation grid, and 100-tick horizon. It must not be
presented as a universal scheduling effect.

## Historical-Fidelity Limitations

The accessible Nowak-May descriptions do not expose every implementation
detail, exact random initial state, or tie-resolution behavior. Civitas Lab
documents its own deterministic tie policy and seed instead of claiming an
exact reconstruction of unpublished source code.

The frequency profile is based on one declared random lattice, as a historical
illustration. Aggregate scientific interpretation should use the robustness
suites rather than that single trajectory.

## Statistical Limitations

The v0.1 robustness grid uses 50 replicates per condition. The scheduling study
uses 20 paired replicates per temptation value. These sample sizes may be
inadequate near sharp transitions, rare absorbing outcomes, or strongly
multimodal regimes.

The reported summaries are descriptive. Across-replicate standard deviations
describe observed dispersion, but they are not confidence intervals, stopping
rules, or formal uncertainty claims. No correction for multiple comparisons,
formal hypothesis test, or model calibration is performed. Multimodal outcomes
require inspecting quantiles and state frequencies, not only means.

The parameter grids sample selected values rather than continuously exploring
the model space. Equal aggregate rows at two sampled temptation values do not
prove that all intermediate values behave identically.

## Validation Limitations

Passing tests demonstrates conformance to the documented implementation.
Qualitative agreement with a published model does not validate the model
against empirical observations.

The synchronous engine has analytical, symmetry, and regression checks. The
random-sequential engine has deterministic schedule vectors, absorbing-state
checks, and repeatability tests, but no single historical trajectory is treated
as a universal numerical target for asynchronous behavior.

## Software Limitations

- Experiment schemas `0.1` and `0.2` are supported, but there is no general
  compatibility promise before `1.0` beyond the explicitly tested v0.1
  scientific artifact contract.
- Large sweeps retain run time series until aggregation, increasing memory use.
- PNG appearance can vary with fonts and graphics implementations even when
  numeric outputs are identical.
- Checksums verify artifact integrity; they do not provide authenticity or
  long-term archival guarantees.
- Performance observations are hardware-specific and are not CI requirements.
