package l2mv.gameserver.model.entity.events.impl.fightclub;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastMap;
import l2mv.commons.collections.MultiValueSet;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.base.RestartType;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2mv.gameserver.model.entity.events.fightclubmanager.FightClubTeam;
import l2mv.gameserver.model.entity.events.impl.AbstractFightClub;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.SocialAction;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

/**
 * Russian Roulette event
 * Each turn the players will choose between some russian npcs, and one of those will be the bad russian
 * Those that choose the bad russian will lose the round
 * The last survivor wins
 *
 * @author Synerge
 */
public class RussianRouletteEvent extends AbstractFightClub
{
	// Npc Simon
	private static final int RUSSIAN_NPC = 90310;
	private final Map<Integer, NpcInstance> _russianNpcs = new FastMap<Integer, NpcInstance>().shared();

	// Skill de explosion
	// private static final Skill EXPLOSION_SKILL = SkillTable.getInstance().getInfo(30054, 1);

	// Esto dice que ahora es el momento de responder, en cualquier otro momento no pasa nada o pierde, segun que este sucediendo ahora
	private boolean _isChooseMoment = false;
	private int _badRussian = -1;
	private int _isLastSingleRound = 0;

	// Concursantes validos
	private final Map<Integer, FightClubPlayer> _participatingPlayers = new FastMap<Integer, FightClubPlayer>().shared();

	public RussianRouletteEvent(MultiValueSet<String> set)
	{
		super(set);
	}

	@Override
	public String getShortName()
	{
		return "Russian";
	}

	@Override
	public void startEvent()
	{
		super.startEvent();

		_participatingPlayers.clear();

		for (FightClubPlayer fPlayer : getPlayers(FIGHTING_PLAYERS, REGISTERED_PLAYERS))
		{
			Player player = fPlayer.getPlayer();
			if (player.isDead())
			{
				player.doRevive();
			}
			if (player.isFakeDeath())
			{
				player.setFakeDeath(false);
			}

			sendMessageToPlayer(fPlayer, "Dont stray far away from this point as you will need to choose a russian!", MessageType.SCREEN_BIG, MessageType.NORMAL_MESSAGE);

			_participatingPlayers.put(player.getObjectId(), fPlayer);
		}

		_badRussian = -1;
		_isChooseMoment = false;
		_isLastSingleRound = 0;
	}

	@Override
	public void stopEvent()
	{
		super.stopEvent();

		for (FightClubPlayer fPlayer : getPlayers(FIGHTING_PLAYERS, REGISTERED_PLAYERS))
		{
			Player player = fPlayer.getPlayer();
			if (player != null)
			{
				player.standUp();
			}
		}

		for (NpcInstance russian : _russianNpcs.values())
		{
			if (russian != null)
			{
				russian.deleteMe();
			}
		}

		_russianNpcs.clear();
	}

	@Override
	public void loggedOut(Player player)
	{
		super.loggedOut(player);

		for (FightClubPlayer fPlayer : getPlayers(FIGHTING_PLAYERS, REGISTERED_PLAYERS))
		{
			if (fPlayer != null && fPlayer.getPlayer() != null && fPlayer.getPlayer().equals(player))
			{
				checkParticipatingPlayers();
			}
		}
	}

	@Override
	public boolean leaveEvent(Player player, boolean teleportTown)
	{
		super.leaveEvent(player, teleportTown);

		if (getState() != EventState.STARTED)
		{
			return true;
		}

		for (FightClubPlayer fPlayer : getPlayers(FIGHTING_PLAYERS, REGISTERED_PLAYERS))
		{
			if (fPlayer != null && fPlayer.getPlayer() != null && fPlayer.getPlayer().equals(player))
			{
				checkParticipatingPlayers();
			}
		}
		return true;
	}

	@Override
	public void startRound()
	{
		super.startRound();

		if (!checkParticipatingPlayers())
		{
			return;
		}

		// Mensaje inicial
		for (FightClubPlayer iFPlayer : _participatingPlayers.values())
		{
			sendMessageToPlayer(iFPlayer, "Pick a russian (You must talk with one npc)!", MessageType.SCREEN_BIG, MessageType.NORMAL_MESSAGE);
		}

		// La cantidad de rusos va a ir disminuyendo mientras pasan las rondas. Hasta llegar a un minimo de 2
		final int nRussians = Math.max(7 - getCurrentRound(), 2);

		// Spawneamos los rusos
		final Location loc = getMap().getKeyLocations()[0];
		int x = loc.getX();
		for (int i = 0; i < nRussians; i++)
		{
			_russianNpcs.put(i, addSpawn(RUSSIAN_NPC, x, loc.getY(), loc.getZ() + 10, (getReflection() != null ? getReflection().getId() : 0)));
			x += 50;
		}

		// Elegimos un ruso aleatorio para que sea el malo
		_badRussian = Rnd.get(0, _russianNpcs.size() - 1);

		_isChooseMoment = true;
	}

