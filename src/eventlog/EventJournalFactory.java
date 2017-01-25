package eventlog;

import java.util.logging.Level;

public class EventJournalFactory
{

    /**
     * 
     *
     * @param _logName - nazwa logu. Dla każdego nowo tworzonego obiektu powinna być unikalna.
     * @param _logFileName - nazwa pliku
     * @param _logSize - maksymalny rozmiar pliku
     * @param _fileCount - maksymalna ilość plików. Po wypełnieniu ostatniego log nadpisuje najstarsze.
     * @param _level - poziom logowania. Logi o niższym poziomie nie będą umieszczane w pliku.
     * @return
     * @throws EEventLogException
     */
    public static IEventJournal createEventJournal(String _logName, String _logFileName, int _logSize, int _fileCount,
            Level _level) throws EEventLogException
    {
        return new TEventJournal(new TJournalConfigParams(_logName, _logFileName, _logSize, _fileCount, _level));
    }
    
    /**
     * Prosty EventJournal, który wyrzuca wszystkie dane na konsolę. W zasadzie powinien być używany tylko do testów. 
     * 
     * @return
     */
    public static IEventJournal createEventJournalConsole()
    {
        return new TEventJournalConsole();
    }

    /**
     * Event journal, który w podanym logu <b>_eventJournal</b> rejestruje rekordy podając trzyliterowe oznaczenie typu
     * urządzenia. Standardowy TEventJournal umieszcza w tym miejscu "---". Np.:<p>
     * <code>2008-06-24 14:40:06.462 000A <b>PBC</b> INFO started</code>
       * 
     *
     * @param _type -
     *            trzyliterowe oznaczenie modułu ()
     * @param _eventJournal -
     *            log, w którym te zdarzenia bęfda rejtrowane. Może to być glówny og programu.
     * @return
     */
    public static IEventJournal createTypeDependentEventJournal(String _type, IEventJournal _eventJournal)
    {
        return new TEventJournalTypeDependent(_type, _eventJournal);
    }

    /**
     * Dla loggera bez kaskadowania do parenta
     * 
     * @param _logName
     * @param _logFileName
     * @param _logSize
     * @param _fileCount
     * @param _level
     * @return
     * @throws EEventLogException
     * 
     *
     */
    public static IEventJournal createEventJournalNotCascaded(String _logName, String _logFileName, int _logSize, int _fileCount,
            Level _level) throws EEventLogException
    {
        return new TEventJournalNotCascaded(new TJournalConfigParams(_logName, _logFileName, _logSize, _fileCount, _level));
    }
    
    /**
     * EventJournal który opakowuje natywny logger Javy
     * {@link java.util.logging.Logger} Może być użyteczny np w aplikacjach
     * działającyh w środowisku serwera aplikacji internetowych ,aby się     * wpiąć w dostarczany przez nie Logger.
     * 
     * @param name
     *            nazwa-klucz loggera
     * @return gotowy do użycia logger
     * 
     */
    public static IEventJournal createEventJournalNativeJava(String name)
    {
        return new TEventJournalNativeJava(name);
    }
}
