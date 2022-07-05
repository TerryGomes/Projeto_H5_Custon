package l2f.gameserver.handler.admincommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import l2f.commons.dbutils.DbUtils;
import l2f.commons.lang.ArrayUtils;
import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.listener.actor.player.OnAnswerListener;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone.ZoneType;
import l2f.gameserver.model.entity.olympiad.Olympiad;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.ConfirmDlg;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.Util;

public class AdminTeleport implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_show_moves, admin_show_moves_other, admin_show_teleport, admin_teleport_to_character, admin_teleportto, admin_teleport_to, admin_move_to, admin_moveto, admin_teleport, admin_teleport_character, admin_recall, admin_recallparty, admin_recallcc, admin_recallinstance, admin_recallserver, admin_recallserverforce, admin_walk, admin_recall_npc, admin_gonorth, admin_gosouth, admin_goeast, admin_goto, admin_gowest, admin_goup, admin_godown, admin_tele, admin_teleto, admin_tele_to,
		admin_instant_move, admin_tonpc, admin_to_npc, admin_toobject, admin_setref, admin_getref, admin_autorecall
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanTeleport)
		{
			return false;
		}

		switch (command)
		{
		case admin_show_moves:
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/teleports.htm"));
			break;
		case admin_show_moves_other:
			activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/tele/other.htm"));
			break;
		case admin_show_teleport:
			showTeleportCharWindow(activeChar);
			break;
		case admin_teleport_to_character:
			teleportToCharacter(activeChar, activeChar.getTarget());
			break;
		case admin_teleport_to:
		case admin_teleportto:
		case admin_goto:
			if (wordList.length < 2)
			{
				activeChar.sendMessage("USAGE: //teleportto charName");
				return false;
			}
			String chaName = Util.joinStrings(" ", wordList, 1);
			Player cha = GameObjectsStorage.getPlayer(chaName);
			if (cha == null)
			{
				activeChar.sendMessage("Player '" + chaName + "' not found in world");
				return false;
			}
			teleportToCharacter(activeChar, cha);
			break;
		case admin_move_to:
		case admin_moveto:
		case admin_teleport:
			if (wordList.length < 2)
			{
				activeChar.sendMessage("USAGE: //teleport x y z [ref]");
				return false;
			}
			activeChar.teleToLocation(Location.parseLoc(Util.joinStrings(" ", wordList, 1, 3)), (ArrayUtils.valid(wordList, 4) != null && !ArrayUtils.valid(wordList, 4).isEmpty() ? Integer.parseInt(wordList[4]) : 0));
			break;
		case admin_walk:
			if (wordList.length < 2)
			{
				activeChar.sendMessage("USAGE: //walk x y z");
				return false;
			}
			try
			{
				activeChar.moveToLocation(Location.parseLoc(Util.joinStrings(" ", wordList, 1)), 0, true);
			}
			catch (IllegalArgumentException e)
			{
				activeChar.sendMessage("USAGE: //walk x y z");
				return false;
			}
			break;
		case admin_gonorth:
		case admin_gosouth:
		case admin_goeast:
		case admin_gowest:
		case admin_goup:
		case admin_godown:
			int val = wordList.length < 2 ? 150 : Integer.parseInt(wordList[1]);
			int x = activeChar.getX();
			int y = activeChar.getY();
			int z = activeChar.getZ();
			if (command == Commands.admin_goup)
			{
				z += val;
			}
			else if (command == Commands.admin_godown)
			{
				z -= val;
			}
			else if (command == Commands.admin_goeast)
			{
				x += val;
			}
			else if (command == Commands.admin_gowest)
			{
				x -= val;
			}
			else if (command == Commands.admin_gosouth)
			{
				y += val;
			}
			else if (command == Commands.admin_gonorth)
			{
				y -= val;
			}

			activeChar.teleToLocation(x, y, z);
			showTeleportWindow(activeChar);
			break;
		case admin_tele:
			showTeleportWindow(activeChar);
			break;
		case admin_teleto:
		case admin_tele_to:
		case admin_instant_move:
			if (wordList.length > 1 && wordList[1].equalsIgnoreCase("r"))
			{
				activeChar.setTeleMode(2);
			}
			else if (wordList.length > 1 && wordList[1].equalsIgnoreCase("end"))
			{
				activeChar.setTeleMode(0);
			}
			else
			{
				activeChar.setTeleMode(1);
			}
			break;
		case admin_tonpc:
		case admin_to_npc:
			if (wordList.length < 2)
			{
				activeChar.sendMessage("USAGE: //tonpc npcId|npcName");
				return false;
			}
			String npcName = Util.joinStrings(" ", wordList, 1);
			NpcInstance npc;
			try
			{
				if ((npc = GameObjectsStorage.getByNpcId(Integer.parseInt(npcName))) != null)
				{
					teleportToCharacter(activeChar, npc);
					return true;
				}
			}
			catch (Exception e)
			{
			}
			if ((npc = GameObjectsStorage.getNpc(npcName)) != null)
			{
				teleportToCharacter(activeChar, npc);
				return true;
			}
			activeChar.sendMessage("Npc " + npcName + " not found");
			break;
		case admin_toobject:
			if (wordList.length < 2)
			{
				activeChar.sendMessage("USAGE: //toobject objectId");
				return false;
			}
			Integer target = Integer.parseInt(wordList[1]);
			GameObject obj;
			if ((obj = GameObjectsStorage.findObject(target)) != null)
			{
				teleportToCharacter(activeChar, obj);
				return true;
			}
			activeChar.sendMessage("Object " + target + " not found");
			break;
		case admin_autorecall:
			if (wordList.length < 2)
			{
				activeChar.sendMessage("USAGE: //autorecall true | //autorecall false");
				return false;
			}
			activeChar.addQuickVar("autoRecall", Boolean.parseBoolean(wordList[1]));
			activeChar.sendMessage("Worked!");
			break;
		}

		if (!activeChar.getPlayerAccess().CanEditChar)
		{
			return false;
		}

		String targetName = wordList.length == 2 ? wordList[1] : "";
		Player target = GameObjectsStorage.getPlayer(targetName.isEmpty() ? null : targetName);
		switch (command)
		{
		case admin_teleport_character:
			if (wordList.length < 3)
			{
				activeChar.sendMessage("USAGE: //teleport_character x y z");
				return false;
			}
			activeChar.teleToLocation(Location.parseLoc(Util.joinStrings(" ", wordList, 1)));
			showTeleportCharWindow(activeChar);
			break;
		case admin_recall:
			if (target != null)
			{
				recall(activeChar, target);
				return true;
			}

			int obj_id = CharacterDAO.getInstance().getObjectIdByName(targetName);
			if (obj_id > 0)
			{
				teleportCharacter_offline(obj_id, activeChar.getLoc());
				activeChar.sendMessage(targetName + " is offline. Offline teleport used...");
			}
			else
			{
				activeChar.sendMessage("->" + targetName + "<- is incorrect.");
			}
			break;
		case admin_recallparty:
			if (target != null)
			{
				recall(activeChar, target.isInParty() ? target.getParty().getMembers().toArray(new Player[0]) : new Player[]
				{
					target
				});
				return true;
			}
			else
			{
				activeChar.sendMessage("->" + targetName + "<- is incorrect.");
			}
			break;
		case admin_recallcc:
			if (target != null)
			{
				recall(activeChar, target.getPlayerGroup().getMembers().toArray(new Player[0]));
				return true;
			}
			else
			{
				activeChar.sendMessage("->" + targetName + "<- is incorrect.");
			}
			break;
		case admin_recallinstance:
			if (target != null && !target.getReflection().isDefault())
			{
				recall(activeChar, target.getReflection().getPlayers().toArray(new Player[0]));
				return true;
			}
			else
			{
				activeChar.sendMessage("->" + targetName + "<- is incorrect, or reflection is default.");
			}
			break;
		case admin_recallserver:
		case admin_recallserverforce:
			final List<Player> targets = new ArrayList<>();
			for (Player plr : GameObjectsStorage.getAllPlayers())
			{
				if (plr == null)
				{
					continue;
				}

				if (plr.isInOfflineMode() || plr.isInBuffStore() || plr.isInStoreMode() || !plr.isOnline() || plr.isInOlympiadMode() || Olympiad.isRegistered(plr) || plr.isJailed() || plr.isInFightClub() || plr.isInZone(ZoneType.SIEGE) || plr.getReflection() != ReflectionManager.DEFAULT || plr.getPvpFlag() > 0 || plr.getKarma() > 0)
				{
					continue;
				}

				targets.add(plr);
			}
			activeChar.sendMessage("Recalling " + targets.size() + " players out of " + GameObjectsStorage.getAllPlayersCount() + " players. Ignored: Offline shops, instance, event, olympiad participants and jailed players.");
			recall(activeChar, (command == Commands.admin_recallserver), true, targets.toArray(new Player[targets.size()]));
			break;
		case admin_setref:
		{
			if (wordList.length < 2)
			{
				activeChar.sendMessage("Usage: //setref <reflection>");
				return false;
			}

			int ref_id = Integer.parseInt(wordList[1]);
			if (ref_id != 0 && ReflectionManager.getInstance().get(ref_id) == null)
			{
				activeChar.sendMessage("Reflection <" + ref_id + "> not found.");
				return false;
			}

			GameObject targetObj = activeChar;
			GameObject obj = activeChar.getTarget();
			if (obj != null)
			{
				targetObj = obj;
			}

			targetObj.setReflection(ref_id);
			targetObj.decayMe();
			targetObj.spawnMe();
			break;
		}
		case admin_getref:
			if (target == null)
			{
				activeChar.sendMessage("Player '" + wordList[1] + "' not found in world");
				return false;
			}
			activeChar.sendMessage("Player '" + wordList[1] + "' in reflection: " + target.getReflectionId() + ", name: " + target.getReflection().getName());
			break;
		}
		if (!activeChar.getPlayerAccess().CanEditNPC)
		{
			return false;
		}

		switch (command)
		{
		case admin_recall_npc:
			recallNPC(activeChar);
			break;
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void showTeleportWindow(Player activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><title>Teleport Menu</title>");
		replyMSG.append("<body>");

		replyMSG.append("<br>");
		replyMSG.append("<center><table>");

		replyMSG.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"North\" action=\"bypass -h admin_gonorth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Up\" action=\"bypass -h admin_goup\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"West\" action=\"bypass -h admin_gowest\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"East\" action=\"bypass -h admin_goeast\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");
		replyMSG.append("<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"South\" action=\"bypass -h admin_gosouth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		replyMSG.append("<td><button value=\"Down\" action=\"bypass -h admin_godown\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

		replyMSG.append("</table></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void showTeleportCharWindow(Player activeChar)
	{
		GameObject target = activeChar.getTarget();
		Player player = null;
		if (target.isPlayer())
		{
			player = (Player) target;
		}
		else
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return;
		}
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

		StringBuilder replyMSG = new StringBuilder("<html><title>Teleport Character</title>");
		replyMSG.append("<body>");
		replyMSG.append("The character you will teleport is " + player.getName() + ".");
		replyMSG.append("<br>");

		replyMSG.append("Co-ordinate x");
		replyMSG.append("<edit var=\"char_cord_x\" width=110>");
		replyMSG.append("Co-ordinate y");
		replyMSG.append("<edit var=\"char_cord_y\" width=110>");
		replyMSG.append("Co-ordinate z");
		replyMSG.append("<edit var=\"char_cord_z\" width=110>");
		replyMSG.append("<button value=\"Teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("<button value=\"Teleport near you\" action=\"bypass -h admin_teleport_character " + activeChar.getX() + " " + activeChar.getY() + " " + activeChar.getZ() + "\" width=115 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
		replyMSG.append("<center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
		replyMSG.append("</body></html>");

		adminReply.setHtml(replyMSG.toString());
		activeChar.sendPacket(adminReply);
	}

	private void recall(Player activeChar, Player... targets)
	{
		recall(activeChar, false, false, targets);
	}

	private void recall(Player activeChar, boolean askToTp, boolean randomTp, Player... targets)
	{
		for (Player target : targets)
		{
			if (askToTp)
			{
				ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 120000).addString("Would you like to be teleported to the Admin's location?");
				target.ask(packet, new OnAnswerListener()
				{
					@Override
					public void sayYes()
					{
						if (!target.equals(activeChar))
						{
							target.sendMessage("Admin is teleporting you.");
						}

						target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
						if (randomTp)
						{
							target.teleToLocation(activeChar.getX() + Rnd.get(-400, 400), activeChar.getY() + Rnd.get(-400, 400), activeChar.getZ(), activeChar.getReflectionId());
						}
						else
						{
							target.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getReflectionId());
						}

						if (target.equals(activeChar))
						{
							activeChar.sendMessage("You have been teleported to " + activeChar.getLoc() + ", reflection id: " + activeChar.getReflectionId());
						}
					}

					@Override
					public void sayNo()
					{
					}
				});
			}
			else
			{
				if (!target.equals(activeChar))
				{
					target.sendMessage("Admin is teleporting you.");
				}

				target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				if (randomTp)
				{
					target.teleToLocation(activeChar.getX() + Rnd.get(-400, 400), activeChar.getY() + Rnd.get(-400, 400), activeChar.getZ(), activeChar.getReflectionId());
				}
				else
				{
					target.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getReflectionId());
				}

				if (target.equals(activeChar))
				{
					activeChar.sendMessage("You have been teleported to " + activeChar.getLoc() + ", reflection id: " + activeChar.getReflectionId());
				}
			}
		}
	}

	private void teleportCharacter_offline(int obj_id, Location loc)
	{
		if (obj_id == 0)
		{
			return;
		}

		Connection con = null;
		PreparedStatement st = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("UPDATE characters SET x=?,y=?,z=? WHERE obj_Id=? LIMIT 1");
			st.setInt(1, loc.x);
			st.setInt(2, loc.y);
			st.setInt(3, loc.z);
			st.setInt(4, obj_id);
			st.executeUpdate();
		}
		catch (Exception e)
		{

		}
		finally
		{
			DbUtils.closeQuietly(con, st);
		}
	}

	private void teleportToCharacter(Player activeChar, GameObject target)
	{
		if (target == null)
		{
			return;
		}

		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		activeChar.teleToLocation(target.getLoc(), target.getReflectionId());

		activeChar.sendMessage("You have teleported to " + target);
	}

	private void recallNPC(Player activeChar)
	{
		GameObject obj = activeChar.getTarget();
		if (obj != null && obj.isNpc())
		{
			obj.setLoc(activeChar.getLoc());
			((NpcInstance) obj).broadcastCharInfo();
			activeChar.sendMessage("You teleported npc " + obj.getName() + " to " + activeChar.getLoc().toString() + ".");
		}
		else
		{
			activeChar.sendMessage("Target is't npc.");
		}
	}
}