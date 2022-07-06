package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _218_TestimonyOfLife extends Quest implements ScriptFile
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

	private static final int MARK_OF_LIFE = 3140;
	private static final int CARDIENS_LETTER = 3141;
	private static final int CAMOMILE_CHARM = 3142;
	private static final int HIERARCHS_LETTER = 3143;
	private static final int MOONFLOWER_CHARM = 3144;
	private static final int GRAIL_DIAGRAM = 3145;
	private static final int THALIAS_LETTER1 = 3146;
	private static final int THALIAS_LETTER2 = 3147;
	private static final int THALIAS_INSTRUCTIONS = 3148;
	private static final int PUSHKINS_LIST = 3149;
	private static final int PURE_MITHRIL_CUP = 3150;
	private static final int ARKENIAS_CONTRACT = 3151;
	private static final int ARKENIAS_INSTRUCTIONS = 3152;
	private static final int ADONIUS_LIST = 3153;
	private static final int ANDARIEL_SCRIPTURE_COPY = 3154;
	private static final int STARDUST = 3155;
	private static final int ISAELS_INSTRUCTIONS = 3156;
	private static final int ISAELS_LETTER = 3157;
	private static final int GRAIL_OF_PURITY = 3158;
	private static final int TEARS_OF_UNICORN = 3159;
	private static final int WATER_OF_LIFE = 3160;
	private static final int PURE_MITHRIL_ORE = 3161;
	private static final int ANT_SOLDIER_ACID = 3162;
	private static final int WYRMS_TALON1 = 3163;
	private static final int SPIDER_ICHOR = 3164;
	private static final int HARPYS_DOWN = 3165;
	private static final int TALINS_SPEAR_BLADE = 3166;
	private static final int TALINS_SPEAR_SHAFT = 3167;
	private static final int TALINS_RUBY = 3168;
	private static final int TALINS_AQUAMARINE = 3169;
	private static final int TALINS_AMETHYST = 3170;
	private static final int TALINS_PERIDOT = 3171;
	private static final int TALINS_SPEAR = 3026;
	private static final int RewardExp = 943416;
	private static final int RewardSP = 62959;
	private static final int RewardAdena = 171144;

	public _218_TestimonyOfLife()
	{
		super(false);

		addStartNpc(30460);

		addTalkId(30154);
		addTalkId(30300);
		addTalkId(30371);
		addTalkId(30375);
		addTalkId(30419);
		addTalkId(30460);
		addTalkId(30655);

		addKillId(20145);
		addKillId(20176);
		addKillId(20233);
		addKillId(27077);
		addKillId(20550);
		addKillId(20581);
		addKillId(20582);
		addKillId(20082);
		addKillId(20084);
		addKillId(20086);
		addKillId(20087);
		addKillId(20088);

		addQuestItem(new int[]
		{
			CAMOMILE_CHARM,
			CARDIENS_LETTER,
			WATER_OF_LIFE,
			MOONFLOWER_CHARM,
			HIERARCHS_LETTER,
			STARDUST,
			PURE_MITHRIL_CUP,
			THALIAS_INSTRUCTIONS,
			ISAELS_LETTER,
			TEARS_OF_UNICORN,
			GRAIL_DIAGRAM,
			PUSHKINS_LIST,
			THALIAS_LETTER1,
			ARKENIAS_CONTRACT,
			ANDARIEL_SCRIPTURE_COPY,
			ARKENIAS_INSTRUCTIONS,
			ADONIUS_LIST,
			THALIAS_LETTER2,
			TALINS_SPEAR_BLADE,
			TALINS_SPEAR_SHAFT,
			TALINS_RUBY,
			TALINS_AQUAMARINE,
			TALINS_AMETHYST,
			TALINS_PERIDOT,
			ISAELS_INSTRUCTIONS,
			GRAIL_OF_PURITY
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("1"))
		{
			htmltext = "30460-04.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(CARDIENS_LETTER, 1);
			if (!st.getPlayer().getVarB("dd2"))
			{
				st.giveItems(7562, 102);
				st.getPlayer().setVar("dd2", "1", -1);
			}
		}
		else if (event.equalsIgnoreCase("30154_1"))
		{
			htmltext = "30154-02.htm";
		}
		else if (event.equalsIgnoreCase("30154_2"))
		{
			htmltext = "30154-03.htm";
		}
		else if (event.equalsIgnoreCase("30154_3"))
		{
			htmltext = "30154-04.htm";
		}
		else if (event.equalsIgnoreCase("30154_4"))
		{
			htmltext = "30154-05.htm";
		}
		else if (event.equalsIgnoreCase("30154_5"))
		{
			htmltext = "30154-06.htm";
		}
		else if (event.equalsIgnoreCase("30154_6"))
		{
			htmltext = "30154-07.htm";
			st.takeItems(CARDIENS_LETTER, 1);
			st.giveItems(MOONFLOWER_CHARM, 1);
			st.giveItems(HIERARCHS_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("30371_1"))
		{
			htmltext = "30371-02.htm";
		}
		else if (event.equalsIgnoreCase("30371_2"))
		{
			htmltext = "30371-03.htm";
			st.takeItems(HIERARCHS_LETTER, 1);
			st.giveItems(GRAIL_DIAGRAM, 1);
		}
		else if (event.equalsIgnoreCase("30371_3"))
		{
			if (st.getPlayer().getLevel() < 38)
			{
				htmltext = "30371-10.htm";
				st.takeItems(STARDUST, 1);
				st.giveItems(THALIAS_INSTRUCTIONS, 1);
			}
			else
			{
				htmltext = "30371-11.htm";
				st.takeItems(STARDUST, 1);
				st.giveItems(THALIAS_LETTER2, 1);
			}
		}
		else if (event.equalsIgnoreCase("30300_1"))
		{
			htmltext = "30300-02.htm";
		}
		else if (event.equalsIgnoreCase("30300_2"))
		{
			htmltext = "30300-03.htm";
		}
		else if (event.equalsIgnoreCase("30300_3"))
		{
			htmltext = "30300-04.htm";
		}
		else if (event.equalsIgnoreCase("30300_4"))
		{
			htmltext = "30300-05.htm";
		}
		else if (event.equalsIgnoreCase("30300_5"))
		{
			htmltext = "30300-06.htm";
			st.takeItems(GRAIL_DIAGRAM, 1);
			st.giveItems(PUSHKINS_LIST, 1);
		}
		else if (event.equalsIgnoreCase("30300_6"))
		{
			htmltext = "30300-09.htm";
		}
		else if (event.equalsIgnoreCase("30300_7"))
		{
			htmltext = "30300-10.htm";
			st.takeItems(PURE_MITHRIL_ORE, -1);
			st.takeItems(ANT_SOLDIER_ACID, -1);
			st.takeItems(WYRMS_TALON1, -1);
			st.takeItems(PUSHKINS_LIST, 1);
			st.giveItems(PURE_MITHRIL_CUP, 1);
		}
		else if (event.equalsIgnoreCase("30419_1"))
		{
			htmltext = "30419-02.htm";
		}
		else if (event.equalsIgnoreCase("30419_2"))
		{
			htmltext = "30419-03.htm";
		}
		else if (event.equalsIgnoreCase("30419_3"))
		{
			htmltext = "30419-04.htm";
			st.takeItems(THALIAS_LETTER1, 1);
			st.giveItems(ARKENIAS_CONTRACT, 1);
			st.giveItems(ARKENIAS_INSTRUCTIONS, 1);
		}
		else if (event.equalsIgnoreCase("30375_1"))
		{
			htmltext = "30375-02.htm";
			st.takeItems(ARKENIAS_INSTRUCTIONS, 1);
			st.giveItems(ADONIUS_LIST, 1);
		}
		else if (event.equalsIgnoreCase("30655_1"))
		{
			htmltext = "30655-02.htm";
			st.takeItems(THALIAS_LETTER2, 1);
			st.giveItems(ISAELS_INSTRUCTIONS, 1);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (st.getQuestItemsCount(MARK_OF_LIFE) > 0)
		{
			st.exitCurrentQuest(true);
			return "completed";
		}
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		if (id == CREATED)
		{
			st.setState(STARTED);
			st.setCond(0);
			st.set("id", "0");
		}
		if (npcId == 30460 && cond == 0)
		{
			if (cond < 15)
			{
				if (st.getPlayer().getRace() != Race.elf)
				{
					htmltext = "30460-01.htm";
				}
				else if (st.getPlayer().getLevel() < 37)
				{
					htmltext = "30460-02.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "30460-03.htm";
					st.setCond(1);
					return htmltext;
				}
			}
			else
			{
				htmltext = "30460-03.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (npcId == 30460 && cond == 1 && st.getQuestItemsCount(CARDIENS_LETTER) == 1)
		{
			htmltext = "30460-05.htm";
		}
		else if (npcId == 30460 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1)
		{
			htmltext = "30460-06.htm";
		}
		else if (npcId == 30460 && cond == 1 && st.getQuestItemsCount(CAMOMILE_CHARM) == 1)
		{
			htmltext = "30460-07.htm";
			st.takeItems(CAMOMILE_CHARM, -1);
			st.giveItems(MARK_OF_LIFE, 1);
			if (!st.getPlayer().getVarB("prof2.2"))
			{
				st.addExpAndSp(RewardExp, RewardSP);
				st.giveItems(ADENA_ID, RewardAdena);
				st.getPlayer().setVar("prof2.2", "1", -1);
			}
			st.playSound(SOUND_FINISH);
			st.unset("cond");
			st.exitCurrentQuest(false);
		}
		else if (npcId == 30154 && cond == 1 && st.getQuestItemsCount(CARDIENS_LETTER) == 1)
		{
			htmltext = "30154-01.htm";
		}
		else if (npcId == 30154 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1 && st.getQuestItemsCount(WATER_OF_LIFE) == 0)
		{
			htmltext = "30154-08.htm";
		}
		else if (npcId == 30154 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(WATER_OF_LIFE) > 0)
		{
			htmltext = "30154-09.htm";
			st.takeItems(WATER_OF_LIFE, 1);
			st.takeItems(MOONFLOWER_CHARM, 1);
			st.giveItems(CAMOMILE_CHARM, 1);
		}
		else if (npcId == 30154 && cond == 1 && st.getQuestItemsCount(CAMOMILE_CHARM) == 1)
		{
			htmltext = "30154-10.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(HIERARCHS_LETTER) > 0)
		{
			htmltext = "30371-01.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(GRAIL_DIAGRAM) > 0)
		{
			htmltext = "30371-04.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(PUSHKINS_LIST) > 0)
		{
			htmltext = "30371-05.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(PURE_MITHRIL_CUP) > 0)
		{
			htmltext = "30371-06.htm";
			st.takeItems(PURE_MITHRIL_CUP, 1);
			st.giveItems(THALIAS_LETTER1, 1);
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(THALIAS_LETTER1) > 0)
		{
			htmltext = "30371-07.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(ARKENIAS_CONTRACT) > 0)
		{
			htmltext = "30371-08.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(STARDUST) > 0)
		{
			htmltext = "30371-09.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(THALIAS_INSTRUCTIONS) > 0)
		{
			if (st.getPlayer().getLevel() < 38)
			{
				htmltext = "30371-12.htm";
			}
			else
			{
				htmltext = "30371-13.htm";
				st.takeItems(THALIAS_INSTRUCTIONS, 1);
				st.giveItems(THALIAS_LETTER2, 1);
			}
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(THALIAS_LETTER2) > 0)
		{
			htmltext = "30371-14.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(ISAELS_INSTRUCTIONS) > 0)
		{
			htmltext = "30371-15.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(TALINS_SPEAR) > 0 && st.getQuestItemsCount(ISAELS_LETTER) > 0)
		{
			htmltext = "30371-16.htm";
			st.takeItems(ISAELS_LETTER, 1);
			st.giveItems(GRAIL_OF_PURITY, 1);
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(TALINS_SPEAR) > 0 && st.getQuestItemsCount(GRAIL_OF_PURITY) > 0)
		{
			htmltext = "30371-17.htm";
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(TEARS_OF_UNICORN) > 0)
		{
			htmltext = "30371-18.htm";
			st.takeItems(TEARS_OF_UNICORN, 1);
			st.giveItems(WATER_OF_LIFE, 1);
		}
		else if (npcId == 30371 && cond == 1 && st.getQuestItemsCount(CAMOMILE_CHARM) > 0 || st.getQuestItemsCount(WATER_OF_LIFE) > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1)
		{
			htmltext = "30371-19.htm";
		}
		else if (npcId == 30300 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(GRAIL_DIAGRAM) > 0)
		{
			htmltext = "30300-01.htm";
		}
		else if (npcId == 30300 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(PUSHKINS_LIST) > 0)
		{
			if (st.getQuestItemsCount(PURE_MITHRIL_ORE) >= 10 && st.getQuestItemsCount(ANT_SOLDIER_ACID) >= 20 && st.getQuestItemsCount(WYRMS_TALON1) >= 20)
			{
				htmltext = "30300-08.htm";
			}
			else
			{
				htmltext = "30300-07.htm";
			}
		}
		else if (npcId == 30300 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(PURE_MITHRIL_CUP) > 0)
		{
			htmltext = "30300-11.htm";
		}
		else if (npcId == 30300 && cond == 1 && st.getQuestItemsCount(GRAIL_DIAGRAM) == 0 && st.getQuestItemsCount(PUSHKINS_LIST) == 0 && st.getQuestItemsCount(PURE_MITHRIL_CUP) == 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1)
		{
			htmltext = "30300-12.htm";
		}
		else if (npcId == 30419 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(THALIAS_LETTER1) > 0)
		{
			htmltext = "30419-01.htm";
		}
		else if (npcId == 30419 && cond == 1 && (st.getQuestItemsCount(ARKENIAS_INSTRUCTIONS) > 0 || st.getQuestItemsCount(ADONIUS_LIST) > 0) && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1)
		{
			htmltext = "30419-05.htm";
		}
		else if (npcId == 30419 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(ANDARIEL_SCRIPTURE_COPY) > 0)
		{
			htmltext = "30419-06.htm";
			st.takeItems(ARKENIAS_CONTRACT, 1);
			st.takeItems(ANDARIEL_SCRIPTURE_COPY, 1);
			st.giveItems(STARDUST, 1);
		}
		else if (npcId == 30419 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(STARDUST) > 0)
		{
			htmltext = "30419-07.htm";
		}
		else if (npcId == 30419 && cond == 1 && st.getQuestItemsCount(THALIAS_LETTER1) == 0 && st.getQuestItemsCount(ARKENIAS_CONTRACT) == 0 && st.getQuestItemsCount(ANDARIEL_SCRIPTURE_COPY) == 0 && st.getQuestItemsCount(STARDUST) == 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1)
		{
			htmltext = "30419-08.htm";
		}
		else if (npcId == 30375 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(ARKENIAS_INSTRUCTIONS) > 0)
		{
			htmltext = "30375-01.htm";
		}
		else if (npcId == 30375 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(ADONIUS_LIST) > 0)
		{
			if (st.getQuestItemsCount(SPIDER_ICHOR) >= 20 && st.getQuestItemsCount(HARPYS_DOWN) >= 20)
			{
				htmltext = "30375-04.htm";
				st.takeItems(SPIDER_ICHOR, st.getQuestItemsCount(SPIDER_ICHOR));
				st.takeItems(HARPYS_DOWN, st.getQuestItemsCount(HARPYS_DOWN));
				st.takeItems(ADONIUS_LIST, 1);
				st.giveItems(ANDARIEL_SCRIPTURE_COPY, 1);
			}
			else
			{
				htmltext = "30375-03.htm";
			}
		}
		else if (npcId == 30375 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(ANDARIEL_SCRIPTURE_COPY) > 0)
		{
			htmltext = "30375-05.htm";
		}
		else if (npcId == 30375 && cond == 1 && st.getQuestItemsCount(ARKENIAS_INSTRUCTIONS) == 0 && st.getQuestItemsCount(ADONIUS_LIST) == 0 && st.getQuestItemsCount(ANDARIEL_SCRIPTURE_COPY) == 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1)
		{
			htmltext = "30375-06.htm";
		}
		else if (npcId == 30655 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(THALIAS_LETTER2) > 0)
		{
			htmltext = "30655-01.htm";
		}
		else if (npcId == 30655 && cond == 1 && st.getQuestItemsCount(MOONFLOWER_CHARM) > 0 && st.getQuestItemsCount(ISAELS_INSTRUCTIONS) > 0)
		{
			if (st.getQuestItemsCount(TALINS_SPEAR_BLADE) > 0 && st.getQuestItemsCount(TALINS_SPEAR_SHAFT) > 0 && st.getQuestItemsCount(TALINS_RUBY) > 0 && st.getQuestItemsCount(TALINS_AQUAMARINE) > 0 && st.getQuestItemsCount(TALINS_AMETHYST) > 0 && st.getQuestItemsCount(TALINS_PERIDOT) > 0)
			{
				htmltext = "30655-04.htm";
				st.takeItems(TALINS_SPEAR_BLADE, 1);
				st.takeItems(TALINS_SPEAR_SHAFT, 1);
				st.takeItems(TALINS_RUBY, 1);
				st.takeItems(TALINS_AQUAMARINE, 1);
				st.takeItems(TALINS_AMETHYST, 1);
				st.takeItems(TALINS_PERIDOT, 1);
				st.takeItems(ISAELS_INSTRUCTIONS, 1);
				st.giveItems(ISAELS_LETTER, 1);
				st.giveItems(TALINS_SPEAR, 1);
			}
			else
			{
				htmltext = "30655-03.htm";
			}
		}
		else if (npcId == 30655 && cond == 1 && st.getQuestItemsCount(TALINS_SPEAR) > 0 && st.getQuestItemsCount(ISAELS_LETTER) > 0)
		{
			htmltext = "30655-05.htm";
		}
		else if (npcId == 30655 && cond == 1 && st.getQuestItemsCount(GRAIL_OF_PURITY) > 0 || st.getQuestItemsCount(WATER_OF_LIFE) > 0 || st.getQuestItemsCount(CAMOMILE_CHARM) > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1)
		{
			htmltext = "30655-06.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case 20550:
			st.set("id", "0");
			if (cond > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1 && st.getQuestItemsCount(PUSHKINS_LIST) == 1 && st.getQuestItemsCount(PURE_MITHRIL_ORE) < 10)
			{
				if (Rnd.chance(50))
				{
					st.giveItems(PURE_MITHRIL_ORE, 1);
					if (st.getQuestItemsCount(PURE_MITHRIL_ORE) < 10)
					{
						st.playSound(SOUND_ITEMGET);
					}
					else
					{
						st.playSound(SOUND_MIDDLE);
					}
				}
			}
			break;
		case 20176:
			st.set("id", "0");
			if (cond > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1 && st.getQuestItemsCount(PUSHKINS_LIST) == 1 && st.getQuestItemsCount(WYRMS_TALON1) < 20)
			{
				if (Rnd.chance(50))
				{
					st.giveItems(WYRMS_TALON1, 1);
					if (st.getQuestItemsCount(WYRMS_TALON1) < 20)
					{
						st.playSound(SOUND_ITEMGET);
					}
					else
					{
						st.playSound(SOUND_MIDDLE);
					}
				}
			}
			break;
		case 20082:
		case 20084:
		case 20086:
			st.set("id", "0");
			if (cond > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1 && st.getQuestItemsCount(PUSHKINS_LIST) == 1 && st.getQuestItemsCount(ANT_SOLDIER_ACID) < 20)
			{
				if (Rnd.chance(80))
				{
					st.giveItems(ANT_SOLDIER_ACID, 1);
					if (st.getQuestItemsCount(ANT_SOLDIER_ACID) < 20)
					{
						st.playSound(SOUND_ITEMGET);
					}
					else
					{
						st.playSound(SOUND_MIDDLE);
					}
				}
			}
			break;
		case 20087:
		case 20088:
			st.set("id", "0");
			if (cond > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1 && st.getQuestItemsCount(PUSHKINS_LIST) == 1 && st.getQuestItemsCount(ANT_SOLDIER_ACID) < 20)
			{
				if (Rnd.chance(50))
				{
					st.giveItems(ANT_SOLDIER_ACID, 1);
					if (st.getQuestItemsCount(ANT_SOLDIER_ACID) < 20)
					{
						st.playSound(SOUND_ITEMGET);
					}
					else
					{
						st.playSound(SOUND_MIDDLE);
					}
				}
			}
			break;
		case 20233:
			st.set("id", "0");
			if (cond > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1 && st.getQuestItemsCount(ADONIUS_LIST) == 1 && st.getQuestItemsCount(SPIDER_ICHOR) < 20)
			{
				if (Rnd.chance(50))
				{
					st.giveItems(SPIDER_ICHOR, 1);
					if (st.getQuestItemsCount(SPIDER_ICHOR) < 20)
					{
						st.playSound(SOUND_ITEMGET);
					}
					else
					{
						st.playSound(SOUND_MIDDLE);
					}
				}
			}
			break;
		case 20145:
			st.set("id", "0");
			if (cond > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1 && st.getQuestItemsCount(ADONIUS_LIST) == 1 && st.getQuestItemsCount(HARPYS_DOWN) < 20)
			{
				if (Rnd.chance(50))
				{
					st.giveItems(HARPYS_DOWN, 1);
					if (st.getQuestItemsCount(HARPYS_DOWN) < 20)
					{
						st.playSound(SOUND_ITEMGET);
					}
					else
					{
						st.playSound(SOUND_MIDDLE);
					}
				}
			}
			break;
		case 27077:
			if (cond > 0 && st.getQuestItemsCount(MOONFLOWER_CHARM) == 1 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == TALINS_SPEAR && st.getQuestItemsCount(GRAIL_OF_PURITY) == 1 && st.getQuestItemsCount(TEARS_OF_UNICORN) == 0)
			{
				if (st.getQuestItemsCount(TALINS_SPEAR) > 0)
				{
					st.takeItems(GRAIL_OF_PURITY, 1);
					st.takeItems(TALINS_SPEAR, 1);
					st.giveItems(TEARS_OF_UNICORN, 1);
				}
			}
			break;
		case 20581:
		case 20582:
			st.set("id", "0");
			if (cond > 0 && st.getQuestItemsCount(ISAELS_INSTRUCTIONS) == 1 && Rnd.chance(50))
			{
				if (st.getQuestItemsCount(TALINS_SPEAR_BLADE) == 0)
				{
					st.giveItems(TALINS_SPEAR_BLADE, 1);
					st.playSound(SOUND_ITEMGET);
				}
				else if (st.getQuestItemsCount(TALINS_SPEAR_SHAFT) == 0)
				{
					st.giveItems(TALINS_SPEAR_SHAFT, 1);
					st.playSound(SOUND_ITEMGET);
				}
				else if (st.getQuestItemsCount(TALINS_RUBY) == 0)
				{
					st.giveItems(TALINS_RUBY, 1);
					st.playSound(SOUND_ITEMGET);
				}
				else if (st.getQuestItemsCount(TALINS_AQUAMARINE) == 0)
				{
					st.giveItems(TALINS_AQUAMARINE, 1);
					st.playSound(SOUND_ITEMGET);
				}
				else if (st.getQuestItemsCount(TALINS_AMETHYST) == 0)
				{
					st.giveItems(TALINS_AMETHYST, 1);
					st.playSound(SOUND_ITEMGET);
				}
				else if (st.getQuestItemsCount(TALINS_PERIDOT) == 0)
				{
					st.giveItems(TALINS_PERIDOT, 1);
					st.playSound(SOUND_MIDDLE);
				}
			}
			break;
		default:
			break;
		}
		return null;
	}
}