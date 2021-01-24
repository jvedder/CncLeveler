package cncleveler;

import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import cncleveler.logging.CncLogFormatter;

public class Main 
{
    protected static Logger logger = Logger.getLogger((Main.class.getName()));   
    protected static FileHandler logFileHander;
    protected static ConsoleHandler consoleHandler;
    
    public static void main(String[] args) throws Exception
    {
        setupLogger();       
        logger.info("Starting");
        
        List<Point3> probes = ProbeLogReader.read("probe-test-data.txt");
        
        ProbeGrid grid = new ProbeGrid(probes);
        
        logger.info("Done.");
     }
    
    
    
    /**
     * Sets up the application logging to a file
     */
    protected static void setupLogger() throws SecurityException, IOException
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
