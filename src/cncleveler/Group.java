package cncleveler;

public enum Group
{
    /**
     * Values for modal G-Codes.
     */
    // Motion modes: {G0, G1, G2, G3}
    MOTION,

    // plane modes: {G17, G18, G19}
    PLANE,

    // distance modes: {G90, G91}
    DISTANCE,

    // feed rate modes: {G93, G94}
    RATE_MODE,

    // units modes: {G20, G21}
    UNITS,

    // cutter radius compensation: {G40, G41, G42}
    CUTTER_COMP,

    // tool length offsets modes: {G43, G44, G49}
    TOOL_LENGTH,

    // work coordinate system modes: {G54, G55, G56, G57, G58, G59}
    WORK_COORDINATES,

    // spindle turning modes: {M3, M4, M5}
    SPINDLE,

    // coolant modes: {M7, M8, M9}
    COOLANT,

    // stopping modes: {M0, M1, M2, M30, M60}
    STOPPING

}
