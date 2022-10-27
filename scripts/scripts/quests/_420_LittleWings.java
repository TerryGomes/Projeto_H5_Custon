package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _420_LittleWings extends Quest implements ScriptFile
{
	// NPCs
	private static final int Cooper = 30829;
	private static final int Cronos = 30610;
	private static final int Byron = 30711;
	private static final int Maria = 30608;
	private static final int Mimyu = 30747;
	private static final int Exarion = 30748;
	private static final int Zwov = 30749;
	private static final int Kalibran = 30750;
	private static final int Suzet = 30751;
	private static final int Shamhai = 30752;
	// Mobs
	private static final int Enchanted_Valey_First = 20589;
	private static final int Enchanted_Valey_Last = 20599;
	private static final int Toad_Lord = 20231;
	private static final int Marsh_Spider = 20233;
	private static final int Leto_Lizardman_Warrior = 20580;
	private static final int Road_Scavenger = 20551;
	private static final int Breka_Orc_Overlord = 20270;
	private static final int Dead_Seeker = 20202;
	// Items
	private static int Coal = 1870;
	private static int Charcoal = 1871;
	private static int Silver_Nugget = 1873;
	private static int Stone_of_Purity = 1875;
	private static int GemstoneD = 2130;
	private static int GemstoneC = 2131;
	private static int Dragonflute_of_Wind = 3500;
	private static int Dragonflute_of_Twilight = 3502;
	private static int Hatchlings_Soft_Leather = 3912;
	private static int Hatchlings_Mithril_Coat = 3918;
	private static int Food_For_Hatchling = 4038;
	// Quest Items
	private static int Fairy_Dust = 3499;
	private static int Fairy_Stone = 3816;
	private static int Deluxe_Fairy_Stone = 3817;
	private static int Fairy_Stone_List = 3818;
	private static int Deluxe_Fairy_Stone_List = 3819;
	private static int Toad_Lord_Back_Skin = 3820;
	private static int Juice_of_Monkshood = 3821;

	private static int Scale_of_Drake_Exarion = 3822;
	private static int Scale_of_Drake_Zwov = 3824;
	private static int Scale_of_Drake_Kalibran = 3826;
	private static int Scale_of_Wyvern_Suzet = 3828;
	private static int Scale_of_Wyvern_Shamhai = 3830;

	private static int Egg_of_Drake_Exarion = 3823;
	private static int Egg_of_Drake_Zwov = 3825;
	private static int Egg_of_Drake_Kalibran = 3827;
	private static int Egg_of_Wyvern_Suzet = 3829;
	private static int Egg_of_Wyvern_Shamhai = 3831;

	// Chances
	private static final int Toad_Lord_Back_Skin_Chance = 30;
	private static final int Egg_Chance = 50;
	private static final int Pet_Armor_Chance = 35;

	private static int[][] Fairy_Stone_Items =
	{
		{
			Coal,
			10
		},
		{
			Charcoal,
			10
		},
		{
			GemstoneD,
			1
		},
		{
			Silver_Nugget,
			3
		},
		{
			Toad_Lord_Back_Skin,
			10
		}
	};

	private static int[][] Delux_Fairy_Stone_Items =
	{
		{
			Coal,
			10
		},
		{
			Charcoal,
			10
		},
		{
			GemstoneC,
			1
		},
		{
			Stone_of_Purity,
			1
		},
		{
			Silver_Nugget,
			5
		},
		{
			Toad_Lord_Back_Skin,
			20
		}
	};

	private static final int[][] wyrms =
	{
		{
			Leto_Lizardman_Warrior,
			Exarion,
			Scale_of_Drake_Exarion,
			Egg_of_Drake_Exarion
		},
		{
			Marsh_Spider,
			Zwov,
			Scale_of_Drake_Zwov,
			Egg_of_Drake_Zwov
		},
		{
			Road_Scavenger,
			Kalibran,
			Scale_of_Drake_Kalibran,
			Egg_of_Drake_Kalibran
		},
		{
			Breka_Orc_Overlord,
			Suzet,
			Scale_of_Wyvern_Suzet,
			Egg_of_Wyvern_Suzet
		},
		{
			Dead_Seeker,
			Shamhai,
			Scale_of_Wyvern_Shamhai,
			Egg_of_Wyvern_Shamhai
		}
	};

	public _420_LittleWings()
	{
		super(false);

		addStartNpc(Cooper);

		addTalkId(Cronos);
		addTalkId(Mimyu);
		addTalkId(Byron);
		addTalkId(Maria);

		addKillId(Toad_Lord);
		for (int Enchanted_Valey_id = Enchanted_Valey_First; Enchanted_Valey_id <= Enchanted_Valey_Last; Enchanted_Valey_id++)
		{
			addKillId(Enchanted_Valey_id);
		}

		for (int[] wyrm : wyrms)
		{
			addTalkId(wyrm[1]);
			addKillId(wyrm[0]);
		}

		addQuestItem(Fairy_Dust);
		addQuestItem(Fairy_Stone);
		addQuestItem(Deluxe_Fairy_Stone);
		addQuestItem(Fairy_Stone_List);
		addQuestItem(Deluxe_Fairy_Stone_List);
		addQuestItem(Toad_Lord_Back_Skin);
		addQuestItem(Juice_of_Monkshood);
		addQuestItem(Scale_of_Drake_Exarion);
		addQuestItem(Scale_of_Drake_Zwov);
		addQuestItem(Scale_of_Drake_Kalibran);
		addQuestItem(Scale_of_Wyvern_Suzet);
		addQuestItem(Scale_of_Wyvern_Shamhai);
		addQuestItem(Egg_of_Drake_Exarion);
		addQuestItem(Egg_of_Drake_Zwov);
		addQuestItem(Egg_of_Drake_Kalibran);
		addQuestItem(Egg_of_Wyvern_Suzet);
		addQuestItem(Egg_of_Wyvern_Shamhai);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		int cond = st.getCond();
		if (event.equalsIgnoreCase("30829-02.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if ((event.equalsIgnoreCase("30610-05.htm") || event.equalsIgnoreCase("30610-12.htm")) && _state == STARTED && cond == 1)
		{
			st.setCond(2);
			st.takeItems(Fairy_Stone, -1);
			st.takeItems(Deluxe_Fairy_Stone, -1);
			st.takeItems(Fairy_Stone_List, -1);
			st.takeItems(Deluxe_Fairy_Stone_List, -1);
			st.giveItems(Fairy_Stone_List, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if ((event.equalsIgnoreCase("30610-06.htm") || event.equalsIgnoreCase("30610-13.htm")) && _state == STARTED && cond == 1)
		{
			st.setCond(2);
			st.takeItems(Fairy_Stone, -1);
			st.takeItems(Deluxe_Fairy_Stone, -1);
			st.takeItems(Fairy_Stone_List, -1);
			st.takeItems(Deluxe_Fairy_Stone_List, -1);
			st.giveItems(Deluxe_Fairy_Stone_List, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30608-03.htm") && _state == STARTED && cond == 2 && st.getQuestItemsCount(Fairy_Stone_List) > 0)
		{
			if (CheckFairyStoneItems(st, Fairy_Stone_Items))
			{
				st.setCond(3);
				TakeFairyStoneItems(st, Fairy_Stone_Items);
				st.giveItems(Fairy_Stone, 1);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				return "30608-01.htm";
			}
		}
		else if (event.equalsIgnoreCase("30608-03a.htm") && _state == STARTED && cond == 2 && st.getQuestItemsCount(Deluxe_Fairy_Stone_List) > 0)
		{
			if (CheckFairyStoneItems(st, Delux_Fairy_Stone_Items))
			{
				st.setCond(3);
				TakeFairyStoneItems(st, Delux_Fairy_Stone_Items);
				st.giveItems(Deluxe_Fairy_Stone, 1);
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				return "30608-01a.htm";
			}
		}
		else if (event.equalsIgnoreCase("30711-03.htm") && _state == STARTED && cond == 3 && st.getQuestItemsCount(Fairy_Stone) + st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
			if (st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
			{
				return st.getInt("broken") == 1 ? "30711-04a.htm" : "30711-03a.htm";
			}
			if (st.getInt("broken") == 1)
			{
				return "30711-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("30747-02.htm") && _state == STARTED && cond == 4 && st.getQuestItemsCount(Fairy_Stone) > 0)
		{
			st.takeItems(Fairy_Stone, -1);
			st.set("takedStone", "1");
		}
		else if (event.equalsIgnoreCase("30747-02a.htm") && _state == STARTED && cond == 4 && st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
		{
			st.takeItems(Deluxe_Fairy_Stone, -1);
			st.set("takedStone", "2");
			st.giveItems(Fairy_Dust, 1);
			st.playSound(SOUND_ITEMGET);
		}
		else if (event.equalsIgnoreCase("30747-04.htm") && _state == STARTED && cond == 4 && st.getInt("takedStone") > 0)
		{
			st.setCond(5);
			st.unset("takedStone");
			st.giveItems(Juice_of_Monkshood, 1);
			st.playSound(SOUND_ITEMGET);
		}

		else if (event.equalsIgnoreCase("30748-02.htm") && cond == 5 && _state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0)
		{
			st.setCond(6);
			st.takeItems(Juice_of_Monkshood, -1);
			st.giveItems(3822, 1);
			st.playSound(SOUND_ITEMGET);
		}

		else if (event.equalsIgnoreCase("30749-02.htm") && cond == 5 && _state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0)
		{
			st.setCond(6);
			st.takeItems(Juice_of_Monkshood, -1);
			st.giveItems(3824, 1);
			st.playSound(SOUND_ITEMGET);
		}

		else if (event.equalsIgnoreCase("30750-02.htm") && cond == 5 && _state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0)
		{
			st.setCond(6);
			st.takeItems(Juice_of_Monkshood, -1);
			st.giveItems(3826, 1);
			st.playSound(SOUND_ITEMGET);
		}

		else if (event.equalsIgnoreCase("30751-02.htm") && cond == 5 && _state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0)
		{
			st.setCond(6);
			st.takeItems(Juice_of_Monkshood, -1);
			st.giveItems(3828, 1);
			st.playSound(SOUND_ITEMGET);
		}

		else if (event.equalsIgnoreCase("30752-02.htm") && cond == 5 && _state == STARTED && st.getQuestItemsCount(Juice_of_Monkshood) > 0)
		{
			st.setCond(6);
			st.takeItems(Juice_of_Monkshood, -1);
			st.giveItems(3830, 1);
			st.playSound(SOUND_ITEMGET);
		}

		else if (event.equalsIgnoreCase("30747-09.htm") && _state == STARTED && cond == 7)
		{
			int egg_id = 0;
			for (int[] wyrm : wyrms)
			{
				if (st.getQuestItemsCount(wyrm[2]) == 0 && st.getQuestItemsCount(wyrm[3]) >= 1)
				{
					egg_id = wyrm[3];
					break;
				}
			}
			if (egg_id == 0)
			{
				return "noquest";
			}
			st.takeItems(egg_id, -1);
			st.giveItems(Rnd.get(Dragonflute_of_Wind, Dragonflute_of_Twilight), 1);
			if (st.getQuestItemsCount(Fairy_Dust) > 0)
			{
				st.playSound(SOUND_MIDDLE);
				return "30747-09a.htm";
			}
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("30747-10.htm") && _state == STARTED && cond == 7)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("30747-11.htm") && _state == STARTED && cond == 7)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			if (st.getQuestItemsCount(Fairy_Dust) == 0)
			{
				return "30747-10.htm";
			}
			st.takeItems(Fairy_Dust, -1);
			if (Rnd.chance(Pet_Armor_Chance))
			{
				int armor_id = Hatchlings_Soft_Leather + Rnd.get((int) st.getRateQuestsReward());
				if (armor_id > Hatchlings_Mithril_Coat)
				{
					armor_id = Hatchlings_Mithril_Coat;
				}
				st.giveItems(armor_id, 1);
			}
			else
			{
				st.giveItems(Food_For_Hatchling, 20, true);
			}
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
			if (npcId != Cooper)
			{
				return "noquest";
			}
			if (st.getPlayer().getLevel() < 35)
			{
				st.exitCurrentQuest(true);
				return "30829-00.htm";
			}
			st.setCond(0);
			return "30829-01.htm";
		}

		if (_state != STARTED)
		{
			return "noquest";
		}
		int cond = st.getCond();
		int broken = st.getInt("broken");

		if (npcId == Cooper)
		{
			if (cond == 1)
			{
				return "30829-02.htm";
			}
			return "30829-03.htm";
		}

		if (npcId == Cronos)
		{
			switch (cond)
			{
			case 1:
				return broken == 1 ? "30610-10.htm" : "30610-01.htm";
			case 2:
				return "30610-07.htm";
			case 3:
				return broken == 1 ? "30610-14.htm" : "30610-08.htm";
			case 4:
				return "30610-09.htm";
			default:
				break;
			}
			if (cond > 4)
			{
				return "30610-11.htm";
			}
		}

		if (npcId == Maria)
		{
			if (cond == 2)
			{
				if (st.getQuestItemsCount(Deluxe_Fairy_Stone_List) > 0)
				{
					return CheckFairyStoneItems(st, Delux_Fairy_Stone_Items) ? "30608-02a.htm" : "30608-01a.htm";
				}
				if (st.getQuestItemsCount(Fairy_Stone_List) > 0)
				{
					return CheckFairyStoneItems(st, Fairy_Stone_Items) ? "30608-02.htm" : "30608-01.htm";
				}
			}
			else if (cond > 2)
			{
				return "30608-04.htm";
			}
		}

		if (npcId == Byron)
		{
			if (cond == 1 && broken == 1)
			{
				return "30711-06.htm";
			}
			if (cond == 2 && broken == 1)
			{
				return "30711-07.htm";
			}
			if (cond == 3 && st.getQuestItemsCount(Fairy_Stone) + st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
			{
				return "30711-01.htm";
			}
			if (cond >= 4 && st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
			{
				return "30711-05a.htm";
			}
			if (cond >= 4 && st.getQuestItemsCount(Fairy_Stone) > 0)
			{
				return "30711-05.htm";
			}
		}

		if (npcId == Mimyu)
		{
			if (cond == 4 && st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
			{
				return "30747-01a.htm";
			}
			if (cond == 4 && st.getQuestItemsCount(Fairy_Stone) > 0)
			{
				return "30747-01.htm";
			}
			switch (cond)
			{
			case 5:
				return "30747-05.htm";
			case 6:
				for (int[] wyrm : wyrms)
				{
					if (st.getQuestItemsCount(wyrm[2]) == 0 && st.getQuestItemsCount(wyrm[3]) >= 20)
					{
						return "30747-07.htm";
					}
				}
				return "30747-06.htm";
			case 7:
				for (int[] wyrm : wyrms)
				{
					if (st.getQuestItemsCount(wyrm[2]) == 0 && st.getQuestItemsCount(wyrm[3]) >= 1)
					{
						return "30747-08.htm";
					}
				}
				break;
			default:
				break;
			}
		}

		if (npcId >= Exarion && npcId <= Shamhai)
		{
			if (cond == 5 && st.getQuestItemsCount(Juice_of_Monkshood) > 0)
			{
				return String.valueOf(npcId) + "-01.htm";
			}
			if (cond == 6 && st.getQuestItemsCount(getWyrmScale(npcId)) > 0)
			{
				int egg_id = getWyrmEgg(npcId);
				if (st.getQuestItemsCount(egg_id) < 20)
				{
					return String.valueOf(npcId) + "-03.htm";
				}
				st.takeItems(getWyrmScale(npcId), -1);
				st.takeItems(egg_id, -1);
				st.giveItems(egg_id, 1);
				st.setCond(7);
				return String.valueOf(npcId) + "-04.htm";
			}
			if (cond == 7 && st.getQuestItemsCount(getWyrmEgg(npcId)) == 1)
			{
				return String.valueOf(npcId) + "-05.htm";
			}
		}

		return "noquest";
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

		if (cond == 2 && npcId == Toad_Lord)
		{
			int needed_skins = getNeededSkins(st);
			if (st.getQuestItemsCount(Toad_Lord_Back_Skin) < needed_skins && Rnd.chance(Toad_Lord_Back_Skin_Chance))
			{
				st.giveItems(Toad_Lord_Back_Skin, 1);
				st.playSound(st.getQuestItemsCount(Toad_Lord_Back_Skin) < needed_skins ? SOUND_ITEMGET : SOUND_MIDDLE);
			}
			return null;
		}

		if (npcId >= Enchanted_Valey_First && npcId <= Enchanted_Valey_Last && st.getQuestItemsCount(Deluxe_Fairy_Stone) > 0)
		{
			st.takeItems(Deluxe_Fairy_Stone, 1);
			st.set("broken", "1");
			st.setCond(1);
			return "You lost fairy stone deluxe!";
		}

		if (cond == 6)
		{
			int wyrm_id = isWyrmStoler(npcId);
			if (wyrm_id > 0 && st.getQuestItemsCount(getWyrmScale(wyrm_id)) > 0 && st.getQuestItemsCount(getWyrmEgg(wyrm_id)) < 20 && Rnd.chance(Egg_Chance))
			{
				st.giveItems(getWyrmEgg(wyrm_id), 1);
				st.playSound(st.getQuestItemsCount(getWyrmEgg(wyrm_id)) < 20 ? SOUND_ITEMGET : SOUND_MIDDLE);
			}
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

	private static int getWyrmScale(int npc_id)
	{
		for (int[] wyrm : wyrms)
		{
			if (npc_id == wyrm[1])
			{
				return wyrm[2];
			}
		}
		return 0;
	}

	private static int getWyrmEgg(int npc_id)
	{
		for (int[] wyrm : wyrms)
		{
			if (npc_id == wyrm[1])
			{
				return wyrm[3];
			}
		}
		return 0;
	}

	private static int isWyrmStoler(int npc_id)
	{
		for (int[] wyrm : wyrms)
		{
			if (npc_id == wyrm[0])
			{
				return wyrm[1];
			}
		}
		return 0;
	}

	public static int getNeededSkins(QuestState st)
	{
		if (st.getQuestItemsCount(Deluxe_Fairy_Stone_List) > 0)
		{
			return 20;
		}
		if (st.getQuestItemsCount(Fairy_Stone_List) > 0)
		{
			return 10;
		}
		return -1;
	}

	public static boolean CheckFairyStoneItems(QuestState st, int[][] item_list)
	{
		for (int[] _item : item_list)
		{
			if (st.getQuestItemsCount(_item[0]) < _item[1])
			{
				return false;
			}
		}
		return true;
	}

	public static void TakeFairyStoneItems(QuestState st, int[][] item_list)
	{
		for (int[] _item : item_list)
		{
			st.takeItems(_item[0], _item[1]);
		}
	}
}