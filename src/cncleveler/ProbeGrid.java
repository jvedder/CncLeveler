package cncleveler;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

public class ProbeGrid
{
    private static Logger logger = Logger.getLogger((Main.class.getName()));

    List<Point3> probes = null;

    //private double x0 = 0.0;
    //private double y0 = 0.0;
    protected double z0 = 0.0;

    protected int xsize = 0;
    protected int ysize = 0;

    protected double[] xgrid;
    protected double[] ygrid;
    protected double[][] zprobe;

    public Point3 extents_min;
    public Point3 extents_max;

    /**
     * Constructor that requires a list of Point3(x,y,z) probe values. Processes the values into a
     * arrays for easier interpolation.
     *
     * @param probePoints List of probe points
     */
    public ProbeGrid(List<Point3> rawProbePoints)
    {
        logger.info("Creating Probe Grid");

        offsetProbes(rawProbePoints);
        findExtents();
        makeXYgrids();
        logger.info("xgrid = " + arrayToSTring(xgrid));
        logger.info("ygrid = " + arrayToSTring(ygrid));
        checkForDuplicates(xgrid, "X");
        checkForDuplicates(ygrid, "Y");
        makeZprobes();
        logZprobes();
        
        // compute Z offset
        z0 = 0.0d;
        z0 = getProbeHeight(0.0,  0.0);
        
        logger.info(String.format("Z0 = %.3f", z0));
        
        logger.info("Probe Grid Complete");        
    }

    /**
     * Computes the z probe offset for a given an (x,y) coordinate by linearly interpolating the
     * probe data.
     * 
     * @param x The x coordinate to use in interpolation
     * @param y The y coordinate to use in interpolation
     * @return the interpolated z value
     */
    public double getProbeHeight(double x, double y)
    {
        double z1, z2;

        int i = findAxisIndex(xgrid, x);
        int j = findAxisIndex(ygrid, y);

        double x_ratio = (x - xgrid[i]) / (xgrid[i + 1] - xgrid[i]);
        double y_ratio = (y - ygrid[j]) / (ygrid[j + 1] - ygrid[j]);

        // linear interpolate in y on left side;
        z1 = zprobe[j][i];
        z2 = zprobe[j + 1][i];
        double z_left = z1 + (z2 - z1) * y_ratio;

        // linear interpolate in y on right side;
        z1 = zprobe[j][i + 1];
        z2 = zprobe[j + 1][i + 1];
        double z_right = z1 + (z2 - z1) * y_ratio;

        // linear interpolate in x between left and right sides;
        double z = z_left + (z_right - z_left) * x_ratio;
        
        // Offset by Z0
        return z - z0;
    }

    /**
     * Given an array of n grid values and a value v, returns i such that grid[i] <= v < grid[i+1].
     * For out-of-bounds values, returns 0 if v <= grid[0] or (n-2) if v >= grid[n-1]. Both the
     * returned value i and (i+1) will valid index to the array.
     * 
     * @param grid a sorted array of grid values
     * @param v a value to search for
     * @return an index i such that grid[i] <= v < grid[i+1]
     */
    private int findAxisIndex(double[] grid, double v)
    {
        int n = grid.length;
        for (int i = 1; i < n; i++)
        {
            if (v <= grid[i]) return i - 1;
        }
        return n - 2;
    }

    /**
     * offsets each probe point to the Work Coordinate System (G54 .. G59)
     * 
     * @param rawProbePoints the probe points from the GRBL log file in machine coordinates
     */
    private void offsetProbes(List<Point3> rawProbePoints)
    {
        logger.info("  Adding offset to probe grid: " + Config.probe_offset);

        probes = new ArrayList<Point3>(rawProbePoints.size());

        for (Point3 p : rawProbePoints)
        {
            probes.add(p.relativeTo(Config.probe_offset));
        }
    }

