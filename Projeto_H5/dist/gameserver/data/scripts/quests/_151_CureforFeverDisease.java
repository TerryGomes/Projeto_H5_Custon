package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage;
import l2f.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2f.gameserver.scripts.ScriptFile;

public class _151_CureforFeverDisease extends Quest implements ScriptFile
{
	int POISON_SAC = 703;
	int FEVER_MEDICINE = 704;
	int ROUND_SHIELD = 102;

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

	public _151_CureforFeverDisease()
	{
		super(false);

		addStartNpc(30050);

		addTalkId(30032);

		addKillId(20103, 20106, 20108);

		addQuestItem(FEVER_MEDICINE, POISON_SAC);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("30050-03.htm"))
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
		int id = st.getState();
		int cond = 0;
		if (id != CREATED)
		{
			cond = st.getCond();
		}
		if (npcId == 30050)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 15)
				{
					htmltext = "30050-02.htm";
				}
				else
				{
					htmltext = "30050-01.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1 && st.getQuestItemsCount(POISON_SAC) == 0 && st.getQuestItemsCount(FEVER_MEDICINE) == 0)
			{
				htmltext = "30050-04.htm";
			}
			else if (cond == 1 && st.getQuestItemsCount(POISON_SAC) == 1)
			{
				htmltext = "30050-05.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(FEVER_MEDICINE) == 1)
			{
				st.takeItems(FEVER_MEDICINE, -1);

				st.giveItems(ROUND_SHIELD, 1);
				st.getPlayer().addExpAndSp(13106, 613);

				if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q4"))
				{
					st.getPlayer().setVar("p1q4", "1", -1);
					st.getPlayer().sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
				}

				htmltext = "30050-06.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
		}
		else if (npcId == 30032)
		{
			if (cond == 2 && st.getQuestItemsCount(POISON_SAC) > 0)
			{
				st.giveItems(FEVER_MEDICINE, 1);
				st.takeItems(POISON_SAC, -1);
				st.setCond(3);
				htmltext = "30032-01.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(FEVER_MEDICINE) > 0)
			{
				htmltext = "30032-02.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if ((npcId == 20103 || npcId == 20106 || npcId == 20108) && st.getQuestItemsCount(POISON_SAC) == 0 && st.getCond() == 1 && Rnd.chance(50))
		{
			st.setCond(2);
			st.giveItems(POISON_SAC, 1);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
}