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

    protected char[] buffer;
    protected int index;
    protected int lineNum;

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
                if (buffer[index] == '(')
                {
                    // skip over (COMMENTS)
                    skipComment();
                }
                else if (CodeWord.COMMANDS.contains(buffer[index]))
                {
                    block.add(parseWord());
                }
                else if (CodeWord.VALUES.contains(buffer[index]))
                {
                    block.add(parseWord());
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
                    throw new RuntimeException(position() + "Unrecognized code letter: " +buffer[index]);
                }
            }
            
            for (CodeWord word : block)
            {
                System.out.print(word.toString());
            }
            System.out.println();
        }

    }

    protected CodeWord parseWord()
    {
        if (endOfLine()) throw new RuntimeException(position() + " Reached end of line; expected letter");

        CodeWord word = new CodeWord(buffer[index]);
        index++;

        // White space allowed before number
        skipWhitespace();
        if (endOfLine()) throw new RuntimeException(position() + " Reached end of line; expected number");

        // Allow '+', '-' or nothing for sign
        boolean negative = false;
        if (buffer[index] == '-')
        {
            negative = true;
            index++;
        }
        else if (buffer[index] == '+')
        {
            negative = false;
            index++;
        }

        // check for gross formating errors
        if (endOfLine()) throw new RuntimeException(position() + " Reached end of line; expected number");

        if (!Character.isDigit(buffer[index]))
        {
            throw new RuntimeException(position() + "Expected digit; found " + buffer[index]);
        }

        // Read integer portion
        int number = 0;
        while (!endOfLine() && Character.isDigit(buffer[index]))
        {
            number = (number * 10) + (buffer[index] - '0');
            index++;
        }
        if (negative) number = -number;
        word.setValue(number);

        if (!endOfLine() && buffer[index] == '.')
        {
            index++;
            // Read fractional portion up to 3 digits
            number = 0;
            int digits = 3;
            while (!endOfLine() && Character.isDigit(buffer[index]) && digits > 0)
            {
                number = (number * 10) + (buffer[index] - '0');
                digits--;
                index++;
            }
            // round value if there is a 4th digit
            if (!endOfLine() && Character.isDigit(buffer[index]) && digits == 0)
            {
                if (buffer[index] >= '5') number++;
            }
            // skip any remaining digits
            while (!endOfLine() && Character.isDigit(buffer[index]))
            {
                index++;
            }
            if (negative) number = -number;
            word.setFraction(number);
        }

        return word;
    }

    protected boolean endOfLine()
    {
        return index >= buffer.length;
    }

    protected void skipWhitespace()
    {
        while (!endOfLine() && (buffer[index] <= ' '))
            index++;
    }

    protected void skipComment()
    {
        while (!endOfLine() &&  (buffer[index] != ')'))
        {
            index++;
        }
        if (endOfLine())
        {    
            throw new RuntimeException(position() + " Reached end of line inside comment");
        }
        index++; //skip the ')' character
    }

//    protected int readDigits(int maxLength)
//    {
//        if (endOfLine()) throw new RuntimeException(position() + " Reached end of line; expected digit");
//
//        int count = 0;
//        int value = 0;
//        while (!endOfLine() && Character.isDigit(chars[i]))
//        {
//            value = (value * 10) + (Character.getNumericValue(chars[i]) - '0');
//            i++;
//            count++;
//            if (count > maxLength) throw new RuntimeException(position() + " Too many digits; max " + maxLength);
//        }
//        return value;
//    }

//    protected double readNumber()
//    {
//        boolean negative = false;
//        int integer = 0;
//        int numerator = 0;
//        int denominator = 1;
//
//        skipWhitespace();
//        if (endOfLine()) throw new RuntimeException(position() + " Reached end of line; expected number");
//
//        if (chars[i] == '-')
//        {
//            negative = true;
//            i++;
//        }
//        else if (chars[i] == '+')
//        {
//            negative = false;
//            i++;
//        }
//        else if (!Character.isDigit(chars[i]))
//        {
//            throw new RuntimeException(position() + "Expected digit; found " + chars[i]);
//        }
//
//        if (endOfLine()) throw new RuntimeException(position() + " Reached end of line; expected number");
//
//        // Read integer portion
//        integer = 0;
//        while (!endOfLine() && Character.isDigit(chars[i]))
//        {
//            integer = (integer * 10) + (chars[i] - '0');
//            i++;
//        }
//
//        if (!endOfLine() && chars[i] == '.')
//        {
//            i++;
//            // Read fractional portion
//            numerator = 0;
//            denominator = 0;
//            while (!endOfLine() && Character.isDigit(chars[i]))
//            {
//                numerator = (numerator * 10) + (chars[i] - '0');
//                denominator *= 10;
//                i++;
//            }
//        }
//
//        double value = (double) integer + ((double) numerator / (double) denominator);
//        if (negative) value = -value;
//        return value;
//    }

    protected String position()
    {
        return "[" + lineNum + ":" + index + "] ";
    }

}
