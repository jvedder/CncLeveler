package cncleveler;

public class State
{
    /**
     * Block number
     */
    public Mode n = new Mode();
    
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
    public Mode x = new Mode();
    public Mode y = new Mode();
    public Mode z = new Mode();

    public Mode f = new Mode();
    public Mode s = new Mode();

    /**
     * Non-persistent positional values
     */
    public Mode i = new Mode();
    public Mode j = new Mode();
    public Mode k = new Mode();

    public Mode r = new Mode();

    /**
     * Comments
     */
    public Mode comment = new Mode();

    /**
     * Constructor
     */
    public State()
    {

    }

    public void mergeWith(State other)
    {
        n.mergeWith(other.n);
        
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
        StringBuilder sb = new StringBuilder();

        sb.append(n);

        sb.append(spindle);
        sb.append(s);

        sb.append(coolant);

        sb.append(plane);
        sb.append(distance);
        sb.append(feedRate);
        sb.append(units);
        sb.append(cutterComp);
        sb.append(toolLength);
        sb.append(workCoordSystem);

        sb.append(motion);

        sb.append(f);
        sb.append(x);
        sb.append(y);
        sb.append(z);

        sb.append(i);
        sb.append(j);
        sb.append(k);
        sb.append(r);

        sb.append(programFlow);

        sb.append(comment);

        return sb.toString();
    }

    public void print()
    {
        System.out.println("-----------------");
        System.out.println("n:" + n.toString());

        System.out.println("spindle:" + spindle.toString());
        System.out.println("coolant:" + coolant.toString());

        System.out.println("plane:" + plane.toString());
        System.out.println("distance:" + distance.toString());
        System.out.println("feedRate:" + feedRate.toString());
        System.out.println("units:" + units.toString());
        System.out.println("cutterComp:" + cutterComp.toString());
        System.out.println("toolLength:" + toolLength.toString());
        System.out.println("workCoordSystem:" + workCoordSystem.toString());

        System.out.println("x:" + x.toString());
        System.out.println("y:" + y.toString());
        System.out.println("z:" + z.toString());
        System.out.println("f:" + f.toString());
        System.out.println("s:" + s.toString());

        System.out.println("i:" + i.toString());
        System.out.println("j:" + j.toString());
        System.out.println("k:" + k.toString());
        System.out.println("r:" + r.toString());

        System.out.println("comment:" + comment.toString());
        System.out.println("-----------------");
    }
}
