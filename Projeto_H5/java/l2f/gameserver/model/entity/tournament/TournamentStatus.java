package l2f.gameserver.model.entity.tournament;

public enum TournamentStatus
{
	NOT_ACTIVE, REGISTRATION, BATTLES, OVER;

	public static TournamentStatus getCurrentStatus()
	{
		if (BattleScheduleManager.getInstance().isScheduleActive())
		{
			return TournamentStatus.BATTLES;
		}
		if (BattleScheduleManager.getInstance().isTournamentOver())
		{
			return TournamentStatus.OVER;
		}
		return TournamentStatus.REGISTRATION;
	}

	public static TournamentStatus getCurrentStatus(BattleScheduleManager scheduleManager)
	{
		if (scheduleManager.isScheduleActive())
		{
			return TournamentStatus.BATTLES;
		}
		if (scheduleManager.isTournamentOver())
		{
			return TournamentStatus.OVER;
		}
		return TournamentStatus.REGISTRATION;
	}
}
