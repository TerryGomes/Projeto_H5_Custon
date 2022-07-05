package l2f.gameserver.model.entity.events.impl.fightclub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import l2f.commons.annotations.Nullable;
import l2f.commons.collections.MultiValueSet;
import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubTeam;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubTeamType;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;
import l2f.gameserver.skills.AbnormalEffect;

public class ProtectTheKingEvent extends AbstractFightClub
{
	private final long _waitMillisAfterKingDeath;
	private final int _kingNameColor;
	private final boolean _announceKingName;
	private Player _king;

	public ProtectTheKingEvent(MultiValueSet<String> set)
	{
		super(set);

		_waitMillisAfterKingDeath = TimeUnit.SECONDS.toMillis(set.getLong("waitSecondsAfterKingDeath"));
		_kingNameColor = Integer.decode("0x" + set.getString("kingNameColor")).intValue();
		_announceKingName = Boolean.parseBoolean("announceKingName");
	}

	@Override
	public String getShortName()
	{
		return "King";
	}

	@Override
	public void onKilled(Creature actor, Creature victim)
	{
		if ((actor != null) && (actor.isPlayable()))
		{
			FightClubPlayer realActor = getFightClubPlayer(actor.getPlayer());
			if ((victim.isPlayer()) && (realActor != null))
			{
				realActor.increaseKills(true);
				updatePlayerScore(realActor);
				sendMessageToPlayer(realActor, AbstractFightClub.MessageType.GM, "You have killed " + victim.getName());
			}
			else if (!victim.isPet())
			{
			}
			actor.getPlayer().sendUserInfo();
		}
		if (victim.isPlayer())
		{
			FightClubPlayer realVictim = getFightClubPlayer(victim);
			if (realVictim != null)
			{
				realVictim.increaseDeaths();
				if (actor != null)
				{
					sendMessageToPlayer(realVictim, AbstractFightClub.MessageType.GM, "You have been killed by " + actor.getName());
				}
			}
			victim.broadcastCharInfo();
			if ((_king != null) && (_king.getObjectId() == victim.getObjectId()))
			{
				onKingDied(actor);
			}
		}
		super.onKilled(actor, victim);
	}

	@Override
	public void startRound()
	{
		super.startRound();

		selectNewKing();
	}

	@Override
	public void endRound()
	{
		FightClubTeam defenders = getTeamsByType(FightClubTeamType.DEFENDER).get(0);
		defenders.incScore(1);
		updateScreenScores();
		if (_king != null)
		{
			_king.stopAbnormalEffect(AbnormalEffect.S_ARCANE_SHIELD);
			_king.broadcastUserInfo(true);
		}
		super.endRound();
	}

	@Override
	public void loggedOut(Player player)
	{
		super.loggedOut(player);
		if ((_king != null) && (_king.getObjectId() == player.getObjectId()) && (getState() == AbstractFightClub.EventState.STARTED))
		{
			onKingLeave();
		}
	}

	@Override
	public boolean leaveEvent(Player player, boolean teleportTown)
	{
		if ((_king != null) && (_king.getObjectId() == player.getObjectId()) && (getState() == AbstractFightClub.EventState.STARTED))
		{
			onKingLeave();
		}
		return super.leaveEvent(player, teleportTown);
	}

	private void selectNewKing()
	{
		FightClubTeam team = getTeamsByType(FightClubTeamType.DEFENDER).get(0);
		_king = choosePlayerToBeKing(team);
		if (_king != null)
		{
			announceKing(_king);
			_king.startAbnormalEffect(AbnormalEffect.S_ARCANE_SHIELD);
			_king.broadcastUserInfo(true);
		}
	}

	private void onKingLeave()
	{
		Player oldKing = _king;
		_king.stopAbnormalEffect(AbnormalEffect.S_ARCANE_SHIELD);
		selectNewKing();
		oldKing.broadcastUserInfo(true);
	}

