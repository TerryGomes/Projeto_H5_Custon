package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.entity.residence.Residence;
import l2mv.gameserver.model.instances.DoorInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.StaticObjectInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.Location;

public abstract class SysMsgContainer<T extends SysMsgContainer<T>> extends L2GameServerPacket
{
	public static enum Types
	{
		TEXT, // 0
		NUMBER, // 1
		NPC_NAME, // 2
		ITEM_NAME, // 3
		SKILL_NAME, // 4
		RESIDENCE_NAME, // 5
		LONG, // 6
		ZONE_NAME, // 7
		ITEM_NAME_WITH_AUGMENTATION, // 8
		ELEMENT_NAME, // 9
		INSTANCE_NAME, // 10 d
		STATIC_OBJECT_NAME, // 11
		PLAYER_NAME, // 12 S
		SYSTEM_STRING // 13 d
	}

	protected SystemMsg _message;
	protected List<IArgument> _arguments;

	// @Deprecated
	protected SysMsgContainer(int messageId)
	{
		this(SystemMsg.valueOf(messageId));
	}

	protected SysMsgContainer(String message)
	{
		this.addString(message);
	}

	protected SysMsgContainer(SystemMsg message)
	{
		if (message == null)
		{
			throw new IllegalArgumentException("SystemMsg is null");
		}

		this._message = message;
		this._arguments = new ArrayList<IArgument>(this._message.size());
	}

	protected void writeElements()
	{
		if (this._message.size() != this._arguments.size())
		{
			throw new IllegalArgumentException("Wrong count of arguments: " + this._message);
		}

		this.writeD(this._message.getId());
		this.writeD(this._arguments.size());
		for (IArgument argument : this._arguments)
		{
			argument.write(this);
		}
	}

	// ==================================================================================================
	public T addName(GameObject object)
	{
		if (object == null)
		{
			return this.add(new StringArgument(null));
		}

		if (object.isNpc())
		{
			return this.add(new NpcNameArgument(((NpcInstance) object).getNpcId() + 1000000));
		}
		else if (object instanceof Summon)
		{
			return this.add(new NpcNameArgument(((Summon) object).getNpcId() + 1000000));
		}
		else if (object.isItem())
		{
			return this.add(new ItemNameArgument(((ItemInstance) object).getItemId()));
		}
		else if (object.isPlayer())
		{
			return this.add(new PlayerNameArgument((Player) object));
		}
		else if (object.isDoor())
		{
			return this.add(new StaticObjectNameArgument(((DoorInstance) object).getDoorId()));
		}
		else if (object instanceof StaticObjectInstance)
		{
			return this.add(new StaticObjectNameArgument(((StaticObjectInstance) object).getUId()));
		}

		return this.add(new StringArgument(object.getName()));
	}

	public T addInstanceName(int id)
	{
		return this.add(new InstanceNameArgument(id));
	}

	public T addSysString(int id)
	{
		return this.add(new SysStringArgument(id));
	}

	public T addSkillName(Skill skill)
	{
		return this.addSkillName(skill.getDisplayId(), skill.getDisplayLevel());
	}

	public T addSkillName(int id, int level)
	{
		return this.add(new SkillArgument(id, level));
	}

	public T addItemName(int item_id)
	{
		return this.add(new ItemNameArgument(item_id));
	}

	public T addItemNameWithAugmentation(ItemInstance item)
	{
		return this.add(new ItemNameWithAugmentationArgument(item.getItemId(), item.getAugmentationId()));
	}

	public T addZoneName(Creature c)
	{
		return this.addZoneName(c.getX(), c.getY(), c.getZ());
	}

	public T addZoneName(Location loc)
	{
		return this.add(new ZoneArgument(loc.x, loc.y, loc.z));
	}

	public T addZoneName(int x, int y, int z)
	{
		return this.add(new ZoneArgument(x, y, z));
	}

	public T addResidenceName(Residence r)
	{
		return this.add(new ResidenceArgument(r.getId()));
	}

	public T addResidenceName(int i)
	{
		return this.add(new ResidenceArgument(i));
	}

	public T addElementName(int i)
	{
		return this.add(new ElementNameArgument(i));
	}

	public T addElementName(Element i)
	{
		return this.add(new ElementNameArgument(i.getId()));
	}

	public T addInteger(double i)
	{
		return this.add(new IntegerArgument((int) i));
	}

	public T addLong(long i)
	{
		return this.add(new LongArgument(i));
	}

	public T addString(String t)
	{
		return this.add(new StringArgument(t));
	}

	public T addNumber(int number)
	{
		return this.add(new IntegerArgument(number));
	}

	@SuppressWarnings("unchecked")
	public T add(IArgument arg)
	{
		this._arguments.add(arg);

		return (T) this;
	}
	// ==================================================================================================
	// Суппорт классы, собственна реализация (не L2jFree)
	// ==================================================================================================

