package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.templates.Henna;

public class HennaItemInfo extends L2GameServerPacket
{
	private int _str, _con, _dex, _int, _wit, _men;
	private long _adena;
	private Henna _henna;

	public HennaItemInfo(Henna henna, Player player)
	{
		_henna = henna;
		_adena = player.getAdena();
		_str = player.getSTR();
		_dex = player.getDEX();
		_con = player.getCON();
		_int = player.getINT();
		_wit = player.getWIT();
		_men = player.getMEN();
	}

	@Override
	protected final void writeImpl()
	{

		writeC(0xe4);
		writeD(_henna.getSymbolId()); // symbol Id
		writeD(_henna.getDyeId()); // item id of dye
		writeQ(_henna.getDrawCount());
		writeQ(_henna.getPrice());
		writeD(1); // able to draw or not 0 is false and 1 is true
		writeQ(_adena);
		writeD(_int); // current INT
		writeC(_int + _henna.getStatINT()); // equip INT
		writeD(_str); // current STR
		writeC(_str + _henna.getStatSTR()); // equip STR
		writeD(_con); // current CON
		writeC(_con + _henna.getStatCON()); // equip CON
		writeD(_men); // current MEM
		writeC(_men + _henna.getStatMEN()); // equip MEM
		writeD(_dex); // current DEX
		writeC(_dex + _henna.getStatDEX()); // equip DEX
		writeD(_wit); // current WIT
		writeC(_wit + _henna.getStatWIT()); // equip WIT
	}
}