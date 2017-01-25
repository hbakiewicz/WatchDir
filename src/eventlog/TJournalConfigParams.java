package eventlog;

import java.util.logging.Level;

class TJournalConfigParams implements IJournalConfigParams
{
    private final String LogName;

    private final String LogFileName;

    private final int FileLogSize;

    private final int FileCount;
    
    private final Level level;

    public TJournalConfigParams(String _logName, String _logFileName, int _logSize, int _fileCount, Level _level)
    {
        LogName = _logName;
        LogFileName = _logFileName;
        FileLogSize = _logSize;
        FileCount = _fileCount;
        level = _level;
    }

    public String getLogName()
    {
        return new String(LogName);
    }

    public String getLogFileName()
    {
        return new String(LogFileName);
    }

    public int getFileLogSize()
    {
        return FileLogSize;
    }

    public int getFileCount()
    {
        return FileCount;
    }

    public Level getLevel()
    {
        return level;
    }

}
