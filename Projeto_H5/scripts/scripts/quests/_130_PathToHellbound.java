package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * User: Keiichi
 * Date: 05.10.2008
 * Time: 19:45:18
 * Info: Один из 2х квестов для прохода на остров Hellbound.
 * Info: Пройдя его ведьма Galate открывает ТП до локации (xyz = -11095, 236440, -3232)
 */
public class _130_PathToHellbound extends Quest implements ScriptFile
{
	// NPC's
	private static int CASIAN = 30612;
	private static int GALATE = 32292;
	// ITEMS
	private static int CASIAN_BLUE_CRY = 12823;

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

	public _130_PathToHellbound()
	{
		super(false);

		addStartNpc(CASIAN);
		addTalkId(GALATE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;

		if (event.equals("sage_kasian_q0130_05.htm") && cond == 0)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}

		if (event.equals("galate_q0130_03.htm") && cond == 1)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}

		if (event.equals("sage_kasian_q0130_08.htm") && cond == 2)
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(CASIAN_BLUE_CRY, 1);
		}

		if (event.equals("galate_q0130_07.htm") && cond == 3)
		{
			st.playSound(SOUND_FINISH);
			st.takeItems(CASIAN_BLUE_CRY, -1);
			st.exitCurrentQuest(false);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();

		if (npcId == CASIAN)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 78)
				{
					htmltext = "sage_kasian_q0130_01.htm";
				}
				else
				{
					htmltext = "sage_kasian_q0130_02.htm";
					st.exitCurrentQuest(true);
				}
			}
			if (cond == 2)
			{
				htmltext = "sage_kasian_q0130_07.htm";
			}
		}

		else if (id == STARTED)
		{
			if (npcId == GALATE)
			{
				if (cond == 1)
				{
					htmltext = "galate_q0130_01.htm";
				}

				if (cond == 3)
				{
					htmltext = "galate_q0130_05.htm";
				}
			}
		}

		return htmltext;
	}
}