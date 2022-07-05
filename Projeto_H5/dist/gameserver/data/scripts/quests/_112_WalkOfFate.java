package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _112_WalkOfFate extends Quest implements ScriptFile
{
	// NPC
	private static final int Livina = 30572;
	private static final int Karuda = 32017;
	// Items
	private static final int EnchantD = 956;

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

	public _112_WalkOfFate()
	{
		super(false);

		addStartNpc(Livina);
		addTalkId(Karuda);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("karuda_q0112_0201.htm"))
		{
			st.addExpAndSp(112876, 5774);
			st.giveItems(ADENA_ID, (long) (22308 + 6000 * (st.getRateQuestsReward() - 1)), true);
			st.giveItems(EnchantD, 1, false);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("seer_livina_q0112_0104.htm"))
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
		if (npcId == Livina)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 20)
				{
					htmltext = "seer_livina_q0112_0101.htm";
				}
				else
				{
					htmltext = "seer_livina_q0112_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "seer_livina_q0112_0105.htm";
			}
		}
		else if (npcId == Karuda)
		{
			if (cond == 1)
			{
				htmltext = "karuda_q0112_0101.htm";
			}
		}
		return htmltext;
	}
}
