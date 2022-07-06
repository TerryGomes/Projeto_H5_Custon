package l2mv.gameserver.model.instances;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.MultiSellHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Hero;
import l2mv.gameserver.model.entity.olympiad.CompType;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2mv.gameserver.network.serverpackets.ExHeroList;
import l2mv.gameserver.network.serverpackets.ExReceiveOlympiad;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class OlympiadManagerInstance extends NpcInstance
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadManagerInstance.class);

	public OlympiadManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		if (Config.ENABLE_OLYMPIAD)
		{
			Olympiad.addOlympiadNpc(this);
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this) || checkForDominionWard(player) || !Config.ENABLE_OLYMPIAD)
		{
			return;
		}

		if (command.startsWith("OlympiadNoble"))
		{
			if (!Config.ENABLE_OLYMPIAD)
			{
				return;
			}

			int val = Integer.parseInt(command.substring(14));
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);

			switch (val)
			{
			case 1:
				Olympiad.unRegisterNoble(player);
				showChatWindow(player, 0);
				break;
			case 2:
				if (Olympiad.isRegistered(player))
				{
					player.sendPacket(html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_noregister.htm"));
				}
				else
				{
					player.sendPacket(html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_register.htm"));
					html.replace("%1%", String.valueOf(Olympiad.getPeriod()));
					html.replace("%2%", String.valueOf(Olympiad.getCurrentCycle()));
					html.replace("%3%", String.valueOf(Olympiad.getCountOpponents()));
					player.sendPacket(html);
				}
				break;
			case 4:
				Olympiad.registerNoble(player, CompType.NON_CLASSED);
				break;
			case 5:
				Olympiad.registerNoble(player, CompType.CLASSED);
				break;
			case 6:
				int passes = Olympiad.getNoblessePasses(player);
				if (passes > 0)
				{
					player.getInventory().addItem(Config.ALT_OLY_COMP_RITEM, passes, "Olympiad End Reward");
					player.sendPacket(SystemMessage2.obtainItems(Config.ALT_OLY_COMP_RITEM, passes, 0));
				}
				else
				{
					player.sendPacket(html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_nopoints.htm"));
				}
				break;
			case 7:
				MultiSellHolder.getInstance().SeparateAndSend(102, player, 0);
				break;
			case 9:
				MultiSellHolder.getInstance().SeparateAndSend(103, player, 0);
				break;
			case 10:
				Olympiad.registerNoble(player, CompType.TEAM);
				break;
			case 3:
			case 8:
			default:
				_log.warn("Olympiad System: Couldnt send packet for request " + val);
				break;
			}
		}
		else if (command.startsWith("Olympiad"))
		{
			if (!Config.ENABLE_OLYMPIAD)
			{
				return;
			}
			int val = Integer.parseInt(command.substring(9, 10));

			NpcHtmlMessage reply = new NpcHtmlMessage(player, this);

			switch (val)
			{
			case 1:
				if (!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd())
				{
					player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
					return;
				}
				player.sendPacket(new ExReceiveOlympiad.MatchList());
				break;
			case 2:
				// for example >> Olympiad 1_88
				int classId = Integer.parseInt(command.substring(11));
				if (classId >= 88)
				{
					reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_ranking.htm");

					List<String> names = OlympiadDatabase.getClassLeaderBoard(classId);

					int index = 1;
					for (String name : names)
					{
						reply.replace("%place" + index + "%", String.valueOf(index));
						reply.replace("%rank" + index + "%", name);
						index++;
						if (index > 10)
						{
							break;
						}
					}
					for (; index <= 10; index++)
					{
						reply.replace("%place" + index + "%", "");
						reply.replace("%rank" + index + "%", "");
					}

					player.sendPacket(reply);
				}
				// TODO Send player each class rank
				break;
			case 3:
				if (!Config.ENABLE_OLYMPIAD_SPECTATING)
				{
					break;
				}
				Olympiad.addSpectator(Integer.parseInt(command.substring(11)), player);
				break;
			case 4:
				player.sendPacket(new ExHeroList());
				break;
			case 5:
				if (Hero.getInstance().isInactiveHero(player.getObjectId()))
				{
					Hero.getInstance().activateHero(player);
					reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "monument_give_hero.htm");
				}
				else
				{
					reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "monument_dont_hero.htm");
				}
				player.sendPacket(reply);
				break;
			case 6:// Getting Best players by current olympiad scores
					// for example >> Olympiad 6_88
				classId = Integer.parseInt(command.substring(11));
				if (classId >= 88)
				{
					reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_ranking_points.htm");

					Map<String, Integer> names = OlympiadDatabase.getClassLeaderBoardCurrent(classId);

					int index = 1;
					for (Entry<String, Integer> name : names.entrySet())
					{
						reply.replace("%place" + index + "%", String.valueOf(index));
						reply.replace("%rank" + index + "%", name.getKey());
						reply.replace("%points" + index + "%", name.getValue());
						index++;
						if (index > 10)
						{
							break;
						}
					}
					for (; index <= 10; index++)
					{
						reply.replace("%place" + index + "%", "");
						reply.replace("%rank" + index + "%", "");
						reply.replace("%points" + index + "%", "");
					}

					player.sendPacket(reply);
				}
				break;
			default:
				_log.warn("Olympiad System: Couldnt send packet for request " + val);
				break;
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (checkForDominionWard(player))
		{
			return;
		}

		String fileName = Olympiad.OLYMPIAD_HTML_PATH;
		int npcId = getNpcId();
		switch (npcId)
		{
		case 31688: // Grand Olympiad Manager
			fileName += "manager";
			break;
		default: // Monument of Heroes
			fileName += "monument";
			break;
		}
		if (player.isNoble())
		{
			fileName += "_n";
		}
		if (val > 0)
		{
			fileName += "-" + val;
		}
		fileName += ".htm";
		player.sendPacket(new NpcHtmlMessage(player, this, fileName, val));
	}
}