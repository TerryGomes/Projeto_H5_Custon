package quests;

import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * Квест на вторую профессию Test Of The Reformer
 *
 * @author Sergey Ibryaev aka Artful
 */
public class _227_TestOfTheReformer extends Quest implements ScriptFile
{
	// NPC
	private static final int Pupina = 30118;
	private static final int Sla = 30666;
	private static final int Katari = 30668;
	private static final int OlMahumPilgrimNPC = 30732;
	private static final int Kakan = 30669;
	private static final int Nyakuri = 30670;
	private static final int Ramus = 30667;
	// Quest Items
	private static final int BookOfReform = 2822;
	private static final int LetterOfIntroduction = 2823;
	private static final int SlasLetter = 2824;
	private static final int Greetings = 2825;
	private static final int OlMahumMoney = 2826;
	private static final int KatarisLetter = 2827;
	private static final int NyakurisLetter = 2828;
	private static final int KakansLetter = 3037;
	private static final int UndeadList = 2829;
	private static final int RamussLetter = 2830;
	private static final int RippedDiary = 2831;
	private static final int HugeNail = 2832;
	private static final int LetterOfBetrayer = 2833;
	private static final int BoneFragment1 = 2834;
	private static final int BoneFragment2 = 2835;
	private static final int BoneFragment3 = 2836;
	private static final int BoneFragment4 = 2837;
	private static final int BoneFragment5 = 2838;
	// private static final int BoneFragment6 = 2839;
	// Items
	private static final int MarkOfReformer = 2821;
	// MOB
	private static final int NamelessRevenant = 27099;
	private static final int Aruraune = 27128;
	private static final int OlMahumInspector = 27129;
	private static final int OlMahumBetrayer = 27130;
	private static final int CrimsonWerewolf = 27131;
	private static final int KrudelLizardman = 27132;
	private static final int SilentHorror = 20404;
	private static final int SkeletonLord = 20104;
	private static final int SkeletonMarksman = 20102;
	private static final int MiserySkeleton = 20022;
	private static final int SkeletonArcher = 20100;

	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	public final int[][] DROPLIST_COND =
	{
		{
			18,
			0,
			SilentHorror,
			0,
			BoneFragment1,
			1,
			70,
			1
		},
		{
			18,
			0,
			SkeletonLord,
			0,
			BoneFragment2,
			1,
			70,
			1
		},
		{
			18,
			0,
			SkeletonMarksman,
			0,
			BoneFragment3,
			1,
			70,
			1
		},
		{
			18,
			0,
			MiserySkeleton,
			0,
			BoneFragment4,
			1,
			70,
			1
		},
		{
			18,
			0,
			SkeletonArcher,
			0,
			BoneFragment5,
			1,
			70,
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

	public _227_TestOfTheReformer()
	{
		super(false);
		addStartNpc(Pupina);
		addTalkId(Sla);
		addTalkId(Katari);
		addTalkId(OlMahumPilgrimNPC);
		addTalkId(Kakan);
		addTalkId(Nyakuri);
		addTalkId(Ramus);
		// Mob Drop
		addKillId(NamelessRevenant);
		addKillId(Aruraune);
		addKillId(OlMahumInspector);
		addKillId(OlMahumBetrayer);
		addKillId(CrimsonWerewolf);
		addKillId(KrudelLizardman);
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
			addQuestItem(DROPLIST_COND[i][4]);
		}
		addQuestItem(new int[]
		{
			BookOfReform,
			HugeNail,
			LetterOfIntroduction,
			SlasLetter,
			KatarisLetter,
			LetterOfBetrayer,
			OlMahumMoney,
			NyakurisLetter,
			UndeadList,
			Greetings,
			KakansLetter,
			RamussLetter,
			RippedDiary
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30118-04.htm"))
		{
			st.giveItems(BookOfReform, 1);
			if (!st.getPlayer().getVarB("dd3"))
			{
				st.giveItems(7562, 60);
				st.getPlayer().setVar("dd3", "1", -1);
			}
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30118-06.htm"))
		{
			st.takeItems(HugeNail, -1);
			st.takeItems(BookOfReform, -1);
			st.giveItems(LetterOfIntroduction, 1);
			st.setCond(4);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30666-04.htm"))
		{
			st.takeItems(LetterOfIntroduction, -1);
			st.giveItems(SlasLetter, 1);
			st.setCond(5);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30669-03.htm"))
		{
			if (GameObjectsStorage.getByNpcId(CrimsonWerewolf) == null)
			{
				st.setCond(12);
				st.setState(STARTED);
				st.addSpawn(CrimsonWerewolf);
				st.startQuestTimer("Wait4", 300000);
			}
			else
			{
				if (!st.isRunningQuestTimer("Wait4"))
				{
					st.startQuestTimer("Wait4", 300000);
				}
				htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
			}
		}
		else if (event.equalsIgnoreCase("30670-03.htm"))
		{
			if (GameObjectsStorage.getByNpcId(KrudelLizardman) == null)
			{
				st.setCond(15);
				st.setState(STARTED);
				st.addSpawn(KrudelLizardman);
				st.startQuestTimer("Wait5", 300000);
			}
			else
			{
				if (!st.isRunningQuestTimer("Wait5"))
				{
					st.startQuestTimer("Wait5", 300000);
				}
				htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
			}
		}
		else if (event.equalsIgnoreCase("Wait1"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(Aruraune);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
			if (st.getCond() == 2)
			{
				st.setCond(1);
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Wait2"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumInspector);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
			isQuest = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
			if (st.getCond() == 6)
			{
				st.setCond(5);
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Wait3"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumBetrayer);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Wait4"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CrimsonWerewolf);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
			if (st.getCond() == 12)
			{
				st.setCond(11);
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Wait5"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(KrudelLizardman);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
			if (st.getCond() == 15)
			{
				st.setCond(14);
			}
			return null;
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
		case Pupina:
			if (st.getQuestItemsCount(MarkOfReformer) != 0)
			{
				htmltext = "completed";
				st.exitCurrentQuest(true);
			}
			else if (cond == 0)
			{
				if (st.getPlayer().getClassId().getId() == 0x0f || st.getPlayer().getClassId().getId() == 0x2a)
				{
					if (st.getPlayer().getLevel() >= 39)
					{
						htmltext = "30118-03.htm";
					}
					else
					{
						htmltext = "30118-01.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "30118-02.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 3)
			{
				htmltext = "30118-05.htm";
			}
			else if (cond >= 4)
			{
				htmltext = "30118-07.htm";
			}
			break;
		case Sla:
			switch (cond)
			{
			case 4:
				htmltext = "30666-01.htm";
				break;
			case 5:
				htmltext = "30666-05.htm";
				break;
			case 10:
				st.takeItems(OlMahumMoney, -1);
				st.giveItems(Greetings, 3);
				htmltext = "30666-06.htm";
				st.setCond(11);
				st.setState(STARTED);
				break;
			case 20:
				st.takeItems(KatarisLetter, -1);
				st.takeItems(KakansLetter, -1);
				st.takeItems(NyakurisLetter, -1);
				st.takeItems(RamussLetter, -1);
				st.giveItems(MarkOfReformer, 1);
				if (!st.getPlayer().getVarB("prof2.3"))
				{
					st.addExpAndSp(626422, 42986);
					st.giveItems(ADENA_ID, 113264);
					st.getPlayer().setVar("prof2.3", "1", -1);
				}
				htmltext = "30666-07.htm";
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
				break;
			default:
				break;
			}
			break;
		case Katari:
			switch (cond)
			{
			case 5:
			case 6:
			{
				NpcInstance NPC = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
				NpcInstance Mob = GameObjectsStorage.getByNpcId(OlMahumInspector);
				if (NPC == null && Mob == null)
				{
					st.takeItems(SlasLetter, -1);
					htmltext = "30668-01.htm";
					st.setCond(6);
					st.setState(STARTED);
					st.addSpawn(OlMahumPilgrimNPC);
					st.addSpawn(OlMahumInspector);
					st.startQuestTimer("Wait2", 300000);
				}
				else
				{
					if (!st.isRunningQuestTimer("Wait2"))
					{
						st.startQuestTimer("Wait2", 300000);
					}
					htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
				}
				break;
			}
			case 8:
				if (GameObjectsStorage.getByNpcId(OlMahumBetrayer) == null)
				{
					htmltext = "30668-02.htm";
					st.addSpawn(OlMahumBetrayer);
					st.startQuestTimer("Wait3", 300000);
				}
				else
				{
					if (!st.isRunningQuestTimer("Wait3"))
					{
						st.startQuestTimer("Wait3", 300000);
					}
					htmltext = "<html><head><body>Please wait 5 minutes</body></html>";
				}
				break;
			case 9:
				st.takeItems(LetterOfBetrayer, -1);
				st.giveItems(KatarisLetter, 1);
				htmltext = "30668-03.htm";
				st.setCond(10);
				st.setState(STARTED);
				break;
			default:
				break;
			}
			break;
		case OlMahumPilgrimNPC:
			if (cond == 7)
			{
				st.giveItems(OlMahumMoney, 1);
				htmltext = "30732-01.htm";
				st.setCond(8);
				st.setState(STARTED);
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumInspector);
				if (isQuest != null)
				{
					isQuest.deleteMe();
				}
				isQuest = GameObjectsStorage.getByNpcId(OlMahumPilgrimNPC);
				if (isQuest != null)
				{
					isQuest.deleteMe();
				}
				st.cancelQuestTimer("Wait2");
			}
			break;
		case Kakan:
			if (cond == 11 || cond == 12)
			{
				htmltext = "30669-01.htm";
			}
			else if (cond == 13)
			{
				st.takeItems(Greetings, 1);
				st.giveItems(KakansLetter, 1);
				htmltext = "30669-04.htm";
				st.setCond(14);
				st.setState(STARTED);
			}
			break;
		case Nyakuri:
			if (cond == 14 || cond == 15)
			{
				htmltext = "30670-01.htm";
			}
			else if (cond == 16)
			{
				st.takeItems(Greetings, 1);
				st.giveItems(NyakurisLetter, 1);
				htmltext = "30670-04.htm";
				st.setCond(17);
				st.setState(STARTED);
			}
			break;
		case Ramus:
			if (cond == 17)
			{
				st.takeItems(Greetings, -1);
				st.giveItems(UndeadList, 1);
				htmltext = "30667-01.htm";
				st.setCond(18);
				st.setState(STARTED);
			}
			else if (cond == 19)
			{
				st.takeItems(BoneFragment1, -1);
				st.takeItems(BoneFragment2, -1);
				st.takeItems(BoneFragment3, -1);
				st.takeItems(BoneFragment4, -1);
				st.takeItems(BoneFragment5, -1);
				st.takeItems(UndeadList, -1);
				st.giveItems(RamussLetter, 1);
				htmltext = "30667-03.htm";
				st.setCond(20);
				st.setState(STARTED);
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
					if (DROPLIST_COND[i][5] == 0)
					{
						st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
					}
					else if (st.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]))
					{
						if (DROPLIST_COND[i][1] != cond && DROPLIST_COND[i][1] != 0)
						{
							st.setCond(Integer.valueOf(DROPLIST_COND[i][1]));
							st.setState(STARTED);
						}
					}
				}
			}
		}
		if (cond == 18 && st.getQuestItemsCount(BoneFragment1) != 0 && st.getQuestItemsCount(BoneFragment2) != 0 && st.getQuestItemsCount(BoneFragment3) != 0 && st.getQuestItemsCount(BoneFragment4) != 0 && st.getQuestItemsCount(BoneFragment5) != 0)
		{
			st.setCond(19);
			st.setState(STARTED);
		}
		else if (npcId == NamelessRevenant && (cond == 1 || cond == 2))
		{
			if (st.getQuestItemsCount(RippedDiary) < 6)
			{
				st.giveItems(RippedDiary, 1);
			}
			else if (GameObjectsStorage.getByNpcId(Aruraune) == null)
			{
				st.takeItems(RippedDiary, -1);
				st.setCond(2);
				st.setState(STARTED);
				st.addSpawn(Aruraune);
				st.startQuestTimer("Wait1", 300000);
			}
			else if (!st.isRunningQuestTimer("Wait1"))
			{
				st.startQuestTimer("Wait1", 300000);
			}
		}
		else
		{
			switch (npcId)
			{
			case Aruraune:
			{
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(Aruraune);
				if (isQuest != null)
				{
					isQuest.deleteMe();
				}
				if (cond == 2)
				{
					if (st.getQuestItemsCount(HugeNail) == 0)
					{
						st.giveItems(HugeNail, 1);
					}
					st.setCond(3);
					st.setState(STARTED);
					st.cancelQuestTimer("Wait1");
				}
				break;
			}
			case OlMahumInspector:
			{
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumInspector);
				if (isQuest != null)
				{
					isQuest.deleteMe();
				}
				st.cancelQuestTimer("Wait2");
				if (cond == 6)
				{
					st.setCond(7);
					st.setState(STARTED);
				}
				break;
			}
			case OlMahumBetrayer:
			{
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(OlMahumBetrayer);
				if (isQuest != null)
				{
					isQuest.deleteMe();
				}
				st.cancelQuestTimer("Wait3");
				if (cond == 8)
				{
					if (st.getQuestItemsCount(LetterOfBetrayer) == 0)
					{
						st.giveItems(LetterOfBetrayer, 1);
					}
					st.setCond(9);
					st.setState(STARTED);
				}
				break;
			}
			case CrimsonWerewolf:
			{
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(CrimsonWerewolf);
				if (isQuest != null)
				{
					isQuest.deleteMe();
				}
				st.cancelQuestTimer("Wait4");
				if (cond == 12)
				{
					st.setCond(13);
					st.setState(STARTED);
				}
				break;
			}
			case KrudelLizardman:
			{
				NpcInstance isQuest = GameObjectsStorage.getByNpcId(KrudelLizardman);
				if (isQuest != null)
				{
					isQuest.deleteMe();
				}
				st.cancelQuestTimer("Wait5");
				if (cond == 15)
				{
					st.setCond(16);
					st.setState(STARTED);
				}
				break;
			}
			default:
				break;
			}
		}
		return null;
	}
}