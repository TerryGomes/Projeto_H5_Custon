package l2mv.gameserver.handler.admincommands.impl;

import java.util.Collection;
import java.util.List;

import l2mv.gameserver.data.xml.holder.SkillAcquireHolder;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.SkillLearn;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.base.AcquireType;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.SkillCoolTime;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Calculator;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.funcs.Func;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Log;

public class AdminSkill implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_show_skills,
		admin_remove_skills,
		admin_skill_list,
		admin_skill_index,
		admin_add_skill,
		admin_remove_skill,
		admin_get_skills,
		admin_reset_skills,
		admin_give_all_skills,
		admin_show_effects,
		admin_debug_stats,
		admin_remove_cooldown,
		admin_resetreuse,
		admin_people_having_effect,
		admin_buff,
		admin_show_skill,
		admin_give_all_clan_skills
	}

	private static Skill[] adminSkills;

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanEditChar)
		{
			return false;
		}

		switch (command)
		{
		case admin_show_skills:
			showSkillsPage(activeChar);
			break;
		case admin_show_effects:
			showEffects(activeChar);
			break;
		case admin_remove_skills:
			removeSkillsPage(activeChar);
			break;
		case admin_skill_list:
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/skills.htm"));
			break;
		case admin_skill_index:
			if (wordList.length > 1)
			{
				activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/skills/" + wordList[1] + ".htm"));
			}
			break;
		case admin_add_skill:
			adminAddSkill(activeChar, wordList);
			break;
		case admin_remove_skill:
			adminRemoveSkill(activeChar, wordList);
			break;
		case admin_get_skills:
			adminGetSkills(activeChar);
			break;
		case admin_reset_skills:
			adminResetSkills(activeChar);
			break;
		case admin_give_all_skills:
			adminGiveAllSkills(activeChar);
			break;
		case admin_debug_stats:
			debug_stats(activeChar);
			break;
		case admin_give_all_clan_skills:
			giveAllClanSkills(activeChar);
			break;
		case admin_remove_cooldown:
		case admin_resetreuse:
			final int radius = (wordList.length > 1 ? Integer.parseInt(wordList[1]) : 0);
			if (radius < 1)
			{
				Player target = activeChar;
				if (activeChar.getTarget() != null && activeChar.getTarget().isPlayable())
				{
					target = activeChar.getTarget().getPlayer();
				}

				target.resetReuse();
				target.sendPacket(new SkillCoolTime(activeChar));
				activeChar.sendMessage("Reseted all skill's reuses on the target");
			}
			else
			{
				for (Player target : World.getAroundPlayers(activeChar, radius, 200))
				{
					target.resetReuse();
					target.sendPacket(new SkillCoolTime(activeChar));
				}
				activeChar.sendMessage("All skills reuses reseted on a " + radius + " radius");
			}
			break;
		case admin_buff:
			for (int i = 7041; i <= 7064; i++)
			{
				activeChar.addSkill(SkillTable.getInstance().getInfo(i, 1));
			}
			activeChar.sendPacket(new SkillList(activeChar));
			break;
		case admin_people_having_effect:
			int skillId = Integer.parseInt(wordList[1]);
			for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			{
				for (Effect e : player.getEffectList().getAllEffects())
				{
					if (e.getSkill().getId() == skillId)
					{
						activeChar.sendMessage("Player: " + player.getName() + " Level:" + e.getSkill().getLevel());
					}
				}
			}
			activeChar.sendMessage("Finished!");
			break;
		case admin_show_skill:
		{
			final int skillId2 = Integer.parseInt(wordList[1]);
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, skillId2, 1, 500, 100L));
			break;
		}
		}

		return true;
	}

	private void giveAllClanSkills(Player activeChar)
	{
		Player target = null;
		if (activeChar.getTarget() != null)
		{
			if (activeChar.getTarget().isPlayer())
			{
				target = (Player) activeChar.getTarget();
			}
		}
		if (target == null)
		{
			activeChar.sendMessage("[ERROR]Incorrect target.");
			return;
		}
		final Clan clan = target.getClan();
		if (clan == null)
		{
			activeChar.sendMessage("[ERROR] This player is NOT in a clan!");
			return;
		}
		Skill skill = null;
		for (int i = 0; i < 10; i++) // Lazy hack to give clan skills at max level for the specific clan level.
		{
			Collection<SkillLearn> clanSkills = SkillAcquireHolder.getInstance().getAvailableSkills(target, AcquireType.CLAN);
			for (SkillLearn sl : clanSkills)
			{
				skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
				clan.addSkill(skill, true);
			}

			// Synerge - If giving clan skills to a clan lvl 11, also give squad skills
			if (clan.getLevel() == 11)
			{
				for (SubUnit squad : clan.getAllSubUnits())
				{
					clanSkills = SkillAcquireHolder.getInstance().getAvailableSkills(target, AcquireType.SUB_UNIT, squad);
					for (SkillLearn sl : clanSkills)
					{
						skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
						clan.addSkill(skill, true);
					}
				}
			}
		}

		clan.broadcastToOnlineMembers(new Say2(0, ChatType.CLAN, "[CLAN]", "Congratulations! This clan just received all clan skills for clan level " + clan.getLevel() + "!"));
		clan.broadcastSkillListToOnlineMembers();
		activeChar.sendMessage("Clan " + clan.getName() + " sucessfully received all clan skills.");
	}

	private void debug_stats(Player activeChar)
	{
		GameObject target_obj = activeChar.getTarget();
		if (!target_obj.isCreature())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Creature target = (Creature) target_obj;

		Calculator[] calculators = target.getCalculators();

		String log_str = "--- Debug for " + target.getName() + " ---\r\n";

		for (Calculator calculator : calculators)
		{
			if (calculator == null)
			{
				continue;
			}
			Env env = new Env(target, activeChar, null);
			env.value = calculator.getBase();
			log_str += "Stat: " + calculator._stat.getValue() + "\r\n";
			Func[] funcs = calculator.getFunctions();
			for (int i = 0; i < funcs.length; i++)
			{
				String order = Integer.toHexString(funcs[i].order).toUpperCase();
				if (order.length() == 1)
				{
					order = "0" + order;
				}
				log_str += "\tFunc #" + i + "@ [0x" + order + "]" + funcs[i].getClass().getSimpleName() + "\t" + env.value;
				if (funcs[i].getCondition() == null || funcs[i].getCondition().test(env))
				{
					funcs[i].calc(env);
				}
				log_str += " -> " + env.value + (funcs[i].owner != null ? "; owner: " + funcs[i].owner.toString() : "; no owner") + "\r\n";
			}
		}

		Log.add(log_str, "debug_stats");
	}

	/**
	 * This function will give all the skills that the gm target can have at its
	 * level to the traget
	 * @param activeChar
	 *
	 */
	private void adminGiveAllSkills(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player = null;
		if (target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}
		int unLearnable = 0;
		int skillCounter = 0;
		Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);
		while (skills.size() > unLearnable)
		{
			unLearnable = 0;
			for (SkillLearn s : skills)
			{
				Skill sk = SkillTable.getInstance().getInfo(s.getId(), s.getLevel());
				if (sk == null || !sk.getCanLearn(player.getClassId()))
				{
					unLearnable++;
					continue;
				}
				if (player.getSkillLevel(sk.getId()) == -1)
				{
					skillCounter++;
				}
				player.addSkill(sk, true);
			}
			skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);
		}

		player.sendMessage("Admin gave you " + skillCounter + " skills.");
		player.sendPacket(new SkillList(player));
		activeChar.sendMessage("You gave " + skillCounter + " skills to " + player.getName());
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void removeSkillsPage(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if (target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Collection<Skill> skills = player.getAllSkills();

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName() + "</center>");
		replyMSG.append("<br><table width=270><tr><td>Lv: " + player.getLevel() + " " + player.getTemplate().className + "</td></tr></table>");
		replyMSG.append("<br><center>Click on the skill you wish to remove:</center>");
		replyMSG.append("<br><table width=270>");
		replyMSG.append("<tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
		for (Skill element : skills)
		{
			replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_remove_skill " + element.getId() + "\">" + element.getName() + "</a></td><td width=60>" + element.getLevel() + "</td><td width=40>" + element.getId() + "</td></tr>");
		}
		replyMSG.append("</table>");
		replyMSG.append("<br><center><table>");
		replyMSG.append("Remove custom skill:");
		replyMSG.append("<tr><td>Id: </td>");
		replyMSG.append("<td><edit var=\"id_to_remove\" width=110></td></tr>");
		replyMSG.append("</table></center>");
		replyMSG.append("<center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
		replyMSG.append("<br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showSkillsPage(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if (target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		final StringBuilder replyMSG = new StringBuilder("<html noscrollbar><body><title>Edit Character</title>");
		replyMSG.append("<table border=0 cellpadding=0 cellspacing=0 width=290 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
		replyMSG.append("<tr><td align=center>");
		replyMSG.append("<br>");
		replyMSG.append("<table cellpadding=0 cellspacing=-5 width=260><tr>");
		replyMSG.append("<td><button value=\"Main\" action=\"bypass -h admin_admin\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td><button value=\"Events\" action=\"bypass -h admin_show_html events/events.htm\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td><button value=\"Chars\" action=\"bypass -h admin_char_manage\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td><button value=\"Server\" action=\"bypass -h admin_server admserver.htm\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("<td><button value=\"GM Shop\" action=\"bypass -h admin_gmshop\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br><br>");
		replyMSG.append("<table cellpadding=0 cellspacing=-2 width=290>");
		replyMSG.append("<tr>");
		replyMSG.append("<td align=center><font name=\"hs12\" color=\"LEVEL\">Edit Player:</font></td>");
		replyMSG.append("<td align=center><font name=\"hs12\" color=\"00FF00\">" + player.getName() + "</font></td>");
		replyMSG.append("</tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br>");
		replyMSG.append("<table cellpadding=0 cellspacing=-2 width=290>");
		replyMSG.append("<tr>");
		replyMSG.append("<td align=center><font color=\"LEVEL\">Level:" + player.getLevel() + " - " + player.getTemplate().className + "</font></td>");
		replyMSG.append("</tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<table cellpadding=0 cellspacing=-5 width=260>");
		replyMSG.append("<tr><td><button value=\"Add skills\" action=\"bypass -h admin_skill_list\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Get skills\" action=\"bypass -h admin_get_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"Delete skills\" action=\"bypass -h admin_remove_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Reset skills\" action=\"bypass -h admin_reset_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"Give All Skills\" action=\"bypass -h admin_give_all_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"All Clan Skills\" action=\"bypass -h admin_give_all_clan_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("</td></tr>");
		replyMSG.append("</table></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showEffects(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if (target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<center>Editing character: " + player.getName() + "</center>");

		replyMSG.append("<br><center><button value=\"");
		replyMSG.append("Refresh");
		replyMSG.append("\" action=\"bypass -h admin_show_effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center>");
		replyMSG.append("<br>");

		List<Effect> list = player.getEffectList().getAllEffects();
		if (list != null && !list.isEmpty())
		{
			for (Effect e : list)
			{
				replyMSG.append(e.getSkill().getName()).append(" ").append(e.getSkill().getLevel()).append(" - ").append(e.getSkill().isToggle() ? "Infinity" : (e.getTimeLeft() + " seconds")).append("<br1>");
			}
		}
		replyMSG.append("<br></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void adminGetSkills(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if (target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		if (player.getName().equals(activeChar.getName()))
		{
			player.sendMessage("There is no point in doing it on your character.");
		}
		else
		{
			Collection<Skill> skills = player.getAllSkills();
			adminSkills = activeChar.getAllSkillsArray();
			for (Skill element : adminSkills)
			{
				activeChar.removeSkill(element, true);
			}
			for (Skill element : skills)
			{
				activeChar.addSkill(element, true);
			}
			activeChar.sendMessage("You now have all the skills of  " + player.getName() + ".");
		}

		showSkillsPage(activeChar);
	}

	private void adminResetSkills(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player = null;
		if (target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Skill[] skills = player.getAllSkillsArray();
		int counter = 0;
		for (Skill element : skills)
		{
			if ((!element.isCommon()) && (!SkillAcquireHolder.getInstance().isSkillPossible(player, element, AcquireType.NORMAL)))
			{
				player.removeSkill(element, true);
				counter++;
			}
		}
		player.checkSkills();
		player.sendPacket(new SkillList(player));
		player.sendMessage("[GM]" + activeChar.getName() + " has updated your skills.");
		activeChar.sendMessage(counter + " skills removed.");

		showSkillsPage(activeChar);
	}

	private void adminAddSkill(Player activeChar, String[] wordList)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if (target != null && target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		if (wordList.length == 3)
		{
			int id = Integer.parseInt(wordList[1]);
			int level = Integer.parseInt(wordList[2]);
			Skill skill = SkillTable.getInstance().getInfo(id, level);
			if (skill != null)
			{
				player.sendMessage("Admin gave you the skill " + skill.getName() + ".");
				player.addSkill(skill, true);
				player.sendPacket(new SkillList(player));
				activeChar.sendMessage("You gave the skill " + skill.getName() + " to " + player.getName() + ".");
			}
			else
			{
				activeChar.sendMessage("Error: there is no such skill.");
			}
		}

		showSkillsPage(activeChar);
	}

	private void adminRemoveSkill(Player activeChar, String[] wordList)
	{
		GameObject target = activeChar.getTarget();
		Player player = null;
		if (target.isPlayer() && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		if (wordList.length == 2)
		{
			int id = Integer.parseInt(wordList[1]);
			int level = player.getSkillLevel(id);
			Skill skill = SkillTable.getInstance().getInfo(id, level);
			if (skill != null)
			{
				player.sendMessage("Admin removed the skill " + skill.getName() + ".");
				player.removeSkill(skill, true);
				player.sendPacket(new SkillList(player));
				activeChar.sendMessage("You removed the skill " + skill.getName() + " from " + player.getName() + ".");
			}
			else
			{
				activeChar.sendMessage("Error: there is no such skill.");
			}
		}

		removeSkillsPage(activeChar);
	}
}