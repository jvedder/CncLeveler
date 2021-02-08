package cncleveler;

import java.util.ArrayList;

public class Block extends ArrayList<CodeWord>
{

    public int lineNum = 0;
    
    public String originalText = "";
    
    public Block()
    {
        super();
    }
}
