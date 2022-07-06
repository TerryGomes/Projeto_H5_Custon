package l2mv.gameserver.model.matching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import l2mv.gameserver.instancemanager.MatchingRoomManager;
import l2mv.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import l2mv.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.PlayerGroup;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.Util;

public abstract class MatchingRoom implements PlayerGroup
{
	private class PartyListenerImpl implements OnPlayerPartyInviteListener, OnPlayerPartyLeaveListener
	{
		@Override
		public void onPartyInvite(Player player)
		{
			broadcastPlayerUpdate(player);
		}

		@Override
		public void onPartyLeave(Player player)
		{
			broadcastPlayerUpdate(player);
		}
	}

	public static int PARTY_MATCHING = 0;
	public static int CC_MATCHING = 1;

	//
	public static int WAIT_PLAYER = 0;
	public static int ROOM_MASTER = 1;
	public static int PARTY_MEMBER = 2;
	public static int UNION_LEADER = 3;
	public static int UNION_PARTY = 4;
	public static int WAIT_PARTY = 5;
	public static int WAIT_NORMAL = 6;

	private final int _id;
	private int _minLevel;
	private int _maxLevel;
	private int _maxMemberSize;
	private int _lootType;
	private String _topic;

	private final PartyListenerImpl _listener = new PartyListenerImpl();
	protected Player _leader;
	protected List<Player> _members = new CopyOnWriteArrayList<Player>();

	public MatchingRoom(Player leader, int minLevel, int maxLevel, int maxMemberSize, int lootType, String topic)
	{
		_leader = leader;
		_id = MatchingRoomManager.getInstance().addMatchingRoom(this);
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_maxMemberSize = maxMemberSize;
		_lootType = lootType;
		_topic = topic;
		addMember0(leader, null, true);
	}

	// ===============================================================================================================================================
	// Add/Remove Member
	// ===============================================================================================================================================
	public boolean addMember(Player player)
	{
		if (_members.contains(player))
		{
			return true;
		}

		if (player.getLevel() < getMinLevel() || player.getLevel() > getMaxLevel() || getPlayers().size() >= getMaxMembersSize())
		{
			player.sendPacket(notValidMessage());
			return false;
		}
		return addMember0(player, new SystemMessage2(enterMessage()).addName(player), true);
	}

	public boolean addMemberForce(Player player)
	{
		if (_members.contains(player))
		{
			return true;
		}
		if (getPlayers().size() >= getMaxMembersSize())
		{
			player.sendPacket(notValidMessage());
			return false;
		}
		return addMember0(player, new SystemMessage2(enterMessage()).addName(player), false);
	}

	private boolean addMember0(Player player, L2GameServerPacket p, boolean sendInfo)
	{
		if (!_members.isEmpty())
		{
			player.addListener(_listener);
		}

		_members.add(player);

		player.setMatchingRoom(this);

		for (Player $member : this)
		{
			if ($member != player && $member.isMatchingRoomWindowOpened())
			{
				$member.sendPacket(p, addMemberPacket($member, player));
			}
		}

		MatchingRoomManager.getInstance().removeFromWaitingList(player);
		if (sendInfo)
		{
			player.setMatchingRoomWindowOpened(true);
			player.sendPacket(infoRoomPacket(), membersPacket(player));
		}
		player.sendChanges();
		return true;
	}

	public void removeMember(Player member, boolean oust)
	{
		if (!_members.remove(member))
		{
			return;
		}

		member.removeListener(_listener);
		member.setMatchingRoom(null);
		if (_members.isEmpty())
		{
			disband();
		}
		else
		{
			final L2GameServerPacket infoPacket = infoRoomPacket();
			final SystemMsg exitMessage0 = exitMessage(true, oust);
			final L2GameServerPacket exitMessage = exitMessage0 != null ? new SystemMessage2(exitMessage0).addName(member) : null;
			for (Player player : this)
			{
				if (player.isMatchingRoomWindowOpened())
				{
					player.sendPacket(infoPacket, removeMemberPacket(player, member), exitMessage);
				}
			}
		}

		member.sendPacket(closeRoomPacket(), exitMessage(false, oust));
		member.setMatchingRoomWindowOpened(false);
		// MatchingRoomManager.getInstance().addToWaitingList(member);
		member.sendChanges();
	}

