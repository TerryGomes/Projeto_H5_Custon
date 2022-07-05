package events.l2day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.util.Rnd;
import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.reward.RewardData;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.npc.NpcTemplate;

public class LettersCollection extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(LettersCollection.class);
	// Переменные, определять
	protected static boolean _active;
	protected static String _name;
	protected static int[][] letters;
	protected static int EVENT_MANAGERS[][] = null;
	protected static String _msgStarted;
	protected static String _msgEnded;

	// Буквы, статика
	protected static int A = 3875;
	protected static int C = 3876;
	protected static int E = 3877;
	protected static int F = 3878;
	protected static int G = 3879;
	protected static int H = 3880;
	protected static int I = 3881;
	protected static int L = 3882;
	protected static int N = 3883;
	protected static int O = 3884;
	protected static int R = 3885;
	protected static int S = 3886;
	protected static int T = 3887;
	protected static int II = 3888;
	protected static int Y = 13417;
	protected static int _5 = 13418;

	protected static int EVENT_MANAGER_ID = 31230;

	// Контейнеры, не трогать
	protected static Map<String, Integer[][]> _words = new HashMap<String, Integer[][]>();
	protected static Map<String, RewardData[]> _rewards = new HashMap<String, RewardData[]>();
	protected static List<SimpleSpawner> _spawns = new ArrayList<SimpleSpawner>();

	@Override
	public void onLoad()
	{
		if (isActive())
		{
			CharListenerList.addGlobal(this);
			_active = true;
			spawnEventManagers();
			_log.info("Loaded Event: " + _name + " [state: activated]");
		}
		else
		{
			_log.info("Loaded Event: " + _name + " [state: deactivated]");
		}
	}

	/**
	 * Читает статус эвента из базы.
	 * @return
	 */
	protected static boolean isActive()
	{
		return IsActive(_name);
	}

	/**
	 * Спавнит эвент менеджеров
	 */
	protected void spawnEventManagers()
	{
		SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, _spawns);
	}

	/**
	 * Удаляет спавн эвент менеджеров
	 */
	protected void unSpawnEventManagers()
	{
		deSpawnNPCs(_spawns);
	}

	@Override
	public void onReload()
	{
		unSpawnEventManagers();
	}

	@Override
	public void onShutdown()
	{
		unSpawnEventManagers();
	}

	/**
	 * Обработчик смерти мобов, управляющий эвентовым дропом
	 */
	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		if (_active && SimpleCheckDrop(cha, killer))
		{
			int[] letter = letters[Rnd.get(letters.length)];
			if (Rnd.chance(letter[1] * Config.EVENT_L2DAY_LETTER_CHANCE * ((NpcTemplate) cha.getTemplate()).rateHp))
			{
				((NpcInstance) cha).dropItem(killer.getPlayer(), letter[0], 1);
			}
		}
	}

	/**
	 * Запускает эвент
	 */
	public void startEvent()
	{
		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}

		if (SetActive(_name, true))
		{
			spawnEventManagers();
			System.out.println("Event '" + _name + "' started.");
			Announcements.getInstance().announceByCustomMessage(_msgStarted, null);
		}
		else
		{
			player.sendMessage("Event '" + _name + "' already started.");
		}

		_active = true;

		show("admin/events/events.htm", player);
	}

	/**
	 * Останавливает эвент
	 */
	public void stopEvent()
	{
		Player player = getSelf();
		if (!player.getPlayerAccess().IsEventGm)
		{
			return;
		}
		if (SetActive(_name, false))
		{
			unSpawnEventManagers();
			System.out.println("Event '" + _name + "' stopped.");
			Announcements.getInstance().announceByCustomMessage(_msgEnded, null);
		}
		else
		{
			player.sendMessage("Event '" + _name + "' not started.");
		}

		_active = false;

		show("admin/events/events.htm", player);
	}

	/**
	 * Обмен эвентовых вещей, где var[0] - слово.
	 */
	public void exchange(String[] var)
	{
		Player player = getSelf();

		if (!player.isQuestContinuationPossible(true) || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
		{
			return;
		}

		Integer[][] mss = _words.get(var[0]);

		for (Integer[] l : mss)
		{
			if (getItemCount(player, l[0]) < l[1])
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
				return;
			}
		}

		for (Integer[] l : mss)
		{
			removeItem(player, l[0], l[1], "LettersCollection");
		}

		RewardData[] rewards = _rewards.get(var[0]);
		int sum = 0;
		for (RewardData r : rewards)
		{
			sum += r.getChance();
		}
		int random = Rnd.get(sum);
		sum = 0;
		for (RewardData r : rewards)
		{
			sum += r.getChance();
			if (sum > random)
			{
				addItem(player, r.getItemId(), Rnd.get(r.getMinDrop(), r.getMaxDrop()), "LettersCollection");
				return;
			}
		}
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		if (_active)
		{
			Announcements.getInstance().announceToPlayerByCustomMessage(player, _msgStarted, null);
		}
	}

	public String DialogAppend_31230(Integer val)
	{
		if (!_active)
		{
			return "";
		}

		StringBuilder append = new StringBuilder("<br><br>");
		for (String word : _words.keySet())
		{
			append.append("[scripts_").append(getClass().getName()).append(":exchange ").append(word).append("|").append(word).append("]<br1>");
		}

		return append.toString();
	}
}