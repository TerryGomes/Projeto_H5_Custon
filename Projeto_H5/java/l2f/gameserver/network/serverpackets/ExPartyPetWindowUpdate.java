package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Summon;

public class ExPartyPetWindowUpdate extends L2GameServerPacket
{
	private int owner_obj_id, npc_id, _type, curHp, maxHp, curMp, maxMp, level;
	private int obj_id = 0;
	private String _name;

	public ExPartyPetWindowUpdate(Summon summon)
	{
		obj_id = summon.getObjectId();
		owner_obj_id = summon.getPlayer().getObjectId();
		npc_id = summon.getTemplate().npcId + 1000000;
		_type = summon.getSummonType();
		_name = summon.getName();
		curHp = (int) summon.getCurrentHp();
		maxHp = summon.getMaxHp();
		curMp = (int) summon.getCurrentMp();
		maxMp = summon.getMaxMp();
		level = summon.getLevel();
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x19);
		writeD(obj_id);
		writeD(npc_id);
		writeD(_type);
		writeD(owner_obj_id);
		writeS(_name);
		writeD(curHp);
		writeD(maxHp);
		writeD(curMp);
		writeD(maxMp);
		writeD(level);
	}
}