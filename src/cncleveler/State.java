package cncleveler;

import java.util.ArrayList;
import java.util.List;

public class State
{   
    /**
     * Values for modal G-Codes.  
     */    
    // Motion modes: {G0, G1, G2, G3}
    public Mode motion = new Mode();
    
    // plane modes: {G17, G18, G19}
    public Mode plane = new Mode();
    
    // distance modes: {G90, G91}
    public Mode distance = new Mode();

    // feed rate modes: {G93, G94}
    public Mode feedRate = new Mode();
    
    // units modes: {G20, G21}
    public Mode units = new Mode();
    
    // cutter radius compensation: {G40, G41, G42}
    public Mode cutterComp = new Mode();
    
    // tool length offsets modes: {G43, G49}
    public Mode toolLength = new Mode();
    
    // work coordinate system modes: {G54, G55, G56, G57, G58, G59}
    public Mode workCoordSystem = new Mode();   
    
    // spindle turning modes: {M3, M4, M5}
    public Mode spindle = new Mode();
    
    // coolant modes: {M7, M8, M9}
    public Mode coolant = new Mode();
    
    // stopping modes: {M0, M1, M2, M30, M60}
    public Mode programFlow = new Mode();

    /**
     * Persistent positional values
     */
    public Position x = new Position();
    public Position y = new Position();
    public Position z = new Position();
    
    public Position f = new Position();
    public Position s = new Position();
    
    /**
     * Non-persistent values
     */
    public Position i = new Position();
    public Position j = new Position();
    public Position k = new Position();
    
    public Position r = new Position();
    
    /**
     * Block number
     */
    public Mode n = new Mode();
    
    public final List<CodeWord> otherCodeWords = new ArrayList<CodeWord>();
    
    /** 
     * Constructor
     */
    public State()
    {
        
    }
    
    public void add(CodeWord word)
    {
        otherCodeWords.add(word);
    }
    
    public void mergeWith(State other)
    {
        motion.mergeWith(other.motion);
        plane.mergeWith(other.plane);
        distance.mergeWith(other.distance);
        feedRate.mergeWith(other.feedRate);
        units.mergeWith(other.units);
        cutterComp.mergeWith(other.cutterComp);
        toolLength.mergeWith(other.toolLength);
        workCoordSystem.mergeWith(other.workCoordSystem);
        
        spindle.mergeWith(other.spindle);
        coolant.mergeWith(other.coolant);
        programFlow.mergeWith(other.programFlow);

        x.mergeWith(other.x);
        y.mergeWith(other.y);
        z.mergeWith(other.z);

        f.mergeWith(other.f);
        s.mergeWith(other.s);
        
    }
    
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        
        s.append(n);
        
        s.append(spindle);
        s.append(coolant);
        
        s.append(plane);
        s.append(distance);
        s.append(feedRate);
        s.append(units);
        s.append(cutterComp);
        s.append(toolLength);
        s.append(workCoordSystem);
        
        s.append(x);
        s.append(y);
        s.append(z);
        s.append(f);
        s.append(s);
        
        s.append(i);
        s.append(j);
        s.append(k);
        s.append(r);
        
        for(CodeWord w : otherCodeWords)
        {
            s.append(w);
        }

        return s.toString();
    }
}
