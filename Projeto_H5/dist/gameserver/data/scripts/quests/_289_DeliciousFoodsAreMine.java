package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _289_DeliciousFoodsAreMine extends Quest implements ScriptFile
{
	private static final int GuardStan = 30200;
	private static final int FoulFruit = 15507;
	private static final int FullBarrelofSoup = 15712;
	private static final int EmptySoupBarrel = 15713;
	private static final int[] SelMahums =
	{
		22786,
		22787,
		22788
	};
	private static final int SelChef = 18908;

	public _289_DeliciousFoodsAreMine()
	{
		super(false);
		addStartNpc(GuardStan);
		addQuestItem(FoulFruit, FullBarrelofSoup, EmptySoupBarrel);
		addKillId(SelMahums);
		addKillId(SelChef);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("stan_q289_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(FoulFruit, 500);
		}
		else if (event.equalsIgnoreCase("stan_q289_05.htm"))
		{
			st.giveItems(FoulFruit, 500);
		}
		else if (event.equalsIgnoreCase("continue"))
		{
			htmltext = "stan_q289_11.htm";
		}
		else if (event.equalsIgnoreCase("quit"))
		{
			htmltext = "stan_q289_12.htm";
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("icarus"))
		{
			if (st.getQuestItemsCount(FullBarrelofSoup) < 500)
			{
				htmltext = "stan_q289_07.htm";
			}
			else
			{
				st.takeItems(FullBarrelofSoup, 500);
				switch (Rnd.get(1, 5))
				{
				case 1:
					st.giveItems(10377, 1);
					break;
				case 2:
					st.giveItems(10401, 3);
					break;
				case 3:
					st.giveItems(10401, 4);
					break;
				case 4:
					st.giveItems(10401, 5);
					break;
				case 5:
					st.giveItems(10401, 6);
					break;
				}
				st.playSound(SOUND_MIDDLE);
				htmltext = "stan_q289_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("moirai"))
		{
			if (st.getQuestItemsCount(FullBarrelofSoup) < 100)
			{
				htmltext = "stan_q289_09.htm";
			}
			else
			{
				st.takeItems(FullBarrelofSoup, 100);
				switch (Rnd.get(1, 18))
				{
				case 1:
					st.giveItems(15775, 1);
					break;
				case 2:
					st.giveItems(15778, 1);
					break;
				case 3:
					st.giveItems(15781, 1);
					break;
				case 4:
					st.giveItems(15784, 1);
					break;
				case 5:
					st.giveItems(15787, 1);
					break;
				case 6:
					st.giveItems(15791, 1);
					break;
				case 7:
					st.giveItems(15812, 1);
					break;
				case 8:
					st.giveItems(15813, 1);
					break;
				case 9:
					st.giveItems(15814, 1);
					break;
				case 10:
					st.giveItems(15645, 3);
					break;
				case 11:
					st.giveItems(15648, 3);
					break;
				case 12:
					st.giveItems(15651, 3);
					break;
				case 13:
					st.giveItems(15654, 3);
					break;
				case 14:
					st.giveItems(15657, 3);
					break;
				case 15:
					st.giveItems(15693, 3);
					break;
				case 16:
					st.giveItems(15772, 3);
					break;
				case 17:
					st.giveItems(15773, 3);
					break;
				case 18:
					st.giveItems(15774, 3);
					break;
				}
				st.playSound(SOUND_MIDDLE);
				htmltext = "stan_q289_10.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npc.getNpcId() == GuardStan)
		{
			if (cond == 0)
			{
				QuestState qs = st.getPlayer().getQuestState(_252_GoodSmell.class);
				if (st.getPlayer().getLevel() >= 82 && qs != null && qs.isCompleted())
				{
					htmltext = "stan_q289_01.htm";
				}
				else
				{
					htmltext = "stan_q289_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1 && (st.getQuestItemsCount(FullBarrelofSoup) + (st.getQuestItemsCount(EmptySoupBarrel) * 2)) < 100)
			{
				htmltext = "stan_q289_04.htm";
			}
			else if (cond == 1 && (st.getQuestItemsCount(FullBarrelofSoup) + (st.getQuestItemsCount(EmptySoupBarrel) * 2)) >= 100)
			{
				if (st.getQuestItemsCount(EmptySoupBarrel) >= 2)
				{
					st.giveItems(FullBarrelofSoup, st.getQuestItemsCount(EmptySoupBarrel) / 2);
					st.takeAllItems(EmptySoupBarrel);
				}
				htmltext = "stan_q289_06.htm";
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1)
		{
			if (ArrayUtils.contains(SelMahums, npc.getNpcId()) || npc.getNpcId() == SelChef)
			{
				if (!st.rollAndGive(FullBarrelofSoup, 1, 15))
				{
					st.rollAndGive(EmptySoupBarrel, 1, 100);
				}
			}
		}
		return null;
	}

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
}