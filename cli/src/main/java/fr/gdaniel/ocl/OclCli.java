package fr.gdaniel.ocl;

import fr.gdaniel.ocl.runner.OCLRunner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.ocl.pivot.utilities.PivotUtil;

import java.io.File;

/**
 * Provides a command-line interface to evaluate OCL constraints against models.
 *
 * @see #main(String[]) for details on the arguments and exit codes
 */
public class OclCli {

    /**
     * The name of the executable.
     * <p>
     * This constant is used to help messages that refer to the executable itself (e.g. help message).
     */
    private static final String CMD_LINE_SYNTAX = "ocl-cli";

    /**
     * The option defining the metamodel to load with the CLI.
     * <p>
     * This is a required option. The argument of the option is a path to an existing file containing a metamodel.
     */
    public static final Option METAMODEL_OPTION = new Option("M", "metamodel", true, "Location of the metamodel "
            + "file (required)");

    /**
     * The option defining the model to load with the CLI.
     * <p>
     * This is a required option. The argument of the option is a path to an existing file containing a model. The
     * provided model must conform to the metamodel specified in {@link #METAMODEL_OPTION}.
     */
    public static final Option MODEL_OPTION = new Option("m", "model", true, "Location of the model file (required)");

    /**
     * The option defining the constraints to evaluate with the CLI.
     * <p>
     * This is a required option. The argument of the option is a path to an existing file containing the constraints
     * . The provided file's extension has to be {@code .ocl}.
     */
    public static final Option CONSTRAINTS_OPTION = new Option("c", "constraints", true, "Location of the constraints"
            + " file (required)");

    /**
     * The option to print a help message from the CLI.
     */
    public static final Option HELP_OPTION = new Option("h", "help", false, "Help");

    /**
     * Evaluates a set of constraints over a given model.
     * <p>
     * This method requires the following arguments:
     * <ul>
     *     <li>{@code -M} (or {@code --metamodel}): the path to the file containing the metamodel the constraints are
     *     defined on.</li>
     *     <li>{@code -m} (or {@code --model}): the path to the file containing the model conforming to the
     *     provided metamodel.</li>
     *     <li>{@code -c} (or {@code --constraints}): the path to the file containing the constraints to evaluate.</li>
     * </ul>
     * If one option is missing the CLI will throw a {@link MissingArgumentException}.
     * <p>
     * This method validates the provided model (conforming to the given metamodel) against the provided constraints.
     * If all the constraints are satisfied the method exits with a success code ({@code 0}). If at least one
     * constraint is not satisfied the method exists with an error code ({@code 1}) and prints a summary of the
     * violated constraints in the standard output.
     *
     * @param args the program's arguments
     * @throws Exception if an error occurred during the execution
     * @see #METAMODEL_OPTION
     * @see #MODEL_OPTION
     * @see #CONSTRAINTS_OPTION
     */
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        /*
         * We don't use Option#setRequired(true) here, because we want users to be able to run ocl-cli -h. If we
         * require the other arguments the parser will throw an exception, which is not very user-friendly.
         */
        options.addOption(METAMODEL_OPTION);
        options.addOption(MODEL_OPTION);
        options.addOption(CONSTRAINTS_OPTION);
        options.addOption(HELP_OPTION);

        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp(CMD_LINE_SYNTAX, options);
            throw e;
        }
        if (cmd.hasOption(HELP_OPTION)) {
            formatter.printHelp(CMD_LINE_SYNTAX, options);
            System.exit(0);
        }

        File metamodelFile;
        File modelFile;
        File constraintsFile;
        if (cmd.hasOption(METAMODEL_OPTION)) {
            metamodelFile = new File(cmd.getOptionValue(METAMODEL_OPTION));
        } else {
            formatter.printHelp(CMD_LINE_SYNTAX, options);
            throw new MissingArgumentException(METAMODEL_OPTION);
        }
        if (cmd.hasOption(MODEL_OPTION)) {
            modelFile = new File(cmd.getOptionValue(MODEL_OPTION));
        } else {
            formatter.printHelp(CMD_LINE_SYNTAX, options);
            throw new MissingArgumentException(MODEL_OPTION);
        }
        if (cmd.hasOption(CONSTRAINTS_OPTION)) {
            constraintsFile = new File(cmd.getOptionValue(CONSTRAINTS_OPTION));
        } else {
            formatter.printHelp(CMD_LINE_SYNTAX, options);
            throw new MissingArgumentException(CONSTRAINTS_OPTION);
        }
        OCLRunner oclRunner = new OCLRunner();

        Diagnostic diagnostics = oclRunner.validate(metamodelFile, modelFile, constraintsFile);
        if (diagnostics.getSeverity() == Diagnostic.OK) {
            System.out.println("OCL validation succeeded without errors");
            System.exit(0);
        } else {
            String formattedDiagnostics = PivotUtil.formatDiagnostics(diagnostics, "\n");
            System.out.println("OCL validation failed, see errors below");
            System.out.println(formattedDiagnostics);
            System.exit(1);
        }
    }
}
