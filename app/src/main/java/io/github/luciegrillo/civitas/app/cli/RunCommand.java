package io.github.luciegrillo.civitas.app.cli;

import io.github.luciegrillo.civitas.app.config.ExperimentLoader;
import io.github.luciegrillo.civitas.app.config.ExperimentSpec;
import io.github.luciegrillo.civitas.app.experiment.ExecutionReport;
import io.github.luciegrillo.civitas.app.experiment.ExperimentRunner;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Executes one experiment file.
 */
@Command(name = "run", description = "Run an experiment from strict JSON configuration.")
public final class RunCommand implements Callable<Integer> {
    @Spec
    private CommandSpec spec;

    @Parameters(index = "0", paramLabel = "EXPERIMENT", description = "Experiment JSON file.")
    private Path experimentPath;

    @Option(
            names = {"-o", "--output"},
            required = true,
            paramLabel = "DIRECTORY",
            description = "New output directory.")
    private Path outputDirectory;

    @Option(
            names = "--overwrite",
            description = "Replace an existing output directory.")
    private boolean overwrite;

    @Override
    public Integer call() {
        try {
            ExperimentSpec experiment = new ExperimentLoader().load(experimentPath);
            ExecutionReport report = new ExperimentRunner()
                    .run(experiment, outputDirectory, overwrite);
            spec.commandLine().getOut().printf(
                    "Completed %d runs in %d ms%nArtifacts: %s%n",
                    report.runCount(),
                    report.elapsedMillis(),
                    report.outputDirectory());
            return 0;
        } catch (IOException exception) {
            spec.commandLine().getErr().println("error: " + exception.getMessage());
            return 1;
        }
    }
}
