package l2f.gameserver.handler.admincommands.impl;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.Config;
import l2f.gameserver.database.mysql;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SubClass;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.base.PlayerClass;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.entity.CCPHelpers.CCPSecondaryPassword;
import l2f.gameserver.model.entity.olympiad.Olympiad;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.loginservercon.AuthServerCommunication;
import l2f.gameserver.network.loginservercon.gspackets.ChangePassword;
import l2f.gameserver.network.serverpackets.ExPCCafePointInfo;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.SkillList;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.randoms.GeoLocation;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.ItemFunctions;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.Util;

@SuppressWarnings("unused")
public class AdminEditChar implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_edit_character, admin_character_actions, admin_current_player, admin_nokarma, admin_setkarma, admin_character_list, admin_show_characters, admin_find_character, admin_save_modifications, admin_rec, admin_settitle, admin_setclass, admin_setname, admin_setsex, admin_setcolor, admin_add_exp_sp_to_character, admin_add_exp_sp, admin_sethero, admin_setnoble, admin_trans, admin_setsubclass, admin_setfame, admin_addfame, admin_setbday, admin_setpassword, admin_setsecondary,
		admin_give_item, admin_add_bang, admin_set_bang
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (activeChar.getPlayerAccess().CanRename)
		{
			if (fullString.startsWith("admin_settitle"))
			{
				try
				{
					String val = fullString.substring(15);
					GameObject target = activeChar.getTarget();
					Player player = null;
					if (target == null)
					{
						return false;
					}
					if (target.isPlayer())
					{
						player = (Player) target;
						player.setTitle(val);
						player.sendMessage("Your title has been changed by a GM");
						player.sendChanges();
					}
					else if (target.isNpc())
					{
						((NpcInstance) target).setTitle(val);
						target.decayMe();
						target.spawnMe();
					}

					return true;
				}
				catch (StringIndexOutOfBoundsException e)
				{ // Case of empty character title
					activeChar.sendMessage("You need to specify the new title.");
					return false;
				}
			}
			else if (fullString.startsWith("admin_setclass"))
			{
				try
				{
					String val = fullString.substring(15);
					int id = Integer.parseInt(val.trim());
					GameObject target = activeChar.getTarget();

					if (target == null || !target.isPlayer())
					{
						target = activeChar;
					}
					if (id > 136)
					{
						activeChar.sendMessage("There are no classes over 136 id.");
						return false;
					}
					Player player = target.getPlayer();
					player.setClassId(id, true, false);
					player.sendMessage("Your class has been changed by a GM");
					player.broadcastCharInfo();

					return true;
				}
				catch (StringIndexOutOfBoundsException e)
				{
					activeChar.sendMessage("You need to specify the new class id.");
					return false;
				}
			}
			else if (fullString.startsWith("admin_setname"))
			{
				try
				{
					String val = fullString.substring(14);
					GameObject target = activeChar.getTarget();
					Player player;
					if (target != null && target.isPlayer())
					{
						player = (Player) target;
					}
					else
					{
						return false;
					}
					if (mysql.simple_get_int("count(*)", "characters", "`char_name` like '" + val + "'") > 0)
					{
						activeChar.sendMessage("Name already exist.");
						return false;
					}
					Log.add("Character " + player.getName() + " renamed to " + val + " by GM " + activeChar.getName(), "renames");
					player.reName(val);
					player.sendMessage("Your name has been changed by a GM");
					return true;
				}
				catch (StringIndexOutOfBoundsException e)
				{ // Case of empty character name
					activeChar.sendMessage("You need to specify the new name.");
					return false;
				}
			}
		}

		if (!activeChar.getPlayerAccess().CanEditChar && !activeChar.getPlayerAccess().CanViewChar)
		{
			return false;
		}

		if (fullString.equals("admin_current_player"))
		{
			showCharacterList(activeChar, null);
		}
		else if (fullString.startsWith("admin_character_list"))
		{
			try
			{
				String val = fullString.substring(21);
				Player target = GameObjectsStorage.getPlayer(val);
				showCharacterList(activeChar, target);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty character name
			}
		}
		else if (fullString.startsWith("admin_show_characters"))
		{
			try
			{
				String val = fullString.substring(22);
				int page = Integer.parseInt(val);
				listCharacters(activeChar, page);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty page
			}
		}
		else if (fullString.startsWith("admin_find_character"))
		{
			try
			{
				String val = fullString.substring(21);
				findCharacter(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{ // Case of empty character name
				activeChar.sendMessage("You didnt enter a character name to find.");

				listCharacters(activeChar, 0);
			}
		}
		else if (!activeChar.getPlayerAccess().CanEditChar)
		{
			return false;
		}
		else if (fullString.equals("admin_edit_character"))
		{
			editCharacter(activeChar);
		}
		else if (fullString.equals("admin_character_actions"))
		{
			showCharacterActions(activeChar);
		}
		else if (fullString.equals("admin_nokarma"))
		{
			setTargetKarma(activeChar, 0);
		}
		else if (fullString.startsWith("admin_setkarma"))
		{
			try
			{
				String val = fullString.substring(15);
				int karma = Integer.parseInt(val);
				setTargetKarma(activeChar, karma);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify new karma value.");
			}
		}
		else if (fullString.startsWith("admin_save_modifications"))
		{
			try
			{
				String val = fullString.substring(24);
				adminModifyCharacter(activeChar, val);
			}
			catch (StringIndexOutOfBoundsException e)
			{ // Case of empty character name
				activeChar.sendMessage("Error while modifying character.");
				listCharacters(activeChar, 0);
			}
		}
		else if (fullString.equals("admin_rec"))
		{
			GameObject target = activeChar.getTarget();
			Player player = null;
			if (target != null && target.isPlayer())
			{
				player = (Player) target;
			}
			else
			{
				return false;
			}
			player.setRecomHave(player.getRecomHave() + 1);
			player.sendMessage("You have been recommended by a GM");
			player.broadcastCharInfo();
		}
		else if (fullString.startsWith("admin_rec"))
		{
			try
			{
				String val = fullString.substring(10);
				int recVal = Integer.parseInt(val);
				GameObject target = activeChar.getTarget();
				Player player = null;
				if (target != null && target.isPlayer())
				{
					player = (Player) target;
				}
				else
				{
					return false;
				}
				player.setRecomHave(player.getRecomHave() + recVal);
				player.sendMessage("You have been recommended by a GM");
				player.broadcastCharInfo();
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Command format is //rec <number>");
			}
		}
		else if (fullString.startsWith("admin_sethero"))
		{
			// Статус меняется только на текущую логон сессию
			GameObject target = activeChar.getTarget();
			Player player;
			if (wordList.length > 1 && wordList[1] != null)
			{
				player = GameObjectsStorage.getPlayer(wordList[1]);
				if (player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					return false;
				}
			}
			else if (target != null && target.isPlayer())
			{
				player = (Player) target;
			}
			else
			{
				activeChar.sendMessage("You must specify the name or target character.");
				return false;
			}

			if (player.isHero() || player.isFakeHero())
			{
				player.setHero(false);
				player.updatePledgeClass();
				player.removeSkill(SkillTable.getInstance().getInfo(395, 1));
				player.removeSkill(SkillTable.getInstance().getInfo(396, 1));
				player.removeSkill(SkillTable.getInstance().getInfo(1374, 1));
				player.removeSkill(SkillTable.getInstance().getInfo(1375, 1));
				player.removeSkill(SkillTable.getInstance().getInfo(1376, 1));
			}
			else
			{
				player.setHero(true);
				player.updatePledgeClass();
				player.addSkill(SkillTable.getInstance().getInfo(395, 1));
				player.addSkill(SkillTable.getInstance().getInfo(396, 1));
				player.addSkill(SkillTable.getInstance().getInfo(1374, 1));
				player.addSkill(SkillTable.getInstance().getInfo(1375, 1));
				player.addSkill(SkillTable.getInstance().getInfo(1376, 1));
			}

			player.sendPacket(new SkillList(player));

			player.sendMessage("Admin has changed your hero status.");
			player.broadcastUserInfo(true);
		}
		else if (fullString.startsWith("admin_setnoble"))
		{
			// Статус сохраняется в базе
			GameObject target = activeChar.getTarget();
			Player player;
			if (wordList.length > 1 && wordList[1] != null)
			{
				player = GameObjectsStorage.getPlayer(wordList[1]);
				if (player == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
					return false;
				}
			}
			else if (target != null && target.isPlayer())
			{
				player = (Player) target;
			}
			else
			{
				activeChar.sendMessage("You must specify the name or target character.");
				return false;
			}

			// Synerge - Only players with 3rd class can be nobles
			if (player.getClassId().getLevel() < 4)
			{
				activeChar.sendMessage("The target must be 3rd class in order to be noble");
				return false;
			}

			if (player.isNoble())
			{
				Olympiad.removeNoble(player);
				player.setNoble(false);
				player.sendMessage("Admin changed your noble status, now you are not nobless.");
			}
			else
			{
				Olympiad.addNoble(player);
				player.setNoble(true);
				player.sendMessage("Admin changed your noble status, now you are Nobless.");
			}

			player.updatePledgeClass();
			player.updateNobleSkills();
			player.sendPacket(new SkillList(player));
			player.broadcastUserInfo(true);
		}
		else if (fullString.startsWith("admin_setsex"))
		{
			GameObject target = activeChar.getTarget();
			Player player = null;
			if (target != null && target.isPlayer())
			{
				player = (Player) target;
			}
			else
			{
				return false;
			}

			player.changeSex();
			player.setTransformation(251);
			player.sendMessage("Your gender has been changed by a GM");
			player.broadcastUserInfo(true);
			player.setTransformation(0);
		}
		else if (fullString.startsWith("admin_setcolor"))
		{
			try
			{
				String val = fullString.substring(15);
				GameObject target = activeChar.getTarget();
				Player player = null;
				if (target != null && target.isPlayer())
				{
					player = (Player) target;
				}
				else
				{
					return false;
				}
				player.setNameColor(Integer.decode("0x" + val));
				player.sendMessage("Your name color has been changed by a GM");
				player.broadcastUserInfo(true);
			}
			catch (StringIndexOutOfBoundsException e)
			{ // Case of empty color
				activeChar.sendMessage("You need to specify the new color.");
			}
		}
		else if (fullString.startsWith("admin_add_exp_sp_to_character"))
		{
			addExpSp(activeChar);
		}
		else if (fullString.startsWith("admin_add_exp_sp"))
		{
			try
			{
				final String val = fullString.substring(16).trim();

				// String[] vals = val.split(" ");
				// long exp = NumberUtils.toLong(vals[0], 0L);
				// int sp = vals.length > 1 ? NumberUtils.toInt(vals[1], 0) : 0;

				adminAddExpSp(activeChar, val);
				// adminAddExpSp(activeChar, exp, sp);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //add_exp_sp <exp> <sp>");
			}
		}
		else if (fullString.startsWith("admin_trans"))
		{
			StringTokenizer st = new StringTokenizer(fullString);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				int transformId = 0;
				try
				{
					transformId = Integer.parseInt(st.nextToken());
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Specify a valid integer value.");
					return false;
				}
				if (transformId != 0 && activeChar.getTransformation() != 0)
				{
					activeChar.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
					return false;
				}
				activeChar.setTransformation(transformId);
				activeChar.sendMessage("Transforming...");
			}
			else
			{
				activeChar.sendMessage("Usage: //trans <ID>");
			}
		}
		else if (fullString.startsWith("admin_setsubclass"))
		{
			final GameObject target = activeChar.getTarget();
			if (target == null || !target.isPlayer())
			{
				activeChar.sendPacket(SystemMsg.SELECT_TARGET);
				return false;
			}
			final Player player = (Player) target;

			StringTokenizer st = new StringTokenizer(fullString);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				int classId = Short.parseShort(st.nextToken());
				if (!player.addSubClass(classId, true, 0))
				{
					activeChar.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded", activeChar));
					return false;
				}
				player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER); // Transfer to new class.
			}
			else
			{
				setSubclass(activeChar, player);
			}
		}
		else if (fullString.startsWith("admin_setfame"))
		{
			try
			{
				String val = fullString.substring(14);
				int fame = Integer.parseInt(val);
				setTargetFame(activeChar, fame);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify new fame value.");
			}
		}
		else if (fullString.startsWith("admin_addfame"))
		{
			try
			{
				String val = fullString.substring(14);
				int fame = Integer.parseInt(val);
				addTargetFame(activeChar, fame);
			}
			catch (StringIndexOutOfBoundsException e)
			{
				activeChar.sendMessage("Please specify how much fame you wanna add.");
			}
		}
		else if (fullString.startsWith("admin_setbday"))
		{
			String msgUsage = "Usage: //setbday YYYY-MM-DD";
			String date = fullString.substring(14);
			if (date.length() != 10 || !Util.isMatchingRegexp(date, "[0-9]{4}-[0-9]{2}-[0-9]{2}"))
			{
				activeChar.sendMessage(msgUsage);
				return false;
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			try
			{
				dateFormat.parse(date);
			}
			catch (ParseException e)
			{
				activeChar.sendMessage(msgUsage);
			}

			if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendMessage("Please select a character.");
				return false;
			}

			if (!mysql.set("update characters set createtime = UNIX_TIMESTAMP('" + date + "') where obj_Id = " + activeChar.getTarget().getObjectId()))
			{
				activeChar.sendMessage(msgUsage);
				return false;
			}

			activeChar.sendMessage("New Birthday for " + activeChar.getTarget().getName() + ": " + date);
			activeChar.getTarget().getPlayer().sendMessage("Admin changed your birthday to: " + date);
		}
		else if (fullString.startsWith("admin_setpassword"))
		{
			if (wordList.length != 3)
			{
				activeChar.sendMessage("Usage: //setpassword <AccountName> <NewPassword>");
				return false;
			}

			AuthServerCommunication.getInstance().sendPacket(new ChangePassword(wordList[1], "", wordList[2], "null"));
			activeChar.sendMessage(wordList[1] + "'s password has been changed to " + wordList[2] + ".");
		}
		else if (fullString.startsWith("admin_setsecondary"))
		{
			if (wordList.length != 3)
			{
				activeChar.sendMessage("//setsecondary <AccountName> <Password>");
				return false;
			}

			String accountName = wordList[1];
			String newpass = wordList[2];

			if (!newpass.isEmpty() && !CCPSecondaryPassword.validatePassword(newpass))
			{
				activeChar.sendMessage("Invalid security password");
				return false;
			}

			CCPSecondaryPassword.setSecondaryPassword(accountName, newpass);
			if (newpass.isEmpty())
			{
				activeChar.sendMessage("You have removed security pass for account " + accountName);
			}
			else
			{
				activeChar.sendMessage("You have changed the security pass of account " + accountName + " to " + newpass);
			}
		}
		else if (fullString.startsWith("admin_give_item"))
		{
			if (wordList.length < 3)
			{
				activeChar.sendMessage("Usage: //give_item id count <target>");
				return false;
			}
			int id = Integer.parseInt(wordList[1]);
			int count = Integer.parseInt(wordList[2]);
			if (id < 1 || count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendMessage("Usage: //give_item id count <target>");
				return false;
			}
			ItemFunctions.addItem(activeChar.getTarget().getPlayer(), id, count, true, "admin_give_item");
		}
		else if (fullString.startsWith("admin_add_bang"))
		{
			if (!Config.ALT_PCBANG_POINTS_ENABLED)
			{
				activeChar.sendMessage("Error! Pc Bang Points service disabled!");
				return true;
			}
			if (wordList.length < 1)
			{
				activeChar.sendMessage("Usage: //add_bang count <target>");
				return false;
			}
			int count = Integer.parseInt(wordList[1]);
			if (count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendMessage("Usage: //add_bang count <target>");
				return false;
			}
			Player target = activeChar.getTarget().getPlayer();
			target.addPcBangPoints(count, false);
			activeChar.sendMessage("You have added " + count + " Pc Bang Points to " + target.getName());
		}
		else if (fullString.startsWith("admin_set_bang"))
		{
			if (!Config.ALT_PCBANG_POINTS_ENABLED)
			{
				activeChar.sendMessage("Error! Pc Bang Points service disabled!");
				return true;
			}
			if (wordList.length < 1)
			{
				activeChar.sendMessage("Usage: //set_bang count <target>");
				return false;
			}
			int count = Integer.parseInt(wordList[1]);
			if (count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer())
			{
				activeChar.sendMessage("Usage: //set_bang count <target>");
				return false;
			}
			Player target = activeChar.getTarget().getPlayer();
			target.setPcBangPoints(count);
			target.sendMessage("Your Pc Bang Points count is now " + count);
			target.sendPacket(new ExPCCafePointInfo(target, count, 1, 2, 12));
			activeChar.sendMessage("You have set " + target.getName() + "'s Pc Bang Points to " + count);
		}
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void listCharacters(Player activeChar, int page)
	{
		List<Player> players = GameObjectsStorage.getAllPlayers();

		int MaxCharactersPerPage = 20;
		int MaxPages = players.size() / MaxCharactersPerPage;

		if (players.size() > MaxCharactersPerPage * MaxPages)
		{
			MaxPages++;
		}

		// Check if number of users changed
		if (page > MaxPages)
		{
			page = MaxPages;
		}

		int CharactersStart = MaxCharactersPerPage * page;
		int CharactersEnd = players.size();
		if (CharactersEnd - CharactersStart > MaxCharactersPerPage)
		{
			CharactersEnd = CharactersStart + MaxCharactersPerPage;
		}

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center></center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=180><center><font name=hs12 color=AADD77>Character Selection Menu</font></center></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<table width=270>");
		replyMSG.append("<tr><td width=270>Find Character</td></tr>");
		replyMSG.append("<tr><td><br></td></tr>");
		replyMSG.append("<tr><td width=270>Note: Names should be written case sensitive.</td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<center><table><tr><td>");
		replyMSG.append("<edit var=\"character_name\" width=120></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("</td></tr></table></center><br><br>");

		for (int x = 0; x < MaxPages; x++)
		{
			int pagenr = x + 1;
			replyMSG.append("<center><a action=\"bypass -h admin_show_characters " + x + "\">Page " + pagenr + "</a></center>");
		}
		replyMSG.append("<br>");

		// List Players in a Table
		replyMSG.append("<table width=270>");
		replyMSG.append("<tr><td width=80>Name:</td><td width=110>Class:</td><td width=40>Level:</td></tr>");
		for (int i = CharactersStart; i < CharactersEnd; i++)
		{
			Player p = players.get(i);
			replyMSG.append("<tr><td width=80>" + "<a action=\"bypass -h admin_character_list " + p.getName() + "\">" + p.getName() + "</a></td><td width=110>" + p.getTemplate().className + "</td><td width=40>" + p.getLevel() + "</td></tr>");
		}
		replyMSG.append("</table>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	public static void showCharacterList(Player activeChar, Player player)
	{
		if (player == null)
		{
			final GameObject target = activeChar.getTarget();
			if (target != null && target.isPlayer())
			{
				player = (Player) target;
			}
			else
			{
				return;
			}
		}
		else
		{
			activeChar.setTarget(player);
		}

		String clanName = "No Clan";
		if (player.getClan() != null)
		{
			clanName = player.getClan().getName() + "/" + player.getClan().getLevel();
		}

		NumberFormat df = NumberFormat.getNumberInstance(Locale.ENGLISH);
		df.setMaximumFractionDigits(4);
		df.setMinimumFractionDigits(1);

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html noscrollbar><body>");
		replyMSG.append("<table width=290><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table><br>");
		replyMSG.append("<table width=290><tr>");
		replyMSG.append("<td width=180><center><font name=hs12 color=LEVEL>Character Selection Menu</font></center></td>");
		replyMSG.append("</tr></table><br>");
		replyMSG.append("<table width=270><tr>");
		replyMSG.append("<td><button value=\"Go To\" action=\"bypass -h admin_goto_char_menu " + player.getName() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Recall\" action=\"bypass -h admin_recall_char_menu " + player.getName() + "\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Set Noble\" action=\"bypass -h admin_setnoble\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr><tr>");
		replyMSG.append("<td><button value=\"Skills\" action=\"bypass -h admin_show_skills\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Effects\" action=\"bypass -h admin_show_effects\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Quests\" action=\"bypass -h admin_quests\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr><tr>");
		replyMSG.append("<td><button value=\"Stats\" action=\"bypass -h admin_edit_character\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Exp & Sp\" action=\"bypass -h admin_add_exp_sp_to_character\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Class\" action=\"bypass -h admin_show_html setclass.htm\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br>");
		replyMSG.append("<table width=290>");
		replyMSG.append("<tr><td width=100>Account/IP: </td><td><a action=\"bypass -h admin_find_char_acc " + player.getName() + "\">" + player.getAccountName() + "</a> / <a action=\"bypass -h admin_find_ip " + player.getIP() + "\">" + player.getIP() + "</a></td></tr>");
		replyMSG.append("<tr><td width=100>HWID: </td><td><a action=\"bypass -h admin_find_hwid " + player.getHWID() + "\">" + (player.hasHWID() ? player.getHWID() : "hwid missing") + "</a></td></tr>");
		replyMSG.append("<tr><td width=100>Country: </td><td>" + GeoLocation.getInstance().getCountry(player) + " (" + GeoLocation.getInstance().getCountryCode(player) + ")</td></tr>");
		replyMSG.append("<tr><td width=100>City: </td><td>" + GeoLocation.getInstance().getCity(player) + " (" + GeoLocation.getInstance().getCityRegion(player) + ")</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Name:</font></td><td>" + player.getName() + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Level:</font></td><td>" + player.getLevel() + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Class:</font></td><td>" + player.getTemplate().className + "</td></tr>");
		// replyMSG.append("<tr><td width=100><font color=0099FF>Class Id:</font></td><td>" + player.getClassId().getId() + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Clan|Level:</font></td><td>" + clanName + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Patk:</font></td><td>" + player.getPAtk(null) + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Matk:</font></td><td>" + player.getMAtk(null, null) + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Pdef:</font></td><td>" + player.getPDef(null) + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Mdef:</font></td><td>" + player.getMDef(null, null) + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>PAtkSpd:</font></td><td>" + player.getPAtkSpd() + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>MAtkSpd:</font></td><td>" + player.getMAtkSpd() + "</td></tr>");
		// replyMSG.append("<tr><td width=100><font color=0099FF>Acc/Evas:</font></td><td>" + player.getAccuracy() + "/" + player.getEvasionRate(null) + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Crit/MCrit:</font></td><td>" + player.getCriticalHit(null, null) + "/" + df.format(player.getMagicCriticalRate(null, null)) + "%</td></tr>");
		// replyMSG.append("<tr><td width=100><font color=0099FF>Walk/Run:</font></td><td>" + player.getWalkSpeed() + "/" + player.getRunSpeed() + "</td></tr>");
		replyMSG.append("<tr><td width=100><font color=0099FF>Karma/Fame:</font></td><td>" + player.getKarma() + "/" + player.getFame() + "</td></tr>");
		// replyMSG.append("<tr><td width=100><font color=0099FF>PvP/PK:</font></td><td>" + player.getPvpKills() + "/" + player.getPkKills() + "</td></tr>");
		// replyMSG.append("<tr><td width=100><font color=0099FF>Coordinates:</font></td><td>" + player.getX() + "," + player.getY() + "," + player.getZ() + "</td></tr>");
		// replyMSG.append("<tr><td width=100><font color=0099FF>Direction:</font></td><td>" + PositionUtils.getDirectionTo(player, activeChar) + "</td></tr>");
		replyMSG.append("</table></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void setTargetKarma(Player activeChar, int newKarma)
	{
		GameObject target = activeChar.getTarget();
		if (target == null)
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Player player;
		if (target.isPlayer())
		{
			player = (Player) target;
		}
		else
		{
			return;
		}

		if (newKarma >= 0)
		{
			int oldKarma = player.getKarma();
			player.setKarma(newKarma);

			player.sendMessage("Admin has changed your karma from " + oldKarma + " to " + newKarma + ".");
			activeChar.sendMessage("Successfully Changed karma for " + player.getName() + " from (" + oldKarma + ") to (" + newKarma + ").");
		}
		else
		{
			activeChar.sendMessage("You must enter a value for karma greater than or equal to 0.");
		}
	}

	private void setTargetFame(Player activeChar, int newFame)
	{
		GameObject target = activeChar.getTarget();
		if (target == null)
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Player player;
		if (target.isPlayer())
		{
			player = (Player) target;
		}
		else
		{
			return;
		}

		if (newFame >= 0)
		{
			int oldFame = player.getFame();
			player.setFame(newFame, "Admin");

			player.sendMessage("Admin has changed your fame from " + oldFame + " to " + newFame + ".");
			activeChar.sendMessage("Successfully Changed fame for " + player.getName() + " from (" + oldFame + ") to (" + newFame + ").");
		}
		else
		{
			activeChar.sendMessage("You must enter a value for fame greater than or equal to 0.");
		}
	}

	private void addTargetFame(Player activeChar, int addFame)
	{
		GameObject target = activeChar.getTarget();
		if (target == null)
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Player player;
		if (target.isPlayer())
		{
			player = (Player) target;
		}
		else
		{
			return;
		}

		int oldFame = player.getFame();
		player.setFame(oldFame + addFame, "Admin");
		player.sendMessage("Admin has given you " + addFame + " fame.");
		activeChar.sendMessage("Successfully Changed fame for " + player.getName() + " from (" + oldFame + ") to (" + (oldFame + addFame) + "). Fame added: " + addFame);
	}

	private void adminModifyCharacter(Player activeChar, String modifications)
	{
		GameObject target = activeChar.getTarget();
		if (target == null || !target.isPlayer())
		{
			activeChar.sendPacket(SystemMsg.SELECT_TARGET);
			return;
		}

		Player player = (Player) target;
		String[] strvals = modifications.split("&");
		Integer[] vals = new Integer[strvals.length];
		for (int i = 0; i < strvals.length; i++)
		{
			strvals[i] = strvals[i].trim();
			vals[i] = strvals[i].isEmpty() ? null : Integer.valueOf(strvals[i]);
		}

		if (vals[0] != null)
		{
			player.setCurrentHp(vals[0], false);
		}

		if (vals[1] != null)
		{
			player.setCurrentMp(vals[1]);
		}

		if (vals[2] != null)
		{
			player.setKarma(vals[2]);
		}

		if (vals[3] != null)
		{
			player.setPvpFlag(vals[3]);
		}

		if (vals[4] != null)
		{
			player.setPvpKills(vals[4]);
		}

		if (vals[5] != null)
		{
			player.setClassId(vals[5], true, false);
		}

		editCharacter(activeChar); // Back to start
		player.broadcastCharInfo();
		player.decayMe();
		player.spawnMe(activeChar.getLoc());
	}

	private void editCharacter(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		if (target == null || !target.isPlayer())
		{
			activeChar.sendPacket(SystemMsg.SELECT_TARGET);
			return;
		}

		Player player = (Player) target;
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
		replyMSG.append("<br><br>");
		replyMSG.append("<table border=0 width=290>");
		replyMSG.append("<tr><td width=40></td><td width=70>Curent:</td><td width=70>Max:</td><td width=70></td></tr>");
		replyMSG.append("<tr><td width=40>HP:</td><td width=70>" + player.getCurrentHp() + "</td><td width=70>" + player.getMaxHp() + "</td><td width=70>Karma: " + player.getKarma() + "</td></tr>");
		replyMSG.append("<tr<td width=40>MP:</td><td width=70>" + player.getCurrentMp() + "</td><td width=70>" + player.getMaxMp() + "</td><td width=70>Pvp Kills: " + player.getPvpKills() + "</td></tr>");
		replyMSG.append("<tr><td width=40>Load:</td><td width=70>" + player.getCurrentLoad() + "</td><td width=70>" + player.getMaxLoad() + "</td><td width=70>Pvp Flag: " + player.getPvpFlag() + "</td></tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br>");
		replyMSG.append("<table width=290><tr><td><font name=\"hs12\" color=\"00FF00\">" + player.getName() + "</font> " + player.getClassId() + " and ClassID: " + player.getClassId().getId() + "</td></tr></table><br>");
		replyMSG.append("<table width=290>");
		replyMSG.append("<tr><td>Note: Fill all values before saving!</td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<table width=290>");
		replyMSG.append("<tr><td width=50>Hp:</td><td><edit var=\"hp\" width=50></td><td width=50>Mp:</td><td><edit var=\"mp\" width=50></td></tr>");
		replyMSG.append("<tr><td width=50>Pvp Flag:</td><td><edit var=\"pvpflag\" width=50></td><td width=50>Karma:</td><td><edit var=\"karma\" width=50></td></tr>");
		replyMSG.append("<tr><td width=50>Class Id:</td><td><edit var=\"classid\" width=50></td><td width=50>Pvp Kills:</td><td><edit var=\"pvpkills\" width=50></td></tr>");
		replyMSG.append("</table><br>");
		replyMSG.append("<table><tr><td align=center><button value=\"Save Changes\" action=\"bypass -h admin_save_modifications $hp & $mp & $karma & $pvpflag & $pvpkills & $classid &\" width=200 height=23 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table>");
		replyMSG.append("</td></tr>");
		replyMSG.append("</table></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showCharacterActions(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player;
		if (target != null && target.isPlayer())
		{
			player = (Player) target;
		}
		else
		{
			return;
		}

		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table><br><br>");
		replyMSG.append("<center>Admin Actions for: " + player.getName() + "</center><br>");
		replyMSG.append("<center><table width=200><tr>");
		replyMSG.append("<td width=100>Argument(*):</td><td width=100><edit var=\"arg\" width=100></td>");
		replyMSG.append("</tr></table><br></center>");
		replyMSG.append("<table width=270>");

		replyMSG.append("<tr><td width=90><button value=\"Teleport\" action=\"bypass -h admin_teleportto " + player.getName() + "\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=90><button value=\"Recall\" action=\"bypass -h admin_recall " + player.getName() + "\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=90><button value=\"Quests\" action=\"bypass -h admin_quests " + player.getName() + "\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void findCharacter(Player activeChar, String CharacterToFind)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		int CharactersFound = 0;

		StringBuilder replyMSG = new StringBuilder("<html><body>");
		replyMSG.append("<table width=260><tr>");
		replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
		replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table>");
		replyMSG.append("<br><br>");

		for (Player element : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (StringUtils.containsIgnoreCase(element.getName(), CharacterToFind))
			{
				CharactersFound = CharactersFound + 1;
				replyMSG.append("<table width=270>");
				replyMSG.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
				replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_list " + element.getName() + "\">" + element.getName() + "</a></td><td width=110>" + element.getTemplate().className + "</td><td width=40>" + element.getLevel() + "</td></tr>");
				replyMSG.append("</table>");
			}
		}

		if (CharactersFound == 0)
		{
			replyMSG.append("<table width=270>");
			replyMSG.append("<tr><td width=270>Your search did not find any characters.</td></tr>");
			replyMSG.append("<tr><td width=270>Please try again.<br></td></tr>");
			replyMSG.append("</table><br>");
			replyMSG.append("<center><table><tr><td>");
			replyMSG.append("<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
			replyMSG.append("</td></tr></table></center>");
		}
		else
		{
			replyMSG.append("<center><br>Found " + CharactersFound + " character");

			if (CharactersFound == 1)
			{
				replyMSG.append(".");
			}
			else if (CharactersFound > 1)
			{
				replyMSG.append("s.");
			}
		}

		replyMSG.append("</center></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void addExpSp(Player activeChar)
	{
		final GameObject target = activeChar.getTarget();
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

		final NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		final StringBuilder replyMSG = new StringBuilder("<html noscrollbar><body><title>Edit Character</title>");
		replyMSG.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
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
		replyMSG.append("<table cellpadding=0 cellspacing=-2 width=260>");
		replyMSG.append("<tr>");
		replyMSG.append("<td align=center><font name=\"hs12\" color=\"LEVEL\">Edit Player:</font></td>");
		replyMSG.append("<td align=center><font name=\"hs12\" color=\"00FF00\">" + player.getName() + "</font></td>");
		replyMSG.append("</tr>");
		replyMSG.append("<tr><td><br></td><td><br></td></tr>");
		replyMSG.append("<tr>");
		replyMSG.append("<td align=center><font color=\"LEVEL\">LeveL:" + player.getLevel() + "</font></td>");
		replyMSG.append("<td align=center><font color=\"LEVEL\">Class:" + player.getTemplate().className + "</font></td>");
		replyMSG.append("</tr>");
		replyMSG.append("</table>");
		replyMSG.append("<br><br>");
		replyMSG.append("<table cellpadding=0 cellspacing=-2 width=260><tr><td align=center>Note: Fill all values before saving!</td></tr>");
		replyMSG.append("<tr><td align=center>Note: Use 0 if no changes are needed.</td></tr></table><br>");
		replyMSG.append("<center><table><tr>");
		replyMSG.append("<td>Exp: <edit var=\"exp_to_add\" width=200><br></td></tr>");
		replyMSG.append("<tr><td>Sp:  <edit var=\"sp_to_add\" width=200></td></tr>");
		replyMSG.append("<tr><td align=center><button value=\"Save Changes\" action=\"bypass -h admin_add_exp_sp $exp_to_add & $sp_to_add &\" width=200 height=23 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table></center>");
		replyMSG.append("<center><table><tr>");
		replyMSG.append("<td>Set Character LeveL: <edit var=\"lvl\" width=200></td></tr>");
		replyMSG.append("<tr><td><button value=\"Set Level\" action=\"bypass -h admin_setlevel $lvl\" width=200 height=23 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("</tr></table></center>");
		replyMSG.append("</td></tr>");
		replyMSG.append("</table></body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void adminAddExpSp(Player activeChar, String ExpSp)
	{
		if (!activeChar.getPlayerAccess().CanEditCharAll)
		{
			activeChar.sendMessage("You have not enough privileges, for use this function.");
			return;
		}

		final GameObject target = activeChar.getTarget();
		if (target == null)
		{
			activeChar.sendPacket(SystemMsg.SELECT_TARGET);
			return;
		}

		if (!target.isPlayable())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}

		Player player = (Player) target;
		String[] strvals = ExpSp.split("&");
		long[] vals = new long[strvals.length];
		for (int i = 0; i < strvals.length; i++)
		{
			strvals[i] = strvals[i].trim();
			vals[i] = strvals[i].isEmpty() ? 0 : Long.parseLong(strvals[i]);
		}

		player.addExpAndSp(vals[0], vals[1], 0, 0, false, false);
		player.sendMessage("Admin is adding you " + vals[0] + " exp and " + vals[1] + " SP.");
		activeChar.sendMessage("Added " + vals[0] + " exp and " + vals[1] + " SP to " + player.getName() + ".");
	}

	private void setSubclass(Player activeChar, Player player)
	{
		StringBuilder content = new StringBuilder("<html><body>");
		NpcHtmlMessage html = new NpcHtmlMessage(5);
		Set<PlayerClass> subsAvailable;
		subsAvailable = getAvailableSubClasses(player);

		if (subsAvailable != null && !subsAvailable.isEmpty())
		{
			content.append("Add Subclass:<br>Which subclass do you wish to add?<br>");

			for (PlayerClass subClass : subsAvailable)
			{
				content.append("<a action=\"bypass -h admin_setsubclass " + subClass.ordinal() + "\">" + formatClassForDisplay(subClass) + "</a><br>");
			}
		}
		else
		{
			activeChar.sendMessage(new CustomMessage("l2f.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime", activeChar));
			return;
		}
		content.append("</body></html>");
		html.setHtml(content.toString());
		activeChar.sendPacket(html);
	}

	private Set<PlayerClass> getAvailableSubClasses(Player player)
	{
		final int charClassId = player.getBaseClassId();

		PlayerClass currClass = PlayerClass.values()[charClassId];// .valueOf(charClassName);

		/**
		 * If the race of your main class is Elf or Dark Elf, you may not select
		 * each class as a subclass to the other class, and you may not select
		 * Overlord and Warsmith class as a subclass.
		 *
		 * You may not select a similar class as the subclass. The occupations
		 * classified as similar classes are as follows:
		 *
		 * Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger
		 * and Phantom Ranger Paladin, Dark Avenger, Temple Knight and Shillien
		 * Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and
		 * Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and
		 * Spellhowler
		 *
		 * Kamael могут брать только сабы Kamael
		 * Другие классы не могут брать сабы Kamael
		 *
		 */
		Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
		if (availSubs == null)
		{
			return null;
		}

		// Из списка сабов удаляем мейн класс игрока
		availSubs.remove(currClass);

		for (PlayerClass availSub : availSubs)
		{
			// Удаляем из списка возможных сабов, уже взятые сабы и их предков
			for (SubClass subClass : player.getSubClasses().values())
			{
				if (availSub.ordinal() == subClass.getClassId())
				{
					availSubs.remove(availSub);
					continue;
				}

				// Удаляем из возможных сабов их родителей, если таковые есть у чара
				ClassId parent = ClassId.VALUES[availSub.ordinal()].getParent(player.getSex());
				if (parent != null && parent.getId() == subClass.getClassId())
				{
					availSubs.remove(availSub);
					continue;
				}

				// Удаляем из возможных сабов родителей текущих сабклассов, иначе если взять саб berserker
				// и довести до 3ей профы - doombringer, игроку будет предложен berserker вновь (дежавю)
				ClassId subParent = ClassId.VALUES[subClass.getClassId()].getParent(player.getSex());
				if (subParent != null && subParent.getId() == availSub.ordinal())
				{
					availSubs.remove(availSub);
				}
			}

			// Особенности саб классов камаэль
			if (availSub.isOfRace(Race.kamael))
			{
				// Для Soulbreaker-а и SoulHound не предлагаем Soulbreaker-а другого пола
				if ((currClass == PlayerClass.MaleSoulHound || currClass == PlayerClass.FemaleSoulHound || currClass == PlayerClass.FemaleSoulbreaker || currClass == PlayerClass.MaleSoulbreaker) && (availSub == PlayerClass.FemaleSoulbreaker || availSub == PlayerClass.MaleSoulbreaker))
				{
					availSubs.remove(availSub);
				}

				// Для Berserker(doombringer) и Arbalester(trickster) предлагаем Soulbreaker-а только своего пола
				if (currClass == PlayerClass.Berserker || currClass == PlayerClass.Doombringer || currClass == PlayerClass.Arbalester || currClass == PlayerClass.Trickster)
				{
					if (player.getSex() == 1 && availSub == PlayerClass.MaleSoulbreaker || player.getSex() == 0 && availSub == PlayerClass.FemaleSoulbreaker)
					{
						availSubs.remove(availSub);
					}
				}

				// Inspector доступен, только когда вкачаны 2 возможных первых саба камаэль(+ мейн класс):
				// doombringer(berserker), soulhound(maleSoulbreaker, femaleSoulbreaker), trickster(arbalester)
				if (availSub == PlayerClass.Inspector)
				{
					// doombringer(berserker)
					if (!(player.getSubClasses().containsKey(131) || player.getSubClasses().containsKey(127)))
					{
						availSubs.remove(availSub);
					}
					else if (!(player.getSubClasses().containsKey(132) || player.getSubClasses().containsKey(133) || player.getSubClasses().containsKey(128) || player.getSubClasses().containsKey(129)))
					{
						availSubs.remove(availSub);
					}
					else if (!(player.getSubClasses().containsKey(134) || player.getSubClasses().containsKey(130)))
					{
						availSubs.remove(availSub);
					}
				}
			}
		}
		return availSubs;
	}

	private String formatClassForDisplay(PlayerClass className)
	{
		String classNameStr = className.toString();
		char[] charArray = classNameStr.toCharArray();

		for (int i = 1; i < charArray.length; i++)
		{
			if (Character.isUpperCase(charArray[i]))
			{
				classNameStr = classNameStr.substring(0, i) + " " + classNameStr.substring(i);
			}
		}

		return classNameStr;
	}
}