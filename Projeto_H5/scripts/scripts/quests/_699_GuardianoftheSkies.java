package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _699_GuardianoftheSkies extends Quest implements ScriptFile
{
	// NPC's
	private static int engineer_recon = 32557;

	// ITEMS
	private static int q_gold_feather_of_vulture = 13871;

	// MOB's
	private static int vulture_rider_1lv = 22614;
	private static int vulture_rider_2lv = 22615;
	private static int vulture_rider_3lv = 25633;
	private static int master_rider = 25623;

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

	public _699_GuardianoftheSkies()
	{
		super(PARTY_ALL);
		addStartNpc(engineer_recon);
		addTalkId(engineer_recon);
		addKillId(vulture_rider_1lv, vulture_rider_2lv, vulture_rider_3lv, master_rider);
		addQuestItem(q_gold_feather_of_vulture);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int cond = st.getCond();
		String htmltext = event;

		if (event.equals("quest_accept") && cond == 0)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			htmltext = "engineer_recon_q0699_04.htm";
		}
		else if (event.equals("reply_2") && cond == 1)
		{
			htmltext = "engineer_recon_q0699_08.htm";
		}
		else if (event.equals("reply_3") && cond == 1)
		{
			st.playSound(SOUND_FINISH);
			htmltext = "engineer_recon_q0699_09.htm";
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		QuestState GoodDayToFly = st.getPlayer().getQuestState(_10273_GoodDayToFly.class);

		if (npcId == engineer_recon)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 75 && GoodDayToFly != null && GoodDayToFly.isCompleted())
				{
					htmltext = "engineer_recon_q0699_01.htm";
				}
				else
				{
					htmltext = "engineer_recon_q0699_02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1 && st.getQuestItemsCount(q_gold_feather_of_vulture) < 1)
			{
				htmltext = "engineer_recon_q0699_05.htm";
			}
			else if (cond == 1 && st.getQuestItemsCount(q_gold_feather_of_vulture) >= 1 && st.getQuestItemsCount(q_gold_feather_of_vulture) < 10)
			{
				st.giveItems(ADENA_ID, st.getQuestItemsCount(q_gold_feather_of_vulture) * 1500);
				st.takeItems(q_gold_feather_of_vulture, -1);
				htmltext = "engineer_recon_q0699_06.htm";
			}
			else if (cond == 1 && st.getQuestItemsCount(q_gold_feather_of_vulture) >= 10)
			{
				st.giveItems(ADENA_ID, st.getQuestItemsCount(q_gold_feather_of_vulture) * 1500 + 8335);
				st.takeItems(q_gold_feather_of_vulture, -1);
				htmltext = "engineer_recon_q0699_06.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1)
		{
			if (npcId == vulture_rider_1lv)
			{
				int i3 = Rnd.get(1000);
				if (i3 < 840)
				{
					st.giveItems(q_gold_feather_of_vulture, 1);
					st.playSound(SOUND_ITEMGET);
				}
			}
			else if (npcId == vulture_rider_2lv)
			{
				int i3 = Rnd.get(1000);
				if (i3 < 857)
				{
					st.giveItems(q_gold_feather_of_vulture, 1);
					st.playSound(SOUND_ITEMGET);
				}
			}
			else if (npcId == vulture_rider_3lv)
			{
				int i3 = Rnd.get(1000);
				if (i3 < 719)
				{
					st.giveItems(q_gold_feather_of_vulture, 1);
					st.playSound(SOUND_ITEMGET);
				}
			}
			else if (npcId == master_rider)
			{
				int i0 = Rnd.get(1000);
				if (i0 < 215)
				{
					int i1 = Rnd.get(10) + 90;
					st.giveItems(q_gold_feather_of_vulture, i1);
					st.playSound(SOUND_ITEMGET);
				}
				else if (i0 < 446)
				{
					int i1 = Rnd.get(10) + 80;
					st.giveItems(q_gold_feather_of_vulture, i1);
					st.playSound(SOUND_ITEMGET);
				}
				else if (i0 < 715)
				{
					int i1 = Rnd.get(10) + 70;
					st.giveItems(q_gold_feather_of_vulture, i1);
					st.playSound(SOUND_ITEMGET);
				}
				else if (i0 < 1000)
				{
					int i1 = Rnd.get(10) + 60;
					st.giveItems(q_gold_feather_of_vulture, i1);
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}
}