package io.github.luciegrillo.civitas.app;

import io.github.luciegrillo.civitas.app.cli.RunCommand;
import io.github.luciegrillo.civitas.app.cli.ValidateCommand;
import io.github.luciegrillo.civitas.app.cli.VersionCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Civitas Lab command-line entry point.
 */
@Command(
        name = "civitas",
        description = "Run reproducible agent-based social simulations.",
        mixinStandardHelpOptions = true,
        versionProvider = CivitasApplication.VersionProvider.class,
        subcommands = {RunCommand.class, ValidateCommand.class, VersionCommand.class})
public final class CivitasApplication implements Runnable {
    @Spec
    private CommandSpec spec;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        int exitCode = new CommandLine(new CivitasApplication()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }

    /**
     * Picocli bridge to packaged version metadata.
     */
    public static final class VersionProvider implements CommandLine.IVersionProvider {
        @Override
        public String[] getVersion() {
            return new String[] {"Civitas Lab " + VersionInfo.version()};
        }
    }
}
