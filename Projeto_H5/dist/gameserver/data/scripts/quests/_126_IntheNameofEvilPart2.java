package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _126_IntheNameofEvilPart2 extends Quest implements ScriptFile
{
	private int Mushika = 32114;
	private int Asamah = 32115;
	private int UluKaimu = 32119;
	private int BaluKaimu = 32120;
	private int ChutaKaimu = 32121;
	private int WarriorGrave = 32122;
	private int ShilenStoneStatue = 32109;

	private int BONEPOWDER = 8783;
	private int EPITAPH = 8781;
	private int EWA = 729;

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

	public _126_IntheNameofEvilPart2()
	{
		super(false);

		addStartNpc(Asamah);
		addTalkId(Mushika);
		addTalkId(UluKaimu);
		addTalkId(BaluKaimu);
		addTalkId(ChutaKaimu);
		addTalkId(WarriorGrave);
		addTalkId(ShilenStoneStatue);
		addQuestItem(BONEPOWDER, EPITAPH);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("asamah_q126_4.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.takeAllItems(EPITAPH);
		}
		else if (event.equalsIgnoreCase("asamah_q126_7.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("ulukaimu_q126_2.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("ulukaimu_q126_8.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("ulukaimu_q126_10.htm"))
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("balukaimu_q126_2.htm"))
		{
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("balukaimu_q126_7.htm"))
		{
			st.setCond(7);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("balukaimu_q126_9.htm"))
		{
			st.setCond(8);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("chutakaimu_q126_2.htm"))
		{
			st.setCond(9);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("chutakaimu_q126_9.htm"))
		{
			st.setCond(10);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("chutakaimu_q126_14.htm"))
		{
			st.setCond(11);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("warriorgrave_q126_2.htm"))
		{
			st.setCond(12);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("warriorgrave_q126_10.htm"))
		{
			st.setCond(13);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("warriorgrave_q126_19.htm"))
		{
			st.setCond(14);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("warriorgrave_q126_20.htm"))
		{
			st.setCond(15);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("warriorgrave_q126_23.htm"))
		{
			st.setCond(16);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("warriorgrave_q126_25.htm"))
		{
			st.setCond(17);
			st.giveItems(BONEPOWDER, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("warriorgrave_q126_27.htm"))
		{
			st.setCond(18);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("shilenstatue_q126_2.htm"))
		{
			st.setCond(19);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("shilenstatue_q126_13.htm"))
		{
			st.setCond(20);
			st.takeAllItems(BONEPOWDER);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("asamah_q126_10.htm"))
		{
			st.setCond(21);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("asamah_q126_17.htm"))
		{
			st.setCond(22);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("mushika_q126_3.htm"))
		{
			st.setCond(23);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("mushika_q126_4.htm"))
		{
			st.giveItems(EWA, 1);
			st.giveItems(ADENA_ID, 460483);
			st.addExpAndSp(1015973, 102802);
			st.playSound(SOUND_FINISH);
			st.setState(COMPLETED);
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

		if (npcId == Asamah)
		{
			switch (cond)
			{
			case 0:
			{
				QuestState qs = st.getPlayer().getQuestState(_125_InTheNameOfEvilPart1.class);
				if (st.getPlayer().getLevel() >= 77 && qs != null && qs.isCompleted())
				{
					htmltext = "asamah_q126_1.htm";
				}
				else
				{
					htmltext = "asamah_q126_0.htm";
					st.exitCurrentQuest(true);
				}
				break;
			}
			case 1:
				htmltext = "asamah_q126_4.htm";
				break;
			case 20:
				htmltext = "asamah_q126_8.htm";
				break;
			case 21:
				htmltext = "asamah_q126_10.htm";
				break;
			case 22:
				htmltext = "asamah_q126_17.htm";
				break;
			default:
				htmltext = "asamah_q126_0a.htm";
				break;
			}
		}
		else if (npcId == UluKaimu)
		{
			switch (cond)
			{
			case 2:
				htmltext = "ulukaimu_q126_1.htm";
				break;
			case 3:
				htmltext = "ulukaimu_q126_2.htm";
				break;
			case 4:
				htmltext = "ulukaimu_q126_8.htm";
				break;
			case 5:
				htmltext = "ulukaimu_q126_10.htm";
				break;
			default:
				htmltext = "ulukaimu_q126_0.htm";
				break;
			}
		}
		else if (npcId == BaluKaimu)
		{
			switch (cond)
			{
			case 5:
				htmltext = "balukaimu_q126_1.htm";
				break;
			case 6:
				htmltext = "balukaimu_q126_2.htm";
				break;
			case 7:
				htmltext = "balukaimu_q126_7.htm";
				break;
			case 8:
				htmltext = "balukaimu_q126_9.htm";
				break;
			default:
				htmltext = "balukaimu_q126_0.htm";
				break;
			}
		}
		else if (npcId == ChutaKaimu)
		{
			switch (cond)
			{
			case 8:
				htmltext = "chutakaimu_q126_1.htm";
				break;
			case 9:
				htmltext = "chutakaimu_q126_2.htm";
				break;
			case 10:
				htmltext = "chutakaimu_q126_9.htm";
				break;
			case 11:
				htmltext = "chutakaimu_q126_14.htm";
				break;
			default:
				htmltext = "chutakaimu_q126_0.htm";
				break;
			}
		}
		else if (npcId == WarriorGrave)
		{
			switch (cond)
			{
			case 11:
				htmltext = "warriorgrave_q126_1.htm";
				break;
			case 12:
				htmltext = "warriorgrave_q126_2.htm";
				break;
			case 13:
				htmltext = "warriorgrave_q126_10.htm";
				break;
			case 14:
				htmltext = "warriorgrave_q126_19.htm";
				break;
			case 15:
				htmltext = "warriorgrave_q126_20.htm";
				break;
			case 16:
				htmltext = "warriorgrave_q126_23.htm";
				break;
			case 17:
				htmltext = "warriorgrave_q126_25.htm";
				break;
			case 18:
				htmltext = "warriorgrave_q126_27.htm";
				break;
			default:
				htmltext = "warriorgrave_q126_0.htm";
				break;
			}
		}
		else if (npcId == ShilenStoneStatue)
		{
			switch (cond)
			{
			case 18:
				htmltext = "shilenstatue_q126_1.htm";
				break;
			case 19:
				htmltext = "shilenstatue_q126_2.htm";
				break;
			case 20:
				htmltext = "shilenstatue_q126_13.htm";
				break;
			default:
				htmltext = "shilenstatue_q126_0.htm";
				break;
			}
		}
		else if (npcId == Mushika)
		{
			if (cond == 22)
			{
				htmltext = "mushika_q126_1.htm";
			}
			else if (cond == 23)
			{
				htmltext = "mushika_q126_3.htm";
			}
			else
			{
				htmltext = "mushika_q126_0.htm";
			}
		}

		return htmltext;
	}
}