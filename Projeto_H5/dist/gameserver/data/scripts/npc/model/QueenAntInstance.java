package npc.model;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.util.Rnd;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.instances.BossInstance;
import l2f.gameserver.model.instances.MinionInstance;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.PlaySound;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;

public class QueenAntInstance extends BossInstance
{
	private static final int Queen_Ant_Larva = 29002;

	private final List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();
	private NpcInstance Larva = null;

	public QueenAntInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public NpcInstance getLarva()
	{
		if (Larva == null)
		{
			Larva = SpawnNPC(Queen_Ant_Larva, new Location(-21600, 179482, -5846, Rnd.get(0, 0xFFFF)));
		}
		return Larva;
	}

	@Override
	protected int getKilledInterval(MinionInstance minion)
	{
		return minion.getNpcId() == 29003 ? 10000 : 280000 + Rnd.get(40000);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, getLoc()));
		Functions.deSpawnNPCs(_spawns);
		Larva = null;
		super.onDeath(killer);
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		getLarva();
		broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS01_A", 1, 0, getLoc()));

		// Synerge - On Queen Ant spawn teleport every player that is inside the zone to the closest town
		/*
		 * final Zone zone = getZone(ZoneType.epic);
		 * if (zone != null)
		 * {
		 * for (Player player : zone.getInsidePlayers())
		 * {
		 * if (player == null)
		 * continue;
		 * player.teleToClosestTown();
		 * }
		 * }
		 */
	}

	private NpcInstance SpawnNPC(int npcId, Location loc)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
		if (template == null)
		{
			System.out.println("WARNING! template is null for npc: " + npcId);
			Thread.dumpStack();
			return null;
		}
		try
		{
			SimpleSpawner sp = new SimpleSpawner(template);
			sp.setLoc(loc);
			sp.setAmount(1);
			sp.setRespawnDelay(0);
			_spawns.add(sp);
			return sp.spawnOne();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}