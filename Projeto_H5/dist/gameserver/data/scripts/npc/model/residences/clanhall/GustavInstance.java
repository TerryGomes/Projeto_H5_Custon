package npc.model.residences.clanhall;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.model.AggroList;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectTasks;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.model.entity.events.objects.SpawnExObject;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;
import npc.model.residences.SiegeGuardInstance;

/**
 * @author VISTALL
 * @date 21:08/07.05.2011
 * 35410
 * 4235 skill id
 */
public class GustavInstance extends SiegeGuardInstance implements _34SiegeGuard
{
	private AtomicBoolean _canDead = new AtomicBoolean();
	private Future<?> _teleportTask;

	public GustavInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		_canDead.set(false);

		Functions.npcShout(this, NpcString.PREPARE_TO_DIE_FOREIGN_INVADERS_I_AM_GUSTAV_THE_ETERNAL_RULER_OF_THIS_FORTRESS_AND_I_HAVE_TAKEN_UP_MY_SWORD_TO_REPEL_THEE);
	}

	@Override
	public void onDeath(Creature killer)
	{
		if (!_canDead.get())
		{
			_canDead.set(true);
			setCurrentHp(1, true);

			// Застваляем снять таргет и остановить аттаку
			for (Creature cha : World.getAroundCharacters(this))
			{
				ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(cha, CtrlEvent.EVT_FORGET_OBJECT, this, null));
			}

			ClanHallSiegeEvent siegeEvent = getEvent(ClanHallSiegeEvent.class);
			if (siegeEvent == null)
			{
				return;
			}

			SpawnExObject obj = siegeEvent.getFirstObject(ClanHallSiegeEvent.BOSS);

			for (int i = 0; i < 3; i++)
			{
				final NpcInstance npc = obj.getSpawns().get(i).getFirstSpawned();

				Functions.npcSay(npc, ((_34SiegeGuard) npc).teleChatSay());
				npc.broadcastPacket(new MagicSkillUse(npc, npc, 4235, 1, 10000, 0));

				_teleportTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl() throws Exception
					{
						Location loc = Location.findAroundPosition(177134, -18807, -2256, 50, 100, npc.getGeoIndex());

						npc.teleToLocation(loc);

						if (npc == GustavInstance.this)
						{
							npc.reduceCurrentHp(npc.getCurrentHp(), npc, null, false, false, false, false, false, false, false);
						}
					}
				}, 10000L);
			}
		}
		else
		{
			if (_teleportTask != null)
			{
				_teleportTask.cancel(false);
				_teleportTask = null;
			}

			SiegeEvent siegeEvent = getEvent(SiegeEvent.class);
			if (siegeEvent == null)
			{
				return;
			}

			siegeEvent.processStep(getMostDamagedClan());

			super.onDeath(killer);
		}
	}

	public Clan getMostDamagedClan()
	{
		ClanHallSiegeEvent siegeEvent = getEvent(ClanHallSiegeEvent.class);

		Player temp = null;

		Map<Player, Integer> damageMap = new HashMap<Player, Integer>();

		for (AggroList.HateInfo info : getAggroList().getPlayableMap().values())
		{
			Playable killer = (Playable) info.attacker;
			int damage = info.damage;
			if (killer.isPet() || killer.isSummon())
			{
				temp = killer.getPlayer();
			}
			else if (killer.isPlayer())
			{
				temp = (Player) killer;
			}

			if (temp == null || siegeEvent.getSiegeClan(SiegeEvent.ATTACKERS, temp.getClan()) == null)
			{
				continue;
			}

			if (!damageMap.containsKey(temp))
			{
				damageMap.put(temp, damage);
			}
			else
			{
				int dmg = damageMap.get(temp) + damage;
				damageMap.put(temp, dmg);
			}
		}

		int mostDamage = 0;
		Player player = null;

		for (Map.Entry<Player, Integer> entry : damageMap.entrySet())
		{
			int damage = entry.getValue();
			Player t = entry.getKey();
			if (damage > mostDamage)
			{
				mostDamage = damage;
				player = t;
			}
		}

		return player == null ? null : player.getClan();
	}

	@Override
	public NpcString teleChatSay()
	{
		return NpcString.THIS_IS_UNBELIEVABLE_HAVE_I_REALLY_BEEN_DEFEATED_I_SHALL_RETURN_AND_TAKE_YOUR_HEAD;
	}

	@Override
	public boolean isEffectImmune()
	{
		return true;
	}
}
