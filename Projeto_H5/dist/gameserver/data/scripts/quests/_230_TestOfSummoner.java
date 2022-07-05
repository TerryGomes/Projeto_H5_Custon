package quests;

import java.util.HashMap;
import java.util.Map;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Summon;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _230_TestOfSummoner extends Quest implements ScriptFile
{
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

	static int MARK_OF_SUMMONER_ID = 3336;
	static int LETOLIZARDMAN_AMULET_ID = 3337;
	static int SAC_OF_REDSPORES_ID = 3338;
	static int KARULBUGBEAR_TOTEM_ID = 3339;
	static int SHARDS_OF_MANASHEN_ID = 3340;
	static int BREKAORC_TOTEM_ID = 3341;
	static int CRIMSON_BLOODSTONE_ID = 3342;
	static int TALONS_OF_TYRANT_ID = 3343;
	static int WINGS_OF_DRONEANT_ID = 3344;
	static int TUSK_OF_WINDSUS_ID = 3345;
	static int FANGS_OF_WYRM_ID = 3346;
	static int LARS_LIST1_ID = 3347;
	static int LARS_LIST2_ID = 3348;
	static int LARS_LIST3_ID = 3349;
	static int LARS_LIST4_ID = 3350;
	static int LARS_LIST5_ID = 3351;
	static int GALATEAS_LETTER_ID = 3352;
	static int BEGINNERS_ARCANA_ID = 3353;
	static int ALMORS_ARCANA_ID = 3354;
	static int CAMONIELL_ARCANA_ID = 3355;
	static int BELTHUS_ARCANA_ID = 3356;
	static int BASILLIA_ARCANA_ID = 3357;
	static int CELESTIEL_ARCANA_ID = 3358;
	static int BRYNTHEA_ARCANA_ID = 3359;
	static int CRYSTAL_OF_PROGRESS1_ID = 3360;
	static int CRYSTAL_OF_INPROGRESS1_ID = 3361;
	static int CRYSTAL_OF_FOUL1_ID = 3362;
	static int CRYSTAL_OF_DEFEAT1_ID = 3363;
	static int CRYSTAL_OF_VICTORY1_ID = 3364;
	static int CRYSTAL_OF_PROGRESS2_ID = 3365;
	static int CRYSTAL_OF_INPROGRESS2_ID = 3366;
	static int CRYSTAL_OF_FOUL2_ID = 3367;
	static int CRYSTAL_OF_DEFEAT2_ID = 3368;
	static int CRYSTAL_OF_VICTORY2_ID = 3369;
	static int CRYSTAL_OF_PROGRESS3_ID = 3370;
	static int CRYSTAL_OF_INPROGRESS3_ID = 3371;
	static int CRYSTAL_OF_FOUL3_ID = 3372;
	static int CRYSTAL_OF_DEFEAT3_ID = 3373;
	static int CRYSTAL_OF_VICTORY3_ID = 3374;
	static int CRYSTAL_OF_PROGRESS4_ID = 3375;
	static int CRYSTAL_OF_INPROGRESS4_ID = 3376;
	static int CRYSTAL_OF_FOUL4_ID = 3377;
	static int CRYSTAL_OF_DEFEAT4_ID = 3378;
	static int CRYSTAL_OF_VICTORY4_ID = 3379;
	static int CRYSTAL_OF_PROGRESS5_ID = 3380;
	static int CRYSTAL_OF_INPROGRESS5_ID = 3381;
	static int CRYSTAL_OF_FOUL5_ID = 3382;
	static int CRYSTAL_OF_DEFEAT5_ID = 3383;
	static int CRYSTAL_OF_VICTORY5_ID = 3384;
	static int CRYSTAL_OF_PROGRESS6_ID = 3385;
	static int CRYSTAL_OF_INPROGRESS6_ID = 3386;
	static int CRYSTAL_OF_FOUL6_ID = 3387;
	static int CRYSTAL_OF_DEFEAT6_ID = 3388;
	static int CRYSTAL_OF_VICTORY6_ID = 3389;

	static int[] npc =
	{
		30063,
		30634,
		30635,
		30636,
		30637,
		30638,
		30639,
		30640
	};
	static int Lara = npc[0];
	static int Galatea = npc[1];
	static int Almors = npc[2];
	static int Camoniell = npc[3];
	static int Belthus = npc[4];
	static int Basilla = npc[5];
	static int Celestiel = npc[6];
	static int Brynthea = npc[7];

	static int[][] SUMMONERS =
	{
		{
			30635,
			ALMORS_ARCANA_ID,
			CRYSTAL_OF_VICTORY1_ID
		},
		// Almors
		{
			30636,
			CAMONIELL_ARCANA_ID,
			CRYSTAL_OF_VICTORY2_ID
		},
		// Camoniell
		{
			30637,
			BELTHUS_ARCANA_ID,
			CRYSTAL_OF_VICTORY3_ID
		},
		// Belthus
		{
			30638,
			BASILLIA_ARCANA_ID,
			CRYSTAL_OF_VICTORY4_ID
		},
		// Basilla
		{
			30639,
			CELESTIEL_ARCANA_ID,
			CRYSTAL_OF_VICTORY5_ID
		},
		// Celestiel
		{
			30640,
			BRYNTHEA_ARCANA_ID,
			CRYSTAL_OF_VICTORY6_ID
		}
		// Brynthea
	};

	static Map<Integer, String> NAMES = new HashMap<Integer, String>();

	static
	{
		NAMES.put(30635, "Almors");
		NAMES.put(30636, "Camoniell");
		NAMES.put(30637, "Belthus");
		NAMES.put(30638, "Basilla");
		NAMES.put(30639, "Celestiel");
		NAMES.put(30640, "Brynthea");
	}

	static Map<Integer, Integer[]> DROPLIST_LARA = new HashMap<Integer, Integer[]>();

	static
	{
		DROPLIST_LARA.put(20555, new Integer[]
		{
			1,
			80,
			SAC_OF_REDSPORES_ID
		});
		DROPLIST_LARA.put(20557, new Integer[]
		{
			1,
			25,
			LETOLIZARDMAN_AMULET_ID
		});
		DROPLIST_LARA.put(20558, new Integer[]
		{
			1,
			25,
			LETOLIZARDMAN_AMULET_ID
		});
		DROPLIST_LARA.put(20559, new Integer[]
		{
			1,
			25,
			LETOLIZARDMAN_AMULET_ID
		});
		DROPLIST_LARA.put(20580, new Integer[]
		{
			1,
			50,
			LETOLIZARDMAN_AMULET_ID
		});
		DROPLIST_LARA.put(20581, new Integer[]
		{
			1,
			75,
			LETOLIZARDMAN_AMULET_ID
		});
		DROPLIST_LARA.put(20582, new Integer[]
		{
			1,
			75,
			LETOLIZARDMAN_AMULET_ID
		});
		DROPLIST_LARA.put(20600, new Integer[]
		{
			2,
			80,
			KARULBUGBEAR_TOTEM_ID
		});
		DROPLIST_LARA.put(20563, new Integer[]
		{
			2,
			80,
			SHARDS_OF_MANASHEN_ID
		});
		DROPLIST_LARA.put(20552, new Integer[]
		{
			3,
			60,
			CRIMSON_BLOODSTONE_ID
		});
		DROPLIST_LARA.put(20267, new Integer[]
		{
			3,
			25,
			BREKAORC_TOTEM_ID
		});
		DROPLIST_LARA.put(20268, new Integer[]
		{
			3,
			25,
			BREKAORC_TOTEM_ID
		});
		DROPLIST_LARA.put(20271, new Integer[]
		{
			3,
			25,
			BREKAORC_TOTEM_ID
		});
		DROPLIST_LARA.put(20269, new Integer[]
		{
			3,
			50,
			BREKAORC_TOTEM_ID
		});
		DROPLIST_LARA.put(20270, new Integer[]
		{
			3,
			50,
			BREKAORC_TOTEM_ID
		});
		DROPLIST_LARA.put(20553, new Integer[]
		{
			4,
			70,
			TUSK_OF_WINDSUS_ID
		});
		DROPLIST_LARA.put(20192, new Integer[]
		{
			4,
			50,
			TALONS_OF_TYRANT_ID
		});
		DROPLIST_LARA.put(20193, new Integer[]
		{
			4,
			50,
			TALONS_OF_TYRANT_ID
		});
		DROPLIST_LARA.put(20089, new Integer[]
		{
			5,
			30,
			WINGS_OF_DRONEANT_ID
		});
		DROPLIST_LARA.put(20090, new Integer[]
		{
			5,
			60,
			WINGS_OF_DRONEANT_ID
		});
		DROPLIST_LARA.put(20176, new Integer[]
		{
			5,
			50,
			FANGS_OF_WYRM_ID
		});
	}

	static String[] STATS =
	{
		"cond",
		"step",
		"Lara_Part",
		"Arcanas",
		"Belthus",
		"Brynthea",
		"Celestiel",
		"Camoniell",
		"Basilla",
		"Almors"
	};

	static int[][] LISTS =
	{
		{},
		// zero element filler
		{
			LARS_LIST1_ID,
			SAC_OF_REDSPORES_ID,
			LETOLIZARDMAN_AMULET_ID
		},
		// List 1
		{
			LARS_LIST2_ID,
			KARULBUGBEAR_TOTEM_ID,
			SHARDS_OF_MANASHEN_ID
		},
		// List 2
		{
			LARS_LIST3_ID,
			CRIMSON_BLOODSTONE_ID,
			BREKAORC_TOTEM_ID
		},
		// List 3
		{
			LARS_LIST4_ID,
			TUSK_OF_WINDSUS_ID,
			TALONS_OF_TYRANT_ID
		},
		// List 4
		{
			LARS_LIST5_ID,
			WINGS_OF_DRONEANT_ID,
			FANGS_OF_WYRM_ID
		}
		// List 5
	};

	public _230_TestOfSummoner()
	{
		super(false);

		addStartNpc(Galatea);

		for (int npcId : npc)
		{
			addTalkId(npcId);
		}
		for (int mobId : DROPLIST_LARA.keySet())
		{
			addKillId(mobId);
		}
		for (int mobId : DROPLIST_SUMMON.keySet())
		{
			addKillId(mobId);
			addAttackId(mobId);
		}
		for (int i = 3337; i <= 3389; i++)
		{
			addQuestItem(i);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30634-08.htm"))
		{ // start part for Galatea
			for (String var : STATS)
			{
				if (var.equalsIgnoreCase("Arcanas") || var.equalsIgnoreCase("Lara_Part"))
				{
					continue;
				}
				st.set(var, "1");
			}
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			if (!st.getPlayer().getVarB("dd3"))
			{
				st.giveItems(7562, 122, false);
				st.getPlayer().setVar("dd3", "1", -1);
			}
		}
		else if (event.equalsIgnoreCase("30634-07.htm"))
		{
			st.giveItems(GALATEAS_LETTER_ID, 1, false);
		}
		else if (event.equalsIgnoreCase("30063-02.htm"))
		{ // Lara first time to give a list out
			int random = Rnd.get(5) + 1;
			st.giveItems(LISTS[random][0], 1, false);
			st.takeItems(GALATEAS_LETTER_ID, 1);
			st.set("Lara_Part", str(random));
			st.set("step", "2");
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("30063-04.htm"))
		{ // Lara later to give a list out
			int random = Rnd.get(5) + 1;
			st.giveItems(LISTS[random][0], 1, false);
			st.set("Lara_Part", str(random));
		}
		else if (event.equalsIgnoreCase("30635-02.htm"))
		{ // Almors' Part, this is the same just other items below.. so just one time comments
			if (st.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0)
			{ // if( the player has more then one beginners' arcana he can start a fight against the masters summon
				htmltext = "30635-03.htm";
				st.set("Almors", "2");
			}
		} // set state ready to fight
		else if (event.equalsIgnoreCase("30635-04.htm"))
		{
			st.giveItems(CRYSTAL_OF_PROGRESS1_ID, 1, false); // give Starting Crystal
			st.takeItems(CRYSTAL_OF_FOUL1_ID, -1); // just in case he cheated or loses
			st.takeItems(CRYSTAL_OF_DEFEAT1_ID, -1);
			st.takeItems(BEGINNERS_ARCANA_ID, 1);
		} // this takes one Beginner Arcana and set Beginner_Arcana stat -1
		else if (event.equalsIgnoreCase("30636-02.htm"))
		{ // Camoniell's Part
			if (st.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0)
			{
				htmltext = "30636-03.htm";
				st.set("Camoniell", "2");
			}
		}
		else if (event.equalsIgnoreCase("30636-04.htm"))
		{
			st.giveItems(CRYSTAL_OF_PROGRESS2_ID, 1, false);
			st.takeItems(CRYSTAL_OF_FOUL2_ID, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT2_ID, -1);
			st.takeItems(BEGINNERS_ARCANA_ID, 1);
		}
		else if (event.equalsIgnoreCase("30637-02.htm"))
		{ // Belthus' Part
			if (st.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0)
			{
				htmltext = "30637-03.htm";
				st.set("Belthus", "2");
			}
		}
		else if (event.equalsIgnoreCase("30637-04.htm"))
		{
			st.giveItems(CRYSTAL_OF_PROGRESS3_ID, 1, false);
			st.takeItems(CRYSTAL_OF_FOUL3_ID, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT3_ID, -1);
			st.takeItems(BEGINNERS_ARCANA_ID, 1);
		}
		else if (event.equalsIgnoreCase("30638-02.htm"))
		{ // Basilla's Part
			if (st.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0)
			{
				htmltext = "30638-03.htm";
				st.set("Basilla", "2");
			}
		}
		else if (event.equalsIgnoreCase("30638-04.htm"))
		{
			st.giveItems(CRYSTAL_OF_PROGRESS4_ID, 1, false);
			st.takeItems(CRYSTAL_OF_FOUL4_ID, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT4_ID, -1);
			st.takeItems(BEGINNERS_ARCANA_ID, 1);
		}
		else if (event.equalsIgnoreCase("30639-02.htm"))
		{ // Celestiel's Part
			if (st.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0)
			{
				htmltext = "30639-03.htm";
				st.set("Celestiel", "2");
			}
		}
		else if (event.equalsIgnoreCase("30639-04.htm"))
		{
			st.giveItems(CRYSTAL_OF_PROGRESS5_ID, 1, false);
			st.takeItems(CRYSTAL_OF_FOUL5_ID, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT5_ID, -1);
			st.takeItems(BEGINNERS_ARCANA_ID, 1);
		}
		else if (event.equalsIgnoreCase("30640-02.htm"))
		{ // Brynthea's Part
			if (st.getQuestItemsCount(BEGINNERS_ARCANA_ID) > 0)
			{
				htmltext = "30640-03.htm";
				st.set("Brynthea", "2");
			}
		}
		else if (event.equalsIgnoreCase("30640-04.htm"))
		{
			st.giveItems(CRYSTAL_OF_PROGRESS6_ID, 1, false);
			st.takeItems(CRYSTAL_OF_FOUL6_ID, -1);
			st.takeItems(CRYSTAL_OF_DEFEAT6_ID, -1);
			st.takeItems(BEGINNERS_ARCANA_ID, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (st.getQuestItemsCount(MARK_OF_SUMMONER_ID) > 0)
		{
			st.exitCurrentQuest(true);
			return "completed";
		}
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		if (id == CREATED && npcId == 30634)
		{ // start part, Galatea
			for (String var : STATS)
			{
				st.set(var, "0");
			}
			if (st.getPlayer().getClassId() == ClassId.wizard || st.getPlayer().getClassId() == ClassId.elvenWizard || st.getPlayer().getClassId() == ClassId.darkWizard)
			{
				if (st.getPlayer().getLevel() > 38)
				{ // conditions are ok, lets start
					htmltext = "30634-03.htm";
				}
				else
				{
					htmltext = "30634-02.htm"; // too young.. not now
					st.exitCurrentQuest(true);
				}
			}
			else
			{ // wrong class.. never
				htmltext = "30634-01.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (id == STARTED)
		{
			int LaraPart = st.getInt("Lara_Part");
			int Arcanas = st.getInt("Arcanas");
			int step = st.getInt("step"); // stats as int vars if( the player has state <Progress>
			if (npcId == 30634)
			{ // Start&&End Npc Galatea related stuff
				if (step == 1)
				{ // step 1 means just started
					htmltext = "30634-09.htm";
				}
				else if (step == 2)
				{ // step 2 means already talkd with lara
					if (Arcanas == 6)
					{ // finished all battles... the player is able to earn the marks
						htmltext = "30634-12.htm";
						st.playSound(SOUND_FINISH);
						st.takeItems(LARS_LIST1_ID, -1);
						st.takeItems(LARS_LIST2_ID, -1);
						st.takeItems(LARS_LIST3_ID, -1);
						st.takeItems(LARS_LIST4_ID, -1);
						st.takeItems(LARS_LIST5_ID, -1);
						st.takeItems(ALMORS_ARCANA_ID, -1);
						st.takeItems(BASILLIA_ARCANA_ID, -1);
						st.takeItems(CAMONIELL_ARCANA_ID, -1);
						st.takeItems(CELESTIEL_ARCANA_ID, -1);
						st.takeItems(BELTHUS_ARCANA_ID, -1);
						st.takeItems(BRYNTHEA_ARCANA_ID, -1);
						st.giveItems(MARK_OF_SUMMONER_ID, 1);
						if (!st.getPlayer().getVarB("prof2.3"))
						{
							st.addExpAndSp(832247, 57110);
							st.giveItems(ADENA_ID, 150480);
							st.getPlayer().setVar("prof2.3", "1", -1);
						}
						st.playSound(SOUND_FINISH);
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					// he lost something ))||didnt finished
					htmltext = "30634-10.htm";
				}
			}
			else if (npcId == Lara)
			{ // anything realated to Lara below
				if (step == 1)
				{ // first talk to lara
					htmltext = "30063-01.htm";
				}
				else if (LaraPart == 0)
				{ // if( you havent a part taken, give one
					htmltext = "30063-03.htm";
				}
				else
				{
					long ItemCount1 = st.getQuestItemsCount(LISTS[LaraPart][1]);
					long ItemCount2 = st.getQuestItemsCount(LISTS[LaraPart][2]);
					if (ItemCount1 < 30 || ItemCount2 < 30)
					{ // if( you have not enough materials, List 1 - 5
						htmltext = "30063-05.htm";
					}
					else if (ItemCount1 > 29 && ItemCount2 > 29)
					{// if( you have enough materials, receive your Beginner Arcanas, List 1 - 5
						htmltext = "30063-06.htm";
						st.giveItems(BEGINNERS_ARCANA_ID, 2, false);
						st.takeItems(LISTS[LaraPart][0], 1);
						st.takeItems(LISTS[LaraPart][1], -1);
						st.takeItems(LISTS[LaraPart][2], -1);
						st.setCond(3);
						st.set("Lara_Part", "0");
					}
				}
			}
			else
			{
				for (int[] i : SUMMONERS)
				{
					if (i[0] == npcId)
					{
						Integer[] k = DROPLIST_SUMMON.get(npcId - 30635 + 27102);
						int SummonerStat = st.getInt(NAMES.get(i[0]));
						if (step > 1)
						{
							if (st.getQuestItemsCount(k[0]) > 0)
							{ // ready to fight... already take the mission to kill his pet
								htmltext = str(npcId) + "-08.htm";
							}
							else if (st.getQuestItemsCount(k[1]) > 0)
							{ // in battle...
								// this will add the player&&his pet to the list of notif(ied objects in onDeath Part
								st.addNotifyOfDeath(st.getPlayer(), true);
								htmltext = str(npcId) + "-09.htm";
							}
							else if (st.getQuestItemsCount(k[3]) > 0)
							{ // haha... your summon lose
								htmltext = str(npcId) + "-05.htm";
							}
							else if (st.getQuestItemsCount(k[2]) > 0)
							{ // hey.. shit cheater.. dont help your pet
								htmltext = str(npcId) + "-06.htm";
							}
							else if (st.getQuestItemsCount(k[4]) > 0)
							{ // damn.. you won the batlle.. here are the arcanas
								htmltext = str(npcId) + "-07.htm";
								st.takeItems(SUMMONERS[npcId - 30635][2], -1); // take crystal of victory
								st.giveItems(SUMMONERS[npcId - 30635][1], 1, false);// give arcana
								if (st.getQuestItemsCount(3354) + st.getQuestItemsCount(3355) + st.getQuestItemsCount(3356) + st.getQuestItemsCount(3357) + st.getQuestItemsCount(3358) + st.getQuestItemsCount(3359) >= 6)
								{
									st.setCond(4);
								}
								st.set(NAMES.get(i[0]), "7"); // set 7, this mark that the players' summon won the battle
								st.set("Arcanas", str(Arcanas + 1));
							} // set arcana stat +1, if( its 6... quest is finished&&he can earn the mark
							else if (SummonerStat == 7)
							{ // you already won the battle against my summon
								htmltext = str(npcId) + "-10.htm";
							}
							else
							{ // you already won the battle against my summon
								htmltext = str(npcId) + "-01.htm";
							}
						}
					}
				}
			}
		}
		return htmltext;
	}

	static Map<Integer, Integer[]> DROPLIST_SUMMON = new HashMap<Integer, Integer[]>();

	static
	{
		DROPLIST_SUMMON.put(27102, new Integer[]
		{
			CRYSTAL_OF_PROGRESS1_ID,
			CRYSTAL_OF_INPROGRESS1_ID,
			CRYSTAL_OF_FOUL1_ID,
			CRYSTAL_OF_DEFEAT1_ID,
			CRYSTAL_OF_VICTORY1_ID
		}); // Pako the Cat
		DROPLIST_SUMMON.put(27103, new Integer[]
		{
			CRYSTAL_OF_PROGRESS2_ID,
			CRYSTAL_OF_INPROGRESS2_ID,
			CRYSTAL_OF_FOUL2_ID,
			CRYSTAL_OF_DEFEAT2_ID,
			CRYSTAL_OF_VICTORY2_ID
		}); // Mimi the Cat
		DROPLIST_SUMMON.put(27104, new Integer[]
		{
			CRYSTAL_OF_PROGRESS3_ID,
			CRYSTAL_OF_INPROGRESS3_ID,
			CRYSTAL_OF_FOUL3_ID,
			CRYSTAL_OF_DEFEAT3_ID,
			CRYSTAL_OF_VICTORY3_ID
		}); // Shadow Turen
		DROPLIST_SUMMON.put(27105, new Integer[]
		{
			CRYSTAL_OF_PROGRESS4_ID,
			CRYSTAL_OF_INPROGRESS4_ID,
			CRYSTAL_OF_FOUL4_ID,
			CRYSTAL_OF_DEFEAT4_ID,
			CRYSTAL_OF_VICTORY4_ID
		}); // Unicorn Racer
		DROPLIST_SUMMON.put(27106, new Integer[]
		{
			CRYSTAL_OF_PROGRESS5_ID,
			CRYSTAL_OF_INPROGRESS5_ID,
			CRYSTAL_OF_FOUL5_ID,
			CRYSTAL_OF_DEFEAT5_ID,
			CRYSTAL_OF_VICTORY5_ID
		}); // Unicorn Phantasm
		DROPLIST_SUMMON.put(27107, new Integer[]
		{
			CRYSTAL_OF_PROGRESS6_ID,
			CRYSTAL_OF_INPROGRESS6_ID,
			CRYSTAL_OF_FOUL6_ID,
			CRYSTAL_OF_DEFEAT6_ID,
			CRYSTAL_OF_VICTORY6_ID
		}); // Silhoutte Tilfo
	}

	static Map<Integer, String> DROPLIST_SUMMON_VARS = new HashMap<Integer, String>();

	static
	{
		NAMES.put(27102, "Almors");
		NAMES.put(27103, "Camoniell");
		NAMES.put(27104, "Belthus");
		NAMES.put(27105, "Basilla");
		NAMES.put(27106, "Celestiel");
		NAMES.put(27107, "Brynthea");
	}

	@Override
	public String onDeath(Creature killer, Creature victim, QuestState st)
	{
		if (killer == null || victim == null)
		{
			return null; // WTF?
		}
		// if players summon dies, the crystal of defeat is given to the player and set stat to lose
		int npcId = killer.getNpcId();
		//// if (deadPerson == st.getPlayer() or deadPerson = st.getPlayer().getPet()) and npcId in DROPLIST_SUMMON.keys() :
		if (victim == st.getPlayer() || victim == st.getPlayer().getPet())
		{
			if (npcId >= 27102 && npcId <= 27107)
			{
				// var means the variable of the SummonerManager, the rest are all Crystalls wich mark the status
				String[] VARS =
				{
					"Almors",
					"Camoniell",
					"Belthus",
					"Basilla",
					"Celestiel",
					"Brynthea"
				};
				String var = VARS[npcId - 27102];
				Integer[] i = DROPLIST_SUMMON.get(npcId);
				int defeat = i[3];
				if (st.getInt(var) == 3)
				{
					st.set(var, "4");
					st.giveItems(defeat, 1, false);
				}
			}
		}
		return null;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{ // on the first attack, the stat is in battle... anytime gives crystal and set stat
		int npcId = npc.getNpcId();
		// var means the variable of the SummonerManager, the rest are all Crystalls wich mark the status
		if (npcId >= 27102 && npcId <= 27107)
		{
			String[] VARS =
			{
				"Almors",
				"Camoniell",
				"Belthus",
				"Basilla",
				"Celestiel",
				"Brynthea"
			};
			String var = VARS[npcId - 27102];
			Integer[] i = DROPLIST_SUMMON.get(npcId);
			int start = i[0];
			int progress = i[1];
			if (st.getInt(var) == 2)
			{
				st.set(var, "3");
				st.giveItems(progress, 1, false);
				st.takeItems(start, 1);
				st.playSound(SOUND_ITEMGET);
			}

			if (st.getQuestItemsCount(i[2]) != 0)
			{
				return null;
			}

			Summon summon = st.getPlayer().getPet();
			if (summon == null || summon.isPet())
			{
				st.giveItems(i[2], 1, false);
			}
		}
		return null;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId(); // this part is just for laras parts
		if (DROPLIST_LARA.containsKey(npcId))
		{
			Integer[] i = DROPLIST_LARA.get(npcId);
			String var = "Lara_Part";
			int value = i[0];
			int chance = i[1];
			int item = i[2];
			long count = st.getQuestItemsCount(item);
			if (st.getInt(var) == value && count < 30 && Rnd.chance(chance))
			{
				st.giveItems(item, 1, true);
				if (count == 29)
				{
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		else if (DROPLIST_SUMMON.containsKey(npcId))
		{ // if a summon dies
			// var means the variable of the SummonerManager, the rest are all Crystalls which mark the status
			String[] VARS =
			{
				"Almors",
				"Camoniell",
				"Belthus",
				"Basilla",
				"Celestiel",
				"Brynthea"
			};
			String var = VARS[npcId - 27102];
			Integer[] i = DROPLIST_SUMMON.get(npcId);
			int progress = i[1];
			int foul = i[2];
			int victory = i[4];
			if (st.getInt(var) == 3)
			{
				boolean isFoul = st.getQuestItemsCount(foul) == 0;
				int isName = 1; // first entry in the droplist is a name (string). Skip it.
				for (Integer item : DROPLIST_SUMMON.get(npcId))
				{ // take all crystal of this summoner away from the player
					if (isName != 1)
					{
						st.takeItems(item, -1);
					}
					isName = 0;
				}

				st.takeItems(progress, -1);
				if (isFoul)
				{
					st.set(var, "6");
					st.giveItems(victory, 1, false); // if he wons without cheating, set stat won and give victory crystal
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.set(var, "5"); // if the player cheats, give foul crystal and set stat to cheat
				}
			}
		}
		return null;
	}
}