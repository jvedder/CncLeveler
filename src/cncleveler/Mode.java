package cncleveler;

/**
 * Defines labels for each supported modal G code word. Also defines the modal group
 * that the G code belongs to.
 */
public enum Mode
{
 // Motion modes: {G0, G1, G2, G3}
    RAPID ("G0", Group.MOTION),
    LINEAR ("G1", Group.MOTION),
    CIRCULAR_CW ("G2",Group.MOTION),
    CIRCULAR_CCW ("G3",Group.MOTION),

 // plane modes: {G17, G18, G19}
    XY_PLANE ("G17",Group.PLANE),
    XZ_PLANE ("G18",Group.PLANE),
    YZ_PLANE ("G19",Group.PLANE),

 // distance modes: {G90, G91}
    ABSOLUTE ("G90",Group.DISTANCE),
    INCREMENTAL ("G91",Group.DISTANCE),
    
 // feed rate modes: {G93, G94}
    STROKES_PER_MIN ("G93", Group.RATE_MODE),
    FEED_PER_MIN ("G94", Group.RATE_MODE),

 // units modes: {G20, G21}
    INCHES ("G20", Group.UNITS),
    MILLIMETERS ("G21", Group.UNITS),
    
 // cutter radius compensation: {G40, G41, G42}
    CUTTER_COMP_OFF ("G40", Group.CUTTER_COMP),
    CUTTER_COMP_LEFT ("G41", Group.CUTTER_COMP),
    CUTTER_COMP_RIGHT ("G42", Group.CUTTER_COMP),
    
 // tool length offsets modes: {G43, G44, G49}
    TOOL_LENGTH_NEG ("G43", Group.TOOL_LENGTH),
    TOOL_LENGTH_POS ("G44", Group.TOOL_LENGTH),  
    TOOL_LENGTH_OFF ("G49", Group.TOOL_LENGTH),  
    
    // work coordinate system modes: {G54, G55, G56, G57, G58, G59}
    WCS_G54 ("G54", Group.WORK_COORDINATES),
    WCS_G55 ("G55", Group.WORK_COORDINATES),
    WCS_G56 ("G56", Group.WORK_COORDINATES),
    WCS_G57 ("G57", Group.WORK_COORDINATES),
    WCS_G58 ("G58", Group.WORK_COORDINATES),
    WCS_G59 ("G59", Group.WORK_COORDINATES),
    
 // spindle turning modes: {M3, M4, M5}
    SPINDLE_CW ("M3", Group.SPINDLE),
    SPINDLE_CCW ("M4", Group.SPINDLE),
    SPINDLE_OFF ("M5", Group.SPINDLE),
    
 // coolant modes: {M7, M8, M9}
    COOLANT_MIST ("M7", Group.COOLANT),
    COOLANT_FLOOD ("M8", Group.COOLANT),
    COOLANT_OFF ("M9", Group.COOLANT),
    
 // stopping modes: {M0, M1, M2, M30, M60}
    STOP ("M0", Group.STOPPING),
    OPTIONAL_STOP ("M1", Group.STOPPING),
    END ("M2", Group.STOPPING),
    END_RETURN ("M30", Group.STOPPING),
    PALLET_CHANGE ("M60", Group.STOPPING);

    private final String code;
    private final Group group;

    /* 
     * Private constructor only used by the enum definitions themselves.
     * 
     * @param code
     *            The full G code word that the enum represents
     * @param group
     *            The modal group that this G code belongs to
     */
    private Mode(String code , Group group)
    {
        this.code = code;
        this.group = group;
    }

    /* Getters */
    public String code() { return code; }
    public Group group() { return group; }
    
    
    /**
     * Utility method to find and return the enum value for a specific G code word.
     * Returns null if not found.
     * 
     * @param code
     *            the code word to search for
     * @return the matching enum or null if not found
     */
    public static Mode find(String code)
    {
        for (Mode m : Mode.values())
        {
            if(m.code().equals(code)) return m;
        }
        return null;
    }

}