package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.database.mysql;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SubClass;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.base.PlayerClass;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.model.entity.olympiad.Olympiad;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.SubUnit;
import l2f.gameserver.network.clientpackets.CharacterCreate;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.tables.ClanTable;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.Util;

public class Rename extends Functions
{
	public void rename_page()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_NICK_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		player.sendPacket(new NpcHtmlMessage(5).setFile("scripts/services/NameChange/index.htm"));
	}

	public void changesex_page()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_SEX_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (!player.isInPeaceZone())
		{
			show("You must be in peace zone to use this service.", player);
			return;
		}

		String htmlsex = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/Donate/changesex.htm", player);
		// player.sendPacket(new NpcHtmlMessage(5).setFile("custom/changesex.htm"));

		String itemName = ItemHolder.getInstance().getTemplate(Config.SERVICES_CHANGE_SEX_ITEM).getName();
		String cost = Util.formatAdena(Config.SERVICES_CHANGE_SEX_PRICE);

		htmlsex = htmlsex.replace("%item%", itemName);
		htmlsex = htmlsex.replace("%cost%", cost);
		htmlsex = htmlsex.replace("%playerName%", player.getName());
		htmlsex = htmlsex.replace("%playersex%", player.getSex() == 1 ? "Female" : "Male");

		show(htmlsex, player);
	}

	public void separate_page(Player player, String newName)
	{
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_SEPARATE_SUB_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (player.isHero())
		{
			show("Not available for heroes.", player);
			return;
		}

		if (player.getSubClasses().size() == 1)
		{
			show("You must have at least one subclass.", player);
			return;
		}

		if (!player.getActiveClass().isBase())
		{
			show("You must be on the main class.", player);
			return;
		}

		if (player.getEvent(SiegeEvent.class) != null)
		{
			player.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow", player));
			return;
		}

		if (!CharacterCreate.checkName(newName) && !Config.SERVICES_CHANGE_NICK_ALLOW_SYMBOL)
		{
			player.sendMessage(new CustomMessage("scripts.services.Rename.incorrectinput", player));
			return;
		}
		if (player.getActiveClass().getLevel() < 75)
		{
			show("You must have 75 sub-class level.", player);
			return;
		}

		String append = "Department subclass:";
		append += "<br>";
		append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.Separate.Price", player).addString(Util.formatAdena(Config.SERVICES_SEPARATE_SUB_PRICE)).addItemName(Config.SERVICES_SEPARATE_SUB_ITEM) + "</font>&nbsp;";
		append += "<edit var=\"name\" width=80 height=15 /><br>";
		append += "<table>";

		for (SubClass s : player.getSubClasses().values())
		{
			if (!s.isBase() && s.getClassId() != ClassId.inspector.getId() && s.getClassId() != ClassId.judicator.getId())
			{
				append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.Separate.Button", player).addString(ClassId.VALUES[s.getClassId()].toString()) + "\" action=\"bypass -h scripts_services.Rename:separate " + s.getClassId() + " $name\" width=200 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";
			}
		}

		append += "</table>";
		show(append, player);
	}

	public void separate(String[] param)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_SEPARATE_SUB_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (player.isHero())
		{
			show("Not available for heroes.", player);
			return;
		}

		if (player.getSubClasses().size() == 1)
		{
			show("You must have at least one subclass.", player);
			return;
		}

		if (!player.getActiveClass().isBase())
		{
			show("You must be on the main class.", player);
			return;
		}

		if (player.getActiveClass().getLevel() < 75)
		{
			show("You must have 75 sub-class level.", player);
			return;
		}

		if (param.length < 2)
		{
			show("You must specify a target.", player);
			return;
		}

		if (getItemCount(player, Config.SERVICES_SEPARATE_SUB_ITEM) < Config.SERVICES_SEPARATE_SUB_PRICE)
		{
			if (Config.SERVICES_SEPARATE_SUB_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
			return;
		}

		int classtomove = Integer.parseInt(param[0]);
		int newcharid = 0;
		for (Entry<Integer, String> e : player.getAccountChars().entrySet())
		{
			if (e.getValue().equalsIgnoreCase(param[1]))
			{
				newcharid = e.getKey();
			}
		}

		if (newcharid == 0)
		{
			show("The purpose is not there.", player);
			return;
		}

		if (mysql.simple_get_int("level", "character_subclasses", "char_obj_id=" + newcharid + " AND level > 1") > 1)
		{
			show("The aim should be level 1.", player);
			return;
		}

		mysql.set("DELETE FROM character_subclasses WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_skills WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_skills_save WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_effects_save WHERE object_id=" + newcharid);
		mysql.set("DELETE FROM character_hennas WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_shortcuts WHERE char_obj_id=" + newcharid);
		mysql.set("DELETE FROM character_variables WHERE obj_id=" + newcharid);

		mysql.set("UPDATE character_subclasses SET char_obj_id=" + newcharid + ", isBase=1, certification=0 WHERE char_obj_id=" + player.getObjectId() + " AND class_id=" + classtomove);
		mysql.set("UPDATE character_skills SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_skills_save SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_effects_save SET object_id=" + newcharid + " WHERE object_id=" + player.getObjectId() + " AND id=" + classtomove);
		mysql.set("UPDATE character_hennas SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);
		mysql.set("UPDATE character_shortcuts SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.getObjectId() + " AND class_index=" + classtomove);

		mysql.set("UPDATE character_variables SET obj_id=" + newcharid + " WHERE obj_id=" + player.getObjectId() + " AND name like 'TransferSkills%'");

		player.modifySubClass(classtomove, 0);

		removeItem(player, Config.SERVICES_CHANGE_BASE_ITEM, Config.SERVICES_CHANGE_BASE_PRICE, "Rename$separate");
		player.logout();
		Log.add("Character " + player + " base changed to " + player, "services");
	}

	public void changebase_page()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_BASE_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (!player.isInPeaceZone())
		{
			show("You need to be in a peaceful area to use this service.", player);
			return;
		}

		if (player.isHero())
		{
			sendMessage("Not available for heroes.", player);
			return;
		}

		String append = "Changing the base class:";
		append += "<br>";
		append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.BaseChange.Price", player).addString(Util.formatAdena(Config.SERVICES_CHANGE_BASE_PRICE)).addItemName(Config.SERVICES_CHANGE_BASE_ITEM) + "</font>";
		append += "<table>";

		List<SubClass> possible = new ArrayList<SubClass>();
		if (player.getActiveClass().isBase())
		{
			possible.addAll(player.getSubClasses().values());
			possible.remove(player.getSubClasses().get(player.getBaseClassId()));

			for (SubClass s : player.getSubClasses().values())
			{
				for (SubClass s2 : player.getSubClasses().values())
				{
					if (s != s2 && !PlayerClass.areClassesComportable(PlayerClass.values()[s.getClassId()], PlayerClass.values()[s2.getClassId()]) || s2.getLevel() < 75)
					{
						possible.remove(s2);
					}
				}
			}
		}

		if (possible.isEmpty())
		{
			append += "<tr><td width=300>" + new CustomMessage("scripts.services.BaseChange.NotPossible", player) + "</td></tr>";
		}
		else
		{
			for (SubClass s : possible)
			{
				append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.BaseChange.Button", player).addString(ClassId.VALUES[s.getClassId()].toString()) + "\" action=\"bypass -h scripts_services.Rename:changebase " + s.getClassId() + "\" width=200 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";
			}
		}
		append += "</table>";
		show(append, player);
	}

	public void changebase(String[] param)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_BASE_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (!player.isInPeaceZone())
		{
			show("You need to be in a peaceful area to use this service.", player);
			return;
		}

		if (!player.getActiveClass().isBase())
		{
			show("You must be on the main class to use this service.", player);
			return;
		}

		if (player.isHero())
		{
			show("Not available for heroes.", player);
			return;
		}

		if (getItemCount(player, Config.SERVICES_CHANGE_BASE_ITEM) < Config.SERVICES_CHANGE_BASE_PRICE)
		{
			if (Config.SERVICES_CHANGE_BASE_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
			return;
		}

		int target = Integer.parseInt(param[0]);
		SubClass newBase = player.getSubClasses().get(target);

		player.getActiveClass().setBase(false);
		player.getActiveClass().setCertification(newBase.getCertification());

		newBase.setCertification(0);
		player.getActiveClass().setExp(player.getExp());
		player.checkSkills();

		newBase.setBase(true);

		player.setBaseClass(target);

		player.setHairColor(0);
		player.setHairStyle(0);
		player.setFace(0);
		Olympiad.unRegisterNoble(player);
		removeItem(player, Config.SERVICES_CHANGE_BASE_ITEM, Config.SERVICES_CHANGE_BASE_PRICE, "Rename$changeBase");
		player.logout();
		// Log.add("Character " + player + " base changed to " + target, "services");
	}

	public void rename()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		player.sendMessage("Incorrect name, try again!");
	}

	public void rename(String[] args)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!Config.SERVICES_CHANGE_NICK_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (player.isHero())
		{
			player.sendMessage("Not available for heroes!");
			return;
		}

		if (args.length != 1)
		{
			player.sendMessage("Incorrect name, try again!");
			return;
		}

		if (player.getEvent(SiegeEvent.class) != null)
		{
			player.sendMessage("Your name can't be changed while Siege is in progress!");
			return;
		}

		String name = args[0];

		if (name.isEmpty() || !Util.isMatchingRegexp(name, Config.CLAN_NAME_TEMPLATE))
		{
			player.sendMessage("Incorrect name, try again!");
			return;
		}

		if (name.length() > 16)
		{
			player.sendMessage("The name must have maximum 16 characters");
			return;
		}

		if (!CharacterCreate.checkName(name) && !Config.SERVICES_CHANGE_NICK_ALLOW_SYMBOL)
		{
			player.sendMessage("Incorrect name, try again!");
			return;
		}

		if (!player.getName().endsWith("V") && getItemCount(player, Config.SERVICES_CHANGE_NICK_ITEM) < Config.SERVICES_CHANGE_NICK_PRICE)
		{
			if (Config.SERVICES_CHANGE_NICK_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendMessage("You dont have " + Config.SERVICES_CHANGE_NICK_PRICE + " Donator Coins!");
			}
			return;
		}

		if (CharacterDAO.getInstance().getObjectIdByName(name) > 0)
		{
			player.sendMessage("Name allready exist, try another name!");
			return;
		}

		if (!StringUtils.endsWithIgnoreCase(player.getName(), "VADC"))
		{
			removeItem(player, Config.SERVICES_CHANGE_NICK_ITEM, Config.SERVICES_CHANGE_NICK_PRICE, "Rename$rename");
		}

		String oldName = player.getName();
		player.reName(name, true);
		player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Rename", "You changed name from " + oldName + " to " + name + "!"));
	}

	public void changesex()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_SEX_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (player.getRace() == Race.kamael)
		{
			player.sendMessage("Not available for Kamael.");
			return;
		}

		if (!player.isInPeaceZone())
		{
			player.sendMessage("You need to be in a peaceful area to use this service.");
			return;
		}

		if (getItemCount(player, Config.SERVICES_CHANGE_SEX_ITEM) < Config.SERVICES_CHANGE_SEX_PRICE)
		{
			player.sendChatMessage(player.getObjectId(), ChatType.PARTY.ordinal(), "Service", "Lack of items to use this function.");
			return;
		}