	@Override
	public void endRound()
	{
		_state = EventState.OVER;

		_isChooseMoment = false;

		try
		{
			// Hacemos un efecto de explosion sobre el ruso malo
			final NpcInstance npc = _russianNpcs.get(_badRussian);
			if (npc != null)
			{
				npc.broadcastPacket(new MagicSkillUse(npc, npc, 5814, 1, 1000, 0));
			}

			final Iterator<FightClubPlayer> it = _participatingPlayers.values().iterator();
			while (it.hasNext())
			{
				FightClubPlayer ppl = it.next();
				if (ppl == null)
				{
					it.remove();
					continue;
				}

				Player player = ppl.getPlayer();
				if (player == null)
				{
					it.remove();
					continue;
				}

				// Chequeamos aquellos que no hayan elegido ningun ruso
				if (ppl.getKills(true) <= 0)
				{
					it.remove();

					sendMessageToPlayer(player, "You have not selected any russian for that you have been eliminated", MessageType.SCREEN_BIG, MessageType.NORMAL_MESSAGE);

					// Hace una explosion que lo envia lejos al que perdio
					player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.SORROW));
					// EXPLOSION_SKILL.getEffects(_russianNpcs.get(0), player, false, false);

					// Despues de 2 segundos se sienta asi no molesta al resto
					ThreadPoolManager.getInstance().schedule(() ->
					{
						player.sitDown(null, true);
					}, 2 * 1000);
					continue;
				}

				// Ahora vemos todos los que eligieron el ruso malo
				if (ppl.getKills(true) == _badRussian)
				{
					it.remove();

					sendMessageToPlayer(player, "You have chosen the bad russian! Better luck next time", MessageType.SCREEN_BIG, MessageType.NORMAL_MESSAGE);

					// Hace una explosion que lo envia lejos al que perdio
					player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.SORROW));
					// EXPLOSION_SKILL.getEffects(_russianNpcs.get(0), player, false, false);

