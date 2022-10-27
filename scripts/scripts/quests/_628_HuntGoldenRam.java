package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _628_HuntGoldenRam extends Quest implements ScriptFile
{
	// Npcs
	private static int KAHMAN = 31554;

	// Items
	private static int CHITIN = 7248; // Splinter Stakato Chitin
	private static int CHITIN2 = 7249; // Needle Stakato Chitin
	private static int RECRUIT = 7246; // Golden Ram Badge - Recruit
	private static int SOLDIER = 7247; // Golden Ram Badge - Soldier

	private static int CHANCE = 49; // Base dropchance of the Badges

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

	public _628_HuntGoldenRam()
	{
		super(true);

		addStartNpc(KAHMAN);

		for (int npcId = 21508; npcId <= 21518; npcId++)
		{
			addKillId(npcId);
		}

		addQuestItem(CHITIN);
		addQuestItem(CHITIN2);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("31554-03a.htm"))
		{
			if (st.getQuestItemsCount(CHITIN) >= 100 && st.getCond() == 1)
			{
				st.setCond(2);
				st.takeItems(CHITIN, 100);
				st.giveItems(RECRUIT, 1);
				st.getPlayer().updateRam();
				htmltext = "31554-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("31554-07.htm"))
		{
			st.playSound(SOUND_GIVEUP);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		long chitin1 = st.getQuestItemsCount(CHITIN);
		long chitin2 = st.getQuestItemsCount(CHITIN2);
		if (st.isCompleted())
		{
			htmltext = "31554-05a.htm";
		}
		else
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 66)
				{
					htmltext = "31554-02.htm";
					st.setCond(1);
					st.setState(STARTED);
					st.playSound(SOUND_ACCEPT);
				}
				else
				{
					htmltext = "31554-01.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				if (chitin1 >= 100)
				{
					htmltext = "31554-03.htm";
				}
				else
				{
					htmltext = "31554-03a.htm";
				}
				break;
			case 2:
				if (chitin1 >= 100 && chitin2 >= 100)
				{
					htmltext = "31554-05.htm";
					st.takeItems(CHITIN, -1);
					st.takeItems(CHITIN2, -1);
					st.takeItems(RECRUIT, -1);
					st.giveItems(SOLDIER, 1);
					st.getPlayer().updateRam();
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(true);
				}
				if (chitin1 == 0 && chitin2 == 0)
				{
					htmltext = "31554-04b.htm";
				}
				else
				{
					htmltext = "31554-04a.htm";
				}
				break;
			default:
				break;
			}
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
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (cond >= 1 && 21507 < npcId && npcId < 21513)
		{
			if (Rnd.chance(CHANCE + (npcId - 21506) * 2))
			{
				st.giveItems(CHITIN, 1);

				if (st.getQuestItemsCount(CHITIN) < 100)
				{
					st.playSound(SOUND_ITEMGET);
				}
				else
				{
					st.playSound(SOUND_MIDDLE);
				}
			}
		}
		else if (cond == 2 && 21513 <= npcId && npcId <= 21518)
		{
			if (Rnd.chance(CHANCE + (npcId - 21512) * 3))
			{
				st.giveItems(CHITIN2, 1);

				if (st.getQuestItemsCount(CHITIN2) < 100)
				{
					st.playSound(SOUND_ITEMGET);
				}
				else
				{
					st.playSound(SOUND_MIDDLE);
				}
			}
		}
		return null;
	}
}