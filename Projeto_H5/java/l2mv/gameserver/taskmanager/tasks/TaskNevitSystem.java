package l2mv.gameserver.taskmanager.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.taskmanager.Task;
import l2mv.gameserver.taskmanager.TaskManager;
import l2mv.gameserver.taskmanager.TaskManager.ExecutedTask;
import l2mv.gameserver.taskmanager.TaskTypes;

public class TaskNevitSystem extends Task
{
	private static final Logger _log = LoggerFactory.getLogger(TaskNevitSystem.class);
	private static final String NAME = "sp_navitsystem";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		_log.info("Navit System Global Task: launched.");
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.getNevitSystem().restartSystem();
		}
		_log.info("Navit System Task: completed.");
	}

	@Override
	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
	}
}
