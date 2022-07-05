package quests;

import java.util.HashMap;
import java.util.Map;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.model.base.Experience;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _663_SeductiveWhispers extends Quest implements ScriptFile
{
	// NPCs
	private final static int Wilbert = 30846;
	// Mobs
	private final static int[] mobs =
	{
		20674,
		20678,
		20954,
		20955,
		20956,
		20957,
		20958,
		20959,
		20960,
		20961,
		20962,
		20974,
		20975,
		20976,
		20996,
		20997,
		20998,
		20999,
		21001,
		21002,
		21006,
		21007,
		21008,
		21009,
		21010
	};
	// Quest Items
	private final static int Spirit_Bead = 8766;
	// Items
	private final static int Enchant_Weapon_D = 955;
	private final static int Enchant_Weapon_C = 951;
	private final static int Enchant_Weapon_B = 947;
	private final static int Enchant_Armor_B = 948;
	private final static int Enchant_Weapon_A = 729;
	private final static int Enchant_Armor_A = 730;
	private final static int[] Recipes_Weapon_B =
	{
		4963,
		4966,
		4967,
		4968,
		5001,
		5003,
		5004,
		5005,
		5006,
		5007
	};
	private final static int[] Ingredients_Weapon_B =
	{
		4101,
		4107,
		4108,
		4109,
		4115,
		4117,
		4118,
		4119,
		4120,
		4121
	};
	// Chances
	private final static int drop_chance = 15;
	private final static int WinChance = 68;

	private final static LevelRewards[] rewards =
	{
		new LevelRewards("%n% adena").add(ADENA_ID, 40000),
		new LevelRewards("%n% adena").add(ADENA_ID, 80000),
		new LevelRewards("%n% adena, %n% D-grade Enchant Weapon Scroll(s)").add(ADENA_ID, 110000).add(Enchant_Weapon_D, 1),
		new LevelRewards("%n% adena, %n% C-grade Enchant Weapon Scroll(s)").add(ADENA_ID, 199000).add(Enchant_Weapon_C, 1),
		new LevelRewards("%n% adena, %n% recipe(s) for a B-grade Weapon").add(ADENA_ID, 388000).add(Recipes_Weapon_B, 1),
		new LevelRewards("%n% adena, %n% essential ingredient(s) for a B-grade Weapon").add(ADENA_ID, 675000).add(Ingredients_Weapon_B, 1),
		new LevelRewards("%n% adena, %n% B-grade Enchant Weapon Scroll(s), %n% B-grade Enchat Armor Scroll(s)").add(ADENA_ID, 1284000).add(Enchant_Weapon_B, 2).add(Enchant_Armor_B, 2),
		new LevelRewards("%n% adena, %n% A-grade Enchant Weapon Scroll(s), %n% A-grade Enchat Armor Scroll(s)").add(ADENA_ID, 2384000).add(Enchant_Weapon_A, 1).add(Enchant_Armor_A, 2)
	};
	private static String Dialog_WinLevel = "<font color=\"LEVEL\">Blacksmith Wilbert:</font><br><br>";
	private static String Dialog_WinGame = "<font color=\"LEVEL\">Blacksmith Wilbert:</font><br><br>";
	private static String Dialog_Rewards = "<font color=\"LEVEL\">Blacksmith Wilbert:</font><br><br>";

	static
	{
		Dialog_WinLevel += "You won round %level%!<br>";
		Dialog_WinLevel += "You can stop game now and take your prize:<br>";
		Dialog_WinLevel += "<font color=\"LEVEL\">%prize%</font><br><br>";
		Dialog_WinLevel += "<a action=\"bypass -h Quest _663_SeductiveWhispers 30846_12.htm\">Pull next card!</a><br>";
		Dialog_WinLevel += "<a action=\"bypass -h Quest _663_SeductiveWhispers 30846_13.htm\">\"No, enough for me, end game and take my prize.\"</a>";

		Dialog_WinGame += "Congratulations! You won round %n%!<br>";
		Dialog_WinGame += "Game ends now and you get your prize:<br>";
		Dialog_WinGame += "<font color=\"LEVEL\">%prize%</font><br><br>";
		Dialog_WinGame += "<a action=\"bypass -h Quest _663_SeductiveWhispers 30846_03.htm\">Return</a>";

		Dialog_Rewards += "If you win the game, the master running it owes you the appropriate amount. The higher the round, the bigger the payout. That's why the game anly allows you to win up to 8 round in a row. If -- and that's a big if -- you manage to win 8 straight times, the game will end.<br>";
		Dialog_Rewards += "Keep in mind that <font color=\"LEVEL\">if you lose any of the rounds, you get nothing</font>. That's fair warning, my friend. Here's how the prize system works:<br>";
		for (int i = 0; i < rewards.length; i++)
		{
			Dialog_Rewards += "<font color=\"LEVEL\">" + String.valueOf(i + 1) + " winning round";
			if (i > 0)
			{
				Dialog_Rewards += "s";
			}
			Dialog_Rewards += ": </font>" + rewards[i].toString() + "<br>";
		}
		Dialog_Rewards += "<br>My advice is to identify what you'd like to win and then to play for that prize. Any questions?<br>";
		Dialog_Rewards += "<a action=\"bypass -h Quest _663_SeductiveWhispers 30846_03.htm\">Return</a>";
	}

	public _663_SeductiveWhispers()
	{
		super(false);
		addStartNpc(Wilbert);
		addKillId(mobs);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		long Spirit_Bead_Count = st.getQuestItemsCount(Spirit_Bead);
		if (event.equalsIgnoreCase("30846_04.htm") && _state == CREATED)
		{
			st.setCond(1);
			st.set("round", "0");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30846_07.htm") && _state == STARTED)
		{
			return Dialog_Rewards;
		}
		else if (event.equalsIgnoreCase("30846_09.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("30846_08.htm") && _state == STARTED)
		{
			if (Spirit_Bead_Count < 1)
			{
				return "30846_11.htm";
			}
			st.takeItems(Spirit_Bead, 1);
			if (!Rnd.chance(WinChance))
			{
				return "30846_08a.htm";
			}
		}
		else if (event.equalsIgnoreCase("30846_10.htm") && _state == STARTED)
		{
			st.set("round", "0");
			if (Spirit_Bead_Count < 50)
			{
				return "30846_11.htm";
			}
		}
		else if (event.equalsIgnoreCase("30846_12.htm") && _state == STARTED)
		{
			int round = st.getInt("round");
			if (round == 0)
			{
				if (Spirit_Bead_Count < 50)
				{
					return "30846_11.htm";
				}
				st.takeItems(Spirit_Bead, 50);
			}
			if (!Rnd.chance(WinChance))
			{
				st.set("round", "0");
				return event;
			}
			LevelRewards current_reward = rewards[round];
			int next_round = round + 1;
			boolean LastLevel = next_round == rewards.length;
			String dialog = LastLevel ? Dialog_WinGame : Dialog_WinLevel;
			dialog = dialog.replaceFirst("%level%", String.valueOf(next_round));
			dialog = dialog.replaceFirst("%prize%", current_reward.toString());

			if (LastLevel)
			{
				next_round = 0;
				current_reward.giveRewards(st);
				st.playSound(SOUND_JACKPOT);
			}

			st.set("round", String.valueOf(next_round));
			return dialog;
		}
		else if (event.equalsIgnoreCase("30846_13.htm") && _state == STARTED)
		{
			int round = st.getInt("round") - 1;
			st.set("round", "0");
			if (round < 0 || round >= rewards.length)
			{
				return "30846_13a.htm";
			}
			rewards[round].giveRewards(st);
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (npc.getNpcId() != Wilbert)
		{
			return "noquest";
		}
		int _state = st.getState();
		if (_state == CREATED)
		{
			if (st.getPlayer().getLevel() < 50)
			{
				st.exitCurrentQuest(true);
				return "30846_00.htm";
			}
			st.setCond(0);
			return "30846_01.htm";
		}
		return "30846_03.htm";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() == STARTED)
		{
			double rand = drop_chance * Experience.penaltyModifier(qs.calculateLevelDiffForDrop(npc.getLevel(), qs.getPlayer().getLevel()), 9) * npc.getTemplate().rateHp;
			qs.rollAndGive(Spirit_Bead, 1, rand);
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

	private static class LevelRewards
	{
		private final Map<int[], Integer> rewards = new HashMap<int[], Integer>();
		private String txt;

		public LevelRewards(String _txt)
		{
			txt = _txt;
		}

		public LevelRewards add(int item_id, int count)
		{
			return add(new int[]
			{
				item_id
			}, count);
		}

		public LevelRewards add(int[] items_id, int count)
		{
			int cnt = (int) (count * Config.RATE_QUESTS_REWARD);
			txt = txt.replaceFirst("%n%", String.valueOf(cnt));
			rewards.put(items_id, cnt);
			return this;
		}

		public void giveRewards(QuestState qs)
		{
			for (int[] item_ids : rewards.keySet())
			{
				qs.giveItems(item_ids[Rnd.get(item_ids.length)], rewards.get(item_ids), false);
			}
		}

		@Override
		public String toString()
		{
			return txt;
		}
	}
}