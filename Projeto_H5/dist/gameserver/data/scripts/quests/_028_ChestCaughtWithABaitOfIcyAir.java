package quests;

import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _028_ChestCaughtWithABaitOfIcyAir extends Quest implements ScriptFile
{
	int OFulle = 31572;
	int Kiki = 31442;

	int BigYellowTreasureChest = 6503;
	int KikisLetter = 7626;
	int ElvenRing = 881;

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

	public _028_ChestCaughtWithABaitOfIcyAir()
	{
		super(false);
		addStartNpc(OFulle);
		addTalkId(Kiki);
		addQuestItem(KikisLetter);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("fisher_ofulle_q0028_0104.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("fisher_ofulle_q0028_0201.htm"))
		{
			if (st.getQuestItemsCount(BigYellowTreasureChest) > 0)
			{
				st.setCond(2);
				st.takeItems(BigYellowTreasureChest, 1);
				st.giveItems(KikisLetter, 1);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				htmltext = "fisher_ofulle_q0028_0202.htm";
			}
		}
		else if (event.equals("mineral_trader_kiki_q0028_0301.htm"))
		{
			if (st.getQuestItemsCount(KikisLetter) == 1)
			{
				htmltext = "mineral_trader_kiki_q0028_0301.htm";
				st.takeItems(KikisLetter, -1);
				st.giveItems(ElvenRing, 1);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(false);
			}
			else
			{
				htmltext = "mineral_trader_kiki_q0028_0302.htm";
				st.exitCurrentQuest(true);
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
		int cond = st.getCond();
		if (npcId == OFulle)
		{
			if (id == CREATED)
			{
				if (st.getPlayer().getLevel() < 36)
				{
					htmltext = "fisher_ofulle_q0028_0101.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					QuestState OFullesSpecialBait = st.getPlayer().getQuestState(_051_OFullesSpecialBait.class);
					if (OFullesSpecialBait != null)
					{
						if (OFullesSpecialBait.isCompleted())
						{
							htmltext = "fisher_ofulle_q0028_0101.htm";
						}
						else
						{
							htmltext = "fisher_ofulle_q0028_0102.htm";
							st.exitCurrentQuest(true);
						}
					}
					else
					{
						htmltext = "fisher_ofulle_q0028_0103.htm";
						st.exitCurrentQuest(true);
					}
				}
			}
			else if (cond == 1)
			{
				htmltext = "fisher_ofulle_q0028_0105.htm";
				if (st.getQuestItemsCount(BigYellowTreasureChest) == 0)
				{
					htmltext = "fisher_ofulle_q0028_0106.htm";
				}
			}
			else if (cond == 2)
			{
				htmltext = "fisher_ofulle_q0028_0203.htm";
			}
		}
		else if (npcId == Kiki)
		{
			if (cond == 2)
			{
				htmltext = "mineral_trader_kiki_q0028_0201.htm";
			}
			else
			{
				htmltext = "mineral_trader_kiki_q0028_0302.htm";
			}
		}
		return htmltext;
	}
}
