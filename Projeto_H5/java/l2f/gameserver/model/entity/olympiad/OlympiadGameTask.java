package l2f.gameserver.model.entity.olympiad;

import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.Log;

public class OlympiadGameTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadGameTask.class);

	private final OlympiadGame _game;
	private final BattleStatus _status;
	private int _count;
	private final long _time;

	private boolean _shoutGameStart = true;
	private boolean _terminated = false;

	public boolean isTerminated()
	{
		return _terminated;
	}

	/**
	 * Set this to false to disable the annoying Olympiad Manager NPC Shout to all players that the match has begun.
	 * @param value
	 */
	public void setShoutGameStart(boolean value)
	{
		_shoutGameStart = value;
	}

	public BattleStatus getStatus()
	{
		return _status;
	}

	public int getCount()
	{
		return _count;
	}

	public OlympiadGame getGame()
	{
		return _game;
	}

	public long getTime()
	{
		return _count;
	}

	public ScheduledFuture<?> shedule()
	{
		return ThreadPoolManager.getInstance().schedule(this, _time);
	}

	public OlympiadGameTask(OlympiadGame game, BattleStatus status, int count, long time)
	{
		_game = game;
		_status = status;
		_count = count;
		_time = time;
	}

	@Override
	public void runImpl()
	{
		if (_game == null || _terminated)
		{
			return;
		}

		OlympiadGameTask task = null;
		final int gameId = _game.getId();
		try
		{
			if (!Olympiad.inCompPeriod())
			{
				return;
			}

			// Прерываем игру, если один из игроков не онлайн, и игра еще не прервана
			if (!_game.checkPlayersOnline() && _status != BattleStatus.ValidateWinner && _status != BattleStatus.Ending)
			{
				Log.add("Player is offline for game " + gameId + ", status: " + _status, "olympiad");
				_game.endGame(1000, true, false);
				return;
			}

			switch (_status)
			{
			case Begining:
			{
				task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, Config.ALT_OLY_WAIT_TIME, 100);
				break;
			}
			case Begin_Countdown:
			{
				_game.broadcastPacket(new SystemMessage(SystemMsg.YOU_WILL_BE_MOVED_TO_THE_OLYMPIAD_STADIUM_IN_S1_SECONDS).addNumber(_count), true, false);
				switch (_count)
				{
				case 120:
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 60, 60000);
					break;
				case 60:
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 30, 30000);
					break;
				case 30:
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 15, 15000);
					break;
				case 15:
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 5, 10000);
					break;
				case 5:
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 4, 1000);
					break;
				case 4:
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 3, 1000);
					break;
				case 3:
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 2, 1000);
					break;
				case 2:
					task = new OlympiadGameTask(_game, BattleStatus.Begin_Countdown, 1, 1000);
					break;
				case 1:
					task = new OlympiadGameTask(_game, BattleStatus.PortPlayers, 0, 1000);
					break;
				}
				break;
			}
			case PortPlayers:
			{
				_game.portPlayersToArena();
				if (_shoutGameStart)
				{
					_game.managerShout();
				}
				task = new OlympiadGameTask(_game, BattleStatus.Started, 60, 1000);
				break;
			}
			case Started:
			{
				if (_count == 60)
				{
					_game.setState(1);
					_game.preparePlayers();
					_game.addBuffers();
					_game.restoreAll();
					if (Config.OLY_SHOW_OPPONENT_PERSONALITY && _game.getType() != CompType.TEAM)
					{
						final Player player1 = _game.getTeam1().getFirstPlayer();
						final Player player2 = _game.getTeam2().getFirstPlayer();
						_game.getTeam1().broadcast(new ExShowScreenMessage("You fight against " + player2.getName() + "(" + player2.getClassId().toPrettyString() + ")", 10000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true));
						_game.getTeam2().broadcast(new ExShowScreenMessage("You fight against " + player1.getName() + "(" + player1.getClassId().toPrettyString() + ")", 10000, ExShowScreenMessage.ScreenMessageAlign.BOTTOM_RIGHT, true));
					}
				}
				// Synerge - Heal after 10 seconds
				else if (_count == 50)
				{
					_game.restoreAll();
				}
				_game.broadcastPacket(new SystemMessage(SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS).addNumber(_count), true, true);
				_count -= 10;
				if (_count > 0)
				{
					task = new OlympiadGameTask(_game, BattleStatus.Started, _count, 10000);
					break;
				}
				_game.openDoors();
				task = new OlympiadGameTask(_game, BattleStatus.CountDown, 5, 5000);
				break;
			}

			case CountDown:
			{
				_game.broadcastPacket(new SystemMessage(SystemMsg.THE_MATCH_WILL_START_IN_S1_SECONDS).addNumber(_count), true, true);
				_count--;
				if (_count <= 0)
				{
					task = new OlympiadGameTask(_game, BattleStatus.StartComp, 36, 1000);
				}
				else
				{
					task = new OlympiadGameTask(_game, BattleStatus.CountDown, _count, 1000);
				}
				break;
			}
			case StartComp:
			{
				_game.deleteBuffers();
				if (_count == 36)
				{
					_game.setState(2);
					_game.broadcastPacket(Msg.STARTS_THE_GAME, true, true);
					_game.broadcastInfo(null, null, false);
				}
				// Wait 3 mins (Battle)
				_count--;
				if (_count == 0)
				{
					task = new OlympiadGameTask(_game, BattleStatus.ValidateWinner, 0, 10000);
				}
				else
				{
					task = new OlympiadGameTask(_game, BattleStatus.StartComp, _count, 10000);
				}
				break;
			}
			case ValidateWinner:
			{
				try
				{
					_game.validateWinner(_count > 0, false, true);
				}
				catch (Exception e)
				{
					_log.error("Error on Olympiad Validate Winner", e);
				}
				task = new OlympiadGameTask(_game, BattleStatus.Ending, 0, 20000);
				break;
			}
			case Ending:
			{
				_game.collapse();
				_terminated = true;
				if (Olympiad._manager != null)
				{
					Olympiad._manager.freeOlympiadInstance(_game.getId());
				}
				return;
			}
			}

			if (task == null)
			{
				Log.add("task == null for game " + gameId, "olympiad. Status: " + _status);
				Thread.dumpStack();
				_game.endGame(1000, true, false);
				return;
			}

			_game.sheduleTask(task);
		}
		catch (Exception e)
		{
			_log.error("Error on Olympiad Game Task", e);
			_game.endGame(1000, true, false);
		}
	}
}