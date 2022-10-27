package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.templates.Henna;

public class HennaItemInfo extends L2GameServerPacket
{
	private int _str, _con, _dex, _int, _wit, _men;
	private long _adena;
	private Henna _henna;

	public HennaItemInfo(Henna henna, Player player)
	{
		this._henna = henna;
		this._adena = player.getAdena();
		this._str = player.getSTR();
		this._dex = player.getDEX();
		this._con = player.getCON();
		this._int = player.getINT();
		this._wit = player.getWIT();
		this._men = player.getMEN();
	}

	@Override
	protected final void writeImpl()
	{

		this.writeC(0xe4);
		this.writeD(this._henna.getSymbolId()); // symbol Id
		this.writeD(this._henna.getDyeId()); // item id of dye
		this.writeQ(this._henna.getDrawCount());
		this.writeQ(this._henna.getPrice());
		this.writeD(1); // able to draw or not 0 is false and 1 is true
		this.writeQ(this._adena);
		this.writeD(this._int); // current INT
		this.writeC(this._int + this._henna.getStatINT()); // equip INT
		this.writeD(this._str); // current STR
		this.writeC(this._str + this._henna.getStatSTR()); // equip STR
		this.writeD(this._con); // current CON
		this.writeC(this._con + this._henna.getStatCON()); // equip CON
		this.writeD(this._men); // current MEM
		this.writeC(this._men + this._henna.getStatMEN()); // equip MEM
		this.writeD(this._dex); // current DEX
		this.writeC(this._dex + this._henna.getStatDEX()); // equip DEX
		this.writeD(this._wit); // current WIT
		this.writeC(this._wit + this._henna.getStatWIT()); // equip WIT
	}
}