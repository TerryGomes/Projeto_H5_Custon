package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * Квест Path To Artisan
 *
 * @author Sergey Ibryaev aka Artful
 */

public class _418_PathToArtisan extends Quest implements ScriptFile
{
	// NPC
	private static final int Silvera = 30527;
	private static final int Kluto = 30317;
	private static final int Pinter = 30298;
	// Quest Item
	private static final int SilverasRing = 1632;
	private static final int BoogleRatmanTooth = 1636;
	private static final int BoogleRatmanLeadersTooth = 1637;
	private static final int PassCertificate1st = 1633;
	private static final int KlutosLetter = 1638;
	private static final int FootprintOfThief = 1639;
	private static final int StolenSecretBox = 1640;
	private static final int PassCertificate2nd = 1634;
	private static final int SecretBox = 1641;
	// Item
	private static final int FinalPassCertificate = 1635;
	// MOB
	private static final int BoogleRatman = 20389;
	private static final int BoogleRatmanLeader = 20390;
	private static final int VukuOrcFighter = 20017;
	// Drop Cond
	// # [COND, NEWCOND, ID, REQUIRED, ITEM, NEED_COUNT, CHANCE, DROP]
	private static final int[][] DROPLIST_COND =
	{
		{
			1,
			0,
			BoogleRatman,
			SilverasRing,
			BoogleRatmanTooth,
			10,
			35,
			1
		},
		{
			1,
			0,
			BoogleRatmanLeader,
			SilverasRing,
			BoogleRatmanLeadersTooth,
			2,
			25,
			1
		},
		{
			5,
			6,
			VukuOrcFighter,
			FootprintOfThief,
			StolenSecretBox,
			1,
			20,
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

	public _418_PathToArtisan()
	{
		super(false);
		addStartNpc(Silvera);
		addTalkId(Kluto, Pinter);
		// Mob Drop
		for (int i = 0; i < DROPLIST_COND.length; i++)
		{
			addKillId(DROPLIST_COND[i][2]);
			addQuestItem(DROPLIST_COND[i][4]);
		}
		addQuestItem(SilverasRing, PassCertificate1st, SecretBox, KlutosLetter, FootprintOfThief, PassCertificate2nd);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("blacksmith_silvery_q0418_06.htm"))
		{
			st.giveItems(SilverasRing, 1);
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("blacksmith_kluto_q0418_04.htm") || event.equalsIgnoreCase("blacksmith_kluto_q0418_07.htm"))
		{
			st.giveItems(KlutosLetter, 1);
			st.setCond(4);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("blacksmith_pinter_q0418_03.htm"))
		{
			st.takeItems(KlutosLetter, -1);
			st.giveItems(FootprintOfThief, 1);
			st.setCond(5);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("blacksmith_pinter_q0418_06.htm"))
		{
			st.takeItems(StolenSecretBox, -1);
			st.takeItems(FootprintOfThief, -1);
			st.giveItems(SecretBox, 1);
			st.giveItems(PassCertificate2nd, 1);
			st.setCond(7);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("blacksmith_kluto_q0418_10.htm") || event.equalsIgnoreCase("blacksmith_kluto_q0418_12.htm"))
		{
			st.takeItems(PassCertificate1st, -1);
			st.takeItems(PassCertificate2nd, -1);
			st.takeItems(SecretBox, -1);
			if (st.getPlayer().getClassId().getLevel() == 1)
			{
				st.giveItems(FinalPassCertificate, 1);
				if (!st.getPlayer().getVarB("prof1"))
				{
					st.getPlayer().setVar("prof1", "1", -1);
					st.addExpAndSp(228064, 16455);
					// FIXME [G1ta0] дать адены, только если первый чар на акке
					st.giveItems(ADENA_ID, 81900);
				}
			}
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
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
		case Silvera:
			if (st.getQuestItemsCount(FinalPassCertificate) != 0)
			{
				htmltext = "blacksmith_silvery_q0418_04.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				switch (cond)
				{
				case 0:
					if (st.getPlayer().getClassId().getId() != 0x35)
					{
						if (st.getPlayer().getClassId().getId() == 0x38)
						{
							htmltext = "blacksmith_silvery_q0418_02a.htm";
						}
						else
						{
							htmltext = "blacksmith_silvery_q0418_02.htm";
						}
						st.exitCurrentQuest(true);
					}
					else if (st.getPlayer().getLevel() < 18)
					{
						htmltext = "blacksmith_silvery_q0418_03.htm";
						st.exitCurrentQuest(true);
					}
					else
					{
						htmltext = "blacksmith_silvery_q0418_01.htm";
					}
					break;
				case 1:
					htmltext = "blacksmith_silvery_q0418_07.htm";
					break;
				case 2:
					st.takeItems(BoogleRatmanTooth, -1);
					st.takeItems(BoogleRatmanLeadersTooth, -1);
					st.takeItems(SilverasRing, -1);
					st.giveItems(PassCertificate1st, 1);
					htmltext = "blacksmith_silvery_q0418_08.htm";
					st.setCond(3);
					break;
				case 3:
					htmltext = "blacksmith_silvery_q0418_09.htm";
					break;
				default:
					break;
				}
			}
			break;
		case Kluto:
			switch (cond)
			{
			case 3:
				htmltext = "blacksmith_kluto_q0418_01.htm";
				break;
			case 4:
			case 5:
				htmltext = "blacksmith_kluto_q0418_08.htm";
				break;
			case 7:
				htmltext = "blacksmith_kluto_q0418_09.htm";
				break;
			default:
				break;
			}
			break;
		case Pinter:
			switch (cond)
			{
			case 4:
				htmltext = "blacksmith_pinter_q0418_01.htm";
				break;
			case 5:
				htmltext = "blacksmith_pinter_q0418_04.htm";
				break;
			case 6:
				htmltext = "blacksmith_pinter_q0418_05.htm";
				break;
			case 7:
				htmltext = "blacksmith_pinter_q0418_07.htm";
				break;
			default:
				break;
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
		if (cond == 1 && st.getQuestItemsCount(BoogleRatmanTooth) >= 10 && st.getQuestItemsCount(BoogleRatmanLeadersTooth) >= 2)
		{
			st.setCond(2);
			st.setState(STARTED);
		}
		return null;
	}
}