package eventlog;

import java.util.logging.Level;



final class TEventJournalTypeDependent implements IEventJournal
{
    private final IEventJournal log;
    
    private final String type;
    
    TEventJournalTypeDependent(String _type, IEventJournal _log)
    {
        log = _log;

        String t = _type;
        // wydłuż do 3-ch, jesli za krótkie
        while (t.length() < 3)
            t = t + " ";
        // skróć do 3-ch jeśli za długie
        if (t.length() > 3)
            t = t.substring(0, 3);
        type = t;
    }
    
    public void logEvent(Level _severity, String _msg)
    {
        if (log != null)
            log.logEvent(_severity, type + "@" + _msg);
    }

    public void logEvent(Level _severity, String _msg, Throwable _th)
    {
        if (log != null)
            log.logEvent(_severity, type + "@" + _msg, _th);
    }
    
    public void logBytes(Level _severity, String _description, byte[] _bytes)
    {
        if (log != null)
            log.logBytes(_severity, type + "@" + _description, _bytes);
    }    

    public void setAdditionalConsoleOutput(boolean _consoleOutput)
    {
        if (log != null)
            log.setAdditionalConsoleOutput(_consoleOutput);
    }

    @Override
    public void addExceptionListener(IExceptionListener list)
    {
        if (log != null)
            log.addExceptionListener(list);
    }

    @Override
    public void removeExceptionListener(IExceptionListener list)
    {
        if (log != null)
            log.removeExceptionListener(list);
    }
}
