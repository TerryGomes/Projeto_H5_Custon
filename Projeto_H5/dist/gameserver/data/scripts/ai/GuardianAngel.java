package ai;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;

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