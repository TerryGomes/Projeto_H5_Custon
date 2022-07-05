package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _167_DwarvenKinship extends Quest implements ScriptFile
{
	// NPC
	private static final int Carlon = 30350;
	private static final int Haprock = 30255;
	private static final int Norman = 30210;
	// Quest Items
	private static final int CarlonsLetter = 1076;
	private static final int NormansLetter = 1106;

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

	public _167_DwarvenKinship()
	{
		super(false);

		addStartNpc(Carlon);

		addTalkId(Haprock);
		addTalkId(Norman);

		addQuestItem(CarlonsLetter, NormansLetter);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30350-04.htm"))
		{
			st.giveItems(CarlonsLetter, 1);
			st.playSound(SOUND_ACCEPT);
			st.setCond(1);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30255-03.htm"))
		{
			st.takeItems(CarlonsLetter, -1);
			st.giveItems(ADENA_ID, 2000);
			st.giveItems(NormansLetter, 1);
			st.setCond(2);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30255-04.htm"))
		{
			st.takeItems(CarlonsLetter, -1);
			st.giveItems(ADENA_ID, 2000);
			st.playSound(SOUND_GIVEUP);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("30210-02.htm"))
		{
			st.takeItems(NormansLetter, -1);
			st.giveItems(ADENA_ID, 20000);
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
		int cond = st.getCond();
		if (npcId == Carlon)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 15)
				{
					htmltext = "30350-03.htm";
				}
				else
				{
					htmltext = "30350-02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond > 0)
			{
				htmltext = "30350-05.htm";
			}
		}
		else if (npcId == Haprock)
		{
			if (cond == 1)
			{
				htmltext = "30255-01.htm";
			}
			else if (cond > 1)
			{
				htmltext = "30255-05.htm";
			}
		}
		else if (npcId == Norman && cond == 2)
		{
			htmltext = "30210-01.htm";
		}
		return htmltext;
	}
}