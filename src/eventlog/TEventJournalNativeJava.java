package eventlog;

import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;


/**
 * EventJournal który opakowuje natywny logger Javy
 * {@link java.util.logging.Logger} Może być użyteczny np w aplikacjach
 * działającyh w środowisku serwera aplikacji internetowych ,aby się * wpiąć w dostarczany przez nie Logger.
 * 

 */
class TEventJournalNativeJava implements IEventJournal
{
    private boolean additionalConsoleOutput = false;

    /** Logger komunikatów i/lub wyjątków */
    private Logger logger = null;

    TEventJournalNativeJava(String name)
    {
        logger = getLogger(name);
    }

    @Override
    public void addExceptionListener(IExceptionListener list)
    {
        throw new RuntimeException("TEventJournalJavaLogger nie obsługuje addExceptionListener");
    }

    public void logBytes(Level severity, String description, byte[] bytes)
    {
    }

    public void logEvent(Level severity, String msg)
    {
        logger.log(severity, msg);
    }

    public void logEvent(Level severity, String msg, Throwable th)
    {
        logger.log(severity, msg, th);
        if (additionalConsoleOutput)
            th.printStackTrace();
    }

    @Override
    public void removeExceptionListener(IExceptionListener list)
    {
        throw new RuntimeException("TEventJournalJavaLogger nie obsługuje removeExceptionListener");
    }

    @Override
    public void setAdditionalConsoleOutput(boolean consoleOutput)
    {
        additionalConsoleOutput = consoleOutput;
    }

}
