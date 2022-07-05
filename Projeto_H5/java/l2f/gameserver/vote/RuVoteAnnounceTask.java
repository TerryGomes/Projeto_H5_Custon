package l2f.gameserver.vote;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.StringHolder;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.utils.Language;

public class RuVoteAnnounceTask
{
	private final Map<Integer, Long> lastPanelOpenDates = new ConcurrentHashMap<Integer, Long>();

	private RuVoteAnnounceTask()
	{
		if (!Config.ENABLE_RU_VOTE_SYSTEM)
		{
			return;
		}

		final long announceDelay = ConfigHolder.getLong("RuVoteAnnouncementDelay");
		if (announceDelay > 0L)
		{
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new AnnounceThread(), announceDelay, announceDelay);
		}
	}

	private boolean checkCanGetMsg(Player player, long currentDate)
	{
		if (player == null || player.getNetConnection() == null || player.isInStoreMode() || RuVotesHolder.getInstance().checkHavePenalty(player, false))
		{
			return false;
		}
		final long lastPanelOpenDate = lastPanelOpenDates.containsKey(player.getObjectId()) ? lastPanelOpenDates.get(player.getObjectId()) : 0L;
		return lastPanelOpenDate <= currentDate - Config.RU_VOTE_APPEAR_DELAY;
	}

	public boolean onPanelOpened(Player player)
	{
		if (!lastPanelOpenDates.containsKey(player.getObjectId()) && !RuVotesHolder.getInstance().checkHavePenalty(player, false))
		{
			lastPanelOpenDates.put(player.getObjectId(), System.currentTimeMillis());
			return true;
		}
		return false;
	}

	public void sendFailMessages()
	{
		final long failAfterDate = System.currentTimeMillis() - Config.RU_VOTE_APPEAR_DELAY;
		final Say2 packet = new Say2(0, ChatType.TELL, Config.SERVER_NAME, Config.RU_VOTE_FAILED_MSG);
		for (Map.Entry<Integer, Long> lastPanelOpen : lastPanelOpenDates.entrySet())
		{
			final Player player = GameObjectsStorage.getPlayer(lastPanelOpen.getKey());
			if (player != null)
			{
				if (RuVotesHolder.getInstance().checkHavePenalty(player, false))
				{
					lastPanelOpenDates.remove(lastPanelOpen.getKey());
				}
				else
				{
					if (lastPanelOpen.getValue() >= failAfterDate)
					{
						continue;
					}
					player.sendPacket(packet);
					lastPanelOpenDates.remove(lastPanelOpen.getKey());
				}
			}
		}
	}

	private static class AnnounceThread extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (!Config.ENABLE_RU_VOTE_SYSTEM)
			{
				return;
			}

			final Map<Language, Say2> msgPerLanguage = new EnumMap<Language, Say2>(Language.class);
			for (Language lang : Language.values())
			{
				msgPerLanguage.put(lang, new Say2(0, ChatType.ANNOUNCEMENT, "",
							StringHolder.getNotNull(lang, "RussianVote.NotVotedAnnounce", ConfigHolder.getString("RuVoteLinkToVote"), ConfigHolder.getString("RuVoteLinkToGuide"))));
			}
			final long currentTime = System.currentTimeMillis();
			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				if (RuVoteAnnounceTask.getInstance().checkCanGetMsg(player, currentTime))
				{
					player.sendPacket(msgPerLanguage.get(player.getLanguage()));
				}
			}
		}
	}

	public static RuVoteAnnounceTask getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final RuVoteAnnounceTask instance = new RuVoteAnnounceTask();
	}
}
