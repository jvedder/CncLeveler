package cncleveler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class PyPlotGrid
{
    private static Logger logger = Logger.getLogger((Main.class.getName()));
    
    public void plot(ProbeGrid probeGrid) throws IOException
    {
        logger.info("Creating plot.py");

        // Open output file for writing
        Path outFile = Paths.get("plot.py");
        BufferedWriter out = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8);

        // Python imports
        out.write("import numpy as np\n");
        out.write("import matplotlib.pyplot as plt\n");
        out.write("from matplotlib import cm\n");
        out.write("from mpl_toolkits.mplot3d import Axes3D\n");
        out.write("\n");

        // Build X Array
        out.write("x=[\n");
        for (int y = 0; y <= 50; y++)
        {
            out.write("[");
            for (int x = 0; x <= 78; x++)
            {
                if (x > 0) out.write(',');
                out.write(Integer.toString(x));
            }
            out.write("],\n");
        }
        out.write("]\n");

        // Build y Array
        out.write("y=[\n");
        for (int y = 0; y <= 50; y++)
        {
            out.write("[");
            for (int x = 0; x <= 78; x++)
            {
                if (x > 0) out.write(',');
                out.write(Integer.toString(y));
            }
            out.write("],\n");
        }
        out.write("]\n");

        // Build Z Array
        out.write("z=[\n");
        for (int y = 0; y <= 50; y++)
        {
            out.write("[");
            for (int x = 0; x <= 78; x++)
            {
                if (x > 0) out.write(',');
                double z = probeGrid.getProbeHeight((double) x, (double) y);
                out.write(String.format("%.3f", z));
            }
            out.write("],\n");
        }
        out.write("]\n");

        // Plot the surface
        out.write("# Plot the surface.\n");
        out.write("X = np.array(x)\n");
        out.write("Y = np.array(y)\n");
        out.write("Z = np.array(z)\n");
        out.write("fig = plt.figure()\n");
        out.write("ax = fig.gca(projection='3d')\n");
        out.write("surf = ax.plot_surface(X, Y, Z, rstride=1, cstride=1, cmap=cm.coolwarm)\n");
        out.write("fig.colorbar(surf, shrink=0.5, aspect=5)\n");

        
        // X Lines
        out.write("# X Lines\n");
        for (int j = 0; j < probeGrid.ysize; j++)
        {
            out.write("x=[");
            for (int i = 0; i < probeGrid.xsize; i++)               
            {
                if (i > 0) out.write(',');
                out.write(String.format("%.3f", probeGrid.xgrid[i]));
            }
            out.write("]\n");

            out.write("y=[");
            for (int i = 0; i < probeGrid.xsize; i++)               
            {
                if (i > 0) out.write(',');
                out.write(String.format("%.3f", probeGrid.ygrid[j]));
            }
            out.write("]\n");
            
            out.write("z=[");
            for (int i = 0; i < probeGrid.xsize; i++)               
            {
                if (i > 0) out.write(',');
                out.write(String.format("%.3f", probeGrid.zprobe[j][i]));
            }
            out.write("]\n");
            out.write("ax.plot(np.array(x),np.array(y),np.array(z),linewidth=2,color='black')\n");
        }
        
        // Y Lines
        out.write("# Y Lines\n");
        for (int i = 0; i < probeGrid.xsize; i++)
        {
            out.write("x=[");
            for (int j = 0; j < probeGrid.ysize; j++)               
            {
                if (j > 0) out.write(',');
                out.write(String.format("%.3f", probeGrid.xgrid[i]));
            }
            out.write("]\n");

            out.write("y=[");
            for (int j = 0; j < probeGrid.ysize; j++)               
            {
                if (j > 0) out.write(',');
                out.write(String.format("%.3f", probeGrid.ygrid[j]));
            }
            out.write("]\n");
            
            out.write("z=[");
            for (int j = 0; j < probeGrid.ysize; j++)                 
            {
                if (j > 0) out.write(',');
                out.write(String.format("%.3f", probeGrid.zprobe[j][i]));
            }
            out.write("]\n");
            out.write("ax.plot(np.array(x),np.array(y),np.array(z),linewidth=2,color='black')\n");
        }

        out.write("plt.show()\n");

        out.close();
        
        logger.info("plot.py complete");
    }
}
