package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест The Wishing Potion
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _334_TheWishingPotion extends Quest implements ScriptFile
{
	// NPC
	private static final int GRIMA = 27135;
	private static final int SUCCUBUS_OF_SEDUCTION = 27136;
	private static final int GREAT_DEMON_KING = 27138;
	private static final int SECRET_KEEPER_TREE = 27139;
	private static final int SANCHES = 27153;
	private static final int BONAPARTERIUS = 27154;
	private static final int RAMSEBALIUS = 27155;
	private static final int TORAI = 30557;
	private static final int ALCHEMIST_MATILD = 30738;
	private static final int RUPINA = 30742;
	private static final int WISDOM_CHEST = 30743;
	// Mobs
	private static final int WHISPERING_WIND = 20078;
	private static final int ANT_SOLDIER = 20087;
	private static final int ANT_WARRIOR_CAPTAIN = 20088;
	private static final int SILENOS = 20168;
	private static final int TYRANT = 20192;
	private static final int TYRANT_KINGPIN = 20193;
	private static final int AMBER_BASILISK = 20199;
	private static final int HORROR_MIST_RIPPER = 20227;
	private static final int TURAK_BUGBEAR = 20248;
	private static final int TURAK_BUGBEAR_WARRIOR = 20249;
	private static final int GLASS_JAGUAR = 20250;
	// Item
	private static final int DEMONS_TUNIC_ID = 441;
	private static final int DEMONS_STOCKINGS_ID = 472;
	private static final int SCROLL_OF_ESCAPE_ID = 736;
	private static final int NECKLACE_OF_GRACE_ID = 931;
	private static final int SPELLBOOK_ICEBOLT_ID = 1049;
	private static final int SPELLBOOK_BATTLEHEAL_ID = 1050;
	private static final int DEMONS_BOOTS_ID = 2435;
	private static final int DEMONS_GLOVES_ID = 2459;
	private static final int WISH_POTION_ID = 3467;
	private static final int ANCIENT_CROWN_ID = 3468;
	private static final int CERTIFICATE_OF_ROYALTY_ID = 3469;
	private static final int GOLD_BAR_ID = 3470;
	private static final int ALCHEMY_TEXT_ID = 3678;
	private static final int SECRET_BOOK_ID = 3679;
	private static final int POTION_RECIPE_1_ID = 3680;
	private static final int POTION_RECIPE_2_ID = 3681;
	private static final int MATILDS_ORB_ID = 3682;
	private static final int FORBIDDEN_LOVE_SCROLL_ID = 3683;
	private static final int HEART_OF_PAAGRIO_ID = 3943;
	// Quest Item
	private static final int AMBER_SCALE_ID = 3684;
	private static final int WIND_SOULSTONE_ID = 3685;
	private static final int GLASS_EYE_ID = 3686;
	private static final int HORROR_ECTOPLASM_ID = 3687;
	private static final int SILENOS_HORN_ID = 3688;
	private static final int ANT_SOLDIER_APHID_ID = 3689;
	private static final int TYRANTS_CHITIN_ID = 3690;
	private static final int BUGBEAR_BLOOD_ID = 3691;
	// Chance
	private static final int DROP_CHANCE_FORBIDDEN_LOVE_SCROLL_ID = 3;
	private static final int DROP_CHANCE_NECKLACE_OF_GRACE_ID = 4;
	private static final int DROP_CHANCE_GOLD_BAR_ID = 10;

	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			2,
			SECRET_KEEPER_TREE,
			0,
			SECRET_BOOK_ID,
			1,
			100,
			1
		},
		{
			3,
			0,
			AMBER_BASILISK,
			0,
			AMBER_SCALE_ID,
			1,
			15,
			1
		},
		{
			3,
			0,
			WHISPERING_WIND,
			0,
			WIND_SOULSTONE_ID,
			1,
			20,
			1
		},
		{
			3,
			0,
			GLASS_JAGUAR,
			0,
			GLASS_EYE_ID,
			1,
			35,
			1
		},
		{
			3,
			0,
			HORROR_MIST_RIPPER,
			0,
			HORROR_ECTOPLASM_ID,
			1,
			15,
			1
		},
		{
			3,
			0,
			SILENOS,
			0,
			SILENOS_HORN_ID,
			1,
			30,
			1
		},
		{
			3,
			0,
			ANT_SOLDIER,
			0,
			ANT_SOLDIER_APHID_ID,
			1,
			40,
			1
		},
		{
			3,
			0,
			ANT_WARRIOR_CAPTAIN,
			0,
			ANT_SOLDIER_APHID_ID,
			1,
			40,
			1
		},
		{
			3,
			0,
			TYRANT,
			0,
			TYRANTS_CHITIN_ID,
			1,
			50,
			1
		},
		{
			3,
			0,
			TYRANT_KINGPIN,
			0,
			TYRANTS_CHITIN_ID,
			1,
			50,
			1
		},
		{
			3,
			0,
			TURAK_BUGBEAR,
			0,
			BUGBEAR_BLOOD_ID,
			1,
			15,
			1
		},
		{
			3,
			0,
			TURAK_BUGBEAR_WARRIOR,
			0,
			BUGBEAR_BLOOD_ID,
			1,
			25,
			1
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

	public _334_TheWishingPotion()
	{
		super(false);

		addStartNpc(ALCHEMIST_MATILD);
		addTalkId(ALCHEMIST_MATILD);
		addTalkId(TORAI);
		addTalkId(WISDOM_CHEST);
		addTalkId(RUPINA);

		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}

		addQuestItem(new int[]
		{
			ALCHEMY_TEXT_ID,
			SECRET_BOOK_ID,
			AMBER_SCALE_ID,
			WIND_SOULSTONE_ID,
			GLASS_EYE_ID,
			HORROR_ECTOPLASM_ID,
			SILENOS_HORN_ID,
			ANT_SOLDIER_APHID_ID,
			TYRANTS_CHITIN_ID,
			BUGBEAR_BLOOD_ID
		});
	}

	public boolean checkIngr(QuestState st)
	{
		if (st.getQuestItemsCount(AMBER_SCALE_ID) == 1 && st.getQuestItemsCount(WIND_SOULSTONE_ID) == 1 && st.getQuestItemsCount(GLASS_EYE_ID) == 1 && st.getQuestItemsCount(HORROR_ECTOPLASM_ID) == 1 && st.getQuestItemsCount(SILENOS_HORN_ID) == 1 && st.getQuestItemsCount(ANT_SOLDIER_APHID_ID) == 1 && st.getQuestItemsCount(TYRANTS_CHITIN_ID) == 1 && st.getQuestItemsCount(BUGBEAR_BLOOD_ID) == 1)
		{
			st.setCond(4);
			return true;
		}
		st.setCond(3);
		return false;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30738-03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.giveItems(ALCHEMY_TEXT_ID, 1);
		}
		else if (event.equalsIgnoreCase("30738-06.htm"))
		{
			if (st.getQuestItemsCount(WISH_POTION_ID) == 0)
			{
				st.takeItems(ALCHEMY_TEXT_ID, -1);
				st.takeItems(SECRET_BOOK_ID, -1);
				if (st.getQuestItemsCount(POTION_RECIPE_1_ID) == 0)
				{
					st.giveItems(POTION_RECIPE_1_ID, 1);
				}
				if (st.getQuestItemsCount(POTION_RECIPE_2_ID) == 0)
				{
					st.giveItems(POTION_RECIPE_2_ID, 1);
				}
				if (st.getQuestItemsCount(MATILDS_ORB_ID) == 0)
				{
					htmltext = "30738-06.htm";
				}
				else
				{
					htmltext = "30738-12.htm";
				}
				st.setCond(3);
			}
			else if (st.getQuestItemsCount(MATILDS_ORB_ID) >= 1 && st.getQuestItemsCount(WISH_POTION_ID) >= 1)
			{
				htmltext = "30738-13.htm";
			}
		}
		else if (event.equalsIgnoreCase("30738-10.htm"))
		{
			if (checkIngr(st))
			{
				st.playSound(SOUND_FINISH);
				st.takeItems(ALCHEMY_TEXT_ID, -1);
				st.takeItems(SECRET_BOOK_ID, -1);
				st.takeItems(POTION_RECIPE_1_ID, -1);
				st.takeItems(POTION_RECIPE_2_ID, -1);
				st.takeItems(AMBER_SCALE_ID, -1);
				st.takeItems(WIND_SOULSTONE_ID, -1);
				st.takeItems(GLASS_EYE_ID, -1);
				st.takeItems(HORROR_ECTOPLASM_ID, -1);
				st.takeItems(SILENOS_HORN_ID, -1);
				st.takeItems(ANT_SOLDIER_APHID_ID, -1);
				st.takeItems(TYRANTS_CHITIN_ID, -1);
				st.takeItems(BUGBEAR_BLOOD_ID, -1);
				if (st.getQuestItemsCount(MATILDS_ORB_ID) == 0)
				{
					st.giveItems(MATILDS_ORB_ID, 1);
				}
				st.giveItems(WISH_POTION_ID, 1);
				st.setCond(0);
			}
			else
			{
				htmltext = "<html><head><body>You don't have required items</body></html>";
			}
		}
		else if (event.equalsIgnoreCase("30738-14.htm"))
		{
			if (st.getQuestItemsCount(WISH_POTION_ID) >= 1)
			{
				htmltext = "30738-15.htm";
			}
		}
		// WISH 1
		else if (event.equalsIgnoreCase("30738-16.htm"))
		{
			if (st.getQuestItemsCount(WISH_POTION_ID) >= 1)
			{
				st.takeItems(WISH_POTION_ID, 1);
				if (Rnd.chance(50))
				{
					st.addSpawn(SUCCUBUS_OF_SEDUCTION);
					st.addSpawn(SUCCUBUS_OF_SEDUCTION);
					st.addSpawn(SUCCUBUS_OF_SEDUCTION);
				}
				else
				{
					st.addSpawn(RUPINA);
				}
			}
			else
			{
				htmltext = "30738-14.htm";
			}
		}
		// WISH 2
		else if (event.equalsIgnoreCase("30738-17.htm"))
		{
			if (st.getQuestItemsCount(WISH_POTION_ID) >= 1)
			{
				st.takeItems(WISH_POTION_ID, 1);
				int WISH_CHANCE = Rnd.get(100) + 1;
				if (WISH_CHANCE <= 33)
				{
					st.addSpawn(GRIMA);
					st.addSpawn(GRIMA);
					st.addSpawn(GRIMA);
				}
				else if (WISH_CHANCE >= 66)
				{
					st.giveItems(ADENA_ID, 10000);
				}
				else if (Rnd.chance(2))
				{
					st.giveItems(ADENA_ID, ((Rnd.get(10) + 1) * 1000000L));
				}
				else
				{
					st.addSpawn(GRIMA);
					st.addSpawn(GRIMA);
					st.addSpawn(GRIMA);
				}
			}
			else
			{
				htmltext = "30738-14.htm";
			}

		}
		// WISH 3
		else if (event.equalsIgnoreCase("30738-18.htm"))
		{
			if (st.getQuestItemsCount(WISH_POTION_ID) >= 1)
			{
				st.takeItems(WISH_POTION_ID, 1);
				int WISH_CHANCE = Rnd.get(100) + 1;
				if (WISH_CHANCE <= 33)
				{
					st.giveItems(CERTIFICATE_OF_ROYALTY_ID, 1);
				}
				else if (WISH_CHANCE >= 66)
				{
					st.giveItems(ANCIENT_CROWN_ID, 1);
				}
				else
				{
					st.addSpawn(SANCHES);
				}
			}
			else
			{
				htmltext = "30738-14.htm";
			}

		}
		// WISH 4
		else if (event.equalsIgnoreCase("30738-19.htm"))
		{
			if (st.getQuestItemsCount(WISH_POTION_ID) >= 1)
			{
				st.takeItems(WISH_POTION_ID, 1);
				int WISH_CHANCE = Rnd.get(100) + 1;
				if (WISH_CHANCE <= 33)
				{
					st.giveItems(SPELLBOOK_ICEBOLT_ID, 1);
				}
				else if (WISH_CHANCE <= 66)
				{
					st.giveItems(SPELLBOOK_BATTLEHEAL_ID, 1);
				}
				else
				{
					st.addSpawn(WISDOM_CHEST);
				}
			}
			else
			{
				htmltext = "30738-14.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = 0;
		if (id != CREATED)
		{
			cond = st.getCond();
		}
		switch (npcId)
		{
		case ALCHEMIST_MATILD:
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() <= 29)
				{
					htmltext = "30738-21.htm";
					st.exitCurrentQuest(true);
				}
				else if (st.getQuestItemsCount(MATILDS_ORB_ID) == 0)
				{
					htmltext = "30738-01.htm";
				}
				else if (st.getQuestItemsCount(WISH_POTION_ID) == 0)
				{
					st.setCond(3);
					if (st.getQuestItemsCount(POTION_RECIPE_1_ID) == 0)
					{
						st.giveItems(POTION_RECIPE_1_ID, 1);
					}
					if (st.getQuestItemsCount(POTION_RECIPE_2_ID) == 0)
					{
						st.giveItems(POTION_RECIPE_2_ID, 1);
					}
					htmltext = "30738-12.htm";
				}
				else
				{
					htmltext = "30738-11.htm";
				}
			}
			else if (cond == 1 && st.getQuestItemsCount(ALCHEMY_TEXT_ID) == 1)
			{
				htmltext = "30738-04.htm";
			}
			else if (cond == 2)
			{
				if (st.getQuestItemsCount(SECRET_BOOK_ID) == 1 && st.getQuestItemsCount(ALCHEMY_TEXT_ID) == 1)
				{
					htmltext = "30738-05.htm";
				}
			}
			else if (cond == 4)
			{
				if (checkIngr(st))
				{
					htmltext = "30738-08.htm";
				}
				else
				{
					htmltext = "30738-07.htm";
				}
			}
			break;
		case TORAI:
			if (st.getQuestItemsCount(FORBIDDEN_LOVE_SCROLL_ID) >= 1)
			{
				st.takeItems(FORBIDDEN_LOVE_SCROLL_ID, 1);
				st.giveItems(ADENA_ID, 500000);
				htmltext = "30557-01.htm";
			}
			else
			{
				htmltext = "noquest";
			}
			break;
		case WISDOM_CHEST:
		{
			int DROP_CHANCE = Rnd.get(100) + 1;
			if (DROP_CHANCE <= 20)
			{
				st.giveItems(SPELLBOOK_ICEBOLT_ID, 1);
				st.giveItems(SPELLBOOK_BATTLEHEAL_ID, 1);
				st.getPlayer().getTarget().decayMe();
				htmltext = "30743-06.htm";
			}
			else if (DROP_CHANCE <= 30)
			{
				st.giveItems(HEART_OF_PAAGRIO_ID, 1);
				st.getPlayer().getTarget().decayMe();
				htmltext = "30743-06.htm";
			}
			else
			{
				st.getPlayer().getTarget().decayMe();
				htmltext = "30743-0" + String.valueOf(Rnd.get(5) + 1) + ".htm";
			}
			break;
		}
		case RUPINA:
		{
			int DROP_CHANCE = Rnd.get(100) + 1;
			if (DROP_CHANCE <= DROP_CHANCE_NECKLACE_OF_GRACE_ID)
			{
				st.giveItems(NECKLACE_OF_GRACE_ID, 1);
			}
			else
			{
				st.giveItems(SCROLL_OF_ESCAPE_ID, 1);
			}
			st.getPlayer().getTarget().decayMe();
			htmltext = "30742-01.htm";
			break;
		}
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			if (cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2])
			{
				if (DROPLIST_COND[i][3] == 0 || st.getQuestItemsCount(DROPLIST_COND[i][3]) > 0)
				{
					if (DROPLIST_COND[i][5] == 0)
					{
						st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
					}
					else if (st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]))
					{
						if (cond == 3)
						{
							checkIngr(st);
						}
						if (DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0)
						{
							st.setCond(Integer.valueOf(DROPLIST_COND[i][1]));
							st.setState(STARTED);
						}
					}
				}
			}
		}
		int DROP_CHANCE = Rnd.get(100) + 1;
		if (npcId == SUCCUBUS_OF_SEDUCTION && DROP_CHANCE <= DROP_CHANCE_FORBIDDEN_LOVE_SCROLL_ID)
		{
			st.playSound(SOUND_ITEMGET);
			st.giveItems(FORBIDDEN_LOVE_SCROLL_ID, 1);
		}
		else if (npcId == GRIMA && DROP_CHANCE <= DROP_CHANCE_GOLD_BAR_ID)
		{
			st.playSound(SOUND_ITEMGET);
			st.giveItems(GOLD_BAR_ID, Rnd.get(5) + 1);
		}
		else if (npcId == SANCHES && Rnd.chance(51))
		{
			st.addSpawn(BONAPARTERIUS);
		}
		else if (npcId == BONAPARTERIUS && Rnd.chance(51))
		{
			st.addSpawn(RAMSEBALIUS);
		}
		else if (npcId == RAMSEBALIUS && Rnd.chance(51))
		{
			st.addSpawn(GREAT_DEMON_KING);
		}
		else if (npcId == GREAT_DEMON_KING && Rnd.chance(51))
		{
			if (DROP_CHANCE <= 25)
			{
				st.giveItems(DEMONS_BOOTS_ID, 1);
			}
			else if (DROP_CHANCE <= 50)
			{
				st.giveItems(DEMONS_GLOVES_ID, 1);
			}
			else if (DROP_CHANCE <= 75)
			{
				st.giveItems(DEMONS_STOCKINGS_ID, 1);
			}
			else
			{
				st.giveItems(DEMONS_TUNIC_ID, 1);
			}
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}