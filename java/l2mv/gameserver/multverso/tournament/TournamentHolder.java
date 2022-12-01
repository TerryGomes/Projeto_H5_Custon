package l2mv.gameserver.multverso.tournament;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.multverso.tournament.model.AbstractTournament;
import l2mv.gameserver.multverso.tournament.model.Tournament;
import l2mv.gameserver.multverso.tournament.model.enums.TournamentPhase;
import l2mv.loginserver.ThreadPoolManager;

/**
 *
 * @author Kara`
 *
 */
public class TournamentHolder
{
	private static final Logger _log = LoggerFactory.getLogger(TournamentHolder.class);

	final static Map<Integer, AbstractTournament> _tournaments = new ConcurrentHashMap<>();

	public static void init()
	{
		if (!TournamentConfig.init())
		{
			_log.info("[TournamentHolder] Failed to load configuration files. Tourmaments won't load.");
			return;
		}

		for (int teamSize : TournamentConfig.TOURNAMENTS)
		{
			_tournaments.put(teamSize, new Tournament(teamSize));
		}

		_log.info("[TournamentHolder] Initialized " + _tournaments.size() + " tournament holders:");

		for (AbstractTournament tournament : getTournaments())
		{
			_log.info(tournament.getTeamSize() + " vs " + tournament.getTeamSize());
		}

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				_tournaments.values().stream().filter(s -> s.getPhase() == TournamentPhase.ACTIVE).forEach(s -> s.onClock());
			}
		}, 1000, 1000);
	}

	public static AbstractTournament getTournament(int size)
	{
		return _tournaments.get(size);
	}

	public static Collection<AbstractTournament> getTournaments()
	{
		return _tournaments.values();
	}

	public static void replaceTournament(AbstractTournament tournament)
	{
		_tournaments.replace(tournament.getTeamSize(), new Tournament(tournament.getTeamSize()));
	}
}
