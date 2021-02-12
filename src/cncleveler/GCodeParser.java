package cncleveler;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class GCodeParser
{
    /**
     * Logger for reporting status
     */
    protected static Logger logger = Logger.getLogger((Main.class.getName()));

    /**
     * List of characters allowed in a number
     */
    private static final Set<Character> NUMBER_CHARS = Set.of('+', '-', '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', '.');

    /**
     * List of valid G code state letters. These correspond to letters in the
     * Mode enum.
     * 
     * <pre>
     * G = General Command 
     * M = Miscellaneous function
     * </pre>
     */
    public static final Set<Character> MODE_LETTERS = Set.of('G', 'M');

    /**
     * List of valid G code axis letters. These correspond to letters in the
     * Axis enum, which includes both axes and parameters.
     * 
     * <pre>
     * N = Line Number or G10 parameter number 
     * F = Feed Rate 
     * S = (Spindle) Speed
     * X,Y,Z = Axis Position 
     * I,J,K = Arc Center 
     * R = Radius 
     * L = Loop Count or G10 register number 
     * P = Parameter address 
     * T = Tool Selection
     * </pre>
     */
    public static final Set<Character> AXIS_LETTERS = Set.of('N', 'F', 'S', 'X', 'Y', 'Z', 'I', 'J', 'K', 'R', 'L', 'P',
            'T');

    /**
     * Character buffer to hold the current line being parsed.
     */
    protected char[] buffer;

    /**
     * Index into the character buffer.
     */
    protected int index;

    /**
     * Current line number in the file. Used as a debug aid.
     */
    protected int lineNum;

    /**
     * Initial letter of the current code word being parsed.
     */
    char letter = '*';

    /**
     * The numerical portion of the current code word being parsed.
     */
    double value = 0.0D;

    /**
     * The integer portion of the number in the current code word being parsed.
     */
    int intValue = 0;

    /**
     * Reads and parses a G code file.
     * 
     * @param filename
     *            The filename (and path) to the G code file
     * @return An ordered list of states that contain the parsed information
     *         from the G code file.
     * @throws IOException
     *             on any I/O error
     */
    public List<State> read(String filename) throws IOException
    {
        // Show progress
        logger.info("Opening: " + filename);

        // Open input file for reading
        Path inFile = Paths.get(filename);

        List<State> states = new ArrayList<State>();

        // BufferedReader supports readLine()
        BufferedReader in = Files.newBufferedReader(inFile, StandardCharsets.UTF_8);
        lineNum = 0;
        String line;
        while ((line = in.readLine()) != null)
        {
            lineNum++;

            // Create a new State for this line
            State state = new State();
            state.lineNum = lineNum;
            state.originalText = line;
            states.add(state);

            // Get a character buffer for parsing the line
            buffer = line.trim().toUpperCase().toCharArray();
            index = 0;

            while (index < buffer.length)
            {
                if (MODE_LETTERS.contains(buffer[index]))
                {
                    parseValue();
                    String code = State.format(letter, value);
                    Mode mode = Mode.find(code);
                    if (mode == null)
                    {
                        String msg = position() + "Unrecognized code word: " + code;
                        logger.severe(msg);
                        throw new RuntimeException(msg);
                    }
                    state.set(mode);
                }
                else if (AXIS_LETTERS.contains(buffer[index]))
                {
                    parseValue();
                    Axis axis = Axis.find(letter);
                    if (axis == null)
                    {
                        String msg = position() + "Unrecognized code word: " + State.format(letter, value);
                        logger.severe(msg);
                        throw new RuntimeException(msg);
                    }
                    state.set(axis, value);
                }
                else if (buffer[index] == '(')
                {
                    // skip over (COMMENTS)
                    state.set(parseComment());
                }
                else if (buffer[index] <= ' ')
                {
                    // skip over white space
                    index++;
                }
                else if ((buffer[index] == '%'))
                {
                    // skip over %
                    index++;
                }
                else
                {
                    String msg = position() + "Unrecognized code letter: " + buffer[index];
                    logger.severe(msg);
                    throw new RuntimeException(msg);
                }
            }

        }
        in.close();
        logger.info("Parsed " + lineNum + " lines.");
        return states;
    }

    /**
     * Parses the value portion of a G-Code Word and sets the class-level
     * letter, value and intValue fields as a return value.
     */
    protected void parseValue()
    {
        letter = buffer[index];
        index++;

        if (endOfLine()) 
        {
            String msg = position() + " Reached end of line; expected letter";
            logger.severe(msg);
            throw new RuntimeException(msg);
        }

        // Assume white space allowed before number
        skipWhitespace();
        if (endOfLine())
        {
            String msg = position() + " Reached end of line; expected number";
            logger.severe(msg);
            throw new RuntimeException(msg);
        }
        // Put all number characters into a string
        StringBuilder number = new StringBuilder();
        while (!endOfLine() && NUMBER_CHARS.contains(buffer[index]))
        {
            number.append(buffer[index]);
            index++;
        }
        // Parse the string into a double
        value = Double.parseDouble(number.toString());
        intValue = (int) Math.floor(value);
    }

//    /**
//     * Utility method to take the letter and value and generate a formated
//     * G code word. The result is suitable for outputing to a G code file.
//     * 
//     * @return the formated code word
//     */
//    protected String formatCode()
//    {
//        String s = String.format("%c%.3f", letter, value);
//
//        // remove any trailing zeros
//        while (s.endsWith("0"))
//        {
//            s = s.substring(0, s.length() - 1);
//        }
//
//        // remove any trailing decimal point
//        if (s.endsWith("."))
//        {
//            s = s.substring(0, s.length() - 1);
//        }
//        return s;
//    }

    /**
     * Utility method to check if the index is at or past the end of the line
     * buffer. This is implemented as a method for code readability.
     * 
     * @return True is at or past the end of the line buffer. False otherwise,
     */
    protected boolean endOfLine()
    {
        return index >= buffer.length;
    }

    /**
     * Utility method to skip over white space in the line. Any character less
     * than a space (' ') is considered white space. That includes tab, CR, and
     * LF.
     */
    protected void skipWhitespace()
    {
        while (!endOfLine() && (buffer[index] <= ' '))
            index++;
    }

    /**
     * Parses a comment field and returns it as a string.
     * 
     */
    protected String parseComment()
    {
        StringBuilder text = new StringBuilder();

        while (!endOfLine() && (buffer[index] != ')'))
        {
            text.append(buffer[index]);
            index++;
        }
        if (endOfLine())
        {
            String msg = position() + " Reached end of line inside comment";
            logger.severe(msg);
            throw new RuntimeException(msg);
        }
        text.append(buffer[index]);
        index++;

        return text.toString();
    }

    /**
     * Utility method to return the current G code file character position as
     * [line,char] used for reporting in Exceptions.
     * 
     * @return String with the current character position
     */
    protected String position()
    {
        return "[Line: " + lineNum + ", Char: " + index + "] ";
    }

}
