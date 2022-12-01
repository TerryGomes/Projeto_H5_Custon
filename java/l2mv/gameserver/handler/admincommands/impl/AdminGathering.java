package l2mv.gameserver.handler.admincommands.impl;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.ExReplyWritePost;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Language;
import l2mv.gameserver.utils.Location;

public class AdminGathering implements IAdminCommandHandler
{
	private static final int TIME_IN_MILLIS_FOR_QUESTION = 30000;
	private static final long TIME_IN_MILLIS_FOR_RESULTS = 120000L;
	private static final double RANGE_NEARBY = 1000.0;

	private static enum Commands
	{
		admin_gathering_ask_peace,
		admin_gathering_ask_outside,
		admin_gathering_reward
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		switch (command)
		{
		case admin_gathering_ask_peace:
		{
			try
			{
				final int range = Integer.parseInt(wordList[1]);
				final long currentTime = System.currentTimeMillis();
				final AtomicInteger totalPeaceTeleported = new AtomicInteger(0);
				int peaceAskedCount = 0;
				final Location adminLoc = activeChar.getLoc();
				final Map<Language, ConfirmDlg> dialogPerLang = new EnumMap<Language, ConfirmDlg>(Language.class);
				for (Language lang : Language.values())
				{
					dialogPerLang.put(lang, new ConfirmDlg(SystemMsg.S1, TIME_IN_MILLIS_FOR_QUESTION).addString(StringHolder.getNotNull(lang, "Gathering.AskDialog", new Object[0])));
				}
				for (Player anyPlayer : GameObjectsStorage.getAllPlayersForIterate())
				{
					if (anyPlayer.isInPeaceZone() && anyPlayer.getReflection().isDefault() && !anyPlayer.isInStoreMode() && anyPlayer.getDistance(activeChar) > RANGE_NEARBY && !anyPlayer.equals(activeChar))
					{
						anyPlayer.ask(dialogPerLang.get(anyPlayer.getLanguage()), new AskPlayerToJoin(adminLoc, anyPlayer, range, currentTime, totalPeaceTeleported));
						++peaceAskedCount;
					}
				}
				ThreadPoolManager.getInstance().schedule(new SendTotalPlayersTeleportedMsgThread(activeChar.getObjectId(), totalPeaceTeleported), TIME_IN_MILLIS_FOR_RESULTS);
				activeChar.sendMessage("Asked " + peaceAskedCount + " Players in Peace Zones!");
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //gathering_ask_peace [range]");
			}
			break;
		}
		case admin_gathering_ask_outside:
		{
			final Map<Language, IStaticPacket> msgPerLang = new EnumMap<Language, IStaticPacket>(Language.class);
			for (Language lang2 : Language.values())
			{
				msgPerLang.put(lang2, new Say2(activeChar.getObjectId(), ChatType.TELL, activeChar.getName(), StringHolder.getNotNull(lang2, "Gathering.AskPM", new Object[0])));
			}
			int outsideAskedCount = 0;
			for (Player anyPlayer2 : GameObjectsStorage.getAllPlayersCopy())
			{
				if (anyPlayer2.getReflection().isDefault() && !anyPlayer2.isInStoreMode() && anyPlayer2.getDistance(activeChar) > RANGE_NEARBY && !anyPlayer2.equals(activeChar))
				{
					anyPlayer2.sendPacket(msgPerLang.get(anyPlayer2.getLanguage()));
					++outsideAskedCount;
				}
			}
			activeChar.sendMessage("Asked " + outsideAskedCount + " Players outside Peace Zone!");
			break;
		}
		case admin_gathering_reward:
		{
			try
			{
				final List<Player> playersToReward = World.getAroundPlayers(activeChar, Integer.parseInt(wordList[3]), 1000);
				playersToReward.add(activeChar);
				final String mailTopic = "Gathering Event Reward!";
				final String mailBody = "Thank you for participating!";
				final Map<Integer, Long> rewardList = new HashMap<Integer, Long>(1);
				rewardList.put(Integer.parseInt(wordList[1]), Long.parseLong(wordList[2]));
				int rewardedCount = 0;
				for (Player target : playersToReward)
				{
					if (!target.isInOfflineMode() && !target.isInStoreMode())
					{
						Functions.sendSystemMail(target, mailTopic, mailBody, rewardList);
						++rewardedCount;
					}
				}
				if (rewardedCount > 0)
				{
					activeChar.sendPacket(ExReplyWritePost.STATIC_TRUE);
					activeChar.sendPacket(SystemMsg.MAIL_SUCCESSFULLY_SENT);
				}
				activeChar.sendMessage("You have rewarded " + rewardedCount + " players!");
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //gathering_reward [itemId] [itemCount] [range]");
			}
			break;
		}
		default:
		{
			return false;
		}
		}

		return true;
	}

	private static class AskPlayerToJoin implements OnAnswerListener
	{
		private final Location gmLoc;
		private final Player askedPlayer;
		private final int rangeAroundTheGM;
		private final long askedDate;
		private final AtomicInteger totalPlayersTeleported;

		private AskPlayerToJoin(Location gmLoc, Player askedPlayer, int rangeAroundTheGM, long askedDate, AtomicInteger totalPlayersTeleported)
		{
			this.gmLoc = gmLoc;
			this.askedPlayer = askedPlayer;
			this.rangeAroundTheGM = rangeAroundTheGM;
			this.askedDate = askedDate;
			this.totalPlayersTeleported = totalPlayersTeleported;
		}

		@Override
		public void sayYes()
		{
			teleport();
		}

		@Override
		public void sayNo()
		{
			final long currentTime = System.currentTimeMillis();
			if (currentTime - askedDate > 29000L)
			{
				teleport();
			}
		}

		private void teleport()
		{
			totalPlayersTeleported.incrementAndGet();
			final Location loc = Location.findAroundPosition(gmLoc, rangeAroundTheGM, askedPlayer.getGeoIndex());
			askedPlayer.teleToLocation(loc);
		}
	}

	private static class SendTotalPlayersTeleportedMsgThread extends RunnableImpl
	{
		private final int adminObjectId;
		private final AtomicInteger totalPlayersTeleported;

		SendTotalPlayersTeleportedMsgThread(int adminObjectId, AtomicInteger totalPlayersTeleported)
		{
			this.adminObjectId = adminObjectId;
			this.totalPlayersTeleported = totalPlayersTeleported;
		}

		@Override
		public void runImpl()
		{
			final Player admin = GameObjectsStorage.getPlayer(adminObjectId);
			if (admin != null)
			{
				admin.sendMessage("Totally " + totalPlayersTeleported.get() + " players were teleported!");
			}
		}
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
