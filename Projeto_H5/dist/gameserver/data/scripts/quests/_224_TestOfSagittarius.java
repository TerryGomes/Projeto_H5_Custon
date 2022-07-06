package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _224_TestOfSagittarius extends Quest implements ScriptFile
{
	private static final int BERNARDS_INTRODUCTION_ID = 3294;
	private static final int LETTER_OF_HAMIL3_ID = 3297;
	private static final int HUNTERS_RUNE2_ID = 3299;
	private static final int MARK_OF_SAGITTARIUS_ID = 3293;
	private static final int CRESCENT_MOON_BOW_ID = 3028;
	private static final int TALISMAN_OF_KADESH_ID = 3300;
	private static final int BLOOD_OF_LIZARDMAN_ID = 3306;
	private static final int LETTER_OF_HAMIL1_ID = 3295;
	private static final int LETTER_OF_HAMIL2_ID = 3296;
	private static final int HUNTERS_RUNE1_ID = 3298;
	private static final int TALISMAN_OF_SNAKE_ID = 3301;
	private static final int MITHRIL_CLIP_ID = 3302;
	private static final int STAKATO_CHITIN_ID = 3303;
	private static final int ST_BOWSTRING_ID = 3304;
	private static final int MANASHENS_HORN_ID = 3305;
	private static final int WOODEN_ARROW_ID = 17;
	private static final int RewardExp = 447444;
	private static final int RewardSP = 30704;
	private static final int RewardAdena = 80903;

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

	public _224_TestOfSagittarius()
	{
		super(false);
		addStartNpc(30702);
		addTalkId(30514);
		addTalkId(30626);
		addTalkId(30653);
		addTalkId(30702);
		addTalkId(30717);

		addKillId(20230);
		addKillId(20232);
		addKillId(20233);
		addKillId(20234);
		addKillId(20269);
		addKillId(20270);
		addKillId(27090);
		addKillId(20551);
		addKillId(20563);
		addKillId(20577);
		addKillId(20578);
		addKillId(20579);
		addKillId(20580);
		addKillId(20581);
		addKillId(20582);
		addKillId(20079);
		addKillId(20080);
		addKillId(20081);
		addKillId(20082);
		addKillId(20084);
		addKillId(20086);
		addKillId(20089);
		addKillId(20090);

		addQuestItem(new int[]
		{
			HUNTERS_RUNE2_ID,
			CRESCENT_MOON_BOW_ID,
			TALISMAN_OF_KADESH_ID,
			BLOOD_OF_LIZARDMAN_ID,
			BERNARDS_INTRODUCTION_ID,
			HUNTERS_RUNE1_ID,
			LETTER_OF_HAMIL1_ID,
			TALISMAN_OF_SNAKE_ID,
			LETTER_OF_HAMIL2_ID,
			LETTER_OF_HAMIL3_ID,
			MITHRIL_CLIP_ID,
			STAKATO_CHITIN_ID,
			ST_BOWSTRING_ID,
			MANASHENS_HORN_ID
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equals("1"))
		{
			htmltext = "30702-04.htm";
			st.setCond(1);
			st.setState(STARTED);
			if (!st.getPlayer().getVarB("dd3"))
			{
				st.giveItems(7562, 96);
				st.getPlayer().setVar("dd3", "1", -1);
			}
			st.playSound(SOUND_ACCEPT);
			st.giveItems(BERNARDS_INTRODUCTION_ID, 1);
		}
		else if (event.equals("30626_1"))
		{
			htmltext = "30626-02.htm";
		}
		else if (event.equals("30626_2"))
		{
			htmltext = "30626-03.htm";
			st.takeItems(BERNARDS_INTRODUCTION_ID, st.getQuestItemsCount(BERNARDS_INTRODUCTION_ID));
			st.giveItems(LETTER_OF_HAMIL1_ID, 1);
			st.setCond(2);
		}
		else if (event.equals("30626_3"))
		{
			htmltext = "30626-06.htm";
		}
		else if (event.equals("30626_4"))
		{
			htmltext = "30626-07.htm";
			st.takeItems(HUNTERS_RUNE1_ID, st.getQuestItemsCount(HUNTERS_RUNE1_ID));
			st.giveItems(LETTER_OF_HAMIL2_ID, 1);
			st.setCond(5);
		}
		else if (event.equals("30653_1"))
		{
			htmltext = "30653-02.htm";
			st.takeItems(LETTER_OF_HAMIL1_ID, st.getQuestItemsCount(LETTER_OF_HAMIL1_ID));
			st.setCond(3);
		}
		else if (event.equals("30514_1"))
		{
			htmltext = "30514-02.htm";
			st.takeItems(LETTER_OF_HAMIL2_ID, st.getQuestItemsCount(LETTER_OF_HAMIL2_ID));
			st.setCond(6);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (st.getQuestItemsCount(MARK_OF_SAGITTARIUS_ID) > 0)
		{
			st.exitCurrentQuest(true);
			return "completed";
		}
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		if (id == CREATED)
		{
			st.setState(STARTED);
			st.setCond(0);
			st.set("id", "0");
		}
		if (npcId == 30702 && st.getCond() == 0)
		{
			if (st.getPlayer().getClassId().getId() == 0x07 || st.getPlayer().getClassId().getId() == 0x16 || st.getPlayer().getClassId().getId() == 0x23)
			{
				if (st.getPlayer().getLevel() >= 39)
				{
					htmltext = "30702-03.htm";
				}
				else
				{
					htmltext = "30702-01.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			{
				htmltext = "30702-02.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (npcId == 30702 && st.getCond() == 1 && st.getQuestItemsCount(BERNARDS_INTRODUCTION_ID) > 0)
		{
			htmltext = "30702-05.htm";
		}
		else if (npcId == 30626 && st.getCond() == 1 && st.getQuestItemsCount(BERNARDS_INTRODUCTION_ID) > 0)
		{
			htmltext = "30626-01.htm";
		}
		else if (npcId == 30626 && st.getCond() == 2 && st.getQuestItemsCount(LETTER_OF_HAMIL1_ID) > 0)
		{
			htmltext = "30626-04.htm";
		}
		else if (npcId == 30626 && st.getCond() == 4 && st.getQuestItemsCount(HUNTERS_RUNE1_ID) == 10)
		{
			htmltext = "30626-05.htm";
		}
		else if (npcId == 30626 && st.getCond() == 5 && st.getQuestItemsCount(LETTER_OF_HAMIL2_ID) > 0)
		{
			htmltext = "30626-08.htm";
		}
		else if (npcId == 30626 && st.getCond() == 8)
		{
			htmltext = "30626-09.htm";
			st.giveItems(LETTER_OF_HAMIL3_ID, 1);
			st.setCond(9);
		}
		else if (npcId == 30626 && st.getCond() == 9 && st.getQuestItemsCount(LETTER_OF_HAMIL3_ID) > 0)
		{
			htmltext = "30626-10.htm";
		}
		else if (npcId == 30626 && st.getCond() == 12 && st.getQuestItemsCount(CRESCENT_MOON_BOW_ID) > 0)
		{
			htmltext = "30626-11.htm";
			st.setCond(13);
		}
		else if (npcId == 30626 && st.getCond() == 13)
		{
			htmltext = "30626-12.htm";
		}
		else if (npcId == 30626 && st.getCond() == 14 && st.getQuestItemsCount(TALISMAN_OF_KADESH_ID) > 0)
		{
			htmltext = "30626-13.htm";
			st.takeItems(CRESCENT_MOON_BOW_ID, -1);
			st.takeItems(TALISMAN_OF_KADESH_ID, -1);
			st.takeItems(BLOOD_OF_LIZARDMAN_ID, -1);
			st.giveItems(MARK_OF_SAGITTARIUS_ID, 1);
			if (!st.getPlayer().getVarB("prof2.3"))
			{
				st.addExpAndSp(RewardExp, RewardSP);
				st.giveItems(ADENA_ID, RewardAdena);
				st.getPlayer().setVar("prof2.3", "1", -1);
			}
			st.playSound(SOUND_FINISH);
			st.unset("cond");
			st.exitCurrentQuest(false);
		}
		else if (npcId == 30653 && st.getCond() == 2 && st.getQuestItemsCount(LETTER_OF_HAMIL1_ID) > 0)
		{
			htmltext = "30653-01.htm";
		}
		else if (npcId == 30653 && st.getCond() == 3)
		{
			htmltext = "30653-03.htm";
		}
		else if (npcId == 30514 && st.getCond() == 5 && st.getQuestItemsCount(LETTER_OF_HAMIL2_ID) > 0)
		{
			htmltext = "30514-01.htm";
		}
		else if (npcId == 30514 && st.getCond() == 6)
		{
			htmltext = "30514-03.htm";
		}
		else if (npcId == 30514 && st.getCond() == 7 && st.getQuestItemsCount(TALISMAN_OF_SNAKE_ID) > 0)
		{
			htmltext = "30514-04.htm";
			st.takeItems(TALISMAN_OF_SNAKE_ID, st.getQuestItemsCount(TALISMAN_OF_SNAKE_ID));
			st.setCond(8);
		}
		else if (npcId == 30514 && st.getCond() == 8)
		{
			htmltext = "30514-05.htm";
		}
		else if (npcId == 30717 && st.getCond() == 9 && st.getQuestItemsCount(LETTER_OF_HAMIL3_ID) > 0)
		{
			htmltext = "30717-01.htm";
			st.takeItems(LETTER_OF_HAMIL3_ID, st.getQuestItemsCount(LETTER_OF_HAMIL3_ID));
			st.setCond(10);
		}
		else if (npcId == 30717 && st.getCond() == 10)
		{
			htmltext = "30717-03.htm";
		}
		else if (npcId == 30717 && st.getCond() == 12)
		{
			htmltext = "30717-04.htm";
		}
		else if (npcId == 30717 && st.getCond() == 11 && st.getQuestItemsCount(STAKATO_CHITIN_ID) > 0 && st.getQuestItemsCount(MITHRIL_CLIP_ID) > 0 && st.getQuestItemsCount(ST_BOWSTRING_ID) > 0 && st.getQuestItemsCount(MANASHENS_HORN_ID) > 0)
		{
			htmltext = "30717-02.htm";
			st.takeItems(MITHRIL_CLIP_ID, st.getQuestItemsCount(MITHRIL_CLIP_ID));
			st.takeItems(STAKATO_CHITIN_ID, st.getQuestItemsCount(STAKATO_CHITIN_ID));
			st.takeItems(ST_BOWSTRING_ID, st.getQuestItemsCount(ST_BOWSTRING_ID));
			st.takeItems(MANASHENS_HORN_ID, st.getQuestItemsCount(MANASHENS_HORN_ID));
			st.giveItems(CRESCENT_MOON_BOW_ID, 1);
			st.giveItems(WOODEN_ARROW_ID, 10);
			st.setCond(12);
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		switch (npcId)
		{
		case 20079:
		case 20080:
		case 20081:
		case 20084:
		case 20086:
		case 20089:
		case 20090:
			if (st.getCond() == 3 && st.getQuestItemsCount(HUNTERS_RUNE1_ID) < 10 && Rnd.chance(50))
			{
				st.giveItems(HUNTERS_RUNE1_ID, 1);
				if (st.getQuestItemsCount(HUNTERS_RUNE1_ID) == 10)
				{
					st.setCond(4);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		case 20269:
		case 20270:
			if (st.getCond() == 6 && st.getQuestItemsCount(HUNTERS_RUNE2_ID) < 10 && Rnd.chance(50))
			{
				st.giveItems(HUNTERS_RUNE2_ID, 1);
				if (st.getQuestItemsCount(HUNTERS_RUNE2_ID) == 10)
				{
					st.takeItems(HUNTERS_RUNE2_ID, 10);
					st.giveItems(TALISMAN_OF_SNAKE_ID, 1);
					st.setCond(7);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		case 20230:
		case 20232:
		case 20234:
			if (st.getCond() == 10 && st.getQuestItemsCount(STAKATO_CHITIN_ID) == 0 && Rnd.chance(10))
			{
				st.giveItems(STAKATO_CHITIN_ID, 1);
				if (st.getQuestItemsCount(MITHRIL_CLIP_ID) > 0 && st.getQuestItemsCount(ST_BOWSTRING_ID) > 0 && st.getQuestItemsCount(MANASHENS_HORN_ID) > 0)
				{
					st.setCond(11);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		case 20563:
			if (st.getCond() == 10 && st.getQuestItemsCount(MANASHENS_HORN_ID) == 0 && Rnd.chance(10))
			{
				st.giveItems(MANASHENS_HORN_ID, 1);
				if (st.getQuestItemsCount(MITHRIL_CLIP_ID) > 0 && st.getQuestItemsCount(ST_BOWSTRING_ID) > 0 && st.getQuestItemsCount(STAKATO_CHITIN_ID) > 0)
				{
					st.setCond(11);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		case 20233:
			if (st.getCond() == 10 && st.getQuestItemsCount(ST_BOWSTRING_ID) == 0 && Rnd.chance(10))
			{
				st.giveItems(ST_BOWSTRING_ID, 1);
				if (st.getQuestItemsCount(MITHRIL_CLIP_ID) > 0 && st.getQuestItemsCount(MANASHENS_HORN_ID) > 0 && st.getQuestItemsCount(STAKATO_CHITIN_ID) > 0)
				{
					st.setCond(11);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		case 20551:
			if (st.getCond() == 10 && st.getQuestItemsCount(MITHRIL_CLIP_ID) == 0 && Rnd.chance(10))
			{
				st.giveItems(MITHRIL_CLIP_ID, 1);
				if (st.getQuestItemsCount(ST_BOWSTRING_ID) > 0 && st.getQuestItemsCount(MANASHENS_HORN_ID) > 0 && st.getQuestItemsCount(STAKATO_CHITIN_ID) > 0)
				{
					st.setCond(11);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		case 20577:
		case 20578:
		case 20579:
		case 20580:
		case 20581:
		case 20582:
			if (st.getCond() == 13)
			{
				if (Rnd.chance((st.getQuestItemsCount(BLOOD_OF_LIZARDMAN_ID) - 120) * 5))
				{
					st.addSpawn(27090);
					st.takeItems(BLOOD_OF_LIZARDMAN_ID, st.getQuestItemsCount(BLOOD_OF_LIZARDMAN_ID));
					st.playSound(SOUND_BEFORE_BATTLE);
				}
				else
				{
					st.giveItems(BLOOD_OF_LIZARDMAN_ID, 1);
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		case 27090:
			if (st.getCond() == 13 && st.getQuestItemsCount(TALISMAN_OF_KADESH_ID) == 0)
			{
				if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == CRESCENT_MOON_BOW_ID)
				{
					st.giveItems(TALISMAN_OF_KADESH_ID, 1);
					st.setCond(14);
					st.playSound(SOUND_MIDDLE);
				}
				else
				{
					st.addSpawn(27090);
				}
			}
			break;
		default:
			break;
		}
		return null;
	}
}