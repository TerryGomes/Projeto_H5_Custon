package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Summon;

public class ExPartyPetWindowUpdate extends L2GameServerPacket
{
	private int owner_obj_id, npc_id, _type, curHp, maxHp, curMp, maxMp, level;
	private int obj_id = 0;
	private String _name;

	public ExPartyPetWindowUpdate(Summon summon)
	{
		this.obj_id = summon.getObjectId();
		this.owner_obj_id = summon.getPlayer().getObjectId();
		this.npc_id = summon.getTemplate().npcId + 1000000;
		this._type = summon.getSummonType();
		this._name = summon.getName();
		this.curHp = (int) summon.getCurrentHp();
		this.maxHp = summon.getMaxHp();
		this.curMp = (int) summon.getCurrentMp();
		this.maxMp = summon.getMaxMp();
		this.level = summon.getLevel();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x19);
		this.writeD(this.obj_id);
		this.writeD(this.npc_id);
		this.writeD(this._type);
		this.writeD(this.owner_obj_id);
		this.writeS(this._name);
		this.writeD(this.curHp);
		this.writeD(this.maxHp);
		this.writeD(this.curMp);
		this.writeD(this.maxMp);
		this.writeD(this.level);
	}
}