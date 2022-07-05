package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _337_AudienceWithLandDragon extends Quest implements ScriptFile
{
	// npc
	public final int MOKE = 30498;
	public final int HELTON = 30678;
	public final int CHAKIRIS = 30705;
	public final int KAIENA = 30720;
	public final int GABRIELLE = 30753;
	public final int GILMORE = 30754;
	public final int THEODRIC = 30755;
	public final int KENDRA = 30851;
	public final int ORVEN = 30857;
	// mobs
	public final int MARSH_STALKER = 20679;
	public final int MARSH_DRAKE = 20680;
	public final int BLOOD_QUEEN = 18001;
	public final int HARIT_LIZARDMAN_SHAMAN = 20644;
	public final int HARIT_LIZARDMAN_MATRIARCH = 20645;
	public final int HAMRUT = 20649;
	public final int KRANROT = 20650;
	public final int CAVE_MAIDEN = 20134;
	public final int CAVE_KEEPER = 20246;
	public final int ABYSSAL_JEWEL_1 = 27165;
	public final int ABYSSAL_JEWEL_2 = 27166;
	public final int ABYSSAL_JEWEL_3 = 27167;
	public final int JEWEL_GUARDIAN_MARA = 27168;
	public final int JEWEL_GUARDIAN_MUSFEL = 27169;
	public final int JEWEL_GUARDIAN_PYTON = 27170;
	public final int SACRIFICE_OF_THE_SACRIFICED = 27171;
	public final int HARIT_LIZARDMAN_ZEALOT = 27172;
	// items
	public final int FEATHER_OF_GABRIELLE_ID = 3852;
	public final int STALKER_HORN_ID = 3853;
	public final int DRAKE_TALON_ID = 3854;
	public final int REMAINS_OF_SACRIFICED_ID = 3857;
	public final int TOTEM_OF_LAND_DRAGON_ID = 3858;
	public final int HAMRUT_LEG_ID = 3856;
	public final int KRANROT_SKIN_ID = 3855;
	public final int MARA_FANG_ID = 3862;
	public final int MUSFEL_FANG_ID = 3863;
	public final int FIRST_ABYSS_FRAGMENT_ID = 3859;
	public final int SECOND_ABYSS_FRAGMENT_ID = 3860;
	public final int THIRD_ABYSS_FRAGMENT_ID = 3861;
	public final int HERALD_OF_SLAYER_ID = 3890;
	public final int PORTAL_STONE_ID = 3865;
	public final int MARK_OF_WATCHMAN_ID = 3864;

	// # [STEP, MOB, ITEM, NEED_COUNT, CHANCE, DROP]
	public final int[][] DROPLIST =
	{
		{
			2,
			MARSH_STALKER,
			STALKER_HORN_ID,
			1,
			50,
			1
		},
		{
			2,
			MARSH_DRAKE,
			DRAKE_TALON_ID,
			1,
			50,
			1
		},
		{
			4,
			SACRIFICE_OF_THE_SACRIFICED,
			REMAINS_OF_SACRIFICED_ID,
			1,
			50,
			1
		},
		{
			6,
			HARIT_LIZARDMAN_ZEALOT,
			TOTEM_OF_LAND_DRAGON_ID,
			1,
			50,
			1
		},
		{
			8,
			HAMRUT,
			HAMRUT_LEG_ID,
			1,
			50,
			1
		},
		{
			8,
			KRANROT,
			KRANROT_SKIN_ID,
			1,
			50,
			1
		},
		{
			11,
			JEWEL_GUARDIAN_MARA,
			MARA_FANG_ID,
			1,
			50,
			1
		},
		{
			11,
			ABYSSAL_JEWEL_1,
			FIRST_ABYSS_FRAGMENT_ID,
			1,
			100,
			1
		},
		{
			13,
			JEWEL_GUARDIAN_MUSFEL,
			MUSFEL_FANG_ID,
			1,
			50,
			1
		},
		{
			13,
			ABYSSAL_JEWEL_2,
			SECOND_ABYSS_FRAGMENT_ID,
			1,
			100,
			1
		},
		{
			16,
			ABYSSAL_JEWEL_3,
			THIRD_ABYSS_FRAGMENT_ID,
			1,
			100,
			1
		},
	};
	// # [STEP, MOB, SPWN_MOB, SPWN_COUNT,]
	public final int[][] SPAWNLIST =
	{
		{
			4,
			BLOOD_QUEEN,
			SACRIFICE_OF_THE_SACRIFICED,
			6
		},
		{
			6,
			HARIT_LIZARDMAN_SHAMAN,
			HARIT_LIZARDMAN_ZEALOT,
			1
		},
		{
			6,
			HARIT_LIZARDMAN_MATRIARCH,
			HARIT_LIZARDMAN_ZEALOT,
			1
		},
		{
			11,
			ABYSSAL_JEWEL_1,
			JEWEL_GUARDIAN_MARA,
			4
		},
		{
			13,
			ABYSSAL_JEWEL_2,
			JEWEL_GUARDIAN_MUSFEL,
			4
		},
		{
			16,
			CAVE_KEEPER,
			ABYSSAL_JEWEL_3,
			1
		},
		{
			16,
			CAVE_MAIDEN,
			ABYSSAL_JEWEL_3,
			1
		},
		{
			16,
			ABYSSAL_JEWEL_3,
			JEWEL_GUARDIAN_PYTON,
			6
		},
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

	public _337_AudienceWithLandDragon()
	{
		super(false);

		addStartNpc(GABRIELLE);

		addTalkId(MOKE);
		addTalkId(HELTON);
		addTalkId(CHAKIRIS);
		addTalkId(KAIENA);
		addTalkId(GILMORE);
		addTalkId(THEODRIC);
		addTalkId(KENDRA);
		addTalkId(ORVEN);

		addKillId(BLOOD_QUEEN);
		addKillId(MARSH_STALKER);
		addKillId(MARSH_DRAKE);
		addKillId(SACRIFICE_OF_THE_SACRIFICED);
		addKillId(HARIT_LIZARDMAN_SHAMAN);
		addKillId(HARIT_LIZARDMAN_MATRIARCH);
		addKillId(HARIT_LIZARDMAN_ZEALOT);
		addKillId(HAMRUT);
		addKillId(KRANROT);
		addKillId(ABYSSAL_JEWEL_1);
		addKillId(ABYSSAL_JEWEL_2);
		addKillId(CAVE_KEEPER);
		addKillId(CAVE_MAIDEN);
		addKillId(ABYSSAL_JEWEL_3);
		addKillId(JEWEL_GUARDIAN_MARA);
		addKillId(JEWEL_GUARDIAN_MUSFEL);
		addKillId(JEWEL_GUARDIAN_PYTON);

		addAttackId(ABYSSAL_JEWEL_1);
		addAttackId(ABYSSAL_JEWEL_2);
		addAttackId(ABYSSAL_JEWEL_3);

		addQuestItem(new int[]
		{
			FEATHER_OF_GABRIELLE_ID,
			HERALD_OF_SLAYER_ID,
			STALKER_HORN_ID,
			DRAKE_TALON_ID,
			REMAINS_OF_SACRIFICED_ID,
			TOTEM_OF_LAND_DRAGON_ID,
			HAMRUT_LEG_ID,
			KRANROT_SKIN_ID,
			MARA_FANG_ID,
			FIRST_ABYSS_FRAGMENT_ID,
			MUSFEL_FANG_ID,
			SECOND_ABYSS_FRAGMENT_ID,
			THIRD_ABYSS_FRAGMENT_ID,
			MARK_OF_WATCHMAN_ID
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("1"))
		{
			st.set("step", "1");
			st.setCond(1);
			st.set("guard", "0");
			st.setState(STARTED);
			st.giveItems(FEATHER_OF_GABRIELLE_ID, 1);
			htmltext = "30753-02.htm";
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("2"))
		{
			st.set("step", "2");
			htmltext = "30720-02.htm";
		}
		else if (event.equalsIgnoreCase("4"))
		{
			st.set("step", "4");
			htmltext = "30857-02.htm";
		}
		else if (event.equalsIgnoreCase("6"))
		{
			st.set("step", "6");
			htmltext = "30851-02.htm";
		}
		else if (event.equalsIgnoreCase("8"))
		{
			st.set("step", "8");
			htmltext = "30705-02.htm";
		}
		else if (event.equalsIgnoreCase("10"))
		{
			st.takeItems(MARK_OF_WATCHMAN_ID, -1);
			st.set("step", "10");
			st.setCond(2);
			htmltext = "30753-05.htm";
		}
		else if (event.equalsIgnoreCase("11"))
		{
			st.set("step", "11");
			htmltext = "30498-02.htm";
		}
		else if (event.equalsIgnoreCase("13"))
		{
			st.set("step", "13");
			htmltext = "30678-02.htm";
		}
		else if (event.equalsIgnoreCase("15"))
		{
			st.set("step", "15");
			st.setCond(3);
			htmltext = "30753-06.htm";
			st.takeItems(MARK_OF_WATCHMAN_ID, -1);
			st.takeItems(FEATHER_OF_GABRIELLE_ID, -1);
			st.giveItems(HERALD_OF_SLAYER_ID, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("16"))
		{
			st.set("step", "16");
			st.setCond(4);
			htmltext = "30754-02.htm";
			st.takeItems(HERALD_OF_SLAYER_ID, -1);
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int step = st.getInt("step");
		int cond = st.getCond();
		if (npcId == GABRIELLE)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() < 50)
				{
					htmltext = "30753-00.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "30753-01.htm";
				}
			}
			else if (step < 9)
			{
				htmltext = "30753-02.htm";
			}
			else if (step == 9)
			{
				htmltext = "30753-03.htm";
			}
			else if (step > 9 && step < 14)
			{
				htmltext = "30753-05.htm";
			}
			else if (step == 14)
			{
				htmltext = "30753-04.htm";
			}
			else if (step > 14)
			{
				htmltext = "30753-06.htm";
			}
		}
		else if (npcId == KAIENA && cond == 1 && step < 4)
		{
			if (st.getQuestItemsCount(STALKER_HORN_ID) < 1 && st.getQuestItemsCount(DRAKE_TALON_ID) < 1 && step == 1)
			{
				htmltext = "30720-01.htm";
			}
			else if (st.getQuestItemsCount(STALKER_HORN_ID) > 0 && st.getQuestItemsCount(DRAKE_TALON_ID) > 0)
			{
				htmltext = "30720-03.htm";
				st.takeItems(STALKER_HORN_ID, -1);
				st.takeItems(DRAKE_TALON_ID, -1);
				st.giveItems(MARK_OF_WATCHMAN_ID, 1);
				st.set("step", "3");
				st.playSound(SOUND_MIDDLE);
			}
			else if (step == 2)
			{
				htmltext = "30720-02.htm";
			}
			else if (step == 3)
			{
				htmltext = "30720-03.htm";
			}
		}
		else if (npcId == ORVEN && cond == 1 && step > 2 && step < 6)
		{
			if (st.getQuestItemsCount(REMAINS_OF_SACRIFICED_ID) < 1 && step == 3)
			{
				htmltext = "30857-01.htm";
			}
			else if (st.getQuestItemsCount(REMAINS_OF_SACRIFICED_ID) > 0)
			{
				htmltext = "30857-03.htm";
				st.takeItems(REMAINS_OF_SACRIFICED_ID, -1);
				st.giveItems(MARK_OF_WATCHMAN_ID, 1);
				st.set("step", "5");
				st.playSound(SOUND_MIDDLE);
			}
			else if (step == 4)
			{
				htmltext = "30857-02.htm";
			}
			else if (step == 5)
			{
				htmltext = "30857-03.htm";
			}
		}
		else if (npcId == KENDRA && cond == 1 && step > 4 && step < 8)
		{
			if (st.getQuestItemsCount(TOTEM_OF_LAND_DRAGON_ID) < 1 && step == 5)
			{
				htmltext = "30851-01.htm";
			}
			else if (st.getQuestItemsCount(TOTEM_OF_LAND_DRAGON_ID) > 0)
			{
				htmltext = "30851-03.htm";
				st.takeItems(TOTEM_OF_LAND_DRAGON_ID, -1);
				st.giveItems(MARK_OF_WATCHMAN_ID, 1);
				st.set("step", "7");
				st.playSound(SOUND_MIDDLE);
			}
			else if (step == 6)
			{
				htmltext = "30851-02.htm";
			}
			else if (step == 7)
			{
				htmltext = "30851-03.htm";
			}
		}
		else if (npcId == CHAKIRIS && cond == 1 && step > 6 && step < 10)
		{
			if (st.getQuestItemsCount(HAMRUT_LEG_ID) < 1 && st.getQuestItemsCount(KRANROT_SKIN_ID) < 1 && step == 7)
			{
				htmltext = "30705-01.htm";
			}
			else if (st.getQuestItemsCount(HAMRUT_LEG_ID) > 0 && st.getQuestItemsCount(KRANROT_SKIN_ID) > 0)
			{
				htmltext = "30705-03.htm";
				st.takeItems(HAMRUT_LEG_ID, -1);
				st.takeItems(KRANROT_SKIN_ID, -1);
				st.giveItems(MARK_OF_WATCHMAN_ID, 1);
				st.set("step", "9");
				st.playSound(SOUND_MIDDLE);
			}
			else if (step == 8)
			{
				htmltext = "30705-02.htm";
			}
			else if (step == 9)
			{
				htmltext = "30705-03.htm";
			}
		}
		else if (npcId == MOKE && cond == 2 && step < 13)
		{
			if (st.getQuestItemsCount(MARA_FANG_ID) < 1 && st.getQuestItemsCount(FIRST_ABYSS_FRAGMENT_ID) < 1 && step == 10)
			{
				htmltext = "30498-01.htm";
			}
			else if (st.getQuestItemsCount(MARA_FANG_ID) > 0 && st.getQuestItemsCount(FIRST_ABYSS_FRAGMENT_ID) > 0)
			{
				htmltext = "30498-03.htm";
				st.takeItems(MARA_FANG_ID, -1);
				st.takeItems(FIRST_ABYSS_FRAGMENT_ID, -1);
				st.giveItems(MARK_OF_WATCHMAN_ID, 1);
				st.set("step", "12");
				st.playSound(SOUND_MIDDLE);
			}
			else if (step == 11)
			{
				htmltext = "30498-02.htm";
			}
			else if (step == 12)
			{
				htmltext = "30498-03.htm";
			}
		}
		else if (npcId == HELTON && cond == 2 && step > 11 && step < 15)
		{
			if (st.getQuestItemsCount(MUSFEL_FANG_ID) < 1 && st.getQuestItemsCount(SECOND_ABYSS_FRAGMENT_ID) < 1 && step == 12)
			{
				htmltext = "30678-01.htm";
			}
			else if (st.getQuestItemsCount(MUSFEL_FANG_ID) > 0 && st.getQuestItemsCount(SECOND_ABYSS_FRAGMENT_ID) > 0)
			{
				htmltext = "30678-03.htm";
				st.takeItems(MUSFEL_FANG_ID, -1);
				st.takeItems(SECOND_ABYSS_FRAGMENT_ID, -1);
				st.giveItems(MARK_OF_WATCHMAN_ID, 1);
				st.set("step", "14");
				st.playSound(SOUND_MIDDLE);
			}
			else if (step == 13)
			{
				htmltext = "30678-02.htm";
			}
			else if (step == 14)
			{
				htmltext = "30678-03.htm";
			}
		}
		else if (npcId == GILMORE && step < 17)
		{
			if (st.getQuestItemsCount(HERALD_OF_SLAYER_ID) > 0 && cond == 3)
			{
				htmltext = "30754-01.htm";
			}
			else if (cond == 4)
			{
				htmltext = "30754-02.htm";
			}
		}
		else if (npcId == THEODRIC && cond == 4 && step == 16)
		{
			if (st.getQuestItemsCount(THIRD_ABYSS_FRAGMENT_ID) < 1)
			{
				htmltext = "30755-02.htm";
			}
			else
			{
				htmltext = "30755-01.htm";
				st.takeItems(THIRD_ABYSS_FRAGMENT_ID, -1);
				st.unset("step");
				st.unset("cond");
				st.unset("guard");
				st.exitCurrentQuest(true);
				st.giveItems(PORTAL_STONE_ID, 1);
				st.playSound(SOUND_FINISH);
			}
		}
		return htmltext;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int step = st.getInt("step");
		for (int[] element : SPAWNLIST)
		{
			// # [STEP, MOB, SPWN_MOB, SPWN_COUNT]
			if (npcId == element[1] && step == element[0] && npc.getCurrentHpPercents() < 50 && st.getInt("guard") == 0)
			{
				for (int j = 0; j < element[3]; j++)
				{
					st.addSpawn(element[2]);
				}
				st.playSound(SOUND_BEFORE_BATTLE);
				st.set("guard", "1");
			}
		}
		return null;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int step = st.getInt("step");
		for (int[] element : DROPLIST)
		{
			// # [STEP, ID, ITEM, NEED_COUNT, CHANCE, DROP]
			if (npcId == element[1] && step == element[0] && st.getQuestItemsCount(element[2]) < element[3] && Rnd.chance(element[4]))
			{
				st.giveItems(element[2], element[5]);
				st.playSound(SOUND_ITEMGET);
			}
		}
		for (int[] element : SPAWNLIST)
		{
			// # [STEP, MOB, SPWN_MOB, SPWN_COUNT]
			if (step == element[0] && npcId == element[1] && Rnd.chance(50) && st.getInt("guard") == 0)
			{
				for (int j = 0; j < element[3]; j++)
				{
					st.addSpawn(element[2]);
				}
				st.playSound(SOUND_BEFORE_BATTLE);
			}
			if (step == element[0] && npcId == element[1] && st.getInt("guard") == 1)
			{
				st.set("guard", "0");
			}
		}
		return null;
	}
}