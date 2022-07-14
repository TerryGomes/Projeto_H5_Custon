package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _241_PossessorOfaPreciousSoul1 extends Quest implements ScriptFile
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

	private static final int LEGENG_OF_SEVENTEEN = 7587;
	private static final int MALRUK_SUCCUBUS_CLAW = 7597;
	private static final int ECHO_CRYSTAL = 7589;
	private static final int FADED_POETRY_BOOK = 7588;
	private static final int CRIMSON_MOSS = 7598;
	private static final int MEDICINE = 7599;
	private static final int VIRGILS_LETTER = 7677;

	public _241_PossessorOfaPreciousSoul1()
	{
		super(false);

		addStartNpc(31739);
		addTalkId(30753);
		addTalkId(30754);
		addTalkId(31042);
		addTalkId(30692);
		addTalkId(31742);
		addTalkId(31744);
		addTalkId(31336);
		addTalkId(31743);
		addTalkId(31740);

		addKillId(21154);
		addKillId(27113);
		addKillId(20244);
		addKillId(20245);
		addKillId(21511);
		addKillId(20669);

		addQuestItem(new int[]
		{
			LEGENG_OF_SEVENTEEN,
			MALRUK_SUCCUBUS_CLAW,
			FADED_POETRY_BOOK,
			ECHO_CRYSTAL,
			MEDICINE,
			CRIMSON_MOSS
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("31739-02.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30753-02.htm"))
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("30754-02.htm"))
		{
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("31739-04.htm"))
		{
			st.takeItems(LEGENG_OF_SEVENTEEN, -1);
			st.setCond(5);
		}
		else if (event.equalsIgnoreCase("31042-02.htm"))
		{
			st.setCond(6);
		}
		else if (event.equalsIgnoreCase("31042-04.htm"))
		{
			st.takeItems(MALRUK_SUCCUBUS_CLAW, -1);
			st.giveItems(ECHO_CRYSTAL, 1);
			st.setCond(8);
		}
		else if (event.equalsIgnoreCase("31739-06.htm"))
		{
			st.takeItems(ECHO_CRYSTAL, -1);
			st.setCond(9);
		}
		else if (event.equalsIgnoreCase("30692-02.htm"))
		{
			st.giveItems(FADED_POETRY_BOOK, 1);
			st.setCond(10);
		}
		else if (event.equalsIgnoreCase("31739-08.htm"))
		{
			st.takeItems(FADED_POETRY_BOOK, -1);
			st.setCond(11);
		}
		else if (event.equalsIgnoreCase("31742-02.htm"))
		{
			st.setCond(12);
		}
		else if (event.equalsIgnoreCase("31744-02.htm"))
		{
			st.setCond(13);
		}
		else if (event.equalsIgnoreCase("31336-02.htm"))
		{
			st.setCond(14);
		}
		else if (event.equalsIgnoreCase("31336-04.htm"))
		{
			st.takeItems(CRIMSON_MOSS, -1);
			st.giveItems(MEDICINE, 1);
			st.setCond(16);
		}
		else if (event.equalsIgnoreCase("31743-02.htm"))
		{
			st.takeItems(MEDICINE, -1);
			st.setCond(17);
		}
		else if (event.equalsIgnoreCase("31742-04.htm"))
		{
			st.setCond(18);
		}
		else if (event.equalsIgnoreCase("31740-02.htm"))
		{
			st.setCond(19);
		}
		else if (event.equalsIgnoreCase("31740-04.htm"))
		{
			st.giveItems(VIRGILS_LETTER, 1);
			st.addExpAndSp(263043, 0);
			st.unset("cond");
			st.exitCurrentQuest(false);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (!st.getPlayer().isSubClassActive())
		{
			return "Subclass only!";
		}

		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case 31739:
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 50)
				{
					htmltext = "31739-01.htm";
				}
				else
				{
					htmltext = "31739-00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "31739-02r.htm";
			}
			else if (cond == 4 && st.getQuestItemsCount(LEGENG_OF_SEVENTEEN) >= 1)
			{
				htmltext = "31739-03.htm";
			}
			else if (cond < 8 && st.getQuestItemsCount(ECHO_CRYSTAL) < 1)
			{
				htmltext = "31739-04r.htm";
			}
			else if (cond == 8 && st.getQuestItemsCount(ECHO_CRYSTAL) == 1)
			{
				htmltext = "31739-05.htm";
			}
			else if (cond < 10 && st.getQuestItemsCount(FADED_POETRY_BOOK) < 1)
			{
				htmltext = "31739-06r.htm";
			}
			else if (cond == 10 && st.getQuestItemsCount(FADED_POETRY_BOOK) == 1)
			{
				htmltext = "31739-07.htm";
			}
			else if (cond == 11)
			{
				htmltext = "31739-08r.htm";
			}
			break;
		case 30753:
			if (cond == 1)
			{
				htmltext = "30753-01.htm";
			}
			else if (cond == 2)
			{
				htmltext = "30753-02r.htm";
			}
			break;
		case 30754:
			if (cond == 2)
			{
				htmltext = "30754-01.htm";
			}
			else if (cond == 3 && st.getQuestItemsCount(LEGENG_OF_SEVENTEEN) < 1)
			{
				htmltext = "30754-02r.htm";
			}
			break;
		case 31042:
			if (cond == 5)
			{
				htmltext = "31042-01.htm";
			}
			else if (cond == 6 && st.getQuestItemsCount(MALRUK_SUCCUBUS_CLAW) < 10)
			{
				htmltext = "31042-02r.htm";
			}
			else if (cond == 7 && st.getQuestItemsCount(MALRUK_SUCCUBUS_CLAW) == 10)
			{
				htmltext = "31042-03.htm";
			}
			else if (cond == 8 && st.getQuestItemsCount(ECHO_CRYSTAL) >= 1)
			{
				htmltext = "31042-04r.htm";
			}
			else if (cond == 8 && st.getQuestItemsCount(ECHO_CRYSTAL) == 0)
			{
				st.giveItems(ECHO_CRYSTAL, 1);
				htmltext = "31042-04r.htm";
			}
			break;
		case 30692:
			if (cond == 9)
			{
				htmltext = "30692-01.htm";
			}
			else if (cond == 10)
			{
				htmltext = "30692-02r.htm";
			}
			break;
		case 31742:
			switch (cond)
			{
			case 11:
				htmltext = "31742-01.htm";
				break;
			case 12:
				htmltext = "31742-02r.htm";
				break;
			case 17:
				htmltext = "31742-03.htm";
				break;
			default:
				if (cond >= 18)
				{
					htmltext = "31742-04r.htm";
				}
				break;
			}
			break;
		case 31744:
			if (cond == 12)
			{
				htmltext = "31744-01.htm";
			}
			break;
		case 31336:
			if (cond == 13)
			{
				htmltext = "31336-01.htm";
			}
			else if (cond == 14 && st.getQuestItemsCount(CRIMSON_MOSS) < 5)
			{
				htmltext = "31336-02r.htm";
			}
			else if (cond == 15 && st.getQuestItemsCount(CRIMSON_MOSS) >= 5)
			{
				htmltext = "31336-03.htm";
			}
			else if (cond == 16 && st.getQuestItemsCount(MEDICINE) >= 1)
			{
				htmltext = "31336-04r.htm";
			}
			break;
		case 31743:
			if (cond == 16 && st.getQuestItemsCount(MEDICINE) >= 1)
			{
				htmltext = "31743-01.htm";
			}
			break;
		case 31740:
			if (cond == 18)
			{
				htmltext = "31740-01.htm";
			}
			else if (cond == 19)
			{
				htmltext = "31740-03.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (!st.getPlayer().isSubClassActive())
		{
			return null;
		}

		int npcId = npc.getNpcId();
		int cond = st.getCond();

		switch (cond)
		{
		case 3:
			if (npcId == 21154 && Rnd.chance(10))
			{
				st.addSpawn(27113);
			}
			else if (npcId == 27113 && st.getQuestItemsCount(LEGENG_OF_SEVENTEEN) == 0)
			{
				st.giveItems(LEGENG_OF_SEVENTEEN, 1);
				st.setCond(4);
				st.playSound(SOUND_ITEMGET);
			}
			break;
		case 6:
			if ((npcId == 20244 || npcId == 20245) && Rnd.chance(40))
			{
				if (st.getQuestItemsCount(MALRUK_SUCCUBUS_CLAW) <= 9)
				{
					st.giveItems(MALRUK_SUCCUBUS_CLAW, 1);
				}
				if (st.getQuestItemsCount(MALRUK_SUCCUBUS_CLAW) == 10)
				{
					st.playSound(SOUND_MIDDLE);
					st.setCond(7);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		case 14:
			if (npcId == 20669 && Rnd.chance(50))
			{
				if (st.getQuestItemsCount(CRIMSON_MOSS) <= 4)
				{
					st.giveItems(CRIMSON_MOSS, 1);
				}
				if (st.getQuestItemsCount(CRIMSON_MOSS) == 5)
				{
					st.playSound(SOUND_MIDDLE);
					st.setCond(15);
				}
				else
				{
					st.playSound(SOUND_ITEMGET);
				}
			}
			break;
		default:
			break;
		}
		return null;
	}
}