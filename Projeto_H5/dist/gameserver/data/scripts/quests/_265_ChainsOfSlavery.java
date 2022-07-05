package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2f.gameserver.scripts.ScriptFile;

/**
 * Квест проверен и работает. Рейты применены путем увеличения награды за квестовые предметы.
 */
public class _265_ChainsOfSlavery extends Quest implements ScriptFile
{
	// NPC
	private static final int KRISTIN = 30357;

	// MOBS
	private static final int IMP = 20004;
	private static final int IMP_ELDER = 20005;

	// ITEMS
	private static final int IMP_SHACKLES = 1368;

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

	public _265_ChainsOfSlavery()
	{
		super(false);
		addStartNpc(KRISTIN);

		addKillId(IMP);
		addKillId(IMP_ELDER);

		addQuestItem(IMP_SHACKLES);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("sentry_krpion_q0265_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("sentry_krpion_q0265_06.htm"))
		{
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if (st.getCond() == 0)
		{
			if (st.getPlayer().getRace() != Race.darkelf)
			{
				htmltext = "sentry_krpion_q0265_00.htm";
				st.exitCurrentQuest(true);
			}
			else if (st.getPlayer().getLevel() < 6)
			{
				htmltext = "sentry_krpion_q0265_01.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "sentry_krpion_q0265_02.htm";
			}
		}
		else
		{
			long count = st.getQuestItemsCount(IMP_SHACKLES);
			if (count > 0)
			{
				if (count >= 10)
				{
					st.giveItems(ADENA_ID, 13 * count + 500, true);
				}
				else
				{
					st.giveItems(ADENA_ID, 13 * count, true);
				}
			}
			st.takeItems(IMP_SHACKLES, -1);
			htmltext = "sentry_krpion_q0265_05.htm";

			if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q2"))
			{
				st.getPlayer().setVar("p1q2", "1", -1);
				st.getPlayer().sendPacket(new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n                  Go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				QuestState qs = st.getPlayer().getQuestState(_255_Tutorial.class);
				if (qs != null && qs.getInt("Ex") != 10)
				{
					st.showQuestionMark(26);
					qs.set("Ex", "10");
					if (st.getPlayer().getClassId().isMage())
					{
						st.playTutorialVoice("tutorial_voice_027");
						st.giveItems(5790, 3000);
					}
					else
					{
						st.playTutorialVoice("tutorial_voice_026");
						st.giveItems(5789, 6000);
					}
				}
			}
			else
			{
				htmltext = "sentry_krpion_q0265_04.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if (st.getCond() == 1 && Rnd.chance(5 + npcId - 20004))
		{
			st.giveItems(IMP_SHACKLES, 1);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}