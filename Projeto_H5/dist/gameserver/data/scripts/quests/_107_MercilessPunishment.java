package quests;

import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.scripts.ScriptFile;

public class _107_MercilessPunishment extends Quest implements ScriptFile
{
	int HATOSS_ORDER1 = 1553;
	int HATOSS_ORDER2 = 1554;
	int HATOSS_ORDER3 = 1555;
	int LETTER_TO_HUMAN = 1557;
	int LETTER_TO_DARKELF = 1556;
	int LETTER_TO_ELF = 1558;
	int BUTCHER = 1510;

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

	public _107_MercilessPunishment()
	{
		super(false);

		addStartNpc(30568);

		addTalkId(30580);

		addKillId(27041);

		addQuestItem(LETTER_TO_DARKELF, LETTER_TO_HUMAN, LETTER_TO_ELF, HATOSS_ORDER1, HATOSS_ORDER2, HATOSS_ORDER3);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("urutu_chief_hatos_q0107_03.htm"))
		{
			st.giveItems(HATOSS_ORDER1, 1);
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("urutu_chief_hatos_q0107_06.htm"))
		{
			st.takeItems(HATOSS_ORDER2, 1);
			st.takeItems(LETTER_TO_DARKELF, 1);
			st.takeItems(LETTER_TO_HUMAN, 1);
			st.takeItems(LETTER_TO_ELF, 1);
			st.takeItems(HATOSS_ORDER1, 1);
			st.takeItems(HATOSS_ORDER2, 1);
			st.takeItems(HATOSS_ORDER3, 1);
			st.giveItems(ADENA_ID, 200);
			st.unset("cond");
			st.playSound(SOUND_GIVEUP);
		}
		else if (event.equalsIgnoreCase("urutu_chief_hatos_q0107_07.htm"))
		{
			st.takeItems(HATOSS_ORDER1, 1);
			if (st.getQuestItemsCount(HATOSS_ORDER2) == 0)
			{
				st.giveItems(HATOSS_ORDER2, 1);
			}
		}
		else if (event.equalsIgnoreCase("urutu_chief_hatos_q0107_09.htm"))
		{
			st.takeItems(HATOSS_ORDER2, 1);
			if (st.getQuestItemsCount(HATOSS_ORDER3) == 0)
			{
				st.giveItems(HATOSS_ORDER3, 1);
			}
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
		if (npcId == 30568)
		{
			if (id == CREATED)
			{
				if (st.getPlayer().getRace() != Race.orc)
				{
					htmltext = "urutu_chief_hatos_q0107_00.htm";
					st.exitCurrentQuest(true);
				}
				else if (st.getPlayer().getLevel() >= 10)
				{
					htmltext = "urutu_chief_hatos_q0107_02.htm";
				}
				else
				{
					htmltext = "urutu_chief_hatos_q0107_01.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1 && st.getQuestItemsCount(HATOSS_ORDER1) > 0)
			{
				htmltext = "urutu_chief_hatos_q0107_04.htm";
			}
			else if (cond == 2 && st.getQuestItemsCount(HATOSS_ORDER1) > 0 && st.getQuestItemsCount(LETTER_TO_HUMAN) == 0)
			{
				htmltext = "urutu_chief_hatos_q0107_04.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(HATOSS_ORDER1) > 0 && st.getQuestItemsCount(LETTER_TO_HUMAN) >= 1)
			{
				htmltext = "urutu_chief_hatos_q0107_05.htm";
				st.setCond(4);
			}
			else if (cond == 4 && st.getQuestItemsCount(HATOSS_ORDER2) > 0 && st.getQuestItemsCount(LETTER_TO_DARKELF) == 0)
			{
				htmltext = "urutu_chief_hatos_q0107_05.htm";
			}
			else if (cond == 5 && st.getQuestItemsCount(HATOSS_ORDER2) > 0 && st.getQuestItemsCount(LETTER_TO_DARKELF) >= 1)
			{
				htmltext = "urutu_chief_hatos_q0107_08.htm";
				st.setCond(6);
			}
			else if (cond == 6 && st.getQuestItemsCount(HATOSS_ORDER3) > 0 && st.getQuestItemsCount(LETTER_TO_ELF) == 0)
			{
				htmltext = "urutu_chief_hatos_q0107_08.htm";
			}
			else if (cond == 7 && st.getQuestItemsCount(HATOSS_ORDER3) > 0 && st.getQuestItemsCount(LETTER_TO_ELF) + st.getQuestItemsCount(LETTER_TO_HUMAN) + st.getQuestItemsCount(LETTER_TO_DARKELF) == 3)
			{
				htmltext = "urutu_chief_hatos_q0107_10.htm";
				st.takeItems(LETTER_TO_DARKELF, -1);
				st.takeItems(LETTER_TO_HUMAN, -1);
				st.takeItems(LETTER_TO_ELF, -1);
				st.takeItems(HATOSS_ORDER3, -1);

				st.giveItems(BUTCHER, 1);
				st.getPlayer().addExpAndSp(34565, 2962);
				st.giveItems(ADENA_ID, 14666, false);

				if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q3"))
				{
					st.getPlayer().setVar("p1q3", "1", -1); // flag for helper
					st.getPlayer().sendPacket(new ExShowScreenMessage("Acquisition of race-specific weapon complete.\n           Go find the Newbie Guide.", 5000, ScreenMessageAlign.TOP_CENTER, true));
					st.giveItems(1060, 100); // healing potion
					for (int item = 4412; item <= 4417; item++)
					{
						st.giveItems(item, 10); // echo cry
					}
					st.playTutorialVoice("tutorial_voice_026");
					st.giveItems(5789, 7000); // newbie ss
				}

				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
		}
		else if (npcId == 30580 && cond >= 1 && (st.getQuestItemsCount(HATOSS_ORDER1) > 0 || st.getQuestItemsCount(HATOSS_ORDER2) > 0 || st.getQuestItemsCount(HATOSS_ORDER3) > 0))
		{
			if (cond == 1)
			{
				st.setCond(2);
			}
			htmltext = "centurion_parugon_q0107_01.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == 27041)
		{
			if (cond == 2 && st.getQuestItemsCount(HATOSS_ORDER1) > 0 && st.getQuestItemsCount(LETTER_TO_HUMAN) == 0)
			{
				st.giveItems(LETTER_TO_HUMAN, 1);
				st.setCond(3);
				st.playSound(SOUND_ITEMGET);
			}
			else if (cond == 4 && st.getQuestItemsCount(HATOSS_ORDER2) > 0 && st.getQuestItemsCount(LETTER_TO_DARKELF) == 0)
			{
				st.giveItems(LETTER_TO_DARKELF, 1);
				st.setCond(5);
				st.playSound(SOUND_ITEMGET);
			}
			else if (cond == 6 && st.getQuestItemsCount(HATOSS_ORDER3) > 0 && st.getQuestItemsCount(LETTER_TO_ELF) == 0)
			{
				st.giveItems(LETTER_TO_ELF, 1);
				st.setCond(7);
				st.playSound(SOUND_ITEMGET);
			}
		}
		return null;
	}
}