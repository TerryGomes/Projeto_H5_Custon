package l2f.gameserver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import l2f.commons.collections.EmptyIterator;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;

public interface PlayerGroup extends Iterable<Player>
{
	public static final PlayerGroup EMPTY = new PlayerGroup()
	{
		@Override
		public Stream<Player> stream()
		{
			return Stream.empty();
		}

		@Override
		public Iterator<Player> iterator()
		{
			return EmptyIterator.getInstance();
		}

		@Override
		public int size()
		{
			return 0;
		}

		@Override
		public Player getLeader()
		{
			return null;
		}

		@Override
		public List<Player> getMembers(Player... excluded)
		{
			return Collections.emptyList();
		}

		@Override
		public boolean containsMember(Player player)
		{
			return false;
		}

		@Override
		public void setReflection(Reflection reflection)
		{

		}
	};

	int size();

	Player getLeader();

	List<Player> getMembers(Player... excluded);

	boolean containsMember(Player player);

	void setReflection(Reflection reflection);

	/**
	 * Badly implemented. Iterates on every call. Useful only for singleton usage, else overriding is suggested.
	 * @return Maximum level of all members in the group.
	 */
	default int getLevel()
	{
		return stream().mapToInt(Player::getLevel).max().orElse(0);
	}

	default void sendPacket(IStaticPacket... packets)
	{
		stream().forEach(p -> p.sendPacket(packets));
	}

	default void sendPacket(Predicate<Player> condition, IStaticPacket... packets)
	{
		stream().filter(condition).forEach(p -> p.sendPacket(packets));
	}

	default void sendPacket(Player exclude, IStaticPacket... packets)
	{
		stream().filter(p -> p != exclude).forEach(p -> p.sendPacket(packets));
	}

	default void sendPacketInRange(GameObject obj, int range, IStaticPacket... packets)
	{
		stream().filter(p -> p.isInRangeZ(obj, range)).forEach(p -> p.sendPacket(packets));
	}

	default void sendMessage(String message)
	{
		stream().forEach(p -> p.sendMessage(message));
	}

	default void sendMessage(CustomMessage string)
	{
		stream().forEach(p -> p.sendMessage(string));
	}

	default void sendMessage(Predicate<Player> condition, String message)
	{
		stream().filter(condition).forEach(p -> p.sendMessage(message));
	}

	default void sendChatMessage(int objectId, int messageType, String charName, String text)
	{
		stream().forEach(p -> p.sendChatMessage(objectId, messageType, charName, text));
	}

	default void sendChatMessage(Predicate<Player> condition, int objectId, int messageType, String charName, String text)
	{
		stream().filter(condition).forEach(p -> p.sendChatMessage(objectId, messageType, charName, text));
	}

	default Stream<Player> stream()
	{
		return getMembers().stream();
	}

	default void forEach(Predicate<Player> condition, Consumer<Player> action)
	{
		stream().filter(condition).forEach(action);
	}

	default boolean isLeader(Player player)
	{
		if (getLeader() == null)
		{
			return false;
		}

		return getLeader() == player;
	}

	default List<Player> getMembersInRange(GameObject obj, int range)
	{
		return stream().filter(member -> member.isInRangeZ(obj, range)).collect(Collectors.toList());
	}

	default int getMemberCountInRange(GameObject obj, int range)
	{
		return (int) stream().filter(member -> member.isInRangeZ(obj, range)).count();
	}

	default List<Integer> getMembersObjIds(Player... excluded)
	{
		return getMembers(excluded).stream().map(Player::getObjectId).collect(Collectors.toList());
	}

	default List<Playable> getMembersWithPets(Player... excluded)
	{
		List<Playable> result = new ArrayList<Playable>();
		for (Player member : getMembers(excluded))
		{
			result.add(member);
			if (member.getPet() != null)
			{
				result.add(member.getPet());
			}
		}
		return result;
	}

	default Player getPlayerByName(String name)
	{
		if (name == null)
		{
			return null;
		}

		return stream().filter(member -> name.equalsIgnoreCase(member.getName())).findAny().orElse(null);
	}

	default Player getPlayer(int objId)
	{
		if (getLeader() != null && getLeader().getObjectId() == objId)
		{
			return getLeader();
		}

		return stream().filter(member -> member != null && member.getObjectId() == objId).findAny().orElse(null);
	}
}
