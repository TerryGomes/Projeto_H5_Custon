package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _10280_MutatedKaneusSchuttgart extends Quest implements ScriptFile
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

	// NPCs
	private static final int Vishotsky = 31981;
	private static final int Atraxia = 31972;

	// MOBs
	private static final int VenomousStorace = 18571;
	private static final int KelBilette = 18573;

	// Items
	private static final int Tissue1 = 13838;
	private static final int Tissue2 = 13839;

	public _10280_MutatedKaneusSchuttgart()
	{
		super(true);
		addStartNpc(Vishotsky);
		addTalkId(Atraxia);
		addKillId(VenomousStorace, KelBilette);
		addQuestItem(Tissue1, Tissue2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("31981-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31972-02.htm"))
		{
			st.giveItems(57, 300000);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		if (id == COMPLETED)
		{
			if (npcId == Vishotsky)
			{
				htmltext = "31981-0a.htm";
			}
		}
		else if (id == CREATED && npcId == Vishotsky)
		{
			if (st.getPlayer().getLevel() >= 58)
			{
				htmltext = "31981-01.htm";
			}
			else
			{
				htmltext = "31981-00.htm";
			}
		}
		else if (npcId == Vishotsky)
		{
			if (cond == 1)
			{
				htmltext = "31981-04.htm";
			}
			else if (cond == 2)
			{
				htmltext = "31981-05.htm";
			}
		}
		else if (npcId == Atraxia)
		{
			if (cond == 1)
			{
				htmltext = "31972-01a.htm";
			}
			else if (cond == 2)
			{
				htmltext = "31972-01.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getState() == STARTED && st.getCond() == 1)
		{
			st.giveItems(Tissue1, 1);
			st.giveItems(Tissue2, 1);
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
}