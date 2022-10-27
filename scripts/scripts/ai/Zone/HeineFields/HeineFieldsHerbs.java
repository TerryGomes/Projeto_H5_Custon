package ai.Zone.HeineFields;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

public class HeineFieldsHerbs implements ScriptFile
{

	private static ScheduledFuture<?> DropAncientHerbTask;
	private static ScheduledFuture<?> DeleteAncientHerbTask;
	private boolean DEBUG = false;
	private static final int[] HERBS =
	{
		14824,
		14825,
		14826,
		14827
	};
	private List<ItemInstance> _herbs = new CopyOnWriteArrayList<ItemInstance>();

	public class DropAncientHerbTask extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			int total_count_herb = 0;
			try
			{
				for (Location loc : Config.HEIN_FIELDS_LOCATIONS)
				{
					if (Rnd.chance(_herbs.isEmpty() ? 100 : Config.ANCIENT_HERB_SPAWN_CHANCE))
					{ // Herb first spawn 100% to avoid NPE in Tax divides.
						for (int x = 0; x < Rnd.get(1, Config.ANCIENT_HERB_SPAWN_COUNT + 1); x++)
						{
							ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), HERBS[Rnd.get(0, HERBS.length - 1)]);
							item.setCount(1);
							Location pos = Location.findPointToStay(loc, Config.ANCIENT_HERB_SPAWN_RADIUS, 120);
							item.dropMe(null, pos);
							_herbs.add(item);
							total_count_herb++;
						}
					}
				}
			}
			catch (Exception e)
			{
				_log.error("Exception in drop herb task: " + e.getMessage(), e);
			}
			if (DEBUG)
			{
				_log.info(getClass().getSimpleName() + ": Spawned " + total_count_herb + " Ancient Herbs in " + Config.HEIN_FIELDS_LOCATIONS.size() + " spawn points.");
			}
		}
	}

	private class DeleteAncientHerbTask extends RunnableImpl
	{

		@Override
		public void runImpl()
		{
			int total_count_herb = 0;
			try
			{
				if (!_herbs.isEmpty())
				{
					for (ItemInstance item : _herbs)
					{
						if (item != null)
						{
							synchronized (_herbs)
							{
								item.deleteMe();
							}
							total_count_herb++;
						}
					}
					synchronized (_herbs)
					{
						_herbs.clear();
					}

				}
			}
			catch (Exception e)
			{
				_log.error("Exception in delete herb task: " + e.getMessage(), e);
			}
			if (DEBUG)
			{
				_log.info(getClass().getSimpleName() + ": Delete " + total_count_herb + " Ancient Herbs in " + Config.HEIN_FIELDS_LOCATIONS.size() + " spawn points.");
			}
		}
	}

	@Override
	public void onLoad()
	{
		DropAncientHerbTask = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new DropAncientHerbTask(), 10000L, Config.ANCIENT_HERB_RESPAWN_TIME);
		DeleteAncientHerbTask = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new DeleteAncientHerbTask(), 70000L, Config.ANCIENT_HERB_DESPAWN_TIME);

	}

	@Override
	public void onReload()
	{
		DropAncientHerbTask.cancel(false);
		DeleteAncientHerbTask.cancel(false);
	}

	@Override
	public void onShutdown()
	{
	}
}