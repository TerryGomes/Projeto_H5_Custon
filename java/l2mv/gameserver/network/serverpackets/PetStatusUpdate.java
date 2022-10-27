package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Summon;
import l2mv.gameserver.utils.Location;

public class PetStatusUpdate extends L2GameServerPacket
{
	private int type, obj_id, level;
	private int maxFed, curFed, maxHp, curHp, maxMp, curMp;
	private long exp, exp_this_lvl, exp_next_lvl;
	private Location _loc;
	private String title;

	public PetStatusUpdate(Summon summon)
	{
		this.type = summon.getSummonType();
		this.obj_id = summon.getObjectId();
		this._loc = summon.getLoc();
		this.title = summon.getTitle();
		this.curHp = (int) summon.getCurrentHp();
		this.maxHp = summon.getMaxHp();
		this.curMp = (int) summon.getCurrentMp();
		this.maxMp = summon.getMaxMp();
		this.curFed = summon.getCurrentFed();
		this.maxFed = summon.getMaxFed();
		this.level = summon.getLevel();
		this.exp = summon.getExp();
		this.exp_this_lvl = summon.getExpForThisLevel();
		this.exp_next_lvl = summon.getExpForNextLevel();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xb6);
		this.writeD(this.type);
		this.writeD(this.obj_id);
		this.writeD(this._loc.x);
		this.writeD(this._loc.y);
		this.writeD(this._loc.z);
		this.writeS(this.title);
		this.writeD(this.curFed);
		this.writeD(this.maxFed);
		this.writeD(this.curHp);
		this.writeD(this.maxHp);
		this.writeD(this.curMp);
		this.writeD(this.maxMp);
		this.writeD(this.level);
		this.writeQ(this.exp);
		this.writeQ(this.exp_this_lvl);// 0% absolute value
		this.writeQ(this.exp_next_lvl);// 100% absolute value
	}
}