package npc.model;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.templates.npc.NpcTemplate;
import quests._250_WatchWhatYouEat;

/**
 * @author VISTALL
 * @date 10:17/24.06.2011
 */
public class SallyInstance extends NpcInstance
{
	public SallyInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equals("ask_about_rare_plants"))
		{
			QuestState qs = player.getQuestState(_250_WatchWhatYouEat.class);
			if (qs != null && qs.isCompleted())
			{
				showChatWindow(player, 3);
			}
			else
			{
				showChatWindow(player, 2);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
