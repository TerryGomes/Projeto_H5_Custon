package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _654_JourneytoaSettlement extends Quest implements ScriptFile
{
	// NPC
	private static final int NamelessSpirit = 31453;

	// Mobs
	private static final int CanyonAntelope = 21294;
	private static final int CanyonAntelopeSlave = 21295;

	// Items
	private static final int AntelopeSkin = 8072;

	// Rewards
	private static final int FrintezzasMagicForceFieldRemovalScroll = 8073;

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

	public _654_JourneytoaSettlement()
	{
		super(true);

		addStartNpc(NamelessSpirit);
		addKillId(CanyonAntelope, CanyonAntelopeSlave);
		addQuestItem(AntelopeSkin);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("printessa_spirit_q0654_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		if (event.equalsIgnoreCase("printessa_spirit_q0654_04.htm"))
		{
			st.setCond(2);
		}
		if (event.equalsIgnoreCase("printessa_spirit_q0654_07.htm"))
		{
			st.giveItems(FrintezzasMagicForceFieldRemovalScroll, 5);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		QuestState q = st.getPlayer().getQuestState(_119_LastImperialPrince.class);
		if (q == null)
		{
			return htmltext;
		}
		if (st.getPlayer().getLevel() < 74)
		{
			htmltext = "printessa_spirit_q0654_02.htm";
			st.exitCurrentQuest(true);
			return htmltext;
		}
		else if (!q.isCompleted())
		{
			htmltext = "noquest";
			st.exitCurrentQuest(true);
			return htmltext;
		}

		int cond = st.getCond();
		if (npc.getNpcId() == NamelessSpirit)
		{
			switch (cond)
			{
			case 0:
				return "printessa_spirit_q0654_01.htm";
			case 1:
				return "printessa_spirit_q0654_03.htm";
			case 3:
				return "printessa_spirit_q0654_06.htm";
			default:
				break;
			}
		}
		else
		{
			htmltext = "noquest";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getCond() == 2 && Rnd.chance(5))
		{
			st.setCond(3);
			st.giveItems(AntelopeSkin, 1);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
}