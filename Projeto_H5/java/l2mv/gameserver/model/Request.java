package l2mv.gameserver.model;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.collections.MultiValueSet;
import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class Request extends MultiValueSet<String>
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(Request.class);

	public static enum L2RequestType
	{
		CUSTOM, PARTY, PARTY_ROOM, CLAN, ALLY, TRADE, TRADE_REQUEST, FRIEND, CHANNEL, DUEL, POST, COUPLE_ACTION, AUCTION_ITEM_ADD
	}

	private final static AtomicInteger _nextId = new AtomicInteger();

	private final int _id;
	private L2RequestType _type;
	private HardReference<Player> _requestor;
	private HardReference<Player> _reciever;
	private boolean _isRequestorConfirmed;
	private boolean _isRecieverConfirmed;
	private boolean _isCancelled;
	private boolean _isDone;

	private long _timeout;
	private Future<?> _timeoutTask;

	/**
	 * Создает запрос
	 */
	public Request(L2RequestType type, Player requestor, Player reciever)
	{
		_id = _nextId.incrementAndGet();
		_requestor = requestor.getRef();
		_reciever = reciever.getRef();
		_type = type;
		requestor.setRequest(this);
		reciever.setRequest(this);
	}

	public Request(L2RequestType type, Player requestor)
	{
		_id = _nextId.incrementAndGet();
		_requestor = requestor.getRef();
		_type = type;
		requestor.setRequest(this);
	}

	public Request setTimeout(long timeout)
	{
		_timeout = timeout > 0 ? System.currentTimeMillis() + timeout : 0;
		_timeoutTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				timeout();
			}
		}, timeout);
		return this;
	}

	public int getId()
	{
		return _id;
	}

	/**
	 * Отменяет запрос и очищает соответствующее поле у участников.
	 */
	public void cancel()
	{
		_isCancelled = true;
		if (_timeoutTask != null)
		{
			_timeoutTask.cancel(false);
		}
		_timeoutTask = null;
		Player player = getRequestor();
		if (player != null && player.getRequest() == this)
		{
			player.setRequest(null);
		}
		player = getReciever();
		if (player != null && player.getRequest() == this)
		{
			player.setRequest(null);
		}
	}

	/**
	 * Заканчивает запрос и очищает соответствующее поле у участников.
	 */
	public void done()
	{
		_isDone = true;
		if (_timeoutTask != null)
		{
			_timeoutTask.cancel(false);
		}
		_timeoutTask = null;
		Player player = getRequestor();
		if (player != null && player.getRequest() == this)
		{
			player.setRequest(null);
		}
		player = getReciever();
		if (player != null && player.getRequest() == this)
		{
			player.setRequest(null);
		}
	}

	/**
	 * Действие при таймауте.
	 */
	public void timeout()
	{
		Player player = getReciever();
		if (player != null)
		{
			if (player.getRequest() == this)
			{
				player.sendPacket(SystemMsg.TIME_EXPIRED);
			}
		}
		cancel();
	}

	public Player getOtherPlayer(Player player)
	{
		if (player == getRequestor())
		{
			return getReciever();
		}
		if (player == getReciever())
		{
			return getRequestor();
		}
		return null;
	}

	public Player getRequestor()
	{
		return _requestor.get();
	}

	public Player getReciever()
	{
		return _reciever.get();
	}

	/**
	 * Проверяет не просрочен ли запрос.
	 */
	public boolean isInProgress()
	{
		if (_isCancelled || _isDone)
		{
			return false;
		}
		if ((_timeout == 0) || (_timeout > System.currentTimeMillis()))
		{
			return true;
		}
		return false;
	}

	/**
	 * Проверяет тип запроса.
	 */
	public boolean isTypeOf(L2RequestType type)
	{
		return _type == type;
	}

	/**
	 * Помечает участника как согласившегося.
	 */
	public void confirm(Player player)
	{
		if (player == getRequestor())
		{
			_isRequestorConfirmed = true;
		}
		else if (player == getReciever())
		{
			_isRecieverConfirmed = true;
		}
	}

	/**
	 * Проверяет согласился ли игрок с запросом.
	 */
	public boolean isConfirmed(Player player)
	{
		if (player == getRequestor())
		{
			return _isRequestorConfirmed;
		}
		else if (player == getReciever())
		{
			return _isRecieverConfirmed;
		}
		return false; // WTF???
	}
}