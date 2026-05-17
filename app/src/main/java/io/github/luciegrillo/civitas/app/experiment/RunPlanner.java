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
                    long seed = SeedDerivation.derive(
                            experiment.masterSeed(), scenario.seedGroup(), replicate);
                    plans.add(new RunPlan(
                            scenario,
                            temptation,
                            replicate,
                            seed,
                            RunPlan.createId(scenario.id(), temptation, replicate)));
                }
            }
        }
        return List.copyOf(plans);
    }
}
