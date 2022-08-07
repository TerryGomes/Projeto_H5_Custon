package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.GameTimeController;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.Location;

public class CharSelected extends L2GameServerPacket
{
	// SdSddddddddddffddddddddddddddddddddddddddddddddddddddddd d
	private int _sessionId, char_id, clan_id, sex, race, class_id;
	private String _name, _title;
	private Location _loc;
	private double curHp, curMp;
	private int _sp, level, karma, _int, _str, _con, _men, _dex, _wit, _pk;
	private long _exp;

	public CharSelected(Player cha, int sessionId)
	{
		this._sessionId = sessionId;

		this._name = cha.getName();
		this.char_id = cha.getObjectId(); // FIXME 0x00030b7a ??
		this._title = cha.getTitle();
		this.clan_id = cha.getClanId();
		this.sex = cha.getSex();
		this.race = cha.getRace().ordinal();
		this.class_id = cha.getClassId().getId();
		this._loc = cha.getLoc();
		this.curHp = cha.getCurrentHp();
		this.curMp = cha.getCurrentMp();
		this._sp = cha.getIntSp();
		this._exp = cha.getExp();
		this.level = cha.getLevel();
		this.karma = cha.getKarma();
		this._pk = cha.getPkKills();
		this._int = cha.getINT();
		this._str = cha.getSTR();
		this._con = cha.getCON();
		this._men = cha.getMEN();
		this._dex = cha.getDEX();
		this._wit = cha.getWIT();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x0b);

		this.writeS(this._name);
		this.writeD(this.char_id);
		this.writeS(this._title);
		this.writeD(this._sessionId);
		this.writeD(this.clan_id);
		this.writeD(0x00); // ??
		this.writeD(this.sex);
		this.writeD(this.race);
		this.writeD(this.class_id);
		this.writeD(0x01); // active ??
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeF(this.curHp);
		this.writeF(this.curMp);
		this.writeD(this._sp);
		this.writeQ(this._exp);
		this.writeD(this.level);
		this.writeD(this.karma); // ?
		this.writeD(this._pk);
		this.writeD(this._int);
		this.writeD(this._str);
		this.writeD(this._con);
		this.writeD(this._men);
		this.writeD(this._dex);
		this.writeD(this._wit);
		for (int i = 0; i < 30; i++)
		{
			this.writeD(0x00);
		}

		this.writeF(0x00); // c3 work
		this.writeF(0x00); // c3 work
		// extra info
		this.writeD(GameTimeController.getInstance().getGameTime()); // in-game time
		this.writeD(0x00); //
		this.writeD(0x00); // c3
		this.writeC(0x00); // c3 InspectorBin
		this.writeH(0x00); // c3
		this.writeH(0x00); // c3
		this.writeD(0x00); // c3

	}
}