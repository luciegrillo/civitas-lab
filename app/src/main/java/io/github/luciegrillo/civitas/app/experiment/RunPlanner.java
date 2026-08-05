package io.github.luciegrillo.civitas.app.experiment;

import io.github.luciegrillo.civitas.app.config.ExperimentSpec;
import io.github.luciegrillo.civitas.app.config.ScenarioSpec;
import io.github.luciegrillo.civitas.core.SeedDerivation;
import java.util.ArrayList;
import java.util.List;

/**
 * Expands a compact experiment specification into deterministic run plans.
 */
public final class RunPlanner {
    private static final String SCHEDULE_SEED_DOMAIN = "schedule";

    private RunPlanner() {
    }

    /**
     * Expands scenarios in document order, then temptation and replicate order.
     */
    public static List<RunPlan> expand(ExperimentSpec experiment) {
        ArrayList<RunPlan> plans = new ArrayList<>(experiment.runCount());
        for (ScenarioSpec scenario : experiment.scenarios()) {
            for (double temptation : scenario.temptationValues()) {
                for (int replicate = 0; replicate < scenario.replicates(); replicate++) {
                    long initializationSeed = SeedDerivation.derive(
                            experiment.masterSeed(), scenario.seedGroup(), replicate);
                    long scheduleSeed = SeedDerivation.derive(
                            experiment.masterSeed(),
                            SCHEDULE_SEED_DOMAIN,
                            scenario.seedGroup(),
                            replicate);
                    plans.add(new RunPlan(
                            scenario,
                            temptation,
                            replicate,
                            initializationSeed,
                            scheduleSeed,
                            RunPlan.createId(scenario.id(), temptation, replicate)));
                }
            }
        }
        return List.copyOf(plans);
    }
}
