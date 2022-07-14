package quests;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import bosses.FourSepulchersManager;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _620_FourGoblets extends Quest implements ScriptFile
{
	// NPC
	private static int NAMELESS_SPIRIT = 31453;

	private static int GHOST_OF_WIGOTH_1 = 31452;
	private static int GHOST_OF_WIGOTH_2 = 31454;

	private static int CONQ_SM = 31921;
	private static int EMPER_SM = 31922;
	private static int SAGES_SM = 31923;
	private static int JUDGE_SM = 31924;

	private static int GHOST_CHAMBERLAIN_1 = 31919;
	private static int GHOST_CHAMBERLAIN_2 = 31920;

	// ITEMS
	private static int GRAVE_PASS = 7261;
	private static int[] GOBLETS = new int[]
	{
		7256,
		7257,
		7258,
		7259
	};
	private static int RELIC = 7254;
	public final static int Sealed_Box = 7255;

	// REWARDS
	private static int ANTIQUE_BROOCH = 7262;
	private static int[] RCP_REWARDS = new int[]
	{
		6881,
		6883,
		6885,
		6887,
		6891,
		6893,
		6895,
		6897,
		6899,
		7580
	};

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

	public _620_FourGoblets()
	{
		super(false);

		addStartNpc(NAMELESS_SPIRIT, CONQ_SM, EMPER_SM, SAGES_SM, JUDGE_SM, GHOST_CHAMBERLAIN_1, GHOST_CHAMBERLAIN_2);

		addTalkId(GHOST_OF_WIGOTH_1, GHOST_OF_WIGOTH_2);

		addQuestItem(Sealed_Box, GRAVE_PASS);
		addQuestItem(GOBLETS);

		for (int id = 18120; id <= 18256; id++)
		{
			addKillId(id);
		}
	}

	private static String onOpenBoxes(QuestState st, String count)
	{
		try
		{
			return new OpenSealedBox(st, Integer.parseInt(count)).apply();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "Dont try to cheat with me!";
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		final Player player = st.getPlayer();
		final int cond = st.getCond();
		if (event.equalsIgnoreCase("Enter"))
		{
			FourSepulchersManager.tryEntry(npc, player);
			return null;
		}

		if (event.equalsIgnoreCase("accept"))
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 74)
				{
					st.setState(STARTED);
					st.playSound(SOUND_ACCEPT);
					st.setCond(1);
					return "31453-13.htm";
				}
				st.exitCurrentQuest(true);
				return "31453-12.htm";
			}
		}
		else if (event.startsWith("openBoxes "))
		{
			return onOpenBoxes(st, event.replace("openBoxes ", "").trim());
		}
		else if (event.equalsIgnoreCase("12"))
		{
			if (!st.checkQuestItemsCount(GOBLETS))
			{
				return "31453-14.htm";
			}
			st.takeAllItems(GOBLETS);
			st.giveItems(ANTIQUE_BROOCH, 1);
			st.setCond(2);
			st.playSound(SOUND_FINISH);
			return "31453-16.htm";
		}
		else if (event.equalsIgnoreCase("13"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return "31453-18.htm";
		}
		else if (event.equalsIgnoreCase("14"))
		{
			if (cond == 2)
			{
				return "31453-19.htm";
			}
			return "31453-13.htm";
		}
		// Ghost Chamberlain of Elmoreden: Teleport to 4th sepulcher
		else if (event.equalsIgnoreCase("15"))
		{
			if (st.getQuestItemsCount(ANTIQUE_BROOCH) >= 1)
			{
				st.getPlayer().teleToLocation(178298, -84574, -7216);
				return null;
			}
			if (st.getQuestItemsCount(GRAVE_PASS) >= 1)
			{
				st.takeItems(GRAVE_PASS, 1);
				st.getPlayer().teleToLocation(178298, -84574, -7216);
				return null;
			}
			return "" + str(npc.getNpcId()) + "-0.htm";
		}
		// Ghost Chamberlain of Elmoreden: Teleport to Imperial Tomb entrance
		else if (event.equalsIgnoreCase("16"))
		{
			if (st.getQuestItemsCount(ANTIQUE_BROOCH) >= 1)
			{
				st.getPlayer().teleToLocation(186942, -75602, -2834);
				return null;
			}
			if (st.getQuestItemsCount(GRAVE_PASS) >= 1)
			{
				st.takeItems(GRAVE_PASS, 1);
				st.getPlayer().teleToLocation(186942, -75602, -2834);
				return null;
			}
			return "" + str(npc.getNpcId()) + "-0.htm";
		}
		// Teleport to Pilgrims Temple
		else if (event.equalsIgnoreCase("17"))
		{
			if (st.getQuestItemsCount(ANTIQUE_BROOCH) >= 1)
			{
				st.getPlayer().teleToLocation(169590, -90218, -2914);
			}
			else
			{
				st.takeItems(GRAVE_PASS, 1);
				st.getPlayer().teleToLocation(169590, -90218, -2914);
			}
			return "31452-6.htm";
		}
		else if (event.equalsIgnoreCase("18"))
		{
			if (st.getSumQuestItemsCount(GOBLETS) < 3)
			{
				return "31452-3.htm";
			}
			if (st.getSumQuestItemsCount(GOBLETS) == 3)
			{
				return "31452-4.htm";
			}
			if (st.getSumQuestItemsCount(GOBLETS) >= 4)
			{
				return "31452-5.htm";
			}
		}
		else if (event.equalsIgnoreCase("19"))
		{
			return new OpenSealedBox(st, 1).apply();
		}
		else if (event.startsWith("19 "))
		{
			return onOpenBoxes(st, event.replaceFirst("19 ", ""));
		}
		else if (event.equalsIgnoreCase("11"))
		{
			return "<html><body><a action=\"bypass -h Quest _620_FourGoblets 19\">\"Please open a box.\"</a><br><a action=\"bypass -h Quest _620_FourGoblets 19 5\">\"Please open 5 boxes.\"</a><br><a action=\"bypass -h Quest _620_FourGoblets 19 10\">\"Please open 10 boxes.\"</a><br><a action=\"bypass -h Quest _620_FourGoblets 19 50\">\"Please open 50 boxes.\"</a><br></body></html>";
		}
		else if (NumberUtils.isNumber(event))
		{
			final int id = Integer.parseInt(event);
			if (ArrayUtils.contains(RCP_REWARDS, id))
			{
				st.takeItems(RELIC, 1000);
				st.giveItems(id, 1);
				return "31454-17.htm";
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		if (id == CREATED)
		{
			st.setCond(0);
		}
		if (npcId == NAMELESS_SPIRIT)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 74)
				{
					htmltext = "31453-1.htm";
				}
				else
				{
					htmltext = "31453-12.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				if (st.checkQuestItemsCount(GOBLETS))
				{
					htmltext = "31453-15.htm";
				}
				else
				{
					htmltext = "31453-14.htm";
				}
				break;
			case 2:
				htmltext = "31453-17.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == GHOST_OF_WIGOTH_1)
		{
			if (cond == 2)
			{
				htmltext = "31452-2.htm";
			}
			else if (cond == 1)
			{
				if (st.getSumQuestItemsCount(GOBLETS) == 1)
				{
					htmltext = "31452-1.htm";
				}
				else if (st.getSumQuestItemsCount(GOBLETS) > 1)
				{
					htmltext = "31452-2.htm";
				}
			}
		}
		else if (npcId == GHOST_OF_WIGOTH_2)
		{
			if (st.getQuestItemsCount(RELIC) >= 1000)
			{
				if (st.getQuestItemsCount(Sealed_Box) >= 1)
				{
					if (st.checkQuestItemsCount(GOBLETS))
					{
						htmltext = "31454-4.htm";
					}
					else if (st.checkQuestItemsCount(GOBLETS))
					{
						htmltext = "31454-8.htm";
					}
					else
					{
						htmltext = "31454-12.htm";
					}
				}
				else if (st.checkQuestItemsCount(GOBLETS))
				{
					htmltext = "31454-3.htm";
				}
				else if (st.getSumQuestItemsCount(GOBLETS) > 1)
				{
					htmltext = "31454-7.htm";
				}
				else
				{
					htmltext = "31454-11.htm";
				}
			}
			else if (st.getQuestItemsCount(Sealed_Box) >= 1)
			{
				if (st.checkQuestItemsCount(GOBLETS))
				{
					htmltext = "31454-2.htm";
				}
				else if (st.getSumQuestItemsCount(GOBLETS) > 1)
				{
					htmltext = "31454-6.htm";
				}
				else
				{
					htmltext = "31454-10.htm";
				}
			}
			else if (st.checkQuestItemsCount(GOBLETS))
			{
				htmltext = "31454-1.htm";
			}
			else if (st.getSumQuestItemsCount(GOBLETS) > 1)
			{
				htmltext = "31454-5.htm";
			}
			else
			{
				htmltext = "31454-9.htm";
			}
		}
		else if (npcId == CONQ_SM)
		{
			htmltext = "31921-E.htm";
		}
		else if (npcId == EMPER_SM)
		{
			htmltext = "31922-E.htm";
		}
		else if (npcId == SAGES_SM)
		{
			htmltext = "31923-E.htm";
		}
		else if (npcId == JUDGE_SM)
		{
			htmltext = "31924-E.htm";
		}
		else if (npcId == GHOST_CHAMBERLAIN_1)
		{
			htmltext = "31919-1.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if ((cond == 1 || cond == 2) && npcId >= 18120 && npcId <= 18256 && Rnd.chance(30))
		{
			st.giveItems(Sealed_Box, 1);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}