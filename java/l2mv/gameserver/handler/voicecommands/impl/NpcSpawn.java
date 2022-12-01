package l2mv.gameserver.handler.voicecommands.impl;

import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastMap;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.Spawner;
import l2mv.gameserver.model.Zone.ZoneType;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SpawnTable;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Util;

public class NpcSpawn extends Functions implements IVoicedCommandHandler
{
	private static final int[] CLANHALL_NPC_IDS =
	{
//		37031,
//		37032,
//		37033,
//		37034,
//		37035,
//		37036,
//		37037,
//		37038,
//		37039,
//		37040,
//		32323,
//		30120
	};

	private static final String[] COMMANDS =
	{
//		"npcspawn"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		if ((args == null) || (args == "") || args.isEmpty())
		{
			String msg = showClanhallNpcSpawnWindow(activeChar);
			if (msg != null)
			{
				activeChar.sendMessage(msg);
			}

			return true;
		}

		String[] paramSplit = args.split(" ");
		if (paramSplit.length != 2)
		{
			activeChar.sendMessage("Something is wrong with the given parameters.");
		}
		else if (paramSplit[0].equalsIgnoreCase("spawn"))
		{
			String npcId = paramSplit[1];
			if (Util.isDigit(npcId))
			{
				String msg = spawnClanhallNpc(activeChar, Integer.parseInt(npcId));
				if (msg != null)
				{
					activeChar.sendMessage(msg);
				}
			}
		}
		else if (paramSplit[0].equalsIgnoreCase("unspawn"))
		{
			String npcObjId = paramSplit[1];
			if (Util.isDigit(npcObjId))
			{
				String msg = unspawnClanhallNpc(activeChar, Integer.parseInt(npcObjId));
				if (msg != null)
				{
					activeChar.sendMessage(msg);
				}
			}
		}
		return true;
	}

