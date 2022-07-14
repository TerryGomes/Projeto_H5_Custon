package quests;

import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _172_NewHorizons extends Quest implements ScriptFile
{
	// NPC
	private static final int Zenya = 32140;
	private static final int Ragara = 32163;
	// Items
	private static final int ScrollOfEscapeGiran = 7126;
	private static final int MarkOfTraveler = 7570;

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

	public _172_NewHorizons()
	{
		super(false);

		addStartNpc(Zenya);

		addTalkId(Zenya);
		addTalkId(Ragara);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("subelder_zenya_q0172_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("gatekeeper_ragara_q0172_02.htm"))
		{
			st.giveItems(ScrollOfEscapeGiran, 1);
			st.giveItems(MarkOfTraveler, 1);
			st.unset("cond");
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Zenya)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getRace() != Race.kamael)
				{
					htmltext = "subelder_zenya_q0172_03.htm";
					st.exitCurrentQuest(true);
				}
				else if (st.getPlayer().getLevel() >= 3)
				{
					htmltext = "subelder_zenya_q0172_01.htm";
				}
				else
				{
					htmltext = "subelder_zenya_q0172_02.htm";
				}
				st.exitCurrentQuest(true);
			}
		}
		else if (npcId == Ragara)
		{
			if (cond == 1)
			{
				htmltext = "gatekeeper_ragara_q0172_01.htm";
			}
		}
		return htmltext;
	}
}