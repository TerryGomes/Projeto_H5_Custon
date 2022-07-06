package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _267_WrathOfVerdure extends Quest implements ScriptFile
{
	// NPCs
	private static int Treant_Bremec = 31853;
	// Mobs
	private static int Goblin_Raider = 20325;
	// Quest Items
	private static int Goblin_Club = 1335;
	// Items
	private static int Silvery_Leaf = 1340;
	// Chances
	private static int Goblin_Club_Chance = 50;

	public _267_WrathOfVerdure()
	{
		super(false);
		addStartNpc(Treant_Bremec);
		addKillId(Goblin_Raider);
		addQuestItem(Goblin_Club);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("bri_mec_tran_q0267_03.htm") && _state == CREATED && st.getPlayer().getRace() == Race.elf && st.getPlayer().getLevel() >= 4)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("bri_mec_tran_q0267_06.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if (npc.getNpcId() != Treant_Bremec)
		{
			return htmltext;
		}
		int _state = st.getState();
		if (_state == CREATED)
		{
			if (st.getPlayer().getRace() != Race.elf)
			{
				htmltext = "bri_mec_tran_q0267_00.htm";
				st.exitCurrentQuest(true);
			}
			else if (st.getPlayer().getLevel() < 4)
			{
				htmltext = "bri_mec_tran_q0267_01.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "bri_mec_tran_q0267_02.htm";
				st.setCond(0);
			}
		}
		else if (_state == STARTED)
		{
			long Goblin_Club_Count = st.getQuestItemsCount(Goblin_Club);
			if (Goblin_Club_Count > 0)
			{
				htmltext = "bri_mec_tran_q0267_05.htm";
				st.takeItems(Goblin_Club, -1);
				st.giveItems(Silvery_Leaf, Goblin_Club_Count);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				htmltext = "bri_mec_tran_q0267_04.htm";
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() != STARTED)
		{
			return null;
		}

		if (Rnd.chance(Goblin_Club_Chance))
		{
			qs.giveItems(Goblin_Club, 1);
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