	@SuppressWarnings("rawtypes")
	public static abstract class IArgument
	{
		void write(SysMsgContainer m)
		{
			m.writeD(this.getType().ordinal());

			this.writeData(m);
		}

		abstract Types getType();

		abstract void writeData(SysMsgContainer message);
	}

	@SuppressWarnings("rawtypes")
	public static class IntegerArgument extends IArgument
	{
		private final int _data;

		public IntegerArgument(int da)
		{
			this._data = da;
		}

		@Override
		public void writeData(SysMsgContainer message)
		{
			message.writeD(this._data);
		}

		@Override
		Types getType()
		{
			return Types.NUMBER;
		}
	}

	public static class NpcNameArgument extends IntegerArgument
	{
		public NpcNameArgument(int da)
		{
			super(da);
		}

		@Override
		Types getType()
		{
			return Types.NPC_NAME;
		}
	}

	public static class ItemNameArgument extends IntegerArgument
	{
		public ItemNameArgument(int da)
		{
			super(da);
		}

		@Override
		Types getType()
		{
			return Types.ITEM_NAME;
		}
	}

	@SuppressWarnings("rawtypes")
	public static class ItemNameWithAugmentationArgument extends IArgument
	{
		private final int _itemId;
		private final int _augmentationId;

		public ItemNameWithAugmentationArgument(int itemId, int augmentationId)
		{
			this._itemId = itemId;
			this._augmentationId = augmentationId;
		}

		@Override
		Types getType()
		{
			return Types.ITEM_NAME_WITH_AUGMENTATION;
		}

		@Override
		void writeData(SysMsgContainer message)
		{
			message.writeD(this._itemId);
			message.writeD(this._augmentationId);
		}
	}

	public static class InstanceNameArgument extends IntegerArgument
	{
		public InstanceNameArgument(int da)
		{
			super(da);
		}

		@Override
		Types getType()
		{
			return Types.INSTANCE_NAME;
		}
	}

	public static class SysStringArgument extends IntegerArgument
	{
		public SysStringArgument(int da)
		{
			super(da);
		}

		@Override
		Types getType()
		{
			return Types.SYSTEM_STRING;
		}
	}

	public static class ResidenceArgument extends IntegerArgument
	{
		public ResidenceArgument(int da)
		{
			super(da);
		}

		@Override
		Types getType()
		{
			return Types.RESIDENCE_NAME;
		}
	}

	public static class StaticObjectNameArgument extends IntegerArgument
	{
		public StaticObjectNameArgument(int da)
		{
			super(da);
		}

		@Override
		Types getType()
		{
			return Types.STATIC_OBJECT_NAME;
		}
	}

	@SuppressWarnings("rawtypes")
	public static class LongArgument extends IArgument
	{
		private final long _data;

		public LongArgument(long da)
		{
			this._data = da;
		}

		@Override
		void writeData(SysMsgContainer message)
		{
			message.writeQ(this._data);
		}

		@Override
		Types getType()
		{
			return Types.LONG;
		}
	}

	@SuppressWarnings("rawtypes")
	public static class StringArgument extends IArgument
	{
		private final String _data;

		public StringArgument(String da)
		{
			this._data = da == null ? "null" : da;
		}

		@Override
		void writeData(SysMsgContainer message)
		{
			message.writeS(this._data);
		}

		@Override
		Types getType()
		{
			return Types.TEXT;
		}
	}

	@SuppressWarnings("rawtypes")
	public static class SkillArgument extends IArgument
	{
		private final int _skillId;
		private final int _skillLevel;

		public SkillArgument(int t1, int t2)
		{
			this._skillId = t1;
			this._skillLevel = t2;
		}

		@Override
		void writeData(SysMsgContainer message)
		{
			message.writeD(this._skillId);
			message.writeD(this._skillLevel);
		}

		@Override
		Types getType()
		{
			return Types.SKILL_NAME;
		}
	}

	@SuppressWarnings("rawtypes")
	public static class ZoneArgument extends IArgument
	{
		private final int _x;
		private final int _y;
		private final int _z;

		public ZoneArgument(int t1, int t2, int t3)
		{
			this._x = t1;
			this._y = t2;
			this._z = t3;
		}

		@Override
		void writeData(SysMsgContainer message)
		{
			message.writeD(this._x);
			message.writeD(this._y);
			message.writeD(this._z);
		}

		@Override
		Types getType()
		{
			return Types.ZONE_NAME;
		}
	}

	public static class ElementNameArgument extends IntegerArgument
	{
		public ElementNameArgument(int type)
		{
			super(type);
		}

		@Override
		Types getType()
		{
			return Types.ELEMENT_NAME;
		}
	}

	public static class PlayerNameArgument extends StringArgument
	{
		public PlayerNameArgument(Creature creature)
		{
			super(creature.getName());
		}

		@Override
		Types getType()
		{
			return Types.PLAYER_NAME;
		}
	}
}
