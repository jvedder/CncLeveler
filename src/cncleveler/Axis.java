package cncleveler;

public enum Axis
{
    // Line Number
    LINE_NUM('N'),
    
    // Feed and Speed
    FEED('F'),
    SPEED('S'),
    
    //Cartesian Axes
    X('X'),
    Y('Y'),
    Z('Z'),
    
    // Arc Center Offset
    I('I'),
    J('J'),
    K('K'),
    
    // Radius
    RADIUS('R'),
    
    // Loop Count or G10 register number 
    LOOP('L'),
    
    // Parameter address 
    PARAM('P'),

    // Tool Selection
    TOOL('T');
    
    private final char code;

    private Axis(char code)
    {
        this.code = code;
    }

    public char code() { return code; }
    
    public static Axis find(char code)
    {
        for (Axis a : Axis.values())
        {
            if(a.code() == code) return a;
        }
        return null;
    }

}
