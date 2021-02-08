package cncleveler;

public class Mode
{
    private String value = null;

    private boolean isSet = false;

    public Mode()
    {
        value = null;
        isSet = false;
    }

    public void set(String value)
    {
        if (isSet)
        {
            throw new IllegalArgumentException("Attempt to set a Mode that is already set.");
        }
        
        this.value = value;
        isSet=true;
    }
    
    public void mergeWith(Mode other)
    {
        if (other.isSet())
        {
            this.value = other.getValue();
            this.isSet = true;
        }
    }

   
    public String getValue()
    {
        if(isSet)
        {
            return value;
        }
        else
        {
            return "";
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
            return value;
        }
        else
        {
            return "";
        } 
    }

}
