package cncleveler;

import java.util.List;
import java.util.logging.Logger;

public class Leveler
{
    private static final Logger logger = Logger.getLogger((Main.class.getName()));

    /**
     * Updates each state in states with an adjusted Z value based on probe data
     */
    public static void level(List<State> states, ProbeGrid grid)
    {
        logger.info("Leveling states");

        // The running state as each state is processed
        State globalState = new State();

        int count = 0;
        Point3 min = new Point3(Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);
        Point3 max = new Point3(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY);
                
                
        for (State state : states)
        {
            // Include this state in the global state
            globalState.mergeWith(state);
            
            // If motion and Z has been defined prior to this point...
            if (globalState.getGroup(Group.MOTION) != null && globalState.getAxis(Axis.Z) != null)
            {
                // and this state defines x, y, or z, then we need to update z in this state
                if (state.getAxis(Axis.X) != null || state.getAxis(Axis.Y) != null || state.getAxis(Axis.Z) != null)
                {

                    Double x = globalState.getAxis(Axis.X);
                    Double y = globalState.getAxis(Axis.Y);
                    Double z = globalState.getAxis(Axis.Z);
                    if (x != null && y != null && z != null)
                    {
                        double adjustedZ = z + grid.getProbeHeight(x, y);
                        state.setAxis(Axis.Z, adjustedZ);
                        globalState.setAxis(Axis.Z, adjustedZ);
                        count++;
                        
                        // compute min/max of each axis
                        min.minWith(x, y, adjustedZ);
                        max.maxWith(x, y, adjustedZ);
                    }
                }
            }
        }
        logger.info("   " + count + " blocks adjusted");
        logger.info("   After adjustment:");
        logger.info("     Min : " + min);
        logger.info("     Max : " + max);
        logger.info("Leveling complete");
    }
}
