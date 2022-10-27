package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.Friend;

/**
 * @author VISTALL
 * @date 23:37/22.03.2011
 */
public class FriendList extends L2GameServerPacket
{
	private List<FriendInfo> _friends = Collections.emptyList();

	public FriendList(Player player)
	{
		Map<Integer, Friend> friends = player.getFriendList().getList();
		this._friends = new ArrayList<FriendInfo>(friends.size());
		for (Map.Entry<Integer, Friend> entry : friends.entrySet())
		{
			Friend friend = entry.getValue();
			FriendInfo f = new FriendInfo();
			f.name = friend.getName();
			f.classId = friend.getClassId();
			f.objectId = entry.getKey();
			f.level = friend.getLevel();
			f.online = friend.isOnline();
			this._friends.add(f);
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeC(0x58);
		this.writeD(this._friends.size());
		for (FriendInfo f : this._friends)
		{
			this.writeD(f.objectId);
			this.writeS(f.name);
			this.writeD(f.online);
			this.writeD(f.online ? f.objectId : 0);
			this.writeD(f.classId);
			this.writeD(f.level);
		}
	}

	private class FriendInfo
	{
		private String name;
		private int objectId;
		private boolean online;
		private int level;
		private int classId;
	}
}
