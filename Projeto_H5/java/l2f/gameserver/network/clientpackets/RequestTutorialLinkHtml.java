package l2f.gameserver.network.clientpackets;

import l2f.gameserver.instancemanager.QuestManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.achievements.Achievements;
import l2f.gameserver.model.quest.Quest;

public class RequestTutorialLinkHtml extends L2GameClientPacket
{
	// format: cS

	String _bypass;

	@Override
	protected void readImpl()
	{
		_bypass = readS();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Quest q = QuestManager.getQuest(255);
		if (q != null)
		{
			player.processQuestEvent(q.getName(), _bypass, null);
		}

		// Synerge - Achievements system
		if (_bypass.startsWith("_bbs_achievements"))
		{
			_bypass = _bypass.replaceAll("%", " ");

			if (_bypass.length() < 5)
			{
				return;
			}

			Achievements.getInstance().onBypass(player, _bypass, null);
		}
	}
}