package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 *         Daily
 */
public class _902_ReclaimOurEra extends Quest implements ScriptFile
{
	private static final int Mathias = 31340;
	private static final int[] OrcsSilenos =
	{
		25309,
		25312,
		25315,
		25299,
		25302,
		25305
	};
	private static final int[] CannibalisticStakatoChief =
	{
		25667,
		25668,
		25669,
		25670
	};
	private static final int Anais = 25701;

	private static final int ShatteredBones = 21997;
	private static final int CannibalisticStakatoLeaderClaw = 21998;
	private static final int AnaisScroll = 21999;

	public _902_ReclaimOurEra()
	{
		super(PARTY_ALL);
		addStartNpc(Mathias);
		addKillId(OrcsSilenos);
		addKillId(CannibalisticStakatoChief);
		addKillId(Anais);
		addQuestItem(ShatteredBones, CannibalisticStakatoLeaderClaw, AnaisScroll);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("mathias_q902_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("mathias_q902_05.htm"))
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("mathias_q902_06.htm"))
		{
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("mathias_q902_07.htm"))
		{
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("mathias_q902_09.htm"))
		{
			if (st.takeAllItems(ShatteredBones) > 0)
			{
				st.giveItems(21750, 1);
				st.giveItems(ADENA_ID, 134038);
			}
			else if (st.takeAllItems(CannibalisticStakatoLeaderClaw) > 0)
			{
				st.giveItems(21750, 3);
				st.giveItems(ADENA_ID, 210119);
			}
			else if (st.takeAllItems(AnaisScroll) > 0)
			{
				st.giveItems(21750, 3);
				st.giveItems(ADENA_ID, 348155);
			}
			st.setState(COMPLETED);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(this);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npc.getNpcId() == Mathias)
		{
			switch (st.getState())
			{
			case CREATED:
				if (st.isNowAvailable())
				{
					if (st.getPlayer().getLevel() >= 80)
					{
						htmltext = "mathias_q902_01.htm";
					}
					else
					{
						htmltext = "mathias_q902_00.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "mathias_q902_00a.htm";
				}
				break;
			case STARTED:
				switch (cond)
				{
				case 1:
					htmltext = "mathias_q902_04.htm";
					break;
				case 2:
					htmltext = "mathias_q902_05.htm";
					break;
				case 3:
					htmltext = "mathias_q902_06.htm";
					break;
				case 4:
					htmltext = "mathias_q902_07.htm";
					break;
				case 5:
					htmltext = "mathias_q902_08.htm";
					break;
				default:
					break;
				}
				break;
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 2 && ArrayUtils.contains(OrcsSilenos, npc.getNpcId()))
		{
			st.giveItems(ShatteredBones, 1);
			st.setCond(5);
		}
		else if (cond == 3 && ArrayUtils.contains(CannibalisticStakatoChief, npc.getNpcId()))
		{
			st.giveItems(CannibalisticStakatoLeaderClaw, 1);
			st.setCond(5);
		}
		else if (cond == 4 && npc.getNpcId() == Anais)
		{
			st.giveItems(AnaisScroll, 1);
			st.setCond(5);
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