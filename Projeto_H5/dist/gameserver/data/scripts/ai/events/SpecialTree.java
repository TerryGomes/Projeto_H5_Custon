package ai.events;

import l2f.commons.math.random.RndSelector;
import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.tables.SkillTable;

public class SpecialTree extends DefaultAI
{
	private static final RndSelector<Integer> SOUNDS;

	static
	{
		SOUNDS = new RndSelector<Integer>(5);
		SOUNDS.add(2140, 20);
		SOUNDS.add(2142, 20);
		SOUNDS.add(2145, 20);
		SOUNDS.add(2147, 20);
		SOUNDS.add(2149, 20);
	}

	private boolean _buffsEnabled = false;
	private int _timer = 0;

	public SpecialTree(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		if (_buffsEnabled)
		{
			_timer++;
			if (_timer >= 180)
			{
				_timer = 0;

				final NpcInstance actor = getActor();
				if (actor == null)
				{
					return false;
				}

				addTaskBuff(actor, SkillTable.getInstance().getInfo(2139, 1));

				if (Rnd.chance(33))
				{
					actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, SOUNDS.select(), 1, 500, 0));
				}
			}
		}

		return super.thinkActive();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		_buffsEnabled = !getActor().isInZonePeace();
		_timer = 0;
	}
}