    /**
     * Utility function to find the minimum and maximum value for each of x, y and z. Results are
     * stored in extents_min and extents_max.
     */
    private void findExtents()
    {
        extents_min = new Point3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        extents_max = new Point3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        extents_min = new Point3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        extents_max = new Point3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (Point3 probe : probes)
        {
            extents_min.x = Math.min(extents_min.x, probe.x);
            extents_min.y = Math.min(extents_min.y, probe.y);
            extents_min.z = Math.min(extents_min.z, probe.z);

            extents_max.x = Math.max(extents_max.x, probe.x);
            extents_max.y = Math.max(extents_max.y, probe.y);
            extents_max.z = Math.max(extents_max.z, probe.z);
        }
        logger.info("  Probe extents min:" + extents_min + ", max:" + extents_max);

    }

    /**
     * Utility function to create the xgrid and ygrid arrays from the probe data provided to the
     * class constructor. They contain the unique x and unique y values in sorted order. Reports the
     * length of each array to the logger.
     */
    private void makeXYgrids()
    {
        int i;

        // Use a sorted set to get unique X and Y values in sorted order
        SortedSet<Double> xvalues = new TreeSet<Double>();
        SortedSet<Double> yvalues = new TreeSet<Double>();
        for (Point3 probe : probes)
        {
            xvalues.add(probe.x);
            yvalues.add(probe.y);
        }

        // Record the grid sizes
        xsize = xvalues.size();
        ysize = yvalues.size();

        logger.info("X grid size: " + xsize);
        logger.info("Y grid size: " + ysize);

        // Convert xvalues set to xgrid array for easy indexing
        xgrid = new double[xsize];
        i = 0;
        for (double x : xvalues)
        {
            xgrid[i++] = x;
        }

        // Convert yvalues set to ygrid array for easy indexing
        ygrid = new double[ysize];
        i = 0;
        for (double y : yvalues)
        {
            ygrid[i++] = y;
        }
    }

    /**
     * Creates the zprobe 2-dimensional array using the same index values as the xgrid and ygrid
     * arrays
     */
    private void makeZprobes()
    {
        zprobe = new double[ysize][xsize];

        for (int i = 0; i < xsize; i++)
        {
            double x = xgrid[i];
            for (int j = 0; j < ysize; j++)
            {
                double y = ygrid[j];

                // find the z value at this (x,y) position
                zprobe[j][i] = Double.NaN;
                for (Point3 probe : probes)
                {
                    if ((probe.x == x) && (probe.y == y))
                    {
                        zprobe[j][i] = probe.z;
                        break;
                    }
                }
                if (Double.isNaN(zprobe[j][i]))
                {
                    logger.warning(String.format("Missing Probe value at index [%d,%d] (%.2f,%.2f)", i, j, x, y));
                }
            }
        }
    }

    /**
     * Utility function to review the xgrid and ygrid array to check for near duplicates. Because
     * the coordinate values are floating point numbers reported bu GRBL, there is a chance of
     * round-off error in the reported x or y coordinates. This function checks for and reports
     * pairs of x or y coordinates that are less than 0.1 from each other. Results are reported to
     * the logger as a warning.
     */
    private void checkForDuplicates(double[] grid, String axis)
    {
        double prev = Double.NEGATIVE_INFINITY;
        for (double v : grid)
        {
            if ((v - prev) < 0.1)
            {
                logger.warning(String.format("Near duplicate %s-Axis probe values: %.4f, %.4f", axis, prev, v));
            }
            prev = v;
        }
    }

    /**
     * Converts an array of double to a printable string.
     * 
     * @param grid array to convert
     */
    private String arrayToSTring(double[] grid)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Boolean first = true;
        for (int i = 0; i < grid.length; i++)
        {
            if (!first)
            {
                sb.append(", ");
                first = false;
            }
            sb.append(String.format("%8.3f", grid[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Prints the Z-probe array to the log file.
     */
    private void logZprobes()
    {
        logger.info("zprobes = ");

        for (int j = 0; j < ysize; j++)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("[");

            Boolean first = true;
            for (int i = 0; i < xsize; i++)
            {
                if (!first)
                {
                    sb.append(", ");
                    first = false;
                }
                sb.append(String.format("%8.3f", zprobe[j][i]));
            }
            sb.append("]");
            logger.info(sb.toString());
        }
    }

    // /**
    // * Method to set the (x,y) offset from work piece coordinates to machine coordinates.
    // *
    // * @param x
    // * @param y
    // */
    // public void setOrigin(double x, double y)
    // {
    // x0 = x;
    // y0 = y;
    // }

}
