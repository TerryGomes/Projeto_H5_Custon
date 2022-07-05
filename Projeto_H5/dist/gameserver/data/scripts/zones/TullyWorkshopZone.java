package zones;

import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.ReflectionUtils;

public class TullyWorkshopZone implements ScriptFile
{
	private static ZoneListener _zoneListener;
	private static final String[] zones =
	{
		"[tully1]",
		"[tully2]",
		"[tully3]",
		"[tully4]"
	};

	@Override
	public void onLoad()
	{
		_zoneListener = new ZoneListener();

		for (String s : zones)
		{
			Zone zone = ReflectionUtils.getZone(s);
			zone.addListener(_zoneListener);
		}
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		final Location TullyFloor2LocationPoint = new Location(-14180, 273060, -13600);
		final Location TullyFloor3LocationPoint = new Location(-13361, 272107, -11936);
		final Location TullyFloor4LocationPoint = new Location(-14238, 273002, -10496);
		final Location TullyFloor5LocationPoint = new Location(-10952, 272536, -9062);
		final int MASTER_ZELOS_ID = 22377;
		final int MASTER_FESTINA_ID = 22380;

		@Override
		public void onZoneEnter(Zone zone, Creature cha)
		{
			Player player = cha.getPlayer();
			if (player == null)
			{
				return;
			}
			if (zone.isActive())
			{
				if (zone.getName().equalsIgnoreCase("[tully1]"))
				{
					player.teleToLocation(TullyFloor2LocationPoint);
				}
				else if (zone.getName().equalsIgnoreCase("[tully2]"))
				{
					player.teleToLocation(TullyFloor4LocationPoint);
				}
				else if (zone.getName().equalsIgnoreCase("[tully3]"))
				{
					player.teleToLocation(TullyFloor3LocationPoint);
				}
				else if (zone.getName().equalsIgnoreCase("[tully4]"))
				{
					player.teleToLocation(TullyFloor5LocationPoint);
				}

			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)
		{
		}

		@Override
		public void onEquipChanged(Zone zone, Creature actor)
		{
		}
	}
}
