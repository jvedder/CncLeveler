package cncleveler;

/**
 * Defines labels for each supported axis or parameter. Also flags if the value
 * is modal -- true if the value carries forward to the next G code line (block).
 */
public enum Axis
{
    // Line Number
    LINE_NUM('N', true),
    
    // Feed and Speed
    FEED('F', true),
    SPEED('S', true),
    
    //Cartesian Axes
    X('X', true),
    Y('Y', true),
    Z('Z', true),
    
    // Arc Center Offset
    I('I', false),
    J('J', false),
    K('K', false),
    
    // Radius
    RADIUS('R', false),
    
    // Loop Count or G10 register number 
    LOOP('L', false),
    
    // Parameter address 
    PARAM('P', false),

    // Tool Selection
    TOOL('T', false);
    

    private final char letter;
    private final boolean modal;

    /* 
     * Private constructor only used by the enum definitions themselves.
     * 
     * @param letter
     *            The G code letter that this enum represents
     * @param modal
     *            True if this G code is modal
     */
    private Axis(char letter, boolean modal)
    {
        this.letter = letter;
        this.modal = modal;
    }

    /* Getters */
    public char letter() { return letter; }
    public boolean modal() { return modal; }
    
    
    /**
     * Utility method to find and return the enum value for a specific letter.
     * Returns null if not found.
     * 
     * @param letter
     *            the letter to search for
     * @return the matching enum or null if not found
     */
    public static Axis find(char letter)
    {
        for (Axis a : Axis.values())
        {
            if(a.letter() == letter) return a;
        }
        return null;
    }

}
