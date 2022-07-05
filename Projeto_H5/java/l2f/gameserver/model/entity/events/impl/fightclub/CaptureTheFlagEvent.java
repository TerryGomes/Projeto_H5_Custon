package l2f.gameserver.model.entity.events.impl.fightclub;

import l2f.commons.collections.MultiValueSet;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2f.gameserver.model.entity.events.fightclubmanager.FightClubTeam;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;
import l2f.gameserver.model.entity.events.objects.CTFCombatFlagObject;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.ItemFunctions;
import l2f.gameserver.utils.Location;

public class CaptureTheFlagEvent extends AbstractFightClub// TODO sprawdzic czy flaga mi zostanie po skonczeniu eventu
{
	private static final int FLAG_TO_STEAL_ID = 53004;
	private static final int FLAG_HOLDER_ID = 53005;

	private CaptureFlagTeam[] _flagTeams;
	private final int _badgesCaptureFlag;

	public CaptureTheFlagEvent(MultiValueSet<String> set)
	{
		super(set);
		_badgesCaptureFlag = set.getInteger("badgesCaptureFlag");
	}

	@Override
	public String getShortName()
	{
		return "CTF";
	}

	@Override
	public void onKilled(Creature actor, Creature victim)
	{
		try
		{
			if (actor != null && actor.isPlayable())
			{
				FightClubPlayer realActor = getFightClubPlayer(actor.getPlayer());
				if (victim.isPlayer() && realActor != null)
				{
					realActor.increaseKills(true);
					updatePlayerScore(realActor);
					sendMessageToPlayer(realActor, MessageType.GM, "You have killed " + victim.getName());
				}
				else if (victim.isPet())
				{

				}
				actor.getPlayer().sendUserInfo();
			}

			if (victim.isPlayer())
			{
				FightClubPlayer realVictim = getFightClubPlayer(victim);
				realVictim.increaseDeaths();
				if (actor != null)
				{
					sendMessageToPlayer(realVictim, MessageType.GM, "You have been killed by " + actor.getName());
				}
				victim.getPlayer().sendUserInfo();

				CaptureFlagTeam flagTeam = getTeam(realVictim.getTeam());
				// If victim was holding flag
				if (flagTeam != null && flagTeam._thisTeamHolder != null && flagTeam._thisTeamHolder.playerHolding.equals(realVictim))
				{
					spawnFlag(getTeam(flagTeam._thisTeamHolder.teamFlagOwner));

					flagTeam._thisTeamHolder = null;
				}
			}

			super.onKilled(actor, victim);
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag OnKilled!", e);
		}
	}

	@Override
	public void startEvent()
	{
		try
		{
			super.startEvent();
			_flagTeams = new CaptureFlagTeam[getTeams().size()];
			int i = 0;
			for (FightClubTeam team : getTeams())
			{
				CaptureFlagTeam flagTeam = new CaptureFlagTeam();
				flagTeam._team = team;
				flagTeam._holder = spawnNpc(FLAG_HOLDER_ID, getFlagHolderSpawnLocation(team), 0);
				spawnFlag(flagTeam);
				_flagTeams[i] = flagTeam;
				i++;
			}
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag startEvent!", e);
		}
	}

	@Override
	public void stopEvent()
	{
		try
		{
			super.stopEvent();
			for (CaptureFlagTeam iFlagTeam : _flagTeams)
			{
				if (iFlagTeam._flag != null)
				{
					iFlagTeam._flag.deleteMe();
				}
				if (iFlagTeam._holder != null)
				{
					iFlagTeam._holder.deleteMe();
				}
				if (iFlagTeam._thisTeamHolder != null && iFlagTeam._thisTeamHolder.enemyFlagHoldByPlayer != null)
				{
					iFlagTeam._thisTeamHolder.enemyFlagHoldByPlayer.despawnObject(this);
				}

				// Synerge - Remove all the flags remaining in the player inventories after the event ends
				for (FightClubPlayer player : iFlagTeam._team.getPlayers())
				{
					final long count = player.getPlayer().getInventory().getCountOf(CTFCombatFlagObject.FLAG_ITEM_ID);
					if (count > 0)
					{
						ItemFunctions.removeItem(player.getPlayer(), CTFCombatFlagObject.FLAG_ITEM_ID, count, true, "CTF");
					}
				}

			}
			_flagTeams = null;
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag stopEvent!", e);
		}
	}

	/**
	 * Team with Npc Holder, Npc Flag and TeamHolder - guy who is carrying the flag
	 */
	private class CaptureFlagTeam
	{
		private FightClubTeam _team;
		private NpcInstance _holder;
		private NpcInstance _flag;
		private CaptureFlagHolder _thisTeamHolder;
	}

	/**
	 * One guy team from @flagTeam - carrying the @enemyFlag
	 */
	private class CaptureFlagHolder
	{
		private FightClubPlayer playerHolding;
		private CTFCombatFlagObject enemyFlagHoldByPlayer;
		private FightClubTeam teamFlagOwner;
	}

	public boolean tryToTakeFlag(Player player, NpcInstance flag)
	{
		try
		{
			FightClubPlayer fPlayer = getFightClubPlayer(player);
			if ((fPlayer == null) || (getState() != EventState.STARTED))
			{
				return false;
			}

			CaptureFlagTeam flagTeam = null;
			for (CaptureFlagTeam iFlagTeam : _flagTeams)
			{
				if (iFlagTeam._flag != null && iFlagTeam._flag.equals(flag))
				{
					flagTeam = iFlagTeam;
				}
			}

			if (fPlayer.getTeam().equals(flagTeam._team))// player talked with his flag
			{
				giveFlagBack(fPlayer, flagTeam);
				return false;
			}
			else// player talked with enemy flag
			{
				return getEnemyFlag(fPlayer, flagTeam);
			}
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag tryToTakeFlag!", e);
			return false;
		}
	}

