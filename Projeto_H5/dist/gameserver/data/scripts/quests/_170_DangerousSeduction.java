package quests;

import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _170_DangerousSeduction extends Quest implements ScriptFile
{
	// NPC
	private static final int Vellior = 30305;
	// Quest Items
	private static final int NightmareCrystal = 1046;
	// MOB
	private static final int Merkenis = 27022;

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

	public _170_DangerousSeduction()
	{
		super(false);
		addStartNpc(Vellior);
		addTalkId(Vellior);
		addKillId(Merkenis);
		addQuestItem(NightmareCrystal);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30305-04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == Vellior)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getRace() != Race.darkelf)
				{
					htmltext = "30305-00.htm";
					st.exitCurrentQuest(true);
				}
				else if (st.getPlayer().getLevel() < 21)
				{
					htmltext = "30305-02.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "30305-03.htm";
				}
				break;
			case 1:
				htmltext = "30305-05.htm";
				break;
			case 2:
				st.takeItems(NightmareCrystal, -1);
				st.giveItems(ADENA_ID, 102680, true);
				st.addExpAndSp(38607, 4018);
				htmltext = "30305-06.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
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
		if (cond == 1 && npcId == Merkenis)
		{
			if (st.getQuestItemsCount(NightmareCrystal) == 0)
			{
				st.giveItems(NightmareCrystal, 1);
			}
			st.playSound(SOUND_MIDDLE);
			st.setCond(2);
			st.setState(STARTED);
		}
		return null;
	}
}