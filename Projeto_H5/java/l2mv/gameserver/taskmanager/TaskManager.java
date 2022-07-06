package l2mv.gameserver.taskmanager;

import static l2mv.gameserver.taskmanager.TaskTypes.TYPE_FIXED_SHEDULED;
import static l2mv.gameserver.taskmanager.TaskTypes.TYPE_GLOBAL_TASK;
import static l2mv.gameserver.taskmanager.TaskTypes.TYPE_NONE;
import static l2mv.gameserver.taskmanager.TaskTypes.TYPE_SHEDULED;
import static l2mv.gameserver.taskmanager.TaskTypes.TYPE_SPECIAL;
import static l2mv.gameserver.taskmanager.TaskTypes.TYPE_STARTUP;
import static l2mv.gameserver.taskmanager.TaskTypes.TYPE_TIME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.taskmanager.tasks.SoIStageUpdater;
import l2mv.gameserver.taskmanager.tasks.TaskEndHero;
import l2mv.gameserver.taskmanager.tasks.TaskNevitSystem;
import l2mv.gameserver.taskmanager.tasks.TaskRecom;

public final class TaskManager
{
	private static final Logger _log = LoggerFactory.getLogger(TaskManager.class);

	static final String[] SQL_STATEMENTS =
	{
		"SELECT id,task,type,last_activation,param1,param2,param3 FROM global_tasks",
		"UPDATE global_tasks SET last_activation=? WHERE id=?",
		"SELECT id FROM global_tasks WHERE task=?",
		"INSERT INTO global_tasks (task,type,last_activation,param1,param2,param3) VALUES(?,?,?,?,?,?)"
	};

	private final Map<Integer, Task> _tasks = new ConcurrentHashMap<>();
	protected final List<ExecutedTask> _currentTasks = new ArrayList<>();

	public class ExecutedTask extends RunnableImpl
	{
		int _id;
		long _lastActivation;
		Task _task;
		TaskTypes _type;
		String[] _params;
		ScheduledFuture<?> _scheduled;

		public ExecutedTask(Task task, TaskTypes type, ResultSet rset) throws SQLException
		{
			_task = task;
			_type = type;
			_id = rset.getInt("id");
			_lastActivation = rset.getLong("last_activation") * 1000L;
			_params = new String[]
			{
				rset.getString("param1"),
				rset.getString("param2"),
				rset.getString("param3")
			};
		}

		@Override
		public void runImpl()
		{
			_task.onTimeElapsed(this);

			_lastActivation = System.currentTimeMillis();

			try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SQL_STATEMENTS[1]))
			{
				statement.setLong(1, _lastActivation / 1000);
				statement.setInt(2, _id);
				statement.executeUpdate();
			}
			catch (SQLException e)
			{
				_log.warn("cannot updated the Global Task " + _id + ": " + e.getMessage());
			}

