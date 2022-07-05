package l2f.gameserver.model.instances;

import java.util.StringTokenizer;

import javolution.text.TextBuilder;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.instancemanager.achievements_engine.AchievementsManager;
import l2f.gameserver.instancemanager.achievements_engine.base.Achievement;
import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.MyTargetSelected;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.ValidateLocation;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.templates.npc.NpcTemplate;

public class AchievementsInstance extends NpcInstance
{
	/**
	 * Author FandC
	 */
	private static final long serialVersionUID = 1L;

	public AchievementsInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	private boolean first = true;

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player == null)
		{
			return;
		}

		if (command.startsWith("showMyAchievements"))
		{
			player.getAchievemntData();
			showMyAchievements(player);
		}
		else if (command.startsWith("achievementInfo"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			int id = Integer.parseInt(st.nextToken());

			showAchievementInfo(id, player);
		}
		else if (command.startsWith("topList"))
		{
			showTopListWindow(player);
		}
		else if (command.startsWith("showMainWindow"))
		{
			String mesaj = showMainHTML(player);
			NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
			msg.setHtml(mesaj);
			msg.replace("%objectId%", String.valueOf(getObjectId()));

			player.sendPacket(msg);

		}
		else if (command.startsWith("getReward"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			int id = Integer.parseInt(st.nextToken());

			switch (id)
			{
			case 10:
				player.getInventory().destroyItemByItemId(8787, 200, "Achievemented");
				AchievementsManager.getInstance().rewardForAchievement(id, player);
				break;
			case 4:
			case 19:
			{
				ItemInstance weapon = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
				if (weapon != null)
				{
					int objid = weapon.getObjectId();
					if (AchievementsManager.getInstance().getAchievementList().get(id).meetAchievementRequirements(player))
					{
						if (!AchievementsManager.getInstance().isBinded(objid, id))
						{
							AchievementsManager.getInstance().getBinded().add(objid + "@" + id);
							player.saveAchievementData(id, objid);
							AchievementsManager.getInstance().rewardForAchievement(id, player);
						}
						else
						{
							player.sendMessage("This item was already used to earn this achievement.");
						}
					}
					else
					{
						player.sendMessage("Seems you don't meet the achievements requirements now.");
					}
				}
				else
				{
					player.sendMessage("You must equip your weapon in order to get rewarded.");
				}
				break;
			}
			case 6:
			case 18:
			{
				int clid = player.getClan().getClanId();
				if (!AchievementsManager.getInstance().isBinded(clid, id))
				{
					AchievementsManager.getInstance().getBinded().add(clid + "@" + id);
					player.saveAchievementData(id, clid);
					AchievementsManager.getInstance().rewardForAchievement(id, player);
				}
				else
				{
					player.sendMessage("Current clan was already rewarded for this achievement.");
				}
				break;
			}
			default:
				player.saveAchievementData(id, 0);
				AchievementsManager.getInstance().rewardForAchievement(id, player);
				break;
			}
			showMyAchievements(player);
		}
		else if (command.startsWith("showMyStats"))
		{
			showMyStatsWindow(player);
		}
		else if (command.startsWith("showHelpWindow"))
		{
			showHelpWindow(player);
		}
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if (this != player.getTarget())
		{
			player.setTarget(this);
			player.sendPacket(new IStaticPacket[]
			{
				new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()),
				new ValidateLocation(this)
			});
		}
		else
		{
			MyTargetSelected my = new MyTargetSelected(getObjectId(), player.getLevel() - getLevel());
			player.sendPacket(my);
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));

			player.sendPacket(new ValidateLocation(this));
		}
		if (!isInRange(player, 200L))
		{
			player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
		}
		else
		{
			showHtmlWindow(player);
		}
	}

	public String showMainHTML(Player player)
	{

		TextBuilder tb = new TextBuilder();
		tb.append("<html noscrollbar><body><title>Achievements Manager</title>");
		tb.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		tb.append("<tr><td align=center>");
		tb.append("<br><br><br>");
		tb.append("<table cellpadding=0 cellspacing=-2 width=270><tr>");
		tb.append("<td align=\"center\"><font name=\"hs12\" color=\"LEVEL\">Hello " + player.getName() + "<br>Get your achievements reward!</font></td>");
		tb.append("</tr></table>");
		tb.append("<br><br>");
		tb.append("<table width=270><tr>");
		tb.append("<td align=center><img src=\"l2ui.squaregray\" width=\"270\" height=\"1\"></td>");
		tb.append("</tr></table>");
		tb.append("<br><br>");
		tb.append("<table cellpadding=0 cellspacing=-2 width=270>");
		tb.append("<tr><td align=\"center\"><button value=\"My Achievements\" action=\"bypass -h npc_%objectId%_showMyAchievements\" width=220 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		tb.append("<tr><td align=\"center\"><button value=\"Statistics\" action=\"bypass -h npc_%objectId%_showMyStats\" width=220 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		tb.append("<tr><td align=\"center\"><button value=\"Help\" action=\"bypass -h npc_%objectId%_showHelpWindow\" width=220 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
		tb.append("</table>");
		tb.append("<br><br>");
		tb.append("<table width=270><tr>");
		tb.append("<td align=\"center\"><img src=\"l2ui.squaregray\" width=\"270\" height=\"1\"></td>");
		tb.append("</tr></table>");
		tb.append("<br><br><br><br>");
		tb.append("<table cellpadding=0 cellspacing=-2 width=270><tr>");
		tb.append("<td align=center><font name=\"hs12\" color=\"LEVEL\">Achievement Manager</font></td>");
		tb.append("</tr></table>");
		tb.append("<br><br>");
		tb.append("</td></tr>");
		tb.append("</table></body></html>");

		return tb.toString();
	}

	public void showHtmlWindow(Player player)
	{
		if (first)
		{
			AchievementsManager.getInstance().loadUsed();
			first = false;
		}

		String mesaj = showMainHTML(player);

		NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
		msg.setHtml(mesaj);
		msg.replace("%objectId%", String.valueOf(getObjectId()));

		player.sendPacket(msg);
	}

	private void showMyAchievements(Player player)
	{
		TextBuilder tb = new TextBuilder();
		tb.append("<html><title>Achievements Manager</title><body><br>");

		tb.append("<center><font name=\"hs12\" color=\"LEVEL\">My achievements</font>:</center><br>");

		if (AchievementsManager.getInstance().getAchievementList().isEmpty())
		{
			tb.append("There are no Achievements created yet!");
		}
		else
		{
			int i = 0;

			tb.append("<table width=290 border=0 bgcolor=0D2437>");
			tb.append("<tr><td width=290 align=\"left\">Name:</td><td width=60 align=\"right\">Info:</td><td width=200 align=\"center\">Status:</td></tr></table>");
			tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1\"><br>");

			for (Achievement a : AchievementsManager.getInstance().getAchievementList().values())
			{
				tb.append(getTableColor(i));
				tb.append("<tr><td width=290 align=\"left\">" + a.getName() + "</td><td width=50 align=\"right\"><a action=\"bypass -h npc_%objectId%_achievementInfo " + a.getID()
							+ "\">info</a></td><td width=200 align=\"center\">" + getStatusString(a.getID(), player) + "</td></tr></table>");
				i++;
			}

			tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1s\"><br>");
			tb.append("<center><button value=\"Back\" action=\"bypass -h npc_%objectId%_showMainWindow\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" width=95 height=21></center>");
		}

		NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
		msg.setHtml(tb.toString());
		msg.replace("%objectId%", String.valueOf(getObjectId()));

		player.sendPacket(msg);
	}

	private void showAchievementInfo(int achievementID, Player player)
	{
		Achievement a = AchievementsManager.getInstance().getAchievementList().get(achievementID);

		TextBuilder tb = new TextBuilder();
		tb.append("<html noscrollbar><title>Achievements Manager</title><body><br>");

		tb.append("<table width=290 height=30 border=0 background=\"L2UI_CT1.Button_DF\">");
		tb.append("<tr><td width=290 align=\"center\">" + a.getName() + "</td></tr></table><br>");
		tb.append("<center>Status: " + getStatusString(achievementID, player));

		if (a.meetAchievementRequirements(player) && !player.getCompletedAchievements().contains(achievementID))
		{
			tb.append("<button value=\"Receive Reward!\" action=\"bypass -h npc_%objectId%_getReward " + a.getID() + "\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" width=200 height=25>");
		}

		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1s\"><br>");

		tb.append("<table width=290 border=0 bgcolor=111111>");
		tb.append("<tr><td width=290 align=\"center\">Description</td></tr></table><br>");
		tb.append(a.getDescription());
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1s\"><br>");

		tb.append("<table width=290 border=0 background=\"L2UI_CT1.Button_DF\">");
		tb.append("<tr><td width=290 align=\"left\">Condition:</td><td width=100 align=\"left\">Value:</td><td width=200 align=\"center\">Status:</td></tr></table>");
		tb.append(getConditionsStatus(achievementID, player));
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1s\"><br>");
		tb.append("<center><button value=\"Back\" action=\"bypass -h npc_%objectId%_showMyAchievements\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" width=200 height=25></center>");

		NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
		msg.setHtml(tb.toString());
		msg.replace("%objectId%", String.valueOf(getObjectId()));

		player.sendPacket(msg);
	}

	private void showMyStatsWindow(Player player)
	{
		TextBuilder tb = new TextBuilder();
		tb.append("<html noscrollbar><title>Achievements Manager</title><body><center><br><br><br>");
		tb.append("<font name=\"hs12\" color=\"LEVEL\">Achievements Statistics </font>");
		tb.append("<br><br><br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1\"><br>");

		player.getAchievemntData();
		int completedCount = player.getCompletedAchievements().size();

		tb.append("You have completed: " + completedCount + "/<font color=\"LEVEL\">" + AchievementsManager.getInstance().getAchievementList().size() + "</font>");

		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1s\"><br>");
		tb.append("<center><button value=\"Back\" action=\"bypass -h npc_%objectId%_showMainWindow\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" width=95 height=21></center>");

		NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
		msg.setHtml(tb.toString());
		msg.replace("%objectId%", String.valueOf(getObjectId()));

		player.sendPacket(msg);
	}

	private void showTopListWindow(Player player)
	{
		TextBuilder tb = new TextBuilder();
		tb.append("<html noscrollbar><title>Achievements Manager</title><body><center><br>");
		tb.append("<font name=\"hs12\" color=\"LEVEL\">Achievements Top List </font>");
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1\"><br>");

		tb.append("Not implemented yet!");

		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1s\"><br>");
		tb.append("<center><button value=\"Back\" action=\"bypass -h npc_%objectId%_showMainWindow\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" width=95 height=21></center>");

		NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
		msg.setHtml(tb.toString());
		msg.replace("%objectId%", String.valueOf(getObjectId()));

		player.sendPacket(msg);
	}

	private void showHelpWindow(Player player)
	{
		TextBuilder tb = new TextBuilder();
		tb.append("<html noscrollbar><title>Achievements Manager</title><body><center><br>");
		tb.append("<font name=\"hs12\" color=\"LEVEL\">Achievements Information:</font>");
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1\"><br>");

		tb.append("<table><tr><td align=center>You can check the status of your achievements, get reward for each condition of the job is meet, if not you can check which condition is still not met, by using info button. </td></tr></table>");
		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1s\"><br>");
		tb.append("<table><tr><td align=center><font color=\"FF0000\">Not Completed</font> - Achivement not done!</td></tr>");
		tb.append("<tr><td align=center><font color=\"LEVEL\">Get Reward</font> - you may receive reward, click info.</td></tr>");
		tb.append("<tr><td align=center><font color=\"5EA82E\">Completed</font> - Achievement completed!</td></tr></table>");

		tb.append("<br><img src=\"l2ui.squaregray\" width=\"290\" height=\"1s\"><br>");
		tb.append("<center><button value=\"Back\" action=\"bypass -h npc_%objectId%_showMainWindow\" back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" width=95 height=21></center>");

		NpcHtmlMessage msg = new NpcHtmlMessage(getObjectId());
		msg.setHtml(tb.toString());
		msg.replace("%objectId%", String.valueOf(getObjectId()));

		player.sendPacket(msg);
	}

	private String getStatusString(int achievementID, Player player)
	{
		if (player.getCompletedAchievements().contains(achievementID))
		{
			return "<font color=\"5EA82E\">Completed</font>";
		}
		if (AchievementsManager.getInstance().getAchievementList().get(achievementID).meetAchievementRequirements(player))
		{
			return "<font color=\"LEVEL\">Get Reward</font>";
		}
		return "<font color=\"FF0000\">Not Completed</font>";

	}

	private String getTableColor(int i)
	{
		if ((i % 2) == 0)
		{
			return "<table width=290 border=0 bgcolor=\"444444\">";
		}
		return "<table width=290 border=0>";
	}

	private String getConditionsStatus(int achievementID, Player player)
	{
		int i = 0;
		String s = "</center>";
		Achievement a = AchievementsManager.getInstance().getAchievementList().get(achievementID);
		String completed = "<font color=\"5EA82E\">Completed</font></td></tr></table>";
		String notcompleted = "<font color=\"FF0000\">Not Completed</font></td></tr></table>";

		for (Condition c : a.getConditions())
		{
			s += getTableColor(i);
			s += "<tr><td width=290 align=\"left\">" + c.getName() + "</td><td width=100 align=\"left\">" + c.getValue() + "</td><td width=200 align=\"center\">";
			i++;

			if (c.meetConditionRequirements(player))
			{
				s += completed;
			}
			else
			{
				s += notcompleted;
			}
		}
		return s;
	}
}