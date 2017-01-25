package eventlog;

public class TEventJournalNotCascaded extends TEventJournal
{
	TEventJournalNotCascaded(IJournalConfigParams _params) throws EEventLogException
	{
		super(_params);
		getLogger().setUseParentHandlers(false);
	}
}