			if (_type == TYPE_SHEDULED || _type == TYPE_TIME)
			{
				stopTask();
			}
		}

		@Override
		public boolean equals(Object object)
		{
			return _id == ((ExecutedTask) object)._id;
		}

		public Task getTask()
		{
			return _task;
		}

		public TaskTypes getType()
		{
			return _type;
		}

		public int getId()
		{
			return _id;
		}

		public String[] getParams()
		{
			return _params;
		}

		public long getLastActivation()
		{
			return _lastActivation;
		}

		public void stopTask()
		{
			_task.onDestroy();

			if (_scheduled != null)
			{
				_scheduled.cancel(false);
			}

			_currentTasks.remove(this);
		}

	}

	public TaskManager()
	{
		initializate();
		startAllTasks();
	}

	private void initializate()
	{
		registerTask(new TaskEndHero());
		registerTask(new TaskRecom());
		registerTask(new TaskNevitSystem());
		registerTask(new SoIStageUpdater());
	}

	public void registerTask(Task task)
	{
		int key = task.getName().hashCode();
		if (!_tasks.containsKey(key))
		{
			_tasks.put(key, task);
			task.initializate();
		}
	}

	private void startAllTasks()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SQL_STATEMENTS[0]); ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				Task task = _tasks.get(rset.getString("task").trim().toLowerCase().hashCode());

				if (task == null)
				{
					continue;
				}

				TaskTypes type = TaskTypes.valueOf(rset.getString("type"));

				if (type != TYPE_NONE)
				{
					ExecutedTask current = new ExecutedTask(task, type, rset);
					if (launchTask(current))
					{
						_currentTasks.add(current);
					}
				}

			}
		}
		catch (IllegalArgumentException | SQLException e)
		{
			_log.error("Error while loading Global Task table", e);
		}
	}

	private boolean launchTask(ExecutedTask task)
	{
		final ThreadPoolManager scheduler = ThreadPoolManager.getInstance();
		final TaskTypes type = task.getType();

		if (type == TYPE_STARTUP)
		{
			task.run();
			return false;
		}
		else if (type == TYPE_SHEDULED)
		{
			long delay = Long.valueOf(task.getParams()[0]);
			task._scheduled = scheduler.schedule(task, delay);
			return true;
		}
		else if (type == TYPE_FIXED_SHEDULED)
		{
			long delay = Long.valueOf(task.getParams()[0]);
			long interval = Long.valueOf(task.getParams()[1]);

			task._scheduled = scheduler.scheduleAtFixedRate(task, delay, interval);
			return true;
		}
		else if (type == TYPE_TIME)
		{
			try
			{
				Date desired = DateFormat.getInstance().parse(task.getParams()[0]);
				long diff = desired.getTime() - System.currentTimeMillis();
				if (diff >= 0)
				{
					task._scheduled = scheduler.schedule(task, diff);
					return true;
				}
				_log.info("Task " + task.getId() + " is obsoleted.");
			}
			catch (ParseException e)
			{
				_log.error("Error while Launching Time Task from TaskManager", e);
			}
		}
		else if (type == TYPE_SPECIAL)
		{
			ScheduledFuture<?> result = task.getTask().launchSpecial(task);
			if (result != null)
			{
				task._scheduled = result;
				return true;
			}
		}
		else if (type == TYPE_GLOBAL_TASK)
		{
			long interval = Long.valueOf(task.getParams()[0]) * 86400000L;
			String[] hour = task.getParams()[1].split(":");

			if (hour.length != 3)
			{
				_log.warn("Task " + task.getId() + " has incorrect parameters");
				return false;
			}

			Calendar check = Calendar.getInstance();
			check.setTimeInMillis(task.getLastActivation() + interval);

			Calendar min = Calendar.getInstance();
			try
			{
				min.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour[0]));
				min.set(Calendar.MINUTE, Integer.valueOf(hour[1]));
				min.set(Calendar.SECOND, Integer.valueOf(hour[2]));
			}
			catch (NumberFormatException e)
			{
				_log.warn("Bad parameter on task " + task.getId() + ": " + e.getMessage());
				return false;
			}

			long delay = min.getTimeInMillis() - System.currentTimeMillis();

			if (check.after(min) || delay < 0)
			{
				delay += interval;
			}

			task._scheduled = scheduler.scheduleAtFixedRate(task, delay, interval);

			return true;
		}

		return false;
	}

	public static boolean addUniqueTask(String task, TaskTypes type, String param1, String param2, String param3)
	{
		return addUniqueTask(task, type, param1, param2, param3, 0);
	}

	public static boolean addUniqueTask(String task, TaskTypes type, String param1, String param2, String param3, long lastActivation)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			boolean exists = false;
			try (PreparedStatement statement = con.prepareStatement(SQL_STATEMENTS[2]))
			{
				statement.setString(1, task);
				try (ResultSet rset = statement.executeQuery())
				{
					exists = rset.next();
				}
			}

			if (!exists)
			{
				try (PreparedStatement statement = con.prepareStatement(SQL_STATEMENTS[3]))
				{
					statement.setString(1, task);
					statement.setString(2, type.toString());
					statement.setLong(3, lastActivation / 1000);
					statement.setString(4, param1);
					statement.setString(5, param2);
					statement.setString(6, param3);
					statement.execute();
				}
			}
			return true;
		}
		catch (SQLException e)
		{
			_log.warn("cannot add the unique task: " + e.getMessage());
		}

		return false;
	}

	public static boolean addTask(String task, TaskTypes type, String param1, String param2, String param3)
	{
		return addTask(task, type, param1, param2, param3, 0);
	}

	public static boolean addTask(String task, TaskTypes type, String param1, String param2, String param3, long lastActivation)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement(SQL_STATEMENTS[3]))
		{
			statement.setString(1, task);
			statement.setString(2, type.toString());
			statement.setLong(3, lastActivation / 1000);
			statement.setString(4, param1);
			statement.setString(5, param2);
			statement.setString(6, param3);
			statement.execute();
			return true;
		}
		catch (SQLException e)
		{
			_log.warn("cannot add the task:	" + e.getMessage());
		}

		return false;
	}

	public static TaskManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final TaskManager _instance = new TaskManager();
	}
}