	private void onKingDied(@Nullable Creature killer)
	{
		_king.stopAbnormalEffect(AbnormalEffect.S_ARCANE_SHIELD);
		_king.broadcastUserInfo(true);
		if ((killer != null) && (killer.isPlayable()))
		{
			sendMessageToFighting(AbstractFightClub.MessageType.GM, "City King " + _king.getName() + " was killed by " + killer.getPlayer().getName() + "!", false);
		}
		else
		{
			sendMessageToFighting(AbstractFightClub.MessageType.GM, "City King " + _king.getName() + " was killed!", false);
		}
		FightClubTeam oldDefender = getTeamsByType(FightClubTeamType.DEFENDER).get(0);
		FightClubTeam newDefender = getTeamsByType(FightClubTeamType.ATTACKER).get(0);

		ThreadPoolManager.getInstance().schedule(new SwitchDefendersThread(this, oldDefender, newDefender), _waitMillisAfterKingDeath);
	}

	private static class SwitchDefendersThread extends RunnableImpl
	{
		private final ProtectTheKingEvent event;
		private final FightClubTeam oldDefender;
		private final FightClubTeam newDefender;

		SwitchDefendersThread(ProtectTheKingEvent event, FightClubTeam oldDefender, FightClubTeam newDefender)
		{
			this.event = event;
			this.oldDefender = oldDefender;
			this.newDefender = newDefender;
		}

		@Override
		public void runImpl()
		{
			if (event.getState() != AbstractFightClub.EventState.STARTED)
			{
				return;
			}
			newDefender.setTeamType(event, FightClubTeamType.DEFENDER);
			oldDefender.setTeamType(event, FightClubTeamType.ATTACKER);

			event.teleportAllToSpawns();
			event.selectNewKing();
		}
	}

	private static Player choosePlayerToBeKing(FightClubTeam team)
	{
		final List<FightClubPlayer> players = new ArrayList<>();
		for (FightClubPlayer fPlayer : team.getPlayers())
		{
			if (fPlayer != null && fPlayer.getPlayer() != null)
			{
				players.add(fPlayer);
			}
		}

		if (players.isEmpty())
		{
			return null;
		}

		return Rnd.get(players).getPlayer();
	}

	private void announceKing(Player newKing)
	{
		if (_announceKingName)
		{
			sendMessageToFighting(AbstractFightClub.MessageType.GM, newKing.getName() + " is now City King!", false);
		}
		else
		{
			for (FightClubTeam team : getTeamsByType(FightClubTeamType.DEFENDER))
			{
				sendMessageToTeam(team, AbstractFightClub.MessageType.GM, newKing.getName() + " is now City King!");
			}
			for (FightClubTeam team : getTeamsByType(FightClubTeamType.ATTACKER))
			{
				sendMessageToTeam(team, AbstractFightClub.MessageType.GM, "New City King has been selected!");
			}
		}
	}

	private void teleportAllToSpawns()
	{
		for (FightClubPlayer fPlayer : getPlayers(new String[]
		{
			FIGHTING_PLAYERS
		}))
		{
			teleportSinglePlayer(fPlayer, false, true);
		}
	}

	@Override
	protected String getScreenScores(boolean showScoreNotKills, boolean teamPointsNotInvidual)
	{
		if (_announceKingName)
		{
			StringBuilder builder = new StringBuilder();
			FightClubTeam defender = getTeamsByType(FightClubTeamType.DEFENDER).get(0);
			builder.append("Defenders: " + defender.getName() + " Team");
			if (_king != null)
			{
				builder.append("\nKing: " + _king.getName());
			}
			return builder.toString();
		}
		return super.getScreenScores(showScoreNotKills, teamPointsNotInvidual);
	}

	@Override
	protected boolean inScreenShowBeScoreNotKills()
	{
		return _announceKingName;
	}

	@Override
	protected boolean inScreenShowBeTeamNotInvidual()
	{
		return !_announceKingName;
	}

	@Override
	public String getVisibleTitle(Player player, String currentTitle, boolean toMe)
	{
		FightClubPlayer fPlayer = getFightClubPlayer(player);
		if ((_king != null) && (player.getObjectId() == _king.getObjectId()))
		{
			return "City King";
		}
		if (fPlayer == null)
		{
			return currentTitle;
		}
		return "Kills: " + fPlayer.getKills(true) + " Deaths: " + fPlayer.getDeaths();
	}

	@Override
	public int getVisibleNameColor(Player player, int currentNameColor, boolean toMe)
	{
		if ((_king != null) && (player.getObjectId() == _king.getObjectId()))
		{
			return _kingNameColor;
		}
		return super.getVisibleNameColor(player, currentNameColor, toMe);
	}
}
