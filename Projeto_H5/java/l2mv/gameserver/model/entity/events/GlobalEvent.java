package l2mv.gameserver.model.entity.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.napile.primitive.Containers;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.maps.impl.TreeIntObjectMap;

import l2mv.commons.collections.MultiValueSet;
import l2mv.commons.listener.Listener;
import l2mv.commons.listener.ListenerList;
import l2mv.commons.logging.LoggerObject;
import l2mv.gameserver.Config;
import l2mv.gameserver.dao.ItemsDAO;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.listener.event.OnStartStopListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.base.RestartType;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2mv.gameserver.model.entity.events.objects.DoorObject;
import l2mv.gameserver.model.entity.events.objects.InitableObject;
import l2mv.gameserver.model.entity.events.objects.SpawnableObject;
import l2mv.gameserver.model.entity.events.objects.ZoneObject;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.taskmanager.actionrunner.ActionRunner;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.TimeUtils;

public abstract class GlobalEvent extends LoggerObject
{
	private class ListenerListImpl extends ListenerList<GlobalEvent>
	{
		public void onStart()
		{
			for (Listener<GlobalEvent> listener : getListeners())
			{
				if (OnStartStopListener.class.isInstance(listener))
				{
					((OnStartStopListener) listener).onStart(GlobalEvent.this);
				}
			}
		}

		public void onStop()
		{
			for (Listener<GlobalEvent> listener : getListeners())
			{
				if (OnStartStopListener.class.isInstance(listener))
				{
					((OnStartStopListener) listener).onStop(GlobalEvent.this);
				}
			}
		}
	}

	public static final String EVENT = "event";

	// actions
	protected final IntObjectMap<List<EventAction>> _onTimeActions = new TreeIntObjectMap<List<EventAction>>();
	protected final List<EventAction> _onStartActions = new ArrayList<EventAction>(0);
	protected final List<EventAction> _onStopActions = new ArrayList<EventAction>(0);
	protected final List<EventAction> _onInitActions = new ArrayList<EventAction>(0);
	// objects
	protected final Map<String, List<Serializable>> _objects = new HashMap<>(0);

	protected final int _id;
	protected final String _name;
	protected final String _timerName;

	protected final ListenerListImpl _listenerList = new ListenerListImpl();

	protected IntObjectMap<ItemInstance> _banishedItems = Containers.emptyIntObjectMap();

	protected GlobalEvent(MultiValueSet<String> set)
	{
		this(set.getInteger("id"), set.getString("name"));
	}

	protected GlobalEvent(int id, String name)
	{
		_id = id;
		_name = name;
		_timerName = id + "_" + name.toLowerCase().replace(" ", "_");
	}

	public void initEvent()
	{
		callActions(_onInitActions);

		reCalcNextTime(true);

		printInfo();
	}

	public void startEvent()
	{
		callActions(_onStartActions);

		_listenerList.onStart();
	}

	public void stopEvent()
	{
		callActions(_onStopActions);

		_listenerList.onStop();
	}

	protected void printInfo()
	{
		final long startSiegeMillis = startTimeMillis();
		if (startSiegeMillis == 0)
		{
			info(getName() + " time - undefined");
		}
		else
		{
			info(getName() + " time - " + TimeUtils.toSimpleFormat(startSiegeMillis));
		}
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "[" + getId() + ";" + getName() + "]";
	}

	// ===============================================================================================================
	// Actions
	// ===============================================================================================================

	protected void callActions(List<EventAction> actions)
	{
		for (EventAction action : actions)
		{
			action.call(this);
		}
	}

	public void addOnStartActions(List<EventAction> start)
	{
		_onStartActions.addAll(start);
	}

	public void addOnStopActions(List<EventAction> start)
	{
		_onStopActions.addAll(start);
	}

	public void addOnInitActions(List<EventAction> start)
	{
		_onInitActions.addAll(start);
	}

	public void addOnTimeAction(int time, EventAction action)
	{
		final List<EventAction> list = _onTimeActions.get(time);
		if (list != null)
		{
			list.add(action);
		}
		else
		{
			final List<EventAction> actions = new ArrayList<EventAction>(1);
			actions.add(action);
			_onTimeActions.put(time, actions);
		}
	}

	public void addOnTimeActions(int time, List<EventAction> actions)
	{
		if (actions.isEmpty())
		{
			return;
		}

		List<EventAction> list = _onTimeActions.get(time);
		if (list != null)
		{
			list.addAll(actions);
		}
		else
		{
			_onTimeActions.put(time, new ArrayList<EventAction>(actions));
		}
	}

	public void timeActions(int time)
	{
		final List<EventAction> actions = _onTimeActions.get(time);
		if (actions == null)
		{
			info("Undefined time : " + time);
			return;
		}

		callActions(actions);
	}

	public int[] timeActions()
	{
		return _onTimeActions.keySet().toArray();
	}

	// ===============================================================================================================
	// Tasks
	// ===============================================================================================================

