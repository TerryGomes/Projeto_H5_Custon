package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _605_AllianceWithKetraOrcs extends Quest implements ScriptFile
{
	// ketra mobs
	private static final int[] KETRA_NPC_LIST = new int[19];

	// items
	private static final int MARK_OF_KETRA_ALLIANCE1 = 7211;
	private static final int MARK_OF_KETRA_ALLIANCE2 = 7212;
	private static final int MARK_OF_KETRA_ALLIANCE3 = 7213;
	private static final int MARK_OF_KETRA_ALLIANCE4 = 7214;
	private static final int MARK_OF_KETRA_ALLIANCE5 = 7215;
	private static final int VB_SOLDIER = 7216;
	private static final int VB_CAPTAIN = 7217;
	private static final int VB_GENERAL = 7218;
	private static final int TOTEM_OF_VALOR = 7219;
	private static final int TOTEM_OF_WISDOM = 7220;

	// hunt for soldier
	private static final int RECRUIT = 21350;
	private static final int FOOTMAN = 21351;
	private static final int SCOUT = 21353;
	private static final int HUNTER = 21354;
	private static final int SHAMAN = 21355;

	// hunt for captain
	private static final int PRIEST = 21357;
	private static final int WARRIOR = 21358;
	private static final int MEDIUM = 21360;
	private static final int MAGUS = 21361;
	private static final int OFFICIER = 21362;
	private static final int COMMANDER = 21369;
	private static final int ELITE_GUARD = 21370;

	// hunt for general
	private static final int GREAT_MAGUS = 21365;
	private static final int GENERAL = 21366;
	private static final int GREAT_SEER = 21368;
	private static final int VARKA_PROPHET = 21373;
	private static final int DISCIPLE_OF_PROPHET = 21375;
	private static final int PROPHET_GUARDS = 21374;
	private static final int HEAD_MAGUS = 21371;
	private static final int HEAD_GUARDS = 21372;
	private static final int Wahkan = 31371;

	public _605_AllianceWithKetraOrcs()
	{
		super(true);

		addStartNpc(Wahkan);

		// ketra mobs
		KETRA_NPC_LIST[0] = 21324;
		KETRA_NPC_LIST[1] = 21325;
		KETRA_NPC_LIST[2] = 21327;
		KETRA_NPC_LIST[3] = 21328;
		KETRA_NPC_LIST[4] = 21329;
		KETRA_NPC_LIST[5] = 21331;
		KETRA_NPC_LIST[6] = 21332;
		KETRA_NPC_LIST[7] = 21334;
		KETRA_NPC_LIST[8] = 21335;
		KETRA_NPC_LIST[9] = 21336;
		KETRA_NPC_LIST[10] = 21338;
		KETRA_NPC_LIST[11] = 21339;
		KETRA_NPC_LIST[12] = 21340;
		KETRA_NPC_LIST[13] = 21342;
		KETRA_NPC_LIST[14] = 21343;
		KETRA_NPC_LIST[15] = 21344;
		KETRA_NPC_LIST[16] = 21345;
		KETRA_NPC_LIST[17] = 21346;
		KETRA_NPC_LIST[18] = 21347;
		addKillId(KETRA_NPC_LIST);

		// hunt for soldier
		addKillId(RECRUIT);
		addKillId(FOOTMAN);
		addKillId(SCOUT);
		addKillId(HUNTER);
		addKillId(SHAMAN);

		// hunt for captain
		addKillId(PRIEST);
		addKillId(WARRIOR);
		addKillId(MEDIUM);
		addKillId(MAGUS);
		addKillId(OFFICIER);
		addKillId(COMMANDER);
		addKillId(ELITE_GUARD);

		// hunt for general
		addKillId(GREAT_MAGUS);
		addKillId(GENERAL);
		addKillId(GREAT_SEER);
		addKillId(VARKA_PROPHET);
		addKillId(DISCIPLE_OF_PROPHET);
		addKillId(PROPHET_GUARDS);
		addKillId(HEAD_MAGUS);
		addKillId(HEAD_GUARDS);

		addQuestItem(VB_SOLDIER);
		addQuestItem(VB_CAPTAIN);
		addQuestItem(VB_GENERAL);
	}

	public boolean isKetraNpc(int npc)
	{
		for (int i : KETRA_NPC_LIST)
		{
			if (npc == i)
			{
				return true;
			}
		}
		return false;
	}

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

	@Override
	public void onAbort(QuestState st)
	{
		takeAllMarks(st);
		st.setCond(0);
		st.getPlayer().updateKetraVarka();
		st.playSound(SOUND_MIDDLE);
	}

	private static void takeAllMarks(QuestState st)
	{
		st.takeItems(MARK_OF_KETRA_ALLIANCE1, -1);
		st.takeItems(MARK_OF_KETRA_ALLIANCE2, -1);
		st.takeItems(MARK_OF_KETRA_ALLIANCE3, -1);
		st.takeItems(MARK_OF_KETRA_ALLIANCE4, -1);
		st.takeItems(MARK_OF_KETRA_ALLIANCE5, -1);
	}

	private void checkMarks(QuestState st)
	{
		if (st.getCond() == 0)
		{
			return;
		}
		if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE5) > 0)
		{
			st.setCond(6);
		}
		else if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE4) > 0)
		{
			st.setCond(5);
		}
		else if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE3) > 0)
		{
			st.setCond(4);
		}
		else if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE2) > 0)
		{
			st.setCond(3);
		}
		else if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE1) > 0)
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
		if ((soilder_count > 0 && st.getQuestItemsCount(VB_SOLDIER) < soilder_count) || (capitan_count > 0 && st.getQuestItemsCount(VB_CAPTAIN) < capitan_count))
		{
			return false;
		}
		if ((general_count > 0 && st.getQuestItemsCount(VB_GENERAL) < general_count) || (other_item > 0 && st.getQuestItemsCount(other_item) < 1))
		{
			return false;
		}

		if (take)
		{
			if (soilder_count > 0)
			{
				st.takeItems(VB_SOLDIER, soilder_count);
			}
			if (capitan_count > 0)
			{
				st.takeItems(VB_CAPTAIN, capitan_count);
			}
			if (general_count > 0)
			{
				st.takeItems(VB_GENERAL, general_count);
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
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("first-2.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			return event;
		}

		checkMarks(st);
		int cond = st.getCond();

		if (event.equalsIgnoreCase("first-have-2.htm") && cond == 1 && CheckNextLevel(st, 100, 0, 0, 0, true))
		{
			st.giveItems(MARK_OF_KETRA_ALLIANCE1, 1);
			st.setCond(2);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("second-have-2.htm") && cond == 2 && CheckNextLevel(st, 200, 100, 0, 0, true))
		{
			st.giveItems(MARK_OF_KETRA_ALLIANCE2, 1);
			st.setCond(3);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("third-have-2.htm") && cond == 3 && CheckNextLevel(st, 300, 200, 100, 0, true))
		{
			st.giveItems(MARK_OF_KETRA_ALLIANCE3, 1);
			st.setCond(4);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("fourth-have-2.htm") && cond == 4 && CheckNextLevel(st, 300, 300, 200, TOTEM_OF_VALOR, true))
		{
			st.giveItems(MARK_OF_KETRA_ALLIANCE4, 1);
			st.setCond(5);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("fifth-have-2.htm") && cond == 5 && CheckNextLevel(st, 400, 400, 200, TOTEM_OF_WISDOM, true))
		{
			st.giveItems(MARK_OF_KETRA_ALLIANCE5, 1);
			st.setCond(6);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("quit-2.htm"))
		{
			takeAllMarks(st);
			st.setCond(0);
			st.getPlayer().updateKetraVarka();
			st.playSound(SOUND_MIDDLE);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (st.getPlayer().getVarka() > 0)
		{
			st.exitCurrentQuest(true);
			return "isvarka.htm";
		}
		checkMarks(st);
		if (st.getState() == CREATED)
		{
			st.setCond(0);
		}
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == 31371)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() < 74)
				{
					st.exitCurrentQuest(true);
					return "no-level.htm";
				}
				return "first.htm";
			case 1:
				return CheckNextLevel(st, 100, 0, 0, 0, false) ? "first-have.htm" : "first-havenot.htm";
			case 2:
				return CheckNextLevel(st, 200, 100, 0, 0, false) ? "second-have.htm" : "second.htm";
			case 3:
				return CheckNextLevel(st, 300, 200, 100, 0, false) ? "third-have.htm" : "third.htm";
			case 4:
				return CheckNextLevel(st, 300, 300, 200, TOTEM_OF_VALOR, false) ? "fourth-have.htm" : "fourth.htm";
			case 5:
				return CheckNextLevel(st, 400, 400, 200, TOTEM_OF_WISDOM, false) ? "fifth-have.htm" : "fifth.htm";
			case 6:
				return "high.htm";
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
		if (isKetraNpc(npcId))
		{
			if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE5) > 0)
			{
				takeAllMarks(st);
				st.giveItems(MARK_OF_KETRA_ALLIANCE4, 1);
				st.getPlayer().updateKetraVarka();
			}
			else if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE4) > 0)
			{
				takeAllMarks(st);
				st.giveItems(MARK_OF_KETRA_ALLIANCE3, 1);
				st.getPlayer().updateKetraVarka();
			}
			else if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE3) > 0)
			{
				takeAllMarks(st);
				st.giveItems(MARK_OF_KETRA_ALLIANCE2, 1);
				st.getPlayer().updateKetraVarka();
			}
			else if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE2) > 0)
			{
				takeAllMarks(st);
				st.giveItems(MARK_OF_KETRA_ALLIANCE1, 1);
				st.getPlayer().updateKetraVarka();
			}
			else if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE1) > 0)
			{
				takeAllMarks(st);
				st.getPlayer().updateKetraVarka();
			}
			else if (st.getPlayer().getKetra() > 0)
			{
				st.getPlayer().updateKetraVarka();
				st.exitCurrentQuest(true);
				return "quit-2.htm";
			}
		}

		if (st.getQuestItemsCount(MARK_OF_KETRA_ALLIANCE5) > 0)
		{
			return null;
		}

		int cond = st.getCond();
		switch (npcId)
		{
		case RECRUIT:
		case FOOTMAN:
		case SCOUT:
		case HUNTER:
		case SHAMAN:
			if (cond > 0)
			{
				st.rollAndGive(VB_SOLDIER, 1, 60);
			}
			break;
		case PRIEST:
		case WARRIOR:
		case MEDIUM:
		case MAGUS:
		case OFFICIER:
		case COMMANDER:
		case ELITE_GUARD:
			if (cond > 1)
			{
				st.rollAndGive(VB_CAPTAIN, 1, 70);
			}
			break;
		case GREAT_MAGUS:
		case GENERAL:
		case GREAT_SEER:
		case VARKA_PROPHET:
		case DISCIPLE_OF_PROPHET:
		case PROPHET_GUARDS:
		case HEAD_MAGUS:
		case HEAD_GUARDS:
			if (cond > 2)
			{
				st.rollAndGive(VB_GENERAL, 1, 80);
			}
			break;
		default:
			break;
		}
		return null;
	}
}