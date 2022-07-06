package ai.hellbound;

import java.util.List;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.instancemanager.naia.NaiaTowerManager;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.ReflectionUtils;

public class NaiaRoomController extends DefaultAI
{
	public NaiaRoomController(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	public boolean thinkActive()
	{
		NpcInstance actor = getActor();
		int npcId = actor.getNpcId();
		if (NaiaTowerManager.isLockedRoom(npcId))
		{
			List<NpcInstance> _roomMobs = NaiaTowerManager.getRoomMobs(npcId);

			if (_roomMobs == null)
			{
				return false;
			}

			if (!_roomMobs.isEmpty())
			{
				for (NpcInstance npc : _roomMobs)
				{
					if (npc == null || !npc.isDead())
					{
						return false;
					}
				}
			}

			switch (npcId)
			{
			// Room 1
			case 18494:
			{
				ReflectionUtils.getDoor(18250002).openMe();
				ReflectionUtils.getDoor(18250003).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 2
			case 18495:
			{
				ReflectionUtils.getDoor(18250004).openMe();
				ReflectionUtils.getDoor(18250005).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 3
			case 18496:
			{
				ReflectionUtils.getDoor(18250006).openMe();
				ReflectionUtils.getDoor(18250007).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 4
			case 18497:
			{
				ReflectionUtils.getDoor(18250008).openMe();
				ReflectionUtils.getDoor(18250009).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 5
			case 18498:
			{
				ReflectionUtils.getDoor(18250010).openMe();
				ReflectionUtils.getDoor(18250011).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 6
			case 18499:
			{
				ReflectionUtils.getDoor(18250101).openMe();
				ReflectionUtils.getDoor(18250013).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 7
			case 18500:
			{
				ReflectionUtils.getDoor(18250014).openMe();
				ReflectionUtils.getDoor(18250015).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 8
			case 18501:
			{
				ReflectionUtils.getDoor(18250102).openMe();
				ReflectionUtils.getDoor(18250017).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 9
			case 18502:
			{
				ReflectionUtils.getDoor(18250018).openMe();
				ReflectionUtils.getDoor(18250019).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 10
			case 18503:
			{
				ReflectionUtils.getDoor(18250103).openMe();
				ReflectionUtils.getDoor(18250021).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 11
			case 18504:
			{
				ReflectionUtils.getDoor(18250022).openMe();
				ReflectionUtils.getDoor(18250023).openMe();
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			// Room 12
			case 18505:
			{
				ReflectionUtils.getDoor(18250024).openMe();
				ThreadPoolManager.getInstance().schedule(new LastDoorClose(), 300 * 1000L);
				NaiaTowerManager.unlockRoom(npcId);
				NaiaTowerManager.removeRoomMobs(npcId);
				break;
			}
			default:
				break;
			}
		}

		return true;
	}

	private class LastDoorClose extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			ReflectionUtils.getDoor(18250024).closeMe();
		}
	}
}