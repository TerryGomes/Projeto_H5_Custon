package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.achievements.Achievements;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2mv.gameserver.model.quest.Quest;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	// format: cd
	int _number = 0;

	@Override
	protected void readImpl()
	{
		this._number = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
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
				player.processQuestEvent(q.getName(), "QM" + this._number, null);
			}

			if (this._number == player.getObjectId())
			{
				Achievements.getInstance().onBypass(player, "_bbs_achievements", null);
			}
		}
	}
}