package eventlog;

import java.io.IOException;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import static java.util.logging.Level.INFO;
import java.util.logging.LogManager;
import static java.util.logging.LogManager.getLogManager;
import java.util.logging.Logger;



/**
 * Będąca Sigletonem klasa dostarcza mechanizmów logowania zdarzeń w ramach aplikacji. Możliwe jest
 * zarówno zapisanie niezaleznego komunikatu tekstowego, jak i komunikatu wraz z wyjątkiem. Klasa do
 * realizacji swoich celów korzysta z metod Logging API Javy. <br>
 * Zdarzenia zapisywane są do pliku w postaci XML. Lokalizacja pliku, jego maksymalny rozmiar oraz
 * długość kolejki plików są możliwe do skonfigurowania podczas wywołania fcji init(). Zapisywane są
 * zdarzenia o ważności co najmniej INFO. <br>
 * Aby możliwe było korzystanie z tej klasy, musi ona zostać zainicjowana, najlepiej przy starcie
 * aplikacji, przy użyciu funkcji init(). Następnie należy odwoływać się do utworzonego obiektu
 * poprzez wywołania fcji getJournal().
 * 
 * 
 */
class TEventJournal implements IEventJournal
{
    private static TEventJournal Journal = null;

    private Logger LocalLogger = null;

    private FileHandler LocalFileHandler = null;

    private boolean LogActive = false;

    private boolean AdditionalConsoleOutput = false;

    /**
     * Tworzy obiekt dziennika na podstawie przekazanych parametrów.
     * 
     * @param _params
     *            obiekt dostarczający parametrów do utworzenia logu.
     * @throws EEventLogException
     */
    TEventJournal(IJournalConfigParams _params) throws EEventLogException
    {
        String fName = _params.getLogFileName();
        LogManager lm = getLogManager();
        LocalLogger = Logger.getLogger(_params.getLogName());
        lm.addLogger(LocalLogger);
        try
        {
            if (_params.getFileCount() > 0)
                fName += "%g";
            fName += ".log";
            if (_params.getFileLogSize() > 0 && _params.getFileCount() > 0)
                LocalFileHandler = new FileHandler(fName, _params.getFileLogSize(), _params
                        .getFileCount(), true);
            else if (_params.getFileLogSize() > 0)
                LocalFileHandler = new FileHandler(fName, _params.getFileLogSize(), 1, true);
            else
                LocalFileHandler = new FileHandler(fName, true);

            
            LocalFileHandler.setFormatter(new TMsgFormatter());
//            LocalFileHandler.setFormatter(new XMLFormatter());
            LocalLogger.addHandler(LocalFileHandler);
            LocalFileHandler.setLevel(_params.getLevel());
            LocalLogger.setLevel(_params.getLevel());
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
            throw new EEventLogException("Błąd bezpieczeństwa podczas tworzenia logu zdarzeń", e);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new EEventLogException("Błąd wejścia/wyjścia podczas tworzenia logu zdarzeń", e);
        }
        LocalLogger.log(INFO, "Start logu");
        LogActive = true;
    }

    /**
     * Inicjalizuje statyczny obiekt klasy według przekazanych parametrów. Musi być wywołana, żeby
     * możliwe było korzystanie z dziennika zdarzeń. Wystarczy jednorazowe wywołanie tej funkcji,
     * kolejne wywołania sż ignorowane.
     * 
     * @param _params
     *            parametry do utworzenia logu.
     * @throws EEventLogException
     */
    public static void initJournal(IJournalConfigParams _params) throws EEventLogException
    {
        if (Journal == null)
            Journal = new TEventJournal(_params);
    }

    /**
     * Zwraca utworzony funkcją init() obiekt klasy dziennika.<br>
     * UWAGA: może zwrócić null, jeżeli nie było wcześniejszego wywołania funkcji init().
     * 
     * @return referencję do statycznego obiektu klasy.
     */
    public static TEventJournal getJournal()
    {
        return Journal;
    }

    /**
     * Decyduje czy w przypadku logowania wyjątku funkcją logEvent() dodatkowo ma być wywołana
     * funkcja printStackTrace() z obiektu wyjątku.
     * 
     * @param _consoleOutput
     *            true włącza dodatkowe wypisywanie na konsolę informacji, false wyłącza
     */
    public void setAdditionalConsoleOutput(boolean _consoleOutput)
    {
        AdditionalConsoleOutput = _consoleOutput;
    }

