package eventlog;

import java.util.logging.Level;

public interface IExceptionListener
{

    /**
     * Informuje o wystąpieniu wyjątku.
     * 
     * @param _severity
     *            "waga" komunikatu
     * @param _msg
     *            wiadomość do zapisania
     * @param _th
     *            obiekt wyjątku do zapisania do dziennika
     */
    public void notifyException(Level _severity, String _msg, Throwable _th);

}
