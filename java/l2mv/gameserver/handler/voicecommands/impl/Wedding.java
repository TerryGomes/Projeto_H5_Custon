package l2mv.gameserver.handler.voicecommands.impl;

import static l2mv.gameserver.model.Zone.ZoneType.no_restart;
import static l2mv.gameserver.model.Zone.ZoneType.no_summon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2mv.gameserver.instancemanager.CoupleManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.entity.Couple;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.SetupGauge;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.skills.AbnormalEffect;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;

public class Wedding implements IVoicedCommandHandler
{
	private static class CoupleAnswerListener implements OnAnswerListener
	{
		private final HardReference<Player> _playerRef1;
		private final HardReference<Player> _playerRef2;

		public CoupleAnswerListener(Player player1, Player player2)
		{
			_playerRef1 = player1.getRef();
			_playerRef2 = player2.getRef();
		}

		@Override
		public void sayYes()
		{
			Player player1, player2;
			if ((player1 = _playerRef1.get()) == null || (player2 = _playerRef2.get()) == null)
			{
				return;
			}

			CoupleManager.getInstance().createCouple(player1, player2);
			player1.sendMessage(new CustomMessage("l2mv.gameserver.model.L2Player.EngageAnswerYes", player2));
		}

		@Override
		public void sayNo()
		{
			Player player1, player2;
			if ((player1 = _playerRef1.get()) == null || (player2 = _playerRef2.get()) == null)
			{
				return;
			}

			player1.sendMessage(new CustomMessage("l2mv.gameserver.model.L2Player.EngageAnswerNo", player2));
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(Wedding.class);

	private static final String[] _commandList = new String[]
	{
		"engage",
		"divorce",
		"gotolove"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (!Config.ALLOW_WEDDING)
		{
			return false;
		}

		if (command.startsWith("engage"))
		{
			return engage(activeChar);
		}
		else if (command.startsWith("divorce"))
		{
			return divorce(activeChar);
		}
		else if (command.startsWith("gotolove"))
		{
			return goToLove(activeChar);
		}
		return false;
	}

	public boolean divorce(Player activeChar)
	{
		if (activeChar.getPartnerId() == 0)
		{
			return false;
		}

		int _partnerId = activeChar.getPartnerId();
		long AdenaAmount = 0;

		if (activeChar.isMaried())
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Divorced", activeChar));
			AdenaAmount = Math.abs(activeChar.getAdena() / 100 * Config.WEDDING_DIVORCE_COSTS - 10);
			activeChar.reduceAdena(AdenaAmount, true, "Divorce");
		}
		else
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Disengaged", activeChar));
		}

		activeChar.setMaried(false);
		activeChar.setPartnerId(0);
		Couple couple = CoupleManager.getInstance().getCouple(activeChar.getCoupleId());
		couple.divorce();
		couple = null;

		Player partner = GameObjectsStorage.getPlayer(_partnerId);

		if (partner != null)
		{
			partner.setPartnerId(0);
			if (partner.isMaried())
			{
				partner.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerDivorce", partner));
			}
			else
			{
				partner.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerDisengage", partner));
			}
			partner.setMaried(false);

			// give adena
			if (AdenaAmount > 0)
			{
				partner.addAdena(AdenaAmount, "Divorce");
			}
		}
		return true;
	}

