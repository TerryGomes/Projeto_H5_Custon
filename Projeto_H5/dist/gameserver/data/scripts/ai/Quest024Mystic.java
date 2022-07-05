package ai;

import l2f.gameserver.ai.Mystic;
import l2f.gameserver.instancemanager.QuestManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import quests._024_InhabitantsOfTheForestOfTheDead;

/**
 * @author VISTALL
 */
public class Quest024Mystic extends Mystic
{
	public Quest024Mystic(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		Quest q = QuestManager.getQuest(_024_InhabitantsOfTheForestOfTheDead.class);
		if (q != null)
		{
			for (Player player : World.getAroundPlayers(getActor(), 300, 200))
			{
				QuestState questState = player.getQuestState(_024_InhabitantsOfTheForestOfTheDead.class);
				if (questState != null && questState.getCond() == 3)
				{
					q.notifyEvent("seePlayer", questState, getActor());
				}
			}
		}
		return super.thinkActive();
	}
}