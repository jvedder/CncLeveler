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
                    case '(':
                        state.set(word.toString());
                        break;
                        
                    case 'G':
                    case 'M':
                        Mode mode = Mode.find(word.toString());
                        if (mode == null)
                        {
                            throw new RuntimeException(
                                    word.getPosition() + "Unrecognized code word: " + word.toString());
                        }
                        state.set(mode);
                        break;
                    case 'N':
                    case 'X':
                    case 'Y':
                    case 'Z':
                    case 'F':
                    case 'S':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'R':
                        Axis axis = Axis.find(word.code);
                        if (axis == null)
                        {
                            throw new RuntimeException(
                                    word.getPosition() + "Unrecognized code word: " + word.toString());
                        }
                        state.set(axis, word.value);
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
            globalState.print();

        }
    }
}
