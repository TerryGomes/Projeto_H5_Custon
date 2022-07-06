package l2mv.gameserver.fandc.tournament.model;

import l2mv.gameserver.fandc.tournament.model.enums.TournamentPhase;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;

public class Tournament extends AbstractTournament
{
	public Tournament(int size)
	{
		super(size);
	}

	@Override
	public void onDisconnect(Player player)
	{
		super.onDisconnect(player);

		if (_phase == TournamentPhase.ACTIVE)
		{
			TeamType winner = player.getTeam() == TeamType.RED ? TeamType.BLUE : TeamType.RED;

			for (Player p : getAllPlayer())
			{
				p.sendPacket(new ExShowScreenMessage(player.getName() + " left the tournament. " + winner.name() + " team won!", 5000, ScreenMessageAlign.TOP_CENTER, false));
			}

			setWinnerTeam(winner);
		}
	}

	@Override
	public void onDie(Player player)
	{
		if (player.getTeam() == TeamType.RED)
		{
			_redLives--;
		}
		else if (player.getTeam() == TeamType.BLUE)
		{
			_blueLives--;
		}

		if (_redLives <= 0)
		{
			TeamType winner = TeamType.BLUE;

			for (Player p : getAllPlayer())
			{
				p.sendPacket(new ExShowScreenMessage("Blue team won the tournament!", 5000, ScreenMessageAlign.TOP_CENTER, false));
			}

			setWinnerTeam(winner);
		}
		else if (_blueLives <= 0)
		{
			TeamType winner = TeamType.RED;

			for (Player p : getAllPlayer())
			{
				p.sendPacket(new ExShowScreenMessage("Red team won the tournament!", 5000, ScreenMessageAlign.TOP_CENTER, false));
			}

			setWinnerTeam(winner);
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onClock()
	{
		if (_phase == TournamentPhase.ACTIVE)
		{
			if (_ticks <= 0)
			{
				setWinnerTeam(null);
				_players.forEach(s -> s.sendPacket(new ExShowScreenMessage("Time is up! Tournament ended in a tie.", 5000, ScreenMessageAlign.TOP_RIGHT, false)));
				return;
			}

			_players.forEach(s -> s.sendPacket(new ExShowScreenMessage("Blue: " + _blueLives + " Red: " + _redLives + " Time: " + _ticks, 2000, ScreenMessageAlign.TOP_RIGHT, false)));
			_ticks--;
		}
	}
}
