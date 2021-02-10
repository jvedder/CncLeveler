package cncleveler;

import java.util.EnumMap;

public class State
{
    private final EnumMap<Group, Mode> stateMap = new EnumMap<>(Group.class);

    private final EnumMap<Axis, Double> axisMap = new EnumMap<>(Axis.class);
    
    private String comment = null;;
       
    /**
     * Constructor
     */
    public State()
    {

    }
    
    public void set(Mode mode)
    {
        if(stateMap.get(mode.group()) != null)
        {
            throw new IllegalArgumentException("Attempt to set a Mode Group that is already set.");
        }
        stateMap.put(mode.group(), mode);
    }
    
    public void set(Axis axis, Double value)
    {
        if(axisMap.get(axis) != null)
        {
            throw new IllegalArgumentException("Attempt to set a Axis that is already set.");
        }
        axisMap.put(axis, value);
    }
    
    public void set(String comment)
    {
        if(this.comment != null)
        {
            throw new IllegalArgumentException("Attempt to set a Comment that is already set.");
        }
        this.comment = comment;
    }

    public Mode get(Group group)
    {
        return stateMap.get(group);
    }
    
    public Double get(Axis axis)
    {
        return axisMap.get(axis);
    }

    public void mergeWith(State other)
    {
        for(Group g : Group.values())
        {
            Mode m = other.get(g);
            if(m != null) stateMap.put(m.group(), m);
        }
        
        for (Axis a : Axis.values())
        {
            Double d = other.get(a);
            if (d != null) axisMap.put(a, d);
        }
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for(Group g : Group.values())
        {
            Mode m = stateMap.get(g);
            if( m != null) sb.append(m.code());
        }

        for (Axis a : Axis.values())
        {
            Double d = axisMap.get(a);
            if (d != null) 
            {
                sb.append(a.code());
                sb.append(format(d));
            }
        }

        if (comment != null)
        {
            sb.append(comment);
        }

        return sb.toString();
    }
    
    private String format(double value)
    {
        String s = String.format("%.3f", value);
        
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
        return s;
    }

    public void print()
    {
        System.out.println("-----------------");
       
        for(Group g : Group.values())
        {
            System.out.print(g.name() + " = ");
            Mode m = stateMap.get(g);
            if( m != null)
            {
                System.out.println(m.name() + " (" + m.code() + ")");
            }
            else
            {
                System.out.println("[Not Set]");
            }
            
        }
        
        for (Axis a : Axis.values())
        {
            System.out.print(a.name() + " = ");
            Double d = axisMap.get(a);
            if (d != null) 
            {
                System.out.println(format(d));
            }
            else
            {
                System.out.println("[Not Set]");
            }
        }
        
        System.out.print("comment = ");
        if (comment != null)
        {
            System.out.println(comment);
        }
        else
        {
            System.out.println("[Not Set]");
        }
        
        System.out.println("-----------------");
    }
}