	public void registerActions()
	{
		final long t = startTimeMillis();
		if (t == 0)
		{
			return;
		}

		for (int key : _onTimeActions.keySet().toArray())
		{
			ActionRunner.getInstance().register(t + key * 1000L, new EventWrapper(_timerName, this, key));
		}
	}

	public void clearActions()
	{
		ActionRunner.getInstance().clear(_timerName);
	}

	// ===============================================================================================================
	// Objects
	// ===============================================================================================================

	@SuppressWarnings("unchecked")
	public <O extends Serializable> List<O> getObjects(String name)
	{
		final List<Serializable> objects = _objects.get(name);
		return objects == null ? Collections.<O>emptyList() : (List<O>) objects;
	}

	@SuppressWarnings("unchecked")
	public <O extends Serializable> List<O> getObjects(String name, Class<O> type)
	{
		List<Serializable> objects = _objects.get(name);
		List<O> objectsFromType = new ArrayList<>();
		if (objects != null)
		{
			for (Serializable object : objects)
			{
				if (type.isAssignableFrom(object.getClass()))
				{
					objectsFromType.add((O) object);
				}
			}
		}
		return objectsFromType;
	}

	@SuppressWarnings("unchecked")
	public <O extends Serializable> O getFirstObject(String name)
	{
		final List<O> objects = getObjects(name);
		return objects.size() > 0 ? (O) objects.get(0) : null;
	}

	public void addObject(String name, Serializable object)
	{
		if (object == null)
		{
			return;
		}

		List<Serializable> list = _objects.get(name);
		if (list != null)
		{
			list.add(object);
		}
		else
		{
			list = new CopyOnWriteArrayList<>();
			list.add(object);
			_objects.put(name, list);
		}
	}

	public void removeObject(String name, Serializable o)
	{
		if (o == null)
		{
			return;
		}

		List<Serializable> list = _objects.get(name);
		if (list != null)
		{
			list.remove(o);
		}
	}

	@SuppressWarnings("unchecked")
	public <O extends Serializable> List<O> removeObjects(String name)
	{
		final List<Serializable> objects = _objects.remove(name);
		return objects == null ? Collections.<O>emptyList() : (List<O>) objects;
	}

	@SuppressWarnings("unchecked")
	public void addObjects(String name, List<? extends Serializable> objects)
	{
		if (objects.isEmpty())
		{
			return;
		}

		List<Serializable> list = _objects.get(name);
		if (list != null)
		{
			list.addAll(objects);
		}
		else
		{
			_objects.put(name, (List<Serializable>) objects);
		}
	}

	public Map<String, List<Serializable>> getObjects()
	{
		return _objects;
	}

	public void spawnAction(String name, boolean spawn)
	{
		final List<Serializable> objects = getObjects(name);
		if (objects.isEmpty())
		{
			info("Undefined objects: " + name);
			return;
		}

		for (Object object : objects)
		{
			if (object instanceof SpawnableObject)
			{
				if (spawn)
				{
					((SpawnableObject) object).spawnObject(this);
				}
				else
				{
					((SpawnableObject) object).despawnObject(this);
				}
			}
		}
	}

	public void doorAction(String name, boolean open)
	{
		final List<Serializable> objects = getObjects(name);
		if (objects.isEmpty())
		{
			info("Undefined objects: " + name);
			return;
		}

		for (Object object : objects)
		{
			if (object instanceof DoorObject)
			{
				if (open)
				{
					((DoorObject) object).open(this);
				}
				else
				{
					((DoorObject) object).close(this);
				}
			}
		}
	}

	public void zoneAction(String name, boolean active)
	{
		final List<Serializable> objects = getObjects(name);
		if (objects.isEmpty())
		{
			info("Undefined objects: " + name);
			return;
		}

		for (Object object : objects)
		{
			if (object instanceof ZoneObject)
			{
				((ZoneObject) object).setActive(active, this);
			}
		}
	}

	public void initAction(String name)
	{
		final List<Serializable> objects = getObjects(name);
		if (objects.isEmpty())
		{
			info("Undefined objects: " + name);
			return;
		}

		for (Object object : objects)
		{
			if (object instanceof InitableObject)
			{
				((InitableObject) object).initObject(this);
			}
		}
	}

	public void action(String name, boolean start)
	{
		if (name.equalsIgnoreCase(EVENT))
		{
			if (start)
			{
				startEvent();
			}
			else
			{
				stopEvent();
			}
		}
	}

	public void refreshAction(String name)
	{
		final List<Serializable> objects = getObjects(name);
		if (objects.isEmpty())
		{
			info("Undefined objects: " + name);
			return;
		}

		for (Object object : objects)
		{
			if (object instanceof SpawnableObject)
			{
				((SpawnableObject) object).refreshObject(this);
			}
		}
	}

	// ===============================================================================================================
	// Abstracts
	// ===============================================================================================================
	public abstract void reCalcNextTime(boolean isServerStarted);

	protected abstract long startTimeMillis();

