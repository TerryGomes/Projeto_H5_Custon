package l2mv.gameserver.handler.voicecommands.impl.BotReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.dao.CaptchaPunishmentDAO;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.tables.GmListTable;
import l2mv.gameserver.utils.AutoBan;
import l2mv.gameserver.utils.TimeUtils;

/**
 * Class that is handling Bot Reports of the player Checking if Bot can be reported, handling Captcha Window Handling Captcha Answer and Success/Fail of it
 */
public class CaptchaHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CaptchaHandler.class);

	/** Time between current time and last attack, to count it as current Attacker */
	private static final long TIME_BETWEEN_LAST_ATTACK = 5000L;

	/**
	 * Checking if Actor can Report Player.
	 * <p/>
	 * Sending Message to the Actor if Target cannot be Reported
	 * <p/>
	 * Return false on:
	 * <ul>
	 * <li>Server not allows Captcha</li>
	 * <li>Actor and Target is same Object</li>
	 * <li>IP or HWID of the Actor and Target is the same</li>
	 * <li>Level is below {@code #Config.CAPTCHA_MIN_LEVEL}</li>
	 * <li>Target is not in combat, is in peace zone or isn't online</li>
	 * <li>Target is in PvP Mode, have karma or is fighting in Olympiad or Fight Club</li>
	 * <li>Target is in Battle or Epic Zone</li>
	 * <li>Target passed Captcha Test lately(not checking if actor is GM)</li>
	 * <li>Actor reported someone lately(not checking if actor is GM)</li>
	 * <li>Target of the Target is not the Monster(not checking if actor is GM)</li>
	 * <li>Target of the Target is Epic Boss or Siege Guard(not checking if actor is GM)</li>
	 * </ul>
	 * Otherwise returns True
	 * @param actor Player that wants to report target
	 * @param target Player that actor wants to report
	 * @return Can Target be reported by the Actor?
	 */
	private static boolean canReport(Player actor, Player target)
	{
		if (!Config.CAPTCHA_ALLOW)
		{
			actor.sendMessage("This option is currently under construction!");
			return false;
		}
		if (actor.equals(target))
		{
			actor.sendMessage("You cannot report yourself...");
			return false;
		}
		if (((actor.getIP().equals(target.getIP())) || (actor.getHWID().equals(target.getHWID()))) && (!actor.isGM()))
		{
			actor.sendMessage("You cannot report character, that might be yours!");
			return false;
		}
		if (actor.getLevel() < Config.CAPTCHA_MIN_LEVEL)
		{
			actor.sendMessage("Your level is too low to report other Players!");
			return false;
		}
		if (!target.isInCombat() || target.isInZonePeace() || !target.isOnline())
		{
			actor.sendMessage("Cannot report Players that aren't fighting!");
			return false;
		}
		if (target.getPvpFlag() > 0 || target.getKarma() > 0 || target.isInOlympiadMode() || target.isInFightClub())
		{
			actor.sendMessage("Cannot report Players that are PvPing!");
			return false;
		}
		if (target.isInZoneBattle() || target.isInZone(Zone.ZoneType.epic))
		{
			actor.sendMessage("You cannot report players in this zone!");
			return false;
		}
		if (actor.isGM())// Not checking delay for GMs
		{
			return true;
		}
		if (target.containsQuickVar("LastCaptchaTest") && target.getQuickVarL("LastCaptchaTest") + Config.CAPTCHA_TIME_BETWEEN_TESTED_SECONDS * TimeUtils.SECOND_IN_MILLIS > System.currentTimeMillis())
		{
			actor.sendMessage("This player answered Captcha test lately!");
			return false;
		}
		if (!CaptchaTimer.getInstance().canReportBotAgain(actor))
		{
			actor.sendMessage("You cannot report players so often!");
			return false;
		}
		Creature lastAttacker = target.getLastAttacker();
		if (lastAttacker == null || !lastAttacker.isMonster() || target.getLastAttackDate() + TIME_BETWEEN_LAST_ATTACK < System.currentTimeMillis())
		{
			if (target.getTarget() == null || !target.getTarget().isMonster())
			{
				actor.sendMessage("You cannot report players, that aren't fighting with monsters.");
				return false;
			}
			lastAttacker = (Creature) target.getTarget();
		}
		// Synerge - Check if the player did damage to a monster in at least 5 seconds from now to see if he is fighting with mobs
		if (lastAttacker.isBoss() || lastAttacker.isSiegeGuard() || (target.getLastMonsterDamageTime() < System.currentTimeMillis() - 5000))
		{
			actor.sendMessage("You cannot report players, that aren't fighting with monsters!");
			return false;
		}

		return true;
	}

	/**
	 * Trying to Report Target Checking if actor can report target, sending message to actor if report was OK If actor could report target: - Adding quickVar LastCaptchaTest to the target - Adding Event to CaptchaTimers list - Generating and Sending Captcha Window to target
	 * @param actor Player that wants to report target
	 * @param target Player that is probably Bot
	 * @return success of the report
	 */
	public static boolean tryReportPlayer(Player actor, Player target)
	{
		if (!canReport(actor, target))
		{
			return false;
		}

		target.addQuickVar("LastCaptchaTest", System.currentTimeMillis());
		CaptchaTimer.getInstance().addBotReporter(actor);
		actor.sendMessage("Thanks for reporting player, he is now being tested.");

		String correctCaptcha = Captcha.sendCaptcha(target);
		CaptchaTimer.getInstance().addCaptchaTimer(actor, target, correctCaptcha);

		return true;
	}

	/**
	 * Handling Captcha Answer If Captcha Event is null, sending message to actor If Captcha is correct: {@link #onCorrectCaptcha(CaptchaEvent)} If Captcha is wrong: {@link #onFailedCaptcha(CaptchaEvent)}
	 * @param actor Player that had to write down Captcha
	 * @param answer Captcha Answer of the player
	 */
	public static void onAnswerCaptcha(Player actor, String answer)
	{
		CaptchaEvent event = CaptchaTimer.getInstance().getMyEvent(actor);
		if (event == null)
		{
			actor.sendMessage("You cannot answer Captcha at this time!");
			return;
		}
		if (answer == null || !event.getCorrectCaptcha().equalsIgnoreCase(answer.trim()))
		{
			onFailedCaptcha(event);
		}
		else
		{
			onCorrectCaptcha(event);
		}
	}

	/**
	 * Removing Event from Captcha Event List Jailing Player that failed Captcha, sending him message if he is still online If reporter is online, sending him message and giving back one Report Try
	 * @param event Captcha Event of the Target
	 */
	public static void onFailedCaptcha(CaptchaEvent event)
	{
		CaptchaTimer.getInstance().removeCaptchaTimer(event);
		String targetName = event.getTargetName();
		Player target = GameObjectsStorage.getPlayer(targetName);
		Player actor = GameObjectsStorage.getPlayer(event.getActorName());
		if (target == null)
		{
			punishment(targetName);
		}
		else
		{
			int count = target.getCapchaCount();
			if (count >= Config.CAPTCHA_COUNT)
			{
				target.updateCapchaCount(0);
				target.sendMessage("You have failed Captcha Test!");
				punishment(target.getName());

				if (actor != null)
				{
					actor.sendMessage("Reported player was put in Jail!");
					CaptchaTimer.getInstance().removeBotReporter(actor);
				}

				GmListTable.broadcastToGMs(new Say2(0, ChatType.HERO_VOICE, "REPORT", target.getName() + " has failed the catpcha code"));
			}
			else
			{
				target.updateCapchaCount(count + 1);
				target.sendMessage("You have one more chance for put captcha!");
				String correctCaptcha = Captcha.sendCaptcha(target);
				CaptchaTimer.getInstance().addCaptchaTimer(actor, target, correctCaptcha);
			}
		}
	}

	private static void punishment(String target)
	{
		int count = CaptchaPunishmentDAO.getInstance().loadReportCount(target);

		if (count == -1)
		{
			_log.error("Captcha punishment: incorrect count from target -> " + target);
			return;
		}

		String data = Config.CAPTCHA_PUNISHMENT[count];
		String[] array = data.split(":");

		String type = array[0];
		int size = Integer.parseInt(array[1]);
		count++;
		if (type.equals("JAIL"))
		{
			AutoBan.doJailPlayer(target, size * 60 * 1000L, true);
			sendMsg(target, count, size);
			if (count <= 1)
			{
				CaptchaPunishmentDAO.getInstance().insertReportCount(target, count);
			}
			else
			{
				CaptchaPunishmentDAO.getInstance().updateReportCount(target, count);
			}
		}
		else if (type.equals("BAN"))
		{
			AutoBan.Banned(target, size, -1, "Incorrect Captcha", "AutoBan");
			Player player = GameObjectsStorage.getPlayer(target);
			if (player != null)
			{
				player.kick();
			}
		}
		else
		{
			_log.error("Captcha punishment: Unknown punishment type -> " + type + " for target -> " + target);
		}
	}

	private static void sendMsg(String target, int count, int time)
	{
		Player player = GameObjectsStorage.getPlayer(target);
		if (player != null)
		{
			String data = Config.CAPTCHA_PUNISHMENT[count];
			String[] array = data.split(":");

			String type = array[0];
			int size = Integer.parseInt(array[1]);
			if (type.equals("JAIL"))
			{
				player.sendMessage("You are now jailed for " + time + " minutes, your next punishment is jail for " + size + " minutes.");
			}
			else if (type.equals("BAN"))
			{
				player.sendMessage("You are now jailed for " + time + " minutes, your next punishment is permanent ban.");
			}
		}
	}

	/**
	 * Removing Event from Captcha Event List Sending Messages to both, reporter and target. Both can be null
	 * @param event Captcha Event of the Target
	 */
	private static void onCorrectCaptcha(CaptchaEvent event)
	{
		CaptchaTimer.getInstance().removeCaptchaTimer(event);
		Player target = GameObjectsStorage.getPlayer(event.getTargetName());
		if (target != null)
		{
			target.sendMessage("Captcha is correct! Thank you!");
		}

		Player actor = GameObjectsStorage.getPlayer(event.getActorName());
		if (actor != null)
		{
			actor.sendMessage("Target answered Captcha correctly. He is not bot.");
		}
	}
}
