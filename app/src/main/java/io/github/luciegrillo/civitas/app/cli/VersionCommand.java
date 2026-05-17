package io.github.luciegrillo.civitas.app.cli;

import io.github.luciegrillo.civitas.app.VersionInfo;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Prints the packaged application version.
 */
@Command(name = "version", description = "Print Civitas Lab version.")
public final class VersionCommand implements Callable<Integer> {
    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        spec.commandLine().getOut().println("Civitas Lab " + VersionInfo.version());
        return 0;
    }
}
