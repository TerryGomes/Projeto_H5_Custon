package quests;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _662_AGameOfCards extends Quest implements ScriptFile
{
	// NPCs
	private final static int KLUMP = 30845;
	// Mobs
	private final static int[] mobs =
	{
		20677,
		21109,
		21112,
		21116,
		21114,
		21004,
		21002,
		21006,
		21008,
		21010,
		18001,
		20672,
		20673,
		20674,
		20955,
		20962,
		20961,
		20959,
		20958,
		20966,
		20965,
		20968,
		20973,
		20972,
		21278,
		21279,
		21280,
		21286,
		21287,
		21288,
		21520,
		21526,
		21530,
		21535,
		21508,
		21510,
		21513,
		21515
	};
	// Quest Items
	private final static int RED_GEM = 8765;
	// Items
	private final static int Enchant_Weapon_S = 959;
	private final static int Enchant_Weapon_A = 729;
	private final static int Enchant_Weapon_B = 947;
	private final static int Enchant_Weapon_C = 951;
	private final static int Enchant_Weapon_D = 955;
	private final static int Enchant_Armor_D = 956;
	private final static int ZIGGOS_GEMSTONE = 8868;
	// Chances
	private final static int drop_chance = 35;

	private final static Map<Integer, CardGame> Games = new ConcurrentHashMap<Integer, CardGame>();

	public _662_AGameOfCards()
	{
		super(true);
		addStartNpc(KLUMP);
		addKillId(mobs);
		addQuestItem(RED_GEM);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("30845_02.htm") && _state == CREATED)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30845_07.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("30845_03.htm") && _state == STARTED && st.getQuestItemsCount(RED_GEM) >= 50)
		{
			return "30845_04.htm";
		}
		else if (event.equalsIgnoreCase("30845_10.htm") && _state == STARTED)
		{
			if (st.getQuestItemsCount(RED_GEM) < 50)
			{
				return "30845_10a.htm";
			}
			st.takeItems(RED_GEM, 50);
			int player_id = st.getPlayer().getObjectId();
			if (Games.containsKey(player_id))
			{
				Games.remove(player_id);
			}
			Games.put(player_id, new CardGame(player_id));
		}
		else if (event.equalsIgnoreCase("play") && _state == STARTED)
		{
			int player_id = st.getPlayer().getObjectId();
			if (!Games.containsKey(player_id))
			{
				return null;
			}
			return Games.get(player_id).playField();
		}
		else if (event.startsWith("card") && _state == STARTED)
		{
			int player_id = st.getPlayer().getObjectId();
			if (!Games.containsKey(player_id))
			{
				return null;
			}
			try
			{
				int cardn = Integer.valueOf(event.replaceAll("card", ""));
				return Games.get(player_id).next(cardn, st);
			}
			catch (Exception E)
			{
				return null;
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (npc.getNpcId() != KLUMP)
		{
			return "noquest";
		}
		int _state = st.getState();
		if (_state == CREATED)
		{
			if (st.getPlayer().getLevel() < 61)
			{
				st.exitCurrentQuest(true);
				return "30845_00.htm";
			}
			st.setCond(0);
			return "30845_01.htm";
		}
		else if (_state == STARTED)
		{
			return st.getQuestItemsCount(RED_GEM) < 50 ? "30845_03.htm" : "30845_04.htm";
		}

		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() == STARTED)
		{
			qs.rollAndGive(RED_GEM, 1, drop_chance);
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

	private static class CardGame
	{
		private final String[] cards = new String[5];
		private final int player_id;
		private final static String[] card_chars = new String[]
		{
			"A",
			"1",
			"2",
			"3",
			"4",
			"5",
			"6",
			"7",
			"8",
			"9",
			"10",
			"J",
			"Q",
			"K"
		};
		private final static String html_header = "<html><body>";
		private final static String html_footer = "</body></html>";
		private final static String table_header = "<table border=\"1\" cellpadding=\"3\"><tr>";
		private final static String table_footer = "</tr></table><br><br>";
		private final static String td_begin = "<center><td width=\"50\" align=\"center\"><br><br><br> ";
		private final static String td_end = " <br><br><br><br></td></center>";

		public CardGame(int _player_id)
		{
			player_id = _player_id;
			for (int i = 0; i < cards.length; i++)
			{
				cards[i] = "<a action=\"bypass -h Quest _662_AGameOfCards card" + i + "\">?</a>";
			}
		}

		public String next(int cardn, QuestState st)
		{
			if (cardn >= cards.length || !cards[cardn].startsWith("<a"))
			{
				return null;
			}
			cards[cardn] = card_chars[Rnd.get(card_chars.length)];
			for (String card : cards)
			{
				if (card.startsWith("<a"))
				{
					return playField();
				}
			}
			return finish(st);
		}

		private String finish(QuestState st)
		{
			String result = html_header + table_header;
			Map<String, Integer> matches = new HashMap<String, Integer>();
			for (String card : cards)
			{
				int count = matches.containsKey(card) ? matches.remove(card) : 0;
				count++;
				matches.put(card, count);
			}
			for (String card : cards)
			{
				if (matches.get(card) < 2)
				{
					matches.remove(card);
				}
			}
			String[] smatches = matches.keySet().toArray(new String[matches.size()]);
			Integer[] cmatches = matches.values().toArray(new Integer[matches.size()]);
			String txt = "Hmmm...? This is... No pair? Tough luck, my friend! Want to try again? Perhaps your luck will take a turn for the better...";
			if (cmatches.length == 1)
			{
				if (cmatches[0] == 5)
				{
					txt = "Hmmm...? This is... Five of a kind!!!! What luck! The goddess of victory must be with you! Here is your prize! Well earned, well played!";
					st.giveItems(ZIGGOS_GEMSTONE, 43);
					st.giveItems(Enchant_Weapon_S, 3);
					st.giveItems(Enchant_Weapon_A, 1);
				}
				else if (cmatches[0] == 4)
				{
					txt = "Hmmm...? This is... Four of a kind! Well done, my young friend! That sort of hand doesn't come up very often, that's for sure. Here's your prize.";
					st.giveItems(Enchant_Weapon_S, 2);
					st.giveItems(Enchant_Weapon_C, 2);
				}
				else if (cmatches[0] == 3)
				{
					txt = "Hmmm...? This is... Three of a kind? Very good, you are very lucky. Here's your prize.";
					st.giveItems(Enchant_Weapon_C, 2);
				}
				else if (cmatches[0] == 2)
				{
					txt = "Hmmm...? This is... One pair? You got lucky this time, but I wonder if it'll last. Here's your prize.";
					st.giveItems(Enchant_Armor_D, 2);
				}
			}
			else if (cmatches.length == 2)
			{
				if (cmatches[0] == 3 || cmatches[1] == 3)
				{
					txt = "Hmmm...? This is... A full house? Excellent! you're better than I thought. Here's your prize.";
					st.giveItems(Enchant_Weapon_A, 1);
					st.giveItems(Enchant_Weapon_B, 2);
					st.giveItems(Enchant_Weapon_D, 1);
				}
				else
				{
					txt = "Hmmm...? This is... Two pairs? You got lucky this time, but I wonder if it'll last. Here's your prize.";
					st.giveItems(Enchant_Weapon_C, 1);
				}
			}

			for (String card : cards)
			{
				if (smatches.length > 0 && smatches[0].equalsIgnoreCase(card))
				{
					result += td_begin + "<font color=\"55FD44\">" + card + "</font>" + td_end;
				}
				else if (smatches.length == 2 && smatches[1].equalsIgnoreCase(card))
				{
					result += td_begin + "<font color=\"FE6666\">" + card + "</font>" + td_end;
				}
				else
				{
					result += td_begin + card + td_end;
				}
			}

			result += table_footer + txt;
			if (st.getQuestItemsCount(RED_GEM) >= 50)
			{
				result += "<br><br><a action=\"bypass -h Quest _662_AGameOfCards 30845_10.htm\">Play Again!</a>";
			}
			result += html_footer;
			Games.remove(player_id);
			return result;
		}

		public String playField()
		{
			String result = html_header + table_header;
			for (String card : cards)
			{
				result += td_begin + card + td_end;
			}
			result += table_footer + "Check your next card." + html_footer;
			return result;
		}
	}
}