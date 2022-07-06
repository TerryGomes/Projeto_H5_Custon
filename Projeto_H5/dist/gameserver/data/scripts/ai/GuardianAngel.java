package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.scripts.Functions;

public class GuardianAngel extends DefaultAI
{
	static final String[] flood =
	{
		"Waaaah! Step back from the confounded box! I will take it myself!",
		"Grr! Who are you and why have you stopped me?",
		"Grr. I've been hit..."
	};

	public GuardianAngel(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		Functions.npcSay(actor, flood[Rnd.get(2)]);

		return super.thinkActive();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if (actor != null)
		{
			Functions.npcSay(actor, flood[2]);
		}
		super.onEvtDead(killer);
	}
}