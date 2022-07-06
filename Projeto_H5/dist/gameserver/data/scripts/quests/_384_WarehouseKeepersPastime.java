package quests;

import java.util.HashMap;
import java.util.Map;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _384_WarehouseKeepersPastime extends Quest implements ScriptFile
{
	// NPCs
	private final static int Cliff = 30182;
	private final static int Baxt = 30685;
	// Items
	private final static int Warehouse_Keepers_Medal = 5964;

	private final static Map<Integer, Integer> Medal_Chances = new HashMap<Integer, Integer>();
	private final static Map<Integer, Bingo> bingos = new HashMap<Integer, Bingo>();

	private final static int[][] Rewards_Win =
	{
		{
			16,
			1888,
			1
		},
		// Synthetic Cokes
		{
			32,
			1887,
			1
		},
		// Varnish of Purity
		{
			50,
			1894,
			1
		},
		// Crafted Leather
		{
			80,
			952,
			1
		},
		// Scroll: Enchant Armor (C)
		{
			89,
			1890,
			1
		},
		// Mithril Alloy
		{
			98,
			1893,
			1
		},
		// Oriharukon
		{
			100,
			951,
			1
		}
		// Scroll: Enchant Weapon (C)
	};
	private final static int[][] Rewards_Win_Big =
	{
		{
			50,
			883,
			1
		},
		// Aquastone Ring
		{
			80,
			951,
			1
		},
		// Scroll: Enchant Weapon (C)
		{
			98,
			852,
			1
		},
		// Moonstone Earring
		{
			100,
			401,
			1
		}
		// Drake Leather Armor
	};
	private final static int[][] Rewards_Lose =
	{
		{
			50,
			4041,
			1
		},
		// Mold Hardener
		{
			80,
			952,
			1
		},
		// Scroll: Enchant Armor (C)
		{
			98,
			1892,
			1
		},
		// Blacksmith's Frame
		{
			100,
			917,
			1
		}
		// Necklace of Mermaid
	};
	private final static int[][] Rewards_Lose_Big =
	{
		{
			50,
			951,
			1
		},
		// Scroll: Enchant Weapon (C)
		{
			80,
			500,
			1
		},
		// Great Helmet
		{
			98,
			2437,
			2
		},
		// Drake Leather Boots
		{
			100,
			135,
			1
		}
		// Samurai Longsword
	};

	public _384_WarehouseKeepersPastime()
	{
		super(false);
		addStartNpc(Cliff);
		addTalkId(Baxt);

		Medal_Chances.put(20948, 18); // Bartal
		Medal_Chances.put(20945, 12); // Cadeine
		Medal_Chances.put(20946, 15); // Sanhidro
		Medal_Chances.put(20947, 16); // Connabi
		Medal_Chances.put(20635, 15); // Carinkain
		Medal_Chances.put(20773, 61); // Conjurer Bat Lord
		Medal_Chances.put(20774, 60); // Conjurer Bat
		Medal_Chances.put(20760, 24); // Dragon Bearer Archer
		Medal_Chances.put(20758, 24); // Dragon Bearer Chief
		Medal_Chances.put(20759, 23); // Dragon Bearer Warrior
		Medal_Chances.put(20242, 22); // Dustwind Gargoyle
		Medal_Chances.put(20281, 22); // Dustwind Gargoyle (2)
		Medal_Chances.put(20556, 14); // Giant Monstereye
		Medal_Chances.put(20668, 21); // Grave Guard
		Medal_Chances.put(20241, 22); // Hunter Gargoyle
		Medal_Chances.put(20286, 22); // Hunter Gargoyle (2)
		Medal_Chances.put(20950, 20); // Innersen
		Medal_Chances.put(20949, 19); // Luminun
		Medal_Chances.put(20942, 9); // Nightmare Guide
		Medal_Chances.put(20943, 12); // Nightmare Keeper
		Medal_Chances.put(20944, 11); // Nightmare Lord
		Medal_Chances.put(20559, 14); // Rotting Golem
		Medal_Chances.put(20243, 21); // Thunder Wyrm
		Medal_Chances.put(20282, 21); // Thunder Wyrm (2)
		Medal_Chances.put(20677, 34); // Tulben
		Medal_Chances.put(20605, 15); // Weird Drake

		for (int id : Medal_Chances.keySet())
		{
			addKillId(id);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		long medals = st.getQuestItemsCount(Warehouse_Keepers_Medal);
		if (event.equalsIgnoreCase("30182-05.htm") && _state == CREATED)
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if ((event.equalsIgnoreCase("30182-08.htm") || event.equalsIgnoreCase("30685-08.htm")) && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if (event.contains("-game") && _state == STARTED)
		{
			boolean big_game = event.contains("-big");
			int need_medals = big_game ? 100 : 10;
			if (medals < need_medals)
			{
				return event.replaceFirst("-big", "").replaceFirst("game", "09.htm");
			}
			st.takeItems(Warehouse_Keepers_Medal, need_medals);
			int char_obj_id = st.getPlayer().getObjectId();
			if (bingos.containsKey(char_obj_id))
			{
				bingos.remove(char_obj_id);
			}
			Bingo bingo = new Bingo(big_game, st);
			bingos.put(char_obj_id, bingo);
			return bingo.getDialog("");
		}
		else if (event.contains("choice-") && _state == STARTED)
		{
			int char_obj_id = st.getPlayer().getObjectId();
			if (!bingos.containsKey(char_obj_id))
			{
				return null;
			}
			Bingo bingo = bingos.get(char_obj_id);
			return bingo.Select(event.replaceFirst("choice-", ""));
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int _state = st.getState();
		int npcId = npc.getNpcId();

		if (_state == CREATED)
		{
			if (npcId != Cliff)
			{
				return "noquest";
			}
			if (st.getPlayer().getLevel() < 40)
			{
				st.exitCurrentQuest(true);
				return "30182-04.htm";
			}
			st.setCond(0);
			return "30182-01.htm";
		}

		if (_state != STARTED)
		{
			return "noquest";
		}
		long medals = st.getQuestItemsCount(Warehouse_Keepers_Medal);

		if (medals >= 100)
		{
			return String.valueOf(npcId) + "-06.htm";
		}
		if (medals >= 10)
		{
			return String.valueOf(npcId) + "-06a.htm";
		}
		return String.valueOf(npcId) + "-06b.htm";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() != STARTED)
		{
			return null;
		}
		Integer chance = Medal_Chances.get(npc.getNpcId());
		if (chance != null && Rnd.chance(chance * Config.RATE_QUESTS_REWARD))
		{
			qs.giveItems(Warehouse_Keepers_Medal, 1);
			qs.playSound(qs.getQuestItemsCount(Warehouse_Keepers_Medal) == 10 ? SOUND_MIDDLE : SOUND_ITEMGET);
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

	public static class Bingo extends quests.Bingo
	{
		protected final static String msg_begin = "I've arranged 9 numbers on the panel. Don't peek! Ha ha ha!<br>Now give me your 10 medals. Some players run away when they realize that they don't stand a good chance of winning. Therefore, I prefer to hold the medals before the game starts. If you quit during game play, you'll forfeit your bet. Is that satisfactory?<br>Now, select your %choicenum% number.";
		protected final static String msg_0lines = "You are spectacularly unlucky! The red-colored numbers on the panel below are the ones you chose. As you can see, they didn't create even a single line. Did you know that it is harder not to create a single line than creating all 3 lines?<br>Usually, I don't give a reward when you don't create a single line, but since I'm feeling sorry for you, I'll be generous this time. Wait here.<br>.<br>.<br>.<br><br><br>Here, take this. I hope it will bring you better luck in the future.";
		protected final static String msg_3lines = "You've created 3 lines! The red colored numbers on the bingo panel below are the numbers you chose. Congratulations! As I promised, I'll give you an unclaimed item from my warehouse. Wait here.<br>.<br>.<br>.<br><br><br>Puff puff... it's very dusty. Here it is. Do you like it?";
		private final static String template_choice = "<a action=\"bypass -h Quest _384_WarehouseKeepersPastime choice-%n%\">%n%</a>&nbsp;&nbsp;&nbsp;&nbsp;  ";
		private final boolean _BigGame;
		private final QuestState _qs;

		public Bingo(boolean BigGame, QuestState qs)
		{
			super(template_choice);
			_BigGame = BigGame;
			_qs = qs;
		}

		@Override
		protected String getFinal()
		{
			String result = super.getFinal();
			if (lines == 3)
			{
				reward(_BigGame ? Rewards_Win_Big : Rewards_Win);
			}
			else if (lines == 0)
			{
				reward(_BigGame ? Rewards_Lose_Big : Rewards_Lose);
			}

			bingos.remove(_qs.getPlayer().getObjectId());
			return result;
		}

		private void reward(int[][] rew)
		{
			int r = Rnd.get(100);
			for (int[] l : rew)
			{
				if (r < l[0])
				{
					_qs.giveItems(l[1], l[2], true);
					return;
				}
			}
		}
	}
}