	private String showClanhallNpcSpawnWindow(Player player)
	{
		if (!((player.getClanPrivileges() & Clan.CP_CH_SET_FUNCTIONS) == Clan.CP_CH_SET_FUNCTIONS) && !player.isGM())
		{
			return "Only clan clan members with privilegies to set clanhall functions can spawn NPCs in their clanhall.";
		}
		else if (!player.isGM() && (player.getClan().getHasHideout() == 0))
		{
			return "You do not own a clanhall.";
		}
		else if (!player.isInZone(ZoneType.RESIDENCE))
		{
			return "You are not located in a clanhall.";
		}
		else if (player.getClanHall() == null)
		{
			return "You do not have a clanhall";
		}

		ClanHall zone = ResidenceHolder.getInstance().getResidenceByCoord(ClanHall.class, player.getX(), player.getY(), player.getZ(), player.getReflection());
		if (zone == null)
		{
			return "You are not located in a clanhall.";
		}
		else if (!player.isGM() && (player.getClan().getHasHideout() != zone.getId()))
		{
			return "This clanhall doesn't belong to your clan.";
		}

		// Add the clanhall NPCs in the list.
		Map<Integer, Integer> _npcIdOid = new FastMap<>();
		for (int npcId : CLANHALL_NPC_IDS)
		{
			if (!_npcIdOid.containsKey(npcId))
			{
				_npcIdOid.put(npcId, 0);
			}
		}

		// Now fill the spawned NPCs
		for (NpcInstance cha : zone.getZone().getInsideNpcs())
		{
			if (!cha.isNpc() || !Util.contains(CLANHALL_NPC_IDS, cha.getNpcId()))
			{
				continue;
			}

			_npcIdOid.put(cha.getNpcId(), cha.getObjectId());
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><title>Clanhall NPC Manage:</title>");
		sb.append("<font color=LEVEL>In this menu you can spawn/unspawn the given NPCs. If you are spawning an NPC, it will be spawned at your char's location and heading. </font><br>");
		sb.append("<table width=280>");
		for (Entry<Integer, Integer> npcIdOid : _npcIdOid.entrySet())
		{
			int npcId = npcIdOid.getKey();
			int objId = npcIdOid.getValue();
			boolean isSpawned = objId != 0;
			sb.append("<tr><td width=160>");
			sb.append("<font color=" + (isSpawned ? "00FF00" : "FF0000") + ">" + getNpcName(npcId) + "</font>");
			sb.append("</td><td align=right>");
			sb.append("<button value=\"" + (isSpawned ? "Unspawn" : "Spawn") + "\" action=\"bypass -h user_npcspawn " + (isSpawned ? ("unspawn " + objId) : ("spawn " + npcId)) + "\" width=100 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			sb.append("</td></tr>");
		}
		sb.append("</table></body></html>");

		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(sb.toString());
		player.sendPacket(html);

		return null;
	}

	private String spawnClanhallNpc(Player player, int npcId)
	{
		if (!((player.getClanPrivileges() & Clan.CP_CH_SET_FUNCTIONS) == Clan.CP_CH_SET_FUNCTIONS) && !player.isGM())
		{
			return "Only clan clan members with privilegies to set clanhall functions can spawn NPCs in their clanhall.";
		}
		else if (!player.isGM() && (player.getClan().getHasHideout() == 0))
		{
			return "You do not own a clanhall.";
		}
		else if (!player.isInZone(ZoneType.RESIDENCE))
		{
			return "You are not located in a clanhall.";
		}

		ClanHall zone = ResidenceHolder.getInstance().getResidenceByCoord(ClanHall.class, player.getX(), player.getY(), player.getZ(), player.getReflection());
		if (zone == null)
		{
			return "You are not located in a clanhall.";
		}
		else if (!player.isGM() && (player.getClan().getHasHideout() != zone.getId()))
		{
			return "This clanhall doesn't belong to your clan.";
		}

		for (NpcInstance cha : zone.getZone().getInsideNpcs())
		{
			if (cha.isNpc() && (cha.getNpcId() == npcId))
			{
				return "This NPC is already spawned in your clanhall.";
			}
		}

		try
		{
			NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
			SimpleSpawner spawn = new SimpleSpawner(template);
			// spawn.setCustom(true);
			spawn.setLocx(player.getX());
			spawn.setLocy(player.getY());
			spawn.setLocz(player.getZ());
			spawn.setAmount(1);
			spawn.setHeading(player.getHeading());
			spawn.setRespawnDelay(0);
			spawn.setReflection(player.getReflection()); // cant understand what this does, need to test...
			spawn.stopRespawn();
			SpawnTable.getInstance().addNewSpawn(spawn);
			spawn.init();

			String msg = showClanhallNpcSpawnWindow(player);
			if (msg != null)
			{
				player.sendMessage(msg);
			}

			return "You have spawned " + template.getName();
		}
		catch (Exception e)
		{
			player.sendPacket(new SystemMessage(SystemMessage.YOUR_TARGET_CANNOT_BE_FOUND));
		}

		return "There has been a problem while spawning the NPC.";
	}

	private String unspawnClanhallNpc(Player player, int npcObjId)
	{
		if (!player.isClanLeader() && !player.isGM())
		{
			return "Only clan leaders can unspawn NPCs from their clanhall.";
		}

		GameObject obj = GameObjectsStorage.findObject(npcObjId);
		if ((obj == null) || !obj.isNpc())
		{
			return "NPC not found.";
		}

		NpcInstance npc = (NpcInstance) obj;
		if (!Util.contains(CLANHALL_NPC_IDS, npc.getNpcId()))
		{
			return "You cannot unspawn this NPC.";
		}
		ClanHall zone = ResidenceHolder.getInstance().getResidenceByCoord(ClanHall.class, player.getX(), player.getY(), player.getZ(), player.getReflection());
		if (zone == null)
		{
			return "The selected NPC is not in a clanhall.";
		}
		else if (!player.isGM() && (player.getClan().getHasHideout() != zone.getId()))
		{
			return "The selected NPC is not in your clanhall.";
		}

		Spawner spawn = npc.getSpawn();
		if (spawn != null)
		{
			spawn.stopRespawn();
			SpawnTable.getInstance().deleteSpawn(npc.getSpawnedLoc(), npc.getNpcId());
			npc.deleteMe();

			String msg = showClanhallNpcSpawnWindow(player);
			if (msg != null)
			{
				player.sendMessage(msg);
			}

			return "Deleted " + npc.getName() + " from your clanhall.";
		}

		return "Something is wrong while trying to unspawn the NPC.";
	}

	private String getNpcName(int npcId)
	{
		NpcTemplate tmpl = NpcHolder.getInstance().getTemplate(npcId);
		if (tmpl == null)
		{
			_log.warn("Npc template is null for NPC ID: " + npcId);
			return "Unknown";
		}

		return tmpl.getName();
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}