//		if (Functions.getItemCount(player, Config.SERVICES_CHANGE_SEX_ITEM) < Config.SERVICES_CHANGE_SEX_PRICE)
//		{
//			player.sendMessage("You don't have enough " + Config.DONATOR_NPC_ITEM_NAME +"." );
//			return;
//		}

		Connection con = null;
		PreparedStatement offline = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("UPDATE characters SET sex = ? WHERE obj_Id = ?");
			offline.setInt(1, player.getSex() == 1 ? 0 : 1);
			offline.setInt(2, player.getObjectId());
			offline.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
		finally
		{
			DbUtils.closeQuietly(con, offline);
		}

		Functions.removeItem(player, Config.SERVICES_CHANGE_SEX_ITEM, Config.SERVICES_CHANGE_SEX_PRICE, "Removed");
		player.changeSex();
		player.setTransformation(251);
		player.sendMessage("Your gender has been changed!");
		player.broadcastUserInfo(true);
		player.setTransformation(0);
	}

	public void rename_clan_page()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_CLAN_NAME_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}
	}

	public void rename_clan()
	{
		// Special Case
		rename_clan(new String[]
		{
			""
		});
	}

	public void rename_clan(String[] param)
	{
		Player player = getSelf();
		if (player == null || param == null || param.length == 0)
		{
			return;
		}

		if (!Config.SERVICES_CHANGE_CLAN_NAME_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (player.getClan() == null || !player.isClanLeader())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
			return;
		}

		if (player.getEvent(SiegeEvent.class) != null)
		{
			show(new CustomMessage("scripts.services.Rename.SiegeNow", player), player);
			return;
		}

		if (!Util.isMatchingRegexp(param[0], Config.CLAN_NAME_TEMPLATE))
		{
			player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
			return;
		}
		if (ClanTable.getInstance().getClanByName(param[0]) != null)
		{
			player.sendPacket(Msg.THIS_NAME_ALREADY_EXISTS);
			return;
		}

		if (getItemCount(player, Config.SERVICES_CHANGE_CLAN_NAME_ITEM) < Config.SERVICES_CHANGE_CLAN_NAME_PRICE)
		{
			if (Config.SERVICES_CHANGE_CLAN_NAME_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
			return;
		}
		show(new CustomMessage("scripts.services.Rename.changedname", player).addString(player.getClan().getName()).addString(param[0]), player);
		SubUnit sub = player.getClan().getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
		sub.setName(param[0], true);

		removeItem(player, Config.SERVICES_CHANGE_CLAN_NAME_ITEM, Config.SERVICES_CHANGE_CLAN_NAME_PRICE, "Rename$rename_clan");
		player.getClan().broadcastClanStatus(true, true, false);
		player.broadcastCharInfo();
		player.broadcastUserInfo(true);
		player.sendUserInfo(true);
	}
}