package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _324_SweetestVenom extends Quest implements ScriptFile
{
	// NPCs
	private static int ASTARON = 30351;
	// Mobs
	private static int Prowler = 20034;
	private static int Venomous_Spider = 20038;
	private static int Arachnid_Tracker = 20043;
	// Items
	private static int VENOM_SAC = 1077;
	// Chances
	private static int VENOM_SAC_BASECHANCE = 60;

	public _324_SweetestVenom()
	{
		super(false);
		addStartNpc(ASTARON);
		addKillId(Prowler);
		addKillId(Venomous_Spider);
		addKillId(Arachnid_Tracker);
		addQuestItem(VENOM_SAC);
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		if (npc.getNpcId() != ASTARON)
		{
			return htmltext;
		}
		int _state = st.getState();

		if (_state == CREATED)
		{
			if (st.getPlayer().getLevel() >= 18)
			{
				htmltext = "astaron_q0324_03.htm";
				st.setCond(0);
			}
			else
			{
				htmltext = "astaron_q0324_02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (_state == STARTED)
		{
			long _count = st.getQuestItemsCount(VENOM_SAC);
			if (_count >= 10)
			{
				htmltext = "astaron_q0324_06.htm";
				st.takeItems(VENOM_SAC, -1);
				st.giveItems(ADENA_ID, 5810);
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "astaron_q0324_05.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("astaron_q0324_04.htm") && st.getState() == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() != STARTED)
		{
			return null;
		}

		long _count = qs.getQuestItemsCount(VENOM_SAC);
		int _chance = VENOM_SAC_BASECHANCE + (npc.getNpcId() - Prowler) / 4 * 12;

		if (_count < 10 && Rnd.chance(_chance))
		{
			qs.giveItems(VENOM_SAC, 1);
			if (_count == 9)
			{
				qs.setCond(2);
				qs.playSound(SOUND_MIDDLE);
			}
			else
			{
				qs.playSound(SOUND_ITEMGET);
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