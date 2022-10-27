package l2mv.gameserver.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.InstantZoneHolder;
import l2mv.gameserver.instancemanager.DimensionalRiftManager;
import l2mv.gameserver.instancemanager.DimensionalRiftManager.DimensionalRiftRoom;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.Spawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.InstantZone;
import l2mv.gameserver.utils.Location;

public class DimensionalRift extends Reflection
{
	protected static final long seconds_5 = 5000L;
	protected static final int MILLISECONDS_IN_MINUTE = 60000;

	protected int _roomType;
	protected List<Integer> _completedRooms = new ArrayList<Integer>();
	protected int jumps_current = 0;

	private Future<?> teleporterTask;
	private Future<?> spawnTask;
	private Future<?> killRiftTask;

	protected int _choosenRoom = -1;
	protected boolean _hasJumped = false;
	protected boolean isBossRoom = false;

	public DimensionalRift(Party party, int type, int room)
	{
		super();
		onCreate();
		startCollapseTimer(7200000); // 120 минут таймер, для защиты от утечек памяти
		setName("DimensionalRift");
		if (this instanceof DelusionChamber)
		{
			InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(type + 120); // Для равенства типа комнаты и ИД инстанса
			setInstancedZone(iz);
			setName(iz.getName());
		}
		_roomType = type;
		setParty(party);
		if (!(this instanceof DelusionChamber))
		{
			party.setDimensionalRift(this);
		}
		party.setReflection(this);
		_choosenRoom = room;
		checkBossRoom(_choosenRoom);

		Location coords = getRoomCoord(_choosenRoom);

		setReturnLoc(party.getLeader().getLoc());
		setTeleportLoc(coords);
		for (Player p : party.getMembers())
		{
			p.setVar("backCoords", getReturnLoc().toXYZString(), -1);
			DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(coords, 50, 100, getGeoIndex()), this);
			p.setReflection(this);
		}

