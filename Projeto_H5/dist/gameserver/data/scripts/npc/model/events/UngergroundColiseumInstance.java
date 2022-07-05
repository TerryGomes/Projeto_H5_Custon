package npc.model.events;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.data.xml.holder.MultiSellHolder;
import l2f.gameserver.instancemanager.UnderGroundColliseumManager;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Experience;
import l2f.gameserver.model.entity.Coliseum;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.templates.npc.NpcTemplate;

//TODO: check gameserver.instancemanager.UnderGroundColliseumManager
public class UngergroundColiseumInstance extends NpcInstance
{
	private static final Logger _log = LoggerFactory.getLogger(UngergroundColiseumInstance.class);
	private static boolean ENABLED = false;

	public UngergroundColiseumInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	private int getMinLevel()
	{
		if (getNpcId() == 32513)
		{
			return 40;
		}
		if (getNpcId() == 32516)
		{
			return 50;
		}
		if (getNpcId() == 32515)
		{
			return 60;
		}
		if (getNpcId() == 32514)
		{
			return 70;
		}
		if (getNpcId() == 32377)
		{
			return 1;
		}
		return 1;
	}

	private int getMaxLevel()
	{
		if (getNpcId() == 32513)
		{
			return 49;
		}
		if (getNpcId() == 32516)
		{
			return 59;
		}
		if (getNpcId() == 32515)
		{
			return 69;
		}
		if (getNpcId() == 32514)
		{
			return 79;
		}
		if (getNpcId() == 32377)
		{
			return 85;
		}
		return Experience.getMaxLevel();
	}

	@Override
	public void showChatWindow(Player player, int val, Object... replace)
	{
		final String filename = "Coliseum/" + val + ".htm";
		final NpcHtmlMessage html = new NpcHtmlMessage(player, this, filename, val);
		html.replace("%levelMin%", "" + getMinLevel());
		html.replace("%levelMax%", "" + getMaxLevel());
		player.sendPacket(html);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		if (!ENABLED)
		{
			player.sendMessage("Sorry but underground coliseum is temporary disabled.");
			return;
		}
		player.sendActionFailed();
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken(); // Get actual command
		if (actualCommand.startsWith("register"))
		{
			if ((player.getParty() == null) || !player.getParty().isLeader(player))
			{
				showChatWindow(player, 3);
				return;
			}
			final Coliseum coliseum = UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(getMaxLevel());
			if ((coliseum == null) || (coliseum.getWaitingPartys().size() > 4))
			{
				showChatWindow(player, 3);
				return;
			}
			for (final Player member : player.getParty())
			{
				if (member.getLevel() > getMaxLevel() || member.getLevel() < getMinLevel())
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
					return;
				}
				if (member.isCursedWeaponEquipped())
				{
					player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member));
					return;
				}
			}
			Coliseum.register(player, getMinLevel(), getMaxLevel());
			return;
		}
		else if (actualCommand.startsWith("view"))
		{
			int count = 0;
			final String filename = "Coliseum/" + 5 + ".htm";
			final NpcHtmlMessage html = new NpcHtmlMessage(player, this, filename, 5);
			final Coliseum coliseum = UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(getMaxLevel());
			if (coliseum != null)
			{
				for (final Party team : coliseum.getWaitingPartys())
				{
					if (team != null)
					{
						switch (count)
						{
						case 0:
							html.replace("%Team1%", team.getLeader().getName());
							break;
						case 1:
							html.replace("%Team2%", team.getLeader().getName());
							break;
						case 2:
							html.replace("%Team3%", team.getLeader().getName());
							break;
						case 3:
							html.replace("%Team4%", team.getLeader().getName());
							break;
						case 4:
							html.replace("%Team5%", team.getLeader().getName());
							break;
						default:
							break;
						}
						count++;
						if (count > 5)
						{
							_log.info("We have six or more registred clans to UC WTF?", "UC");
							continue;
						}
					}
				}
			}
			if (count == 0)
			{
				html.replace("%Team1%", "none");
				html.replace("%Team2%", "none");
				html.replace("%Team3%", "none");
				html.replace("%Team4%", "none");
				html.replace("%Team5%", "none");
			}
			player.sendPacket(html);
		}
		// TODO: диалог
		else if (actualCommand.startsWith("winner"))
		{
			String filename;
			NpcHtmlMessage html;
			/*
			 * if(UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(getMaxLevel()).getPreviusWinners() != null) { filename = "data/html/Coliseum/"+ 7 + "htm"; html = new
			 * NpcHtmlMessage(player, this, filename, 7); html.replace("winner",
			 * UnderGroundColliseumManager.getInstance().getColiseumByLevelLimit(getMaxLevel()).getPreviusWinners().getLeader().getName()); } else {
			 */
			filename = "Coliseum/" + 6 + ".htm";
			html = new NpcHtmlMessage(player, this, filename, 6);
			// }
			player.sendPacket(html);
		}
		else if (actualCommand.startsWith("Multisell") || actualCommand.startsWith("multisell"))
		{
			final int listId = Integer.parseInt(command.substring(9).trim());
			final Castle castle = getCastle(player);
			MultiSellHolder.getInstance().SeparateAndSend(listId, player, castle != null ? castle.getTaxRate() : 0);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
