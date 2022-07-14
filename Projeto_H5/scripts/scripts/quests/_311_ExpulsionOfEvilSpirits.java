package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _311_ExpulsionOfEvilSpirits extends Quest implements ScriptFile
{
	private static int Chairen = 32655;

	private static int SoulCoreContainingEvilSpirit = 14881;
	private static int ProtectionSoulsPendant = 14848;

	private static int RagnaOrcAmulet = 14882;

	private static int DROP_CHANCE1 = 1;
	private static int DROP_CHANCE2 = 40;

	private static int[] MOBS =
	{
		22691,
		22692,
		22693,
		22694,
		22695,
		22696,
		22697,
		22698,
		22699,
		22701,
		22702
	};

	public _311_ExpulsionOfEvilSpirits()
	{
		super(false);
		addStartNpc(Chairen);
		addKillId(MOBS);
		addQuestItem(RagnaOrcAmulet, SoulCoreContainingEvilSpirit);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("chairen_q311_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("continue"))
		{
			htmltext = "chairen_q311_04b.htm";
		}
		else if (event.equalsIgnoreCase("quit"))
		{
			htmltext = "chairen_q311_04a.htm";
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("soulores"))
		{
			if (st.getQuestItemsCount(SoulCoreContainingEvilSpirit) >= 10)
			{
				st.takeItems(SoulCoreContainingEvilSpirit, 10);
				st.giveItems(ProtectionSoulsPendant, 1);
				htmltext = "chairen_q311_06a.htm";
			}
			else
			{
				htmltext = "chairen_q311_06b.htm";
			}
		}
		else
		{
			int id = 0;
			try
			{
				id = Integer.parseInt(event);
			}
			catch (Exception e)
			{
			}

			if (id > 0)
			{
				int count = 0;
				switch (id)
				{
				case 9482:
					count = 488;
					break;
				case 9483:
					count = 305;
					break;
				case 9484:
					count = 183;
					break;
				case 9485:
					count = 122;
					break;
				case 9486:
					count = 122;
					break;
				case 9487:
					count = 366;
					break;
				case 9488:
					count = 229;
					break;
				case 9489:
					count = 183;
					break;
				case 9490:
					count = 122;
					break;
				case 9491:
					count = 122;
					break;
				case 9497:
					count = 129;
					break;
				case 9628:
					count = 24;
					break;
				case 9629:
					count = 43;
					break;
				case 9630:
					count = 36;
					break;
				case 9625:
					count = 667;
					break;
				case 9626:
					count = 1000;
					break;
				default:
					count = 0;
					break;
				}
				if (count > 0)
				{
					if (st.getQuestItemsCount(RagnaOrcAmulet) >= count)
					{
						st.giveItems(id, 1);
						st.takeItems(RagnaOrcAmulet, count);
						st.playSound(SOUND_MIDDLE);
						htmltext = "chairen_q311_10.htm";
					}
					else
					{
						htmltext = "chairen_q311_11.htm";
					}
				}
				else
				{
					return null;
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int id = st.getState();
		int cond = st.getCond();
		if (npcId == Chairen)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 80)
				{
					htmltext = "chairen_q311_01.htm";
				}
				else
				{
					htmltext = "chairen_q311_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (id == STARTED)
			{
				if (st.getQuestItemsCount(RagnaOrcAmulet) >= 1)
				{
					htmltext = "chairen_q311_05.htm";
				}
				else
				{
					htmltext = "chairen_q311_04.htm";
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();

		if (cond == 1 && ArrayUtils.contains(MOBS, npcId))
		{
			if (Rnd.chance(DROP_CHANCE1) && st.getQuestItemsCount(SoulCoreContainingEvilSpirit) < 10)
			{
				st.giveItems(SoulCoreContainingEvilSpirit, 1);
				st.playSound(SOUND_FANFARE2);
			}
			if (Rnd.chance(DROP_CHANCE2))
			{
				st.giveItems(RagnaOrcAmulet, (int) Config.RATE_QUESTS_REWARD * 1);
				st.playSound(SOUND_ITEMGET);
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
}