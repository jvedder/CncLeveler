package cncleveler;

/**
 * Defines the offset of the probe data in machine coordinates to work coordinates coordinates. This should be taken from the G54-G59 or G28, G30 settings.
 *
 *  Hard-coded for now. Need to implement a config reader.
 * 
 * <pre>
 * [G54:-278.000,-155.000,-1.000]
 * [G55:-232.000,-152.000,-15.000]
 * [G28:-238.000,-155.000,-1.000]
 * [G30:-278.000,-155.000,-1.000]
 * </pre>
 */
public class Config
{
    /* Setting for probe-results-4.txt */
    public static Point3 probe_offset = new Point3( -272.5, -152.0, 0.0);
}
