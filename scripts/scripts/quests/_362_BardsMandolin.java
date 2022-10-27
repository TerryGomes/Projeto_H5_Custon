package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _362_BardsMandolin extends Quest implements ScriptFile
{
	// NPC
	private static int SWAN = 30957;
	private static int NANARIN = 30956;
	private static int GALION = 30958;
	private static int WOODROW = 30837;
	// Items
	private static int SWANS_FLUTE = 4316;
	private static int SWANS_LETTER = 4317;
	private static int Musical_Score__Theme_of_Journey = 4410;

	public _362_BardsMandolin()
	{
		super(false);
		addStartNpc(SWAN);
		addTalkId(NANARIN);
		addTalkId(GALION);
		addTalkId(WOODROW);
		addQuestItem(SWANS_FLUTE);
		addQuestItem(SWANS_LETTER);
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		if (st.getState() == CREATED)
		{
			if (npcId != SWAN)
			{
				return htmltext;
			}
			st.setCond(0);
		}

		int cond = st.getCond();
		if (npcId == SWAN)
		{
			if (cond == 0)
			{
				htmltext = "30957_1.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(SWANS_FLUTE) > 0 && st.getQuestItemsCount(SWANS_LETTER) == 0)
			{
				htmltext = "30957_3.htm";
				st.setCond(4);
				st.giveItems(SWANS_LETTER, 1);
			}
			else if (cond == 4 && st.getQuestItemsCount(SWANS_FLUTE) > 0 && st.getQuestItemsCount(SWANS_LETTER) > 0)
			{
				htmltext = "30957_6.htm";
			}
			else if (cond == 5)
			{
				htmltext = "30957_4.htm";
			}
		}
		else if (npcId == WOODROW && cond == 1)
		{
			htmltext = "30837_1.htm";
			st.setCond(2);
		}
		else if (npcId == GALION && cond == 2)
		{
			htmltext = "30958_1.htm";
			st.setCond(3);
			st.giveItems(SWANS_FLUTE, 1);
			st.playSound(SOUND_ITEMGET);
		}
		else if (npcId == NANARIN && cond == 4 && st.getQuestItemsCount(SWANS_FLUTE) > 0 && st.getQuestItemsCount(SWANS_LETTER) > 0)
		{
			htmltext = "30956_1.htm";
			st.takeItems(SWANS_FLUTE, 1);
			st.takeItems(SWANS_LETTER, 1);
			st.setCond(5);
		}
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int _state = st.getState();
		int cond = st.getCond();
		if (event.equalsIgnoreCase("30957_2.htm") && _state == CREATED && cond == 0)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30957_5.htm") && _state == STARTED && cond == 5)
		{
			st.giveItems(ADENA_ID, 10000);
			st.giveItems(Musical_Score__Theme_of_Journey, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
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
