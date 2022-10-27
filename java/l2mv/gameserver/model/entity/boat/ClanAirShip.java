package l2mv.gameserver.model.entity.boat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.lang.reference.HardReferences;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.BoatHolder;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.actions.StartStopAction;
import l2mv.gameserver.model.entity.events.impl.BoatWayEvent;
import l2mv.gameserver.model.entity.events.objects.BoatPoint;
import l2mv.gameserver.model.instances.ControlKeyInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.DeleteObject;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.AirshipDock;
import l2mv.gameserver.utils.Location;

public class ClanAirShip extends AirShip
{
	private static final long MAINTENANCE_DELAY = 60 * 1000L;
	public static final int MAX_FUEL = 600;
	private int _currentFuel;

	private AirshipDock _dock;
	private AirshipDock.AirshipPlatform _platform;

	private HardReference<Player> _driverRef = HardReferences.emptyRef();
	private GameObject _controlKey = new ControlKeyInstance();
	private Clan _clan;

	private boolean _customMove;
	private Future<?> _deleteTask = null;

	public ClanAirShip(Clan clan)
	{
		super(IdFactory.getInstance().getNextId(), BoatHolder.TEMPLATE);

		BoatHolder.getInstance().addBoat(this);
		_clan = clan;
		_clan.setAirship(this);
		_currentFuel = clan.getAirshipFuel();
	}

	@Override
	public void onSpawn()
	{
		_controlKey.spawnMe(getLoc());
	}

	@Override
	protected void updatePeopleInTheBoat(int x, int y, int z)
	{
		super.updatePeopleInTheBoat(x, y, z);
		_controlKey.setXYZ(x, y, z);
	}

	@Override
	public void oustPlayer(Player player, Location loc, boolean teleport)
	{
		if (player == getDriver())
		{
			setDriver(null);
		}

		super.oustPlayer(player, loc, teleport);
	}

	public void startDepartTask()
	{
		BoatWayEvent arrivalWay = new BoatWayEvent(this);
		BoatWayEvent departWay = new BoatWayEvent(this);

		for (BoatPoint p : _platform.getArrivalPoints())
		{
			arrivalWay.addObject(BoatWayEvent.BOAT_POINTS, p);
		}

		for (BoatPoint p : _platform.getDepartPoints())
		{
			departWay.addObject(BoatWayEvent.BOAT_POINTS, p);
		}

		arrivalWay.addOnTimeAction(0, new StartStopAction(StartStopAction.EVENT, true));
		departWay.addOnTimeAction(300, new StartStopAction(StartStopAction.EVENT, true));

		setWay(0, arrivalWay);
		setWay(1, departWay);

		arrivalWay.reCalcNextTime(false);
	}

	public void startArrivalTask()
	{
		if (_deleteTask != null)
		{
			_deleteTask.cancel(true);
			_deleteTask = null;
		}

		for (Player player : _players)
		{
			player.showQuestMovie(_platform.getOustMovie());

			oustPlayer(player, getReturnLoc(), true);
		}

		deleteMe();
	}

	public void addTeleportPoint(Player player, int id)
	{
		if (isMoving || !isDocked())
		{
			return;
		}

		if (id == 0)
		{
			getCurrentWay().clearActions();
			getCurrentWay().startEvent();
		}
		else
		{
			BoatPoint point = getDock().getTeleportList().get(id);

			if (getCurrentFuel() < point.getFuel())
			{
				player.sendPacket(SystemMsg.YOUR_SHIP_CANNOT_TELEPORT_BECAUSE_IT_DOES_NOT_HAVE_ENOUGH_FUEL_FOR_THE_TRIP);
				return;
			}

			setCurrentFuel(getCurrentFuel() - point.getFuel());

			getCurrentWay().clearActions();
			getCurrentWay().addObject(BoatWayEvent.BOAT_POINTS, point);
			getCurrentWay().startEvent();
		}
	}

