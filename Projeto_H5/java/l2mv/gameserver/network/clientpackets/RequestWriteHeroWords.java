package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Hero;

/**
 * Format chS
 * c (id) 0xD0
 * h (subid) 0x0C
 * S the hero's words :)
 *
 */
public class RequestWriteHeroWords extends L2GameClientPacket
{
	private String _heroWords;

	@Override
	protected void readImpl()
	{
		_heroWords = readS();
	}

	@Override
	protected void runImpl()
	{
		final Player player = getClient().getActiveChar();
		if (player == null || !player.isHero() || _heroWords == null || _heroWords.length() > 300)
		{
			return;
		}

		Hero.getInstance().setHeroMessage(player.getObjectId(), _heroWords);
	}
}