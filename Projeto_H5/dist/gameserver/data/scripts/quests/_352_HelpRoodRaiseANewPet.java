package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _352_HelpRoodRaiseANewPet extends Quest implements ScriptFile
{
	// NPCs
	private static int Rood = 31067;
	// Mobs
	private static int Lienrik = 20786;
	private static int Lienrik_Lad = 20787;
	// Quest Items
	private static int LIENRIK_EGG1 = 5860;
	private static int LIENRIK_EGG2 = 5861;
	// Chances
	private static int LIENRIK_EGG1_Chance = 30;
	private static int LIENRIK_EGG2_Chance = 7;

	public _352_HelpRoodRaiseANewPet()
	{
		super(false);
		addStartNpc(Rood);
		addKillId(Lienrik);
		addKillId(Lienrik_Lad);
		addQuestItem(LIENRIK_EGG1);
		addQuestItem(LIENRIK_EGG2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("31067-04.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31067-09.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if (npc.getNpcId() != Rood)
		{
			return htmltext;
		}
		int _state = st.getState();

		if (_state == CREATED)
		{
			if (st.getPlayer().getLevel() < 39)
			{
				htmltext = "31067-00.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "31067-01.htm";
				st.setCond(0);
			}
		}
		else if (_state == STARTED)
		{
			long reward = st.getQuestItemsCount(LIENRIK_EGG1) * 209 + st.getQuestItemsCount(LIENRIK_EGG2) * 2050;
			if (reward > 0)
			{
				htmltext = "31067-08.htm";
				st.takeItems(LIENRIK_EGG1, -1);
				st.takeItems(LIENRIK_EGG2, -1);
				st.giveItems(ADENA_ID, reward);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				htmltext = "31067-05.htm";
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

		if (Rnd.chance(LIENRIK_EGG1_Chance))
		{
			qs.giveItems(LIENRIK_EGG1, 1);
			qs.playSound(SOUND_ITEMGET);
		}
		else if (Rnd.chance(LIENRIK_EGG2_Chance))
		{
			qs.giveItems(LIENRIK_EGG2, 1);
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