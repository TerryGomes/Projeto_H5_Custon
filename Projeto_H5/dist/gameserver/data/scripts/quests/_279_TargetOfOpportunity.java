package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _279_TargetOfOpportunity extends Quest implements ScriptFile
{
	private static final int Jerian = 32302;
	private static final int CosmicScout = 22373;
	private static final int CosmicWatcher = 22374;
	private static final int CosmicPriest = 22375;
	private static final int CosmicLord = 22376;

	private static final int SealComponentsPart1 = 15517;
	private static final int SealComponentsPart2 = 15518;
	private static final int SealComponentsPart3 = 15519;
	private static final int SealComponentsPart4 = 15520;

	public _279_TargetOfOpportunity()
	{
		super(PARTY_ALL);
		addStartNpc(Jerian);
		addKillId(CosmicScout, CosmicWatcher, CosmicPriest, CosmicLord);
		addQuestItem(SealComponentsPart1, SealComponentsPart2, SealComponentsPart3, SealComponentsPart4);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("jerian_q279_04.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("jerian_q279_07.htm"))
		{
			st.takeAllItems(SealComponentsPart1, SealComponentsPart2, SealComponentsPart3, SealComponentsPart4);
			st.giveItems(15515, 1);
			st.giveItems(15516, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Jerian)
		{
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 82)
				{
					htmltext = "jerian_q279_01.htm";
				}
				else
				{
					htmltext = "jerian_q279_00.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "jerian_q279_05.htm";
				break;
			case 2:
				htmltext = "jerian_q279_06.htm";
				break;
			default:
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1)
		{
			if (npcId == CosmicScout && st.getQuestItemsCount(SealComponentsPart1) < 1 && Rnd.chance(15))
			{
				st.giveItems(SealComponentsPart1, 1);
			}
			else if (npcId == CosmicWatcher && st.getQuestItemsCount(SealComponentsPart2) < 1 && Rnd.chance(15))
			{
				st.giveItems(SealComponentsPart2, 1);
			}
			else if (npcId == CosmicPriest && st.getQuestItemsCount(SealComponentsPart3) < 1 && Rnd.chance(15))
			{
				st.giveItems(SealComponentsPart3, 1);
			}
			else if (npcId == CosmicLord && st.getQuestItemsCount(SealComponentsPart4) < 1 && Rnd.chance(15))
			{
				st.giveItems(SealComponentsPart4, 1);
			}

			if (st.getQuestItemsCount(SealComponentsPart1) >= 1 && st.getQuestItemsCount(SealComponentsPart2) >= 1 && st.getQuestItemsCount(SealComponentsPart3) >= 1 && st.getQuestItemsCount(SealComponentsPart4) >= 1)
			{
				st.setCond(2);
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