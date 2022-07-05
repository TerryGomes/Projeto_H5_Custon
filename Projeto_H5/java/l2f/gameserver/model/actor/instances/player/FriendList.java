package l2f.gameserver.model.actor.instances.player;

import java.sql.Connection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.dao.CharacterFriendDAO;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.network.serverpackets.L2Friend;
import l2f.gameserver.network.serverpackets.L2FriendStatus;
import l2f.gameserver.network.serverpackets.SystemMessage;

public class FriendList
{
	private Map<Integer, Friend> _friendList = Collections.emptyMap();
	private final Player _owner;

	public FriendList(Player owner)
	{
		_owner = owner;
	}

	public void restore(Connection con)
	{
		_friendList = CharacterFriendDAO.select(_owner, con);
	}

	public void removeFriend(String name)
	{
		if (StringUtils.isEmpty(name))
		{
			return;
		}
		int objectId = removeFriend0(name);
		if (objectId > 0)
		{
			Player friendChar = World.getPlayer(objectId);

			_owner.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIEND_LIST).addString(name), new L2Friend(name, false, friendChar != null, objectId));

			if (friendChar != null)
			{
				friendChar.sendPacket(new SystemMessage(SystemMessage.S1__HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST).addString(_owner.getName()), new L2Friend(_owner, false));
			}
		}
		else
		{
			_owner.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_ON_YOUR_FRIEND_LIST).addString(name));
		}
	}

	public void notifyFriends(boolean login)
	{
		for (Friend friend : _friendList.values())
		{
			Player friendPlayer = GameObjectsStorage.getPlayer(friend.getObjectId());
			if (friendPlayer != null)
			{
				Friend thisFriend = friendPlayer.getFriendList().getList().get(_owner.getObjectId());
				if (thisFriend == null)
				{
					continue;
				}

				thisFriend.update(_owner, login);

				if (login)
				{
					friendPlayer.sendPacket(new SystemMessage(SystemMessage.S1_FRIEND_HAS_LOGGED_IN).addString(_owner.getName()));
				}

				friendPlayer.sendPacket(new L2FriendStatus(_owner, login));

				friend.update(friendPlayer, login);
			}
		}
	}

	public void addFriend(Player friendPlayer)
	{
		_friendList.put(friendPlayer.getObjectId(), new Friend(friendPlayer));

		CharacterFriendDAO.getInstance().insert(_owner, friendPlayer);
	}

	private int removeFriend0(String name)
	{
		if (name == null)
		{
			return 0;
		}

		Integer objectId = 0;
		for (Map.Entry<Integer, Friend> entry : _friendList.entrySet())
		{
			if (name.equalsIgnoreCase(entry.getValue().getName()))
			{
				objectId = entry.getKey();
				break;
			}
		}

		if (objectId > 0)
		{
			_friendList.remove(objectId);
			CharacterFriendDAO.getInstance().delete(_owner, objectId);
			return objectId;
		}
		return 0;
	}

	public Map<Integer, Friend> getList()
	{
		return _friendList;
	}

	@Override
	public String toString()
	{
		return "FriendList[owner=" + _owner.getName() + "]";
	}
}
