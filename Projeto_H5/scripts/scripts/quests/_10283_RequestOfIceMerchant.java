package quests;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _10283_RequestOfIceMerchant extends Quest implements ScriptFile
{
	// NPC's
	private static final int _rafforty = 32020;
	private static final int _kier = 32022;
	private static final int _jinia = 32760;

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

	public _10283_RequestOfIceMerchant()
	{
		super(false);

		addStartNpc(_rafforty);
		addTalkId(_rafforty);
		addTalkId(_kier);
		addTalkId(_jinia);
		addFirstTalkId(_jinia);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (npc == null)
		{
			return null;
		}

		int npcId = npc.getNpcId();
		if (npcId == _rafforty)
		{
			if (event.equalsIgnoreCase("32020-03.htm"))
			{
				st.setState(STARTED);
				st.setCond(1);
				st.playSound(SOUND_ACCEPT);
			}
			else if (event.equalsIgnoreCase("32020-07.htm"))
			{
				st.setCond(2);
				st.playSound(SOUND_MIDDLE);
			}
		}
		else if (npcId == _kier && event.equalsIgnoreCase("spawn"))
		{
			addSpawn(_jinia, 104322, -107669, -3680, 44954, 0, 60000);
			return null;
		}
		else if (npcId == _jinia && event.equalsIgnoreCase("32760-04.htm"))
		{
			st.giveItems(57, 190000);
			st.addExpAndSp(627000, 50300);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
			npc.deleteMe();
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		if (npcId == _rafforty)
		{
			switch (st.getState())
			{
			case CREATED:
				QuestState _prev = st.getPlayer().getQuestState(_115_TheOtherSideOfTruth.class);
				if (_prev != null && _prev.isCompleted() && st.getPlayer().getLevel() >= 82)
				{
					htmltext = "32020-01.htm";
				}
				else
				{
					htmltext = "32020-00.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case STARTED:
				if (st.getCond() == 1)
				{
					htmltext = "32020-04.htm";
				}
				else if (st.getCond() == 2)
				{
					htmltext = "32020-08.htm";
				}
				break;
			case COMPLETED:
				htmltext = "31350-08.htm";
				break;
			}
		}
		else if (npcId == _kier && st.getCond() == 2)
		{
			htmltext = "32022-01.htm";
		}
		else if (npcId == _jinia && st.getCond() == 2)
		{
			htmltext = "32760-02.htm";
		}

		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState st = player.getQuestState(getClass());
		if (st == null)
		{
			return null;
		}
		if (npc.getNpcId() == _jinia && st.getCond() == 2)
		{
			return "32760-01.htm";
		}
		return null;
	}
}
