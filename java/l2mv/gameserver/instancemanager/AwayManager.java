package l2mv.gameserver.instancemanager;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.SetupGauge;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;

public final class AwayManager
{
	protected static final Logger _log = LoggerFactory.getLogger(AwayManager.class);
	private static AwayManager _instance;
	private Map<Player, RestoreData> _awayPlayers;

	public static final AwayManager getInstance()
	{
		if (_instance == null)
		{
			_log.info("Away Manager: Initializing...");
			_instance = new AwayManager();
		}
		return _instance;
	}

	private AwayManager()
	{
		_awayPlayers = Collections.synchronizedMap(new WeakHashMap<Player, RestoreData>());
	}

	public void setAway(Player activeChar, String text)
	{
		activeChar.setAwayingMode(true);
		activeChar.broadcastPacket(new L2GameServerPacket[]
		{
			new SocialAction(activeChar.getObjectId(), 9)
		});
		activeChar.sendMessage(new CustomMessage("l2mv.gameserver.instancemanager.AwayManager.setAway", activeChar, new Object[]
		{
			Integer.valueOf(Config.AWAY_TIMER)
		}));
		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		SetupGauge sg = new SetupGauge(activeChar, 0, Config.AWAY_TIMER * 1000);
		activeChar.sendPacket(sg);
		activeChar.startImmobilized();
		ThreadPoolManager.getInstance().schedule(new setPlayerAwayTask(activeChar, text), Config.AWAY_TIMER * 1000);
	}

	public void setBack(Player activeChar)
	{
		activeChar.sendMessage(new CustomMessage("l2mv.gameserver.instancemanager.AwayManager.setBack", activeChar, new Object[]
		{
			Integer.valueOf(Config.BACK_TIMER)
		}));
		SetupGauge sg = new SetupGauge(activeChar, 0, Config.BACK_TIMER * 1000);
		activeChar.sendPacket(sg);
		ThreadPoolManager.getInstance().schedule(new setPlayerBackTask(activeChar), Config.BACK_TIMER * 1000);
	}

	public void extraBack(Player activeChar)
	{
		if (activeChar == null)
		{
			return;
		}
		RestoreData rd = _awayPlayers.get(activeChar);
		if (rd == null)
		{
			return;
		}
		rd.restore(activeChar);
		_awayPlayers.remove(activeChar);
	}

	private class setPlayerBackTask implements Runnable
	{
		private final Player _activeChar;

		setPlayerBackTask(Player activeChar)
		{
			_activeChar = activeChar;
		}

		@Override
		public void run()
		{
			if (_activeChar == null)
			{
				return;
			}

			AwayManager.RestoreData rd = _awayPlayers.get(_activeChar);

			if (rd == null)
			{
				return;
			}
			_activeChar.stopParalyzed();

			if (rd.isSitForced())
			{
				_activeChar.standUp();
			}
			rd.restore(_activeChar);
			_awayPlayers.remove(_activeChar);
			_activeChar.broadcastUserInfo(false);
			_activeChar.setAwayingMode(false);
			_activeChar.sendMessage(new CustomMessage("l2mv.gameserver.instancemanager.AwayManager.setPlayerBackTask", _activeChar, new Object[0]));
		}
	}

	private class setPlayerAwayTask implements Runnable
	{
		private final Player _activeChar;
		private final String _awayText;

		setPlayerAwayTask(Player activeChar, String awayText)
		{
			_activeChar = activeChar;
			_awayText = awayText;
		}

		@Override
		public void run()
		{
			if ((_activeChar == null) || _activeChar.isAttackingNow() || _activeChar.isCastingNow())
			{
				return;
			}

			if (_activeChar.isSitting())
			{
				_activeChar.sendMessage(new CustomMessage("l2mv.gameserver.instancemanager.AwayManager.setPlayerAwayTask.Sitting", _activeChar, new Object[0]));
				return;
			}

			_awayPlayers.put(_activeChar, new AwayManager.RestoreData(_activeChar));

			_activeChar.abortAttack(true, false);
			_activeChar.abortCast(true, false);
			_activeChar.setTarget(null);
			_activeChar.stopImmobilized();
			_activeChar.sitDown(null);

			if (_awayText.length() <= 1)
			{
				_activeChar.sendMessage(new CustomMessage("l2mv.gameserver.instancemanager.AwayManager.setPlayerAwayTask.NoText", _activeChar, new Object[0]));
			}
			else
			{
				_activeChar.sendMessage(new CustomMessage("l2mv.gameserver.instancemanager.AwayManager.setPlayerAwayTask", _activeChar, new Object[]
				{
					_awayText
				}));
			}
			_activeChar.setTitleColor(Config.AWAY_TITLE_COLOR);

			if (_awayText.length() <= 1)
			{
				_activeChar.setTitle("*Away*");
			}
			else
			{
				_activeChar.setTitle(new StringBuilder().append("Away*").append(_awayText).append("*").toString());
			}
			_activeChar.broadcastUserInfo(false);
			_activeChar.startParalyzed();
		}
	}

	private final class RestoreData
	{
		private final String _originalTitle;
		private final int _originalTitleColor;
		private final boolean _sitForced;

		public RestoreData(Player activeChar)
		{
			_originalTitle = activeChar.getTitle();
			_originalTitleColor = activeChar.getTitleColor();
			_sitForced = (!activeChar.isSitting());
		}

		public boolean isSitForced()
		{
			return _sitForced;
		}

		public void restore(Player activeChar)
		{
			activeChar.setTitleColor(_originalTitleColor);
			activeChar.setTitle(_originalTitle);
		}
	}
}