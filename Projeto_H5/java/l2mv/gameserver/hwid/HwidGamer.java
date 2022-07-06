package l2mv.gameserver.hwid;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.gameserver.model.Player;

public class HwidGamer
{
	public static enum PlayerThreat
	{
		NONE, FRIENDLY, KEEP_EYE_ON, CRITICAL
	}

	private final String hwid;
	private final List<Player> onlineChars = new CopyOnWriteArrayList<>();
	private long firstTimePlayed;
	private long totalTimePlayed;
	private int pollAnswer;
	private int warnings;
	private int seenChangeLog;
	private final PlayerThreat threat;
	private long bannedToDate;

	public HwidGamer(String hwid, long firstTimePlayed, long totalTimePlayed, int pollAnswer, int warnings, int seenChangeLog, PlayerThreat threat, long bannedToDate)
	{
		this.hwid = hwid;
		this.firstTimePlayed = firstTimePlayed;
		this.totalTimePlayed = totalTimePlayed;
		this.pollAnswer = pollAnswer;
		this.seenChangeLog = seenChangeLog;
		this.warnings = warnings;
		this.threat = threat;
		this.bannedToDate = bannedToDate;
	}

	public void addPlayer(Player player)
	{
		onlineChars.add(player);
	}

	public void removePlayer(Player player)
	{
		onlineChars.remove(player);
		if (onlineChars.isEmpty())
		{
			// Prims - Instead of removing the hwid info, calculate the total ingame time and reset the variables
			calculateTotalTimePlayed();
		}
	}

	public List<Player> getOnlineChars()
	{
		return onlineChars;
	}

	public String getHwid()
	{
		return hwid;
	}

	public long getFirstTimePlayed()
	{
		return firstTimePlayed;
	}

	public void calculateTotalTimePlayed()
	{
		totalTimePlayed += System.currentTimeMillis() - firstTimePlayed;
		firstTimePlayed = System.currentTimeMillis();
	}

	public void incTotalTimePlayed(long timeToAdd)
	{
		totalTimePlayed += timeToAdd;
	}

	public long getTotalTimePlayed()
	{
		return totalTimePlayed;
	}

	public void setPollAnswer(int answer, boolean updateDb)
	{
		pollAnswer = answer;
		if (updateDb)
		{
			HwidEngine.getInstance().updateGamerInDb(this);
		}
	}

	public int getPollAnswer()
	{
		return pollAnswer;
	}

	public PlayerThreat getThreat()
	{
		return threat;
	}

	public void setHwidBanned(long toDate)
	{
		bannedToDate = toDate;
	}

	public long getBannedToDate()
	{
		return bannedToDate;
	}

	public void setWarnings(int newWarnings)
	{
		warnings = newWarnings;
		HwidEngine.getInstance().updateGamerInDb(this);
	}

	public int getWarnings()
	{
		return warnings;
	}

	public void setSeenChangeLog(int changeLogIndex, boolean updateInDb)
	{
		seenChangeLog = changeLogIndex;
		if (updateInDb)
		{
			HwidEngine.getInstance().updateGamerInDb(this);
		}
	}

	public int getSeenChangeLog()
	{
		return seenChangeLog;
	}

	public void logToPlayer(int charObjId, String msg)
	{
		HwidEngine.getInstance().addToSaveLog(charObjId, hwid, msg, System.currentTimeMillis());
	}
}
