package featureX;

/**
 * Created by dorien on 29/06/16.
 */
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());
    private String[] args = null;
    private Options options = new Options();


    //setting default values
    public int windowsPerBar = 4;
    public int meterUnits = 4;
    public String inputfile = "/home/dorien/workspace/PMusicOR/data/Haydn_half.xml";
    public int windowLength = 0;

    public Cli(String[] args) {

        this.args = args;

        options.addOption("h", "help", false, "show help.");
        options.addOption("inputfile", "var", true, "Input musicXML file.");
        options.addOption("windowsPerBar", "var", true, "The number of windows per bar used to calculate the tension. (depreciated)");
        options.addOption("windowLength", "var", true, "Length of the windows expressed as 4 (quarter note), 8, 16, etc. (This setting has preference over windowsPerBar.) Default value is 1 eight note.");
        options.addOption("meterUnits", "var", true, "option only used to change the inscore rendering: number of units of meter. Default value is 4.");

    }

    public void parse() {
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("t"))
                help();

            if (cmd.hasOption("inputfile")) {
                log.log(Level.INFO, "Using argument -inputfile=" + cmd.getOptionValue("inputfile"));
                inputfile = cmd.getOptionValue("inputfile");

                // Whatever you want to do with the setting goes here
            } else {
                log.log(Level.SEVERE, "Missing -inputfile option");
                help();
            }


            if (cmd.hasOption("windowsPerBar")) {
                log.log(Level.INFO, "Using argument -windowsPerBar=" + cmd.getOptionValue("windowsPerBar"));
                windowsPerBar = Integer.valueOf(cmd.getOptionValue("windowsPerBar"));


                // Whatever you want to do with the setting goes here
            } else {
                //log.log(Level.SEVERE, "MIssing windowsPer option");
                //help();
            }

            if (cmd.hasOption("windowLength")) {
                log.log(Level.INFO, "Using argument -windowLength=" + cmd.getOptionValue("windowLength"));
                String option = cmd.getOptionValue("windowLength");
                windowLength = Integer.valueOf(option);
                //windowLength = Integer.getInteger(option);

                int test = 0;
                // Whatever you want to do with the setting goes here
            } else {
                //log.log(Level.SEVERE, "MIssing windowLength option");
                //help();
            }

            if (cmd.hasOption("meterUnits")) {
                log.log(Level.INFO, "Using argument -meterUnits=" + cmd.getOptionValue("meterUnits"));
                meterUnits = Integer.valueOf(cmd.getOptionValue("meterUnits"));


                // Whatever you want to do with the setting goes here
            } else {
                //log.log(Level.SEVERE, "MIssing windowLength option");
                //help();
            }



        } catch (ParseException e) {
            log.log(Level.SEVERE, "Failed to parse comand line properties", e);
            help();
        }


    }

    private void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
        System.exit(0);
    }
}
