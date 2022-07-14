package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _269_InventionAmbition extends Quest implements ScriptFile
{
	// NPC
	public final int INVENTOR_MARU = 32486;
	// MOBS
	public final int[] MOBS =
	{
		21124,
		// Red Eye Barbed Bat
		21125,
		// Northern Trimden
		21126,
		// Kerope Werewolf
		21127,
		// Northern Goblin
		21128,
		// Spine Golem
		21129,
		// Kerope Werewolf Chief
		21130,
		// Northern Goblin Leader
		21131,
		// Enchanted Spine Golem
	};
	// ITEMS
	public final int ENERGY_ORES = 10866;

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

	public _269_InventionAmbition()
	{
		super(false);
		addStartNpc(INVENTOR_MARU);
		addKillId(MOBS);
		addQuestItem(ENERGY_ORES);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("inventor_maru_q0269_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equals("inventor_maru_q0269_07.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound(SOUND_FINISH);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		long count = st.getQuestItemsCount(ENERGY_ORES);
		if (st.getState() == CREATED)
		{
			if (st.getPlayer().getLevel() < 18)
			{
				htmltext = "inventor_maru_q0269_02.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "inventor_maru_q0269_01.htm";
			}
		}
		else if (count > 0)
		{
			st.giveItems(ADENA_ID, count * 50 + 2044 * (count / 20), true);
			st.takeItems(ENERGY_ORES, -1);
			htmltext = "inventor_maru_q0269_06.htm";
		}
		else
		{
			htmltext = "inventor_maru_q0269_05.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getState() != STARTED)
		{
			return null;
		}
		if (Rnd.chance(60))
		{
			st.giveItems(ENERGY_ORES, 1, false);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}