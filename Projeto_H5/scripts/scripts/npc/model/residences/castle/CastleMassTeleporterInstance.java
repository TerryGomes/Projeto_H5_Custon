package npc.model.residences.castle;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 17:46/12.07.2011
 */
public class CastleMassTeleporterInstance extends NpcInstance
{
	private class TeleportTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Functions.npcShout(CastleMassTeleporterInstance.this, NpcString.THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE, "#" + getCastle().getNpcStringName().getId());

			for (Player p : World.getAroundPlayers(CastleMassTeleporterInstance.this, 200, 50))
			{
				p.teleToLocation(Location.findPointToStay(_teleportLoc, 10, 100, p.getGeoIndex()));
			}

			_teleportTask = null;
		}
	}

	private ScheduledFuture<?> _teleportTask = null;
	private Location _teleportLoc;

	public CastleMassTeleporterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		_teleportLoc = Location.parseLoc(template.getAIParams().getString("teleport_loc"));
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (_teleportTask != null)
		{
			showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", "%teleportIn%", getSecondsToTP());
			return;
		}

		_teleportTask = ThreadPoolManager.getInstance().schedule(new TeleportTask(), isAllTowersDead() ? 480000L : 30000L);

		showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", "%teleportIn%", getSecondsToTP());
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (_teleportTask != null)
		{
			showChatWindow(player, "residence2/castle/CastleTeleportDelayed.htm", "%teleportIn%", getSecondsToTP());
		}
		else if (isAllTowersDead())
		{
			showChatWindow(player, "residence2/castle/gludio_mass_teleporter002.htm");
		}
		else
		{
			showChatWindow(player, "residence2/castle/gludio_mass_teleporter001.htm");
		}
	}

	/**
	 * @return Number of Seconds to next teleportation into the castle
	 */
	private String getSecondsToTP()
	{
		if (_teleportTask == null)
		{
			return isAllTowersDead() ? "480" : "30";
		}
		return String.valueOf(_teleportTask.getDelay(TimeUnit.SECONDS));
	}

	private boolean isAllTowersDead()
	{
		SiegeEvent siegeEvent = getEvent(SiegeEvent.class);
		if (siegeEvent == null || !siegeEvent.isInProgress())
		{
			return false;
		}

		List<SiegeToggleNpcObject> towers = siegeEvent.getObjects(CastleSiegeEvent.CONTROL_TOWERS);
		for (SiegeToggleNpcObject t : towers)
		{
			if (t.isAlive())
			{
				return false;
			}
		}

		return true;
	}
}
