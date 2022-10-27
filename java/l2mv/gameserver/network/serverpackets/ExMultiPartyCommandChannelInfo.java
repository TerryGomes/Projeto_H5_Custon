package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.CommandChannel;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;

public class ExMultiPartyCommandChannelInfo extends L2GameServerPacket
{
	private String ChannelLeaderName;
	private int MemberCount;
	private List<ChannelPartyInfo> parties;

	public ExMultiPartyCommandChannelInfo(CommandChannel channel)
	{
		this.ChannelLeaderName = channel.getLeader().getName();
		this.MemberCount = channel.size();

		this.parties = new ArrayList<ChannelPartyInfo>();
		for (Party party : channel.getParties())
		{
			Player leader = party.getLeader();
			if (leader != null)
			{
				this.parties.add(new ChannelPartyInfo(leader.getName(), leader.getObjectId(), party.size()));
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x31);
		this.writeS(this.ChannelLeaderName); // имя лидера CC
		this.writeD(0); // Looting type?
		this.writeD(this.MemberCount); // общее число человек в СС
		this.writeD(this.parties.size()); // общее число партий в СС

		for (ChannelPartyInfo party : this.parties)
		{
			this.writeS(party.Leader_name); // имя лидера партии
			this.writeD(party.Leader_obj_id); // ObjId пати лидера
			this.writeD(party.MemberCount); // количество мемберов в пати
		}
	}

	static class ChannelPartyInfo
	{
		public String Leader_name;
		public int Leader_obj_id, MemberCount;

		public ChannelPartyInfo(String _Leader_name, int _Leader_obj_id, int _MemberCount)
		{
			this.Leader_name = _Leader_name;
			this.Leader_obj_id = _Leader_obj_id;
			this.MemberCount = _MemberCount;
		}
	}
}