    /**
     * Zapisuje do dziennika zdarzenie składające się wyłącznie z komunikatu tekstowego.
     * 
     * @param _severity
     *            "waga" komunikatu, używane są oznaczenia z java.util.logging.Level.
     * @param _msg
     *            komunikat do zapisania
     */
    public void logEvent(Level _severity, String _msg)
    {
        if (!LogActive)
            return;
        LocalLogger.log(_severity, _msg);
    }

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
    public void logEvent(Level _severity, String _msg, Throwable _th)
    {
        if (!LogActive)
            return;
        LocalLogger.log(_severity, _msg, _th);
        if (AdditionalConsoleOutput)
            _th.printStackTrace();
        notifyExcListTab(_severity, _msg, _th);
    }

    public void logBytes(Level _severity, String _description, byte[] _bytes)
    {
        if (!LogActive)
            return;
        
        String msg = createByteMessage(_description, _bytes);
        LocalLogger.log(_severity, msg);
    }

    /**
     * Tworzy komunikat tekstowy z podesłanej tablicy bajtów. Komunikat
     * prezentuje bajty w postaci czytelnej.
     *
     * @param _bytes
     * @return
     */
    private String createByteMessage(String _description, byte[] _bytes)
    {
        final int MAX_RECORDS_PER_LINE = 25;
        
        StringBuilder sb = new StringBuilder();
        String line;
        String tab = format("%12s", "");
        String lineSeparator =  getProperty("line.separator", "\r\n");

        // najpierw nagłówek
        sb.append(_description + lineSeparator);
        
        // a teraz kolejne linie
        for (int y = 0; y < _bytes.length;){
        
            String line1 = "", line2 = "", line3 = "", line4 = "", line5 = "", lineSep = "";
            // składane z kolejnych rekordów
            for (int records = 0; records < MAX_RECORDS_PER_LINE && y < _bytes.length; ++records, ++y)
            {
                line1 += format("[%-3d] |", y);
                lineSep += "======|";
                line2 += format("0x%02X  |", _bytes[y]);
                line3 += format("%-5d |", (int) (_bytes[y]) & 0xFF);
                line4 += format("%-5d |", (int) (_bytes[y]));
                line5 += format("\'%1c\'   |", _bytes[y] >= 0x20 ? (char)_bytes[y] : ' ');
            }
    
            line = tab + line1 + lineSeparator
                    //
                    + tab + lineSep + lineSeparator
                    //
                    + tab + line2 + lineSeparator
                    //
                    + tab + line3 + lineSeparator
                    //
                    + tab + line4 + lineSeparator
                    //
                    + tab + line5 + lineSeparator;

            sb.append(line);
        }
        
        return sb.toString();
    }
    
    public Logger getLogger()
    {
        return LocalLogger;
    }

    private ArrayList<IExceptionListener> locExcListTab = new ArrayList<>();
    
    @Override
    public synchronized void addExceptionListener(IExceptionListener list)
    {
        for (IExceptionListener locExcListTab1 : locExcListTab) {
            if (locExcListTab1 == list) {
                return;
            }
        }
        locExcListTab.add(list);
    }

    @Override
    public synchronized void removeExceptionListener(IExceptionListener list)
    {
        for (int i = 0; i < locExcListTab.size(); i++)
        {
            if (locExcListTab.get(i) == list)
            {
                locExcListTab.remove(i);
                return;
            }
        }
    }

    private synchronized IExceptionListener[] getListenerTab()
    {
        IExceptionListener[] retTab = new IExceptionListener[locExcListTab.size()];
        for (int i = 0; i < locExcListTab.size(); i++)
        {
            retTab[i] = locExcListTab.get(i);
        }
        return retTab;
    }

    private void notifyExcListTab(Level _severity, String _msg, Throwable _th)
    {
        IExceptionListener[] tab = getListenerTab();
        for (IExceptionListener tab1 : tab) {
            tab1.notifyException(_severity, _msg, _th);
        }
    }
    
}
