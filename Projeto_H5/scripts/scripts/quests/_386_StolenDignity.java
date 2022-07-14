package quests;

import java.util.HashMap;
import java.util.Map;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _386_StolenDignity extends Quest implements ScriptFile
{
	// NPCs
	private final static int Romp = 30843;
	// Items
	private final static int Stolen_Infernium_Ore = 6363;

	private final static int Required_Stolen_Infernium_Ore = 100;
	private final static Map<Integer, Integer> dropchances = new HashMap<Integer, Integer>();
	private final static Map<Integer, Bingo> bingos = new HashMap<Integer, Bingo>();

	private final static int[][] Rewards_Win =
	{
		{
			5529,
			10
		},
		// dragon_slayer_edge
		{
			5532,
			10
		},
		// meteor_shower_head
		{
			5533,
			10
		},
		// elysian_head
		{
			5534,
			10
		},
		// soul_bow_shaft
		{
			5535,
			10
		},
		// carnium_bow_shaft
		{
			5536,
			10
		},
		// bloody_orchid_head
		{
			5537,
			10
		},
		// soul_separator_head
		{
			5538,
			10
		},
		// dragon_grinder_edge
		{
			5539,
			10
		},
		// blood_tornado_edge
		{
			5541,
			10
		},
		// tallum_glaive_edge
		{
			5542,
			10
		},
		// halbard_edge
		{
			5543,
			10
		},
		// dasparion_s_staff_head
		{
			5544,
			10
		},
		// worldtree_s_branch_head
		{
			5545,
			10
		},
		// dark_legion_s_edge_edge
		{
			5546,
			10
		},
		// sword_of_miracle_edge
		{
			5547,
			10
		},
		// elemental_sword_edge
		{
			5548,
			10
		},
		// tallum_blade_edge
		{
			8331,
			10
		},
		// Infernal Master Blade
		{
			8341,
			10
		},
		// Spiritual Eye Piece
		{
			8342,
			10
		},
		// Flaming Dragon Skull Piece
		{
			8346,
			10
		},
		// Hammer Piece of Destroyer
		{
			8349,
			10
		},
		// Doom Crusher Head
		{
			8712,
			10
		},
		// Sirra's Blade Edge
		{
			8713,
			10
		},
		// Sword of Ipos Blade
		{
			8714,
			10
		},
		// Barakiel's Axe Piece
		{
			8715,
			10
		},
		// Behemoth's Tuning Fork Piece
		{
			8716,
			10
		},
		// Naga Storm Piece
		{
			8717,
			10
		},
		// Tiphon's Spear Edge
		{
			8718,
			10
		},
		// Shyeed's Bow Shaft
		{
			8719,
			10
		},
		// Sobekk's Hurricane Edge
		{
			8720,
			10
		},
		// Themis' Tongue Piece
		{
			8721,
			10
		},
		// Cabrio's Hand Head
		{
			8722,
			10
		},
		// Daimon Crystal Fragment
	};
	private final static int[][] Rewards_Lose =
	{
		{
			5529,
			4
		},
		// dragon_slayer_edge
		{
			5532,
			4
		},
		// meteor_shower_head
		{
			5533,
			4
		},
		// elysian_head
		{
			5534,
			4
		},
		// soul_bow_shaft
		{
			5535,
			4
		},
		// carnium_bow_shaft
		{
			5536,
			4
		},
		// bloody_orchid_head
		{
			5537,
			4
		},
		// soul_separator_head
		{
			5538,
			4
		},
		// dragon_grinder_edge
		{
			5539,
			4
		},
		// blood_tornado_edge
		{
			5541,
			4
		},
		// tallum_glaive_edge
		{
			5542,
			4
		},
		// halbard_edge
		{
			5543,
			4
		},
		// dasparion_s_staff_head
		{
			5544,
			4
		},
		// worldtree_s_branch_head
		{
			5545,
			4
		},
		// dark_legion_s_edge_edge
		{
			5546,
			4
		},
		// sword_of_miracle_edge
		{
			5547,
			4
		},
		// elemental_sword_edge
		{
			5548,
			4
		},
		// tallum_blade_edge
		{
			8331,
			4
		},
		// Infernal Master Blade
		{
			8341,
			4
		},
		// Spiritual Eye Piece
		{
			8342,
			4
		},
		// Flaming Dragon Skull Piece
		{
			8346,
			4
		},
		// Hammer Piece of Destroyer
		{
			8349,
			4
		},
		// Doom Crusher Head
		{
			8712,
			4
		},
		// Sirra's Blade Edge
		{
			8713,
			4
		},
		// Sword of Ipos Blade
		{
			8714,
			4
		},
		// Barakiel's Axe Piece
		{
			8715,
			4
		},
		// Behemoth's Tuning Fork Piece
		{
			8716,
			4
		},
		// Naga Storm Piece
		{
			8717,
			4
		},
		// Tiphon's Spear Edge
		{
			8718,
			4
		},
		// Shyeed's Bow Shaft
		{
			8719,
			4
		},
		// Sobekk's Hurricane Edge
		{
			8720,
			4
		},
		// Themis' Tongue Piece
		{
			8721,
			4
		},
		// Cabrio's Hand Head
		{
			8722,
			4
		},
		// Daimon Crystal Fragment
	};

	public _386_StolenDignity()
	{
		super(true);
		addStartNpc(Romp);

		dropchances.put(20670, 14);
		dropchances.put(20671, 14);
		dropchances.put(20954, 11);
		dropchances.put(20956, 13);
		dropchances.put(20958, 13);
		dropchances.put(20959, 13);
		dropchances.put(20960, 11);
		dropchances.put(20964, 13);
		dropchances.put(20969, 19);
		dropchances.put(20967, 18);
		dropchances.put(20970, 18);
		dropchances.put(20971, 18);
		dropchances.put(20974, 28);
		dropchances.put(20975, 28);
		dropchances.put(21001, 14);
		dropchances.put(21003, 18);
		dropchances.put(21005, 14);
		dropchances.put(21020, 16);
		dropchances.put(21021, 15);
		dropchances.put(21259, 15);
		dropchances.put(21089, 13);
		dropchances.put(21108, 19);
		dropchances.put(21110, 18);
		dropchances.put(21113, 25);
		dropchances.put(21114, 23);
		dropchances.put(21116, 25);

		for (int kill_id : dropchances.keySet())
		{
			addKillId(kill_id);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("warehouse_keeper_romp_q0386_05.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("warehouse_keeper_romp_q0386_08.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("game"))
		{
			if (st.getQuestItemsCount(Stolen_Infernium_Ore) < Required_Stolen_Infernium_Ore)
			{
				return "warehouse_keeper_romp_q0386_11.htm";
			}
			st.takeItems(Stolen_Infernium_Ore, Required_Stolen_Infernium_Ore);
			int char_obj_id = st.getPlayer().getObjectId();
			if (bingos.containsKey(char_obj_id))
			{
				bingos.remove(char_obj_id);
			}
			Bingo bingo = new Bingo(st);
			bingos.put(char_obj_id, bingo);
			return bingo.getDialog("");
		}
		else if (event.contains("choice-"))
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
		if (st.getState() == CREATED)
		{
			if (st.getPlayer().getLevel() < 58)
			{
				st.exitCurrentQuest(true);
				return "warehouse_keeper_romp_q0386_04.htm";
			}
			return "warehouse_keeper_romp_q0386_01.htm";
		}
		return st.getQuestItemsCount(Stolen_Infernium_Ore) < Required_Stolen_Infernium_Ore ? "warehouse_keeper_romp_q0386_06.htm" : "warehouse_keeper_romp_q0386_07.htm";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		Integer _chance = dropchances.get(npc.getNpcId());
		if (_chance != null)
		{
			qs.rollAndGive(Stolen_Infernium_Ore, 1, _chance);
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
		protected final static String msg_begin = "I've arranged the numbers 1 through 9 on the grid. Don't peek!<br>Let me have the 100 Infernium Ores. Too many players try to run away without paying when it becomes obvious that they're losing...<br>OK, select six numbers between 1 and 9. Choose the %choicenum% number.";
		protected final static String msg_again = "You've already chosen that number. Make your %choicenum% choice again.";
		protected final static String msg_0lines = "Wow! How unlucky can you get? Your choices are highlighted in red below. As you can see, your choices didn't make a single line! Losing this badly is actually quite rare!<br>You look so sad, I feel bad for you... Wait here... <br>.<br>.<br>.<br>Take this... I hope it will bring you better luck in the future.";
		protected final static String msg_3lines = "Excellent! As you can see, you've formed three lines! Congratulations! As promised, I'll give you some unclaimed merchandise from the warehouse. Wait here...<br>.<br>.<br>.<br>Whew, it's dusty! OK, here you go. Do you like it?";
		protected final static String msg_lose = "Oh, too bad. Your choices didn't form three lines. You should try again... Your choices are highlighted in red.";
		private final static String template_choice = "<a action=\"bypass -h Quest _386_StolenDignity choice-%n%\">%n%</a>&nbsp;&nbsp;&nbsp;&nbsp;  ";
		private final QuestState _qs;

		public Bingo(QuestState qs)
		{
			super(template_choice);
			_qs = qs;
		}

		@Override
		protected String getFinal()
		{
			String result = super.getFinal();
			if (lines == 3)
			{
				reward(Rewards_Win);
			}
			else if (lines == 0)
			{
				reward(Rewards_Lose);
			}

			bingos.remove(_qs.getPlayer().getObjectId());
			return result;
		}

		private void reward(int[][] rew)
		{
			int[] r = rew[Rnd.get(rew.length)];
			_qs.giveItems(r[0], r[1], false);
		}
	}
}