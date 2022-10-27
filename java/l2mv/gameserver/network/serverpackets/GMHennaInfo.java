package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.templates.Henna;

//ccccccdd[dd]
public class GMHennaInfo extends L2GameServerPacket
{
	private int _count, _str, _con, _dex, _int, _wit, _men;
	private final Henna[] _hennas = new Henna[3];

	public GMHennaInfo(Player cha)
	{
		this._str = cha.getHennaStatSTR();
		this._con = cha.getHennaStatCON();
		this._dex = cha.getHennaStatDEX();
		this._int = cha.getHennaStatINT();
		this._wit = cha.getHennaStatWIT();
		this._men = cha.getHennaStatMEN();

		int j = 0;
		for (int i = 0; i < 3; i++)
		{
			Henna h = cha.getHenna(i + 1);
			if (h != null)
			{
				this._hennas[j++] = h;
			}
		}
		this._count = j;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xf0);

		this.writeC(this._int);
		this.writeC(this._str);
		this.writeC(this._con);
		this.writeC(this._men);
		this.writeC(this._dex);
		this.writeC(this._wit);
		this.writeD(3);
		this.writeD(this._count);
		for (int i = 0; i < this._count; i++)
		{
			this.writeD(this._hennas[i].getSymbolId());
			this.writeD(this._hennas[i].getSymbolId());
		}
	}
}