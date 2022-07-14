package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _405_PathToCleric extends Quest implements ScriptFile
{
	// npc
	public final int GALLINT = 30017;
	public final int ZIGAUNT = 30022;
	public final int VIVYAN = 30030;
	public final int SIMPLON = 30253;
	public final int PRAGA = 30333;
	public final int LIONEL = 30408;
	// mobs
	public final int RUIN_ZOMBIE = 20026;
	public final int RUIN_ZOMBIE_LEADER = 20029;
	// items
	public final int LETTER_OF_ORDER1 = 1191;
	public final int LETTER_OF_ORDER2 = 1192;
	public final int BOOK_OF_LEMONIELL = 1193;
	public final int BOOK_OF_VIVI = 1194;
	public final int BOOK_OF_SIMLON = 1195;
	public final int BOOK_OF_PRAGA = 1196;
	public final int CERTIFICATE_OF_GALLINT = 1197;
	public final int PENDANT_OF_MOTHER = 1198;
	public final int NECKLACE_OF_MOTHER = 1199;
	public final int LEMONIELLS_COVENANT = 1200;
	public final int MARK_OF_FAITH = 1201;

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

	public _405_PathToCleric()
	{
		super(false);

		addStartNpc(ZIGAUNT);

		addTalkId(GALLINT);
		addTalkId(VIVYAN);
		addTalkId(SIMPLON);
		addTalkId(PRAGA);
		addTalkId(LIONEL);

		addKillId(RUIN_ZOMBIE);
		addKillId(RUIN_ZOMBIE_LEADER);

		addQuestItem(new int[]
		{
			LEMONIELLS_COVENANT,
			LETTER_OF_ORDER2,
			BOOK_OF_PRAGA,
			BOOK_OF_VIVI,
			BOOK_OF_SIMLON,
			LETTER_OF_ORDER1,
			NECKLACE_OF_MOTHER,
			PENDANT_OF_MOTHER,
			CERTIFICATE_OF_GALLINT,
			BOOK_OF_LEMONIELL
		});
	}

	public void checkBooks(QuestState st)
	{
		if (st.getQuestItemsCount(BOOK_OF_PRAGA) + st.getQuestItemsCount(BOOK_OF_VIVI) + st.getQuestItemsCount(BOOK_OF_SIMLON) >= 5)
		{
			st.setCond(2);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("1"))
		{
			if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().getId() == 0x0a && st.getQuestItemsCount(MARK_OF_FAITH) < 1)
			{
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
				st.giveItems(LETTER_OF_ORDER1, 1);
				htmltext = "gigon_q0405_05.htm";
			}
			else if (st.getPlayer().getClassId().getId() != 0x0a)
			{
				if (st.getPlayer().getClassId().getId() == 0x0f)
				{
					htmltext = "gigon_q0405_02a.htm";
				}
				else
				{
					htmltext = "gigon_q0405_02.htm";
				}
			}
			else if (st.getPlayer().getLevel() < 18 && st.getPlayer().getClassId().getId() == 0x0a)
			{
				htmltext = "gigon_q0405_03.htm";
			}
			else if (st.getPlayer().getLevel() >= 18 && st.getPlayer().getClassId().getId() == 0x0a && st.getQuestItemsCount(MARK_OF_FAITH) > 0)
			{
				htmltext = "gigon_q0405_04.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == ZIGAUNT)
		{
			if (st.getQuestItemsCount(MARK_OF_FAITH) > 0)
			{
				htmltext = "gigon_q0405_04.htm";
				st.exitCurrentQuest(true);
			}
			if (cond < 1 && st.getQuestItemsCount(MARK_OF_FAITH) < 1)
			{
				htmltext = "gigon_q0405_01.htm";
			}
			else if (cond == 1 | cond == 2 && st.getQuestItemsCount(LETTER_OF_ORDER1) > 0)
			{
				if (st.getQuestItemsCount(BOOK_OF_VIVI) > 0 && st.getQuestItemsCount(BOOK_OF_SIMLON) > 2 && st.getQuestItemsCount(BOOK_OF_PRAGA) > 0)
				{
					htmltext = "gigon_q0405_08.htm";
					st.takeItems(BOOK_OF_PRAGA, -1);
					st.takeItems(BOOK_OF_VIVI, -1);
					st.takeItems(BOOK_OF_SIMLON, -1);
					st.takeItems(LETTER_OF_ORDER1, -1);
					st.giveItems(LETTER_OF_ORDER2, 1);
					st.setCond(3);
				}
				else
				{
					htmltext = "gigon_q0405_06.htm";
				}
			}
			else if (cond < 6 && st.getQuestItemsCount(LETTER_OF_ORDER2) > 0)
			{
				htmltext = "gigon_q0405_07.htm";
			}
			else if (cond == 6 && st.getQuestItemsCount(LETTER_OF_ORDER2) > 0 && st.getQuestItemsCount(LEMONIELLS_COVENANT) > 0)
			{
				htmltext = "gigon_q0405_09.htm";
				st.takeItems(LEMONIELLS_COVENANT, -1);
				st.takeItems(LETTER_OF_ORDER2, -1);
				if (!st.getPlayer().getVarB("q405"))
				{
					st.getPlayer().setVar("q405", "1", -1);
				}
				st.exitCurrentQuest(true);
				if (st.getPlayer().getClassId().getLevel() == 1)
				{
					st.giveItems(MARK_OF_FAITH, 1);
					if (!st.getPlayer().getVarB("prof1"))
					{
						st.getPlayer().setVar("prof1", "1", -1);
						st.addExpAndSp(295862, 17964);
						// FIXME [G1ta0] дать адены, только если первый чар на акке
						st.giveItems(ADENA_ID, 81900);
					}
				}
				st.playSound(SOUND_FINISH);
			}
		}
		else if (npcId == SIMPLON && cond == 1 && st.getQuestItemsCount(LETTER_OF_ORDER1) > 0)
		{
			if (st.getQuestItemsCount(BOOK_OF_SIMLON) < 1)
			{
				htmltext = "trader_simplon_q0405_01.htm";
				st.giveItems(BOOK_OF_SIMLON, 3);
				checkBooks(st);
			}
			else if (st.getQuestItemsCount(BOOK_OF_SIMLON) > 2)
			{
				htmltext = "trader_simplon_q0405_02.htm";
			}
		}
		else if (npcId == VIVYAN && cond == 1 && st.getQuestItemsCount(LETTER_OF_ORDER1) > 0)
		{
			if (st.getQuestItemsCount(BOOK_OF_VIVI) < 1)
			{
				htmltext = "vivi_q0405_01.htm";
				st.giveItems(BOOK_OF_VIVI, 1);
				checkBooks(st);
			}
			else if (st.getQuestItemsCount(BOOK_OF_VIVI) > 0)
			{
				htmltext = "vivi_q0405_02.htm";
			}
		}
		else if (npcId == PRAGA && cond == 1 && st.getQuestItemsCount(LETTER_OF_ORDER1) > 0)
		{
			if (st.getQuestItemsCount(BOOK_OF_PRAGA) < 1 && st.getQuestItemsCount(NECKLACE_OF_MOTHER) < 1)
			{
				htmltext = "guard_praga_q0405_01.htm";
				st.giveItems(NECKLACE_OF_MOTHER, 1);
			}
			else if (st.getQuestItemsCount(BOOK_OF_PRAGA) < 1 && st.getQuestItemsCount(NECKLACE_OF_MOTHER) > 0 && st.getQuestItemsCount(PENDANT_OF_MOTHER) < 1)
			{
				htmltext = "guard_praga_q0405_02.htm";
			}
			else if (st.getQuestItemsCount(BOOK_OF_PRAGA) < 1 && st.getQuestItemsCount(NECKLACE_OF_MOTHER) > 0 && st.getQuestItemsCount(PENDANT_OF_MOTHER) > 0)
			{
				htmltext = "guard_praga_q0405_03.htm";
				st.takeItems(NECKLACE_OF_MOTHER, -1);
				st.takeItems(PENDANT_OF_MOTHER, -1);
				st.giveItems(BOOK_OF_PRAGA, 1);
				checkBooks(st);
			}
			else if (st.getQuestItemsCount(BOOK_OF_PRAGA) > 0)
			{
				htmltext = "guard_praga_q0405_04.htm";
			}
		}
		else if (npcId == LIONEL)
		{
			if (st.getQuestItemsCount(LETTER_OF_ORDER2) < 1)
			{
				htmltext = "lemoniell_q0405_02.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(LETTER_OF_ORDER2) == 1 && st.getQuestItemsCount(BOOK_OF_LEMONIELL) < 1 && st.getQuestItemsCount(LEMONIELLS_COVENANT) < 1 && st.getQuestItemsCount(CERTIFICATE_OF_GALLINT) < 1)
			{
				htmltext = "lemoniell_q0405_01.htm";
				st.giveItems(BOOK_OF_LEMONIELL, 1);
				st.setCond(4);
			}
			else if (cond == 4 && st.getQuestItemsCount(LETTER_OF_ORDER2) == 1 && st.getQuestItemsCount(BOOK_OF_LEMONIELL) > 0 && st.getQuestItemsCount(LEMONIELLS_COVENANT) < 1 && st.getQuestItemsCount(CERTIFICATE_OF_GALLINT) < 1)
			{
				htmltext = "lemoniell_q0405_03.htm";
			}
			else if (st.getQuestItemsCount(LETTER_OF_ORDER2) == 1 && st.getQuestItemsCount(BOOK_OF_LEMONIELL) < 1 && st.getQuestItemsCount(LEMONIELLS_COVENANT) < 1 && st.getQuestItemsCount(CERTIFICATE_OF_GALLINT) > 0)
			{
				htmltext = "lemoniell_q0405_04.htm";
				st.takeItems(CERTIFICATE_OF_GALLINT, -1);
				st.giveItems(LEMONIELLS_COVENANT, 1);
				st.setCond(6);
			}
			else if (st.getQuestItemsCount(LETTER_OF_ORDER2) == 1 && st.getQuestItemsCount(BOOK_OF_LEMONIELL) < 1 && st.getQuestItemsCount(LEMONIELLS_COVENANT) > 0 && st.getQuestItemsCount(CERTIFICATE_OF_GALLINT) < 1)
			{
				htmltext = "lemoniell_q0405_05.htm";
			}
		}
		else if (npcId == GALLINT && st.getQuestItemsCount(LETTER_OF_ORDER2) > 0)
		{
			if (cond == 4 && st.getQuestItemsCount(BOOK_OF_LEMONIELL) > 0 && st.getQuestItemsCount(CERTIFICATE_OF_GALLINT) < 1)
			{
				htmltext = "gallin_q0405_01.htm";
				st.takeItems(BOOK_OF_LEMONIELL, -1);
				st.giveItems(CERTIFICATE_OF_GALLINT, 1);
				st.setCond(5);
			}
			else if (cond == 5 && st.getQuestItemsCount(BOOK_OF_LEMONIELL) < 1 && st.getQuestItemsCount(CERTIFICATE_OF_GALLINT) > 0)
			{
				htmltext = "gallin_q0405_02.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if (npcId == RUIN_ZOMBIE | npcId == RUIN_ZOMBIE_LEADER)
		{
			if (st.getCond() == 1 && st.getQuestItemsCount(PENDANT_OF_MOTHER) < 1)
			{
				st.giveItems(PENDANT_OF_MOTHER, 1);
				st.playSound(SOUND_MIDDLE);
			}
		}
		return null;
	}
}