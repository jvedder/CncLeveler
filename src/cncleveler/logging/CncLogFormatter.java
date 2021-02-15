package cncleveler.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A formatter for java.util.logging that outputs each log record as a simple, single line with a
 * time stamp.
 */
public class CncLogFormatter extends Formatter
{
    /**
     * Format string for printing the log record
     */
    private static final String FORMAT = "[%1$td-%1$tb-%1$tY %1$tT] %2$s%3$s%4$s%n";

    /**
     * Local variable used to hold and the time stamp of the log record in date and time format.
     */
    private final Date timeStamp = new Date();

    /**
     * Formats the given LogRecord in the format:
     * 
     * <pre>
     *    [20-Oct-2016 13:25:30] The application started 
     *    [20-Oct-2016 13:25:31] This is application info status
     *    [20-Oct-2016 13:25:33] WARNING: This is some warning message
     *    [20-Oct-2016 13:28:10] ERROR: This is a severe message
     *    [20-Oct-2016 13:28:15] ERROR: This is a severe message with exception
     *    java.lang.IllegalArgumentException: invalid argument
     *             at MyClass.mash(MyClass.java:9)
     *             at MyClass.crunch(MyClass.java:6)
     *             at MyClass.main(MyClass.java:3)
     * </pre>
     * 
     * Adapted from the Oracle implementation of java.util.logging.SimpleFormatter (which is shared
     * under GNU General Public License version 2)
     * 
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    @Override
    public synchronized String format(LogRecord record)
    {
        timeStamp.setTime(record.getMillis());

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
        return String.format(FORMAT, timeStamp, level, record.getMessage(), throwable);
    }
}
