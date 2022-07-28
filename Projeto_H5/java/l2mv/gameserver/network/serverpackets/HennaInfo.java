package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class HennaInfo extends L2GameServerPacket
{
	private final Henna[] _hennas = new Henna[3];
	private final int _str, _con, _dex, _int, _wit, _men;
	private int _count;

	public HennaInfo(Player player)
	{
		this._count = 0;
		l2mv.gameserver.templates.Henna h;
		for (int i = 0; i < 3; i++)
		{
			if ((h = player.getHenna(i + 1)) != null)
			{
				this._hennas[this._count++] = new Henna(h.getSymbolId(), h.isForThisClass(player));
			}
		}

		this._str = player.getHennaStatSTR();
		this._con = player.getHennaStatCON();
		this._dex = player.getHennaStatDEX();
		this._int = player.getHennaStatINT();
		this._wit = player.getHennaStatWIT();
		this._men = player.getHennaStatMEN();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xe5);
		this.writeC(this._int); // equip INT
		this.writeC(this._str); // equip STR
		this.writeC(this._con); // equip CON
		this.writeC(this._men); // equip MEM
		this.writeC(this._dex); // equip DEX
		this.writeC(this._wit); // equip WIT
		this.writeD(3); // interlude, slots?
		this.writeD(this._count);
		for (int i = 0; i < this._count; i++)
		{
			this.writeD(this._hennas[i]._symbolId);
			this.writeD(this._hennas[i]._valid ? this._hennas[i]._symbolId : 0);
		}
	}

	private static class Henna
	{
		private int _symbolId;
		private boolean _valid;

		public Henna(int sy, boolean valid)
		{
			this._symbolId = sy;
			this._valid = valid;
		}
	}
}