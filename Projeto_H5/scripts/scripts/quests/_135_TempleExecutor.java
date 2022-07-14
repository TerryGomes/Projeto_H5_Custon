package quests;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _135_TempleExecutor extends Quest implements ScriptFile
{
	// NPCs
	private final static int Shegfield = 30068;
	private final static int Pano = 30078;
	private final static int Alex = 30291;
	private final static int Sonin = 31773;

	// Mobs
	private final static int[] mobs =
	{
		20781,
		21104,
		21105,
		21106,
		21107
	};

	// Quest Items
	private final static int Stolen_Cargo = 10328;
	private final static int Hate_Crystal = 10329;
	private final static int Old_Treasure_Map = 10330;
	private final static int Sonins_Credentials = 10331;
	private final static int Panos_Credentials = 10332;
	private final static int Alexs_Credentials = 10333;

	// Items
	private final static int Badge_Temple_Executor = 10334;

	public _135_TempleExecutor()
	{
		super(false);

		addStartNpc(Shegfield);
		addTalkId(Alex);
		addTalkId(Sonin);
		addTalkId(Pano);
		addKillId(mobs);
		addQuestItem(Stolen_Cargo);
		addQuestItem(Hate_Crystal);
		addQuestItem(Old_Treasure_Map);
		addQuestItem(Sonins_Credentials);
		addQuestItem(Panos_Credentials);
		addQuestItem(Alexs_Credentials);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("shegfield_q0135_03.htm") && _state == CREATED)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("shegfield_q0135_13.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.unset("Report");
			st.giveItems(ADENA_ID, 16924);
			st.addExpAndSp(30000, 2000);
			st.giveItems(Badge_Temple_Executor, 1);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("shegfield_q0135_04.htm") && _state == STARTED)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("alankell_q0135_07.htm") && _state == STARTED)
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
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
			if (npcId != Shegfield)
			{
				return "noquest";
			}
			if (st.getPlayer().getLevel() < 35)
			{
				st.exitCurrentQuest(true);
				return "shegfield_q0135_02.htm";
			}
			st.setCond(0);
			return "shegfield_q0135_01.htm";
		}

		int cond = st.getCond();

		if (npcId == Shegfield && _state == STARTED)
		{
			if (cond == 1)
			{
				return "shegfield_q0135_03.htm";
			}
			if (cond == 5)
			{
				if (st.getInt("Report") == 1)
				{
					return "shegfield_q0135_09.htm";
				}
				if (st.getQuestItemsCount(Sonins_Credentials) > 0 && st.getQuestItemsCount(Panos_Credentials) > 0 && st.getQuestItemsCount(Alexs_Credentials) > 0)
				{
					st.takeItems(Panos_Credentials, -1);
					st.takeItems(Sonins_Credentials, -1);
					st.takeItems(Alexs_Credentials, -1);
					st.set("Report", "1");
					return "shegfield_q0135_08.htm";
				}
				return "noquest";
			}
			return "shegfield_q0135_06.htm";
		}

		if (npcId == Alex && _state == STARTED)
		{
			switch (cond)
			{
			case 2:
				return "alankell_q0135_02.htm";
			case 3:
				return "alankell_q0135_08.htm";
			case 4:
				if (st.getQuestItemsCount(Sonins_Credentials) > 0 && st.getQuestItemsCount(Panos_Credentials) > 0)
				{
					st.setCond(5);
					st.takeItems(Old_Treasure_Map, -1);
					st.giveItems(Alexs_Credentials, 1);
					st.playSound(SOUND_MIDDLE);
					return "alankell_q0135_10.htm";
				}
				return "alankell_q0135_09.htm";
			case 5:
				return "alankell_q0135_11.htm";
			default:
				break;
			}
		}

		if (npcId == Sonin && _state == STARTED)
		{
			if (st.getQuestItemsCount(Stolen_Cargo) < 10)
			{
				return "warehouse_keeper_sonin_q0135_04.htm";
			}
			st.takeItems(Stolen_Cargo, -1);
			st.giveItems(Sonins_Credentials, 1);
			st.playSound(SOUND_MIDDLE);
			return "warehouse_keeper_sonin_q0135_03.htm";
		}

		if (npcId == Pano && _state == STARTED && cond == 4)
		{
			if (st.getQuestItemsCount(Hate_Crystal) < 10)
			{
				return "pano_q0135_04.htm";
			}
			st.takeItems(Hate_Crystal, -1);
			st.giveItems(Panos_Credentials, 1);
			st.playSound(SOUND_MIDDLE);
			return "pano_q0135_03.htm";
		}

		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() == STARTED && qs.getCond() == 3)
		{
			List<Integer> drops = new ArrayList<Integer>();
			if (qs.getQuestItemsCount(Stolen_Cargo) < 10)
			{
				drops.add(Stolen_Cargo);
			}
			if (qs.getQuestItemsCount(Hate_Crystal) < 10)
			{
				drops.add(Hate_Crystal);
			}
			if (qs.getQuestItemsCount(Old_Treasure_Map) < 10)
			{
				drops.add(Old_Treasure_Map);
			}
			if (drops.isEmpty())
			{
				return null;
			}
			int drop = drops.get(Rnd.get(drops.size()));
			qs.giveItems(drop, 1);
			if (drops.size() == 1 && qs.getQuestItemsCount(drop) >= 10)
			{
				qs.setCond(4);
				qs.playSound(SOUND_MIDDLE);
				return null;
			}
			qs.playSound(SOUND_ITEMGET);
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