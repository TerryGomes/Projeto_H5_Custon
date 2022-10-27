package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Summon;

public class ExPartyPetWindowAdd extends L2GameServerPacket
{
	private final int ownerId, npcId, type, curHp, maxHp, curMp, maxMp, level;
	private final int summonId;
	private final String name;

	public ExPartyPetWindowAdd(Summon summon)
	{
		this.summonId = summon.getObjectId();
		this.ownerId = summon.getPlayer().getObjectId();
		this.npcId = summon.getTemplate().npcId + 1000000;
		this.type = summon.getSummonType();
		this.name = summon.getName();
		this.curHp = (int) summon.getCurrentHp();
		this.maxHp = summon.getMaxHp();
		this.curMp = (int) summon.getCurrentMp();
		this.maxMp = summon.getMaxMp();
		this.level = summon.getLevel();
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x18);
		this.writeD(this.summonId);
		this.writeD(this.npcId);
		this.writeD(this.type);
		this.writeD(this.ownerId);
		this.writeS(this.name);
		this.writeD(this.curHp);
		this.writeD(this.maxHp);
		this.writeD(this.curMp);
		this.writeD(this.maxMp);
		this.writeD(this.level);
	}
}