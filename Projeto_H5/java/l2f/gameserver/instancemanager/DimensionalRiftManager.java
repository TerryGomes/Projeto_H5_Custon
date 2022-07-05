package l2f.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import l2f.commons.geometry.Rectangle;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.Territory;
import l2f.gameserver.model.entity.DimensionalRift;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.TeleportToLocation;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;

public class DimensionalRiftManager
{
	private static final Logger LOG = LoggerFactory.getLogger(DimensionalRiftManager.class);
	private static DimensionalRiftManager _instance;
	private Map<Integer, Map<Integer, DimensionalRiftRoom>> _rooms = new ConcurrentHashMap<Integer, Map<Integer, DimensionalRiftRoom>>();
	private final static int DIMENSIONAL_FRAGMENT_ITEM_ID = 7079;

	public static DimensionalRiftManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new DimensionalRiftManager();
		}

		return _instance;
	}

	public DimensionalRiftManager()
	{
		load();
	}

	public DimensionalRiftRoom getRoom(int type, int room)
	{
		return _rooms.get(type).get(room);
	}

	public Map<Integer, DimensionalRiftRoom> getRooms(int type)
	{
		return _rooms.get(type);
	}

	public void load()
	{
		int countGood = 0, countBad = 0;
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(Config.DATAPACK_ROOT, "data/dimensional_rift.xml");
			if (!file.exists())
			{
				throw new IOException();
			}

			Document doc = factory.newDocumentBuilder().parse(file);
			NamedNodeMap attrs;
			int type;
			int roomId;
			int mobId, delay, count;
			SimpleSpawner spawnDat;
			NpcTemplate template;
			Location tele = new Location();
			int xMin = 0, xMax = 0, yMin = 0, yMax = 0, zMin = 0, zMax = 0;
			boolean isBossRoom;

			for (Node rift = doc.getFirstChild(); rift != null; rift = rift.getNextSibling())
			{
				if ("rift".equalsIgnoreCase(rift.getNodeName()))
				{
					for (Node area = rift.getFirstChild(); area != null; area = area.getNextSibling())
					{
						if ("area".equalsIgnoreCase(area.getNodeName()))
						{
							attrs = area.getAttributes();
							type = Integer.parseInt(attrs.getNamedItem("type").getNodeValue());

							for (Node room = area.getFirstChild(); room != null; room = room.getNextSibling())
							{
								if ("room".equalsIgnoreCase(room.getNodeName()))
								{
									attrs = room.getAttributes();
									roomId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
									Node boss = attrs.getNamedItem("isBossRoom");
									isBossRoom = boss != null ? Boolean.parseBoolean(boss.getNodeValue()) : false;
									Territory territory = null;
									for (Node coord = room.getFirstChild(); coord != null; coord = coord.getNextSibling())
									{
										if ("teleport".equalsIgnoreCase(coord.getNodeName()))
										{
											attrs = coord.getAttributes();
											tele = Location.parseLoc(attrs.getNamedItem("loc").getNodeValue());
										}
										else if ("zone".equalsIgnoreCase(coord.getNodeName()))
										{
											attrs = coord.getAttributes();
											xMin = Integer.parseInt(attrs.getNamedItem("xMin").getNodeValue());
											xMax = Integer.parseInt(attrs.getNamedItem("xMax").getNodeValue());
											yMin = Integer.parseInt(attrs.getNamedItem("yMin").getNodeValue());
											yMax = Integer.parseInt(attrs.getNamedItem("yMax").getNodeValue());
											zMin = Integer.parseInt(attrs.getNamedItem("zMin").getNodeValue());
											zMax = Integer.parseInt(attrs.getNamedItem("zMax").getNodeValue());

											territory = new Territory().add(new Rectangle(xMin, yMin, xMax, yMax).setZmin(zMin).setZmax(zMax));
										}
									}

									if (territory == null)
									{
										LOG.error("DimensionalRiftManager: invalid spawn data for room id " + roomId + "!");
									}

									if (!_rooms.containsKey(type))
									{
										_rooms.put(type, new ConcurrentHashMap<Integer, DimensionalRiftRoom>());
									}

									_rooms.get(type).put(roomId, new DimensionalRiftRoom(territory, tele, isBossRoom));

									for (Node spawn = room.getFirstChild(); spawn != null; spawn = spawn.getNextSibling())
									{
										if ("spawn".equalsIgnoreCase(spawn.getNodeName()))
										{
											attrs = spawn.getAttributes();
											mobId = Integer.parseInt(attrs.getNamedItem("mobId").getNodeValue());
											delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
											count = Integer.parseInt(attrs.getNamedItem("count").getNodeValue());

											template = NpcHolder.getInstance().getTemplate(mobId);
											if (template == null)
											{
												LOG.warn("Template " + mobId + " not found!");
											}
											if (!_rooms.containsKey(type))
											{
												LOG.warn("Type " + type + " not found!");
											}
											else if (!_rooms.get(type).containsKey(roomId))
											{
												LOG.warn("Room " + roomId + " in Type " + type + " not found!");
											}

											if (template != null && _rooms.containsKey(type) && _rooms.get(type).containsKey(roomId))
											{
												spawnDat = new SimpleSpawner(template);
												spawnDat.setTerritory(territory);
												spawnDat.setHeading(-1);
												spawnDat.setRespawnDelay(delay);
												spawnDat.setAmount(count);
												_rooms.get(type).get(roomId).getSpawns().add(spawnDat);
												countGood++;
											}
											else
											{
												countBad++;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		catch (NumberFormatException e)
		{
			LOG.error("DimensionalRiftManager: File not Found!", e);
		}
		catch (DOMException | IllegalArgumentException | ParserConfigurationException | SAXException e)
		{
			LOG.error("DimensionalRiftManager: Error on loading DimensionalRift spawns!", e);
		}
		catch (IOException e)
		{
			LOG.error("DimensionalRiftManager: IOException on loading DimensionalRift spawns!", e);
		}
		int typeSize = _rooms.keySet().size();
		int roomSize = 0;

		for (int b : _rooms.keySet())
		{
			roomSize += _rooms.get(b).keySet().size();
		}

		LOG.info("DimensionalRiftManager: Loaded " + typeSize + " room types with " + roomSize + " rooms.");
		LOG.info("DimensionalRiftManager: Loaded " + countGood + " DimensionalRift spawns, " + countBad + " errors.");
	}

	public void reload()
	{
		for (int b : _rooms.keySet())
		{
			_rooms.get(b).clear();
		}

		_rooms.clear();
		load();
	}

	public boolean checkIfInRiftZone(Location loc, boolean ignorePeaceZone)
	{
		if (ignorePeaceZone)
		{
			return _rooms.get(0).get(1).checkIfInZone(loc);
		}
		return _rooms.get(0).get(1).checkIfInZone(loc) && !_rooms.get(0).get(0).checkIfInZone(loc);
	}

	public boolean checkIfInPeaceZone(Location loc)
	{
		return _rooms.get(0).get(0).checkIfInZone(loc);
	}

	public void teleportToWaitingRoom(Player player)
	{
		teleToLocation(player, Location.findPointToStay(getRoom(0, 0).getTeleportCoords(), 0, 250, ReflectionManager.DEFAULT.getGeoIndex()), null);
	}

	public void start(Player player, int type, NpcInstance npc)
	{
		if (!player.isInParty())
		{
			showHtmlFile(player, "rift/NoParty.htm", npc);
			return;
		}

		if (!player.isGM())
		{
			if (!player.getParty().isLeader(player))
			{
				showHtmlFile(player, "rift/NotPartyLeader.htm", npc);
				return;
			}

			if (player.getParty().isInDimensionalRift())
			{
				showHtmlFile(player, "rift/Cheater.htm", npc);

				if (!player.isGM())
				{
					LOG.warn("Player " + player.getName() + "(" + player.getObjectId() + ") was cheating in dimension rift area!");
				}

				return;
			}

			if (player.getParty().size() < Config.RIFT_MIN_PARTY_SIZE)
			{
				showHtmlFile(player, "rift/SmallParty.htm", npc);
				return;
			}

			for (Player p : player.getParty().getMembers())
			{
				if (!checkIfInPeaceZone(p.getLoc()))
				{
					showHtmlFile(player, "rift/NotInWaitingRoom.htm", npc);
					return;
				}
			}

			ItemInstance i;
			for (Player p : player.getParty().getMembers())
			{
				i = p.getInventory().getItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID);
				if (i == null || i.getCount() < getNeededItems(type))
				{
					showHtmlFile(player, "rift/NoFragments.htm", npc);
					return;
				}
			}

			for (Player p : player.getParty().getMembers())
			{
				if (!p.getInventory().destroyItemByItemId(DIMENSIONAL_FRAGMENT_ITEM_ID, getNeededItems(type), "DimensionalRift"))
				{
					showHtmlFile(player, "rift/NoFragments.htm", npc);
					return;
				}
			}
		}

		new DimensionalRift(player.getParty(), type, Rnd.get(1, _rooms.get(type).size() - 1));
	}

	public class DimensionalRiftRoom
	{
		private final Territory _territory;
		private final Location _teleportCoords;
		private final boolean _isBossRoom;
		private final List<SimpleSpawner> _roomSpawns;

		public DimensionalRiftRoom(Territory territory, Location tele, boolean isBossRoom)
		{
			_territory = territory;
			_teleportCoords = tele;
			_isBossRoom = isBossRoom;
			_roomSpawns = new ArrayList<SimpleSpawner>();
		}

		public Location getTeleportCoords()
		{
			return _teleportCoords;
		}

		public boolean checkIfInZone(Location loc)
		{
			return checkIfInZone(loc.x, loc.y, loc.z);
		}

		public boolean checkIfInZone(int x, int y, int z)
		{
			return _territory.isInside(x, y, z);
		}

		public boolean isBossRoom()
		{
			return _isBossRoom;
		}

		public List<SimpleSpawner> getSpawns()
		{
			return _roomSpawns;
		}
	}

	private long getNeededItems(int type)
	{
		switch (type)
		{
		case 1:
			return Config.RIFT_ENTER_COST_RECRUIT;
		case 2:
			return Config.RIFT_ENTER_COST_SOLDIER;
		case 3:
			return Config.RIFT_ENTER_COST_OFFICER;
		case 4:
			return Config.RIFT_ENTER_COST_CAPTAIN;
		case 5:
			return Config.RIFT_ENTER_COST_COMMANDER;
		case 6:
			return Config.RIFT_ENTER_COST_HERO;
		default:
			return Long.MAX_VALUE;
		}
	}

	public void showHtmlFile(Player player, String file, NpcInstance npc)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(player, npc);
		html.setFile(file);
		html.replace("%t_name%", npc.getName());
		player.sendPacket(html);
	}

	public static void teleToLocation(Player player, Location loc, Reflection ref)
	{
		if (player.isTeleporting() || player.isDeleted())
		{
			return;
		}
		player.setIsTeleporting(true);

		player.setTarget(null);
		player.stopMove();

		if (player.isInBoat())
		{
			player.setBoat(null);
		}

		player.breakFakeDeath();

		player.decayMe();

		player.setLoc(loc);

		if (ref == null)
		{
			player.setReflection(ReflectionManager.DEFAULT);
		}

		// Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
		player.setLastClientPosition(null);
		player.setLastServerPosition(null);
		player.sendPacket(new TeleportToLocation(player, loc));
	}
}