package cncleveler;

public class Position
{
    private double value = Double.NaN;

    private boolean isSet = false;

    public Position()
    {
        value = Double.NaN;
        isSet = false;
    }

    public void set(double value)
    {
        if (isSet)
        {
            throw new IllegalArgumentException("Attempt to set a Position that is already set.");
        }
        
        this.value = value;
        isSet=true;
    }
    
    public void mergeWith(Position other)
    {
        if (other.isSet())
        {
            this.value = other.getValue();
            this.isSet = true;
        }
    }

   
    public double getValue()
    {
        if(isSet)
        {
            return value;
        }
        else
        {
            return 0.0d;
        }
    }
    

    public boolean isSet()
    {
        return isSet;
    }

}
