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
		this._heroWords = this.readS();
	}

	@Override
	protected void runImpl()
	{
		final Player player = this.getClient().getActiveChar();
		if (player == null || !player.isHero() || this._heroWords == null || this._heroWords.length() > 300)
		{
			return;
		}

		Hero.getInstance().setHeroMessage(player.getObjectId(), this._heroWords);
	}
}