					// Despues de 2 segundos se sienta asi no molesta al resto
					ThreadPoolManager.getInstance().schedule(() ->
					{
						player.sitDown(null, true);
					}, 2 * 1000);
				}
				else
				{
					sendMessageToPlayer(player, "You have chosen wisely. You are on the next round", MessageType.SCREEN_BIG, MessageType.NORMAL_MESSAGE);

					// Mostramos el efecto de un fuego artificial al que contesto bien
					player.broadcastPacket(new MagicSkillUse(player, player, 2024, 1, 1, 0));

					// Reseteamos sus acciones
					ppl.setKills(0, true);

					// Le sumamos 1 respuesta correcta, solo sirve como un top
					ppl.increaseScore(1);
				}
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
		}

		// Eliminamos los npcs rusos
		for (NpcInstance russian : _russianNpcs.values())
		{
			if (russian != null)
			{
				russian.deleteMe();
			}
		}

		_russianNpcs.clear();

		// Chequeamos si debemos hacer una ultima ronda para el ultimo que quedo vivo para decidir si gana o no
		if (_participatingPlayers.size() == 1 && _isLastSingleRound == 0)
		{
			_isLastSingleRound = 1;
		}

		// Chequeamos si el evento debe seguir, o sea si se deslogueo alguien
		if (checkParticipatingPlayers())
		{
			// Programamos la proxima ronda
			startNewTimer(true, 0, "startRoundTimer", 4);
		}
	}

	@Override
	public synchronized boolean onTalkNpc(Player player, NpcInstance npc)
	{
		// Solo se usa el ruso, el resto no son de este evento
		if (player == null || npc == null || (npc.getNpcId() != RUSSIAN_NPC))
		{
			return false;
		}

		// No es el momento de hablar con el npc
		// No esta compitiendo
		if (!_isChooseMoment || !_participatingPlayers.containsKey(player.getObjectId()))
		{
			return true;
		}

		final FightClubPlayer eventPlayer = _participatingPlayers.get(player.getObjectId());

		// Si ya contesto, no hace nada tampoco
		if (eventPlayer.getKills(true) > 0)
		{
			return true;
		}

		// Chequeamos si esta hablando con un npc ruso, y anotamos su eleccion, solo 1 vez
		for (Entry<Integer, NpcInstance> russian : _russianNpcs.entrySet())
		{
			if (russian != null && russian.getValue().getObjectId() == npc.getObjectId())
			{
				// Establecemos que npc uso en el pj, usamos la variable posicion para guardarlo
				eventPlayer.setKills(russian.getKey(), true);

				sendMessageToPlayer(player, "You have chosen the russian number " + (russian.getKey() + 1) + ". Good Luck", MessageType.SCREEN_BIG, MessageType.NORMAL_MESSAGE);

				break;
			}
		}

		return true;
	}

	private boolean checkParticipatingPlayers()
	{
		if (getState() == EventState.NOT_ACTIVE)
		{
			return true;
		}

		final Iterator<FightClubPlayer> it = _participatingPlayers.values().iterator();
		while (it.hasNext())
		{
			FightClubPlayer oldPlayer = it.next();
			if (oldPlayer == null || !isPlayerActive(oldPlayer.getPlayer()) || getFightClubPlayer(oldPlayer.getPlayer()) == null)
			{
				it.remove();
			}
		}

		// Si es la ultima ronda solista, entonces no terminamos el evento ya que debe haber una ronda mas con el ultimo participante
		if (_isLastSingleRound != 1 && _participatingPlayers.size() <= 1)
		{
			stopEvent();
			return false;
		}

		// Ya la proxima ronda debe terminarse si o si luego de la ultima ronda solista
		if (_isLastSingleRound == 1)
		{
			_isLastSingleRound = 2;
		}

		return true;
	}

	@Override
	protected FightClubPlayer[] getTopKillers()
	{
		if (_participatingPlayers.size() != 1)
		{
			return null;
		}

		// El ganador solo va a ser el ultimo vivo, el resto no gana nada
		FightClubPlayer[] topKillers = new FightClubPlayer[1];
		FightClubPlayer player = _participatingPlayers.values().iterator().next();
		if (player != null && player.getPlayer() != null)
		{
			topKillers[0] = player;
			return topKillers;
		}

		return null;
	}

	private NpcInstance addSpawn(int npcId, int x, int y, int z, int instanceId)
	{
		try
		{
			final NpcTemplate FAME_PC_NPC = NpcHolder.getInstance().getTemplate(npcId);
			final NpcInstance npc = FAME_PC_NPC.getNewInstance();
			npc.setReflection(instanceId);
			npc.setSpawnedLoc(new Location(x, y, z));
			npc.spawnMe(npc.getSpawnedLoc());
			return npc;
		}
		catch (Exception e1)
		{
			_log.warn(getClass().getSimpleName() + ": Could not spawn Npc " + npcId);
		}

		return null;
	}

	@Override
	public Boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force)
	{
		if (getState() != EventState.STARTED)
		{
			return null;
		}
		if (target == null || !target.isPlayable() || attacker == null || !attacker.isPlayable())
		{
			return null;
		}

		for (FightClubPlayer fPlayer : getPlayers(FIGHTING_PLAYERS, REGISTERED_PLAYERS))
		{
			if (fPlayer != null && fPlayer.getPlayer() != null && (fPlayer.getPlayer().getObjectId() == attacker.getObjectId() || fPlayer.getPlayer().getObjectId() == target.getObjectId()))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canUseSkill(Creature actor, Creature target, Skill skill)
	{
		for (FightClubPlayer fPlayer : getPlayers(FIGHTING_PLAYERS, REGISTERED_PLAYERS))
		{
			if (fPlayer != null && fPlayer.getPlayer() != null && fPlayer.getPlayer().getObjectId() == actor.getObjectId())
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canUseFixedRessurect(Player player)
	{
		return false;
	}

	@Override
	public void checkRestartLocs(Player player, Map<RestartType, Boolean> r)
	{
		r.clear();
	}

	@Override
	protected int getRewardForWinningTeam(FightClubPlayer fPlayer, boolean atLeast1Kill)
	{
		return super.getRewardForWinningTeam(fPlayer, false);
	}

	@Override
	protected void handleAfk(FightClubPlayer fPlayer, boolean setAsAfk)
	{
	}

	@Override
	protected void unrootPlayers()
	{
	}

	@Override
	protected boolean inScreenShowBeScoreNotKills()
	{
		return false;
	}

	@Override
	protected boolean inScreenShowBeTeamNotInvidual()
	{
		return false;
	}

	@Override
	protected boolean isAfkTimerStopped(Player player)
	{
		return player.isSitting() || super.isAfkTimerStopped(player);
	}

	@Override
	protected List<List<Player>> spreadTeamInPartys(FightClubTeam team)
	{
		return Collections.emptyList();
	}

	@Override
	protected void createParty(List<Player> listOfPlayers)
	{
	}

	@Override
	public void onKilled(Creature actor, Creature victim)
	{
	}

	@Override
	public void onDamage(Creature actor, Creature victim, double damage)
	{
	}

	@Override
	protected void updateScreenScores()
	{
	}

	@Override
	protected void updateScreenScores(Player player)
	{
	}
}
