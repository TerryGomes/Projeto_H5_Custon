package ai.SkyshadowMeadow;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;

/**
 * @author claww
  * - AI for mobs Sel Mahum Recruit (22,780) and Sel Mahum Recruit (22,782) and Sel Mahum Soldier (22,783) and Sel Mahum Recruit (22,784) and Sel Mahum Soldier (22,785).
  * - If the attack swears to chat with a 20% chance, Agrita main mob.
  * - AI is tested and works.
 */
public class SelMahumRecruit extends Fighter
{
	private long _wait_timeout = System.currentTimeMillis() + 180000;
	private List<NpcInstance> _arm = new ArrayList<NpcInstance>();
	private boolean _firstTimeAttacked = true;
	public static final NpcString[] _text =
	{
		NpcString.SCHOOL1,
		NpcString.SCHOOL2
	};

	public SelMahumRecruit(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		if (_wait_timeout < System.currentTimeMillis())
		{
			_wait_timeout = (System.currentTimeMillis() + Rnd.get(150, 200) * 1000);
			actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
		}

		if (_arm == null || _arm.isEmpty())
		{
			for (NpcInstance npc : getActor().getAroundNpc(750, 750))
			{
				if (npc != null && (npc.getNpcId() == 22775 || npc.getNpcId() == 22776 || npc.getNpcId() == 22778 || npc.getNpcId() == 22780 || npc.getNpcId() == 22782 || npc.getNpcId() == 22783 || npc.getNpcId() == 22784 || npc.getNpcId() == 22785))
				{
					_arm.add(npc);
				}
			}
		}
		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		for (NpcInstance npc : _arm)
		{
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));

			if (npc.isDead())
			{
				if (Rnd.chance(20))
				{
					if (_firstTimeAttacked)
					{
						_firstTimeAttacked = false;
						Functions.npcSay(actor, _text[Rnd.get(_text.length)]);
					}
				}
				actor.moveToLocation(actor.getSpawnedLoc(), 0, true);
			}
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