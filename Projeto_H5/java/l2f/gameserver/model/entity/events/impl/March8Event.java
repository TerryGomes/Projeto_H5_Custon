package l2f.gameserver.model.entity.events.impl;

import java.util.Calendar;

import l2f.commons.collections.MultiValueSet;
import l2f.gameserver.Announcements;
import l2f.gameserver.model.entity.events.GlobalEvent;

public class March8Event extends GlobalEvent
{
	private Calendar _calendar = Calendar.getInstance();
	private static final long LENGTH = 7 * 24 * 60 * 60 * 1000L;

	public March8Event(MultiValueSet<String> set)
	{
		super(set);
	}

	/**
	 * Вызывается при старте сервака, если ненужен евент ставим фелс
	 */
	@Override
	public void initEvent()
	{

	}

	@Override
	public void startEvent()
	{
		super.startEvent();

		Announcements.getInstance().announceToAll("Test startEvent");
	}

	@Override
	public void stopEvent()
	{
		super.stopEvent();

		Announcements.getInstance().announceToAll("Test stopEvent");
	}

	@Override
	public void reCalcNextTime(boolean onInit)
	{
		clearActions(); // удаляем все дейсвтия которые остались(вообще то ненужно, ибо они должны были все выполнится)

		// вызывается при старте, создаем дату, если она уже неправильная добаляем 1 год
		if (onInit)
		{
			_calendar.set(Calendar.MONTH, Calendar.MARCH);
			_calendar.set(Calendar.DAY_OF_MONTH, 8);
			_calendar.set(Calendar.HOUR_OF_DAY, 0);
			_calendar.set(Calendar.MINUTE, 0);
			_calendar.set(Calendar.SECOND, 0);

			if ((_calendar.getTimeInMillis() + LENGTH) < System.currentTimeMillis())
			{
				_calendar.add(Calendar.YEAR, 1);
			}
		}
		// было вызвано на рабочем серваке - добавляем год
		else
		{
			_calendar.add(Calendar.YEAR, 1);
		}

		registerActions(); // регистрируем действия
	}

	@Override
	protected long startTimeMillis()
	{
		return _calendar.getTimeInMillis();
	}
}
