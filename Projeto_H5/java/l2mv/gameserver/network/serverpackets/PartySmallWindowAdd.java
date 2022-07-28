package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class PartySmallWindowAdd extends L2GameServerPacket
{
	private int objectId;
	private final PartySmallWindowAll.PartySmallWindowMemberInfo member;

	public PartySmallWindowAdd(Player player, Player member)
	{
		this.objectId = player.getObjectId();
		this.member = new PartySmallWindowAll.PartySmallWindowMemberInfo(member);
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x4F);
		this.writeD(this.objectId); // c3
		this.writeD(0);// writeD(0x04); ?? //c3
		this.writeD(this.member._id);
		this.writeS(this.member._name);
		this.writeD(this.member.curCp);
		this.writeD(this.member.maxCp);
		this.writeD(this.member.curHp);
		this.writeD(this.member.maxHp);
		this.writeD(this.member.curMp);
		this.writeD(this.member.maxMp);
		this.writeD(this.member.level);
		this.writeD(this.member.class_id);
		this.writeD(0);// writeD(0x01); ??
		this.writeD(this.member.race_id);
		this.writeD(0);
		this.writeD(0);
	}
}