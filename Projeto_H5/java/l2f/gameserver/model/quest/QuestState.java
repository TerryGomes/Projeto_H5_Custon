package l2f.gameserver.model.quest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.cache.ImagesCache;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.instancemanager.QuestManager;
import l2f.gameserver.instancemanager.SpawnManager;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.OnKillListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.Summon;
import l2f.gameserver.model.base.Element;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.ExShowQuestMark;
import l2f.gameserver.network.serverpackets.PlaySound;
import l2f.gameserver.network.serverpackets.QuestList;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.TutorialCloseHtml;
import l2f.gameserver.network.serverpackets.TutorialEnableClientEvent;
import l2f.gameserver.network.serverpackets.TutorialShowHtml;
import l2f.gameserver.network.serverpackets.TutorialShowQuestionMark;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.spawn.PeriodOfDay;
import l2f.gameserver.utils.AddonsConfig;
import l2f.gameserver.utils.ItemFunctions;

public final class QuestState
{
	public class OnDeathListenerImpl implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			Player player = actor.getPlayer();
			if (player == null)
			{
				return;
			}

			player.removeListener(this);

			_quest.notifyDeath(killer, actor, QuestState.this);
		}
	}

	public class PlayerOnKillListenerImpl implements OnKillListener
	{
		@Override
		public void onKill(Creature actor, Creature victim)
		{
			if (!victim.isPlayer())
			{
				return;
			}

			Player actorPlayer = (Player) actor;
			List<Player> players = null;
			switch (_quest.getParty())
			{
			case Quest.PARTY_NONE:
				players = Collections.singletonList(actorPlayer);
				break;
			case Quest.PARTY_ALL:
				if (actorPlayer.getParty() == null)
				{
					players = Collections.singletonList(actorPlayer);
				}
				else
				{
					players = new ArrayList<Player>(actorPlayer.getParty().size());
					for (Player $member : actorPlayer.getParty().getMembers())
					{
						if ($member.isInRange(actorPlayer, Creature.INTERACTION_DISTANCE))
						{
							players.add($member);
						}
					}
				}
				break;
			default:
				players = Collections.emptyList();
				break;
			}

			for (Player player : players)
			{
				QuestState questState = player.getQuestState(_quest.getClass());
				if (questState != null && !questState.isCompleted())
				{
					_quest.notifyKill((Player) victim, questState);
				}
			}
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(QuestState.class);

	public static final int RESTART_HOUR = 6;
	public static final int RESTART_MINUTES = 30;
	public static final String VAR_COND = "cond";

	public final static QuestState[] EMPTY_ARRAY = new QuestState[0];

	private final Player _player;
	private final Quest _quest;
	private int _state;
	private Integer _cond = null;
	private final Map<String, String> _vars = new ConcurrentHashMap<String, String>();
	private final Map<String, QuestTimer> _timers = new ConcurrentHashMap<String, QuestTimer>();
	private OnKillListener _onKillListener = null;

	/**
	 * Constructor<?> of the QuestState : save the quest in the list of quests of the player.<BR/><BR/>
	 * <p/>
	 * <U><I>Actions :</U></I><BR/>
	 * <LI>Save informations in the object QuestState created (Quest, Player, Completion, State)</LI>
	 * <LI>Add the QuestState in the player's list of quests by using setQuestState()</LI>
	 * <LI>Add drops gotten by the quest</LI>
	 * <BR/>
	 *
	 * @param quest  : quest associated with the QuestState
	 * @param player : L2Player pointing out the player
	 * @param state  : state of the quest
	 */
	public QuestState(Quest quest, Player player, int state)
	{
		_quest = quest;
		_player = player;

		// Save the state of the quest for the player in the player's list of quest onwed
		player.setQuestState(this);

		// set the state of the quest
		_state = state;
		quest.notifyCreate(this);
	}

	/**
	 * Add XP and SP as quest reward
	 * <br><br>
	 * Метод учитывает рейты!
	 * 3-ий параметр true/false показывает является ли квест на профессию
	 * и рейты учитываются в завимисомти от параметра RateQuestsRewardOccupationChange
	 */
	public void addExpAndSp(long exp, long sp)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		if (exp > 0)
		{
			player.addExpAndSp((long) (exp * getRateQuestsReward()), 0);
		}
		if (sp > 0)
		{
			player.addExpAndSp(0, (long) (sp * getRateQuestsReward()));
		}
	}

	/**
	 * Add player to get notification of characters death
	 *
	 * @param player : L2Character of the character to get notification of death
	 */
	public void addNotifyOfDeath(Player player, boolean withPet)
	{
		OnDeathListenerImpl listener = new OnDeathListenerImpl();
		player.addListener(listener);
		if (withPet)
		{
			Summon summon = player.getPet();
			if (summon != null)
			{
				summon.addListener(listener);
			}
		}
	}

	public void addPlayerOnKillListener()
	{
		if (_onKillListener != null)
		{
			throw new IllegalArgumentException("Cant add twice kill listener to player");
		}

		_onKillListener = new PlayerOnKillListenerImpl();
		_player.addListener(_onKillListener);
	}

	public void removePlayerOnKillListener()
	{
		if (_onKillListener != null)
		{
			_player.removeListener(_onKillListener);
		}
	}

	public void addRadar(int x, int y, int z)
	{
		Player player = getPlayer();
		if (player != null)
		{
			player.addRadar(x, y, z);
		}
	}

	public void addRadarWithMap(int x, int y, int z)
	{
		Player player = getPlayer();
		if (player != null)
		{
			player.addRadarWithMap(x, y, z);
		}
	}

	/**
	 * Используется для однодневных квестов
	 */
	public void exitCurrentQuest(Quest quest)
	{
		Player player = getPlayer();
		exitCurrentQuest(true);
		quest.newQuestState(player, Quest.DELAYED);
		QuestState qs = player.getQuestState(quest.getClass());
		qs.setRestartTime();
	}

	/**
	 * Destroy element used by quest when quest is exited
	 *
	 * @param repeatable
	 * @return QuestState
	 */
	public QuestState exitCurrentQuest(boolean repeatable)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return this;
		}

		removePlayerOnKillListener();
		// Clean drops
		for (int itemId : _quest.getItems())
		{
			// Get [item from] / [presence of the item in] the inventory of the player
			ItemInstance item = player.getInventory().getItemByItemId(itemId);
			if (item == null || itemId == 57)
			{
				continue;
			}
			long count = item.getCount();
			// If player has the item in inventory, destroy it (if not gold)
			player.getInventory().destroyItemByItemId(itemId, count, "Exiting Quest " + _quest.getName());
			player.getWarehouse().destroyItemByItemId(itemId, count, "WH " + player.toString(), "Exiting Quest " + _quest.getName());// TODO [G1ta0] analyze this
		}

		// If quest is repeatable, delete quest from list of quest of the player and from database (quest CAN be created again => repeatable)
		if (repeatable)
		{
			player.removeQuestState(_quest.getName());
			Quest.deleteQuestInDb(this);
			_vars.clear();
		}
		else
		{ // Otherwise, delete variables for quest and update database (quest CANNOT be created again => not repeatable)
			for (String var : _vars.keySet())
			{
				if (var != null)
				{
					unset(var);
				}
			}
			setState(Quest.COMPLETED);
			Quest.updateQuestInDb(this); // FIXME: оно вроде не нужно?
		}
		player.sendPacket(new QuestList(player));

		if (getQuest().getQuestIntId() > 0)
		{
			if (repeatable)
			{
				player.getCounters().repeatableQuestsCompleted++;
			}
			else
			{
				player.getCounters().unrepeatableQuestsCompleted++;
			}
		}

		return this;
	}

	public void abortQuest()
	{
		_quest.onAbort(this);
		exitCurrentQuest(true);
	}

	/**
	 * <font color=red>Не использовать для получения кондов!</font><br><br>
	 * <p/>
	 * Return the value of the variable of quest represented by "var"
	 *
	 * @param var : name of the variable of quest
	 * @return Object
	 */
	public String get(String var)
	{
		return _vars.get(var);
	}

	public Map<String, String> getVars()
	{
		return _vars;
	}

	/**
	 * Возвращает переменную в виде целого числа.
	 *
	 * @param var : String designating the variable for the quest
	 * @return int
	 */
	public int getInt(String var)
	{
		int varint = 0;
		try
		{
			String val = get(var);
			if (val == null)
			{
				return 0;
			}
			varint = Integer.parseInt(val);
		}
		catch (NumberFormatException e)
		{
			_log.error(_player.getName() + ": variable " + var + " isn't an integer: " + varint, e);
		}
		return varint;
	}

	/**
	 * Return item number which is equipped in selected slot
	 *
	 * @return int
	 */
	public int getItemEquipped(int loc)
	{
		return getPlayer().getInventory().getPaperdollItemId(loc);
	}

	/**
	 * @return L2Player
	 */
	public Player getPlayer()
	{
		return _player;
	}

	/**
	 * Return the quest
	 *
	 * @return Quest
	 */
	public Quest getQuest()
	{
		return _quest;
	}

	public boolean checkQuestItemsCount(int... itemIds)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return false;
		}
		for (int itemId : itemIds)
		{
			if (player.getInventory().getCountOf(itemId) <= 0)
			{
				return false;
			}
		}
		return true;
	}

	public long getSumQuestItemsCount(int... itemIds)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return 0;
		}
		long count = 0;
		for (int itemId : itemIds)
		{
			count += player.getInventory().getCountOf(itemId);
		}
		return count;
	}

	/**
	 * Return the quantity of one sort of item hold by the player
	 *
	 * @param itemId : ID of the item wanted to be count
	 * @return int
	 */
	public long getQuestItemsCount(int itemId)
	{
		Player player = getPlayer();
		return player == null ? 0 : player.getInventory().getCountOf(itemId);
	}

	public long getQuestItemsCount(int... itemsIds)
	{
		long result = 0;
		for (int id : itemsIds)
		{
			result += getQuestItemsCount(id);
		}
		return result;
	}

	public boolean haveQuestItem(int itemId, int count)
	{
		if (getQuestItemsCount(itemId) >= count)
		{
			return true;
		}
		return false;
	}

	public boolean haveQuestItem(int itemId)
	{
		return haveQuestItem(itemId, 1);
	}

	public int getState()
	{
		return _state == Quest.DELAYED ? Quest.CREATED : _state;
	}

	public String getStateName()
	{
		return Quest.getStateName(_state);
	}

	/**
	 * Добавить предмет игроку
	 * By default if item is adena rates 'll be applyed, else no
	 *
	 * @param itemId
	 * @param count
	 */
	public void giveItems(int itemId, long count)
	{
		if (itemId == ItemTemplate.ITEM_ID_ADENA)
		{
			giveItems(itemId, count, true);
		}
		else
		{
			giveItems(itemId, count, false);
		}
	}

	/**
	 * Добавить предмет игроку
	 *
	 * @param itemId
	 * @param count
	 * @param rate   - учет квестовых рейтов
	 */
	public void giveItems(int itemId, long count, boolean rate)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return;
		}

		if (count <= 0)
		{
			count = 1;
		}

		if (rate)
		{
			count = (long) (count * getRateQuestsReward());
		}

		ItemFunctions.addItem(player, itemId, count, true, "Quest " + _quest.getName());
		player.sendChanges();
	}

	public void giveItems(int itemId, long count, Element element, int power)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return;
		}

		if (count <= 0)
		{
			count = 1;
		}

		// Get template of item
		ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
		if (template == null)
		{
			return;
		}

		for (int i = 0; i < count; i++)
		{
			ItemInstance item = ItemFunctions.createItem(itemId);

			if (element != Element.NONE)
			{
				item.setAttributeElement(element, power);
			}

			// Add items to player's inventory
			player.getInventory().addItem(item, "Quest " + _quest.getName());
		}

		player.sendPacket(SystemMessage2.obtainItems(template.getItemId(), count, 0));
		player.sendChanges();
	}

	public void dropItem(NpcInstance npc, int itemId, long count)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return;
		}

		ItemInstance item = ItemFunctions.createItem(itemId);
		item.setCount(count);
		item.dropToTheGround(player, npc);
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param count	  количество при рейтах 1х
	 * @param calcChance шанс при рейтах 1х, в процентах
	 * @return количество вещей для дропа, может быть 0
	 */
	public int rollDrop(int count, double calcChance)
	{
		if (calcChance <= 0 || count <= 0)
		{
			return 0;
		}
		return rollDrop(count, count, calcChance);
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param min		минимальное количество при рейтах 1х
	 * @param max		максимальное количество при рейтах 1х
	 * @param calcChance шанс при рейтах 1х, в процентах
	 * @return количество вещей для дропа, может быть 0
	 */
	public int rollDrop(int min, int max, double calcChance)
	{
		if (calcChance <= 0 || min <= 0 || max <= 0)
		{
			return 0;
		}
		int dropmult = 1;
		calcChance *= getRateQuestsDrop();
		if (getQuest().getParty() > Quest.PARTY_NONE)
		{
			Player player = getPlayer();
			if (player.getParty() != null)
			{
				calcChance *= Config.ALT_PARTY_BONUS[player.getParty().getMemberCountInRange(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) - 1];
			}
		}
		if (calcChance > 100)
		{
			if ((int) Math.ceil(calcChance / 100) <= calcChance / 100)
			{
				calcChance = Math.nextUp(calcChance);
			}
			dropmult = (int) Math.ceil(calcChance / 100);
			calcChance = calcChance / dropmult;
		}
		return Rnd.chance(calcChance) ? Rnd.get(min * dropmult, max * dropmult) : 0;
	}

	public double getRateQuestsDrop()
	{
		Player player = getPlayer();
		double Bonus = player == null ? 1. : player.getBonus().getQuestDropRate();
		if (Config.ALLOW_ADDONS_CONFIG)
		{
			return Config.RATE_QUESTS_DROP * Bonus * AddonsConfig.getQuestDropRates(getQuest());
		}
		return Config.RATE_QUESTS_DROP * Bonus;
	}

	public double getRateQuestsReward()
	{
		Player player = getPlayer();
		double Bonus = player == null ? 1. : player.getBonus().getQuestRewardRate();
		if (Config.ALLOW_ADDONS_CONFIG)
		{
			return Config.RATE_QUESTS_REWARD * Bonus * AddonsConfig.getQuestRewardRates(getQuest());
		}
		return Config.RATE_QUESTS_REWARD * Bonus;
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * проверяет максимум, а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId	 id вещи
	 * @param min		минимальное количество при рейтах 1х
	 * @param max		максимальное количество при рейтах 1х
	 * @param limit	  максимум таких вещей
	 * @param calcChance
	 * @return true если после выполнения количество достигло лимита
	 */
	public boolean rollAndGive(int itemId, int min, int max, int limit, double calcChance)
	{
		if (calcChance <= 0 || min <= 0 || max <= 0 || limit <= 0 || itemId <= 0)
		{
			return false;
		}
		long count = rollDrop(min, max, calcChance);
		if (count > 0)
		{
			long alreadyCount = getQuestItemsCount(itemId);
			if (alreadyCount + count > limit)
			{
				count = limit - alreadyCount;
			}
			if (count > 0)
			{
				giveItems(itemId, count, false);
				if (count + alreadyCount < limit)
				{
					playSound(Quest.SOUND_ITEMGET);
				}
				else
				{
					playSound(Quest.SOUND_MIDDLE);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId	 id вещи
	 * @param min		минимальное количество при рейтах 1х
	 * @param max		максимальное количество при рейтах 1х
	 * @param calcChance
	 */
	public void rollAndGive(int itemId, int min, int max, double calcChance)
	{
		if (calcChance <= 0 || min <= 0 || max <= 0 || itemId <= 0)
		{
			return;
		}
		int count = rollDrop(min, max, calcChance);
		if (count > 0)
		{
			giveItems(itemId, count, false);
			playSound(Quest.SOUND_ITEMGET);
		}
	}

	/**
	 * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
	 * а так же проигрывает звук получения вещи.
	 * <br><br>
	 * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
	 * нужно набить определенное количество предметов не осуществляется.
	 * <br><br>
	 * Ни один из передаваемых параметров не должен быть равен 0
	 *
	 * @param itemId	 id вещи
	 * @param count	  количество при рейтах 1х
	 * @param calcChance
	 */
	public boolean rollAndGive(int itemId, int count, double calcChance)
	{
		if (calcChance <= 0 || count <= 0 || itemId <= 0)
		{
			return false;
		}
		int countToDrop = rollDrop(count, calcChance);
		if (countToDrop > 0)
		{
			giveItems(itemId, countToDrop, false);
			playSound(Quest.SOUND_ITEMGET);
			return true;
		}
		return false;
	}

	/**
	 * Return true if quest completed, false otherwise
	 *
	 * @return boolean
	 */
	public boolean isCompleted()
	{
		return getState() == Quest.COMPLETED;
	}

	/**
	 * Return true if quest started, false otherwise
	 *
	 * @return boolean
	 */
	public boolean isStarted()
	{
		return getState() == Quest.STARTED;
	}

	/**
	 * Return true if quest created, false otherwise
	 *
	 * @return boolean
	 */
	public boolean isCreated()
	{
		return getState() == Quest.CREATED;
	}

	public void killNpcByObjectId(int _objId)
	{
		NpcInstance npc = GameObjectsStorage.getNpc(_objId);
		if (npc != null)
		{
			npc.doDie(null);
		}
		else
		{
			_log.warn("Attemp to kill object that is not npc in quest " + getQuest().getQuestIntId());
		}
	}

	/**
	 * Check if a given variable is set for this quest.
	 * @param variable the variable to check
	 * @return {@code true} if the variable is set, {@code false} otherwise
	 * @see #get(String)
	 * @see #getInt(String)
	 * @see #getCond()
	 */
	public boolean isSet(String variable)
	{
		return (get(variable) != null);
	}

	public String set(String var, String val)
	{
		return set(var, val, true);
	}

	public String set(String var, int intval)
	{
		return set(var, String.valueOf(intval), true);
	}

	/**
	 * <font color=red>Использовать осторожно! Служебная функция!</font><br><br>
	 * <p/>
	 * Устанавливает переменную и сохраняет в базу, если установлен флаг. Если получен cond обновляет список квестов игрока (только с флагом).
	 *
	 * @param var   : String pointing out the name of the variable for quest
	 * @param val   : String pointing out the value of the variable for quest
	 * @param store : Сохраняет в базу и если var это cond обновляет список квестов игрока.
	 * @return String (equal to parameter "val")
	 */
	public String set(String var, String val, boolean store)
	{
		if (val == null)
		{
			val = StringUtils.EMPTY;
		}

		_vars.put(var, val);

		if (store)
		{
			Quest.updateQuestVarInDb(this, var, val);
		}

		return val;
	}

	/**
	 * Return state of the quest after its initialization.<BR><BR>
	 * <U><I>Actions :</I></U>
	 * <LI>Remove drops from previous state</LI>
	 * <LI>Set new state of the quest</LI>
	 * <LI>Add drop for new state</LI>
	 * <LI>Update information in database</LI>
	 * <LI>Send packet QuestList to client</LI>
	 *
	 * @param state
	 * @return object
	 */
	public Object setState(int state)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return null;
		}

		_state = state;

		if (getQuest().isVisible() && isStarted())
		{
			player.sendPacket(new ExShowQuestMark(getQuest().getQuestIntId()));
		}

		Quest.updateQuestInDb(this);
		player.sendPacket(new QuestList(player));
		return state;
	}

	public Object setStateAndNotSave(int state)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return null;
		}

		_state = state;

		if (getQuest().isVisible() && isStarted())
		{
			player.sendPacket(new ExShowQuestMark(getQuest().getQuestIntId()));
		}

		player.sendPacket(new QuestList(player));
		return state;
	}

	/**
	 * Send a packet in order to play sound at client terminal
	 *
	 * @param sound
	 */
	public void playSound(String sound)
	{
		Player player = getPlayer();
		if (player != null)
		{
			player.sendPacket(new PlaySound(sound));
		}
	}

	public void playTutorialVoice(String voice)
	{
		Player player = getPlayer();
		if (player != null)
		{
			player.sendPacket(new PlaySound(PlaySound.Type.VOICE, voice, 0, 0, player.getLoc()));
		}
	}

	public void onTutorialClientEvent(int number)
	{
		Player player = getPlayer();
		if (player != null)
		{
			player.sendPacket(new TutorialEnableClientEvent(number));
		}
	}

	public void showQuestionMark(int number)
	{
		Player player = getPlayer();
		if (player != null)
		{
			player.sendPacket(new TutorialShowQuestionMark(number));
		}
	}

	public void showTutorialPage(String html)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return;
		}
		String text = HtmCache.getInstance().getNotNull("quests/_255_Tutorial/" + html, player);
		player.sendPacket(new TutorialShowHtml(text));
	}

	public void closeTutorial()
	{
		onTutorialClientEvent(0);
		if (_player != null)
		{
			_player.sendPacket(TutorialCloseHtml.STATIC);
			_player.deleteQuickVar("watchingTutorial");
			Quest q = QuestManager.getQuest(255);
			if (q != null)
			{
				_player.processQuestEvent(q.getName(), "onTutorialClose", null);
			}
		}
	}

	public void showTutorialHTML(String html)
	{
		if (_player != null)
		{
			// Synerge - Added support for showing crest images on tutorial windows
			html = ImagesCache.getInstance().sendUsedImages(html, _player);

			// Synerge - If the html has crests then we should delay the tutorial html so the images reach their destination before the htm
			if (html.startsWith("CREST"))
			{
				ThreadPoolManager.getInstance().schedule(new TutorialShowThread(html.substring(5)), 200);
			}
			else
			{
				_player.sendPacket(new TutorialShowHtml(html));
				_player.addQuickVar("watchingTutorial", true);
			}
		}
	}

	private class TutorialShowThread implements Runnable
	{
		private final String _html;

		public TutorialShowThread(String html)
		{
			_html = html;
		}

		@Override
		public void run()
		{
			if (_player == null)
			{
				return;
			}

			_player.sendPacket(new TutorialShowHtml(_html));
			_player.addQuickVar("watchingTutorial", true);
		}
	}

	/**
	 * Start a timer for quest.<BR><BR>
	 *
	 * @param name<BR> The name of the timer. Will also be the value for event of onEvent
	 * @param time<BR> The milisecond value the timer will elapse
	 */
	public void startQuestTimer(String name, long time)
	{
		startQuestTimer(name, time, null);
	}

	/**
	 * Add a timer to the quest.<BR><BR>
	 *
	 * @param name:   name of the timer (also passed back as "event" in notifyEvent)
	 * @param time:   time in ms for when to fire the timer
	 * @param npc:    npc associated with this timer (can be null)
	 */
	public void startQuestTimer(String name, long time, NpcInstance npc)
	{
		QuestTimer timer = new QuestTimer(name, time, npc);
		timer.setQuestState(this);
		QuestTimer oldTimer = getTimers().put(name, timer);
		if (oldTimer != null)
		{
			oldTimer.stop();
		}
		timer.start();
	}

	public boolean isRunningQuestTimer(String name)
	{
		return getTimers().get(name) != null;
	}

	public boolean cancelQuestTimer(String name)
	{
		QuestTimer timer = removeQuestTimer(name);
		if (timer != null)
		{
			timer.stop();
		}
		return timer != null;
	}

	QuestTimer removeQuestTimer(String name)
	{
		QuestTimer timer = getTimers().remove(name);
		if (timer != null)
		{
			timer.setQuestState(null);
		}
		return timer;
	}

	public void pauseQuestTimers()
	{
		getQuest().pauseQuestTimers(this);
	}

	public void stopQuestTimers()
	{
		for (QuestTimer timer : getTimers().values())
		{
			timer.setQuestState(null);
			timer.stop();
		}
		_timers.clear();
	}

	public void resumeQuestTimers()
	{
		getQuest().resumeQuestTimers(this);
	}

	Map<String, QuestTimer> getTimers()
	{
		return _timers;
	}

	/**
	 * Удаляет указанные предметы из инвентаря игрока, и обновляет инвентарь
	 *
	 * @param itemId : id удаляемого предмета
	 * @param count  : число удаляемых предметов<br>
	 *               Если count передать -1, то будут удалены все указанные предметы.
	 * @return Количество удаленных предметов
	 */
	public long takeItems(int itemId, long count)
	{
		Player player = getPlayer();
		if (player == null)
		{
			return 0;
		}

		// Get object item from player's inventory list
		ItemInstance item = player.getInventory().getItemByItemId(itemId);
		if (item == null)
		{
			return 0;
		}
		// Tests on count value in order not to have negative value
		if (count < 0 || count > item.getCount())
		{
			count = item.getCount();
		}

		// Destroy the quantity of items wanted
		player.getInventory().destroyItemByItemId(itemId, count, "Quest " + _quest.getName());
		// Send message of destruction to client
		player.sendPacket(SystemMessage2.removeItems(itemId, count));

		return count;
	}

	public long takeAllItems(int itemId)
	{
		return takeItems(itemId, -1);
	}

	public long takeAllItems(int... itemsIds)
	{
		long result = 0;
		for (int id : itemsIds)
		{
			result += takeAllItems(id);
		}
		return result;
	}

	public long takeAllItems(Collection<Integer> itemsIds)
	{
		long result = 0;
		for (int id : itemsIds)
		{
			result += takeAllItems(id);
		}
		return result;
	}

	/**
	 * Remove the variable of quest from the list of variables for the quest.<BR><BR>
	 * <U><I>Concept : </I></U>
	 * Remove the variable of quest represented by "var" from the class variable FastMap "vars" and from the database.
	 *
	 * @param var : String designating the variable for the quest to be deleted
	 * @return String pointing out the previous value associated with the variable "var"
	 */
	public String unset(String var)
	{
		if (var == null)
		{
			return null;
		}
		String old = _vars.remove(var);
		if (old != null)
		{
			Quest.deleteQuestVarInDb(this, var);
		}
		return old;
	}

	private boolean checkPartyMember(Player member, int state, int maxrange, GameObject rangefrom)
	{
		if ((member == null) || (rangefrom != null && maxrange > 0 && !member.isInRange(rangefrom, maxrange)))
		{
			return false;
		}
		QuestState qs = member.getQuestState(getQuest().getName());
		if (qs == null || qs.getState() != state)
		{
			return false;
		}
		return true;
	}

	public List<Player> getPartyMembers(int state, int maxrange, GameObject rangefrom)
	{
		List<Player> result = new ArrayList<Player>();
		Party party = getPlayer().getParty();
		if (party == null)
		{
			if (checkPartyMember(getPlayer(), state, maxrange, rangefrom))
			{
				result.add(getPlayer());
			}
			return result;
		}

		for (Player _member : party.getMembers())
		{
			if (checkPartyMember(_member, state, maxrange, rangefrom))
			{
				result.add(getPlayer());
			}
		}

		return result;
	}

	public Player getRandomPartyMember(int state, int maxrangefromplayer)
	{
		return getRandomPartyMember(state, maxrangefromplayer, getPlayer());
	}

	public Player getRandomPartyMember(int state, int maxrange, GameObject rangefrom)
	{
		List<Player> list = getPartyMembers(state, maxrange, rangefrom);
		if (list.size() == 0)
		{
			return null;
		}
		return list.get(Rnd.get(list.size()));
	}

	/**
	 * Add spawn for player instance
	 * Return object id of newly spawned npc
	 */
	public NpcInstance addSpawn(int npcId)
	{
		return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, 0, 0);
	}

	public NpcInstance addSpawn(int npcId, int despawnDelay)
	{
		return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, 0, despawnDelay);
	}

	public NpcInstance addSpawn(int npcId, int x, int y, int z)
	{
		return addSpawn(npcId, x, y, z, 0, 0, 0);
	}

	/**
	 * Add spawn for player instance
	 * Will despawn after the spawn length expires
	 * Return object id of newly spawned npc
	 */
	public NpcInstance addSpawn(int npcId, int x, int y, int z, int despawnDelay)
	{
		return addSpawn(npcId, x, y, z, 0, 0, despawnDelay);
	}

	/**
	 * Add spawn for player instance
	 * Return object id of newly spawned npc
	 */
	public NpcInstance addSpawn(int npcId, int x, int y, int z, int heading, int randomOffset, int despawnDelay)
	{
		return getQuest().addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay);
	}

	public NpcInstance findTemplate(int npcId)
	{
		for (Spawner spawn : SpawnManager.getInstance().getSpawners(PeriodOfDay.NONE.name()))
		{
			if (spawn != null && spawn.getCurrentNpcId() == npcId)
			{
				return spawn.getLastSpawn();
			}
		}
		return null;
	}

	public int calculateLevelDiffForDrop(int mobLevel, int player)
	{
		if (!Config.DEEPBLUE_DROP_RULES)
		{
			return 0;
		}
		return Math.max(player - mobLevel - Config.DEEPBLUE_DROP_MAXDIFF, 0);
	}

	public int getCond()
	{
		if (_cond == null)
		{
			int val = getInt(VAR_COND);
			if ((val & 0x80000000) != 0)
			{
				val &= 0x7fffffff;
				for (int i = 1; i < 32; i++)
				{
					val = (val >> 1);
					if (val == 0)
					{
						val = i;
						break;
					}
				}
			}
			_cond = val;
		}

		return _cond.intValue();
	}

	public String setCond(int newCond)
	{
		return setCond(newCond, true);
	}

	public String setCond(int newCond, boolean store)
	{
		if (newCond == getCond())
		{
			return String.valueOf(newCond);
		}

		int oldCond = getInt(VAR_COND);
		_cond = newCond;

		if ((oldCond & 0x80000000) != 0)
		{
			// уже используется второй формат
			if (newCond > 2) // Если этап меньше 3 то возвращаемся к первому варианту.
			{
				oldCond &= 0x80000001 | ((1 << newCond) - 1);
				newCond = oldCond | (1 << (newCond - 1));
			}
		}
		else // Второй вариант теперь используется всегда если этап больше 2
		if (newCond > 2)
		{
			newCond = 0x80000001 | (1 << (newCond - 1)) | ((1 << oldCond) - 1);
		}

		final String sVal = String.valueOf(newCond);
		final String result = set(VAR_COND, sVal, false);
		if (store)
		{
			Quest.updateQuestVarInDb(this, VAR_COND, sVal);
		}

		final Player player = getPlayer();
		if (player != null)
		{
			player.sendPacket(new QuestList(player));
			if (newCond != 0 && getQuest().isVisible() && isStarted())
			{
				player.sendPacket(new ExShowQuestMark(getQuest().getQuestIntId()));
			}
		}
		return result;
	}

	/**
	 * Устанавлевает время, когда квест будет доступен персонажу.
	 * Метод используется для квестов, которые проходятся один раз в день.
	 */
	public void setRestartTime()
	{
		Calendar reDo = Calendar.getInstance();
		if (reDo.get(Calendar.HOUR_OF_DAY) >= RESTART_HOUR)
		{
			reDo.add(Calendar.DATE, 1);
		}
		reDo.set(Calendar.HOUR_OF_DAY, RESTART_HOUR);
		reDo.set(Calendar.MINUTE, RESTART_MINUTES);
		set("restartTime", String.valueOf(reDo.getTimeInMillis()));
	}

	public long getRestartTime()
	{
		String val = get("restartTime");
		if (val == null)
		{
			return 0;
		}

		return Long.parseLong(val);
	}

	/**
	 * Проверяет, наступило ли время для выполнения квеста.
	 * Метод используется для квестов, которые проходятся один раз в день.
	 * @return boolean
	 */
	public boolean isNowAvailable()
	{
		String val = get("restartTime");
		if (val == null)
		{
			return true;
		}

		long restartTime = Long.parseLong(val);
		return restartTime <= System.currentTimeMillis();
	}
}