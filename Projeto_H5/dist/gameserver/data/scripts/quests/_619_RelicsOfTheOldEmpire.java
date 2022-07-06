package quests;

import java.util.HashMap;
import java.util.Map;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _619_RelicsOfTheOldEmpire extends Quest implements ScriptFile
{
	// Items
	private static final int Entrance_Pass_to_the_Sepulcher = 7075;
	private static final int Broken_Relic_Part = 7254;
	// NPCs
	private static final int GHOST = 31538;

	private static final Map<Integer, Integer> drops = new HashMap<Integer, Integer>();

	static
	{
		drops.put(18120, 138);
		drops.put(18121, 131);
		drops.put(18122, 100);
		drops.put(18123, 138);
		drops.put(18124, 131);
		drops.put(18125, 100);
		drops.put(18126, 138);
		drops.put(18127, 131);
		drops.put(18128, 100);
		drops.put(18129, 138);
		drops.put(18130, 131);
		drops.put(18131, 100);
		drops.put(18132, 141);
		drops.put(18133, 130);
		drops.put(18134, 98);
		drops.put(18135, 130);
		drops.put(18136, 130);
		drops.put(18137, 96);
		drops.put(18138, 129);
		drops.put(18139, 127);
		drops.put(18140, 128);
		drops.put(18141, 64);
		drops.put(18142, 64);
		drops.put(18143, 64);
		drops.put(18144, 64);
		drops.put(18145, 53);
		drops.put(18146, 56);
		drops.put(18147, 51);
		drops.put(18148, 60);
		drops.put(18149, 53);
		drops.put(18166, 99);
		drops.put(18167, 98);
		drops.put(18168, 101);
		drops.put(18169, 97);
		drops.put(18170, 97);
		drops.put(18171, 101);
		drops.put(18172, 96);
		drops.put(18173, 100);
		drops.put(18174, 132);
		drops.put(18175, 101);
		drops.put(18176, 100);
		drops.put(18177, 132);
		drops.put(18178, 101);
		drops.put(18179, 100);
		drops.put(18180, 132);
		drops.put(18181, 101);
		drops.put(18182, 100);
		drops.put(18183, 132);
		drops.put(18184, 101);
		drops.put(18185, 133);
		drops.put(18186, 134);
		drops.put(18187, 130);
		drops.put(18188, 98);
		drops.put(18189, 130);
		drops.put(18190, 130);
		drops.put(18191, 96);
		drops.put(18192, 129);
		drops.put(18193, 127);
		drops.put(18194, 128);
		drops.put(18195, 98);
		drops.put(18212, 510);
		drops.put(18213, 510);
		drops.put(18214, 510);
		drops.put(18215, 510);
		drops.put(18216, 510);
		drops.put(18217, 510);
		drops.put(18218, 510);
		drops.put(18219, 510);
		drops.put(18220, 134);
		drops.put(18221, 138);
		drops.put(18222, 131);
		drops.put(18223, 98);
		drops.put(18224, 132);
		drops.put(18225, 131);
		drops.put(18226, 96);
		drops.put(18227, 166);
		drops.put(18228, 125);
		drops.put(18229, 128);
		drops.put(18230, 53);
		drops.put(21396, 36);
		drops.put(21397, 39);
		drops.put(21398, 48);
		drops.put(21399, 62);
		drops.put(21400, 42);
		drops.put(21401, 48);
		drops.put(21402, 47);
		drops.put(21403, 49);
		drops.put(21404, 34);
		drops.put(21405, 36);
		drops.put(21406, 61);
		drops.put(21407, 60);
		drops.put(21408, 70);
		drops.put(21409, 70);
		drops.put(21410, 45);
		drops.put(21411, 46);
		drops.put(21412, 52);
		drops.put(21413, 52);
		drops.put(21414, 51);
		drops.put(21415, 51);
		drops.put(21416, 83);
		drops.put(21417, 83);
		drops.put(21418, 43);
		drops.put(21419, 36);
		drops.put(21420, 63);
		drops.put(21421, 53);
		drops.put(21422, 68);
		drops.put(21423, 69);
		drops.put(21424, 89);
		drops.put(21425, 69);
		drops.put(21426, 38);
		drops.put(21427, 49);
		drops.put(21428, 55);
		drops.put(21429, 65);
		drops.put(21430, 70);
		drops.put(21431, 91);
		drops.put(21432, 156);
		drops.put(21433, 66);
		drops.put(21434, 135);
		drops.put(21435, 67);
		drops.put(21436, 67);
		drops.put(21437, 17);
		drops.put(21798, 36);
		drops.put(21799, 52);
		drops.put(21800, 31);
	}

	public static final int[] Recipes =
	{
		6881,
		6883,
		6885,
		6887,
		7580,
		6891,
		6893,
		6895,
		6897,
		6899
	};

	public _619_RelicsOfTheOldEmpire()
	{
		super(true);
		addStartNpc(GHOST);
		addKillId(drops.keySet());
		addQuestItem(Broken_Relic_Part);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equals("explorer_ghost_a_q0619_03.htm"))
		{
			if (st.getPlayer().getLevel() < 74)
			{
				st.exitCurrentQuest(true);
				return "explorer_ghost_a_q0619_02.htm";
			}
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("explorer_ghost_a_q0619_09.htm"))
		{
			if (st.getQuestItemsCount(Broken_Relic_Part) < 1000)
			{
				return st.getQuestItemsCount(Entrance_Pass_to_the_Sepulcher) > 0 ? "explorer_ghost_a_q0619_06.htm" : "explorer_ghost_a_q0619_07.htm";
			}
			st.takeItems(Broken_Relic_Part, 1000);
			st.giveItems(Recipes[Rnd.get(Recipes.length)], 1);
			return "explorer_ghost_a_q0619_09.htm";
		}
		else if (event.equals("explorer_ghost_a_q0619_10.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (st.getState() == CREATED)
		{
			if (st.getPlayer().getLevel() < 74)
			{
				st.exitCurrentQuest(true);
				return "explorer_ghost_a_q0619_02.htm";
			}
			st.setCond(0);
			return "explorer_ghost_a_q0619_01.htm";
		}

		if (st.getQuestItemsCount(Broken_Relic_Part) >= 1000)
		{
			return "explorer_ghost_a_q0619_04.htm";
		}

		return st.getQuestItemsCount(Entrance_Pass_to_the_Sepulcher) > 0 ? "explorer_ghost_a_q0619_06.htm" : "explorer_ghost_a_q0619_07.htm";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		Integer Broken_Relic_Part_chance = drops.get(npcId);
		if (Broken_Relic_Part_chance == null)
		{
			return null;
		}

		st.rollAndGive(Broken_Relic_Part, 1, Broken_Relic_Part_chance);
		if (npcId > 20000) // npcId < 20000 тут это мобы из Four Goblets, из них билет в Sepulcher не должен падать
		{
			st.rollAndGive(Entrance_Pass_to_the_Sepulcher, 1, 3);
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