	@Override
	public void trajetEnded(boolean oust)
	{
		_runState = 0;

		if (_fromHome == 0)
		{
			_fromHome = 1;
			getCurrentWay().reCalcNextTime(false);
		}
		else
		{
			_customMove = true;
			_deleteTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new FuelAndDeleteTask(), MAINTENANCE_DELAY, MAINTENANCE_DELAY);
		}
	}

	@Override
	public void onEvtArrived()
	{
		if (!_customMove)
		{
			getCurrentWay().moveNext();
		}
	}

	public void setDriver(Player player)
	{
		if (player != null)
		{
			if (_clan != player.getClan())
			{
				return;
			}

			if (player.getTargetId() != _controlKey.getObjectId())
			{
				player.sendPacket(SystemMsg.YOU_MUST_TARGET_THE_ONE_YOU_WISH_TO_CONTROL);
				return;
			}

			final int x = player.getInBoatPosition().x - 0x16e;
			final int y = player.getInBoatPosition().y;
			final int z = player.getInBoatPosition().z - 0x6b;
			if (x * x + y * y + z * z > 2500)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR);
				return;
			}

			if (player.getTransformation() != 0)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_TRANSFORMED);
				return;
			}

			if (player.isParalyzed())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_YOU_ARE_PETRIFIED);
				return;
			}

			if (player.isDead() || player.isFakeDeath())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHEN_YOU_ARE_DEAD);
				return;
			}

			if (player.isFishing())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_FISHING);
				return;
			}

			if (player.isInCombat())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_BATTLE);
				return;
			}

			if (player.isInDuel())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_DUEL);
				return;
			}

			if (player.isSitting())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_SITTING_POSITION);
				return;
			}

			if (player.isCastingNow())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_USING_A_SKILL);
				return;
			}

			if (player.isCursedWeaponEquipped())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_A_CURSED_WEAPON_IS_EQUIPPED);
				return;
			}

			if (player.getActiveWeaponFlagAttachment() != null)
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_CONTROL_THE_HELM_WHILE_HOLDING_A_FLAG);
				return;
			}

			_driverRef = player.getRef();

			player.setLockedTarget(true);
			player.unEquipWeapon();
			player.broadcastCharInfo();
		}
		else
		{
			Player oldDriver = getDriver();

			_driverRef = HardReferences.emptyRef();

			if (oldDriver != null)
			{
				oldDriver.setLockedTarget(false);
				oldDriver.broadcastCharInfo();
			}
		}

		broadcastCharInfo();
	}

	public void setCurrentFuel(int fuel)
	{
		final int old = _currentFuel;
		_currentFuel = fuel;
		if (_currentFuel <= 0)
		{
			_currentFuel = 0;
			setMoveSpeed(150);
			setRotationSpeed(1000);
		}
		else if (_currentFuel > MAX_FUEL)
		{
			_currentFuel = MAX_FUEL;
		}

		if (_currentFuel == 0 && old > 0)
		{
			broadcastPacketToPassengers(SystemMsg.THE_AIRSHIPS_FUEL_EP_HAS_RUN_OUT);
		}
		else if (_currentFuel < 40)
		{
			broadcastPacketToPassengers(SystemMsg.THE_AIRSHIPS_FUEL_EP_WILL_SOON_RUN_OUT);
		}

		broadcastCharInfo();
	}

	public int getCurrentFuel()
	{
		return _currentFuel;
	}

	public int getMaxFuel()
	{
		return MAX_FUEL;
	}

	public Player getDriver()
	{
		return _driverRef.get();
	}

	public GameObject getControlKey()
	{
		return _controlKey;
	}

	@Override
	protected void onDelete()
	{
		_clan.setAirship(null);
		_clan.setAirshipFuel(_currentFuel);
		_clan.updateClanInDB();

		IdFactory.getInstance().releaseId(_controlKey.getObjectId());
		BoatHolder.getInstance().removeBoat(this);

		super.onDelete();
	}

	@Override
	public Location getReturnLoc()
	{
		return _platform == null ? null : _platform.getOustLoc();
	}

	@Override
	public Clan getClan()
	{
		return _clan;
	}

	public void setDock(AirshipDock dockId)
	{
		_dock = dockId;
	}

	public void setPlatform(AirshipDock.AirshipPlatform platformId)
	{
		_platform = platformId;
	}

	public AirshipDock getDock()
	{
		return _dock;
	}

	public boolean isCustomMove()
	{
		return _customMove;
	}

	@Override
	public boolean isDocked()
	{
		return _dock != null && !isMoving;
	}

	@Override
	public boolean isClanAirShip()
	{
		return true;
	}

	@Override
	public List<L2GameServerPacket> deletePacketList()
	{
		List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>(2);
		list.add(new DeleteObject(_controlKey));
		list.add(new DeleteObject(this));
		return list;
	}

	private class FuelAndDeleteTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			boolean empty = true;
			for (Player player : _players)
			{
				if (player.isOnline())
				{
					empty = false;
				}
			}

			if (empty)
			{
				deleteMe();
			}
			else
			{
				setCurrentFuel(getCurrentFuel() - 10);
			}
		}
	}
}
