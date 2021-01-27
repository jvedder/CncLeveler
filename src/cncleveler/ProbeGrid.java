package cncleveler;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

public class ProbeGrid
{
    protected final boolean DEBUG = true;
    protected static Logger logger = Logger.getLogger((Main.class.getName()));

    List<Point3> probes = null;

    protected double x0 = 0.0;
    protected double y0 = 0.0;

    public int xsize = 0;
    public int ysize = 0;

    public double[] xgrid;
    public double[] ygrid;
    public double[][] zprobe;

    public Point3 extents_min;
    public Point3 extents_max;


    /**
     * Constructor that requires a list of (x,y,z) probe values. Processes the
     * values into a arrays for easier interpolation.
     *
     * @param probePoints
     *            List of probe points
     */
    public ProbeGrid(List<Point3> probePoints)
    {
        logger.info("Creating Probe Grid");

        probes = probePoints;
        findExtents();
        makeXYgrids();
        if (DEBUG)
        {
            printAxisGrid(xgrid, "X");
            printAxisGrid(ygrid, "Y");
        }
        checkForDuplicates(xgrid, "X");
        checkForDuplicates(ygrid, "Y");
        makeZprobes();
        if (DEBUG)
        {
            printZprobes();
        }
    }

    /**
     * Computes the z probe offset for a given an (x,y) coordinate by linearly
     * interpolating the probe data.
     * 
     * @param x
     *            The x coordinate to use in interpolation
     * @param y
     *            The y coordinate to use in interpolation
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
        z1 = zprobe[i][j];
        z2 = zprobe[i][j + 1];
        double z_left = z1 + (z2 - z1) * y_ratio;

        // linear interpolate in y on right side;
        z1 = zprobe[i + 1][j];
        z2 = zprobe[i + 1][j + 1];
        double z_right = z1 + (z2 - z1) * y_ratio;

        // linear interpolate in x between left and right sides;
        double z = z_left + (z_right - z_left) * x_ratio;

        return z;
    }

    
    /**
     * Given an array of n grid values and a value v, returns i such that
     * grid[i] <= v < grid[i+1]. For out-of-bounds values, returns 0 if v <=
     * grid[0] or (n-2) if v >= grid[n-1]. 1 *
     * 
     * @param grid
     *            a sorted array of grid values
     * @param v
     *            a value to search for
     * @return an index i such that grid[i] and grid[i+1] can be used for
     *         interpolation
     */
    protected int findAxisIndex(double[] grid, double v)
    {
        int n = grid.length;
        for (int i = 1; i < n; i++)
        {
            if (v <= grid[i]) return i - 1;
        }
        return n - 2;
    }

    /**
     * Utility function to find the minimum and maximum value for each of x, y
     * and z. Results are stored in extents_min and extents_max.
     */
    protected void findExtents()
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
    }

    /**
     * Utility function to create the xgrid and ygrid arrays from the probe data
     * provided to the class constructor. They contain the unique x and unique y values
     * in sorted order. Reports the length of each array to the logger.
     */
    protected void makeXYgrids()
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

        // Convert xvalues to xgrid array for easy indexing
        xgrid = new double[xsize];
        i = 0;
        for (double x : xvalues)
        {
            xgrid[i++] = x;
        }

        // Convert yvalues to ygrid array for easy indexing
        ygrid = new double[ysize];
        i = 0;
        for (double y : yvalues)
        {
            ygrid[i++] = y;
        }
    }

    /**
     * Utility function to create the zprobe array. The zprobe values have the
     * indexed the same as xgrid and ygrid.
     */
    protected void makeZprobes()
    {
        zprobe = new double[xsize][ysize];

        for (int i = 0; i < xsize; i++)
        {
            double x = xgrid[i];
            for (int j = 0; j < ysize; j++)
            {
                double y = ygrid[j];

                zprobe[i][j] = Double.NaN;
                for (Point3 probe : probes)
                {
                    if ((probe.x == x) && (probe.y == y))
                    {
                        zprobe[i][j] = probe.z;
                        break;
                    }
                }
                if (Double.isNaN(zprobe[i][j]))
                {
                    logger.warning(String.format("Missing Probe value at [%d,%d] (%.2f,%.2f)", i, j, x, y));
                }
            }
        }
    }

    /**
     * Utility function to review the xgrid and ygrid array to check for near duplicates. 
     * Because the coordinate values are floating point numbers reported bu GRBL,
     * there is a chance of round-off error in the reported x or y coordinates. This
     * function checks for and reports pairs of x or y coordinates that are less
     * than 0.1 from each other.  Results are reported to the logger as a warning.
     */
    protected void checkForDuplicates(double[] grid, String axis)
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
     * A debug function to print the values in the xgrid or ygrid arrays to System.out.
     * @param grid The xgrid or ygrid array to print
     * @param axis the string value "X" or "Y" to annotate the output
     */
    protected void printAxisGrid(double[] grid, String axis)
    {
        System.out.println(axis + " Grid values");
        for (int i = 0; i < grid.length; i++)
        {
            System.out.print(String.format("%8.2f, ", grid[i]));
        }
        System.out.println();
    }

    /**
     * A debug function to print the values in the zprobe arrays to System.out.
     */
        protected void printZprobes()
    {
        System.out.println("Z Probe values");
        for (int i = 0; i < xsize; i++)
        {
            for (int j = 0; j < ysize; j++)
            {
                System.out.print(String.format("%8.2f, ", zprobe[i][j]));
            }
            System.out.println();
        }
    }

    /**
     * Method to set the (x,y) offset from work piece coordinates to machine coordinates.
     * @param x
     * @param y
     */
    public void setOrigin(double x, double y)
    {
        x0 = x;
        y0 = y;
    }

}
