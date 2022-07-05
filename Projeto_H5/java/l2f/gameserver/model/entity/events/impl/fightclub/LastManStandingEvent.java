package l2f.gameserver.model.entity.events.impl.fightclub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2f.commons.collections.MultiValueSet;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;

public class LastManStandingEvent extends AbstractFightClub implements OnCurrentHpDamageListener
{
	private static final long MAX_DELAY_BETWEEN_DEATHS = 30000L;
	private FightClubPlayer _winner;
	private long lastKill;

	public LastManStandingEvent(MultiValueSet<String> set)
	{
		super(set);
		lastKill = 0L;
	}

	@Override
	public String getShortName()
	{
		return "LastMan";
	}

	@Override
	public void onKilled(Creature actor, Creature victim)
	{
		if (actor != null && actor.isPlayable())
		{
			FightClubPlayer fActor = getFightClubPlayer(actor.getPlayer());
			if (fActor != null && victim.isPlayer())
			{
				fActor.increaseKills(true);
				updatePlayerScore(fActor);
				sendMessageToPlayer(fActor, MessageType.GM, "You have killed " + victim.getName());
			}
			else if (victim.isPet())
			{

			}
			actor.getPlayer().sendUserInfo();
		}

		if (victim.isPlayer())
		{
			FightClubPlayer fVictim = getFightClubPlayer(victim);
			fVictim.increaseDeaths();
			if (actor != null)
			{
				sendMessageToPlayer(fVictim, MessageType.GM, "You have been killed by " + actor.getName());
			}
			victim.getPlayer().sendUserInfo();
			lastKill = System.currentTimeMillis();

			leaveEvent(fVictim.getPlayer(), true);

			checkRoundOver();
		}

		super.onKilled(actor, victim);
	}

	@Override
	public void startEvent()
	{
		super.startEvent();

		lastKill = System.currentTimeMillis();
		ThreadPoolManager.getInstance().schedule(new InactivityCheck(), 60000);
		for (FightClubPlayer fPlayer : getPlayers(new String[]
		{
			"fighting_players",
			"registered_players"
		}))
		{
			fPlayer.getPlayer().addListener(this);
		}
	}

	@Override
	public void startRound()
	{
		super.startRound();
		checkRoundOver();
	}

	@Override
	public void endRound()
	{
		super.endRound();
		for (Player player : getAllFightingPlayers())
		{
			player.removeListener(this);
		}
	}

	@Override
	public boolean leaveEvent(Player player, boolean teleportTown)
	{
		boolean result = super.leaveEvent(player, teleportTown);
		if (result)
		{
			checkRoundOver();
		}
		player.removeListener(this);
		return result;
	}

	private boolean checkRoundOver()
	{
		if (getState() != EventState.STARTED)
		{
			return true;
		}

		int alivePlayers = 0;
		FightClubPlayer aliveFPlayer = null;

		for (FightClubPlayer iFPlayer : getPlayers(FIGHTING_PLAYERS))
		{
			if (isPlayerActive(iFPlayer.getPlayer()))
			{
				alivePlayers++;
				aliveFPlayer = iFPlayer;
			}
			if (aliveFPlayer == null)
			{
				if (!iFPlayer.getPlayer().isDead())
				{
					aliveFPlayer = iFPlayer;
				}
			}
		}

		if (alivePlayers <= 1)
		{
			_winner = aliveFPlayer;
			if (_winner != null)
			{
				_winner.increaseScore(1);
				announceWinnerPlayer(false, _winner);
			}
			updateScreenScores();
			setState(EventState.OVER);

			ThreadPoolManager.getInstance().schedule(new Runnable()
			{

				@Override
				public void run()
				{
					endRound();
				}
			}, 5000L);
			if (_winner != null)
			{
				FightClubEventManager.getInstance().sendToAllMsg(this, _winner.getPlayer().getName() + " Won Last Hero Event!");
			}
			return true;
		}
		return false;
	}

	@Override
	protected boolean inScreenShowBeScoreNotKills()
	{
		return false;
	}

	private final Map<FightClubPlayer, Double> damagePerPlayer = new ConcurrentHashMap<>();

	@Override
	public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
	{
		if ((actor != null) && (attacker != null) && (attacker.isPlayable()) && (damage > 0.0D) && (!actor.equals(attacker)))
		{
			FightClubPlayer fPlayer = getFightClubPlayer(attacker.getPlayer());
			if (fPlayer != null)
			{
				if (damagePerPlayer.containsKey(fPlayer))
				{
					damagePerPlayer.put(fPlayer, Double.valueOf(damagePerPlayer.get(fPlayer).doubleValue() + damage));
				}
				else
				{
					damagePerPlayer.put(fPlayer, Double.valueOf(damage));
				}
			}
		}
	}

	private class InactivityCheck extends RunnableImpl
	{

		@Override
		public void runImpl()
		{
			if (getState() == EventState.NOT_ACTIVE)
			{
				return;
			}
			boolean finished = checkRoundOver();
			if (!finished && lastKill + MAX_DELAY_BETWEEN_DEATHS < System.currentTimeMillis())
			{
				killOnePlayer();
			}

			ThreadPoolManager.getInstance().schedule(this, 60000);
		}
	}

	private void killOnePlayer()
	{
		double playerToKillHp = Double.MAX_VALUE;
		Player playerToKill = null;
		for (FightClubPlayer fPlayer : getPlayers(FIGHTING_PLAYERS))
		{
			if (fPlayer != null && fPlayer.getPlayer() != null && !fPlayer.getPlayer().isDead())
			{
				if (fPlayer.isAfk())
				{
					playerToKillHp = -1.0;
					playerToKill = fPlayer.getPlayer();
				}
				else if (damagePerPlayer.getOrDefault(fPlayer, Double.valueOf(0.0D)) < playerToKillHp)
				{
					playerToKill = fPlayer.getPlayer();
					playerToKillHp = damagePerPlayer.getOrDefault(fPlayer, Double.valueOf(0.0D)).doubleValue();
				}
			}
		}

//		if (playerToKill != null)
//			playerToKill.doDie(null);
	}

	@Override
	protected int getRewardForWinningTeam(FightClubPlayer fPlayer, boolean atLeast1Kill)
	{
		if (fPlayer.equals(_winner))
		{
			return (int) _badgeWin;
		}
		return super.getRewardForWinningTeam(fPlayer, true);
	}

	@Override
	public String getVisibleTitle(Player player, String currentTitle, boolean toMe)
	{
		FightClubPlayer realPlayer = getFightClubPlayer(player);

		if (realPlayer == null)
		{
			return currentTitle;
		}

		return "Kills: " + realPlayer.getKills(true);
	}
}
