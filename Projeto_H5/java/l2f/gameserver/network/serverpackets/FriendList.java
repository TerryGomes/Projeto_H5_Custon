package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.instances.player.Friend;

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
		_friends = new ArrayList<FriendInfo>(friends.size());
		for (Map.Entry<Integer, Friend> entry : friends.entrySet())
		{
			Friend friend = entry.getValue();
			FriendInfo f = new FriendInfo();
			f.name = friend.getName();
			f.classId = friend.getClassId();
			f.objectId = entry.getKey();
			f.level = friend.getLevel();
			f.online = friend.isOnline();
			_friends.add(f);
		}
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x58);
		writeD(_friends.size());
		for (FriendInfo f : _friends)
		{
			writeD(f.objectId);
			writeS(f.name);
			writeD(f.online);
			writeD(f.online ? f.objectId : 0);
			writeD(f.classId);
			writeD(f.level);
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
