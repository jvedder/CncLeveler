package cncleveler;

import java.util.Set;

public class CodeWord
{

    /**
     * The single character code (G, M X, etc.) that defines this code word.
     */
    char code = '*';

    /**
     * The numeric portion of this code word
     */
    double value = 0;

    /**
     * The integer portion of this gcode as a convenience
     */
    int intValue = 0;

    /**
     * The text if this word is a comment
     */
    String text = null;

    /**
     * Constructor
     * 
     * @param codeLetter the letter that defines this code word or '(' 
     */
    public CodeWord(char codeLetter)
    {
        code = codeLetter;
    }

    /**
     * Sets the numberic value of this code word
     * @param value
     */
    public void setValue(double value)
    {
        this.value = value;
        this.intValue = (int) Math.floor(value);
    }

    /**
     * Sets the comment text for this code word
     * @param text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Returns this code word as valid G code text 
     */
    public String toString()
    {
        String s;
        if (code == '(')
        {
            s = "(" + text + ")";
        }
        else
        {
            s = String.format("%c%.3f", code, value);
            
            // remove any trailing zeros
            while (s.endsWith("0"))
            {
                s = s.substring(0, s.length() - 1);
            }
            
            // remove any trailing decimal point
            if (s.endsWith("."))
            {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
}
