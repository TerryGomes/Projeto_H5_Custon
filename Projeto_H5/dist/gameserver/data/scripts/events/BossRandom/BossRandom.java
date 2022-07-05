package events.BossRandom;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.Zone;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.NpcUtils;
import l2f.gameserver.utils.ReflectionUtils;

public class BossRandom extends Functions implements ScriptFile
{

	private static final Logger LOG = LoggerFactory.getLogger(BossRandom.class);
	private static boolean isActiveBossRandom = true;
	private static final int BossId = Config.RANDOM_BOSS_ID; // enter boss id
	private static Creature _boss;
	private static final int BossEventInterval = Config.RANDOM_BOSS_TIME; // 60*60*1000 - time
	@SuppressWarnings("unused")
	private static ArrayList<Spawner> _spawns = new ArrayList<Spawner>();
	private static Zone _zone = ReflectionUtils.getZone("[dino_peac_zone]");

	private static void spawnBoss()
	{
		_boss = NpcUtils.spawnSingle(BossId, Config.RANDOM_BOSS_X, Config.RANDOM_BOSS_Y, Config.RANDOM_BOSS_Z);
	}

	public static void OnDie(Creature self, Creature killer)
	{

		if (self.getNpcId() == getBossId())
		{
			Announcements.getInstance().announceToAll(self.getName() + " defeated, the player " + killer.getName() + " final blow!");
			Announcements.getInstance().announceToAll("Peace zone in the island canceled.");
			ThreadPoolManager.getInstance().schedule(new spawnBossShedule(), BossEventInterval);
			_zone.setActive(false);
		}
	}

	private static class spawnBossShedule implements Runnable
	{

		@Override
		public void run()
		{

			spawnBoss();
			Location nearestTown = Location.findNearest(_boss, new Location[]
			{
				_boss.getLoc()
			});
			Announcements.getInstance().announceToAll(_boss.getName() + " appeared in " + String.valueOf(nearestTown) + "!");
			Announcements.getInstance().announceToAll("Part of the land on the island was peaceful.");
			_zone.setActive(false);

		}
	}

	public static int getBossId()
	{
		return BossId;
	}

	@Override
	public void onLoad()
	{

		if (!Config.RANDOM_BOSS_ENABLE)
		{
			return;
		}

		if (NpcHolder.getInstance().getTemplate(BossId) == null)
		{
			isActiveBossRandom = false;
		}
		if (isActiveBossRandom)
		{
			spawnBoss();
			LOG.info("Loaded Event: Boss Random");
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