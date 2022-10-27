package quests;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _10288_SecretMission extends Quest implements ScriptFile
{
	// NPC's
	private static final int _dominic = 31350;
	private static final int _aquilani = 32780;
	private static final int _greymore = 32757;
	// Items
	private static final int _letter = 15529;

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

	public _10288_SecretMission()
	{
		super(false);

		addStartNpc(_dominic);
		addStartNpc(_aquilani);
		addTalkId(_dominic);
		addTalkId(_greymore);
		addTalkId(_aquilani);
		addFirstTalkId(_aquilani);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int npcId = npc.getNpcId();
		if (npcId == _dominic)
		{
			if (event.equalsIgnoreCase("31350-05.htm"))
			{
				st.setState(STARTED);
				st.setCond(1);
				st.giveItems(_letter, 1);
				st.playSound(SOUND_ACCEPT);
			}
		}
		else if (npcId == _greymore && event.equalsIgnoreCase("32757-03.htm"))
		{
			st.unset("cond");
			st.takeItems(_letter, -1);
			st.giveItems(57, 106583);
			st.addExpAndSp(417788, 46320);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if (npcId == _aquilani)
		{
			if (st.getState() == STARTED)
			{
				if (event.equalsIgnoreCase("32780-05.htm"))
				{
					st.setCond(2);
					st.playSound(SOUND_MIDDLE);
				}
			}
			else if (st.getState() == COMPLETED && event.equalsIgnoreCase("teleport"))
			{
				st.getPlayer().teleToLocation(118833, -80589, -2688);
				return null;
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		if (npcId == _dominic)
		{
			switch (st.getState())
			{
			case CREATED:
				if (st.getPlayer().getLevel() >= 82)
				{
					htmltext = "31350-01.htm";
				}
				else
				{
					htmltext = "31350-00.htm";
				}
				break;
			case STARTED:
				if (st.getCond() == 1)
				{
					htmltext = "31350-06.htm";
				}
				else if (st.getCond() == 2)
				{
					htmltext = "31350-07.htm";
				}
				break;
			case COMPLETED:
				htmltext = "31350-08.htm";
				break;
			}
		}
		else if (npcId == _aquilani)
		{
			if (st.getCond() == 1)
			{
				htmltext = "32780-03.htm";
			}
			else if (st.getCond() == 2)
			{
				htmltext = "32780-06.htm";
			}
		}
		else if (npcId == _greymore && st.getCond() == 2)
		{
			htmltext = "32757-01.htm";
		}

		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState st = player.getQuestState(getClass());
		if (st == null)
		{
			newQuestState(player, CREATED);
			st = player.getQuestState(getClass());
		}
		if (npc.getNpcId() == _aquilani)
		{
			if (st.getState() == COMPLETED)
			{
				return "32780-01.htm";
			}
			else
			{
				return "32780-00.htm";
			}
		}
		return null;
	}
}