	public boolean engage(Player activeChar)
	{
		// check target
		if (activeChar.getTarget() == null)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.NoneTargeted", activeChar));
			return false;
		}
		// check if target is a L2Player
		if (!activeChar.getTarget().isPlayer())
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.OnlyAnotherPlayer", activeChar));
			return false;
		}
		// check if player is already engaged
		if (activeChar.getPartnerId() != 0)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.AlreadyEngaged", activeChar));
			if (Config.WEDDING_PUNISH_INFIDELITY)
			{
				activeChar.startAbnormalEffect(AbnormalEffect.BIG_HEAD);
				// Head
				// lets recycle the sevensigns debuffs
				int skillId;

				int skillLevel = 1;

				if (activeChar.getLevel() > 40)
				{
					skillLevel = 2;
				}

				if (activeChar.isMageClass())
				{
					skillId = 4361;
				}
				else
				{
					skillId = 4362;
				}

				Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);

				if (activeChar.getEffectList().getEffectsBySkill(skill) == null)
				{
					skill.getEffects(activeChar, activeChar, false, false);
					activeChar.sendPacket(new SystemMessage2(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(skillId, skillLevel));
				}
			}
			return false;
		}

		final Player ptarget = (Player) activeChar.getTarget();

		// check if player target himself
		if (ptarget.getObjectId() == activeChar.getObjectId())
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.EngagingYourself", activeChar));
			return false;
		}

		if (ptarget.isMaried())
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyMarried", activeChar));
			return false;
		}

		if (ptarget.getPartnerId() != 0)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyEngaged", activeChar));
			return false;
		}

		Pair<Integer, OnAnswerListener> entry = ptarget.getAskListener(false);
		if (entry != null && entry.getValue() instanceof CoupleAnswerListener)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyAsked", activeChar));
			return false;
		}

		if (ptarget.getPartnerId() != 0)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PlayerAlreadyEngaged", activeChar));
			return false;
		}

		if (ptarget.getSex() == activeChar.getSex() && !Config.WEDDING_SAMESEX)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.SameSex", activeChar));
			return false;
		}

		// TODO [G1ta0] Реализовать нормальный список друзей
		boolean FoundOnFriendList = false;
		int objectId;
		ResultSet rset = null;
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT friend_id FROM character_friends WHERE char_id=?"))
		{
			statement.setInt(1, ptarget.getObjectId());
			rset = statement.executeQuery();

			while (rset.next())
			{
				objectId = rset.getInt("friend_id");
				if (objectId == activeChar.getObjectId())
				{
					FoundOnFriendList = true;
					break;
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while getting checking Friend", e);
		}

		if (!FoundOnFriendList)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.NotInFriendlist", activeChar));
			return false;
		}

		ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString("Player " + activeChar.getName() + " asking you to engage. Do you want to start new relationship?");
		ptarget.ask(packet, new CoupleAnswerListener(activeChar, ptarget));
		return true;
	}

	public boolean goToLove(Player activeChar)
	{
		if (!activeChar.isMaried())
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.YoureNotMarried", activeChar));
			return false;
		}

		if (activeChar.getPartnerId() == 0)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerNotInDB", activeChar));
			return false;
		}

		Player partner = GameObjectsStorage.getPlayer(activeChar.getPartnerId());
		if (partner == null)
		{
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.PartnerOffline", activeChar));
			return false;
		}

		if (partner.isInOlympiadMode() || partner.isFestivalParticipant() || activeChar.isMovementDisabled() || activeChar.isMuted(null) || activeChar.isInOlympiadMode() || activeChar.isInDuel() || activeChar.isFestivalParticipant() || activeChar.getPlayer().isTerritoryFlagEquipped() || partner.isInZone(no_summon))
		{
			activeChar.sendMessage(new CustomMessage("common.TryLater", activeChar));
			return false;
		}

		if (activeChar.isInParty() && activeChar.getParty().isInDimensionalRift() || partner.isInParty() && partner.getParty().isInDimensionalRift())
		{
			activeChar.sendMessage(new CustomMessage("common.TryLater", activeChar));
			return false;
		}

		if (activeChar.getTeleMode() != 0 || activeChar.getReflection() != ReflectionManager.DEFAULT)
		{
			activeChar.sendMessage(new CustomMessage("common.TryLater", activeChar));
			return false;
		}

		// "Нельзя вызывать персонажей в/из зоны свободного PvP"
		// "в зоны осад"
		// "на Олимпийский стадион"
		// "в зоны определенных рейд-боссов и эпик-боссов"
		// в режиме обсервера или к обсерверу
		if (partner.isInZoneBattle() || partner.isInZone(Zone.ZoneType.SIEGE) || partner.isInZone(no_restart) || partner.isInOlympiadMode() || activeChar.isInZoneBattle() || activeChar.isInZone(Zone.ZoneType.SIEGE) || activeChar.isInZone(no_restart) || activeChar.isInOlympiadMode() || partner.getReflection() != ReflectionManager.DEFAULT || partner.isInZone(no_summon) || activeChar.isInObserverMode() || partner.isInObserverMode())
		{
			activeChar.sendPacket(SystemMsg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING);
			return false;
		}

		if (!activeChar.reduceAdena(Config.WEDDING_TELEPORT_PRICE, true, "goToLove"))
		{
			activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			return false;
		}

		int teleportTimer = Config.WEDDING_TELEPORT_INTERVAL;

		activeChar.abortAttack(true, true);
		activeChar.abortCast(true, true);
		activeChar.sendActionFailed();
		activeChar.stopMove();
		activeChar.startParalyzed();

		activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Wedding.Teleport", activeChar).addNumber(teleportTimer / 60));
		activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);

		// SoE Animation section
		activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1050, 1, teleportTimer, 0));
		activeChar.sendPacket(new SetupGauge(activeChar, SetupGauge.BLUE, teleportTimer));
		// End SoE Animation section

		// continue execution later
		ThreadPoolManager.getInstance().schedule(new EscapeFinalizer(activeChar, partner.getLoc()), teleportTimer * 1000L);
		return true;
	}

	static class EscapeFinalizer extends RunnableImpl
	{
		private final Player _activeChar;
		private final Location _loc;

		EscapeFinalizer(Player activeChar, Location loc)
		{
			_activeChar = activeChar;
			_loc = loc;
		}

		@Override
		public void runImpl()
		{
			if (_activeChar == null)
			{
				return;
			}

			_activeChar.stopParalyzed();
			if (_activeChar.isDead())
			{
				return;
			}

			_activeChar.teleToLocation(_loc);
		}
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	public void onLoad()
	{

		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	public void onReload()
	{
	}

	public void onShutdown()
	{
	}
}