	public void talkedWithFlagHolder(Player player, NpcInstance holder)
	{
		try
		{
			FightClubPlayer fPlayer = getFightClubPlayer(player);
			if ((fPlayer == null) || (getState() != EventState.STARTED))
			{
				return;
			}

			CaptureFlagTeam flagTeam = null;
			for (CaptureFlagTeam iFlagTeam : _flagTeams)
			{
				if (iFlagTeam._holder != null && iFlagTeam._holder.equals(holder))
				{
					flagTeam = iFlagTeam;
				}
			}

			if (fPlayer.getTeam().equals(flagTeam._team))// player talked with his holder
			{
				giveFlagBack(fPlayer, flagTeam);
			}
			else// player talked with enemy holder
			{
				boolean shouldFlagDissaper = getEnemyFlag(fPlayer, flagTeam);
				if (shouldFlagDissaper)
				{
					// flagTeam._flag.deleteMe(); TODO
					// flagTeam._flag = null;
				}
			}
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag talkedWithFlagHolder!", e);
		}
	}

	/**
	 * @param fPlayer
	 * @param enemyFlagTeam
	 * @return
	 * @FPlayer from talked to Flag/Holder of @enemyFlagTeam
	 */
	private boolean getEnemyFlag(FightClubPlayer fPlayer, CaptureFlagTeam enemyFlagTeam)
	{
		try
		{
			CaptureFlagTeam goodTeam = getTeam(fPlayer.getTeam());
			Player player = fPlayer.getPlayer();

			if (enemyFlagTeam._flag != null)
			{
				// Synerge - We must check if the player is not already holding a flag, he can only hold 1
				if (player.getInventory().getCountOf(CTFCombatFlagObject.FLAG_ITEM_ID) > 0)
				{
					return false;
				}

				enemyFlagTeam._flag.deleteMe();
				enemyFlagTeam._flag = null;

				// Adding flag
				CTFCombatFlagObject flag = new CTFCombatFlagObject();
				flag.spawnObject(this);
				player.getInventory().addItem(flag.getItem(), "CTF Adding Flag");
				player.getInventory().equipItem(flag.getItem());

				CaptureFlagHolder holder = new CaptureFlagHolder();
				holder.enemyFlagHoldByPlayer = flag;
				holder.playerHolding = fPlayer;
				holder.teamFlagOwner = enemyFlagTeam._team;
				goodTeam._thisTeamHolder = holder;

				sendMessageToTeam(enemyFlagTeam._team, MessageType.CRITICAL, "Someone stolen your Flag!");
				sendMessageToTeam(goodTeam._team, MessageType.CRITICAL, fPlayer.getPlayer().getName() + " stolen flag from " + enemyFlagTeam._team.getName() + " Team!");

				return true;
			}
			return false;
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag talkedWithFlagHolder!", e);
			return false;
		}
	}

	private CaptureFlagTeam getTeam(FightClubTeam team)
	{
		if (team == null)
		{
			return null;
		}
		try
		{
			for (CaptureFlagTeam iFlagTeam : _flagTeams)
			{
				if (iFlagTeam._team != null && iFlagTeam._team.equals(team))
				{
					return iFlagTeam;
				}
			}
			return null;
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag getTeam!", e);
			return null;
		}
	}

	/**
	 * @param fPlayer - player talking to holder/flag
	 * @param flagTeam - his flagTeam
	 */
	private void giveFlagBack(FightClubPlayer fPlayer, CaptureFlagTeam flagTeam)
	{
		try
		{
			CaptureFlagHolder holdingTeam = flagTeam._thisTeamHolder;
			if (holdingTeam != null && fPlayer.equals(holdingTeam.playerHolding))
			{
				holdingTeam.enemyFlagHoldByPlayer.despawnObject(this);

				spawnFlag(getTeam(holdingTeam.teamFlagOwner));

				flagTeam._thisTeamHolder = null;
				flagTeam._team.incScore(1);
				updateScreenScores();

				for (FightClubTeam team : getTeams())
				{
					if (!team.equals(flagTeam._team))
					{
						sendMessageToTeam(holdingTeam.teamFlagOwner, MessageType.CRITICAL, flagTeam._team.getName() + " team gained score!");
					}
				}
				sendMessageToTeam(flagTeam._team, MessageType.CRITICAL, "You have gained score!");

				fPlayer.increaseEventSpecificScore("capture");
			}
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag giveFlagBack!", e);
		}
	}

	private Location getFlagHolderSpawnLocation(FightClubTeam team)
	{
		return getMap().getKeyLocations()[team.getUniqueIndex() - 1];
	}

	private void spawnFlag(CaptureFlagTeam flagTeam)
	{
		try
		{
			NpcInstance flag = spawnNpc(FLAG_TO_STEAL_ID, getFlagHolderSpawnLocation(flagTeam._team), 0);
			flag.setName(flagTeam._team.getName() + " Flag");
			flag.broadcastCharInfo();
			flagTeam._flag = flag;
		}
		catch (Exception e)
		{
			_log.error("Error on CaptureTheFlag spawnFlag!", e);
		}
	}

	@Override
	public String getVisibleTitle(Player player, String currentTitle, boolean toMe)
	{
		FightClubPlayer fPlayer = getFightClubPlayer(player);

		if (fPlayer == null)
		{
			return currentTitle;
		}

		return "Kills: " + fPlayer.getKills(true) + " Deaths: " + fPlayer.getDeaths();
	}
}
