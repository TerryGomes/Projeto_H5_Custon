package services;

import java.util.List;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.network.serverpackets.ExStartScenePlayer;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.ReflectionUtils;

/**
 * Раз в 3 часа на всей территории базы альянса на Грации всем внутри зоны показывается мувик
 *
 * @author pchayka
 */

public class LindviorMovie implements ScriptFile
{
	private static long movieDelay = 3 * 60 * 60 * 1000L; // показывать мувик раз в n часов

	@Override
	public void onLoad()
	{
		Zone zone = ReflectionUtils.getZone("[keucereus_alliance_base_town_peace]");
		zone.setActive(true);

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ShowLindviorMovie(zone), movieDelay, movieDelay);
	}

	public class ShowLindviorMovie extends RunnableImpl
	{
		Zone _zone;

		public ShowLindviorMovie(Zone zone)
		{
			_zone = zone;
		}

		@Override
		public void runImpl()
		{
			List<Player> insideZoners = _zone.getInsidePlayers();

			if (insideZoners != null && !insideZoners.isEmpty())
			{
				for (Player player : insideZoners)
				{
					if (!player.isInBoat() && !player.isInFlyingTransform())
					{
						player.showQuestMovie(ExStartScenePlayer.SCENE_LINDVIOR);
					}
				}
			}
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
}
