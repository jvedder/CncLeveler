package cncleveler;

import java.util.List;
import java.util.logging.Logger;


public class ProbeGridAlternateIdea
{
    protected static Logger logger = Logger.getLogger((Main.class.getName()));

    List<Point3> probes = null;

    protected double x0 = 0.0;
    protected double y0 = 0.0;
    
    protected int xsize = 0;
    protected int ysize = 0;
    

    protected Point3 extents_min;
    protected Point3 extents_max;

    protected Point3 left_bottom;
    protected Point3 left_top;
    protected Point3 right_bottom;
    protected Point3 right_top;

    public ProbeGridAlternateIdea(List<Point3> probePoints)
    {
        // find the extents
        logger.info("Creating Probe Grid #2");
        this.probes = probePoints;

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

    public void find(Point3 point)
    {
        left_bottom = new Point3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 0);
        left_top = new Point3(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
        right_bottom = new Point3(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0);
        right_top = new Point3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0);

        for (Point3 probe : probes)
        {
            // left_bottom
            if ((probe.x <= point.x) && (probe.y <= point.y) && (probe.x >= left_bottom.x)
                    && (probe.y >= left_bottom.y))
            {
                left_bottom = probe;
            }

            // left_top
            if ((probe.x <= point.x) && (probe.y >= point.y) && (probe.x >= left_top.x) && (probe.y <= left_top.y))
            {
                left_top = probe;
            }

            // right_bottom
            if ((probe.x >= point.x) && (probe.y <= point.y) && (probe.x <= right_bottom.x)
                    && (probe.y >= right_bottom.y))
            {
                right_bottom = probe;
            }

            // right_top
            if ((probe.x >= point.x) && (probe.y >= point.y) && (probe.x <= right_top.x) && (probe.y <= right_top.y))
            {
                right_top = probe;
            }
        }

    }

    public void setZero(double x, double y)
    {
        x0 = x;
        y0 = y;
    }

}
