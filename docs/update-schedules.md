# Update Schedules

Civitas Lab treats update scheduling as part of the model specification rather
than as an implementation detail.

## Synchronous Baseline

The published v0.1 engine uses synchronous updates:

1. compute every site's payoff from the complete lattice at generation `t`;
2. select every next strategy from that same state;
3. write selected strategies to a separate buffer;
4. swap buffers only after all sites have been evaluated.

Iteration order and experiment-level thread scheduling cannot affect a
synchronous trajectory.

## Random-Sequential Schedule

The first asynchronous mechanism is random sequential updating without
replacement.

One asynchronous tick contains exactly `N` site updates, where `N` is the
number of lattice sites:

1. create a uniformly shuffled permutation of all row-major site indices;
2. visit every site exactly once in that order;
3. immediately before updating a focal site, compute the focal and candidate
   payoffs from the current in-place lattice;
4. apply the configured local strategy-update rule;
5. write the selected strategy immediately before visiting the next site.

Later sites in the same tick therefore observe changes made earlier in the
shuffled sweep. This mechanism is distinct from random asynchronous updating
with replacement and from continuous-time event scheduling.

## Time and Metrics

Generation zero remains the initial lattice. Metrics and snapshots are exposed
only after a complete shuffled sweep, so synchronous and random-sequential runs
share the same public time index.

`strategyChanges` counts sites whose strategy changed during the completed
sweep. Because each site is visited once per sweep, the count cannot exceed the
population size.

## Randomness Contract

Initialization randomness and scheduling randomness are separate model inputs.
A random-sequential engine receives a dedicated schedule seed. Its shuffled
orders are produced by Civitas Lab's owned SplitMix64 implementation and a
stable Fisher-Yates shuffle.

The same configuration and schedule seed must produce bit-identical sweeps and
trajectories. Changing only the schedule seed must not change generation zero.
The synchronous engine consumes no scheduling randomness.

## Claim Boundary

Comparing these schedules can reveal sensitivity within the implemented
spatial game and declared parameter grid. It does not establish that either
schedule is universally correct for social systems, nor does it represent all
possible asynchronous formulations.
