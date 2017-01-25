package eventlog;

import java.util.logging.Level;

/**
 * Służy do przekazania do funkcji inicjującej klasę dziennika TEventJournal parametrów niezbędnych
 * do utworzenia loga
 * 
 * 
 */
interface IJournalConfigParams
{
    /**
     * Zwraca nazwę logu, obiektu logu, a nie pliku do którego będzie ew. robiony zapis!!!
     * 
     * @return nazwa obiektu
     */
    public String getLogName();

    /**
     * !! TA funkcja zwraca nazwę pliku do którego log ma być zapisywany. Nazwa pliku powianna być
     * podana z pełną ścieżką ale bez rozszerzenia, po uwzględnieiu ew. numeru pliku, zostanie
     * dodane automatyczne rozszerzenie: .log
     * 
     * @return pełna nazwa pliku
     */
    public String getLogFileName();

    /**
     * Zwraca maksymalny rozmiar pliku logu, je�eli zwróconą wartością będzie 0 maksymalny rozmiar
     * nie będzie narzucony.
     * 
     * @return
     */
    public int getFileLogSize();

    /**
     * Zwraca ilość plików, do których może być zapisywany log w ramach cyklicznego zapisu. Jeżeli
     * maks. rozmiar pliku jest ograniczony, to wtedy zdarzenia są pisane do kolejnego pliku o
     * kolejnym numerze. Jednak łączna ilość takich plików nie może być większa niż liczba zwracana
     * przez tą funkcję.<br>
     * Jeżeli zwróconą wartością będzie 0, kolejkowanie nie będzie włączone.
     * 
     * @return
     */
    public int getFileCount();
    
    /**
     * Poziom logowania. Zdarzenia o poziomie niższym, niż podany tutaj, nie będą rejestrowane (w funkcjach logEvent).
     */
    public Level getLevel();
    
}