		createSpawnTimer(_choosenRoom);
		createTeleporterTimer();
	}

	public int getType()
	{
		return _roomType;
	}

	public int getCurrentRoom()
	{
		return _choosenRoom;
	}

	protected void createTeleporterTimer()
	{
		if (teleporterTask != null)
		{
			teleporterTask.cancel(false);
			teleporterTask = null;
		}

		teleporterTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				if (jumps_current < getMaxJumps() && getPlayersInside(true) > 0)
				{
					jumps_current++;
					teleportToNextRoom();
					createTeleporterTimer();
				}
				else
				{
					createNewKillRiftTimer();
				}
			}
		}, calcTimeToNextJump()); // Teleporter task, 8-10 minutes
	}

	public void createSpawnTimer(int room)
	{
		if (spawnTask != null)
		{
			spawnTask.cancel(false);
			spawnTask = null;
		}

		final DimensionalRiftRoom riftRoom = DimensionalRiftManager.getInstance().getRoom(_roomType, room);

		spawnTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				for (SimpleSpawner s : riftRoom.getSpawns())
				{
					SimpleSpawner sp = s.clone();
					sp.setReflection(DimensionalRift.this);
					addSpawn(sp);
					if (!isBossRoom)
					{
						sp.startRespawn();
					}
					for (int i = 0; i < sp.getAmount(); i++)
					{
						sp.doSpawn(true);
					}
				}
				DimensionalRift.this.addSpawnWithoutRespawn(getManagerId(), riftRoom.getTeleportCoords(), 0);
			}
		}, Config.RIFT_SPAWN_DELAY);
	}

	public synchronized void createNewKillRiftTimer()
	{
		if (killRiftTask != null)
		{
			killRiftTask.cancel(false);
			killRiftTask = null;
		}

		killRiftTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				if (isCollapseStarted())
				{
					return;
				}
				for (Player p : getParty().getMembers())
				{
					if (p != null && p.getReflection() == DimensionalRift.this)
					{
						DimensionalRiftManager.getInstance().teleportToWaitingRoom(p);
					}
				}
				DimensionalRift.this.collapse();
			}
		}, 100L);
	}

	public void partyMemberInvited()
	{
		createNewKillRiftTimer();
	}

	public void partyMemberExited(Player player)
	{
		if (getParty().size() < Config.RIFT_MIN_PARTY_SIZE || getParty().size() == 1 || getPlayersInside(true) == 0)
		{
			createNewKillRiftTimer();
		}
	}

	public void manualTeleport(Player player, NpcInstance npc)
	{
		if (!player.isInParty() || !player.getParty().isInReflection() || !(player.getParty().getReflection() instanceof DimensionalRift))
		{
			return;
		}

		if (!player.getParty().isLeader(player))
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
			return;
		}

		if (!isBossRoom)
		{
			if (_hasJumped)
			{
				DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/AlreadyTeleported.htm", npc);
				return;
			}
			_hasJumped = true;
		}
		else
		{
			manualExitRift(player, npc);
			return;
		}

		teleportToNextRoom();
	}

	public void manualExitRift(Player player, NpcInstance npc)
	{
		if (!player.isInParty() || !player.getParty().isInDimensionalRift())
		{
			return;
		}

		if (!player.getParty().isLeader(player))
		{
			DimensionalRiftManager.getInstance().showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
			return;
		}

		createNewKillRiftTimer();
	}

	protected void teleportToNextRoom()
	{
		_completedRooms.add(_choosenRoom);

		for (Spawner s : getSpawns())
		{
			s.deleteAll();
		}

		int size = DimensionalRiftManager.getInstance().getRooms(_roomType).size();
		/*
		 * if (jumps_current < getMaxJumps())
		 * size--; // комната босса может быть только последней
		 */

		if (getType() >= 11 && jumps_current == getMaxJumps())
		{
			_choosenRoom = 9; // В DC последние 2 печати всегда кончаются рейдом
		}
		else
		{ // выбираем комнату, где еще не были
			List<Integer> notCompletedRooms = new ArrayList<Integer>();
			for (int i = 1; i <= size; i++)
			{
				if (!_completedRooms.contains(i))
				{
					notCompletedRooms.add(i);
				}
			}
			_choosenRoom = notCompletedRooms.get(Rnd.get(notCompletedRooms.size()));
		}

		checkBossRoom(_choosenRoom);
		setTeleportLoc(getRoomCoord(_choosenRoom));

		for (Player p : getParty().getMembers())
		{
			if (p.getReflection() == this)
			{
				DimensionalRiftManager.teleToLocation(p, Location.findPointToStay(getRoomCoord(_choosenRoom), 50, 100, DimensionalRift.this.getGeoIndex()), this);
			}
		}

		createSpawnTimer(_choosenRoom);
	}

	@Override
	public void collapse()
	{
		if (isCollapseStarted())
		{
			return;
		}

		Future<?> task = teleporterTask;
		if (task != null)
		{
			teleporterTask = null;
			task.cancel(false);
		}

		task = spawnTask;
		if (task != null)
		{
			spawnTask = null;
			task.cancel(false);
		}

		task = killRiftTask;
		if (task != null)
		{
			killRiftTask = null;
			task.cancel(false);
		}

		_completedRooms = null;

		Party party = getParty();
		if (party != null)
		{
			party.setDimensionalRift(null);
		}

		super.collapse();
	}

	protected long calcTimeToNextJump()
	{
		if (isBossRoom)
		{
			return 60 * MILLISECONDS_IN_MINUTE;
		}
		return Config.RIFT_AUTO_JUMPS_TIME * MILLISECONDS_IN_MINUTE + Rnd.get(Config.RIFT_AUTO_JUMPS_TIME_RAND);
	}

	public void memberDead(Player player)
	{
		if (getPlayersInside(true) == 0)
		{
			createNewKillRiftTimer();
		}
	}

	public void usedTeleport(Player player)
	{
		if (getPlayersInside(false) < Config.RIFT_MIN_PARTY_SIZE)
		{
			createNewKillRiftTimer();
		}
	}

	public void checkBossRoom(int room)
	{
		isBossRoom = DimensionalRiftManager.getInstance().getRoom(_roomType, room).isBossRoom();
	}

	public Location getRoomCoord(int room)
	{
		return DimensionalRiftManager.getInstance().getRoom(_roomType, room).getTeleportCoords();
	}

	/** По умолчанию 4 */
	public int getMaxJumps()
	{
		return Math.max(Math.min(Config.RIFT_MAX_JUMPS, 8), 1);
	}

	@Override
	public boolean canChampions()
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "DimensionalRift";
	}

	protected int getManagerId()
	{
		return 31865;
	}

	protected int getPlayersInside(boolean alive)
	{
		if (_playerCount == 0)
		{
			return 0;
		}

		int sum = 0;

		for (Player p : getPlayers())
		{
			if (!alive || !p.isDead())
			{
				sum++;
			}
		}

		return sum;
	}

	@Override
	public void removeObject(GameObject o)
	{
		if (o.isPlayer())
		{
			if (_playerCount <= 1)
			{
				createNewKillRiftTimer();
			}
		}
		super.removeObject(o);
	}
}