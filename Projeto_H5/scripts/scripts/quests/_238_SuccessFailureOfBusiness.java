package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _238_SuccessFailureOfBusiness extends Quest implements ScriptFile
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

	private static final int Helvetica = 32641;

	private static final int BrazierOfPurity = 18806;
	private static final int EvilSpirit = 22658;
	private static final int GuardianSpirit = 22659;

	private static final int VicinityOfTheFieldOfSilenceResearchCenter = 14865;
	private static final int BrokenPieveOfMagicForce = 14867;
	private static final int GuardianSpiritFragment = 14868;

	public _238_SuccessFailureOfBusiness()
	{
		super(false);
		addStartNpc(Helvetica);
		addKillId(BrazierOfPurity, EvilSpirit, GuardianSpirit);
		addQuestItem(BrokenPieveOfMagicForce, GuardianSpiritFragment);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("32641-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
		}
		if (event.equalsIgnoreCase("32641-06.htm"))
		{
			st.takeAllItems(BrokenPieveOfMagicForce);
			st.setCond(3);
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

		if (npcId == Helvetica)
		{
			if (id == CREATED)
			{
				if (st.getPlayer().getLevel() < 82 || !st.getPlayer().isQuestCompleted(_237_WindsOfChange.class))
				{
					st.exitCurrentQuest(true);
					htmltext = "32641-00.htm";
				}
				else if (st.getQuestItemsCount(VicinityOfTheFieldOfSilenceResearchCenter) == 0)
				{
					htmltext = "32641-10.htm";
				}
				else
				{
					htmltext = "32641-01.htm";
				}
			}
			else if (id == COMPLETED)
			{
				htmltext = "32641-09.htm";
			}
			else
			{
				switch (cond)
				{
				case 1:
					htmltext = "32641-04.htm";
					break;
				case 2:
					htmltext = "32641-05.htm";
					break;
				case 3:
					htmltext = "32641-07.htm";
					break;
				case 4:
					st.takeAllItems(VicinityOfTheFieldOfSilenceResearchCenter);
					st.takeAllItems(GuardianSpiritFragment);
					st.giveItems(ADENA_ID, 283346);
					st.addExpAndSp(1319736, 103553);
					st.setState(COMPLETED);
					st.exitCurrentQuest(false);
					htmltext = "32641-08.htm";
					break;
				default:
					break;
				}
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if (cond == 1 && npc.getNpcId() == BrazierOfPurity)
		{
			st.giveItems(BrokenPieveOfMagicForce, 1);
			if (st.getQuestItemsCount(BrokenPieveOfMagicForce) >= 10)
			{
				st.setCond(2);
			}
		}
		else if (cond == 3 && (npc.getNpcId() == EvilSpirit || npc.getNpcId() == GuardianSpirit))
		{
			st.giveItems(GuardianSpiritFragment, 1);
			if (st.getQuestItemsCount(GuardianSpiritFragment) >= 20)
			{
				st.setCond(4);
			}
		}
		return null;
	}
}