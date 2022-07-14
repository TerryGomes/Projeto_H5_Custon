package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест на вторую профессию Test Of Witchcraft
 *
 * @author Sergey Ibryaev aka Artful
 */
public class _229_TestOfWitchcraft extends Quest implements ScriptFile
{
	// NPC
	private static final int Orim = 30630;
	private static final int Alexandria = 30098;
	private static final int Iker = 30110;
	private static final int Kaira = 30476;
	private static final int Lara = 30063;
	private static final int Roderik = 30631;
	private static final int Nestle = 30314;
	private static final int Leopold = 30435;
	private static final int Vasper = 30417;
	private static final int Vadin = 30188;
	private static final int Evert = 30633;
	private static final int Endrigo = 30632;
	// Quest Item
	private static final int MarkOfWitchcraft = 3307;
	private static final int OrimsDiagram = 3308;
	private static final int AlexandriasBook = 3309;
	private static final int IkersList = 3310;
	private static final int DireWyrmFang = 3311;
	private static final int LetoLizardmanCharm = 3312;
	private static final int EnchantedGolemHeartstone = 3313;
	private static final int LarasMemo = 3314;
	private static final int NestlesMemo = 3315;
	private static final int LeopoldsJournal = 3316;
	private static final int Aklantoth_1stGem = 3317;
	private static final int Aklantoth_2stGem = 3318;
	private static final int Aklantoth_3stGem = 3319;
	private static final int Aklantoth_4stGem = 3320;
	private static final int Aklantoth_5stGem = 3321;
	private static final int Aklantoth_6stGem = 3322;
	private static final int Brimstone_1st = 3323;
	private static final int OrimsInstructions = 3324;
	private static final int Orims1stLetter = 3325;
	private static final int Orims2stLetter = 3326;
	private static final int SirVaspersLetter = 3327;
	private static final int VadinsCrucifix = 3328;
	private static final int TamlinOrcAmulet = 3329;
	private static final int VadinsSanctions = 3330;
	private static final int IkersAmulet = 3331;
	private static final int SoultrapCrystal = 3332;
	private static final int PurgatoryKey = 3333;
	private static final int ZeruelBindCrystal = 3334;
	private static final int Brimstone_2nd = 3335;
	private static final int SwordOfBinding = 3029;
	// MOBs
	private static final int DireWyrm = 20557;
	private static final int EnchantedStoneGolem = 20565;
	private static final int LetoLizardman = 20577;
	private static final int LetoLizardmanArcher = 20578;
	private static final int LetoLizardmanSoldier = 20579;
	private static final int LetoLizardmanWarrior = 20580;
	private static final int LetoLizardmanShaman = 20581;
	private static final int LetoLizardmanOverlord = 20582;
	private static final int NamelessRevenant = 27099;
	private static final int SkeletalMercenary = 27100;
	private static final int DrevanulPrinceZeruel = 27101;
	private static final int TamlinOrc = 20601;
	private static final int TamlinOrcArcher = 20602;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			2,
			0,
			DireWyrm,
			IkersList,
			DireWyrmFang,
			20,
			100,
			1
		},
		{
			2,
			0,
			EnchantedStoneGolem,
			IkersList,
			EnchantedGolemHeartstone,
			20,
			80,
			1
		},
		{
			2,
			0,
			LetoLizardman,
			IkersList,
			LetoLizardmanCharm,
			20,
			50,
			1
		},
		{
			2,
			0,
			LetoLizardmanArcher,
			IkersList,
			LetoLizardmanCharm,
			20,
			50,
			1
		},
		{
			2,
			0,
			LetoLizardmanSoldier,
			IkersList,
			LetoLizardmanCharm,
			20,
			60,
			1
		},
		{
			2,
			0,
			LetoLizardmanWarrior,
			IkersList,
			LetoLizardmanCharm,
			20,
			60,
			1
		},
		{
			2,
			0,
			LetoLizardmanShaman,
			IkersList,
			LetoLizardmanCharm,
			20,
			70,
			1
		},
		{
			2,
			0,
			LetoLizardmanOverlord,
			IkersList,
			LetoLizardmanCharm,
			20,
			70,
			1
		},
		{
			2,
			0,
			NamelessRevenant,
			LarasMemo,
			Aklantoth_3stGem,
			1,
			100,
			1
		},
		{
			6,
			0,
			TamlinOrc,
			VadinsCrucifix,
			TamlinOrcAmulet,
			20,
			50,
			1
		},
		{
			6,
			0,
			TamlinOrcArcher,
			VadinsCrucifix,
			TamlinOrcAmulet,
			20,
			55,
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

	public _229_TestOfWitchcraft()
	{
		super(false);

		addStartNpc(Orim);

		addTalkId(Alexandria);
		addTalkId(Iker);
		addTalkId(Kaira);
		addTalkId(Lara);
		addTalkId(Roderik);
		addTalkId(Nestle);
		addTalkId(Leopold);
		addTalkId(Vasper);
		addTalkId(Vadin);
		addTalkId(Evert);
		addTalkId(Endrigo);

		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
		}

		addKillId(SkeletalMercenary);
		addKillId(DrevanulPrinceZeruel);

		addQuestItem(new int[]
		{
			OrimsDiagram,
			OrimsInstructions,
			Orims1stLetter,
			Orims2stLetter,
			Brimstone_1st,
			AlexandriasBook,
			IkersList,
			Aklantoth_1stGem,
			SoultrapCrystal,
			IkersAmulet,
			Aklantoth_2stGem,
			LarasMemo,
			NestlesMemo,
			LeopoldsJournal,
			Aklantoth_4stGem,
			Aklantoth_5stGem,
			Aklantoth_6stGem,
			SirVaspersLetter,
			SwordOfBinding,
			VadinsCrucifix,
			VadinsSanctions,
			Brimstone_2nd,
			PurgatoryKey,
			ZeruelBindCrystal,
			DireWyrmFang,
			EnchantedGolemHeartstone,
			LetoLizardmanCharm,
			Aklantoth_3stGem,
			TamlinOrcAmulet
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30630-08.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.giveItems(OrimsDiagram, 1);
			if (!st.getPlayer().getVarB("dd3"))
			{
				st.giveItems(7562, 104);
				st.getPlayer().setVar("dd3", "1", -1);
			}
			st.setCond(1);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30098-03.htm"))
		{
			st.giveItems(AlexandriasBook, 1);
			st.takeItems(OrimsDiagram, 1);
			st.setCond(2);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30110-03.htm"))
		{
			st.giveItems(IkersList, 1);
		}
		else if (event.equalsIgnoreCase("30476-02.htm"))
		{
			st.giveItems(Aklantoth_2stGem, 1);
		}
		else if (event.equalsIgnoreCase("30063-02.htm"))
		{
			st.giveItems(LarasMemo, 1);
		}
		else if (event.equalsIgnoreCase("30314-02.htm"))
		{
			st.giveItems(NestlesMemo, 1);
		}
		else if (event.equalsIgnoreCase("30435-02.htm"))
		{
			st.takeItems(NestlesMemo, 1);
			st.giveItems(LeopoldsJournal, 1);
		}
		else if (event.equalsIgnoreCase("30630-14.htm"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
			if (isQuest != null && !isQuest.isDead())
			{
				htmltext = "Drevanul Prince Zeruel is already spawned.";
			}
			else
			{
				st.takeItems(AlexandriasBook, 1);
				st.takeItems(Aklantoth_1stGem, 1);
				st.takeItems(Aklantoth_2stGem, 1);
				st.takeItems(Aklantoth_3stGem, 1);
				st.takeItems(Aklantoth_4stGem, 1);
				st.takeItems(Aklantoth_5stGem, 1);
				st.takeItems(Aklantoth_6stGem, 1);
				if (st.getQuestItemsCount(Brimstone_1st) == 0)
				{
					st.giveItems(Brimstone_1st, 1);
				}
				st.setCond(4);
				st.set("id", "1");
				st.startQuestTimer("DrevanulPrinceZeruel_Fail", 300000);
				NpcInstance Zeruel = st.addSpawn(DrevanulPrinceZeruel);
				if (Zeruel != null)
				{
					Zeruel.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 1);
				}
			}
		}
		else if (event.equalsIgnoreCase("30630-16.htm"))
		{
			htmltext = "30630-16.htm";
			st.takeItems(Brimstone_1st, -1);
			st.giveItems(OrimsInstructions, 1);
			st.giveItems(Orims1stLetter, 1);
			st.giveItems(Orims2stLetter, 1);
			st.setCond(6);
		}
		else if (event.equalsIgnoreCase("30110-08.htm"))
		{
			st.takeItems(Orims2stLetter, 1);
			st.giveItems(SoultrapCrystal, 1);
			st.giveItems(IkersAmulet, 1);
			if (st.getQuestItemsCount(SwordOfBinding) > 0)
			{
				st.setCond(7);
				st.setState(STARTED);
			}
		}
		else if (event.equalsIgnoreCase("30417-03.htm"))
		{
			st.takeItems(Orims1stLetter, 1);
			st.giveItems(SirVaspersLetter, 1);
		}
		else if (event.equalsIgnoreCase("30633-02.htm"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
			if (isQuest != null)
			{
				htmltext = "30633-fail.htm";
			}
			else
			{
				st.set("id", "2");
				st.setCond(9);
				if (st.getQuestItemsCount(Brimstone_2nd) == 0)
				{
					st.giveItems(Brimstone_2nd, 1);
				}
				st.addSpawn(DrevanulPrinceZeruel);
				st.startQuestTimer("DrevanulPrinceZeruel_Fail", 300000);
				NpcInstance Zeruel = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
				if (Zeruel != null)
				{
					Zeruel.getAggroList().addDamageHate(st.getPlayer(), 0, 1);
				}
			}
		}

		else if (event.equalsIgnoreCase("30630-20.htm"))
		{
			st.takeItems(ZeruelBindCrystal, 1);
		}
		else if (event.equalsIgnoreCase("30630-21.htm"))
		{
			st.takeItems(PurgatoryKey, 1);
		}
		else if (event.equalsIgnoreCase("30630-22.htm"))
		{
			st.takeItems(SwordOfBinding, -1);
			st.takeItems(IkersAmulet, -1);
			st.takeItems(OrimsInstructions, -1);
			if (!st.getPlayer().getVarB("prof2.3"))
			{
				st.addExpAndSp(1029122, 70620);
				st.giveItems(ADENA_ID, 186077);
				st.getPlayer().setVar("prof2.3", "1", -1);
			}
			st.giveItems(MarkOfWitchcraft, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		if (event.equalsIgnoreCase("DrevanulPrinceZeruel_Fail"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case Orim:
			if (st.getQuestItemsCount(MarkOfWitchcraft) != 0)
			{
				htmltext = "completed";
				st.exitCurrentQuest(true);
			}
			else
			{
				switch (cond)
				{
				case 0:
					if (st.getPlayer().getClassId().getId() == 0x0b || st.getPlayer().getClassId().getId() == 0x04 || st.getPlayer().getClassId().getId() == 0x20)
					{
						if (st.getPlayer().getLevel() < 39)
						{
							htmltext = "30630-02.htm";
							st.exitCurrentQuest(true);
						}
						else if (st.getPlayer().getClassId().getId() == 0x0b)
						{
							htmltext = "30630-03.htm";
						}
						else
						{
							htmltext = "30630-05.htm";
						}
					}
					else
					{
						htmltext = "30630-01.htm";
						st.exitCurrentQuest(true);
					}
					break;
				case 1:
					htmltext = "30630-09.htm";
					break;
				case 2:
					htmltext = "30630-10.htm";
					break;
				default:
					if (cond == 3 || st.getInt("id") == 1)
					{
						htmltext = "30630-11.htm";
					}
					else
					{
						switch (cond)
						{
						case 5:
							htmltext = "30630-15.htm";
							break;
						case 6:
							htmltext = "30630-17.htm";
							break;
						case 7:
							htmltext = "30630-18.htm";
							st.setCond(8);
							break;
						case 10:
							if (st.getQuestItemsCount(ZeruelBindCrystal) != 0)
							{
								htmltext = "30630-19.htm";
							}
							else if (st.getQuestItemsCount(PurgatoryKey) != 0)
							{
								htmltext = "30630-20.htm";
							}
							else
							{
								htmltext = "30630-21.htm";
							}
							break;
						default:
							break;
						}
					}
					break;
				}
			}
			break;
		case Alexandria:
			if (cond == 1)
			{
				htmltext = "30098-01.htm";
			}
			else if (cond == 2)
			{
				htmltext = "30098-04.htm";
			}
			else
			{
				htmltext = "30098-05.htm";
			}
			break;
		case Iker:
			switch (cond)
			{
			case 2:
				if (st.getQuestItemsCount(Aklantoth_1stGem) == 0 && st.getQuestItemsCount(IkersList) == 0)
				{
					htmltext = "30110-01.htm";
				}
				else if (st.getQuestItemsCount(IkersList) > 0 && (st.getQuestItemsCount(DireWyrmFang) < 20 || st.getQuestItemsCount(LetoLizardmanCharm) < 20 || st.getQuestItemsCount(EnchantedGolemHeartstone) < 20))
				{
					htmltext = "30110-04.htm";
				}
				else if (st.getQuestItemsCount(Aklantoth_1stGem) == 0 && st.getQuestItemsCount(IkersList) > 0)
				{
					st.takeItems(IkersList, -1);
					st.takeItems(DireWyrmFang, -1);
					st.takeItems(LetoLizardmanCharm, -1);
					st.takeItems(EnchantedGolemHeartstone, -1);
					st.giveItems(Aklantoth_1stGem, 1);
					htmltext = "30110-05.htm";
				}
				else if (st.getQuestItemsCount(Aklantoth_1stGem) == 1)
				{
					htmltext = "30110-06.htm";
				}
				break;
			case 6:
				htmltext = "30110-07.htm";
				break;
			case 10:
				htmltext = "30110-10.htm";
				break;
			default:
				htmltext = "30110-09.htm";
				break;
			}
			break;
		case Kaira:
			if (cond == 2)
			{
				if (st.getQuestItemsCount(Aklantoth_2stGem) == 0)
				{
					htmltext = "30476-01.htm";
				}
				else
				{
					htmltext = "30476-03.htm";
				}
			}
			else if (cond > 2)
			{
				htmltext = "30476-04.htm";
			}
			break;
		case Lara:
			if (cond == 2)
			{
				if (st.getQuestItemsCount(LarasMemo) == 0 && st.getQuestItemsCount(Aklantoth_3stGem) == 0)
				{
					htmltext = "30063-01.htm";
				}
				else if (st.getQuestItemsCount(LarasMemo) == 1 && st.getQuestItemsCount(Aklantoth_3stGem) == 0)
				{
					htmltext = "30063-03.htm";
				}
				else if (st.getQuestItemsCount(Aklantoth_3stGem) == 1)
				{
					htmltext = "30063-04.htm";
				}
			}
			else if (cond > 2)
			{
				htmltext = "30063-05.htm";
			}
			break;
		default:
			if (npcId == Roderik && cond == 2 && st.getQuestItemsCount(LarasMemo) > 0)
			{
				htmltext = "30631-01.htm";
			}
			else if (npcId == Nestle && cond == 2)
			{
				if (st.getQuestItemsCount(Aklantoth_1stGem) > 0 && st.getQuestItemsCount(Aklantoth_2stGem) > 0 && st.getQuestItemsCount(Aklantoth_3stGem) > 0)
				{
					htmltext = "30314-01.htm";
				}
				else
				{
					htmltext = "30314-04.htm";
				}
			}
			else
			{
				switch (npcId)
				{
				case Leopold:
					if (cond == 2 && st.getQuestItemsCount(NestlesMemo) > 0)
					{
						if (st.getQuestItemsCount(Aklantoth_4stGem) + st.getQuestItemsCount(Aklantoth_5stGem) + st.getQuestItemsCount(Aklantoth_6stGem) == 0)
						{
							htmltext = "30435-01.htm";
						}
						else
						{
							htmltext = "30435-04.htm";
						}
					}
					else
					{
						htmltext = "30435-05.htm";
					}
					break;
				case Vasper:
					if (cond == 6)
					{
						if (st.getQuestItemsCount(SirVaspersLetter) > 0 || st.getQuestItemsCount(VadinsCrucifix) > 0)
						{
							htmltext = "30417-04.htm";
						}
						else if (st.getQuestItemsCount(VadinsSanctions) == 0)
						{
							htmltext = "30417-01.htm";
						}
						else if (st.getQuestItemsCount(VadinsSanctions) != 0)
						{
							htmltext = "30417-05.htm";
							st.takeItems(VadinsSanctions, 1);
							st.giveItems(SwordOfBinding, 1);
							if (st.getQuestItemsCount(SoultrapCrystal) > 0)
							{
								st.setCond(7);
								st.setState(STARTED);
							}
						}
					}
					else if (cond == 7)
					{
						htmltext = "30417-06.htm";
					}
					break;
				case Vadin:
					if (cond == 6)
					{
						if (st.getQuestItemsCount(SirVaspersLetter) != 0)
						{
							htmltext = "30188-01.htm";
							st.takeItems(SirVaspersLetter, 1);
							st.giveItems(VadinsCrucifix, 1);
						}
						else if (st.getQuestItemsCount(VadinsCrucifix) > 0 && st.getQuestItemsCount(TamlinOrcAmulet) < 20)
						{
							htmltext = "30188-02.htm";
						}
						else if (st.getQuestItemsCount(TamlinOrcAmulet) >= 20)
						{
							htmltext = "30188-03.htm";
							st.takeItems(TamlinOrcAmulet, -1);
							st.takeItems(VadinsCrucifix, -1);
							st.giveItems(VadinsSanctions, 1);
						}
						else if (st.getQuestItemsCount(VadinsSanctions) > 0)
						{
							htmltext = "30188-04.htm";
						}
					}
					else if (cond == 7)
					{
						htmltext = "30188-05.htm";
					}
					break;
				case Evert:
					if (st.getInt("id") == 2 || cond == 8 && st.getQuestItemsCount(Brimstone_2nd) == 0)
					{
						htmltext = "30633-01.htm";
					}
					else
					{
						htmltext = "30633-03.htm";
					}
					break;
				default:
					if (npcId == Endrigo && cond == 2)
					{
						htmltext = "30632-01.htm";
					}
					break;
				}
			}
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
					if (npcId == NamelessRevenant)
					{
						st.takeItems(LarasMemo, -1);
					}
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
		if (cond == 2 && st.getQuestItemsCount(LeopoldsJournal) > 0 && npcId == SkeletalMercenary)
		{
			if (st.getQuestItemsCount(Aklantoth_4stGem) == 0 && Rnd.chance(50))
			{
				st.giveItems(Aklantoth_4stGem, 1);
			}
			if (st.getQuestItemsCount(Aklantoth_5stGem) == 0 && Rnd.chance(50))
			{
				st.giveItems(Aklantoth_5stGem, 1);
			}
			if (st.getQuestItemsCount(Aklantoth_6stGem) == 0 && Rnd.chance(50))
			{
				st.giveItems(Aklantoth_6stGem, 1);
			}
			if (st.getQuestItemsCount(Aklantoth_4stGem) != 0 && st.getQuestItemsCount(Aklantoth_5stGem) != 0 && st.getQuestItemsCount(Aklantoth_6stGem) != 0)
			{
				st.takeItems(LeopoldsJournal, -1);
				st.playSound(SOUND_MIDDLE);
				st.setCond(3);
				st.setState(STARTED);
			}
		}
		else if (cond == 4 && npcId == DrevanulPrinceZeruel)
		{
			st.cancelQuestTimer("DrevanulPrinceZeruel_Fail");
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
			st.setCond(5);
			st.unset("id");
			st.setState(STARTED);
		}
		else if (cond == 9 && npcId == DrevanulPrinceZeruel)
		{
			if (st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == SwordOfBinding)
			{
				st.takeItems(Brimstone_2nd, 1);
				st.takeItems(SoultrapCrystal, 1);
				st.giveItems(PurgatoryKey, 1);
				st.giveItems(ZeruelBindCrystal, 1);
				st.playSound(SOUND_MIDDLE);
				st.unset("id");
				st.setCond(10);
				st.setState(STARTED);
				return "You trapped the Seal of Drevanul Prince Zeruel";
			}
			st.cancelQuestTimer("DrevanulPrinceZeruel_Fail");
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(DrevanulPrinceZeruel);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
		}
		return null;
	}
}