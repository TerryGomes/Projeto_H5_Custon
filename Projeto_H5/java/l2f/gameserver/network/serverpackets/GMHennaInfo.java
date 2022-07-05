package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.templates.Henna;

//ccccccdd[dd]
public class GMHennaInfo extends L2GameServerPacket
{
	private int _count, _str, _con, _dex, _int, _wit, _men;
	private final Henna[] _hennas = new Henna[3];

	public GMHennaInfo(Player cha)
	{
		_str = cha.getHennaStatSTR();
		_con = cha.getHennaStatCON();
		_dex = cha.getHennaStatDEX();
		_int = cha.getHennaStatINT();
		_wit = cha.getHennaStatWIT();
		_men = cha.getHennaStatMEN();

		int j = 0;
		for (int i = 0; i < 3; i++)
		{
			Henna h = cha.getHenna(i + 1);
			if (h != null)
			{
				_hennas[j++] = h;
			}
		}
		_count = j;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xf0);

		writeC(_int);
		writeC(_str);
		writeC(_con);
		writeC(_men);
		writeC(_dex);
		writeC(_wit);
		writeD(3);
		writeD(_count);
		for (int i = 0; i < _count; i++)
		{
			writeD(_hennas[i].getSymbolId());
			writeD(_hennas[i].getSymbolId());
		}
	}
}