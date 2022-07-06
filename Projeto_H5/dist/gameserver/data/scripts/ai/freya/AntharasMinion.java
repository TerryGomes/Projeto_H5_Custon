package ai.freya;

import bosses.AntharasManager;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.tables.SkillTable;

public class AntharasMinion extends Fighter
{
	public AntharasMinion(NpcInstance actor)
	{
		super(actor);
		actor.startDebuffImmunity();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		for (Player p : AntharasManager.getZone().getInsidePlayers())
		{
			notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		getActor().doCast(SkillTable.getInstance().getInfo(5097, 1), getActor(), true);
		super.onEvtDead(killer);
	}
}