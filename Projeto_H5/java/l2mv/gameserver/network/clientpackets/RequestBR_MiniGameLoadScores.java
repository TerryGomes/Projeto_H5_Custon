package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExBR_MiniGameLoadScores;

public class RequestBR_MiniGameLoadScores extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null || !Config.EX_JAPAN_MINIGAME)
		{
			return;
		}

		player.sendPacket(new ExBR_MiniGameLoadScores(player));
	}
}