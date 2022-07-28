package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.achievements.Achievements;
import l2mv.gameserver.model.quest.Quest;

public class RequestTutorialLinkHtml extends L2GameClientPacket
{
	// format: cS

	String _bypass;

	@Override
	protected void readImpl()
	{
		this._bypass = this.readS();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Quest q = QuestManager.getQuest(255);
		if (q != null)
		{
			player.processQuestEvent(q.getName(), this._bypass, null);
		}

		// Synerge - Achievements system
		if (this._bypass.startsWith("_bbs_achievements"))
		{
			this._bypass = this._bypass.replaceAll("%", " ");

			if (this._bypass.length() < 5)
			{
				return;
			}

			Achievements.getInstance().onBypass(player, this._bypass, null);
		}
	}
}