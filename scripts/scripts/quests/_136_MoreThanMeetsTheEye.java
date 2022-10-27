package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _136_MoreThanMeetsTheEye extends Quest implements ScriptFile
{
	// NPC
	private static final int HARDIN = 30832;
	private static final int ERRICKIN = 30701;
	private static final int CLAYTON = 30464;
	// Item
	private static final int TransformSealbook = 9648;
	// Quest Item
	private static final int Ectoplasm = 9787;
	private static final int StabilizedEctoplasm = 9786;
	private static final int HardinsInstructions = 9788;
	private static final int GlassJaguarCrystal = 9789;
	private static final int BlankSealbook = 9790;

	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			3,
			4,
			20636,
			0,
			Ectoplasm,
			35,
			100,
			1
		},
		{
			3,
			4,
			20637,
			0,
			Ectoplasm,
			35,
			100,
			1
		},
		{
			3,
			4,
			20638,
			0,
			Ectoplasm,
			35,
			100,
			1
		},
		{
			3,
			4,
			20639,
			0,
			Ectoplasm,
			35,
			100,
			2
		},
		{
			7,
			8,
			20250,
			0,
			GlassJaguarCrystal,
			5,
			100,
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

	public _136_MoreThanMeetsTheEye()
	{
		super(false);

		addStartNpc(HARDIN);
		addTalkId(HARDIN);
		addTalkId(ERRICKIN);
		addTalkId(CLAYTON);

		addQuestItem(new int[]
		{
			StabilizedEctoplasm,
			HardinsInstructions,
			BlankSealbook,
			Ectoplasm,
			GlassJaguarCrystal
		});

		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("hardin_q0136_08.htm"))
		{
			st.setCond(2);
			st.set("id", "0");
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("magister_errickin_q0136_03.htm"))
		{
			st.setCond(3);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("hardin_q0136_16.htm"))
		{
			st.giveItems(HardinsInstructions, 1);
			st.setCond(6);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("magister_clayton_q0136_10.htm"))
		{
			st.setCond(7);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("hardin_q0136_23.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.giveItems(TransformSealbook, 1);
			st.giveItems(ADENA_ID, 67550, true);
			st.unset("id");
			st.unset("cond");
			st.exitCurrentQuest(false);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		switch (npcId)
		{
		case HARDIN:
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 50)
				{
					st.setCond(1);
					htmltext = "hardin_q0136_01.htm";
				}
				else
				{
					htmltext = "hardin_q0136_02.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 2:
			case 3:
			case 4:
				htmltext = "hardin_q0136_09.htm";
				break;
			case 5:
				st.takeItems(StabilizedEctoplasm, -1);
				htmltext = "hardin_q0136_10.htm";
				break;
			case 6:
				htmltext = "hardin_q0136_17.htm";
				break;
			case 9:
				st.takeItems(BlankSealbook, -1);
				htmltext = "hardin_q0136_18.htm";
				break;
			default:
				break;
			}
			break;
		case ERRICKIN:
			if (cond == 2)
			{
				htmltext = "magister_errickin_q0136_02.htm";
			}
			else if (cond == 3)
			{
				htmltext = "magister_errickin_q0136_03.htm";
			}
			else if (cond == 4 && st.getQuestItemsCount(Ectoplasm) < 35 && st.getInt("id") == 0)
			{
				st.setCond(3);
				htmltext = "magister_errickin_q0136_03.htm";
			}
			else if (cond == 4 && st.getInt("id") == 0)
			{
				st.takeItems(Ectoplasm, -1);
				htmltext = "magister_errickin_q0136_05.htm";
				st.set("id", "1");
			}
			else if (cond == 4 && st.getInt("id") == 1)
			{
				htmltext = "magister_errickin_q0136_06.htm";
				st.giveItems(StabilizedEctoplasm, 1);
				st.set("id", "0");
				st.setCond(5);
				st.setState(STARTED);
			}
			else if (cond == 5)
			{
				htmltext = "magister_errickin_q0136_07.htm";
			}
			break;
		case CLAYTON:
			if (cond == 6)
			{
				st.takeItems(HardinsInstructions, -1);
				htmltext = "magister_clayton_q0136_09.htm";
			}
			else if (cond == 7)
			{
				htmltext = "magister_clayton_q0136_12.htm";
			}
			else if (cond == 8 && st.getQuestItemsCount(GlassJaguarCrystal) < 5)
			{
				htmltext = "magister_clayton_q0136_12.htm";
				st.setCond(7);
			}
			else if (cond == 8)
			{
				htmltext = "magister_clayton_q0136_13.htm";
				st.takeItems(GlassJaguarCrystal, -1);
				st.giveItems(BlankSealbook, 1);
				st.setCond(9);
				st.setState(STARTED);
			}
			else if (cond == 9)
			{
				htmltext = "magister_clayton_q0136_14.htm";
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
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			if (cond == DROPLIST_COND[i][0] && npcId == DROPLIST_COND[i][2])
			{
				if (DROPLIST_COND[i][3] == 0 || st.getQuestItemsCount(DROPLIST_COND[i][3]) > 0)
				{
					long count = st.getQuestItemsCount(DROPLIST_COND[i][4]);
					if (DROPLIST_COND[i][5] > count && Rnd.chance(DROPLIST_COND[i][6]))
					{
						long random = 0;
						if (DROPLIST_COND[i][7] > 1)
						{
							random = Rnd.get(DROPLIST_COND[i][7]) + 1;
							if (count + random > DROPLIST_COND[i][5])
							{
								random = DROPLIST_COND[i][5] - count;
							}
						}
						else
						{
							random = 1;
						}
						// Аддон
						if (cond == 3)
						{
							if (random == 1)
							{
								if (Rnd.chance(15))
								{
									random = 2;
								}
							}
							else if (Rnd.chance(15))
							{
								random = 3;
							}
							if (count + random > DROPLIST_COND[i][5])
							{
								random = DROPLIST_COND[i][5] - count;
							}
						}
						// Конец Аддона
						st.giveItems(DROPLIST_COND[i][4], random);
						if (count + random == DROPLIST_COND[i][5])
						{
							st.playSound(SOUND_MIDDLE);
							if (DROPLIST_COND[i][1] != 0)
							{
								st.setCond(Integer.valueOf(DROPLIST_COND[i][1]));
								st.setState(STARTED);
							}
						}
						else
						{
							st.playSound(SOUND_ITEMGET);
						}
					}
				}
			}
		}
		return null;
	}
}