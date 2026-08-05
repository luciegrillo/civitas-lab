package io.github.luciegrillo.civitas.app.experiment;

import io.github.luciegrillo.civitas.app.artifact.OutputWorkspace;
import io.github.luciegrillo.civitas.app.artifact.RunArtifactWriter;
import io.github.luciegrillo.civitas.app.config.InitializationType;
import io.github.luciegrillo.civitas.core.Lattice;
import io.github.luciegrillo.civitas.core.LatticeInitializers;
import io.github.luciegrillo.civitas.core.SimulationConfig;
import io.github.luciegrillo.civitas.core.SimulationEngine;
import io.github.luciegrillo.civitas.core.StepMetrics;
import io.github.luciegrillo.civitas.core.WeakPrisonersDilemma;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Executes one isolated simulation and writes only to its owned directory.
 */
public final class SimulationTask implements Callable<RunResult> {
    private final RunPlan plan;
    private final OutputWorkspace workspace;

    public SimulationTask(RunPlan plan, OutputWorkspace workspace) {
        this.plan = plan;
        this.workspace = workspace;
    }

    @Override
    public RunResult call() throws Exception {
        Lattice initial = initialize();
        SimulationConfig config = new SimulationConfig(
                plan.scenario().lattice().width(),
                plan.scenario().lattice().height(),
                plan.scenario().lattice().boundary(),
                new WeakPrisonersDilemma(plan.temptation()),
                plan.scenario().selfInteraction());
        SimulationEngine engine = SimulationEngineFactory.create(
                plan.scenario(), config, initial, plan.scheduleSeed());
        RunArtifactWriter artifacts = new RunArtifactWriter(workspace, plan);

        ArrayList<TimePoint> points = new ArrayList<>(plan.scenario().ticks() + 1);
        Set<Integer> snapshotTicks = new HashSet<>(plan.scenario().snapshotTicks());
        StepMetrics initialMetrics = engine.metrics();
        points.add(TimePoint.from(initialMetrics));
        if (snapshotTicks.contains(0)) {
            artifacts.writeSnapshot(0, initial, initial);
        }

        Lattice transitionPrevious = snapshotTicks.contains(1) ? initial : null;
        for (int tick = 1; tick <= plan.scenario().ticks(); tick++) {
            StepMetrics metrics = engine.step();
            points.add(TimePoint.from(metrics));

            boolean snapshotNow = snapshotTicks.contains(tick);
            boolean snapshotNext = snapshotTicks.contains(tick + 1);
            Lattice current = null;
            if (snapshotNow || snapshotNext) {
                current = engine.snapshot().lattice();
            }
            if (snapshotNow) {
                if (transitionPrevious == null) {
                    throw new IllegalStateException(
                            "previous lattice was not captured for snapshot tick " + tick);
                }
                artifacts.writeSnapshot(tick, transitionPrevious, current);
            }
            transitionPrevious = snapshotNext ? current : null;
        }

        List<TimePoint> immutablePoints = List.copyOf(points);
        artifacts.writeTimeSeries(immutablePoints);
        return new RunResult(plan, immutablePoints, summarize(immutablePoints));
    }

    private Lattice initialize() {
        int width = plan.scenario().lattice().width();
        int height = plan.scenario().lattice().height();
        if (plan.scenario().initialization().type() == InitializationType.CENTRAL_DEFECTOR) {
            return LatticeInitializers.centralDefector(width, height);
        }
        return LatticeInitializers.bernoulliCooperators(
                width,
                height,
                plan.scenario().initialization().pCooperator(),
                plan.initializationSeed());
    }

    private RunSummary summarize(List<TimePoint> points) {
        List<TimePoint> measurement = points.subList(
                plan.scenario().measurementStart(), points.size());
        double cooperationSum = 0.0;
        double flipSum = 0.0;
        for (TimePoint point : measurement) {
            cooperationSum += point.cooperatorFraction();
            flipSum += point.flipRate();
        }
        TimePoint finalPoint = points.getLast();
        PopulationState state;
        if (finalPoint.cooperators() == 0) {
            state = PopulationState.ALL_DEFECT;
        } else if (finalPoint.defectors() == 0) {
            state = PopulationState.ALL_COOPERATE;
        } else {
            state = PopulationState.MIXED;
        }
        return new RunSummary(
                plan.scenario().id(),
                plan.temptation(),
                plan.replicate(),
                plan.initializationSeed(),
                finalPoint.cooperatorFraction(),
                cooperationSum / measurement.size(),
                flipSum / measurement.size(),
                state);
    }
}
