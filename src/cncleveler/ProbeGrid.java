package cncleveler;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

public class ProbeGrid
{
    protected static Logger logger = Logger.getLogger((Main.class.getName()));

    List<Point3> probes = null;

    protected double x0 = 0.0;
    protected double y0 = 0.0;

    protected int xsize = 0;
    protected int ysize = 0;

    protected double[] xgrid;
    protected double[] ygrid;
    protected double[][] zprobe;

    protected Point3 extents_min;
    protected Point3 extents_max;

    protected Point3 left_bottom;
    protected Point3 left_top;
    protected Point3 right_bottom;
    protected Point3 right_top;

    public ProbeGrid(List<Point3> probePoints)
    {
        logger.info("Creating Probe Grid");
        probes = probePoints;

        // Use a sorted set to get unique values in order
        SortedSet<Double> xvalues = new TreeSet<Double>();
        SortedSet<Double> yvalues = new TreeSet<Double>();
        for (Point3 probe : probes)
        {
            xvalues.add(probe.x);
            yvalues.add(probe.y);
        }
        
        //Record grid size
        xsize = xvalues.size();
        ysize = yvalues.size();

        logger.info("X grid size: " + xsize);
        logger.info("Y grid size: " + ysize);

        checkForDuplicates(xvalues, "X");
        checkForDuplicates(yvalues, "Y");

        xgrid = toArray(xvalues);
        ygrid = toArray(yvalues);

        printAxisGrid(xgrid, "X");
        printAxisGrid(ygrid, "Y");

        zprobe = new double[xsize][ysize];

        for (int i = 0; i < xsize; i++)
        {
            double x = xgrid[i];
            for (int j = 0; j < ysize; j++)
            {
                double y = ygrid[j];

                zprobe[i][j] = Double.NaN;
                for (Point3 probe : probePoints)
                {
                    if ((probe.x == x) && (probe.y == y))
                    {
                        zprobe[i][j] = probe.z;
                        //break;
                    } 
                }
                if (Double.isNaN(zprobe[i][j]))
                {
                    logger.warning(String.format("Missing Probe value at [%d,%d] (%f,%f)", i, j, x, y));
                }
            }
        }

        printZprobes();
    }

    protected void checkForDuplicates(SortedSet<Double> values, String axis)
    {
        double prev = Double.NEGATIVE_INFINITY;
        for (double v : values)
        {
            if ((v - prev) < 0.1)
            {
                logger.warning(String.format("Near duplicate %s probe values: %f, %f", axis, prev, v));
            }
            prev = v;
        }
    }

    protected double[] toArray(SortedSet<Double> values)
    {
        double[] grid = new double[values.size()];

        int i = 0;
        for (double v : values)
        {
            grid[i] = v;
            i++;
        }
        return grid;
    }

    protected void printAxisGrid(double[] grid, String axis)
    {
        System.out.println(axis + " Grid values");
        for (int i = 0; i < grid.length; i++)
        {
            System.out.print(String.format("%.2f,", grid[i]));
        }
        System.out.println();
    }

    protected void printZprobes()
    {
        System.out.println("Z Probe values");
        for (int i = 0; i < xsize; i++)
        {
            for (int j = 0; j < ysize; j++)
            {
                System.out.print(String.format("%.2f,", zprobe[i][j]));
            }
            System.out.println();
        }
    }

    public void setZero(double x, double y)
    {
        x0 = x;
        y0 = y;
    }

}
