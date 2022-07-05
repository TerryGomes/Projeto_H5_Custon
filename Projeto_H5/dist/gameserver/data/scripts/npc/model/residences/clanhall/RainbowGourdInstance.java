package npc.model.residences.clanhall;

import java.util.List;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.World;
import l2f.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2f.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2f.gameserver.model.entity.events.objects.SpawnExObject;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.NpcUtils;

/**
 * @author VISTALL
 * @date 12:39/21.05.2011
 */
public class RainbowGourdInstance extends NpcInstance
{
	private CMGSiegeClanObject _winner;

	public RainbowGourdInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setHasChatWindow(false);
	}

	public void doDecrease(Creature character)
	{
		if (isDead())
		{
			return;
		}

		reduceCurrentHp(getMaxHp() * 0.2, character, null, false, false, false, false, false, false, false);
	}

	public void doHeal()
	{
		if (isDead())
		{
			return;
		}

		setCurrentHp(getCurrentHp() + getMaxHp() * 0.2, false);
	}

	public void doSwitch(RainbowGourdInstance npc)
	{
		if (isDead() || npc.isDead())
		{
			return;
		}

		final double currentHp = getCurrentHp();
		setCurrentHp(npc.getCurrentHp(), false);
		npc.setCurrentHp(currentHp, false);
	}

	@Override
	public void onDeath(Creature killer)
	{
		super.onDeath(killer);

		ClanHallMiniGameEvent miniGameEvent = getEvent(ClanHallMiniGameEvent.class);
		if (miniGameEvent == null)
		{
			return;
		}

		Player player = killer.getPlayer();

		CMGSiegeClanObject siegeClanObject = miniGameEvent.getSiegeClan(ClanHallMiniGameEvent.ATTACKERS, player.getClan());
		if (siegeClanObject == null)
		{
			return;
		}

		_winner = siegeClanObject;

		List<CMGSiegeClanObject> attackers = miniGameEvent.getObjects(ClanHallMiniGameEvent.ATTACKERS);
		for (int i = 0; i < attackers.size(); i++)
		{
			if (attackers.get(i) == siegeClanObject)
			{
				continue;
			}

			String arenaName = "arena_" + i;
			SpawnExObject spawnEx = miniGameEvent.getFirstObject(arenaName);

			RainbowYetiInstance yetiInstance = (RainbowYetiInstance) spawnEx.getSpawns().get(0).getFirstSpawned();
			yetiInstance.teleportFromArena();

			miniGameEvent.spawnAction(arenaName, false);
		}
	}

	@Override
	public void onDecay()
	{
		super.onDecay();

		final ClanHallMiniGameEvent miniGameEvent = getEvent(ClanHallMiniGameEvent.class);
		if ((miniGameEvent == null) || (_winner == null))
		{
			return;
		}

		List<CMGSiegeClanObject> attackers = miniGameEvent.getObjects(ClanHallMiniGameEvent.ATTACKERS);

		int index = attackers.indexOf(_winner);

		String arenaName = "arena_" + index;
		miniGameEvent.spawnAction(arenaName, false);

		SpawnExObject spawnEx = miniGameEvent.getFirstObject(arenaName);

		Spawner spawner = spawnEx.getSpawns().get(0);

		Location loc = (Location) spawner.getCurrentSpawnRange();

		miniGameEvent.removeBanishItems();

		final NpcInstance npc = NpcUtils.spawnSingle(35600, loc.x, loc.y, loc.z, 0);
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				List<Player> around = World.getAroundPlayers(npc, 750, 100);

				npc.deleteMe();

				for (Player player : around)
				{
					player.teleToLocation(miniGameEvent.getResidence().getOwnerRestartPoint());
				}

				miniGameEvent.processStep(_winner.getClan());
			}
		}, 10000L);
	}

	@Override
	public boolean isAttackable(Creature c)
	{
		return false;
	}

	@Override
	public boolean isAutoAttackable(Creature c)
	{
		return false;
	}

	@Override
	public boolean isInvul()
	{
		return false;
	}
}
