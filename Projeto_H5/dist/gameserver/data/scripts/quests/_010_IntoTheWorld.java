package quests;

import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _010_IntoTheWorld extends Quest implements ScriptFile
{
	int VERY_EXPENSIVE_NECKLACE = 7574;
	int SCROLL_OF_ESCAPE_GIRAN = 7126;
	int MARK_OF_TRAVELER = 7570;

	int BALANKI = 30533;
	int REED = 30520;
	int GERALD = 30650;

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

	public _010_IntoTheWorld()
	{
		super(false);

		addStartNpc(BALANKI);

		addTalkId(BALANKI);
		addTalkId(REED);
		addTalkId(GERALD);

		addQuestItem(VERY_EXPENSIVE_NECKLACE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("elder_balanki_q0010_0104.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("warehouse_chief_reed_q0010_0201.htm"))
		{
			st.giveItems(VERY_EXPENSIVE_NECKLACE, 1);
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("gerald_priest_of_earth_q0010_0301.htm"))
		{
			st.takeItems(VERY_EXPENSIVE_NECKLACE, -1);
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("warehouse_chief_reed_q0010_0401.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("elder_balanki_q0010_0501.htm"))
		{
			st.giveItems(SCROLL_OF_ESCAPE_GIRAN, 1);
			st.giveItems(MARK_OF_TRAVELER, 1);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == BALANKI)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getRace() == Race.dwarf && st.getPlayer().getLevel() >= 3)
				{
					htmltext = "elder_balanki_q0010_0101.htm";
				}
				else
				{
					htmltext = "elder_balanki_q0010_0102.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "elder_balanki_q0010_0105.htm";
				break;
			case 4:
				htmltext = "elder_balanki_q0010_0401.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == REED)
		{
			switch (cond)
			{
			case 1:
				htmltext = "warehouse_chief_reed_q0010_0101.htm";
				break;
			case 2:
				htmltext = "warehouse_chief_reed_q0010_0202.htm";
				break;
			case 3:
				htmltext = "warehouse_chief_reed_q0010_0301.htm";
				break;
			case 4:
				htmltext = "warehouse_chief_reed_q0010_0402.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == GERALD)
		{
			if (cond == 2 && st.getQuestItemsCount(VERY_EXPENSIVE_NECKLACE) > 0)
			{
				htmltext = "gerald_priest_of_earth_q0010_0201.htm";
			}
			else if (cond == 3)
			{
				htmltext = "gerald_priest_of_earth_q0010_0302.htm";
			}
			else
			{
				htmltext = "gerald_priest_of_earth_q0010_0303.htm";
			}
		}
		return htmltext;
	}
}