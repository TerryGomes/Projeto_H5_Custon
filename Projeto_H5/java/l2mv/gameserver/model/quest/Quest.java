package l2mv.gameserver.model.quest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import l2mv.commons.logging.LogUtils;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.TroveUtils;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.entity.olympiad.OlympiadGame;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.ExNpcQuestHtmlMessage;
import l2mv.gameserver.network.serverpackets.ExQuestNpcLogList;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.HtmlUtils;
import l2mv.gameserver.utils.Location;

public class Quest
{
	private static final Logger _log = LoggerFactory.getLogger(Quest.class);

	public static final String SOUND_ITEMGET = "ItemSound.quest_itemget";
	public static final String SOUND_ACCEPT = "ItemSound.quest_accept";
	public static final String SOUND_MIDDLE = "ItemSound.quest_middle";
	public static final String SOUND_FINISH = "ItemSound.quest_finish";
	public static final String SOUND_GIVEUP = "ItemSound.quest_giveup";
	public static final String SOUND_TUTORIAL = "ItemSound.quest_tutorial";
	public static final String SOUND_JACKPOT = "ItemSound.quest_jackpot";
	public static final String SOUND_HORROR2 = "SkillSound5.horror_02";
	public static final String SOUND_BEFORE_BATTLE = "Itemsound.quest_before_battle";
	public static final String SOUND_FANFARE_MIDDLE = "ItemSound.quest_fanfare_middle";
	public static final String SOUND_FANFARE2 = "ItemSound.quest_fanfare_2";
	public static final String SOUND_BROKEN_KEY = "ItemSound2.broken_key";
	public static final String SOUND_ENCHANT_SUCESS = "ItemSound3.sys_enchant_sucess";
	public static final String SOUND_ENCHANT_FAILED = "ItemSound3.sys_enchant_failed";
	public static final String SOUND_ED_CHIMES05 = "AmdSound.ed_chimes_05";
	public static final String SOUND_ARMOR_WOOD_3 = "ItemSound.armor_wood_3";
	public static final String SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH = "ItemSound.item_drop_equip_armor_cloth";

	public static final String NO_QUEST_DIALOG = "no-quest";

	public static final int ADENA_ID = 57;

	public static final int PARTY_NONE = 0;
	public static final int PARTY_ONE = 1;
	public static final int PARTY_ALL = 2;

	// карта с приостановленными квестовыми таймерами для каждого игрока
	private final Map<Integer, Map<String, QuestTimer>> _pausedQuestTimers = new ConcurrentHashMap<Integer, Map<String, QuestTimer>>();

	private final TIntHashSet _questItems = new TIntHashSet();
	private TIntObjectHashMap<List<QuestNpcLogInfo>> _npcLogList = TroveUtils.emptyIntObjectMap();

	/**
	 * Этот метод для регистрации квестовых вещей, которые будут удалены
	 * при прекращении квеста, независимо от того, был он закончен или
	 * прерван. <strong>Добавлять сюда награды нельзя</strong>.
	 */
	public void addQuestItem(int... ids)
	{
		for (int id : ids)
		{
			if (id != 0)
			{
				ItemTemplate i = null;
				i = ItemHolder.getInstance().getTemplate(id);

				if (_questItems.contains(id))
				{
					_log.warn("Item " + i + " multiple times in quest drop in " + getName());
				}

				_questItems.add(id);
			}
		}
	}

	public int[] getItems()
	{
		return _questItems.toArray();
	}

	public boolean isQuestItem(int id)
	{
		return _questItems.contains(id);
	}

	/**
	 * Update informations regarding quest in database.<BR>
	 * <U><I>Actions :</I></U><BR>
	 * <LI>Get ID state of the quest recorded in object qs</LI>
	 * <LI>Save in database the ID state (with or without the star) for the variable called "&lt;state&gt;" of the quest</LI>
	 * @param qs : QuestState
	 */
	public static void updateQuestInDb(QuestState qs)
	{
		updateQuestVarInDb(qs, "<state>", qs.getStateName());
	}

