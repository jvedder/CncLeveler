package cncleveler;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * Provides a method to interpolate a Z offset at a specific (X,Y) coordinate based on the probe
 * data.
 * 
 * Processes raw probe values passed to the constructor into an internal grid format that allows for
 * easy querying and interpolation. The (X,Y) values are offset buy the work coordinate offset. And
 * Z is offset so that Z=0 at (X,Y) = (0,0). 
 * 
 */
public class ProbeGrid
{
    private static final Logger logger = Logger.getLogger((Main.class.getName()));

    List<Point3> probes = null;

    protected int xsize = 0;
    protected int ysize = 0;

    protected Double[] xgrid;
    protected Double[] ygrid;
    protected Double[][] zprobe;

    /**
     * Constructor, which requires a list of Point3(x,y,z) probe values. Processes the values into a
     * internal arrays for easier interpolation.
     *
     * @param probePoints List of raw probe points
     */
    public ProbeGrid(List<Point3> rawProbePoints)
    {
        logger.info("Creating Probe Grid");

        // Offset probe values to work coordinates
        offsetProbes(rawProbePoints);
        makeXYgrids();
        makeZprobes();

        logProbeGrid();
        logger.info("Probe Grid Complete");
    }

    /**
     * Computes the Z probe offset for a given an (x,y) coordinate by linearly interpolating the Z
     * probe data in both X and Y. Some references called this "bilinear interpolation".
     * 
     * <pre>
     * Algorithm: 
     * 1. Determine the Z values at points at A,B,C,D in the four probe grid corners around 
     *    point G at target (x,y).
     *        A is at coordinates (xgrid[i],  ygrid[j]).
     *        B is at coordinates (xgrid[i],  ygrid[j+1]).
     *        C is at coordinates (xgrid[i+1],ygrid[j]).
     *        D is at coordinates (xgrid[i+1],ygrid[j+1]).
     * 2. Linearly interpolate in Y between Z values at points A and B to obtain z_left 
     *    at point E at coordinates (xgrid[i],  y). 
     * 3. Linearly interpolate in Y between Z values at points C and D to obtain z_right 
     *    at point F at coordinates (xgrid[i+1], y).
     * 4. Linearly interpolate in X between z_left and z_right to obtain Z at point G at (X,Y).
     * 
     *         B               D 
     *         |               |
     *         |               |
     *         |   (x,y)       |
     *  z_left E-----G---------F z_right
     *         |               |
     *         A               C
     * 
     * Note: Algebraically, it does not matter if we interpolate in X first between (A,C) and (B,D)
     * or Y first between (A,B) and (C,D). The result is the same.
     * 
     * For values outside the grid (in either x or y), the closest 4 points within the grid are used
     * to extrapolate outside the grid.
     * </pre>
     * 
     * @param x The X coordinate to use in interpolation
     * @param y The Y coordinate to use in interpolation
     * @return the interpolated Z value
     */
    public double getProbeHeight(double x, double y)
    {
        double z1, z2;

        int i = findGridIndex(xgrid, x);
        int j = findGridIndex(ygrid, y);

        double y_ratio = (y - ygrid[j]) / (ygrid[j + 1] - ygrid[j]);

        // linear interpolate in y on left side;
        z1 = zprobe[j][i];
        z2 = zprobe[j + 1][i];
        double z_left = z1 + (z2 - z1) * y_ratio;

        // linear interpolate in y on right side;
        z1 = zprobe[j][i + 1];
        z2 = zprobe[j + 1][i + 1];
        double z_right = z1 + (z2 - z1) * y_ratio;

        double x_ratio = (x - xgrid[i]) / (xgrid[i + 1] - xgrid[i]);

        // linear interpolate in x between left and right sides;
        double z = z_left + (z_right - z_left) * x_ratio;

        return z;
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
    private int findGridIndex(Double[] grid, double v)
    {
        int n = grid.length;
        for (int i = 1; i < n; i++)
        {
            if (v <= grid[i]) return i - 1;
        }
        return n - 2;
    }
    

    /**
     * Offsets each probe point to the Work Coordinate System (G54 .. G59)
     * 
     * @param rawProbePoints the probe points from the GRBL log file in machine coordinates
     */
    private void offsetProbes(List<Point3> rawProbePoints)
    {
        logger.info("   Offseting to probe grid by " + Config.probe_offset);

        probes = new ArrayList<Point3>(rawProbePoints.size());

        for (Point3 p : rawProbePoints)
        {
            probes.add(p.relativeTo(Config.probe_offset));
        }
    }

    /**
     * Creates the X grid and Y grid arrays from the raw probe data provided to the class
     * constructor. They contain the unique x and unique y values in sorted order to facilitate
     * indexing into the Z Probe array.
     */
    private void makeXYgrids()
    {
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

        // Convert to arrays for indexing
        xgrid = new Double[xsize];
        ygrid = new Double[ysize];
        xgrid = xvalues.toArray(xgrid);
        ygrid = yvalues.toArray(ygrid);

        // Because these are Doubles, check for near duplicate values as a sanity check.
        // For example, 15.0 and 14.999999999
        checkForDuplicates(xgrid, "X");
        checkForDuplicates(ygrid, "Y");
    }

    /**
     * Creates the Z probe 2-dimensional array using the same index values as the xgrid and ygrid
     * arrays
     */
    private void makeZprobes()
    {
        zprobe = new Double[ysize][xsize];

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
                    logger.severe(String.format("Missing Probe value at [%d,%d] (%.3f,%.3f)", i, j, x, y));
                }
            }
        }

        // Offset so that Z = 0 at (x,y) = (0,0)
        double zOffset = getProbeHeight(0.0, 0.0);
        logger.info(String.format("Z offset = %.3f", zOffset));

        // offset all probe values
        for (int i = 0; i < xsize; i++)
        {
            for (int j = 0; j < ysize; j++)
            {
                if (!Double.isNaN(zprobe[j][i]))
                {
                    zprobe[j][i] -= zOffset;
                }
            }
        }
    }

    /**
     * Reviews the xgrid and ygrid array to check for near duplicates. Because the coordinate values
     * are floating point numbers reported by GRBL, there is a chance of round-off error in the
     * reported x or y coordinates. For example 15.0 and 14.999999999. This function checks for and
     * reports pairs of x or y coordinates that are less than 0.1 from each other. Results are
     * reported to the logger as a warning.
     */
    private void checkForDuplicates(Double[] grid, String axis)
    {
        double prev = Double.NEGATIVE_INFINITY;
        for (double v : grid)
        {
            // We will never probe with less than a 0.01 mm [0.4 mil] grid
            if ((v - prev) < 0.01)
            {
                logger.warning(String.format("Near duplicate %s-Axis probe values: %.6f, %.6f", axis, prev, v));
            }
            prev = v;
        }
    }

    /**
     * Prints the X and Y Grid and Z-probe array to the log file.
     */
    private void logProbeGrid()
    {
        logger.info("   X grid size: " + xsize);
        logger.fine("   X grid = ");
        logger.fine("   " + arrayToString(xgrid));

        logger.info("   Y grid size: " + ysize);
        logger.fine("   Y grid = ");
        logger.fine("   " + arrayToString(ygrid));

        logger.fine("   Z probes = ");

        for (int j = 0; j < ysize; j++)
        {
            Double[] row = new Double[xsize];
            for (int i = 0; i < xsize; i++)
            {
                row[i] = zprobe[j][i];
            }
            logger.fine("   " + arrayToString(row));
        }

        // Log the min/max value for each axis
        double maxZ = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        for (int j = 0; j < ysize; j++)
        {
            for (int i = 0; i < xsize; i++)
            {
                double z = zprobe[j][i];
                minZ = Math.min(minZ, z);
                maxZ = Math.max(maxZ, z);
            }
        }
        Point3 min = new Point3(xgrid[0], ygrid[0], minZ);
        Point3 max = new Point3(xgrid[xsize - 1], ygrid[ysize - 1], maxZ);

        logger.info("   Min Z: " + min);
        logger.info("   Max Z: " + max);
    }

    /**
     * Converts an array of double to a printable string.
     * 
     * @param grid array to convert
     */
    private String arrayToString(Double[] grid)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Boolean first = true;
        for (int i = 0; i < grid.length; i++)
        {
            if (!first) sb.append(", ");
            sb.append(String.format("%8.3f", grid[i]));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}
