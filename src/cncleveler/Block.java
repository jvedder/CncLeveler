package cncleveler;

import java.util.ArrayList;

public class Block extends ArrayList<CodeWord>
{

    private static final long serialVersionUID = -9170431150694910573L;

    public int lineNum = 0;
    
    public String originalText = "";
    
    public Block()
    {
        super();
    }
    
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (CodeWord word : this)
        {
            sb.append(word);
        }
        return sb.toString();
    }
}
