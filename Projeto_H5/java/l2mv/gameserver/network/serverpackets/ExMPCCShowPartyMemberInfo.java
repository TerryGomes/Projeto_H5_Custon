package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;

/**
 * Format: ch d[Sdd]
 * @author SYS
 */
public class ExMPCCShowPartyMemberInfo extends L2GameServerPacket
{
	private List<PartyMemberInfo> members;

	public ExMPCCShowPartyMemberInfo(Party party)
	{
		this.members = new ArrayList<PartyMemberInfo>();
		for (Player _member : party.getMembers())
		{
			this.members.add(new PartyMemberInfo(_member.getName(), _member.getObjectId(), _member.getClassId().getId()));
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x4b);
		this.writeD(this.members.size()); // Количество членов в пати

		for (PartyMemberInfo member : this.members)
		{
			this.writeS(member.name); // Имя члена пати
			this.writeD(member.object_id); // object Id члена пати
			this.writeD(member.class_id); // id класса члена пати
		}

		this.members.clear();
	}

	static class PartyMemberInfo
	{
		public String name;
		public int object_id, class_id;

		public PartyMemberInfo(String _name, int _object_id, int _class_id)
		{
			this.name = _name;
			this.object_id = _object_id;
			this.class_id = _class_id;
		}
	}
}