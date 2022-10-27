package quests;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _648_AnIceMerchantsDream extends Quest implements ScriptFile
{
	// NPCs
	private static int Rafforty = 32020;
	private static int Ice_Shelf = 32023;
	// Items
	private static int Silver_Hemocyte = 8057;
	private static int Silver_Ice_Crystal = 8077;
	private static int Black_Ice_Crystal = 8078;
	// Chances
	private static int Silver_Hemocyte_Chance = 10;
	private static int Silver2Black_Chance = 30;

	private static List<Integer> silver2black = new ArrayList<Integer>();

	public _648_AnIceMerchantsDream()
	{
		super(true);
		addStartNpc(Rafforty);
		addStartNpc(Ice_Shelf);
		for (int i = 22080; i <= 22098; i++)
		{
			if (i != 22095)
			{
				addKillId(i);
			}
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("repre_q0648_04.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("repre_q0648_22.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		if (_state != STARTED)
		{
			return event;
		}

		long Silver_Ice_Crystal_Count = st.getQuestItemsCount(Silver_Ice_Crystal);
		long Black_Ice_Crystal_Count = st.getQuestItemsCount(Black_Ice_Crystal);

		if (event.equalsIgnoreCase("repre_q0648_14.htm"))
		{
			long reward = Silver_Ice_Crystal_Count * 300 + Black_Ice_Crystal_Count * 1200;
			if (reward > 0)
			{
				st.takeItems(Silver_Ice_Crystal, -1);
				st.takeItems(Black_Ice_Crystal, -1);
				st.giveItems(ADENA_ID, reward);
			}
			else
			{
				return "repre_q0648_15.htm";
			}
		}
		else if (event.equalsIgnoreCase("ice_lathe_q0648_06.htm"))
		{
			int char_obj_id = st.getPlayer().getObjectId();
			synchronized (silver2black)
			{
				if (silver2black.contains(char_obj_id))
				{
					return event;
				}
				else if (Silver_Ice_Crystal_Count > 0)
				{
					silver2black.add(char_obj_id);
				}
				else
				{
					return "cheat.htm";
				}
			}

			st.takeItems(Silver_Ice_Crystal, 1);
			st.playSound(SOUND_BROKEN_KEY);
		}
		else if (event.equalsIgnoreCase("ice_lathe_q0648_08.htm"))
		{
			Integer char_obj_id = st.getPlayer().getObjectId();
			synchronized (silver2black)
			{
				if (silver2black.contains(char_obj_id))
				{
					while (silver2black.contains(char_obj_id))
					{
						silver2black.remove(char_obj_id);
					}
				}
				else
				{
					return "cheat.htm";
				}
			}

			if (Rnd.chance(Silver2Black_Chance))
			{
				st.giveItems(Black_Ice_Crystal, 1);
				st.playSound(SOUND_ENCHANT_SUCESS);
			}
			else
			{
				st.playSound(SOUND_ENCHANT_FAILED);
				return "ice_lathe_q0648_09.htm";
			}
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int _state = st.getState();
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (_state == CREATED)
		{
			if (npcId == Rafforty)
			{
				if (st.getPlayer().getLevel() >= 53)
				{
					st.setCond(0);
					return "repre_q0648_03.htm";
				}
				st.exitCurrentQuest(true);
				return "repre_q0648_01.htm";
			}
			if (npcId == Ice_Shelf)
			{
				return "ice_lathe_q0648_01.htm";
			}
		}

		if (_state != STARTED)
		{
			return "noquest";
		}

		long Silver_Ice_Crystal_Count = st.getQuestItemsCount(Silver_Ice_Crystal);
		if (npcId == Ice_Shelf)
		{
			return Silver_Ice_Crystal_Count > 0 ? "ice_lathe_q0648_03.htm" : "ice_lathe_q0648_02.htm";
		}

		long Black_Ice_Crystal_Count = st.getQuestItemsCount(Black_Ice_Crystal);
		if (npcId == Rafforty)
		{
			QuestState st_115 = st.getPlayer().getQuestState(_115_TheOtherSideOfTruth.class);
			if (st_115 != null && st_115.isCompleted())
			{
				cond = 2;
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}

			if (cond == 1)
			{
				if (Silver_Ice_Crystal_Count > 0 || Black_Ice_Crystal_Count > 0)
				{
					return "repre_q0648_10.htm";
				}
				else
				{
					return "repre_q0648_08.htm";
				}
			}

			if (cond == 2)
			{
				return Silver_Ice_Crystal_Count > 0 || Black_Ice_Crystal_Count > 0 ? "repre_q0648_11.htm" : "repre_q0648_09.htm";
			}
		}

		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		int cond = qs.getCond();
		if (cond > 0)
		{
			qs.rollAndGive(Silver_Ice_Crystal, /* (int)Config.RATE_QUESTS_DROP */ 1, npc.getNpcId() - 22050);
			if (cond == 2)
			{
				qs.rollAndGive(Silver_Hemocyte, /* (int)Config.RATE_QUESTS_DROP */ 1, Silver_Hemocyte_Chance);
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