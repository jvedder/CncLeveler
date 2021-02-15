package cncleveler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import cncleveler.logging.CncLogFormatter;

public class Main
{
    private static final Logger logger = Logger.getLogger((Main.class.getName()));
    private static FileHandler logFileHander;
    private static ConsoleHandler consoleHandler;

    public static void main(String[] args) throws Exception
    {
        setupLogger();
        logger.info("Starting");

        List<Point3> probes = ProbeLogReader.read("probe-results-4.txt");
        ProbeGrid grid = new ProbeGrid(probes);
        //ProbeGrid grid = new ProbeGrid(testProbes());

         PyPlotGrid plot = new PyPlotGrid();
         plot.plot(grid);

        GCodeParser parser = new GCodeParser();
        List<State> states = parser.read("gcode.nc");
        parser = null;
        
        Leveler.level(states, grid);

        //replayStates(states);
        
        GCodeWriter.write("gcode_leveled.nc", states);
 
        logger.info("Done.");
    }

    protected static void replayStates(List<State> states)
    {
        State globalState = new State();
        for (State state : states)
        {
            globalState.mergeWith(state);

            System.out.println("----------------------------------");
            System.out.println("Orig:   " + state.originalText);
            System.out.println("State:  " + state.toString());
            System.out.println("Global: " + globalState.toString());
            // globalState.print();
        }
    }

    protected static List<Point3> testProbes()
    {
        List<Point3> probes = new ArrayList<>();

        probes.add(new Point3(2, 2, 0.003));
        probes.add(new Point3(24, 2, -0.121));
        probes.add(new Point3(50, 2, -0.143));
        probes.add(new Point3(74, 2, -0.143));
        probes.add(new Point3(2, 25, 0.035));
        probes.add(new Point3(24, 25, -0.065));
        probes.add(new Point3(50, 25, -0.1));
        probes.add(new Point3(74, 25, -0.104));
        probes.add(new Point3(2, 48, 0));
        probes.add(new Point3(24, 48, -0.036));
        probes.add(new Point3(50, 48, -0.041));
        probes.add(new Point3(74, 48, -0.038));

        return probes;
    }

    /**
     * Sets up the application logging to a file
     */
    private static void setupLogger() throws SecurityException, IOException
    {
        LogManager.getLogManager().reset();
        logFileHander = new FileHandler("CncLeveler.%u.log");
        logFileHander.setFormatter(new CncLogFormatter());
        logger.addHandler(logFileHander);

        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new CncLogFormatter());
        logger.addHandler(consoleHandler);

        logger.setLevel(Level.ALL);
    }

}
