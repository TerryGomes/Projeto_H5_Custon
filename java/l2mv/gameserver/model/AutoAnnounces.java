package l2mv.gameserver.model;

import java.util.ArrayList;

public class AutoAnnounces
{
	private final int _id;
	private ArrayList<String> _msg;
	private int _repeat;
	private long _nextSend;

	public AutoAnnounces(int id)
	{
		_id = id;
	}

	public int getId()
	{
		return _id;
	}

	public void setAnnounce(int delay, int repeat, ArrayList<String> msg)
	{
		_nextSend = System.currentTimeMillis() + delay * 1000;
		_repeat = repeat;
		_msg = msg;
	}

	public void updateRepeat()
	{
		_nextSend = System.currentTimeMillis() + _repeat * 1000;
	}

	public boolean canAnnounce()
	{
		long currentTimeMillis = System.currentTimeMillis();
		if (currentTimeMillis > _nextSend)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public ArrayList<String> getMessage()
	{
		return _msg;
	}
}