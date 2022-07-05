package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

/**
 * AI моба Pronghorn для Frozen Labyrinth.<br>
 * - Если был атакован физическим скилом, спавнится миньон-мобы Pronghorn Spirit 22087 в количестве 4 штук.<br>
 * - Не используют функцию Random Walk, если были заспавнены "миньоны"<br>
 * @author SYS
 */
public class Pronghorn extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(Pronghorn.class);

	private boolean _mobsNotSpawned = true;
	private static final int MOBS = 22087;
	private static final int MOBS_COUNT = 4;

	public Pronghorn(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		NpcInstance actor = getActor();
		if (skill.isMagic())
		{
			return;
		}
		if (_mobsNotSpawned)
		{
			_mobsNotSpawned = false;
			for (int i = 0; i < MOBS_COUNT; i++)
			{
				try
				{
					SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(MOBS));
					sp.setLoc(Location.findPointToStay(actor, 100, 120));
					NpcInstance npc = sp.doSpawn(true);
					if (caster.isPet() || caster.isSummon())
					{
						npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster, Rnd.get(2, 100));
					}
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, caster.getPlayer(), Rnd.get(1, 100));
				}
				catch (RuntimeException e)
				{
					LOG.error("Error while spawning Mobs of Pronghorn", e);
				}
			}
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_mobsNotSpawned = true;
		super.onEvtDead(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return _mobsNotSpawned;
	}
}