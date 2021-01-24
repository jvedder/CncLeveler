package cncleveler.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A formatter for java.util.logging that outputs just the message text for each
 * log record as a single line. Warning and Severe messages are prefixed with
 * their level. If a throwable exception is provided, it is printed on the line
 * after the message.
 * <p>
 * This format is useful for simple run-time status logging to the console or
 * status window.
 */

public class MessageFormatter extends Formatter
{
    /**
     * Format string for printing the log record
     */
    private static final String FORMAT = "%1$s%2$s%3$s%n";

    /**
     * Formats the given LogRecord in the format:
     * 
     * <pre>
     *    The application started
     *    This is application status
     *    WARNING: This is some warning message
     *    SEVERE: This is a severe message with exception
     *    java.lang.IllegalArgumentException: invalid argument
     *             at MyClass.mash(MyClass.java:9)
     *             at MyClass.crunch(MyClass.java:6)
     *             at MyClass.main(MyClass.java:3)
     * </pre>
     * 
     * @param record
     *            the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record)
    {
        String level = "";
        if (record.getLevel() == Level.WARNING) level = "WARNING: ";
        if (record.getLevel() == Level.SEVERE) level = "ERROR: ";

        String throwable = "";
        if (record.getThrown() != null)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }
        return String.format(FORMAT, level, record.getMessage(), throwable);
    }
}
