package l2f.gameserver.utils;

import org.apache.commons.lang3.StringUtils;

import gnu.trove.iterator.TIntLongIterator;
import gnu.trove.map.hash.TIntLongHashMap;
import l2f.gameserver.Config;

public class AntiFlood
{
	private final TIntLongHashMap _recentReceivers = new TIntLongHashMap();
	private long _lastSent = 0L;
	private String _lastText = StringUtils.EMPTY;

	private long _lastHeroTime;
	private long _lastTradeTime;
	private long _lastShoutTime;

	private long _lastMailTime;
	private long _lastRequestedCaptcha;

	private long _lastAcademyRegTime;
	private final TIntLongHashMap _recentInviteAcademy = new TIntLongHashMap();

	public boolean canTrade(String text)
	{
		long currentMillis = System.currentTimeMillis();

		if (currentMillis - _lastTradeTime < 5000L)
		{
			return false;
		}

		_lastTradeTime = currentMillis;
		return true;
	}

	public boolean canShout(String text)
	{
		long currentMillis = System.currentTimeMillis();

		if (currentMillis - _lastShoutTime < 5000L)
		{
			return false;
		}

		_lastShoutTime = currentMillis;
		return true;
	}

	public boolean canHero(String text)
	{
		long currentMillis = System.currentTimeMillis();

		if (currentMillis - _lastHeroTime < 10000L)
		{
			return false;
		}

		_lastHeroTime = currentMillis;
		return true;
	}

	public boolean canMail()
	{
		long currentMillis = System.currentTimeMillis();

		if (currentMillis - _lastMailTime < 10000L)
		{
			return false;
		}

		_lastMailTime = currentMillis;
		return true;
	}

	public boolean canTell(int charId, String text)
	{
		long currentMillis = System.currentTimeMillis();
		long lastSent;

		TIntLongIterator itr = _recentReceivers.iterator();

		int recent = 0;
		while (itr.hasNext())
		{
			itr.advance();
			lastSent = itr.value();
			if (currentMillis - lastSent < (text.equalsIgnoreCase(_lastText) ? 600000L : 60000L))
			{
				recent++;
			}
			else
			{
				itr.remove();
			}
		}

		lastSent = _recentReceivers.put(charId, currentMillis);

		long delay = 333L;
		if (recent > 3)
		{
			lastSent = _lastSent;
			delay = (recent - 3) * 3333L;
		}

		_lastText = text;
		_lastSent = currentMillis;

		return currentMillis - lastSent > delay;
	}

	public boolean canRequestCaptcha()
	{
		final long currentMillis = System.currentTimeMillis();
		if (currentMillis - _lastRequestedCaptcha < 5 * 1000) // every 5 sec can request new captcha
		{
			return false;
		}
		_lastRequestedCaptcha = currentMillis;
		return true;
	}

	public boolean canInviteInAcademy(int charId)
	{
		final long currentMillis = System.currentTimeMillis();
		long lastSent;
		int lastChar;
		final TIntLongIterator itr = _recentInviteAcademy.iterator();
		while (itr.hasNext())
		{
			itr.advance();
			lastChar = itr.key();
			lastSent = itr.value();
			if (lastChar == charId && currentMillis - lastSent < Config.ACADEMY_INVITE_DELAY * 60 * 1000) // 5 minutes
			{
				return false;
			}
		}
		lastSent = _recentInviteAcademy.put(charId, currentMillis);
		return true;
	}

	public boolean canRegisterForAcademy()
	{
		final long currentMillis = System.currentTimeMillis();
		if (currentMillis - _lastAcademyRegTime < 5 * 1000) // 5 sec
		{
			return false;
		}
		_lastAcademyRegTime = currentMillis;
		return true;
	}
}
