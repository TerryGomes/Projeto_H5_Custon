package ai.den_of_evil;

import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.taskmanager.AiTaskManager;

/**
 * @author claww
 * @date 20.01.2012
 * Npc Id: 32026
 */
public class HestuiGuard extends DefaultAI
{
	public HestuiGuard(NpcInstance actor)
	{
		super(actor);

	}

	@Override
	public synchronized void startAITask()
	{
		if (_aiTask == null)
		{
			_aiTask = AiTaskManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
		}
	}

	@Override
	protected synchronized void switchAITask(long NEW_DELAY)
	{
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();

		for (Player player : World.getAroundPlayers(actor))
		{
			if (player.getLevel() <= 37)
			{
				Functions.npcSay(actor, NpcString.THIS_PLACE_IS_DANGEROUS_S1, player.getName());
			}
		}

		return false;
	}
}
