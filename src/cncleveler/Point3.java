package cncleveler;

/**
 * Represents a point in 3-dimensional Cartesian coordinates holding values for x, y and z.
 *
 */
public class Point3
{
    public double x = 0.0;
    public double y = 0.0;
    public double z = 0.0;

    /**
     * Constructor without specifying the (x,y,z) values.
     */
    public Point3()
    {
    }

    /**
     * Constructor that specifies (x,y,z) values.
     */
    public Point3(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Returns a new Point3 representing the offset of this instance from the specified point.
     */
    public Point3 relativeTo(Point3 p0)
    {
        return new Point3(x - p0.x, y - p0.y, z - p0.z);
    }

    /**
     * Returns a new Point3 representing the offset of this instance to the specified (x,y,z)
     * coordinate.
     */
    public Point3 relativeToXYZ(double x0, double y0, double z0)
    {
        return new Point3(x - x0, y - y0, z - z0);
    }

    /**
     * Returns a new Point3 representing the offset of this instance to the specified (x,y)
     * coordinate.
     */
    public Point3 relativeToXY(double x0, double y0)
    {
        return new Point3(x - x0, y - y0, z);
    }

    /**
     * Returns a new Point3 representing the offset of this instance to the specified z
     * coordinate.
     */
    public Point3 relativeToZ(double z0)
    {
        return new Point3(x, y, z - z0);
    }
    
    /**
     * Modifies this instance to the minimum of each axis between this and the coordinates provided.
     */
    public void minWith(double x0, double y0, double z0)
    {
        x = Math.min(x, x0);
        y = Math.min(y, y0);
        z = Math.min(z, z0);
    }
    /**
     * Modifies this instance to the maximum of each axis between this and the coordinates provided.
     */
    public void maxWith(double x0, double y0, double z0)
    {
        x = Math.max(x, x0);
        y = Math.max(y, y0);
        z = Math.max(z, z0);
    }
    
    /**
     * Printable representation of this point as (x,y,z).
     */
    public String toString()
    {
        return String.format("(%.3f,%.3f,%.3f)", x, y, z);
    }
}
