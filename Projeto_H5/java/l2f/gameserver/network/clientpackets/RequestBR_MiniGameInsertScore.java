package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.instancemanager.games.MiniGameScoreManager;
import l2f.gameserver.model.Player;

public class RequestBR_MiniGameInsertScore extends L2GameClientPacket
{
	private int _score;

	@Override
	protected void readImpl()
	{
		_score = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null || !Config.EX_JAPAN_MINIGAME)
		{
			return;
		}

		MiniGameScoreManager.getInstance().insertScore(player, _score);
	}
}