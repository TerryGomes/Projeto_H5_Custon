package quests;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.ExStartScenePlayer;
import l2f.gameserver.scripts.ScriptFile;

public class _192_SevenSignSeriesOfDoubt extends Quest implements ScriptFile
{
	// NPC
	private static int CROOP = 30676;
	private static int HECTOR = 30197;
	private static int STAN = 30200;
	private static int CORPSE = 32568;
	private static int HOLLINT = 30191;

	// ITEMS
	private static int CROOP_INTRO = 13813;
	private static int JACOB_NECK = 13814;
	private static int CROOP_LETTER = 13815;

	public _192_SevenSignSeriesOfDoubt()
	{
		super(false);

		addStartNpc(CROOP);
		addTalkId(HECTOR, STAN, CORPSE, HOLLINT);
		addQuestItem(CROOP_INTRO, JACOB_NECK, CROOP_LETTER);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if (event.equalsIgnoreCase("30676-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("8"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_SUSPICIOUS_DEATH);
			return "";
		}
		else if (event.equalsIgnoreCase("30197-03.htm"))
		{
			st.setCond(4);
			st.takeItems(CROOP_INTRO, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30200-04.htm"))
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32568-02.htm"))
		{
			st.setCond(6);
			st.giveItems(JACOB_NECK, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30676-12.htm"))
		{
			st.setCond(7);
			st.takeItems(JACOB_NECK, 1);
			st.giveItems(CROOP_LETTER, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30191-03.htm"))
		{
			if (player.getLevel() < 79)
			{
				htmltext = "<html><body>Only characters who are <font color=\"LEVEL\">level 79</font> or higher may complete this quest.</body></html>";
			}
			else if (player.getBaseClassId() == player.getActiveClassId())
			{
				st.addExpAndSp(25000000, 2500000);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
			else
			{
				return "subclass_forbidden.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		int id = st.getState();
		Player player = st.getPlayer();
		if (npcId == CROOP)
		{
			if (id == CREATED && player.getLevel() >= 79)
			{
				htmltext = "30676-01.htm";
			}
			else if (cond == 1)
			{
				htmltext = "30676-04.htm";
			}
			else if (cond == 2)
			{
				htmltext = "30676-05.htm";
				st.setCond(3);
				st.playSound(SOUND_MIDDLE);
				st.giveItems(CROOP_INTRO, 1);
			}
			else if (cond >= 3 && cond <= 5)
			{
				htmltext = "30676-06.htm";
			}
			else if (cond == 6)
			{
				htmltext = "30676-07.htm";
			}
			else if (id == COMPLETED)
			{
				htmltext = "30676-13.htm";
			}
			else if (player.getLevel() < 79)
			{
				htmltext = "30676-00.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (npcId == HECTOR)
		{
			if (cond == 3)
			{
				htmltext = "30197-01.htm";
			}
			if (cond >= 4 && cond <= 7)
			{
				htmltext = "30197-04.htm";
			}
		}
		else if (npcId == STAN)
		{
			if (cond == 4)
			{
				htmltext = "30200-01.htm";
			}
			if (cond >= 5 && cond <= 7)
			{
				htmltext = "30200-05.htm";
			}
		}
		else if (npcId == CORPSE)
		{
			if (cond == 5)
			{
				htmltext = "32568-01.htm";
			}
		}
		else if (npcId == HOLLINT)
		{
			if (cond == 7)
			{
				htmltext = "30191-01.htm";
			}
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