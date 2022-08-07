package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _270_TheOneWhoEndsSilence extends Quest implements ScriptFile
{
	private static final int Greymore = 32757;
	private static final int TatteredMonkClothes = 15526;
	private static final int[] LowMobs =
	{
		22791,
		22790,
		22793
	};
	private static final int[] HighMobs =
	{
		22794,
		22795,
		22797,
		22798,
		22799,
		22800
	};

	public _270_TheOneWhoEndsSilence()
	{
		super(false);
		addStartNpc(Greymore);
		addKillId(LowMobs);
		addKillId(HighMobs);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("greymore_q270_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("showrags"))
		{
			if (st.getQuestItemsCount(TatteredMonkClothes) < 1)
			{
				htmltext = "greymore_q270_05.htm";
			}
			else if (st.getQuestItemsCount(TatteredMonkClothes) < 100)
			{
				htmltext = "greymore_q270_06.htm";
			}
			else if (st.getQuestItemsCount(TatteredMonkClothes) >= 100)
			{
				htmltext = "greymore_q270_07.htm";
			}
		}
		else if (event.equalsIgnoreCase("rags100"))
		{
			if (st.getQuestItemsCount(TatteredMonkClothes) >= 100)
			{
				st.takeItems(TatteredMonkClothes, 100);
				switch (Rnd.get(1, 21))
				{
				// Recipes
				case 1:
					st.giveItems(10373, 1);
					break;
				case 2:
					st.giveItems(10374, 1);
					break;
				case 3:
					st.giveItems(10375, 1);
					break;
				case 4:
					st.giveItems(10376, 1);
					break;
				case 5:
					st.giveItems(10377, 1);
					break;
				case 6:
					st.giveItems(10378, 1);
					break;
				case 7:
					st.giveItems(10379, 1);
					break;
				case 8:
					st.giveItems(10380, 1);
					break;
				case 9:
					st.giveItems(10381, 1);
					break;
				// Material
				case 10:
					st.giveItems(10397, 1);
					break;
				case 11:
					st.giveItems(10398, 1);
					break;
				case 12:
					st.giveItems(10399, 1);
					break;
				case 13:
					st.giveItems(10400, 1);
					break;
				case 14:
					st.giveItems(10401, 1);
					break;
				case 15:
					st.giveItems(10402, 1);
					break;
				case 16:
					st.giveItems(10403, 1);
					break;
				case 17:
					st.giveItems(10405, 1);
					break;
				// SP Scrolls
				case 18:
					st.giveItems(5593, 1);
					break;
				case 19:
					st.giveItems(5594, 1);
					break;
				case 20:
					st.giveItems(5595, 1);
					break;
				case 21:
					st.giveItems(9898, 1);
					break;
				}
				htmltext = "greymore_q270_09.htm";
			}
			else
			{
				htmltext = "greymore_q270_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("rags200"))
		{
			if (st.getQuestItemsCount(TatteredMonkClothes) >= 200)
			{
				st.takeItems(TatteredMonkClothes, 200);
				switch (Rnd.get(1, 17))
				{
				// Recipes
				case 1:
					st.giveItems(10373, 1);
					break;
				case 2:
					st.giveItems(10374, 1);
					break;
				case 3:
					st.giveItems(10375, 1);
					break;
				case 4:
					st.giveItems(10376, 1);
					break;
				case 5:
					st.giveItems(10377, 1);
					break;
				case 6:
					st.giveItems(10378, 1);
					break;
				case 7:
					st.giveItems(10379, 1);
					break;
				case 8:
					st.giveItems(10380, 1);
					break;
				case 9:
					st.giveItems(10381, 1);
					break;
				// Material
				case 10:
					st.giveItems(10397, 1);
					break;
				case 11:
					st.giveItems(10398, 1);
					break;
				case 12:
					st.giveItems(10399, 1);
					break;
				case 13:
					st.giveItems(10400, 1);
					break;
				case 14:
					st.giveItems(10401, 1);
					break;
				case 15:
					st.giveItems(10402, 1);
					break;
				case 16:
					st.giveItems(10403, 1);
					break;
				case 17:
					st.giveItems(10405, 1);
					break;
				}
				switch (Rnd.get(1, 4))
				{
				// SP Scrolls
				case 1:
					st.giveItems(5593, 1);
					break;
				case 2:
					st.giveItems(5594, 1);
					break;
				case 3:
					st.giveItems(5595, 1);
					break;
				case 4:
					st.giveItems(9898, 1);
					break;
				}
				htmltext = "greymore_q270_09.htm";
			}
			else
			{
				htmltext = "greymore_q270_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("rags300"))
		{
			if (st.getQuestItemsCount(TatteredMonkClothes) >= 300)
			{
				st.takeItems(TatteredMonkClothes, 300);
				switch (Rnd.get(1, 9))
				{
				// Recipes
				case 1:
					st.giveItems(10373, 1);
					break;
				case 2:
					st.giveItems(10374, 1);
					break;
				case 3:
					st.giveItems(10375, 1);
					break;
				case 4:
					st.giveItems(10376, 1);
					break;
				case 5:
					st.giveItems(10377, 1);
					break;
				case 6:
					st.giveItems(10378, 1);
					break;
				case 7:
					st.giveItems(10379, 1);
					break;
				case 8:
					st.giveItems(10380, 1);
					break;
				case 9:
					st.giveItems(10381, 1);
					break;
				}
				switch (Rnd.get(10, 17))
				{
				// Material
				case 10:
					st.giveItems(10397, 1);
					break;
				case 11:
					st.giveItems(10398, 1);
					break;
				case 12:
					st.giveItems(10399, 1);
					break;
				case 13:
					st.giveItems(10400, 1);
					break;
				case 14:
					st.giveItems(10401, 1);
					break;
				case 15:
					st.giveItems(10402, 1);
					break;
				case 16:
					st.giveItems(10403, 1);
					break;
				case 17:
					st.giveItems(10405, 1);
					break;
				}
				switch (Rnd.get(1, 4))
				{
				// SP Scrolls
				case 1:
					st.giveItems(5593, 1);
					break;
				case 2:
					st.giveItems(5594, 1);
					break;
				case 3:
					st.giveItems(5595, 1);
					break;
				case 4:
					st.giveItems(9898, 1);
					break;
				}
				htmltext = "greymore_q270_09.htm";
			}
			else
			{
				htmltext = "greymore_q270_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("rags400"))
		{
			if (st.getQuestItemsCount(TatteredMonkClothes) >= 400)
			{
				st.takeItems(TatteredMonkClothes, 400);
				switch (Rnd.get(1, 9))
				{
				// Recipes
				case 1:
					st.giveItems(10373, 1);
					break;
				case 2:
					st.giveItems(10374, 1);
					break;
				case 3:
					st.giveItems(10375, 1);
					break;
				case 4:
					st.giveItems(10376, 1);
					break;
				case 5:
					st.giveItems(10377, 1);
					break;
				case 6:
					st.giveItems(10378, 1);
					break;
				case 7:
					st.giveItems(10379, 1);
					break;
				case 8:
					st.giveItems(10380, 1);
					break;
				case 9:
					st.giveItems(10381, 1);
					break;
				}
				switch (Rnd.get(10, 17))
				{
				// Material
				case 10:
					st.giveItems(10397, 1);
					break;
				case 11:
					st.giveItems(10398, 1);
					break;
				case 12:
					st.giveItems(10399, 1);
					break;
				case 13:
					st.giveItems(10400, 1);
					break;
				case 14:
					st.giveItems(10401, 1);
					break;
				case 15:
					st.giveItems(10402, 1);
					break;
				case 16:
					st.giveItems(10403, 1);
					break;
				case 17:
					st.giveItems(10405, 1);
					break;
				}
				switch (Rnd.get(1, 4))
				{
				// SP Scrolls
				case 1:
					st.giveItems(5593, 2);
					break;
				case 2:
					st.giveItems(5594, 2);
					break;
				case 3:
					st.giveItems(5595, 2);
					break;
				case 4:
					st.giveItems(9898, 2);
					break;
				}
				htmltext = "greymore_q270_09.htm";
			}
			else
			{
				htmltext = "greymore_q270_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("rags500"))
		{
			if (st.getQuestItemsCount(TatteredMonkClothes) >= 500)
			{
				st.takeItems(TatteredMonkClothes, 500);
				switch (Rnd.get(1, 9))
				{
				// Recipes
				case 1:
					st.giveItems(10373, 2);
					break;
				case 2:
					st.giveItems(10374, 2);
					break;
				case 3:
					st.giveItems(10375, 2);
					break;
				case 4:
					st.giveItems(10376, 2);
					break;
				case 5:
					st.giveItems(10377, 2);
					break;
				case 6:
					st.giveItems(10378, 2);
					break;
				case 7:
					st.giveItems(10379, 2);
					break;
				case 8:
					st.giveItems(10380, 2);
					break;
				case 9:
					st.giveItems(10381, 2);
					break;
				}
				switch (Rnd.get(10, 17))
				{
				// Material
				case 10:
					st.giveItems(10397, 2);
					break;
				case 11:
					st.giveItems(10398, 2);
					break;
				case 12:
					st.giveItems(10399, 2);
					break;
				case 13:
					st.giveItems(10400, 2);
					break;
				case 14:
					st.giveItems(10401, 2);
					break;
				case 15:
					st.giveItems(10402, 2);
					break;
				case 16:
					st.giveItems(10403, 2);
					break;
				case 17:
					st.giveItems(10405, 2);
					break;
				}
				switch (Rnd.get(1, 4))
				{
				// SP Scrolls
				case 1:
					st.giveItems(5593, 1);
					break;
				case 2:
					st.giveItems(5594, 1);
					break;
				case 3:
					st.giveItems(5595, 1);
					break;
				case 4:
					st.giveItems(9898, 1);
					break;
				}
				htmltext = "greymore_q270_09.htm";
			}
			else
			{
				htmltext = "greymore_q270_08.htm";
			}
		}
		else if (event.equalsIgnoreCase("quit"))
		{
			htmltext = "greymore_q270_10.htm";
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npc.getNpcId() == Greymore)
		{
			if (cond == 0)
			{
				QuestState qs = st.getPlayer().getQuestState(_10288_SecretMission.class);
				if (st.getPlayer().getLevel() >= 82 && qs != null && qs.isCompleted())
				{
					htmltext = "greymore_q270_01.htm";
				}
				else
				{
					htmltext = "greymore_q270_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "greymore_q270_04.htm";
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
			if ((ArrayUtils.contains(LowMobs, npc.getNpcId()) && Rnd.chance(40)) || ArrayUtils.contains(HighMobs, npc.getNpcId()))
			{
				st.giveItems(TatteredMonkClothes, 1, true);
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