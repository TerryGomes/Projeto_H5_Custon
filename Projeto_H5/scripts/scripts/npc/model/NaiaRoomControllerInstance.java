package npc.model;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.geometry.Rectangle;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.instancemanager.naia.NaiaTowerManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.Territory;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */
public class NaiaRoomControllerInstance extends NpcInstance
{
	private static final Territory _room1territory = new Territory().add(new Rectangle(-46652, 245576, -45735, 246648).setZmin(-9175).setZmax(-9075));
	private static final Territory _room3territory = new Territory().add(new Rectangle(-52088, 245667, -51159, 246609).setZmin(-10037).setZmax(-9837));
	private static final Territory _room5territory = new Territory().add(new Rectangle(-46652, 245596, -45737, 246626).setZmin(-10032).setZmax(-9832));
	private static final Territory _room6territory = new Territory().add(new Rectangle(-49220, 247903, -48647, 248543).setZmin(-10027).setZmax(-9827));
	private static final Territory _room7territory = new Territory().add(new Rectangle(-52068, 245575, -51195, 246617).setZmin(-10896).setZmax(-10696));
	private static final Territory _room8territory = new Territory().add(new Rectangle(-49284, 243788, -48592, 244408).setZmin(-10892).setZmax(-10692));
	private static final Territory _room9territory = new Territory().add(new Rectangle(-46679, 245661, -45771, 246614).setZmin(-11756).setZmax(-11556));
	private static final Territory _room10territory = new Territory().add(new Rectangle(-49252, 247894, -48587, 248519).setZmin(-11757).setZmax(-11757));
	private static final Territory _room11territory = new Territory().add(new Rectangle(-52080, 245665, -51174, 246660).setZmin(-12619).setZmax(-12419));
	private static final Territory _room12territory = new Territory().add(new Rectangle(-48732, 243186, -47752, 244097).setZmin(-13423).setZmax(-13223));

	private static List<NpcInstance> _roomMobList;

	private static final Location[] _room2locs =
	{
		new Location(-48146, 249597, -9124, -16280),
		new Location(-48144, 248711, -9124, 16368),
		new Location(-48704, 249597, -9104, -16380),
		new Location(-49219, 249596, -9104, -16400),
		new Location(-49715, 249601, -9104, -16360),
		new Location(-49714, 248696, -9104, 15932),
		new Location(-49225, 248710, -9104, 16512),
		new Location(-48705, 248708, -9104, 16576),
	};

	private static final Location[] _room4locs =
	{
		new Location(-49754, 243866, -9968, -16328),
		new Location(-49754, 242940, -9968, 16336),
		new Location(-48733, 243858, -9968, -16208),
		new Location(-48745, 242936, -9968, 16320),
		new Location(-49264, 242946, -9968, 16312),
		new Location(-49268, 243869, -9968, -16448),
		new Location(-48186, 242934, -9968, 16576),
		new Location(-48185, 243855, -9968, -16448),
	};

	public NaiaRoomControllerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		Location kickLoc = new Location(17656, 244328, 11595);

		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.startsWith("challengeroom"))
		{
			if (!NaiaTowerManager.isLegalGroup(player))
			{
				if (player.isInParty())
				{
					for (Player member : player.getParty().getMembers())
					{
						member.teleToLocation(kickLoc);
					}
					return;
				}
				else
				{
					player.teleToLocation(kickLoc);
					return;
				}
			}

			int npcId = getNpcId();
			if (NaiaTowerManager.isRoomDone(npcId, player))
			{
				player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Ingenious Contraption:<br><br>The room is already challenged."));
				return;
			}

			switch (npcId)
			{
			// Room 1
			case 18494:
			{
				ReflectionUtils.getDoor(18250001).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22393, 3, _room1territory, npcId);
				spawnToRoom(22394, 3, _room1territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				// no update for 1st room
				break;
			}
			// Room 2
			case 18495:
			{
				ReflectionUtils.getDoor(18250002).closeMe();
				ReflectionUtils.getDoor(18250003).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				for (int i = 0; i < _room2locs.length; i++)
				{
					spawnExactToRoom(22439, _room2locs[i], npcId);
				}
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 3
			case 18496:
			{
				ReflectionUtils.getDoor(18250004).closeMe();
				ReflectionUtils.getDoor(18250005).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22441, 2, _room3territory, npcId);
				spawnToRoom(22442, 2, _room3territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 4
			case 18497:
			{
				ReflectionUtils.getDoor(18250006).closeMe();
				ReflectionUtils.getDoor(18250007).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				for (int i = 0; i < _room4locs.length; i++)
				{
					spawnExactToRoom(22440, _room4locs[i], npcId);
				}
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 5
			case 18498:
			{
				ReflectionUtils.getDoor(18250008).closeMe();
				ReflectionUtils.getDoor(18250009).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22411, 2, _room5territory, npcId);
				spawnToRoom(22393, 2, _room5territory, npcId);
				spawnToRoom(22394, 2, _room5territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 6
			case 18499:
			{
				ReflectionUtils.getDoor(18250010).closeMe();
				ReflectionUtils.getDoor(18250011).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22395, 2, _room6territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 7
			case 18500:
			{
				ReflectionUtils.getDoor(18250101).closeMe();
				ReflectionUtils.getDoor(18250013).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22393, 3, _room7territory, npcId);
				spawnToRoom(22394, 3, _room7territory, npcId);
				spawnToRoom(22412, 1, _room7territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 8
			case 18501:
			{
				ReflectionUtils.getDoor(18250014).closeMe();
				ReflectionUtils.getDoor(18250015).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22395, 2, _room8territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 9
			case 18502:
			{
				ReflectionUtils.getDoor(18250102).closeMe();
				ReflectionUtils.getDoor(18250017).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22441, 2, _room9territory, npcId);
				spawnToRoom(22442, 3, _room9territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 10
			case 18503:
			{
				ReflectionUtils.getDoor(18250018).closeMe();
				ReflectionUtils.getDoor(18250019).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22395, 2, _room10territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 11
			case 18504:
			{
				ReflectionUtils.getDoor(18250103).closeMe();
				ReflectionUtils.getDoor(18250021).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(22413, 6, _room11territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.updateGroupTimer(player);
				break;
			}
			// Room 12
			// Last special room
			case 18505:
			{
				ReflectionUtils.getDoor(18250022).closeMe();
				ReflectionUtils.getDoor(18250023).closeMe();
				_roomMobList = new ArrayList<NpcInstance>();
				spawnToRoom(18490, 12, _room12territory, npcId);
				NaiaTowerManager.lockRoom(npcId);
				NaiaTowerManager.addRoomDone(npcId, player);
				NaiaTowerManager.addMobsToRoom(npcId, _roomMobList);
				NaiaTowerManager.removeGroupTimer(player);
				break;
			}
			default:
				break;
			}

		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	private void spawnToRoom(int mobId, int count, Territory territory, int roomId)
	{
		for (int i = 0; i < count; i++)
		{
			try
			{
				SimpleSpawner sp = new SimpleSpawner(mobId);
				sp.setLoc(Territory.getRandomLoc(territory).setH(Rnd.get(65535)));
				sp.doSpawn(true);
				sp.stopRespawn();
				_roomMobList.add(sp.getLastSpawn());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void spawnExactToRoom(int mobId, Location loc, int roomId)
	{
		try
		{
			SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(mobId));
			sp.setLoc(loc);
			sp.doSpawn(true);
			sp.stopRespawn();
			_roomMobList.add(sp.getLastSpawn());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		return "default/18494.htm";
	}
}