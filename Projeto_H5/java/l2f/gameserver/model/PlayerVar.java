package l2f.gameserver.model;

import java.util.concurrent.ScheduledFuture;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.skills.AbnormalEffect;

/**
 * @author Akumu
 * @date 14:53/29.04.12
 */
public class PlayerVar
{
	private Player owner;
	private String name;
	private String value;
	private long expire_time;

	@SuppressWarnings("rawtypes")
	private ScheduledFuture task;

	public PlayerVar(Player owner, String name, String value, long expire_time)
	{
		this.owner = owner;
		this.name = name;
		this.value = value;
		this.expire_time = expire_time;

		if (expire_time > 0) // if expires schedule expiration
		{
			task = ThreadPoolManager.getInstance().schedule(new PlayerVarExpireTask(this), expire_time - System.currentTimeMillis());
		}
	}

	public String getName()
	{
		return name;
	}

	public Player getOwner()
	{
		return owner;
	}

	public boolean hasExpired()
	{
		return task == null || task.isDone();
	}

	public long getTimeToExpire()
	{
		return expire_time - System.currentTimeMillis();
	}

	/**
	 *
	 * @return возвращает значение переменной
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 *
	 * @return возвращает значение в виде логической переменной
	 */
	public boolean getValueBoolean()
	{
		return value.equals("1") || value.equalsIgnoreCase("true");
	}

	public void setValue(String val)
	{
		value = val;
	}

	public void stopExpireTask()
	{
		if (task != null && !task.isDone())
		{
			task.cancel(true);
		}
	}

	private static class PlayerVarExpireTask extends RunnableImpl
	{
		private PlayerVar _pv;

		private PlayerVarExpireTask(PlayerVar pv)
		{
			_pv = pv;
		}

		@Override
		public void runImpl()
		{
			Player pc = _pv.getOwner();
			if (pc == null)
			{
				return;
			}

			pc.unsetVar(_pv.getName());

			onUnsetVar(_pv);
		}

		private static void onUnsetVar(PlayerVar var)
		{
			switch (var.getName())
			{
			case "Para":
				if (!var.getOwner().isBlocked())
				{
					return;
				}
				var.getOwner().unblock();
				var.getOwner().stopAbnormalEffect(AbnormalEffect.HOLD_1);
				if (var.getOwner().isPlayable())
				{
					var.getOwner().getPlayer().unsetVar("Para");
				}
				break;
			}
		}
	}
}
