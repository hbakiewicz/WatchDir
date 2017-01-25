package eventlog;

import static java.lang.String.format;
import static java.lang.System.out;
import java.util.logging.Level;


/**
 * Prosty EventJournal, który wyrzuca dane na konsolę. W zasadzie powinien być
 * używany tylko do testów.
 */
class TEventJournalConsole implements IEventJournal
{
    public void logBytes(Level _severity, String _description, byte[] _bytes)
    {
        out.println(format("%7s: %s", _severity.toString(), _description));
    }

    public void logEvent(Level _severity, String _msg)
    {
        out.println(format("%7s: %s", _severity.toString(), _msg));
    }

    public void logEvent(Level _severity, String _msg, Throwable _th)
    {
        out.println(format("%7s: %s", _severity.toString(), _msg));
        _th.printStackTrace();
    }

    public void setAdditionalConsoleOutput(boolean _consoleOutput)
    {
    }

    @Override
    public void addExceptionListener(IExceptionListener list)
    {        
    }

    @Override
    public void removeExceptionListener(IExceptionListener list)
    {
    }
}
