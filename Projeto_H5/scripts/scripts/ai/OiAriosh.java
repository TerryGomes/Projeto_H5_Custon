package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

/**
 * AI босса Oi Ariosh для Kamaloka.<br>
 * - Спавнит "миньонов" при атаке.<br>
 * - _hps - таблица процентов hp, после которых спавнит "миньонов".<br>
 * @author n0nam3
 */
public class OiAriosh extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(OiAriosh.class);
	private static final int MOB = 18556;
	private int _hpCount = 0;
	private static final int[] _hps =
	{
		80,
		60,
		40,
		30,
		20,
		10,
		5,
		-5
	};

	public OiAriosh(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (!actor.isDead())
		{
			if (_hpCount < _hps.length && actor.getCurrentHpPercents() < _hps[_hpCount])
			{
				spawnMob(attacker);
				_hpCount++;
			}
		}
		super.onEvtAttacked(attacker, damage);
	}

	private void spawnMob(Creature attacker)
	{
		NpcInstance actor = getActor();
		try
		{
			SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(MOB));
			sp.setLoc(Location.findPointToStay(actor, 100, 120));
			sp.setReflection(actor.getReflection());
			NpcInstance npc = sp.doSpawn(true);
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
		}
		catch (RuntimeException e)
		{
			LOG.error("Error while spawning Mobs of Oi Ariosh", e);
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_hpCount = 0;
		super.onEvtDead(killer);
	}
}