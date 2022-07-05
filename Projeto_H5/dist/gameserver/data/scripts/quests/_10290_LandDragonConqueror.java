package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _10290_LandDragonConqueror extends Quest implements ScriptFile
{
	private static final int Theodric = 30755;
	private static final int ShabbyNecklace = 15522;
	private static final int MiracleNecklace = 15523;
	private static final int UltimateAntharas = 29068;

	public _10290_LandDragonConqueror()
	{
		super(PARTY_ALL);
		addStartNpc(Theodric);
		addQuestItem(ShabbyNecklace, MiracleNecklace);
		addKillId(UltimateAntharas);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("theodric_q10290_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(ShabbyNecklace, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Theodric)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 83 && st.getQuestItemsCount(3865) >= 1)
				{
					htmltext = "theodric_q10290_01.htm";
				}
				else if (st.getQuestItemsCount(3865) < 1)
				{
					htmltext = "theodric_q10290_00a.htm";
				}
				else
				{
					htmltext = "theodric_q10290_00.htm";
				}
				break;
			case 1:
				htmltext = "theodric_q10290_05.htm";
				break;
			case 2:
				if (st.getQuestItemsCount(MiracleNecklace) >= 1)
				{
					htmltext = "theodric_q10290_07.htm";
					st.takeAllItems(MiracleNecklace);
					st.giveItems(8568, 1);
					st.giveItems(ADENA_ID, 131236);
					st.addExpAndSp(702557, 76334);
					st.playSound(SOUND_FINISH);
					st.setState(COMPLETED);
					st.exitCurrentQuest(false);
				}
				else
				{
					htmltext = "theodric_q10290_06.htm";
				}
				break;
			default:
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (cond == 1 && npcId == UltimateAntharas)
		{
			st.takeAllItems(ShabbyNecklace);
			st.giveItems(MiracleNecklace, 1);
			st.setCond(2);
		}
		return null;
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
}