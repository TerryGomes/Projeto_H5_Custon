package quests;

import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _634_InSearchofDimensionalFragments extends Quest implements ScriptFile
{
	int DIMENSION_FRAGMENT_ID = 7079;

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

	public _634_InSearchofDimensionalFragments()
	{
		super(true);

		for (int npcId = 31494; npcId < 31508; npcId++)
		{
			addTalkId(npcId);
			addStartNpc(npcId);
		}

		for (int mobs = 21208; mobs < 21256; mobs++)
		{
			addKillId(mobs);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "dimension_keeper_1_q0634_03.htm";
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.setCond(1);
		}
		else if (event.equalsIgnoreCase("634_2"))
		{
			htmltext = "dimension_keeper_1_q0634_06.htm";
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		if (id == CREATED)
		{
			if (st.getPlayer().getLevel() > 20)
			{
				htmltext = "dimension_keeper_1_q0634_01.htm";
			}
			else
			{
				htmltext = "dimension_keeper_1_q0634_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (id == STARTED)
		{
			htmltext = "dimension_keeper_1_q0634_04.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		st.rollAndGive(DIMENSION_FRAGMENT_ID, 2, 60 * Experience.penaltyModifier(st.calculateLevelDiffForDrop(npc.getLevel(), st.getPlayer().getLevel()), 9) * npc.getTemplate().rateHp / 4);
		return null;
	}
}