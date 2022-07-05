package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _373_SupplierOfReagents extends Quest implements ScriptFile
{
	// Quest items
	private static final int REAGENT_POUCH1 = 6007;
	private static final int REAGENT_POUCH2 = 6008;
	private static final int REAGENT_POUCH3 = 6009;
	private static final int REAGENT_BOX = 6010;
	private static final int WYRMS_BLOOD = 6011;
	private static final int LAVA_STONE = 6012;
	private static final int MOONSTONE_SHARD = 6013;
	private static final int ROTTEN_BONE = 6014;
	private static final int DEMONS_BLOOD = 6015;
	private static final int INFERNIUM_ORE = 6016;
	private static final int BLOOD_ROOT = 6017;
	private static final int VOLCANIC_ASH = 6018;
	private static final int QUICKSILVER = 6019;
	private static final int SULFUR = 6020;
	private static final int DRACOPLASM = 6021;
	private static final int MAGMA_DUST = 6022;
	private static final int MOON_DUST = 6023;
	private static final int NECROPLASM = 6024;
	private static final int DEMONPLASM = 6025;
	private static final int INFERNO_DUST = 6026;
	private static final int DRACONIC_ESSENCE = 6027;
	private static final int FIRE_ESSENCE = 6028;
	private static final int LUNARGENT = 6029;
	private static final int MIDNIGHT_OIL = 6030;
	private static final int DEMONIC_ESSENCE = 6031;
	private static final int ABYSS_OIL = 6032;
	private static final int HELLFIRE_OIL = 6033;
	private static final int NIGHTMARE_OIL = 6034;
	private static final int MIXING_STONE1 = 5904;
	// Mimir's Elixir items
	private static final int BLOOD_FIRE = 6318;
	private static final int MIMIRS_ELIXIR = 6319;
	private static final int PURE_SILVER = 6320;
	private static final int TRUE_GOLD = 6321;

	private static final int WESLEY = 30166;
	private static final int URN = 31149;

	// MOBs
	private static final int Crendion = 20813;
	private static final int HallatesMaid = 20822;
	private static final int HallatesGuardian = 21061;
	private static final int PlatinumTribeShaman = 20828;
	private static final int PlatinumGuardianShaman = 21066;
	private static final int LavaWyrm = 21111;
	private static final int HamesOrcShaman = 21115;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			0,
			Crendion,
			0,
			QUICKSILVER,
			0,
			38,
			1
		},
		{
			1,
			0,
			Crendion,
			0,
			ROTTEN_BONE,
			0,
			62,
			1
		},
		{
			1,
			0,
			LavaWyrm,
			0,
			WYRMS_BLOOD,
			0,
			50,
			1
		},
		{
			1,
			0,
			LavaWyrm,
			0,
			LAVA_STONE,
			0,
			25,
			1
		},
		{
			1,
			0,
			HallatesGuardian,
			0,
			DEMONS_BLOOD,
			0,
			73,
			1
		},
		{
			1,
			0,
			HallatesGuardian,
			0,
			MOONSTONE_SHARD,
			0,
			11,
			1
		},
		{
			1,
			0,
			HamesOrcShaman,
			0,
			REAGENT_POUCH3,
			0,
			47,
			1
		},
		{
			1,
			0,
			PlatinumTribeShaman,
			0,
			REAGENT_POUCH2,
			0,
			68,
			1
		},
		{
			1,
			0,
			PlatinumTribeShaman,
			0,
			QUICKSILVER,
			0,
			32,
			1
		},
		{
			1,
			0,
			PlatinumGuardianShaman,
			0,
			REAGENT_BOX,
			0,
			44,
			1
		},
		{
			1,
			0,
			HallatesMaid,
			0,
			VOLCANIC_ASH,
			0,
			18,
			1
		},
		{
			1,
			0,
			HallatesMaid,
			0,
			REAGENT_POUCH1,
			0,
			66,
			1
		}
	};

	private static final Object[][] ITEMS =
	{
		{
			4042,
			"etc_gem_red_i00",
			"Enria",
			""
		},
		{
			4043,
			"etc_gem_blue_i00",
			"Asofe",
			""
		},
		{
			4044,
			"etc_gem_clear_i00",
			"Thons",
			""
		},
		{
			2508,
			"etc_piece_bone_red_i00",
			"Cursed Bone",
			""
		},
		{
			735,
			"etc_reagent_green_i00",
			"Potion of Alacrity",
			""
		},
		{
			737,
			"etc_scroll_of_resurrection_i00",
			"Scroll of Resurrection",
			""
		},
		{
			4953,
			"etc_recipe_red_i00",
			"Recipe: Avadon Gloves (60%)",
			""
		},
		{
			4960,
			"etc_recipe_red_i00",
			"Recipe: Zubei's Gauntlets (60%)",
			""
		},
		{
			4959,
			"etc_recipe_red_i00",
			"Recipe: Avadon Boots (60%)",
			""
		},
		{
			4958,
			"etc_recipe_red_i00",
			"Recipe: Zubei's Boots (60%)",
			""
		},
		{
			4998,
			"etc_recipe_red_i00",
			"Recipe: Blue Wolf Gloves (60%)",
			""
		},
		{
			4992,
			"etc_recipe_red_i00",
			"Recipe: Blue Wolf Boots (60%)",
			""
		},
		{
			4993,
			"etc_recipe_red_i00",
			"Recipe: Doom Gloves (60%)",
			""
		},
		{
			4999,
			"etc_recipe_red_i00",
			"Recipe: Doom Boots (60%)",
			""
		},
		{
			5524,
			"etc_letter_red_i00",
			"Sealed Dark Crystal Gaiters Pattern",
			""
		},
		{
			5478,
			"etc_letter_red_i00",
			"Sealed Dark Crystal Leather Armor Pattern",
			""
		},
		{
			5520,
			"etc_letter_red_i00",
			"Sealed Dark Crystal Breastplate Pattern",
			""
		},
		{
			5479,
			"etc_letter_red_i00",
			"Sealled Tallum Leather Armor Pattern",
			""
		},
		{
			5521,
			"etc_letter_red_i00",
			"Sealed Tallum Plate Armor Pattern",
			""
		},
		{
			5480,
			"etc_leather_gray_i00",
			"Sealed Leather Armor of Nightmare Fabric",
			""
		},
		{
			5481,
			"etc_leather_gray_i00",
			"Sealed Majestic Leather Armor Fabric",
			""
		},
		{
			5522,
			"etc_letter_red_i00",
			"Sealed Armor of Nightmare Pattern",
			""
		},
		{
			5523,
			"etc_letter_red_i00",
			"Sealed Majestic Plate Armor Pattern",
			""
		},
		{
			103,
			"shield_tower_shield_i00",
			"Tower Shield",
			"Shield"
		},
		{
			2437,
			"armor_t21_b_i00",
			"Drake Leather Boots",
			"Boots"
		},
		{
			630,
			"shield_square_shield_i00",
			"Square Shield",
			"Shield"
		},
		{
			612,
			"armor_t64_g_i00",
			"Zubei's Gauntlets",
			"Gloves"
		},
		{
			2464,
			"armor_t66_g_i00",
			"Avadon Gloves",
			"Gloves"
		},
		{
			554,
			"armor_t64_b_i00",
			"Zubei's Boots",
			"Boots"
		},
		{
			600,
			"armor_t66_b_i00",
			"Avadon Boots",
			"Boots"
		},
		{
			601,
			"armor_t68_b_i00",
			"Blue Wolf Boots",
			"Boots"
		},
		{
			2439,
			"armor_t71_b_i00",
			"Boots of Doom",
			"Boots"
		},
		{
			2475,
			"armor_t68_g_i00",
			"Blue Wolf Gloves",
			"Gloves"
		},
		{
			2487,
			"armor_t71_g_i00",
			"Doom Gloves",
			"Gloves"
		},
		{
			6011,
			"etc_reagent_red_i00",
			"Wyrm's Blood",
			""
		},
		{
			6012,
			"etc_inf_ore_high_i00",
			"Lava Stone",
			""
		},
		{
			6013,
			"etc_broken_crystal_silver_i00",
			"Moonstone Shard",
			""
		},
		{
			6014,
			"etc_piece_bone_black_i00",
			"Rotten Bone Piece",
			""
		},
		{
			6015,
			"etc_reagent_green_i00",
			"Demon's Blood",
			""
		},
		{
			6016,
			"etc_inf_ore_least_i00",
			"Infernium Ore",
			"Low Level Reagent"
		},
		{
			6017,
			"etc_ginseng_red_i00",
			"Blood Root",
			""
		},
		{
			6018,
			"etc_powder_gray_i00",
			"Volcanic Ash",
			""
		},
		{
			6019,
			"etc_reagent_silver_i00",
			"Quicksilver",
			""
		},
		{
			6020,
			"etc_powder_orange_i00",
			"Sulfur",
			""
		},
		{
			6021,
			"etc_dragons_blood_i05",
			"Dracoplasm",
			"Low Level Reagent"
		},
		{
			6022,
			"etc_powder_red_i00",
			"Magma Dust",
			""
		},
		{
			6023,
			"etc_powder_white_i00",
			"Moon Dust",
			"Low Level Reagent"
		},
		{
			6024,
			"etc_potion_purpel_i00",
			"Necroplasm",
			"Low Level Reagent"
		},
		{
			6025,
			"etc_potion_green_i00",
			"Demonplasm",
			"Low Level Reagent"
		},
		{
			6026,
			"etc_powder_black_i00",
			"Inferno Dust",
			""
		},
		{
			6027,
			"etc_dragon_blood_i00",
			"Draconic Essence",
			"High Level Reagent"
		},
		{
			6028,
			"etc_dragons_blood_i00",
			"Fire Essence",
			"High Level Reagent"
		},
		{
			6029,
			"etc_mithril_ore_i00",
			"Lunargent",
			"High Level Reagent"
		},
		{
			6030,
			"etc_dragons_blood_i02",
			"Midnight Oil",
			"High Level Reagent"
		},
		{
			6031,
			"etc_dragons_blood_i05",
			"Demonic Essence",
			"High Level Reagent"
		},
		{
			6032,
			"etc_dragons_blood_i04",
			"Abyss Oil",
			"High Level Reagent"
		},
		{
			6033,
			"etc_luxury_wine_b_i00",
			"Hellfire Oil",
			"Highest Level Reagent"
		},
		{
			6034,
			"etc_luxury_wine_c_i00",
			"Nightmare Oil",
			"Highest Level Reagent"
		},
		{
			6320,
			"etc_broken_crystal_silver_i00",
			"Pure Silver",
			""
		},
		{
			6321,
			"etc_broken_crystal_gold_i00",
			"True Gold",
			""
		}
	};

	private static final int[][] FORMULAS =
	{
		{
			DRACOPLASM,
			WYRMS_BLOOD,
			10,
			BLOOD_ROOT,
			1
		},
		{
			MAGMA_DUST,
			LAVA_STONE,
			10,
			VOLCANIC_ASH,
			1
		},
		{
			MOON_DUST,
			MOONSTONE_SHARD,
			10,
			VOLCANIC_ASH,
			1
		},
		{
			NECROPLASM,
			ROTTEN_BONE,
			10,
			BLOOD_ROOT,
			1
		},
		{
			DEMONPLASM,
			DEMONS_BLOOD,
			10,
			BLOOD_ROOT,
			1
		},
		{
			INFERNO_DUST,
			INFERNIUM_ORE,
			10,
			VOLCANIC_ASH,
			1
		},
		{
			DRACONIC_ESSENCE,
			DRACOPLASM,
			10,
			QUICKSILVER,
			1
		},
		{
			FIRE_ESSENCE,
			MAGMA_DUST,
			10,
			SULFUR,
			1
		},
		{
			LUNARGENT,
			MOON_DUST,
			10,
			QUICKSILVER,
			1
		},
		{
			MIDNIGHT_OIL,
			NECROPLASM,
			10,
			QUICKSILVER,
			1
		},
		{
			DEMONIC_ESSENCE,
			DEMONPLASM,
			10,
			SULFUR,
			1
		},
		{
			ABYSS_OIL,
			INFERNO_DUST,
			10,
			SULFUR,
			1
		},
		{
			HELLFIRE_OIL,
			FIRE_ESSENCE,
			1,
			DEMONIC_ESSENCE,
			1
		},
		{
			NIGHTMARE_OIL,
			LUNARGENT,
			1,
			MIDNIGHT_OIL,
			1
		},
		{
			PURE_SILVER,
			LUNARGENT,
			1,
			QUICKSILVER,
			1
		},
		{
			MIMIRS_ELIXIR,
			PURE_SILVER,
			1,
			TRUE_GOLD,
			1
		}
	};

	private static final int[][] TEMPERATURE =
	{
		{
			1,
			100,
			1
		},
		{
			2,
			53,
			2
		},
		{
			3,
			36,
			3
		}
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

	public _373_SupplierOfReagents()
	{
		super(true);
		addStartNpc(WESLEY);
		addTalkId(URN);
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}
	}

	public String render_urn(QuestState st, String[] page)
	{
		String html = "noquest";
		int stone = st.getInt("mixing");
		int ingredient = st.getInt("ingredient");
		int catalyst = st.getInt("catalyst");
		if (page == null)
		{
			html = "<html>" + "<body>Alchemists Mixing Urn:" + "<br><table border=0 width=300><tr>" + "<tr><td width=50%>" + "<a action=\"bypass -h Quest _373_SupplierOfReagents U_M_MACT\">MACT Mixing Stone</a></td><td></td></tr>" + "<tr><td><a action=\"bypass -h Quest _373_SupplierOfReagents U_I_IACT\">IACT Ingredients</a></td><td>(current: INGR)</td></tr>" + "<tr><td><a action=\"bypass -h Quest _373_SupplierOfReagents U_C_CACT\">CACT Catalyst</a></td><td>(current: CATA)</td></tr>"
						+ "<tr><td><a action=\"bypass -h Quest _373_SupplierOfReagents 31149-5.htm\">Select Temperature</a></td>" + "<td>(current: TEMP)</td></tr><tr><td><a action=\"bypass -h Quest _373_SupplierOfReagents 31149-6.htm\">Mix Ingredients</a></td><td></td></tr></table></body></html>";
			int ingr = st.getInt("ingredient");
			int cata = st.getInt("catalyst");
			String temp = st.get("temp");
			String r_ingr = "";
			if (ingr != 0)
			{
				for (int i = 0; i < ITEMS.length; i++)
				{
					if ((Integer) ITEMS[i][0] == ingr)
					{
						r_ingr = ITEMS[i][2] + "x" + st.get("i_qty");
					}
				}
			}
			else
			{
				r_ingr = "None";
			}
			String r_cata = "";
			if (cata != 0)
			{
				for (int i = 0; i < ITEMS.length; i++)
				{
					if ((Integer) ITEMS[i][0] == cata)
					{
						r_cata = ITEMS[i][2] + "x" + st.get("c_qty");
					}
				}
			}
			else
			{
				r_cata = "None";
			}
			html = html.replace("INGR", r_ingr).replace("CATA", r_cata).replace("TEMP", temp);
			if (stone != 0)
			{
				html = html.replace("MACT", "Retrieve");
			}
			else
			{
				html = html.replace("MACT", "Insert");
			}
			if (ingredient != 0)
			{
				html = html.replace("IACT", "Retrieve");
			}
			else
			{
				html = html.replace("IACT", "Insert");
			}
			if (catalyst != 0)
			{
				html = html.replace("CACT", "Retrieve");
			}
			else
			{
				html = html.replace("CACT", "Insert");
			}
		}
		else
		{
			html = "<html><body>Insert:<table border=0>";
			int amt = 0;
			int item = 0;
			for (int i = 0; i < ITEMS.length; i++)
			{
				item = (Integer) ITEMS[i][0];
				if (item >= 6011 && item <= 6031 || item >= 6320 && item <= 6321)
				{
					if (st.getQuestItemsCount(item) > 0)
					{
						amt += 1;
						html += "<tr><td height=45><img src=icon." + ITEMS[i][1] + " height=32 width=32></td><td width=180>" + ITEMS[i][2] + "</td><td><button value=X1 action=\"bypass -h Quest _373_SupplierOfReagents x_1_" + page[1] + "_" + str(item) + "\" width=40 height=15 fore=sek.cbui92><button value=X10 action=\"bypass -h Quest _373_SupplierOfReagents x_2_" + page[1] + "_" + str(item) + "\" width=40 height=15 fore=sek.cbui92></td></tr>";
					}
				}
			}
			if (amt == 0)
			{
				html += "<tr><td>You don't have any material that could be used with this Urn. Read the Mixing Manual.</td></tr>";
				html += "</table><center><a action=\"bypass -h Quest _373_SupplierOfReagents urn\">Back</a></center></body></html>";
			}
		}
		return html;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30166-4.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.set("ingredient", "0");
			st.set("catalyst", "0");
			st.set("i_qty", "0");
			st.set("c_qty", "0");
			st.set("temp", "0");
			st.set("mixing", "0");
			st.giveItems(6317, 1);
			st.giveItems(5904, 1);
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("30166-5.htm"))
		{
			st.exitCurrentQuest(true);
			st.playSound("ItemSound.quest_finish");
		}
		else if (event.equalsIgnoreCase("urn"))
		{
			htmltext = render_urn(st, null);
		}
		else if (event.startsWith("U"))
		{
			String[] s_event = event.split("_");
			if (s_event[1].equals("M"))
			{
				if (s_event[2].equals("Insert"))
				{
					if (st.getQuestItemsCount(MIXING_STONE1) != 0)
					{
						st.takeItems(MIXING_STONE1, -1);
						st.set("mixing", "1");
						htmltext = "31149-2.htm";
					}
					else
					{
						htmltext = "You don't have a mixing stone.";
					}
				}
				else if (s_event[2].equals("Retrieve"))
				{
					if (st.getInt("mixing") != 0)
					{
						st.set("mixing", "0");
						st.set("temp", "0");
						st.giveItems(MIXING_STONE1, 1);
						if (st.getInt("ingredient") > 0 || st.getInt("catalyst") > 0)
						{
							htmltext = "31149-2c.htm";
						}
						else
						{
							htmltext = "31149-2a.htm";
						}
					}
					else
					{
						htmltext = "31149-2b.htm";
					}
				}
			}
			else if (s_event[2].equals("Insert"))
			{
				htmltext = render_urn(st, s_event);
			}
			else if (s_event[2].equals("Retrieve"))
			{
				int item = 0;
				int qty = 0;
				if (s_event[1].equals("I"))
				{
					item = st.getInt("ingredient");
					qty = st.getInt("i_qty");
					st.set("ingredient", "0");
					st.set("i_qty", "0");
				}
				else if (s_event[1].equals("C"))
				{
					item = st.getInt("catalyst");
					qty = st.getInt("c_qty");
					st.set("catalyst", "0");
					st.set("c_qty", "0");
				}
				if (item > 0 && qty > 0)
				{
					st.giveItems(item, qty);
					htmltext = "31149-3a.htm";
				}
				else
				{
					htmltext = "31149-3b.htm";
				}
			}
		}
		else if (event.startsWith("x"))
		{
			String[] s_event = event.split("_");
			int qty = Integer.valueOf(s_event[1]);
			String dst = s_event[2];
			int item = Integer.valueOf(s_event[3]);
			String dest;
			String count;
			if (qty == 2)
			{
				qty = 10;
			}
			else
			{
				qty = 1;
			}
			if (st.getQuestItemsCount(item) >= qty)
			{
				if (dst.equals("I"))
				{
					dest = "ingredient";
					count = "i_qty";
				}
				else
				{
					dest = "catalyst";
					count = "c_qty";
				}
				st.takeItems(item, qty);
				st.set(dest, String.valueOf(item));
				st.set(count, String.valueOf(qty));
				htmltext = "31149-4a.htm";
			}
			else
			{
				htmltext = "31149-4b.htm";
			}
		}
		else if (event.startsWith("tmp"))
		{
			st.set("temp", event.split("_")[1]);
			htmltext = "31149-5a.htm";
		}
		else if (event.equalsIgnoreCase("31149-6.htm"))
		{
			if (st.getInt("mixing") > 0)
			{
				int temp = st.getInt("temp");
				if (temp != 0)
				{
					int ingredient = st.getInt("ingredient");
					int catalyst = st.getInt("catalyst");
					int iq = st.getInt("i_qty");
					int cq = st.getInt("c_qty");
					st.set("ingredient", "0");
					st.set("i_qty", "0");
					st.set("catalyst", "0");
					st.set("c_qty", "0");
					st.set("temp", "0");
					int item = 0;
					for (int i = 0; i < FORMULAS.length; i++)
					{
						if (ingredient == FORMULAS[i][1] && catalyst == FORMULAS[i][3] && iq == FORMULAS[i][2] && cq == FORMULAS[i][4] || ingredient == FORMULAS[i][3] && catalyst == FORMULAS[i][1] && iq == FORMULAS[i][4] && cq == FORMULAS[i][2])
						{
							item = FORMULAS[i][0];
							break;
						}
					}
					if (item == PURE_SILVER && temp != 1)
					{
						return "31149-7c.htm";
					}
					if (item == MIMIRS_ELIXIR)
					{
						if (temp == 3)
						{
							if (st.getQuestItemsCount(BLOOD_FIRE) > 0)
							{
								st.takeItems(BLOOD_FIRE, 1);
							}
							else
							{
								return "31149-7a.htm";
							}
						}
						else
						{
							return "31149-7b.htm";
						}
					}
					if (item > 0)
					{
						int chance = 0;
						int qty = 0;
						for (int i = 0; i < TEMPERATURE.length; i++)
						{
							if (TEMPERATURE[i][0] == temp)
							{
								chance = TEMPERATURE[i][1];
								qty = TEMPERATURE[i][2];
							}
						}
						if (item == MIMIRS_ELIXIR)
						{
							QuestState mimirs = st.getPlayer().getQuestState(_235_MimirsElixir.class);
							if (mimirs != null)
							{
								chance = 100;
								qty = 1;
								mimirs.setCond(8);
							}
							else
							{
								return "31149-7d.htm";
							}
						}
						if (Rnd.chance(chance))
						{
							st.giveItems(item, qty);
						}
						else
						{
							htmltext = "31149-6c.htm";
						}
					}
					else
					{
						htmltext = "31149-6d.htm";
					}
				}
				else
				{
					htmltext = "31149-6b.htm";
				}
			}
			else
			{
				htmltext = "31149-6a.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == WESLEY)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() < 57)
				{
					st.exitCurrentQuest(true);
					htmltext = "30166-2.htm";
				}
				else
				{
					htmltext = "30166-1.htm";
				}
			}
			else
			{
				htmltext = "30166-3.htm";
			}
		}
		else if (npcId == URN && cond == 1)
		{
			htmltext = render_urn(st, null);
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		for (int[] i : DROPLIST_COND)
		{
			if (npcId == i[2])
			{
				st.rollAndGive(i[4], i[7], i[6]);
			}
		}
		return null;
	}
}