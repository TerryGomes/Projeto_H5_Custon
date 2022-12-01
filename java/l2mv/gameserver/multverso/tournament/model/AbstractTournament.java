package l2mv.gameserver.multverso.tournament.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.multverso.tournament.TournamentConfig;
import l2mv.gameserver.multverso.tournament.TournamentHolder;
import l2mv.gameserver.multverso.tournament.model.enums.TournamentPhase;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.utils.ItemFunctions;

public abstract class AbstractTournament
{
	protected List<Player> _players = new ArrayList<>();
	protected int _size;
	protected int _ticks;
	protected TournamentPhase _phase = TournamentPhase.QUENE;

	protected int _blueLives;
	protected int _redLives;

	protected Future<?> _task;

	public AbstractTournament(int size)
	{
		_size = size;
		_ticks = TournamentConfig.TOURNAMENT_DURATION;
		_blueLives = _size;
		_redLives = _size;
	}

	public TournamentPhase getPhase()
	{
		return _phase;
	}

	public int getTeamSize()
	{
		return _size;
	}

	public synchronized void handleRegUnReg(Player player)
	{
		if (_players.contains(player))
		{
			if (_phase != TournamentPhase.QUENE)
			{
				player.sendPacket(new ExShowScreenMessage("You may no unregister now.", 5000, ScreenMessageAlign.TOP_CENTER, false));
				return;
			}

			_players.remove(player);
			player.setTournament(null);
			player.sendPacket(new ExShowScreenMessage("You successfuly unregistered.", 5000, ScreenMessageAlign.TOP_CENTER, false));
			return;
		}

		if (_phase != TournamentPhase.QUENE)
		{
			player.sendPacket(new ExShowScreenMessage("Tournament has started already.", 5000, ScreenMessageAlign.TOP_CENTER, false));
			return;
		}

		if (player.isRegisteredInTournament())
		{
			player.sendPacket(new ExShowScreenMessage("You're already in different tournament.", 5000, ScreenMessageAlign.TOP_CENTER, false));
			return;
		}

		if (player.getLevel() < TournamentConfig.MIN_LEVEL)
		{
			player.sendPacket(new ExShowScreenMessage("Min level to register is " + TournamentConfig.MIN_LEVEL, 5000, ScreenMessageAlign.TOP_CENTER, false));
			return;
		}

		if (player.getLevel() > TournamentConfig.MAX_LEVEL)
		{
			player.sendPacket(new ExShowScreenMessage("Max level to register is " + TournamentConfig.MAX_LEVEL, 5000, ScreenMessageAlign.TOP_CENTER, false));
			return;
		}

		for (ItemHolder holder : TournamentConfig.REQUIRES)
		{
			ItemInstance item = player.getInventory().getItemByItemId(holder.getItemId());

			if (item == null || item.getCount() < holder.getCount())
			{
				player.sendMessage("You don't have " + holder.getCount() + " " + l2mv.gameserver.data.xml.holder.ItemHolder.getInstance().getItemName(holder.getItemId()));
				return;
			}
		}

		for (ItemHolder holder : TournamentConfig.REQUIRES)
		{
			player.getInventory().destroyItemByItemId(holder.getItemId(), holder.getCount(), "");
		}

		_players.add(player);
		player.setTournament(this);
		player.sendPacket(new ExShowScreenMessage("You have been registered in " + _size + " vs " + _size + " tournament.", 5000, ScreenMessageAlign.TOP_CENTER, false));

		if (isValidSize())
		{
			checkToStart();
		}
	}

	protected List<Player> getPlayers(TeamType type)
	{
		return _players.stream().filter(s -> s.getTeam() == type).collect(Collectors.toList());
	}

	protected List<Player> getAllPlayer()
	{
		return _players;
	}

	protected boolean isValidSize()
	{
		return _players.size() == _size * 2;
	}

	private synchronized void checkToStart()
	{
		_phase = TournamentPhase.PREPARE;

		TournamentHolder.replaceTournament(this);

		getAllPlayer().forEach(s -> s.sendPacket(new ExShowScreenMessage("You will be teleported in " + TournamentConfig.TELEPORT_SECONDS + " second(s).", 10000, ScreenMessageAlign.TOP_CENTER, false)));

		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				if (isValidSize())
				{
					onStart();
				}
				else
				{
					getAllPlayer().forEach(s ->
					{
						s.setTournament(null);
						s.sendPacket(new ExShowScreenMessage("Tournament has been aborted due to participation.", 5000, ScreenMessageAlign.TOP_CENTER, false));
					});
					_players.clear();
				}
			}
		}, TournamentConfig.TELEPORT_SECONDS * 1000);
	}

	protected void setWinnerTeam(TeamType winner)
	{
		for (Player p : getAllPlayer())
		{
			if (winner != null)
			{
				if (p.getTeam() == winner)
				{
					for (RewardHolder rh : TournamentConfig.REWARDS)
					{
						ItemFunctions.addItem(p, rh.getItemId(), rh.getCount(), true, "");
					}
					p.sendItemList(true);
					p.sendMessage("You will teleport back in 10 seconds.");
				}
			}

			p.setTournament(null);

			if (p.isDead())
			{
				p.doRevive();
			}
		}

		_phase = TournamentPhase.OFFLINE;

		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				for (Player player : getAllPlayer())
				{
					player.setReflection(0);
					player.setTeam(TeamType.NONE);
					player.broadcastUserInfo(true);
					player.teleToClosestTown();
				}

				_players.clear();
			}
		}, 10000);
	}

	protected void onStart()
	{
		Collections.shuffle(_players);

		List<Player> temp = new ArrayList<>(_players);

		int i = 0;
		while (temp.size() != 0)
		{
			i++;
			Player player = temp.get(Rnd.nextInt(temp.size()));
			player.setTeam(i == 1 ? TeamType.BLUE : TeamType.RED);
			player.broadcastUserInfo(true);
			temp.remove(player);
			if (i == 2)
			{
				i = 0;
			}
		}

		int ReflectionId = IdFactory.getInstance().getNextId();

		_players.forEach(s ->
		{
			if (s.isDead())
			{
				s.doRevive();
			}

			s.setCurrentCp(s.getMaxCp());
			s.setCurrentHpMp(s.getMaxHp(), s.getMaxMp());

			if (s.getPet() != null)
			{
				s.getPet().unSummon();
			}

			s.sitDown(null, true);

			s.setReflection(ReflectionId);
			s.sendMessage("Match starts in few seconds.");
			s.teleToLocation(s.getTeam() == TeamType.RED ? TournamentConfig.TEAM_RED_LOCATION : TournamentConfig.TEAM_BLUE_LOCATION);
		});

		ThreadPoolManager.getInstance().schedule(new Runnable()
		{

			@Override
			public void run()
			{
				_phase = TournamentPhase.ACTIVE;

				_players.forEach(s ->
				{
					s.sendMessage("Tournament started, go fight!");
					s.standUp();
				});
			}
		}, 10000);
	}

	public void onDisconnect(Player player)
	{
		_players.remove(player);
	}

	public abstract void onDie(Player player);

	public abstract void onClock();
}
