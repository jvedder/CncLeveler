package cncleveler;

import java.util.List;

public class GCodeParser
{

    public void parse(List<Block> blocks)
    {
        State globalState = new State();

        for (Block block : blocks)
        {
            State state = new State();

            

            for (CodeWord word : block)
            {
                switch (word.code)
                {
                    case 'N':
                        state.n.set(word);
                        break;

                    case 'G':
                        switch (word.intValue)
                        {
                            case 0: // Rapid positioning
                            case 1: // Linear interpolation
                            case 2: // Circular interpolation, clockwise
                            case 3: // Circular interpolation, counter-clockwise
                                state.motion.set(word);
                                break;
                            case 17: // XY plane selection
                            case 18: // ZX plane selection
                            case 19: // YZ plane selection
                                state.plane.set(word);
                                break;
                            case 90: // Absolute programming
                            case 91: // Incremental programming
                                state.distance.set(word);
                                break;
                            case 93: // Strokes per minute
                            case 94: // Feed rate per minute
                                state.feedRate.set(word);
                                break;
                            case 20: // Programming in inches
                            case 21: // Programming in millimeters
                                state.units.set(word);
                                break;
                            case 40: // tool length cancel
                                state.cutterComp.set(word);
                                break;
                            case 41: // cutter radius compensation left
                            case 42: // cutter radius compensation left
                                throw new RuntimeException(
                                        word.getPosition() + "Cutter Compensation {G41,G42} not supported");
                            case 43: // Tool height offset compensation negative
                            case 44: // Tool height offset compensation positive
                                throw new RuntimeException(
                                        word.getPosition() + "Cutter tool height compensation {G43,G44} not supported");
                            case 49: // tool length cancel
                                state.toolLength.set(word);
                                break;
                            case 98: // Return to initial Z level in canned
                                     // cycle
                            case 99: // Return to R level in canned cycle
                                throw new RuntimeException(
                                        word.getPosition() + "Return mode in canned cycles {G98,G99} not supported.");
                            case 54: // Work coordinate system 1
                            case 55: // Work coordinate system 2
                            case 56: // Work coordinate system 3
                            case 57: // Work coordinate system 4
                            case 58: // Work coordinate system 5
                            case 59: // Work coordinate system 6
                                state.workCoordSystem.set(word);
                                break;
                            case 61: // path control mode
                            case 64: // path control mode
                                throw new RuntimeException(
                                        word.getPosition() + "Path control mode {G61, G61.1, G64} not supported.");
                            default:
                                throw new RuntimeException(
                                        word.getPosition() + "Unrecognized G code: " + word.toString());
                        }
                        break;

                    case 'M':
                        switch (word.intValue)
                        {
                            case 0: // Compulsory stop
                            case 1: // Optional stop
                            case 2: // End of Program
                            case 30: // End of program, with return to program
                                     // top
                                state.programFlow.set(word);
                                break;
                            case 60: // Automatic pallet change
                                throw new RuntimeException(
                                        word.getPosition() + "Automatic pallet change {M60} not supported.");
                            case 3:
                            case 4:
                            case 5:
                                state.spindle.set(word);
                                break;
                            case 6:
                                throw new RuntimeException(word.getPosition() + "Tool change {M6} not supported.");
                            case 7:
                            case 8:
                            case 9:
                                state.coolant.set(word);
                                break;
                            default:
                                throw new RuntimeException(
                                        word.getPosition() + "Unrecognized M code: " + word.toString());
                        }
                        break;
                    case 'X':
                        state.x.set(word);
                        break;
                    case 'Y':
                        state.y.set(word);
                        break;
                    case 'Z':
                        state.z.set(word);
                        break;
                    case 'F':
                        state.f.set(word);
                        break;
                    case 'S':
                        state.s.set(word);
                        break;

                    case 'I':
                        state.i.set(word);
                        break;
                    case 'J':
                        state.j.set(word);
                        break;
                    case 'K':
                        state.k.set(word);
                        break;
                    case 'R':
                        state.r.set(word);
                        break;
                    case '(':
                        state.comment.set(word);
                        break;
                    default:
                        throw new RuntimeException(
                                word.getPosition() + "Unrecognized code word: " + word.toString());
                }

            }
            globalState.mergeWith(state);
            
            System.out.println("----------------------------------");
            System.out.println("Orig:   " + block.originalText);
            System.out.println("Block:  " + block.toString());
            System.out.println("State:  " + state.toString());
            System.out.println("Global: " + globalState.toString());

        }
    }
}
