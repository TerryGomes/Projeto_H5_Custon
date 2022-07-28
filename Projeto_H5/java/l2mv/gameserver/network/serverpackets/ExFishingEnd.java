package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

/**
 * Format: (ch) dc
 * d: character object id
 * c: 1 if won 0 if failed
 */
public class ExFishingEnd extends L2GameServerPacket
{
	private int _charId;
	private boolean _win;

	public ExFishingEnd(Player character, boolean win)
	{
		this._charId = character.getObjectId();
		this._win = win;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x1f);
		this.writeD(this._charId);
		this.writeC(this._win ? 1 : 0);
	}
}