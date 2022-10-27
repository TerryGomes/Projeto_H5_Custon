package l2mv.gameserver.model.entity.events.impl.fightclub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.commons.collections.MultiValueSet;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class LuckyCreaturesEvent extends AbstractFightClub
{
	private final int _monstersCount;
	private final int[] _monsterTemplates;
	private final int _respawnSeconds;

	private final List<NpcInstance> _monsters = new CopyOnWriteArrayList<>();
	private final List<Long> _deathTimes = new CopyOnWriteArrayList<>();

	public LuckyCreaturesEvent(MultiValueSet<String> set)
	{
		super(set);
		_monstersCount = set.getInteger("monstersCount", 1);
		_respawnSeconds = set.getInteger("monstersRespawn", 60);
		_monsterTemplates = parseExcludedSkills(set.getString("monsterTemplates", "14200"));

	}

	@Override
	public String getShortName()
	{
		return "Lucky";
	}

	@Override
	public void onKilled(Creature actor, Creature victim)
	{
		if (victim.isMonster() && actor != null && actor.isPlayable())
		{
			FightClubPlayer fActor = getFightClubPlayer(actor.getPlayer());
			fActor.increaseKills(true);
			updatePlayerScore(fActor);
			actor.getPlayer().sendUserInfo();

			_deathTimes.add(System.currentTimeMillis() + _respawnSeconds * 1000);
			_monsters.remove(victim);
		}

		super.onKilled(actor, victim);
	}

	@Override
	public void startEvent()
	{
		super.startEvent();

		ThreadPoolManager.getInstance().schedule(new RespawnThread(), 30000);

		for (Zone zone : getReflection().getZones())
		{
			zone.setType(Zone.ZoneType.peace_zone);
		}
	}

	@Override
	public void startRound()
	{
		super.startRound();

		System.out.println("spawning " + _monstersCount + " monsters");
		for (int i = 0; i < _monstersCount; i++)
		{
			spawnMonster();
		}
	}

	@Override
	public void stopEvent()
	{
		super.stopEvent();

		for (NpcInstance npc : _monsters)
		{
			if (npc != null)
			{
				npc.doDecay();
			}
		}

		_monsters.clear();
	}

	private void spawnMonster()
	{
		Zone zone = getReflection().getZones().iterator().next();
		Location loc = zone.getTerritory().getRandomLoc(getReflection().getGeoIndex());

		int template = Rnd.get(_monsterTemplates);
		SimpleSpawner spawn = new SimpleSpawner(template);
		spawn.setLoc(loc);
		spawn.setAmount(1);
		spawn.setRespawnDelay(0);
		spawn.setReflection(getReflection());
		NpcInstance monster = spawn.spawnOne();
		spawn.stopRespawn();

		_monsters.add(monster);
	}

	private class RespawnThread extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			if (getState() == EventState.OVER || getState() == EventState.NOT_ACTIVE)
			{
				return;
			}

			long current = System.currentTimeMillis();
			List<Long> toRemove = new ArrayList<>();
			for (Long deathTime : _deathTimes)
			{
				if (deathTime < current)
				{
					spawnMonster();
					toRemove.add(deathTime);
				}
			}

			for (Long l : toRemove)
			{
				_deathTimes.remove(l);
			}

			ThreadPoolManager.getInstance().schedule(this, 10000L);
		}
	}

	@Override
	protected boolean inScreenShowBeScoreNotKills()
	{
		return false;
	}

	@Override
	public boolean isFriend(Creature c1, Creature c2)
	{
		return !(c1.isMonster() || c2.isMonster());
	}

	@Override
	public String getVisibleTitle(Player player, String currentTitle, boolean toMe)
	{
		FightClubPlayer fPlayer = getFightClubPlayer(player);

		if (fPlayer == null)
		{
			return currentTitle;
		}

		return "Kills: " + fPlayer.getKills(true);
	}
}
