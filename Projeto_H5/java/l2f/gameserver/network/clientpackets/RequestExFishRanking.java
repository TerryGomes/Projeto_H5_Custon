package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.instancemanager.games.FishingChampionShipManager;
import l2f.gameserver.model.Player;

/**
 * @author n0nam3
 * @date 08/08/2010 15:53
 */
public class RequestExFishRanking extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
		{
			FishingChampionShipManager.getInstance().showMidResult(getClient().getActiveChar());
		}
	}
}