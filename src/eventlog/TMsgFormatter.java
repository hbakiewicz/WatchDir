package eventlog;

import static java.lang.System.getProperty;
import java.util.Calendar;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;
import java.util.GregorianCalendar;
import java.util.logging.Formatter;
import java.util.logging.Level;
import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import java.util.logging.LogRecord;

class TMsgFormatter extends Formatter
{

    @Override
    public String format(LogRecord arg0)
    {
        String source = "---";
        String text = arg0.getMessage();
        if (text == null)
            text = "";
        
        // jeśli komunikat zaczyna się od AAA@Treśc komunikatu, to wytnij AAA
        if (text.length() >= 4 && text.charAt(3) == '@')
        {
            source = text.substring(0,3);
            text = text.substring(4); 
        }
        
        String newline = getProperty("line.separator");
        String msg = formatTime(arg0.getMillis());
        msg += " ";
        msg += String.format("%04X", arg0.getThreadID());
        msg += " ";
        msg += source; // 3-literowy identyfikator modułu. Np. MSR-czytnik kodów, PRN-drukarka, PRG-główny program itp.
        msg += " ";
        if (arg0.getLevel() == SEVERE)
            msg += "SEVERE  ";
        else if (arg0.getLevel() == INFO)
            msg += "INFO    ";
        else if (arg0.getLevel() == WARNING)
            msg += "WARNING ";
        else if (arg0.getLevel() == FINEST)
            msg += "FINEST  ";
        else if (arg0.getLevel() == FINER)
            msg += "FINER   ";
        else if (arg0.getLevel() == FINE)
            msg += "FINE    ";
        else if (arg0.getLevel() == CONFIG)
            msg += "CONFIG  ";
        else
            msg += "ERROR   ";

        msg += text;
        msg += newline;

        try
        {
            Throwable thr = arg0.getThrown();
            while (thr != null)
            {
                msg += "          Przyczyna -> ";
                msg += thr.getClass() + ": ";
                msg += thr.getMessage();
                msg += newline;
                if (arg0.getLevel() == SEVERE)
                {
                    StackTraceElement[] stTab = thr.getStackTrace();
                    if (stTab != null)
                    {
                        msg += "              Stack trace: ";
                        msg += newline;
                        for (StackTraceElement stTab1 : stTab) {
                            msg += "                  at ";
                            msg += stTab1.toString();
                            msg += newline;
                        }
                    }
                }
                thr = thr.getCause();
            }
        }
        catch (RuntimeException e)
        {
            // żeby nic się tutaj nie mmogło wywalić.
        }
        return msg;
    }

    private String formatTime(long millis)
    {
        GregorianCalendar gc = new GregorianCalendar();
        String msg = "";
        gc.setTimeInMillis(millis);
        msg += new Integer(gc.get(YEAR)).toString();
        msg += "-";
        msg += String.format("%02d", gc.get(MONTH) + 1);
        msg += "-";
        msg += String.format("%02d", gc.get(DAY_OF_MONTH));
        msg += " ";
        msg += String.format("%02d", gc.get(HOUR_OF_DAY));
        msg += ":";
        msg += String.format("%02d", gc.get(MINUTE));
        msg += ":";
        msg += String.format("%02d", gc.get(SECOND));
        msg += ".";
        msg += String.format("%03d", gc.get(MILLISECOND));
        return msg;
    }

}
