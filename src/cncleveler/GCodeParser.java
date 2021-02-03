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
    protected static Logger logger = Logger.getLogger((Main.class.getName()));

    protected static final boolean DEBUG = true;

    private static final Set<Character> NUMBER_CHARS = Set.of('+', '-', '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', '.');

    /**
     * List of valid G code letters 
     * 
     * <pre>
     * G = General Command 
     * L = Loop Count or G10 register number 
     * M = Miscellaneous function 
     * N = Line Number or G10 parameter number 
     * P = Parameter address 
     * T = Tool Selection
     * X,Y,Z = Axis Position 
     * I,J,K = Arc Center 
     * F = Feed Rate 
     * R = Radius 
     * S = (Spindle) Speed
     * </pre>
     */
    public static final Set<Character> COMMANDS = Set.of('G', 'L', 'M', 'N', 'P', 'T', 'X', 'Y', 'Z', 'I', 'J', 'K',
            'F', 'R', 'S');

    
    /**
     * Character buffer to hold the current line being parsed.
     */
    protected char[] buffer;

    
    /**
     * Index into the character buffer.
     */
    protected int index;
    
    
    /**
     * Current line number in the file. Printed on parse exceptions.
     */
    protected int lineNum;

    
    /**
     * Reads and parses a G code file. 
     * @param filename The filename (and path) to the G code file
     * @throws IOException on an I/O error
     */
    public void read(String filename) throws IOException
    {
        // Show progress
        logger.info("Opening: " + filename);

        // Open input file for reading
        Path inFile = Paths.get(filename);

        List<CodeWord> block = new ArrayList<>();

        // BufferedReader supports readLine()
        BufferedReader in = Files.newBufferedReader(inFile, StandardCharsets.UTF_8);
        lineNum = 0;
        String line;
        while ((line = in.readLine()) != null)
        {
            lineNum++;
            buffer = line.trim().toUpperCase().toCharArray();
            index = 0;
            block.clear();

            while (index < buffer.length)
            {
                if (COMMANDS.contains(buffer[index]))
                {
                    block.add(parseWord());
                }
                else if (buffer[index] == '(')
                {
                    // skip over (COMMENTS)
                    block.add(parseComment());
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
                    throw new RuntimeException(position() + "Unrecognized code letter: " + buffer[index]);
                }
            }

            for (CodeWord word : block)
            {
                System.out.print(word.toString());
            }
            System.out.println();
        }

    }

    
    /**
     * Parses a standard G code word: a letter followed by a number and returns
     * a CodeWord.
     * 
     * @return a CodeWord with the parsed G Code word
     */
    protected CodeWord parseWord()
    {
        if (endOfLine()) throw new RuntimeException(position() + " Reached end of line; expected letter");

        CodeWord word = new CodeWord(buffer[index]);
        index++;

        // White space allowed before number
        skipWhitespace();
        if (endOfLine()) throw new RuntimeException(position() + " Reached end of line; expected number");

        // Put all number characters into a string
        StringBuilder number = new StringBuilder();
        while (!endOfLine() && NUMBER_CHARS.contains(buffer[index]))
        {
            number.append(buffer[index]);
            index++;
        }
        // Parse the string into a double
        double value = Double.parseDouble(number.toString());
        word.setValue(value);

        return word;
    }

    
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
     * Parses a comment field and returns it as a CodeWord.
     * 
     * @return a CodeWord containing the comment
     */
    protected CodeWord parseComment()
    {
        StringBuilder text = new StringBuilder();

        if (!endOfLine() && (buffer[index] == '('))
        {
            index++;
        }

        while (!endOfLine() && (buffer[index] != ')'))
        {
            text.append(buffer[index]);
            index++;
        }
        if (endOfLine())
        {
            throw new RuntimeException(position() + " Reached end of line inside comment");
        }
        index++; // skip the ')' character

        CodeWord word = new CodeWord('(');
        word.setText(text.toString());
        return word;
    }

    
    /**
     * Utility method to return the current G code file character position as
     * [line,char] used for reporting in Exceptions.
     * 
     * @return String with the current character position
     */
    protected String position()
    {
        return "[" + lineNum + ":" + index + "] ";
    }
}
