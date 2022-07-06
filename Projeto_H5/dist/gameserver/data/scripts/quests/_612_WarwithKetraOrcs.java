package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _612_WarwithKetraOrcs extends Quest implements ScriptFile
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

	// NPC
	private static final int DURAI = 31377;

	// Quest items
	private static final int MOLAR_OF_KETRA_ORC = 7234;
	private static final int MOLAR_OF_KETRA_ORC_DROP_CHANCE = 80;
	private static final int NEPENTHES_SEED = 7187;

	private static final int[] KETRA_NPC_LIST = new int[19];

	public _612_WarwithKetraOrcs()
	{
		super(true);

		addStartNpc(DURAI);

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

		addQuestItem(MOLAR_OF_KETRA_ORC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "elder_ashas_barka_durai_q0612_0104.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("elder_ashas_barka_durai_q0612_0202.htm"))
		{
			long ec = st.getQuestItemsCount(MOLAR_OF_KETRA_ORC) / 5;
			if (ec > 0)
			{
				st.takeItems(MOLAR_OF_KETRA_ORC, ec * 5);
				st.giveItems(NEPENTHES_SEED, ec);
			}
			else
			{
				htmltext = "elder_ashas_barka_durai_q0612_0203.htm";
			}
		}
		else if (event.equalsIgnoreCase("elder_ashas_barka_durai_q0612_0204.htm"))
		{
			st.takeItems(MOLAR_OF_KETRA_ORC, -1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (cond == 0)
		{
			if (st.getPlayer().getLevel() >= 74)
			{
				htmltext = "elder_ashas_barka_durai_q0612_0101.htm";
			}
			else
			{
				htmltext = "elder_ashas_barka_durai_q0612_0103.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (cond == 1 && st.getQuestItemsCount(MOLAR_OF_KETRA_ORC) == 0)
		{
			htmltext = "elder_ashas_barka_durai_q0612_0106.htm";
		}
		else if (cond == 1 && st.getQuestItemsCount(MOLAR_OF_KETRA_ORC) > 0)
		{
			htmltext = "elder_ashas_barka_durai_q0612_0105.htm";
		}
		return htmltext;
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
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (isKetraNpc(npc.getNpcId()) && st.getCond() == 1)
		{
			st.rollAndGive(MOLAR_OF_KETRA_ORC, 1, MOLAR_OF_KETRA_ORC_DROP_CHANCE);
		}
		return null;
	}
}