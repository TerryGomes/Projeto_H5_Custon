package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _134_TempleMissionary extends Quest implements ScriptFile
{
	// NPCs
	private final static int Glyvka = 30067;
	private final static int Rouke = 31418;
	// Mobs
	private final static int Cruma_Marshlands_Traitor = 27339;
	private final static int[] mobs =
	{
		20157,
		20229,
		20230,
		20231,
		20232,
		20233,
		20234
	};
	// Quest Items
	private final static int Giants_Experimental_Tool_Fragment = 10335;
	private final static int Giants_Experimental_Tool = 10336;
	private final static int Giants_Technology_Report = 10337;
	private final static int Roukes_Report = 10338;
	// Items
	private final static int Badge_Temple_Missionary = 10339;
	// Chances
	private final static int Giants_Experimental_Tool_Fragment_chance = 66;
	private final static int Cruma_Marshlands_Traitor_spawnchance = 45;

	public _134_TempleMissionary()
	{
		super(false);
		addStartNpc(Glyvka);
		addTalkId(Rouke);
		addKillId(mobs);
		addKillId(Cruma_Marshlands_Traitor);
		addQuestItem(Giants_Experimental_Tool_Fragment);
		addQuestItem(Giants_Experimental_Tool);
		addQuestItem(Giants_Technology_Report);
		addQuestItem(Roukes_Report);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("glyvka_q0134_03.htm") && _state == CREATED)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("glyvka_q0134_06.htm") && _state == STARTED)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("glyvka_q0134_11.htm") && _state == STARTED && st.getCond() == 5)
		{
			st.playSound(SOUND_FINISH);
			st.unset("Report");
			st.giveItems(ADENA_ID, 15100);
			st.giveItems(Badge_Temple_Missionary, 1);
			st.addExpAndSp(30000, 2000);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("scroll_seller_rouke_q0134_03.htm") && _state == STARTED)
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("scroll_seller_rouke_q0134_09.htm") && _state == STARTED && st.getInt("Report") == 1)
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
			st.giveItems(Roukes_Report, 1);
			st.unset("Report");
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int _state = st.getState();
		if (_state == COMPLETED)
		{
			return "completed";
		}

		int npcId = npc.getNpcId();
		if (_state == CREATED)
		{
			if (npcId != Glyvka)
			{
				return "noquest";
			}
			if (st.getPlayer().getLevel() < 35)
			{
				st.exitCurrentQuest(true);
				return "glyvka_q0134_02.htm";
			}
			st.setCond(0);
			return "glyvka_q0134_01.htm";
		}

		int cond = st.getCond();

		if (npcId == Glyvka && _state == STARTED)
		{
			if (cond == 1)
			{
				return "glyvka_q0134_03.htm";
			}
			if (cond == 5)
			{
				if (st.getInt("Report") == 1)
				{
					return "glyvka_q0134_09.htm";
				}
				if (st.getQuestItemsCount(Roukes_Report) > 0)
				{
					st.takeItems(Roukes_Report, -1);
					st.set("Report", "1");
					return "glyvka_q0134_08.htm";
				}
				return "noquest";
			}
			return "glyvka_q0134_07.htm";
		}

		if (npcId == Rouke && _state == STARTED)
		{
			switch (cond)
			{
			case 2:
				return "scroll_seller_rouke_q0134_02.htm";
			case 5:
				return "scroll_seller_rouke_q0134_10.htm";
			case 3:
			{
				long Tools = st.getQuestItemsCount(Giants_Experimental_Tool_Fragment) / 10;
				if (Tools < 1)
				{
					return "scroll_seller_rouke_q0134_04.htm";
				}
				st.takeItems(Giants_Experimental_Tool_Fragment, Tools * 10);
				st.giveItems(Giants_Experimental_Tool, Tools);
				return "scroll_seller_rouke_q0134_05.htm";
			}
			case 4:
				if (st.getInt("Report") == 1)
				{
					return "scroll_seller_rouke_q0134_07.htm";
				}
				if (st.getQuestItemsCount(Giants_Technology_Report) > 2)
				{
					st.takeItems(Giants_Experimental_Tool_Fragment, -1);
					st.takeItems(Giants_Experimental_Tool, -1);
					st.takeItems(Giants_Technology_Report, -1);
					st.set("Report", "1");
					return "scroll_seller_rouke_q0134_06.htm";
				}
				return "noquest";
			default:
				break;
			}
		}

		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() == STARTED && qs.getCond() == 3)
		{
			if (npc.getNpcId() == Cruma_Marshlands_Traitor)
			{
				qs.giveItems(Giants_Technology_Report, 1);
				if (qs.getQuestItemsCount(Giants_Technology_Report) < 3)
				{
					qs.playSound(SOUND_ITEMGET);
				}
				else
				{
					qs.playSound(SOUND_MIDDLE);
					qs.setCond(4);
				}
			}
			else if (qs.getQuestItemsCount(Giants_Experimental_Tool) < 1)
			{
				if (Rnd.chance(Giants_Experimental_Tool_Fragment_chance))
				{
					qs.giveItems(Giants_Experimental_Tool_Fragment, 1);
				}
			}
			else
			{
				qs.takeItems(Giants_Experimental_Tool, 1);
				if (Rnd.chance(Cruma_Marshlands_Traitor_spawnchance))
				{
					qs.addSpawn(Cruma_Marshlands_Traitor, qs.getPlayer().getX(), qs.getPlayer().getY(), qs.getPlayer().getZ(), 0, 100, 900000);
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