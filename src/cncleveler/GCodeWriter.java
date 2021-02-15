package cncleveler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCodeWriter
{
    private static final Logger logger = Logger.getLogger((Main.class.getName()));

    public static void write(String filename, List<State> states) 
    {
        logger.info("Writing G Code to: " + filename);
        State globalState = new State();

        Path outFile = Paths.get(filename);
        int lineCount = 0;
        try (BufferedWriter out = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8))
        {
            for (State state : states)
            {
                out.write(state.toString());
                out.newLine();
                lineCount++;
                globalState.mergeWith(state);
            }
            out.close();
            logger.info("Wrote " + lineCount + " lines");
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "Error writing G Code File", ex);
        }

    }
}
