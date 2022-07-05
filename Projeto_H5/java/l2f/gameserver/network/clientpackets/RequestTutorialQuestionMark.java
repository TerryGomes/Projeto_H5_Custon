package l2f.gameserver.network.clientpackets;

import l2f.gameserver.instancemanager.QuestManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.achievements.Achievements;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2f.gameserver.model.quest.Quest;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	// format: cd
	int _number = 0;

	@Override
	protected void readImpl()
	{
		_number = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		player.isntAfk();

		if (player.isInFightClub())
		{
			FightClubEventManager.getInstance().sendEventPlayerMenu(player);
		}
		else
		{
			Quest q = QuestManager.getQuest(255);
			if (q != null)
			{
				player.processQuestEvent(q.getName(), "QM" + _number, null);
			}

			if (_number == player.getObjectId())
			{
				Achievements.getInstance().onBypass(player, "_bbs_achievements", null);
			}
		}
	}
}