	// ===============================================================================================================
	// Broadcast
	// ===============================================================================================================
	public void broadcastToWorld(IStaticPacket packet)
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player != null)
			{
				player.sendPacket(packet);
			}
		}
	}

	public static void broadcastToWorld(L2GameServerPacket packet)
	{
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player != null)
			{
				player.sendPacket(packet);
			}
		}
	}

	// ===============================================================================================================
	// Getters & Setters
	// ===============================================================================================================
	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

	public GameObject getCenterObject()
	{
		return null;
	}

	public Reflection getReflection()
	{
		return ReflectionManager.DEFAULT;
	}

	public int getRelation(Player thisPlayer, Player target, int oldRelation)
	{
		return oldRelation;
	}

	public int getUserRelation(Player thisPlayer, int oldRelation)
	{
		return oldRelation;
	}

	public void checkRestartLocs(Player player, Map<RestartType, Boolean> r)
	{
		//
	}

	public Location getRestartLoc(Player player, RestartType type)
	{
		return null;
	}

	public Boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		return null;
	}

	public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		return null;
	}

	public boolean canUseSkill(Creature caster, Creature target, Skill skill)
	{
		return true;
	}

	public boolean isInProgress()
	{
		return false;
	}

	public boolean isParticle(Player player)
	{
		return false;
	}

	public void announce(int a)
	{
		throw new UnsupportedOperationException();
	}

	public void teleportPlayers(String teleportWho)
	{
		throw new UnsupportedOperationException();
	}

	public boolean ifVar(String name)
	{
		throw new UnsupportedOperationException();
	}

	public List<Player> itemObtainPlayers()
	{
		throw new UnsupportedOperationException();
	}

	public void giveItem(Player player, int itemId, long count)
	{
		switch (itemId)
		{
		case -300:
			if (Config.ENABLE_ALT_FAME_REWARD)
			{
				if ((this instanceof CastleSiegeEvent))
				{
					count = Config.ALT_FAME_CASTLE;
				}
				else if ((this instanceof FortressSiegeEvent))
				{
					count = Config.ALT_FAME_FORTRESS;
				}
			}
			player.setFame(player.getFame() + (int) count, toString());
			break;
		default:
			Functions.addItem(player, itemId, count, getName() + " Global Event");
		}
	}

	public List<Player> broadcastPlayers(int range)
	{
		throw new UnsupportedOperationException();
	}

	public boolean canRessurect(Player resurrectPlayer, Creature creature, boolean force)
	{
		return true;
	}

	// ===============================================================================================================
	// setEvent helper
	// ===============================================================================================================
	public void onAddEvent(GameObject o)
	{
		//
	}

	public void onRemoveEvent(GameObject o)
	{
		//
	}

	// ===============================================================================================================
	// Banish items
	// ===============================================================================================================
	public void addBanishItem(ItemInstance item)
	{
		if (_banishedItems.isEmpty())
		{
			_banishedItems = new CHashIntObjectMap<ItemInstance>();
		}

		_banishedItems.put(item.getObjectId(), item);
	}

	public void removeBanishItems()
	{
		final Iterator<IntObjectMap.Entry<ItemInstance>> iterator = _banishedItems.entrySet().iterator();
		while (iterator.hasNext())
		{
			final IntObjectMap.Entry<ItemInstance> entry = iterator.next();
			iterator.remove();

			ItemInstance item = ItemsDAO.getInstance().load(entry.getKey());
			if (item != null)
			{
				if (item.getOwnerId() > 0)
				{
					final GameObject object = GameObjectsStorage.findObject(item.getOwnerId());
					if (object != null && object.isPlayable())
					{
						if (object.isSummon())
						{
							((Summon) object).getInventory().destroyItem(item, "removeBanishItems");
						}
						else
						{
							object.getPlayer().getInventory().destroyItem(item, "removeBanishItems");
						}
						object.getPlayer().sendPacket(SystemMessage2.removeItems(item));
					}
				}
				item.delete();
			}
			else
			{
				item = entry.getValue();
			}

			item.deleteMe();
		}
	}

	// ===============================================================================================================
	// Listeners
	// ===============================================================================================================
	public void addListener(Listener<GlobalEvent> l)
	{
		_listenerList.add(l);
	}

	public void removeListener(Listener<GlobalEvent> l)
	{
		_listenerList.remove(l);
	}

	// ===============================================================================================================
	// Object
	// ===============================================================================================================
	public void cloneTo(GlobalEvent e)
	{
		for (EventAction a : _onInitActions)
		{
			e._onInitActions.add(a);
		}

		for (EventAction a : _onStartActions)
		{
			e._onStartActions.add(a);
		}

		for (EventAction a : _onStopActions)
		{
			e._onStopActions.add(a);
		}

		for (IntObjectMap.Entry<List<EventAction>> entry : _onTimeActions.entrySet())
		{
			e.addOnTimeActions(entry.getKey(), entry.getValue());
		}
	}
}
