package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _275_BlackWingedSpies extends Quest implements ScriptFile
{
	// NPCs
	private static int Tantus = 30567;
	// Mobs
	private static int Darkwing_Bat = 20316;
	private static int Varangkas_Tracker = 27043;
	// Quest Items
	private static int Darkwing_Bat_Fang = 1478;
	private static int Varangkas_Parasite = 1479;
	// Chances
	private static int Varangkas_Parasite_Chance = 10;

	public _275_BlackWingedSpies()
	{
		super(false);
		addStartNpc(Tantus);
		addKillId(Darkwing_Bat);
		addKillId(Varangkas_Tracker);
		addQuestItem(Darkwing_Bat_Fang);
		addQuestItem(Varangkas_Parasite);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("neruga_chief_tantus_q0275_03.htm") && st.getState() == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (npc.getNpcId() != Tantus)
		{
			return "noquest";
		}
		int _state = st.getState();

		if (_state == CREATED)
		{
			if (st.getPlayer().getRace() != Race.orc)
			{
				st.exitCurrentQuest(true);
				return "neruga_chief_tantus_q0275_00.htm";
			}
			if (st.getPlayer().getLevel() < 11)
			{
				st.exitCurrentQuest(true);
				return "neruga_chief_tantus_q0275_01.htm";
			}
			st.setCond(0);
			return "neruga_chief_tantus_q0275_02.htm";
		}

		if (_state != STARTED)
		{
			return "noquest";
		}
		int cond = st.getCond();

		if (st.getQuestItemsCount(Darkwing_Bat_Fang) < 70)
		{
			if (cond != 1)
			{
				st.setCond(1);
			}
			return "neruga_chief_tantus_q0275_04.htm";
		}
		if (cond == 2)
		{
			st.giveItems(ADENA_ID, 4550);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return "neruga_chief_tantus_q0275_05.htm";
		}
		return "noquest";
	}

	private static void spawn_Varangkas_Tracker(QuestState st)
	{
		if (st.getQuestItemsCount(Varangkas_Parasite) > 0)
		{
			st.takeItems(Varangkas_Parasite, -1);
		}
		st.giveItems(Varangkas_Parasite, 1);
		st.addSpawn(Varangkas_Tracker);
	}

	public static void give_Darkwing_Bat_Fang(QuestState st, long _count)
	{
		long max_inc = 70 - st.getQuestItemsCount(Darkwing_Bat_Fang);
		if (max_inc < 1)
		{
			return;
		}
		if (_count > max_inc)
		{
			_count = max_inc;
		}
		st.giveItems(Darkwing_Bat_Fang, _count);
		st.playSound(_count == max_inc ? SOUND_MIDDLE : SOUND_ITEMGET);
		if (_count == max_inc)
		{
			st.setCond(2);
		}
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() != STARTED)
		{
			return null;
		}
		int npcId = npc.getNpcId();
		long Darkwing_Bat_Fang_count = qs.getQuestItemsCount(Darkwing_Bat_Fang);

		if (npcId == Darkwing_Bat && Darkwing_Bat_Fang_count < 70)
		{
			if (Darkwing_Bat_Fang_count > 10 && Darkwing_Bat_Fang_count < 65 && Rnd.chance(Varangkas_Parasite_Chance))
			{
				spawn_Varangkas_Tracker(qs);
				return null;
			}
			give_Darkwing_Bat_Fang(qs, 1);
		}
		else if (npcId == Varangkas_Tracker && Darkwing_Bat_Fang_count < 70 && qs.getQuestItemsCount(Varangkas_Parasite) > 0)
		{
			qs.takeItems(Varangkas_Parasite, -1);
			give_Darkwing_Bat_Fang(qs, 5);
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