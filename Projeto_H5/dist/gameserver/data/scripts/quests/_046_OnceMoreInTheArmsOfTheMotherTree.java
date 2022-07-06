package quests;

import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _046_OnceMoreInTheArmsOfTheMotherTree extends Quest implements ScriptFile
{
	private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_1 = 7563;
	private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_2 = 7564;
	private static final int GALLADUCCIS_ORDER_DOCUMENT_ID_3 = 7565;
	private static final int MAGIC_SWORD_HILT_ID = 7568;
	private static final int GEMSTONE_POWDER_ID = 7567;
	private static final int PURIFIED_MAGIC_NECKLACE_ID = 7566;
	private static final int MARK_OF_TRAVELER_ID = 7570;
	private static final int SCROLL_OF_ESCAPE_ELVEN_VILLAGE = 7118;

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

	public _046_OnceMoreInTheArmsOfTheMotherTree()
	{
		super(false);

		addStartNpc(30097);

		addTalkId(30097);

		addTalkId(30097);
		addTalkId(30094);
		addTalkId(30090);
		addTalkId(30116);

		addQuestItem(new int[]
		{
			GALLADUCCIS_ORDER_DOCUMENT_ID_1,
			GALLADUCCIS_ORDER_DOCUMENT_ID_2,
			GALLADUCCIS_ORDER_DOCUMENT_ID_3,
			MAGIC_SWORD_HILT_ID,
			GEMSTONE_POWDER_ID,
			PURIFIED_MAGIC_NECKLACE_ID
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("1"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_1, 1);
			htmltext = "galladuchi_q0046_0104.htm";
		}
		else if (event.equals("2"))
		{
			st.setCond(2);
			st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_1, 1);
			st.giveItems(MAGIC_SWORD_HILT_ID, 1);
			htmltext = "gentler_q0046_0201.htm";
		}
		else if (event.equals("3"))
		{
			st.setCond(3);
			st.takeItems(MAGIC_SWORD_HILT_ID, 1);
			st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_2, 1);
			htmltext = "galladuchi_q0046_0301.htm";
		}
		else if (event.equals("4"))
		{
			st.setCond(4);
			st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_2, 1);
			st.giveItems(GEMSTONE_POWDER_ID, 1);
			htmltext = "sandra_q0046_0401.htm";
		}
		else if (event.equals("5"))
		{
			st.setCond(5);
			st.takeItems(GEMSTONE_POWDER_ID, 1);
			st.giveItems(GALLADUCCIS_ORDER_DOCUMENT_ID_3, 1);
			htmltext = "galladuchi_q0046_0501.htm";
		}
		else if (event.equals("6"))
		{
			st.setCond(6);
			st.takeItems(GALLADUCCIS_ORDER_DOCUMENT_ID_3, 1);
			st.giveItems(PURIFIED_MAGIC_NECKLACE_ID, 1);
			htmltext = "dustin_q0046_0601.htm";
		}
		else if (event.equals("7"))
		{
			st.giveItems(SCROLL_OF_ESCAPE_ELVEN_VILLAGE, 1);
			st.takeItems(PURIFIED_MAGIC_NECKLACE_ID, 1);
			htmltext = "galladuchi_q0046_0701.htm";
			st.setCond(0);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		if (id == CREATED)
		{
			if (st.getPlayer().getRace() != Race.elf || st.getQuestItemsCount(MARK_OF_TRAVELER_ID) == 0)
			{
				htmltext = "galladuchi_q0046_0102.htm";
				st.exitCurrentQuest(true);
			}
			else if (st.getPlayer().getLevel() < 3)
			{
				htmltext = "galladuchi_q0046_0103.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "galladuchi_q0046_0101.htm";
			}
		}
		else if (npcId == 30097 && st.getCond() == 1)
		{
			htmltext = "galladuchi_q0046_0105.htm";
		}
		else if (npcId == 30097 && st.getCond() == 2)
		{
			htmltext = "galladuchi_q0046_0201.htm";
		}
		else if (npcId == 30097 && st.getCond() == 3)
		{
			htmltext = "galladuchi_q0046_0303.htm";
		}
		else if (npcId == 30097 && st.getCond() == 4)
		{
			htmltext = "galladuchi_q0046_0401.htm";
		}
		else if (npcId == 30097 && st.getCond() == 5)
		{
			htmltext = "galladuchi_q0046_0503.htm";
		}
		else if (npcId == 30097 && st.getCond() == 6)
		{
			htmltext = "galladuchi_q0046_0601.htm";
		}
		else if (npcId == 30094 && st.getCond() == 1)
		{
			htmltext = "gentler_q0046_0101.htm";
		}
		else if (npcId == 30094 && st.getCond() == 2)
		{
			htmltext = "gentler_q0046_0203.htm";
		}
		else if (npcId == 30090 && st.getCond() == 3)
		{
			htmltext = "sandra_q0046_0301.htm";
		}
		else if (npcId == 30090 && st.getCond() == 4)
		{
			htmltext = "sandra_q0046_0403.htm";
		}
		else if (npcId == 30116 && st.getCond() == 5)
		{
			htmltext = "dustin_q0046_0501.htm";
		}
		else if (npcId == 30116 && st.getCond() == 6)
		{
			htmltext = "dustin_q0046_0603.htm";
		}
		return htmltext;
	}
}