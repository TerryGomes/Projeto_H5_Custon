package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _611_AllianceWithVarkaSilenos extends Quest implements ScriptFile
{
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	// Varka mobs
	private final int[] VARKA_NPC_LIST = new int[20];

	// Items
	private static final int MARK_OF_VARKA_ALLIANCE1 = 7221;
	private static final int MARK_OF_VARKA_ALLIANCE2 = 7222;
	private static final int MARK_OF_VARKA_ALLIANCE3 = 7223;
	private static final int MARK_OF_VARKA_ALLIANCE4 = 7224;
	private static final int MARK_OF_VARKA_ALLIANCE5 = 7225;
	private static final int KB_SOLDIER = 7226;
	private static final int KB_CAPTAIN = 7227;
	private static final int KB_GENERAL = 7228;
	private static final int TOTEM_OF_VALOR = 7229;
	private static final int TOTEM_OF_WISDOM = 7230;

	// hunt for soldier
	private static final int RAIDER = 21327;
	private static final int FOOTMAN = 21324;
	private static final int SCOUT = 21328;
	private static final int WAR_HOUND = 21325;
	private static final int SHAMAN = 21329;

	// hunt for captain
	private static final int SEER = 21338;
	private static final int WARRIOR = 21331;
	private static final int LIEUTENANT = 21332;
	private static final int ELITE_SOLDIER = 21335;
	private static final int MEDIUM = 21334;
	private static final int COMMAND = 21343;
	private static final int ELITE_GUARD = 21344;
	private static final int WHITE_CAPTAIN = 21336;

	// hunt for general
	private static final int BATTALION_COMMANDER_SOLDIER = 21340;
	private static final int GENERAL = 21339;
	private static final int GREAT_SEER = 21342;
	private static final int KETRA_PROPHET = 21347;
	private static final int DISCIPLE_OF_PROPHET = 21375;
	private static final int PROPHET_GUARDS = 21348;
	private static final int PROPHET_AIDE = 21349;
	private static final int HEAD_SHAMAN = 21345;
	private static final int HEAD_GUARDS = 21346;

	private static void takeAllMarks(QuestState st)
	{
		st.takeItems(MARK_OF_VARKA_ALLIANCE1, -1);
		st.takeItems(MARK_OF_VARKA_ALLIANCE2, -1);
		st.takeItems(MARK_OF_VARKA_ALLIANCE3, -1);
		st.takeItems(MARK_OF_VARKA_ALLIANCE4, -1);
		st.takeItems(MARK_OF_VARKA_ALLIANCE5, -1);
	}

	public _611_AllianceWithVarkaSilenos()
	{
		super(true);

		addStartNpc(31378);

		VARKA_NPC_LIST[0] = 21350;
		VARKA_NPC_LIST[1] = 21351;
		VARKA_NPC_LIST[2] = 21353;
		VARKA_NPC_LIST[3] = 21354;
		VARKA_NPC_LIST[4] = 21355;
		VARKA_NPC_LIST[5] = 21357;
		VARKA_NPC_LIST[6] = 21358;
		VARKA_NPC_LIST[7] = 21360;
		VARKA_NPC_LIST[8] = 21361;
		VARKA_NPC_LIST[9] = 21362;
		VARKA_NPC_LIST[10] = 21364;
		VARKA_NPC_LIST[11] = 21365;
		VARKA_NPC_LIST[12] = 21366;
		VARKA_NPC_LIST[13] = 21368;
		VARKA_NPC_LIST[14] = 21369;
		VARKA_NPC_LIST[15] = 21370;
		VARKA_NPC_LIST[16] = 21371;
		VARKA_NPC_LIST[17] = 21372;
		VARKA_NPC_LIST[18] = 21373;
		VARKA_NPC_LIST[19] = 21374;

		for (int npcId : VARKA_NPC_LIST)
		{
			addKillId(npcId);
		}

		// hunt for soldier
		addKillId(RAIDER);
		addKillId(FOOTMAN);
		addKillId(SCOUT);
		addKillId(WAR_HOUND);
		addKillId(SHAMAN);

		// hunt for captain
		addKillId(SEER);
		addKillId(WARRIOR);
		addKillId(LIEUTENANT);
		addKillId(ELITE_SOLDIER);
		addKillId(MEDIUM);
		addKillId(COMMAND);
		addKillId(ELITE_GUARD);
		addKillId(WHITE_CAPTAIN);

		// hunt for general
		addKillId(BATTALION_COMMANDER_SOLDIER);
		addKillId(GENERAL);
		addKillId(GREAT_SEER);
		addKillId(KETRA_PROPHET);
		addKillId(PROPHET_AIDE);
		addKillId(PROPHET_GUARDS);
		addKillId(HEAD_SHAMAN);
		addKillId(HEAD_GUARDS);

		addQuestItem(KB_SOLDIER);
		addQuestItem(KB_CAPTAIN);
		addQuestItem(KB_GENERAL);
	}

	public boolean isVarkaNpc(int npc)
	{
		for (int i : VARKA_NPC_LIST)
		{
			if (npc == i)
			{
				return true;
			}
		}
		return false;
	}

	private static void checkMarks(QuestState st)
	{
		if (st.getCond() == 0)
		{
			return;
		}
		if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE5) > 0)
		{
			st.setCond(6);
		}
		else if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE4) > 0)
		{
			st.setCond(5);
		}
		else if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE3) > 0)
		{
			st.setCond(4);
		}
		else if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE2) > 0)
		{
			st.setCond(3);
		}
		else if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE1) > 0)
		{
			st.setCond(2);
		}
		else
		{
			st.setCond(1);
		}
	}

	private static boolean CheckNextLevel(QuestState st, int soilder_count, int capitan_count, int general_count, int other_item, boolean take)
	{
		if ((soilder_count > 0 && st.getQuestItemsCount(KB_SOLDIER) < soilder_count) || (capitan_count > 0 && st.getQuestItemsCount(KB_CAPTAIN) < capitan_count))
		{
			return false;
		}
		if ((general_count > 0 && st.getQuestItemsCount(KB_GENERAL) < general_count) || (other_item > 0 && st.getQuestItemsCount(other_item) < 1))
		{
			return false;
		}

		if (take)
		{
			if (soilder_count > 0)
			{
				st.takeItems(KB_SOLDIER, soilder_count);
			}
			if (capitan_count > 0)
			{
				st.takeItems(KB_CAPTAIN, capitan_count);
			}
			if (general_count > 0)
			{
				st.takeItems(KB_GENERAL, general_count);
			}
			if (other_item > 0)
			{
				st.takeItems(other_item, 1);
			}
			takeAllMarks(st);
		}
		return true;
	}

	@Override
	public void onAbort(QuestState st)
	{
		takeAllMarks(st);
		st.setCond(0);
		st.getPlayer().updateKetraVarka();
		st.playSound(SOUND_MIDDLE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("herald_naran_q0611_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return event;
		}

		checkMarks(st);
		int cond = st.getCond();

		if (event.equalsIgnoreCase("herald_naran_q0611_12.htm") && cond == 1 && CheckNextLevel(st, 100, 0, 0, 0, true))
		{
			st.giveItems(MARK_OF_VARKA_ALLIANCE1, 1);
			st.setCond(2);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("herald_naran_q0611_15.htm") && cond == 2 && CheckNextLevel(st, 200, 100, 0, 0, true))
		{
			st.giveItems(MARK_OF_VARKA_ALLIANCE2, 1);
			st.setCond(3);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("herald_naran_q0611_18.htm") && cond == 3 && CheckNextLevel(st, 300, 200, 100, 0, true))
		{
			st.giveItems(MARK_OF_VARKA_ALLIANCE3, 1);
			st.setCond(4);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("herald_naran_q0611_21.htm") && cond == 4 && CheckNextLevel(st, 300, 300, 200, TOTEM_OF_VALOR, true))
		{
			st.giveItems(MARK_OF_VARKA_ALLIANCE4, 1);
			st.setCond(5);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("herald_naran_q0611_23.htm") && cond == 5 && CheckNextLevel(st, 400, 400, 200, TOTEM_OF_WISDOM, true))
		{
			st.giveItems(MARK_OF_VARKA_ALLIANCE5, 1);
			st.setCond(6);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("herald_naran_q0611_26.htm"))
		{
			takeAllMarks(st);
			st.setCond(0);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (st.getPlayer().getKetra() > 0)
		{
			st.exitCurrentQuest(true);
			return "herald_naran_q0611_02.htm";
		}
		int npcId = npc.getNpcId();
		checkMarks(st);
		if (st.getState() == CREATED)
		{
			st.setCond(0);
		}
		int cond = st.getCond();
		if (npcId == 31378)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() < 74)
				{
					st.exitCurrentQuest(true);
					return "herald_naran_q0611_03.htm";
				}
				return "herald_naran_q0611_01.htm";
			case 1:
				return CheckNextLevel(st, 100, 0, 0, 0, false) ? "herald_naran_q0611_11.htm" : "herald_naran_q0611_10.htm";
			case 2:
				return CheckNextLevel(st, 200, 100, 0, 0, false) ? "herald_naran_q0611_14.htm" : "herald_naran_q0611_13.htm";
			case 3:
				return CheckNextLevel(st, 300, 200, 100, 0, false) ? "herald_naran_q0611_17.htm" : "herald_naran_q0611_16.htm";
			case 4:
				return CheckNextLevel(st, 300, 300, 200, TOTEM_OF_VALOR, false) ? "herald_naran_q0611_20.htm" : "herald_naran_q0611_19.htm";
			case 5:
				return CheckNextLevel(st, 400, 400, 200, TOTEM_OF_WISDOM, false) ? "herald_naran_q0611_27.htm" : "herald_naran_q0611_22.htm";
			case 6:
				return "herald_naran_q0611_24.htm";
			default:
				break;
			}
		}
		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if (isVarkaNpc(npcId))
		{
			if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE5) > 0)
			{
				takeAllMarks(st);
				st.giveItems(MARK_OF_VARKA_ALLIANCE4, 1);
				st.getPlayer().updateKetraVarka();
				checkMarks(st);
			}
			else if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE4) > 0)
			{
				takeAllMarks(st);
				st.giveItems(MARK_OF_VARKA_ALLIANCE3, 1);
				st.getPlayer().updateKetraVarka();
				checkMarks(st);
			}
			else if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE3) > 0)
			{
				takeAllMarks(st);
				st.giveItems(MARK_OF_VARKA_ALLIANCE2, 1);
				st.getPlayer().updateKetraVarka();
				checkMarks(st);
			}
			else if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE2) > 0)
			{
				takeAllMarks(st);
				st.giveItems(MARK_OF_VARKA_ALLIANCE1, 1);
				st.getPlayer().updateKetraVarka();
				checkMarks(st);
			}
			else if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE1) > 0)
			{
				takeAllMarks(st);
				st.getPlayer().updateKetraVarka();
				checkMarks(st);
			}
			else if (st.getPlayer().getVarka() > 0)
			{
				st.getPlayer().updateKetraVarka();
				st.exitCurrentQuest(true);
				return "herald_naran_q0611_26.htm";
			}
		}

		if (st.getQuestItemsCount(MARK_OF_VARKA_ALLIANCE5) > 0)
		{
			return null;
		}

		int cond = st.getCond();
		switch (npcId)
		{
		case RAIDER:
		case FOOTMAN:
		case SCOUT:
		case WAR_HOUND:
		case SHAMAN:
			if (cond > 0)
			{
				st.rollAndGive(KB_SOLDIER, 1, 60);
			}
			break;
		case SEER:
		case WARRIOR:
		case LIEUTENANT:
		case ELITE_SOLDIER:
		case MEDIUM:
		case COMMAND:
		case ELITE_GUARD:
		case WHITE_CAPTAIN:
			if (cond > 1)
			{
				st.rollAndGive(KB_CAPTAIN, 1, 70);
			}
			break;
		case BATTALION_COMMANDER_SOLDIER:
		case GENERAL:
		case GREAT_SEER:
		case KETRA_PROPHET:
		case DISCIPLE_OF_PROPHET:
		case PROPHET_GUARDS:
		case HEAD_SHAMAN:
		case HEAD_GUARDS:
			if (cond > 2)
			{
				st.rollAndGive(KB_GENERAL, 1, 80);
			}
			break;
		default:
			break;
		}
		return null;
	}
}