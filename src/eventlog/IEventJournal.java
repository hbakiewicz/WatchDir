package eventlog;

import java.util.logging.Level;

public interface IEventJournal
{

    /**
     * Decyduje czy w przypadku logowania wyjątku funkcją logEvent() dodatkowo ma być wywołana
     * funkcja printStackTrace() z obiektu wyjątku.
     * 
     * @param _consoleOutput
     *            true włącza dodatkowe wypisywanie na konsolę informacji, false wyłącza
     */
    public void setAdditionalConsoleOutput(boolean _consoleOutput);

    /**
     * Zapisuje do dziennika zdarzenie składające się wyłącznie z komunikatu tekstowego.
     * 
     * @param _severity
     *            "waga" komunikatu, używane są oznaczenia z java.util.logging.Level.
     * @param _msg
     *            komunikat do zapisania
     */
    public void logEvent(Level _severity, String _msg);

    /**
     * Zapisuje do logu zdarzenie składające się komunikatu tekstowego i wyjątku. Zależnie od
     * ustawienia AdditionalConsoleOutput na konsolę będzie dodatkowo wysyłana zawartość wyjątku
     * metodą printStackTrace(), lub nie.
     * 
     * @param _severity
     *            "waga" komunikatu
     * @param _msg
     *            wiadomość do zapisania
     * @param _th
     *            obiekt wyjątku do zapisania do dziennika
     */
    public void logEvent(Level _severity, String _msg, Throwable _th);
    
    /**
     * Zapisuje do logu ciąg bajtów opatrzonych jakimś opisem tekstowym. Używane w trakcie logowania komunikacji.
     * 
     * @param _severity
     *            "waga" komunikatu
     * @param _description -
     *            opis, który zostanie umieszczony nad bajtami
     * @param _bytes -
     *            bajty, które powinny zostać wrzucone do logu
     */
    public void logBytes(Level _severity, String _description, byte[] _bytes);

    /**
     * Dodaje nowego słuchacza wyjątków.
     * 
     * @param list
     */
    public void addExceptionListener(IExceptionListener list);
    
    /**
     * Usuwa podanego słuchacza wyjątków.
     * 
     * @param list
     */
    public void removeExceptionListener(IExceptionListener list);
    
}
