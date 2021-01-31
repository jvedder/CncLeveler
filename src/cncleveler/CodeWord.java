package cncleveler;

import java.util.Set;

public class CodeWord
{
    /**
     * The code word 
     * G = General Command
     * L = Loop Count or G10 register number
     * M = Miscellaneous function
     * N = Line Number or G10 parameter number
     * P = Parameter address
     * T = Tool Selection
     */
    public static final Set<Character> COMMANDS = Set.of('G','L','M','N','P','T');
    
    /**
     * X,Y,Z = Axis Position
     * I,J,K = Arc Center
     * F = Feed Rate
     * R = Radius
     * S = (Spindle) Speed     
     */
    public static final Set<Character> VALUES = Set.of('X','Y','Z','I','J','K','F','R','S');
    
    /** 
     * The single character code (G, M X, etc.) that defines this code word.
     */
    char code = '*';

    /**
     * Either the value of this code word (1 for G01) or the coordinate value in microns for positional code words (X,Y,Z, etc).
     */
    int value = 0;
 
    /** 
     * The sub-code for some code words. For example, the 1 in G28.1.  Zero for positional code words (X,Y,Z, etc).
     */
    int fraction = 0;
    
    /**
     * Constructor with only the  
     * @param c
     */
    public CodeWord(char codeLetter)
    {
        code = codeLetter;
    }
    
    public void setValue(int val)
    {
        value = val;
    }
 
    public void setFraction(int frac)
    {
        fraction = frac;
    }
    
    public double toDouble() 
    {
        if (value < 0)
        {
            return (double)value - (double)fraction / 1000.0D;
        }
        else
        {
            return (double)value + (double)fraction / 1000.0D;
        }
    }
 
    public String toString()
    {
        if (fraction != 0)
        {
            String s = String.format("%c%d.%03d", code, value, fraction);
            while (s.endsWith("0"))
            {
                s = s.substring(0,s.length()-1);
            }
            return s;
        }
        else
        {
            return String.format("%c%d", code, value);
        }
    }

}