	/**
	 * Insert in the database the quest for the player.
	 * @param qs : QuestState pointing out the state of the quest
	 * @param var : String designating the name of the variable for the quest
	 * @param value : String designating the value of the variable for the quest
	 */
	public static void updateQuestVarInDb(QuestState qs, String var, String value)
	{
		Player player = qs.getPlayer();
		if (player == null)
		{
			return;
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)"))
		{
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuest().getName());
			statement.setString(3, var);
			statement.setString(4, value);
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Could not insert char quest:", e);
		}
	}

	/**
	 * Delete the player's quest from database.
	 * @param qs : QuestState pointing out the player's quest
	 */
	public static void deleteQuestInDb(QuestState qs)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=?"))
		{
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuest().getName());
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("could not delete char quest", e);
		}
	}

	/**
	 * Delete a variable of player's quest from the database.
	 * @param qs : object QuestState pointing out the player's quest
	 * @param var : String designating the variable characterizing the quest
	 */
	public static void deleteQuestVarInDb(QuestState qs, String var)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=? AND var=?"))
		{
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuest().getName());
			statement.setString(3, var);
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.error("Could not delete char quest", e);
		}
	}

	/**
	 * Add quests to the L2Player.<BR><BR>
	 * <U><I>Action : </U></I><BR>
	 * Add state of quests, drops and variables for quests in the HashMap _quest of L2Player
	 * @param player : Player who is entering the world
	 */
	public static void restoreQuestStates(Player player, Connection con)
	{
		try (PreparedStatement invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? and name=?"); PreparedStatement statement = con.prepareStatement("SELECT name,value FROM character_quests WHERE char_id=? AND var=?"))
		{
			statement.setInt(1, player.getObjectId());
			statement.setString(2, "<state>");
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					String questId = rset.getString("name");
					String state = rset.getString("value");

					if (state.equalsIgnoreCase("Start")) // невзятый квест
					{
						invalidQuestData.setInt(1, player.getObjectId());
						invalidQuestData.setString(2, questId);
						invalidQuestData.executeUpdate();
						continue;
					}

					// Search quest associated with the ID
					Quest q = QuestManager.getQuest(questId);
					if (q == null)
					{
						if (!Config.DONTLOADQUEST)
						{
							_log.warn("Unknown quest " + questId + " for player " + player.getName());
						}
						continue;
					}

					// Create a new QuestState for the player that will be added to the player's list of quests
					new QuestState(q, player, getStateId(state));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while restoring Quest States ", e);
		}
		// Get list of quests owned by the player from the DB in order to add variables used in the quest.
		try (PreparedStatement statement = con.prepareStatement("SELECT name,var,value FROM character_quests WHERE char_id=?"))
		{
			statement.setInt(1, player.getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					String questId = rset.getString("name");
					String var = rset.getString("var");
					String value = rset.getString("value");
					// Get the QuestState saved in the loop before
					QuestState qs = player.getQuestState(questId);
					if (qs == null)
					{
						continue;
					}
					// затычка на пропущенный первый конд
					if (var.equals("cond") && Integer.parseInt(value) < 0)
					{
						value = String.valueOf(Integer.parseInt(value) | 1);
					}
					// Add parameter to the quest
					qs.set(var, value, false);
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while restoring Quest States ", e);
		}
	}

	protected final String _name;

	protected final int _party;

	protected final int _questId;

	public final static int CREATED = 1;
	public final static int STARTED = 2;
	public final static int COMPLETED = 3;
	public final static int DELAYED = 4;

	public static String getStateName(int state)
	{
		switch (state)
		{
		case CREATED:
			return "Start";
		case STARTED:
			return "Started";
		case COMPLETED:
			return "Completed";
		case DELAYED:
			return "Delayed";
		}
		return "Start";
	}

	public static int getStateId(String state)
	{
		if (state.equalsIgnoreCase("Start"))
		{
			return CREATED;
		}
		else if (state.equalsIgnoreCase("Started"))
		{
			return STARTED;
		}
		else if (state.equalsIgnoreCase("Completed"))
		{
			return COMPLETED;
		}
		else if (state.equalsIgnoreCase("Delayed"))
		{
			return DELAYED;
		}
		return CREATED;
	}

	public Quest()
	{
		_name = getClass().getSimpleName();
		_questId = getQuestIntId();
		_party = 0;
		QuestManager.addQuest(this);
	}

	/**
	 * Deprecated.
	 */
	public Quest(boolean party)
	{
		this(party ? 1 : 0);
	}

	/**
	 * 0 - по ластхиту, 1 - случайно по пати, 2 - всей пати.
	 */
	public Quest(int party)
	{
		_name = getClass().getSimpleName();
		_questId = Integer.parseInt(_name.split("_")[1]);
		_party = party;
		QuestManager.addQuest(this);
	}

	public List<QuestNpcLogInfo> getNpcLogList(int cond)
	{
		return _npcLogList.get(cond);
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for Attack Events.<BR>
	 * <BR>
	 *
	 * @param attackIds
	 */
	public void addAttackId(int... attackIds)
	{
		for (int attackId : attackIds)
		{
			addEventId(attackId, QuestEventType.ATTACKED_WITH_QUEST);
		}
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for the specified Event type.<BR>
	 * <BR>
	 *
	 * @param npcId : id of the NPC to register
	 * @param eventType : type of event being registered
	 * @return int : npcId
	 */
	public NpcTemplate addEventId(int npcId, QuestEventType eventType)
	{
		try
		{
			NpcTemplate t = NpcHolder.getInstance().getTemplate(npcId);
			if (t != null)
			{
				t.addQuestEvent(eventType, this);
			}
			return t;
		}
		catch (RuntimeException e)
		{
			_log.error("Error while adding Event Id. Npc ID:" + npcId + " event Type:" + eventType, e);
			return null;
		}
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for Kill Events.<BR>
	 * <BR>
	 *
	 * @param killIds
	 * @return int : killId
	 */
	public void addKillId(int... killIds)
	{
		for (int killid : killIds)
		{
			addEventId(killid, QuestEventType.MOB_KILLED_WITH_QUEST);
		}
	}

	/**
	 * Добавляет нпц масив для слушателя при их убийстве, и обновлении пакетом {@link l2mv.gameserver.network.serverpackets.ExQuestNpcLogList}
	 * @param cond
	 * @param varName
	 * @param killIds
	 */
	public void addKillNpcWithLog(int cond, String varName, int max, int... killIds)
	{
		if (killIds.length == 0)
		{
			throw new IllegalArgumentException("Npc list cant be empty!");
		}

		addKillId(killIds);
		if (_npcLogList.isEmpty())
		{
			_npcLogList = new TIntObjectHashMap<List<QuestNpcLogInfo>>(5);
		}

		List<QuestNpcLogInfo> vars = _npcLogList.get(cond);
		if (vars == null)
		{
			_npcLogList.put(cond, (vars = new ArrayList<QuestNpcLogInfo>(5)));
		}

		vars.add(new QuestNpcLogInfo(killIds, varName, max));
	}

	public boolean updateKill(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		if (player == null)
		{
			return false;
		}
		List<QuestNpcLogInfo> vars = getNpcLogList(st.getCond());
		if (vars == null)
		{
			return false;
		}
		boolean done = true;
		boolean find = false;
		for (QuestNpcLogInfo info : vars)
		{
			int count = st.getInt(info.getVarName());
			if (!find && ArrayUtils.contains(info.getNpcIds(), npc.getNpcId()))
			{
				find = true;
				if (count < info.getMaxCount())
				{
					st.set(info.getVarName(), ++count);
					player.sendPacket(new ExQuestNpcLogList(st));
				}
			}

			if (count != info.getMaxCount())
			{
				done = false;
			}
		}

		return done;
	}

	public void addKillId(Collection<Integer> killIds)
	{
		for (int killid : killIds)
		{
			addKillId(killid);
		}
	}

	/**
	 * Add this quest to the list of quests that the passed npc will respond to
	 * for Skill-Use Events.<BR>
	 * <BR>
	 *
	 * @param npcId : ID of the NPC
	 * @return int : ID of the NPC
	 */
	public NpcTemplate addSkillUseId(int npcId)
	{
		return addEventId(npcId, QuestEventType.MOB_TARGETED_BY_SKILL);
	}

	public void addStartNpc(int... npcIds)
	{
		for (int talkId : npcIds)
		{
			addStartNpc(talkId);
		}
	}

	/**
	 * Add the quest to the NPC's startQuest
	 * Вызывает addTalkId
	 *
	 * @param npcId
	 * @return L2NpcTemplate : Start NPC
	 */
	public NpcTemplate addStartNpc(int npcId)
	{
		addTalkId(npcId);
		return addEventId(npcId, QuestEventType.QUEST_START);
	}

	/**
	 * Add the quest to the NPC's first-talk (default action dialog)
	 *
	 * @param npcIds
	 * @return L2NpcTemplate : Start NPC
	 */
	public void addFirstTalkId(int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(npcId, QuestEventType.NPC_FIRST_TALK);
		}
	}

	/**
	 * Add this quest to the list of quests that the passed npc will respond to
	 * for Talk Events.<BR>
	 * <BR>
	 *
	 * @param talkIds : ID of the NPC
	 * @return int : ID of the NPC
	 */
	public void addTalkId(int... talkIds)
	{
		for (int talkId : talkIds)
		{
			addEventId(talkId, QuestEventType.QUEST_TALK);
		}
	}

	public void addTalkId(Collection<Integer> talkIds)
	{
		for (int talkId : talkIds)
		{
			addTalkId(talkId);
		}
	}

	/**
	 * Возвращает название квеста (Берется с npcstring-*.dat)
	 * state 1 = ""
	 * state 2 = "In Progress"
	 * state 3 = "Done"
	 */
	public String getDescr(Player player)
	{
		if (!isVisible())
		{
			return null;
		}

		QuestState qs = player.getQuestState(getName());
		int state = 2;
		if (qs == null || qs.isCreated() && qs.isNowAvailable())
		{
			state = 1;
		}
		else if (qs.isCompleted() || !qs.isNowAvailable())
		{
			state = 3;
		}

		int fStringId = getQuestIntId();
		if (fStringId >= 10000)
		{
			fStringId -= 5000;
		}
		fStringId = fStringId * 100 + state;
		return HtmlUtils.htmlNpcString(fStringId);
	}

	/**
	 * Return name of the quest
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Return ID of the quest
	 * @return int
	 */
	public int getQuestIntId()
	{
		return _questId;
	}

	/**
	 * Return party state of quest
	 * @return String
	 */
	public int getParty()
	{
		return _party;
	}

	/**
	 * Add a new QuestState to the database and return it.
	 * @param player
	 * @param state
	 * @return QuestState : QuestState created
	 */
	public QuestState newQuestState(Player player, int state)
	{
		QuestState qs = new QuestState(this, player, state);
		Quest.updateQuestInDb(qs);
		return qs;
	}

	public QuestState newQuestStateAndNotSave(Player player, int state)
	{
		return new QuestState(this, player, state);
	}

	public void notifyAttack(NpcInstance npc, QuestState qs)
	{
		String res = null;
		try
		{
			res = onAttack(npc, qs);
		}
		catch (RuntimeException e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(npc, qs.getPlayer(), res);
	}

	public void notifyDeath(Creature killer, Creature victim, QuestState qs)
	{
		String res = null;
		try
		{
			res = onDeath(killer, victim, qs);
		}
		catch (RuntimeException e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(null, qs.getPlayer(), res);
	}

	public void notifyEvent(String event, QuestState qs, NpcInstance npc)
	{
		String res = null;
		try
		{
			res = onEvent(event, qs, npc);
		}
		catch (RuntimeException e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(npc, qs.getPlayer(), res);
	}

	public void notifyKill(NpcInstance npc, QuestState qs)
	{
		String res = null;
		try
		{
			res = onKill(npc, qs);
		}
		catch (RuntimeException e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(npc, qs.getPlayer(), res);
	}

	public void notifyKill(Player target, QuestState qs)
	{
		String res = null;
		try
		{
			res = onKill(target, qs);
		}
		catch (RuntimeException e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
		showResult(null, qs.getPlayer(), res);
	}

	/**
	 * Override the default NPC dialogs when a quest defines this for the given NPC
	 */
	public final boolean notifyFirstTalk(NpcInstance npc, Player player)
	{
		String res = null;
		try
		{
			res = onFirstTalk(npc, player);
		}
		catch (RuntimeException e)
		{
			showError(player, e);
			return true;
		}
		// if the quest returns text to display, display it. Otherwise, use the default npc text.
		return showResult(npc, player, res, true);
	}

	public boolean notifyTalk(NpcInstance npc, QuestState qs)
	{
		String res = null;
		try
		{
			res = onTalk(npc, qs);
		}
		catch (RuntimeException e)
		{
			showError(qs.getPlayer(), e);
			return true;
		}
		return showResult(npc, qs.getPlayer(), res);
	}

	public boolean notifySkillUse(NpcInstance npc, Skill skill, QuestState qs)
	{
		String res = null;
		try
		{
			res = onSkillUse(npc, skill, qs);
		}
		catch (RuntimeException e)
		{
			showError(qs.getPlayer(), e);
			return true;
		}
		return showResult(npc, qs.getPlayer(), res);
	}

	public void notifyCreate(QuestState qs)
	{
		try
		{
			onCreate(qs);
		}
		catch (RuntimeException e)
		{
			showError(qs.getPlayer(), e);
		}
	}

	public void onCreate(QuestState qs)
	{
	}

	public String onAttack(NpcInstance npc, QuestState qs)
	{
		return null;
	}

	public String onDeath(Creature killer, Creature victim, QuestState qs)
	{
		return null;
	}

	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		return null;
	}

	public String onKill(NpcInstance npc, QuestState qs)
	{
		return null;
	}

	public String onKill(Player killed, QuestState st)
	{
		return null;
	}

	public String onFirstTalk(NpcInstance npc, Player player)
	{
		return null;
	}

	public String onTalk(NpcInstance npc, QuestState qs)
	{
		return null;
	}

	public String onSkillUse(NpcInstance npc, Skill skill, QuestState qs)
	{
		return null;
	}

	public void onOlympiadEnd(OlympiadGame og, QuestState qs)
	{
	}

	public void onAbort(QuestState qs)
	{
	}

	public boolean canAbortByPacket()
	{
		return true;
	}

	/**
	 * Show message error to player who has an access level greater than 0
	 * @param player : L2Player
	 * @param t : Throwable
	 */
	private void showError(Player player, Throwable t)
	{
		_log.error("Quest Error!", t);
		if (player != null && player.isGM())
		{
			String res = "<html><body><title>Script error</title>" + LogUtils.dumpStack(t).replace("\n", "<br>") + "</body></html>";
			showResult(null, player, res);
		}
	}

	protected void showHtmlFile(Player player, String fileName, boolean showQuestInfo)
	{
		showHtmlFile(player, fileName, showQuestInfo, ArrayUtils.EMPTY_OBJECT_ARRAY);
	}

	protected void showHtmlFile(Player player, String fileName, boolean showQuestInfo, Object... arg)
	{
		if (player == null)
		{
			return;
		}

		GameObject target = player.getTarget();
		NpcHtmlMessage npcReply = showQuestInfo ? new ExNpcQuestHtmlMessage(target == null ? 5 : target.getObjectId(), getQuestIntId()) : new NpcHtmlMessage(target == null ? 5 : target.getObjectId());
		npcReply.setFile("quests/" + getClass().getSimpleName() + "/" + fileName);

		if (arg.length % 2 == 0)
		{
			for (int i = 0; i < arg.length; i += 2)
			{
				npcReply.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));
			}
		}

		player.sendPacket(npcReply);
	}

	protected void showSimpleHtmFile(Player player, String fileName)
	{
		if (player == null)
		{
			return;
		}

		NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
		npcReply.setFile(fileName);
		player.sendPacket(npcReply);
	}

	/**
	 * Show a message to player.<BR><BR>
	 * <U><I>Concept : </I></U><BR>
	 * 3 cases are managed according to the value of the parameter "res" :<BR>
	 * <LI><U>"res" ends with string ".html" :</U> an HTML is opened in order to be shown in a dialog box</LI>
	 * <LI><U>"res" starts with tag "html" :</U> the message hold in "res" is shown in a dialog box</LI>
	 * <LI><U>"res" is null :</U> do not show any message</LI>
	 * <LI><U>"res" is empty string :</U> show default message</LI>
	 * <LI><U>otherwise :</U> the message hold in "res" is shown in chat box</LI>
	 * @param npc
	 * @param player
	 * @param res : String pointing out the message to show at the player
	 */
	private boolean showResult(NpcInstance npc, Player player, String res)
	{
		return showResult(npc, player, res, false);
	}

	private boolean showResult(NpcInstance npc, Player player, String res, boolean isFirstTalk)
	{
		boolean showQuestInfo = showQuestInfo(player);
		if (isFirstTalk)
		{
			showQuestInfo = false;
		}
		if (res == null) // do not show message
		{
			return true;
		}
		if (res.isEmpty()) // show default npc message
		{
			return false;
		}
		if (res.startsWith("no_quest") || res.equalsIgnoreCase("noquest") || res.equalsIgnoreCase("no-quest"))
		{
			showSimpleHtmFile(player, "no-quest.htm");
		}
		else if (res.equalsIgnoreCase("completed"))
		{
			showSimpleHtmFile(player, "completed-quest.htm");
		}
		else if (res.endsWith(".htm"))
		{
			showHtmlFile(player, res, showQuestInfo);
		}
		else
		{
			NpcHtmlMessage npcReply = showQuestInfo ? new ExNpcQuestHtmlMessage(npc == null ? 5 : npc.getObjectId(), getQuestIntId()) : new NpcHtmlMessage(npc == null ? 5 : npc.getObjectId());
			npcReply.setHtml(res);
			player.sendPacket(npcReply);
		}
		return true;
	}

	// Проверяем, показывать ли информацию о квесте в диалоге.
	private boolean showQuestInfo(Player player)
	{
		QuestState qs = player.getQuestState(getName());
		if ((qs != null && qs.getState() != CREATED) || !isVisible())
		{
			return false;
		}

		return true;
	}

	// Останавливаем и сохраняем таймеры (при выходе из игры)
	void pauseQuestTimers(QuestState qs)
	{
		if (qs.getTimers().isEmpty())
		{
			return;
		}

		for (QuestTimer timer : qs.getTimers().values())
		{
			timer.setQuestState(null);
			timer.pause();
		}

		_pausedQuestTimers.put(qs.getPlayer().getObjectId(), qs.getTimers());
	}

	// Восстанавливаем таймеры (при входе в игру)
	void resumeQuestTimers(QuestState qs)
	{
		Map<String, QuestTimer> timers = _pausedQuestTimers.remove(qs.getPlayer().getObjectId());
		if (timers == null)
		{
			return;
		}

		qs.getTimers().putAll(timers);

		for (QuestTimer timer : qs.getTimers().values())
		{
			timer.setQuestState(qs);
			timer.start();
		}
	}

	protected String str(long i)
	{
		return String.valueOf(i);
	}

	// =========================================================
	// QUEST SPAWNS
	// =========================================================

	public class DeSpawnScheduleTimerTask extends RunnableImpl
	{
		NpcInstance _npc = null;

		public DeSpawnScheduleTimerTask(NpcInstance npc)
		{
			_npc = npc;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_npc != null)
			{
				if (_npc.getSpawn() != null)
				{
					_npc.getSpawn().deleteAll();
				}
				else
				{
					_npc.deleteMe();
				}
			}
		}
	}

	public NpcInstance addSpawn(int npcId, int x, int y, int z, int heading, int randomOffset, int despawnDelay)
	{
		return addSpawn(npcId, new Location(x, y, z, heading), randomOffset, despawnDelay);
	}

	public NpcInstance addSpawn(int npcId, Location loc, int randomOffset, int despawnDelay)
	{
		NpcInstance result = Functions.spawn(randomOffset > 50 ? Location.findPointToStay(loc, 0, randomOffset, ReflectionManager.DEFAULT.getGeoIndex()) : loc, npcId);
		if (despawnDelay > 0 && result != null)
		{
			ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(result), despawnDelay);
		}
		return result;
	}

	/**
	 * Добавляет спаун с числовым значением разброса - от 50 до randomOffset.
	 * Если randomOffset указан мене 50, то координаты не меняются.
	 */
	public static NpcInstance addSpawnToInstance(int npcId, int x, int y, int z, int heading, int randomOffset, int refId)
	{
		return addSpawnToInstance(npcId, new Location(x, y, z, heading), randomOffset, refId);
	}

	public static NpcInstance addSpawnToInstance(int npcId, Location loc, int randomOffset, int refId)
	{
		try
		{
			NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
			if (template != null)
			{
				NpcInstance npc = NpcHolder.getInstance().getTemplate(npcId).getNewInstance();
				npc.setReflection(refId);
				npc.setSpawnedLoc(randomOffset > 50 ? Location.findPointToStay(loc, 50, randomOffset, npc.getGeoIndex()) : loc);
				npc.spawnMe(npc.getSpawnedLoc());
				return npc;
			}
		}
		catch (RuntimeException e)
		{
			_log.warn("Could not spawn Npc " + npcId, e);
		}
		return null;
	}

	public boolean isVisible()
	{
		return true;
	}

	/**
	 * Gets the reset hour for a daily quest.
	 * @return the reset hour
	 */
	public int getResetHour()
	{
		return QuestState.RESTART_HOUR;
	}

	/**
	 * Gets the reset minutes for a daily quest.
	 * @return the reset minutes
	 */
	public int getResetMinutes()
	{
		return QuestState.RESTART_MINUTES;
	}
}