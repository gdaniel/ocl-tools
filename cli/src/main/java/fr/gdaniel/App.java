package fr.gdaniel;

import fr.gdaniel.ocl.OCLRunner;
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
 * Hello world!
 */
public class App {

    public static final Option METAMODEL_OPTION = new Option("M", "metamodel", true, "Location of the metamodel "
            + "file (required)");

    public static final Option MODEL_OPTION = new Option("m", "model", true, "Location of the model file (required)");

    public static final Option CONSTRAINTS_OPTION = new Option("c", "constraints", true, "Location of the constraints"
            + " file (required)");

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(METAMODEL_OPTION);
        options.addOption(MODEL_OPTION);
        options.addOption(CONSTRAINTS_OPTION);
        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp("ocl runner", options);
            throw new RuntimeException();
        }
        File metamodelFile;
        File modelFile;
        File constraintsFile;
        if (cmd.hasOption(METAMODEL_OPTION)) {
            metamodelFile = new File(cmd.getOptionValue(METAMODEL_OPTION));
        } else {
            formatter.printHelp("ocl runner", options);
            throw new MissingArgumentException(METAMODEL_OPTION);
        }
        if (cmd.hasOption(MODEL_OPTION)) {
            modelFile = new File(cmd.getOptionValue(MODEL_OPTION));
        } else {
            formatter.printHelp("ocl runner", options);
            throw new MissingArgumentException(MODEL_OPTION);
        }
        if (cmd.hasOption(CONSTRAINTS_OPTION)) {
            constraintsFile = new File(cmd.getOptionValue(CONSTRAINTS_OPTION));
        } else {
            formatter.printHelp("ocl runner", options);
            throw new MissingArgumentException(CONSTRAINTS_OPTION);
        }
        OCLRunner oclRunner = new OCLRunner();

        Diagnostic diagnostics = oclRunner.checkConstraints(metamodelFile, modelFile, constraintsFile);
        if(diagnostics.getSeverity() == Diagnostic.OK) {
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
