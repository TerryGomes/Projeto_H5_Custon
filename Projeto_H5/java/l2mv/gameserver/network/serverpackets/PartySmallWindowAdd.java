package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class PartySmallWindowAdd extends L2GameServerPacket
{
	private int objectId;
	private final PartySmallWindowAll.PartySmallWindowMemberInfo member;

	public PartySmallWindowAdd(Player player, Player member)
	{
		objectId = player.getObjectId();
		this.member = new PartySmallWindowAll.PartySmallWindowMemberInfo(member);
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x4F);
		writeD(objectId); // c3
		writeD(0);// writeD(0x04); ?? //c3
		writeD(member._id);
		writeS(member._name);
		writeD(member.curCp);
		writeD(member.maxCp);
		writeD(member.curHp);
		writeD(member.maxHp);
		writeD(member.curMp);
		writeD(member.maxMp);
		writeD(member.level);
		writeD(member.class_id);
		writeD(0);// writeD(0x01); ??
		writeD(member.race_id);
		writeD(0);
		writeD(0);
	}
}