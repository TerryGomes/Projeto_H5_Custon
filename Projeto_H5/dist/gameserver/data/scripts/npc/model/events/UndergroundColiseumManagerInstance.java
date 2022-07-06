package npc.model.events;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.EventType;
import l2mv.gameserver.model.entity.events.impl.UndergroundColiseumEvent;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Util;

/**
 * @author VISTALL
 * @date 15:40/12.07.2011
 */
public class UndergroundColiseumManagerInstance extends UndergroundColiseumHelperInstance
{
	private final String _startHtm;

	public UndergroundColiseumManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		_startHtm = getParameter("start_htm", StringUtils.EMPTY);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		final UndergroundColiseumEvent coliseumEvent = getEvent(UndergroundColiseumEvent.class);
		if (coliseumEvent == null)
		{
			return;
		}
		final List<Player> leaders = coliseumEvent.getObjects(UndergroundColiseumEvent.REGISTERED_LEADERS);
		if (command.equals("register"))
		{
			final Party party = player.getParty();
			if (party == null)
			{
				showChatWindow(player, "events/kerthang_manager008.htm");
			}
			else if (party.getLeader() != player)
			{
				showChatWindow(player, "events/kerthang_manager004.htm");
			}
			else if (party.size() < UndergroundColiseumEvent.PARTY_SIZE)
			{
				showChatWindow(player, "events/kerthang_manager010.htm");
			}
			else
			{
				for (int i = 3; i <= 7; i++)
				{
					final UndergroundColiseumEvent $event = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, i);
					if ($event == null)
					{
						continue;
					}
					final List<Player> $leaders = coliseumEvent.getObjects(UndergroundColiseumEvent.REGISTERED_LEADERS);
					for (final Player object : $leaders)
					{
						if (object == player)
						{
							showChatWindow(player, "events/kerthang_manager009.htm");
							return;
						}
					}
				}
				for (final Player $player : party)
				{
					if ($player.getEffectList().getEffectsBySkillId(5661) != null)
					{
						showChatWindow(player, "events/kerthang_manager021.htm", "%name%", $player.getName());
						return;
					}
					if ($player.getLevel() < coliseumEvent.getMinLevel() || $player.getLevel() > coliseumEvent.getMaxLevel())
					{
						showChatWindow(player, "events/kerthang_manager011.htm", "%name%", $player.getName());
						return;
					}
					if ($player.getDistance(this) > 400)
					{
						showChatWindow(player, "events/kerthang_manager012.htm");
						return;
					}
				}
				if (leaders.size() >= 5)
				{
					showChatWindow(player, "events/kerthang_manager013.htm");
					return;
				}
				coliseumEvent.addObject(UndergroundColiseumEvent.REGISTERED_LEADERS, player);
				showChatWindow(player, "events/kerthang_manager014.htm");
			}
		}
		else if (command.equals("viewMostWins"))
		{
			final Pair<String, Integer> mostWin = coliseumEvent.getTopWinner();
			if (mostWin == null)
			{
				showChatWindow(player, "events/kerthang_manager020.htm");
			}
			else
			{
				showChatWindow(player, "events/kerthang_manager019.htm", "%name%", mostWin.getKey(), "%count%", mostWin.getValue());
			}
		}
		else if (command.equals("cancel"))
		{
			final Party party = player.getParty();
			if (party == null)
			{
				showChatWindow(player, "events/kerthang_manager008.htm");
			}
			else if (party.getLeader() != player)
			{
				showChatWindow(player, "events/kerthang_manager004.htm");
			}
			else
			{
				for (final Player temp : leaders)
				{
					if (temp == player)
					{
						leaders.remove(player);
						showChatWindow(player, "events/kerthang_manager005.htm");
						return;
					}
				}
				showChatWindow(player, "events/kerthang_manager006.htm");
			}
		}
		else if (command.equals("viewTeams"))
		{
			final NpcHtmlMessage msg = new NpcHtmlMessage(0);
			msg.setFile("events/kerthang_manager003.htm");
			for (int i = 0; i < UndergroundColiseumEvent.REGISTER_COUNT; i++)
			{
				final Player team = Util.safeGet(leaders, i);
				msg.replace("%team" + i + "%", team == null ? StringUtils.EMPTY : team.getName());
			}
			player.sendPacket(msg);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... ar)
	{
		showChatWindow(player, _startHtm);
	}
}
