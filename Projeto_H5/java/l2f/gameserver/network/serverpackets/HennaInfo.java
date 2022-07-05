package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

public class HennaInfo extends L2GameServerPacket
{
	private final Henna[] _hennas = new Henna[3];
	private final int _str, _con, _dex, _int, _wit, _men;
	private int _count;

	public HennaInfo(Player player)
	{
		_count = 0;
		l2f.gameserver.templates.Henna h;
		for (int i = 0; i < 3; i++)
		{
			if ((h = player.getHenna(i + 1)) != null)
			{
				_hennas[_count++] = new Henna(h.getSymbolId(), h.isForThisClass(player));
			}
		}

		_str = player.getHennaStatSTR();
		_con = player.getHennaStatCON();
		_dex = player.getHennaStatDEX();
		_int = player.getHennaStatINT();
		_wit = player.getHennaStatWIT();
		_men = player.getHennaStatMEN();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xe5);
		writeC(_int); // equip INT
		writeC(_str); // equip STR
		writeC(_con); // equip CON
		writeC(_men); // equip MEM
		writeC(_dex); // equip DEX
		writeC(_wit); // equip WIT
		writeD(3); // interlude, slots?
		writeD(_count);
		for (int i = 0; i < _count; i++)
		{
			writeD(_hennas[i]._symbolId);
			writeD(_hennas[i]._valid ? _hennas[i]._symbolId : 0);
		}
	}

	private static class Henna
	{
		private int _symbolId;
		private boolean _valid;

		public Henna(int sy, boolean valid)
		{
			_symbolId = sy;
			_valid = valid;
		}
	}
}