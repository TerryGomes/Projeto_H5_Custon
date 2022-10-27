package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.Friend;

public class L2FriendList extends L2GameServerPacket
{
	private List<FriendInfo> _list = Collections.emptyList();

	public L2FriendList(Player player)
	{
		Map<Integer, Friend> list = player.getFriendList().getList();
		this._list = new ArrayList<FriendInfo>(list.size());
		for (Map.Entry<Integer, Friend> entry : list.entrySet())
		{
			FriendInfo f = new FriendInfo();
			f._objectId = entry.getKey();
			f._name = entry.getValue().getName();
			f._online = entry.getValue().isOnline();
			this._list.add(f);
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x75);
		this.writeD(this._list.size());
		for (FriendInfo friendInfo : this._list)
		{
			this.writeD(0);
			this.writeS(friendInfo._name); // name
			this.writeD(friendInfo._online ? 1 : 0); // online or offline
			this.writeD(friendInfo._objectId); // object_id
		}
	}

	private static class FriendInfo
	{
		private int _objectId;
		private String _name;
		private boolean _online;
	}
}