package cncleveler;

public class Mode
{
    private CodeWord word = null;

    private boolean isSet = false;

    public Mode()
    {
        word = null;
        isSet = false;
    }

    public void set(CodeWord value)
    {
        if (isSet)
        {
            throw new IllegalArgumentException("Attempt to set a Mode that is already set.");
        }
        
        this.word = value;
        isSet=true;
    }
    
    public void mergeWith(Mode other)
    {
        if (other.isSet())
        {
            this.word = other.getWord();
            this.isSet = true;
        }
    }

   
    public CodeWord getWord()
    {
        if(isSet)
        {
            return word;
        }
        else
        {
            return null;
        }
    }
    

    public boolean isSet()
    {
        return isSet;
    }
    
    public String toString()
    {
        if(isSet)
        {
            return word.toString();
        }
        else
        {
            return "";
        } 
    }

}
