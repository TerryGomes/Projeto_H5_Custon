package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _116_BeyondtheHillsofWinter extends Quest implements ScriptFile
{
	// NPC
	public final int FILAUR = 30535;
	public final int OBI = 32052;
	// Quest Item
	public final int Supplying_Goods_for_Railroad_Worker = 8098;
	// Item
	public final int Bandage = 1833;
	public final int Energy_Stone = 5589;
	public final int Thief_Key = 1661;
	public final int SSD = 1463;

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

	public _116_BeyondtheHillsofWinter()
	{
		super(false);

		addStartNpc(FILAUR);
		addTalkId(OBI);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("elder_filaur_q0116_0104.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("elder_filaur_q0116_0201.htm"))
		{
			if (st.getQuestItemsCount(Bandage) >= 20 && st.getQuestItemsCount(Energy_Stone) >= 5 && st.getQuestItemsCount(Thief_Key) >= 10)
			{
				st.takeItems(Bandage, 20);
				st.takeItems(Energy_Stone, 5);
				st.takeItems(Thief_Key, 10);
				st.giveItems(Supplying_Goods_for_Railroad_Worker, 1);
				st.setCond(2);
				st.setState(STARTED);
			}
			else
			{
				htmltext = "elder_filaur_q0116_0104.htm";
			}
		}
		else if (event.equalsIgnoreCase("materials"))
		{
			htmltext = "railman_obi_q0116_0302.htm";
			st.takeItems(Supplying_Goods_for_Railroad_Worker, 1);
			st.giveItems(SSD, 1740);
			st.addExpAndSp(82792, 4981);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("adena"))
		{
			htmltext = "railman_obi_q0116_0302.htm";
			st.takeItems(Supplying_Goods_for_Railroad_Worker, 1);
			st.giveItems(ADENA_ID, 17387);
			st.addExpAndSp(82792, 4981);
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
		int cond = 0;
		if (id != CREATED)
		{
			cond = st.getCond();
		}
		if (npcId == FILAUR)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() < 30)
				{
					htmltext = "elder_filaur_q0116_0103.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "elder_filaur_q0116_0101.htm";
				}
				break;
			case 1:
				htmltext = "elder_filaur_q0116_0105.htm";
				break;
			case 2:
				htmltext = "elder_filaur_q0116_0201.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == OBI)
		{
			if (cond == 2 && st.getQuestItemsCount(Supplying_Goods_for_Railroad_Worker) > 0)
			{
				htmltext = "railman_obi_q0116_0201.htm";
			}
		}
		return htmltext;
	}
}