	public void broadcastPlayerUpdate(Player player)
	{
		for (Player $member : MatchingRoom.this)
		{
			if ($member.isMatchingRoomWindowOpened())
			{
				$member.sendPacket(updateMemberPacket($member, player));
			}
		}
	}

	public void disband()
	{
		for (Player player : this)
		{
			player.removeListener(_listener);
			if (player.isMatchingRoomWindowOpened())
			{
				player.sendPacket(closeRoomMessage());
				player.sendPacket(closeRoomPacket());
			}
			player.setMatchingRoom(null);
			player.sendChanges();
		}
		_members.clear();
		MatchingRoomManager.getInstance().removeMatchingRoom(this);
	}

	public void setLeader(Player leader)
	{
		_leader = leader;
		if (!_members.contains(leader))
		{
			addMember0(leader, null, true);
		}
		else if (!leader.isMatchingRoomWindowOpened())
		{
			leader.setMatchingRoomWindowOpened(true);
			leader.sendPacket(infoRoomPacket(), membersPacket(leader));
		}
	}

	// ===============================================================================================================================================
	// Abstracts
	// ===============================================================================================================================================
	public abstract SystemMsg notValidMessage();

	public abstract SystemMsg enterMessage();

	public abstract SystemMsg exitMessage(boolean toOthers, boolean kick);

	public abstract SystemMsg closeRoomMessage();

	public abstract L2GameServerPacket closeRoomPacket();

	public abstract L2GameServerPacket infoRoomPacket();

	public abstract L2GameServerPacket addMemberPacket(Player $member, Player active);

	public abstract L2GameServerPacket removeMemberPacket(Player $member, Player active);

	public abstract L2GameServerPacket updateMemberPacket(Player $member, Player active);

	public abstract L2GameServerPacket membersPacket(Player active);

	public abstract int getType();

	public abstract int getMemberType(Player member);

	// ===============================================================================================================================================
	// Broadcast
	// ===============================================================================================================================================
	@Override
	public void sendPacket(IStaticPacket... arg)
	{
		for (Player player : this)
		{
			player.sendPacket(arg);
		}
	}

	@Override
	public void sendMessage(String message)
	{
		for (Player member : _members)
		{
			member.sendMessage(message);
		}
	}

	@Override
	public void sendChatMessage(int objectId, int messageType, String charName, String text)
	{
		for (Player member : _members)
		{
			member.sendChatMessage(objectId, messageType, charName, text);
		}
	}

	// ===============================================================================================================================================
	// Getters
	// ===============================================================================================================================================
	public int getId()
	{
		return _id;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	public String getTopic()
	{
		return _topic;
	}

	public int getMaxMembersSize()
	{
		return _maxMemberSize;
	}

	public int getLocationId()
	{
		return MatchingRoomManager.getInstance().getLocation(_leader);
	}

	public Collection<Player> getPlayers()
	{
		return _members;
	}

	public int getLootType()
	{
		return _lootType;
	}

	@Override
	public Iterator<Player> iterator()
	{
		return _members.iterator();
	}

	@Override
	public int size()
	{
		return _members.size();
	}

	@Override
	public Player getLeader()
	{
		return _leader;
	}

	@Override
	public boolean isLeader(Player player)
	{
		return _leader == player;
	}

	@Override
	public List<Player> getMembers(Player... excluded)
	{
		if (excluded != null && excluded.length > 0)
		{
			List<Player> members = new ArrayList<Player>();
			for (Player member : _members)
			{
				if (!Util.arrayContains(excluded, member))
				{
					members.add(member);
				}
			}

			return members;
		}

		return _members;
	}

	@Override
	public boolean containsMember(Player player)
	{
		return _members.contains(player);
	}

	// ===============================================================================================================================================
	// Setters
	// ===============================================================================================================================================
	public void setMinLevel(int minLevel)
	{
		_minLevel = minLevel;
	}

	public void setMaxLevel(int maxLevel)
	{
		_maxLevel = maxLevel;
	}

	public void setTopic(String topic)
	{
		_topic = topic;
	}

	public void setMaxMemberSize(int maxMemberSize)
	{
		_maxMemberSize = maxMemberSize;
	}

	public void setLootType(int lootType)
	{
		_lootType = lootType;
	}
}