package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _122_OminousNews extends Quest implements ScriptFile
{
	int MOIRA = 31979;
	int KARUDA = 32017;

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

	public _122_OminousNews()
	{
		super(false);
		addStartNpc(MOIRA);
		addTalkId(KARUDA);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getCond();
		htmltext = event;
		if (htmltext.equalsIgnoreCase("seer_moirase_q0122_0104.htm") && cond == 0)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (htmltext.equalsIgnoreCase("karuda_q0122_0201.htm"))
		{
			if (cond == 1)
			{
				st.giveItems(ADENA_ID, 8923);
				st.addExpAndSp(45151, 2310); // награда соответствует Т2
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
			{
				htmltext = "noquest";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == MOIRA)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 20)
				{
					htmltext = "seer_moirase_q0122_0101.htm";
				}
				else
				{
					htmltext = "seer_moirase_q0122_0103.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "seer_moirase_q0122_0104.htm";
			}
		}
		else if (npcId == KARUDA && cond == 1)
		{
			htmltext = "karuda_q0122_0101.htm";
		}
		return htmltext;
	}
}