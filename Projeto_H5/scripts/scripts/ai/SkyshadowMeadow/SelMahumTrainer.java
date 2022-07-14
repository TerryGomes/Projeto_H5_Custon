package ai.SkyshadowMeadow;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;

/**
 * @author claww
  * - AI for mobs Sel Mahum Drill Sergeant (22775), Sel Mahum Training Officer (22,776), Sel Mahum Drill Sergeant (22778).
  * - If the attack swears to chat, Agrita Coaches mobs.
  * - He uses a random Social Networks and makes Coaches mobs to repeat after him three times.
  * - AI is tested and works.
 */
public class SelMahumTrainer extends Fighter
{
	private long _wait_timeout = System.currentTimeMillis() + 20000;
	private List<NpcInstance> _arm = new ArrayList<NpcInstance>();
	private boolean _firstTimeAttacked = true;

	public static final NpcString[] _text =
	{
		NpcString.SCHOOL7,
		NpcString.SCHOOL8
	};

	public SelMahumTrainer(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		final int social = Rnd.get(4, 7);
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		if (_wait_timeout < System.currentTimeMillis())
		{
			if (_arm == null || _arm.isEmpty())
			{
				for (NpcInstance npc : getActor().getAroundNpc(750, 750))
				{
					_arm.add(npc);
				}
			}

			_wait_timeout = (System.currentTimeMillis() + Rnd.get(20, 30) * 1000);

			actor.broadcastPacket(new SocialAction(actor.getObjectId(), social));
			actor.setHeading(actor.getSpawnedLoc().h);

			int time = 2000;
			for (int i = 0; i <= 2; i++)
			{
				ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						for (NpcInstance voin : _arm)
						{
							voin.setHeading(voin.getSpawnedLoc().h);
							voin.broadcastPacket(new SocialAction(voin.getObjectId(), social));
						}
					}
				}, time);
				time += 2000;
			}
		}
		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance npc = _arm.get(_arm.size() - 1);
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));

		if (attacker.isDead())
		{
			actor.moveToLocation(actor.getSpawnedLoc(), 0, true);
		}

		if (_firstTimeAttacked)
		{
			_firstTimeAttacked = false;
			Functions.npcSay(actor, _text[Rnd.get(_text.length)]);
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_firstTimeAttacked = true;
		super.onEvtDead(killer);
	}
}