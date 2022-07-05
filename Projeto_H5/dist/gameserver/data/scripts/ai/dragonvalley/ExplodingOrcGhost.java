package ai.dragonvalley;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;

/**
 * @author FandC
 *
 * AI mob 22818.
 * After spawn explode after 3 seconds damage.
 */
public class ExplodingOrcGhost extends Fighter
{

	private Skill SELF_DESTRUCTION = SkillTable.getInstance().getInfo(6850, 1);

	public ExplodingOrcGhost(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		ThreadPoolManager.getInstance().schedule(new StartSelfDestructionTimer(getActor()), 3000L);
		super.onEvtSpawn();
	}

	private class StartSelfDestructionTimer extends RunnableImpl
	{

		private NpcInstance _npc;

		public StartSelfDestructionTimer(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl()
		{
			_npc.abortAttack(true, false);
			_npc.abortCast(true, false);
			_npc.doCast(SELF_DESTRUCTION, _actor, true);
		}
	}

}
