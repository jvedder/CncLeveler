package cncleveler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Reads a GRBL log file saved from UniversalGCodeSender console window and parses out the probe
 * values.
 *
 */
public class ProbeLogReader
{
    private static final Logger logger = Logger.getLogger((Main.class.getName()));

    /**
     * Reads the specified probe log file and returns a list of probe values in file order.
     * 
     * @param filename the filename of the probe log file.
     * @return a list of the probe values.
     * @throws IOException on IO errors
     */
    public static List<Point3> read(String filename) throws IOException
    {
        // Show progress
        logger.info("Reading: " + filename);

        // Open input file for reading
        Path inFile = Paths.get(filename);

        // BufferedReader supports readLine()
        BufferedReader in = Files.newBufferedReader(inFile, StandardCharsets.UTF_8);

        String line;
        List<Point3> probes = new ArrayList<Point3>();

        while ((line = in.readLine()) != null)
        {
            if (line.startsWith("[PRB:"))
            {
                //
                // Typical Line formated as:
                // [PRB:-262.500,-150.000,-20.966:1]
                //
                try
                {
                    // Pull out values between colons, then split at commas
                    String subline = line.split(":")[1];
                    String values[] = subline.split(",");
                    Point3 probe = new Point3();
                    probe.x = Double.parseDouble(values[0]);
                    probe.y = Double.parseDouble(values[1]);
                    probe.z = Double.parseDouble(values[2]);
                    probes.add(probe);
                }
                catch (Exception ex)
                {
                    System.out.println("Error parsing: " + line);
                }
            }
        }
        in.close();

        logProbeValues(probes);

        // Show progress
        logger.info("Reading complete");

        return probes;
    }

    /**
     * Reports the raw probe points to the logger for debug.
     * 
     * @param probes the list of raw probe point values
     */
    private static void logProbeValues(List<Point3> probes)
    {
        logger.info("   Raw Probe Points: " + probes.size());

        for (Point3 probe : probes)
        {
            logger.fine("   " + probe.toString());
        }
    }

}
