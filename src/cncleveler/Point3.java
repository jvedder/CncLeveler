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
    public Point3( )
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
     * @param p0 the point to use as the "origin" for the offset,
     * @return the offset as a Point3
     */
    public Point3 relativeTo(Point3 p0)
    {
        return new Point3(x-p0.x, y-p0.y, z-p0.z);
    }
    

    /**
     * Returns a new Point3 representing the offset of this instance to the specified (x,y,z) coordinate.
     * @param x the x-coordinate of the point to use as the "origin" for the offset.
     * @param y the y-coordinate of the point to use as the "origin" for the offset.     
     * @param z the z-coordinate of the point to use as the "origin" for the offset.
     * @return the offset as a Point3
     */
    public Point3 relativeToXYZ(double x0, double y0, double z0)
    {
        return new Point3(x-x0, y-y0, z-z0);
    }
    
    
    /**
     * Returns a new Point3 representing the offset of this instance to the specified (x,y) coordinate.
     * @param x the x-coordinate of the point to use as the "origin" for the offset.
     * @param y the y-coordinate of the point to use as the "origin" for the offset.     
     * @param z the z-coordinate of the point to use as the "origin" for the offset.
     * @return the offset as a Point3
     */
    public Point3 relativeToXY(double x0, double y0)
    {
        return new Point3(x-x0, y-y0, z);
    }
 
    
    /**
     * Returns a new Point3 representing the offset of this instance to the specified (z) coordinate.
     * @param z the z-coordinate of the point to use as the "origin" for the offset.
     * @return the offset as a Point3
     */
    public Point3 relativeToZ(double z0)
    {
        return new Point3(x, y, z-z0);
    }
 
    
    /**
     * Printable representation of this point as (x,y,z).
     */
    public String toString()
    {
        return String.format("(%.3f,%.3f,%.3f)", x, y, z);
    }
}
