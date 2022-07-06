package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _10291_FireDragonDestroyer extends Quest implements ScriptFile
{
	private static final int Klein = 31540;
	private static final int PoorNecklace = 15524;
	private static final int ValorNecklace = 15525;
	private static final int Valakas = 29028;

	public _10291_FireDragonDestroyer()
	{
		super(PARTY_ALL);
		addStartNpc(Klein);
		addQuestItem(PoorNecklace, ValorNecklace);
		addKillId(Valakas);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("klein_q10291_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(PoorNecklace, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Klein)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 83 && st.getQuestItemsCount(7267) >= 1)
				{
					htmltext = "klein_q10291_01.htm";
				}
				else if (st.getQuestItemsCount(7267) < 1)
				{
					htmltext = "klein_q10291_00a.htm";
				}
				else
				{
					htmltext = "klein_q10291_00.htm";
				}
				break;
			case 1:
				htmltext = "klein_q10291_05.htm";
				break;
			case 2:
				if (st.getQuestItemsCount(ValorNecklace) >= 1)
				{
					htmltext = "klein_q10291_07.htm";
					st.takeAllItems(ValorNecklace);
					st.giveItems(8567, 1);
					st.giveItems(ADENA_ID, 126549);
					st.addExpAndSp(717291, 77397);
					st.playSound(SOUND_FINISH);
					st.setState(COMPLETED);
					st.exitCurrentQuest(false);
				}
				else
				{
					htmltext = "klein_q10291_06.htm";
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

		if (cond == 1 && npcId == Valakas)
		{
			st.takeAllItems(PoorNecklace);
			st.giveItems(ValorNecklace, 1);
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