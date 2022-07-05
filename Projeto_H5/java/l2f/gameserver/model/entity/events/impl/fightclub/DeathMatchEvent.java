package l2f.gameserver.model.entity.events.impl.fightclub;

import l2f.commons.collections.MultiValueSet;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubGameRoom;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;

public class DeathMatchEvent extends AbstractFightClub
{
	public DeathMatchEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public String getShortName()
	{
		return "DM";
	}

	@Override
	public void onKilled(Creature actor, Creature victim)
	{
		if ((actor != null) && (actor.isPlayable()))
		{
			FightClubPlayer fActor = getFightClubPlayer(actor.getPlayer());
			if ((fActor != null) && (victim.isPlayer()))
			{
				fActor.increaseScore(1);
				fActor.increaseKills(true);
				updatePlayerScore(fActor);
				sendMessageToPlayer(fActor, AbstractFightClub.MessageType.GM, "You have killed " + victim.getName());
			}
			else if (!victim.isPet())
			{
			}
			actor.getPlayer().sendUserInfo();
		}
		if (victim.isPlayer())
		{
			FightClubPlayer fVictim = getFightClubPlayer(victim);
			if (fVictim != null)
			{
				fVictim.increaseDeaths();
				if (actor != null)
				{
					sendMessageToPlayer(fVictim, AbstractFightClub.MessageType.GM, "You have been killed by " + actor.getName());
				}
				victim.getPlayer().sendUserInfo();
			}
		}
		super.onKilled(actor, victim);
	}

	protected int getRewardForEventWinner(FightClubPlayer fPlayer, boolean atLeast1Kill)
	{
		if ((_state != AbstractFightClub.EventState.OVER) && (_state != AbstractFightClub.EventState.NOT_ACTIVE))
		{
			return 0;
		}
		if ((atLeast1Kill) && (fPlayer.getKills(true) <= 0) && (FightClubGameRoom.getPlayerClassGroup(fPlayer.getPlayer()) != FightClubEventManager.CLASSES.HEALERS) && (fPlayer.getScore() <= 0))
		{
			return 0;
		}
		if (getWinner().equals(fPlayer))
		{
			return (int) _badgeWin;
		}
		return 0;
	}

	private FightClubPlayer getWinner()
	{
		FightClubPlayer bestPlayer = null;
		int bestScore = Integer.MIN_VALUE;
		for (FightClubPlayer iFPlayer : getPlayers(new String[]
		{
			"fighting_players"
		}))
		{
			if ((iFPlayer.getPlayer() != null) && (iFPlayer.getPlayer().isOnline()))
			{
				if (iFPlayer.getScore() > bestScore)
				{
					bestScore = iFPlayer.getScore();
					bestPlayer = iFPlayer;
				}
			}
		}
		return bestPlayer;
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
