package cncleveler;

import java.util.EnumMap;

/**
 * Holds a representation of the settings defined by a line (block) of G code.
 * 
 */
public class State
{
    /**
     * Storage for the current state of each mode group. The current state of each group is defined
     * by a Mode enum value. Null means that the group's state has not been set.
     * 
     * Groups here roughly match the definitions in "The NIST RS274NGC Interpreter - Version 3" by
     * Thomas R. Kramer, Frederick M. Proctor, Elena Messina (August 17, 2000)
     */
    private final EnumMap<Group, Mode> stateMap = new EnumMap<>(Group.class);

    /**
     * Storage for the current state of each axis and parameter. The current state of each is
     * defined by a Double value. Null means that the Axis or paramter's state has not been set.
     */
    private final EnumMap<Axis, Double> axisMap = new EnumMap<>(Axis.class);

    /**
     * Storage for the comment string. Assumption is there will only be one per line (block). Null
     * means the comment has not been set.
     */
    private String comment = null;

    /**
     * Physical line number of the source file that this state represents. This is not the block
     * number defined by the N code word. This is used to facilitate debugging.
     */
    public int lineNum = 0;

    /**
     * The original line (block) from the source file. This is used to facilitate debugging.
     */
    public String originalText = "";

    /**
     * Constructor
     */
    public State()
    {

    }

    /**
     * Sets a group to a specified mode. For example, set the units group to either inches (G20) or
     * mm (G21). The group identification is taken from the Mode enum as each code word only belongs
     * to one group.
     * 
     * @param mode The mode to set.
     */
    public void setMode(Mode mode)
    {
        stateMap.put(mode.group(), mode);
    }

    /**
     * Sets a group to a specified mode. For example, set the units group to either inches (G20) or
     * mm (G21). The group identification is taken from the Mode enum as each code word only belongs
     * to one group.
     * 
     * @param mode The mode to set.
     */
    public void setGroup(Group group, Mode mode)
    {
        stateMap.put(group, mode);
    }
    
    /**
     * Returns the current state of a mode group.
     * 
     * @param group the specific group to return.
     * @return the current setting of the or null is it has not been set
     */
    public Mode getGroup(Group group)
    {
        return stateMap.get(group);
    }

    
    /**
     * Sets an axis or parameter to the specified value.
     * 
     * @param axis axis or parameter to set
     * @param value the value use
     */
    public void setAxis(Axis axis, Double value)
    {
        axisMap.put(axis, value);
    }

    /**
     * Returns the current state of an axis or parameter.
     * 
     * @param axis the specific axis or parameter to return.
     * @return the current setting of the or null is it has not been set
     */
    public Double getAxis(Axis axis)
    {
        return axisMap.get(axis);
    }

    /**
     * Sets the text string for the comment field. Assumption is that there is only one comment per
     * line (block).
     * 
     * @param comment the comment text to store
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * Gets the text string for the comment field. 
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * Updated this state with all modal state values in the other state that have been set.
     * Non-modal Axis values are not updated.
     * 
     * @param other the other state to use to update this state
     */
    public void mergeWith(State other)
    {
        for (Group g : Group.values())
        {
            Mode m = other.getGroup(g);
            if (m != null) stateMap.put(m.group(), m);
        }

        for (Axis a : Axis.values())
        {
            if (a.modal())
            {
                Double d = other.getAxis(a);
                if (d != null) axisMap.put(a, d);
            }
        }
    }

    /**
     * Returns a formated string of G codes that represent this state. Unset group or axis values
     * are not included.
     * 
     * For example, if the units group has been set to inches, G20 is included. If unset, nothing is
     * included for the units group.
     * 
     * @return this state as a formated G code string
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for (Group g : Group.values())
        {
            Mode m = stateMap.get(g);
            if (m != null)
            {
                if (sb.length() > 0) sb.append(' ');
                sb.append(m.code());
            }
        }

        for (Axis a : Axis.values())
        {
            Double d = axisMap.get(a);
            if (d != null)
            {
                if (sb.length() > 0) sb.append(' ');
                sb.append(format(a.letter(), d));
            }
        }

        if (comment != null)
        {
            if (sb.length() > 0) sb.append(' ');
            sb.append(comment);
        }

        return sb.toString();
    }

    /**
     * Utility method to take the letter and value and generate a formated G code word. The result
     * is suitable to output to a G code file.
     * 
     * @return the formated code word
     */
    public static String format(char letter, double value)
    {
        String s = String.format("%c%.3f", letter, value);

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

    /**
     * Debug utility function to print the current settings of this state in human readable form.
     * Unset items are included.
     */
    public void print()
    {
        System.out.println("-----------------");

        for (Group g : Group.values())
        {
            System.out.print(g.name() + " = ");
            Mode m = stateMap.get(g);
            if (m != null)
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
                System.out.println(String.format("%3f", d));
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
