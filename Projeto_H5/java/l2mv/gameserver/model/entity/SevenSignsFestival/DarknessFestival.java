package l2mv.gameserver.model.entity.SevenSignsFestival;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.entity.SevenSigns;
import l2mv.gameserver.model.instances.FestivalMonsterInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

public class DarknessFestival extends Reflection
{
	private static final Logger _log = LoggerFactory.getLogger(DarknessFestival.class);

	public static final int FESTIVAL_LENGTH = 1080000; // 18 mins
	public static final int FESTIVAL_FIRST_SPAWN = 60000; // 1 min
	public static final int FESTIVAL_SECOND_SPAWN = 540000; // 9 mins
	public static final int FESTIVAL_CHEST_SPAWN = 900000; // 15 mins

	private FestivalSpawn _witchSpawn;
	private FestivalSpawn _startLocation;

	private int currentState = 0;
	private boolean _challengeIncreased = false;
	private final int _levelRange;
	private final int _cabal;

	private Future<?> _spawnTimerTask;

	public DarknessFestival(Party party, int cabal, int level)
	{
		super();
		onCreate();
		setName("Darkness Festival");
		setParty(party);
		_levelRange = level;
		_cabal = cabal;
		startCollapseTimer(FESTIVAL_LENGTH + FESTIVAL_FIRST_SPAWN);

		if (cabal == SevenSigns.CABAL_DAWN)
		{
			_witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_WITCH_SPAWNS[_levelRange]);
			_startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_PLAYER_SPAWNS[_levelRange]);
		}
		else
		{
			_witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_WITCH_SPAWNS[_levelRange]);
			_startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_PLAYER_SPAWNS[_levelRange]);
		}

		party.setReflection(this);
		setReturnLoc(party.getLeader().getLoc());
		for (Player p : party.getMembers())
		{
			p.setVar("backCoords", p.getLoc().toXYZString(), -1);
			p.getEffectList().stopAllEffects();
			p.teleToLocation(Location.findPointToStay(_startLocation.loc, 20, 100, getGeoIndex()), this);
		}

		scheduleNext();
		NpcTemplate witchTemplate = NpcHolder.getInstance().getTemplate(_witchSpawn.npcId);
		// Spawn the festival witch for this arena
		try
		{
			SimpleSpawner npcSpawn = new SimpleSpawner(witchTemplate);
			npcSpawn.setLoc(_witchSpawn.loc);
			npcSpawn.setReflection(this);
			addSpawn(npcSpawn);
			npcSpawn.doSpawn(true);
		}
		catch (RuntimeException e)
		{
			_log.error("Error while initializing Darkness Festival", e);
		}
		sendMessageToParticipants("The festival will begin in 1 minute.");
	}

	private void scheduleNext()
	{
		switch (currentState)
		{
		case 0:
			currentState = FESTIVAL_FIRST_SPAWN;

			_spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 0);
					sendMessageToParticipants("Go!");
					scheduleNext();
				}
			}, FESTIVAL_FIRST_SPAWN);
			break;
		case FESTIVAL_FIRST_SPAWN:
			currentState = FESTIVAL_SECOND_SPAWN;

			_spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 2);
					sendMessageToParticipants("Next wave arrived!");
					scheduleNext();
				}
			}, FESTIVAL_SECOND_SPAWN - FESTIVAL_FIRST_SPAWN);
			break;
		case FESTIVAL_SECOND_SPAWN:
			currentState = FESTIVAL_CHEST_SPAWN;

			_spawnTimerTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 3);
					sendMessageToParticipants("The chests have spawned! Be quick, the festival will end soon.");
				}
			}, FESTIVAL_CHEST_SPAWN - FESTIVAL_SECOND_SPAWN);
			break;
		}
	}

	public void spawnFestivalMonsters(int respawnDelay, int spawnType)
	{
		int[][] spawns = null;
		switch (spawnType)
		{
		case 0:
		case 1:
			spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_PRIMARY_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_PRIMARY_SPAWNS[_levelRange];
			break;
		case 2:
			spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_SECONDARY_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_SECONDARY_SPAWNS[_levelRange];
			break;
		case 3:
			spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_CHEST_SPAWNS[_levelRange] : FestivalSpawn.FESTIVAL_DUSK_CHEST_SPAWNS[_levelRange];
			break;
		}

		if (spawns != null)
		{
			for (int[] element : spawns)
			{
				FestivalSpawn currSpawn = new FestivalSpawn(element);
				NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(currSpawn.npcId);

				SimpleSpawner npcSpawn;
				npcSpawn = new SimpleSpawner(npcTemplate);
				npcSpawn.setReflection(this);
				npcSpawn.setLoc(currSpawn.loc);
				npcSpawn.setHeading(Rnd.get(65536));
				npcSpawn.setAmount(1);
				npcSpawn.setRespawnDelay(respawnDelay);
				npcSpawn.startRespawn();
				FestivalMonsterInstance festivalMob = (FestivalMonsterInstance) npcSpawn.doSpawn(true);
				// Set the offering bonus to 2x or 5x the amount per kill, if this spawn is part of an increased challenge or is a festival chest.
				if (spawnType == 1)
				{
					festivalMob.setOfferingBonus(2);
				}
				else if (spawnType == 3)
				{
					festivalMob.setOfferingBonus(5);
				}
				addSpawn(npcSpawn);
			}
		}
	}

	public boolean increaseChallenge()
	{
		if (_challengeIncreased)
		{
			return false;
		}
		// Set this flag to true to make sure that this can only be done once.
		_challengeIncreased = true;
		// Spawn more festival monsters, but this time with a twist.
		spawnFestivalMonsters(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN, 1);
		return true;
	}

	@Override
	public void collapse()
	{
		if (isCollapseStarted())
		{
			return;
		}

		if (_spawnTimerTask != null)
		{
			_spawnTimerTask.cancel(false);
			_spawnTimerTask = null;
		}

		if (SevenSigns.getInstance().getCurrentPeriod() == SevenSigns.PERIOD_COMPETITION && getParty() != null)
		{
			Player player = getParty().getLeader();
			ItemInstance bloodOfferings = player.getInventory().getItemByItemId(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING);
			long offeringCount = bloodOfferings == null ? 0 : bloodOfferings.getCount();
			// Check if the player collected any blood offerings during the festival.
			if (player.getInventory().destroyItem(bloodOfferings, "DarknessFestival"))
			{
				long offeringScore = offeringCount * SevenSignsFestival.FESTIVAL_OFFERING_VALUE;
				boolean isHighestScore = SevenSignsFestival.getInstance().setFinalScore(getParty(), _cabal, _levelRange, offeringScore);
				// Send message that the contribution score has increased.
				player.sendPacket(new SystemMessage(SystemMessage.YOUR_CONTRIBUTION_SCORE_IS_INCREASED_BY_S1).addNumber(offeringScore));

				sendCustomMessageToParticipants("l2mv.gameserver.model.entity.SevenSignsFestival.Ended");
				if (isHighestScore)
				{
					sendMessageToParticipants("Your score is highest!");
				}
			}
			else
			{
				player.sendMessage(new CustomMessage("l2mv.gameserver.model.instances.L2FestivalGuideInstance.BloodOfferings", player));
			}
		}

		super.collapse();
	}

	private void sendMessageToParticipants(String s)
	{
		for (Player p : getPlayers())
		{
			p.sendMessage(s);
		}
	}

	private void sendCustomMessageToParticipants(String s)
	{
		for (Player p : getPlayers())
		{
			p.sendMessage(new CustomMessage(s, p));
		}
	}

	public void partyMemberExited()
	{
		if (getParty() == null || getParty().size() <= 1)
		{
			collapse();
		}
	}

	@Override
	public boolean canChampions()
	{
		return true;
	}

	@Override
	public boolean isAutolootForced()
	{
		return true;
	}
}