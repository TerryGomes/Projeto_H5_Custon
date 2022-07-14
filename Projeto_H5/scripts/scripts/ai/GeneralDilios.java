package ai;

import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.scripts.Functions;

/**
 * Dilios AI
 *
 * @author pchayka
 *
 */
public class GeneralDilios extends DefaultAI
{
	private static final int GUARD_ID = 32619;
	private long _wait_timeout = 0;

	private static final String[] diliosText =
	{
		/* "Messenger, inform the patrons of the Keucereus Alliance Base! The Seed of Infinity is currently secured under the flag of the Keucereus Alliance!", */
		"Messenger, inform the patrons of the Keucereus Alliance Base! We're gathering brave adventurers to attack Tiat's Mounted Troop that's rooted in the Seed of Destruction.",
		"Messenger, inform the brothers in Keucereus's clan outpost! Brave adventurers are currently eradicating Undead that are widespread in Seed of Immortality's Hall of Suffering and Hall of Erosion!",
		"Stabbing three times!"
	};

	public GeneralDilios(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 10000;
	}

	@Override
	public boolean thinkActive()
	{
		NpcInstance actor = getActor();

		if (System.currentTimeMillis() > _wait_timeout)
		{
			_wait_timeout = System.currentTimeMillis() + 60000;
			int j = Rnd.get(1, 3);
			switch (j)
			{
			case 1:
				Functions.npcSay(actor, diliosText[0]);
				break;
			case 2:
				Functions.npcSay(actor, diliosText[1]);
				break;
			case 3:
				Functions.npcSay(actor, diliosText[2]);
				List<NpcInstance> around = actor.getAroundNpc(1500, 100);
				if (around != null && !around.isEmpty())
				{
					for (NpcInstance guard : around)
					{
						if (!guard.isMonster() && guard.getNpcId() == GUARD_ID)
						{
							guard.broadcastPacket(new SocialAction(guard.getObjectId(), 4));
						}
					}
				}
			}
		}
		return false;
	}
}