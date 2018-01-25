import org.apache.commons.cli.*;

public class Main {
    // TODO: make it a CLI service.
    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("d", false, "Delete records"); // does not have a value
        options.addOption("c", true, "CSV Repository"); // has a value

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (cmd != null && cmd.hasOption("d")) {
            System.out.println("Clearing index");
        }
    }
}
