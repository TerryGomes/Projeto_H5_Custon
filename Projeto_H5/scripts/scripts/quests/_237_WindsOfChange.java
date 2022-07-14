package quests;

import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _237_WindsOfChange extends Quest implements ScriptFile
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

	private static final int Flauen = 30899;
	private static final int Iason = 30969;
	private static final int Roman = 30897;
	private static final int Morelyn = 30925;
	private static final int Helvetica = 32641;
	private static final int Athenia = 32643;

	private static final int FlauensLetter = 14862;
	private static final int LetterToHelvetica = 14863;
	private static final int LetterToAthenia = 14864;
	private static final int VicinityOfTheFieldOfSilenceResearchCenter = 14865;
	private static final int CertificateOfSupport = 14866;

	public _237_WindsOfChange()
	{
		super(false);
		addStartNpc(Flauen);
		addTalkId(Iason, Roman, Morelyn, Helvetica, Athenia);
		addQuestItem(FlauensLetter, LetterToHelvetica, LetterToAthenia);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30899-06.htm"))
		{
			st.giveItems(FlauensLetter, 1);
			st.setCond(1);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30969-05.htm"))
		{
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("30897-03.htm"))
		{
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("30925-03.htm"))
		{
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("30969-09.htm"))
		{
			st.giveItems(LetterToHelvetica, 1);
			st.setCond(5);
		}
		else if (event.equalsIgnoreCase("30969-10.htm"))
		{
			st.giveItems(LetterToAthenia, 1);
			st.setCond(6);
		}
		else if (event.equalsIgnoreCase("32641-02.htm"))
		{
			st.takeItems(LetterToHelvetica, -1);
			st.giveItems(ADENA_ID, 213876);
			st.addExpAndSp(892773, 60012);
			st.giveItems(VicinityOfTheFieldOfSilenceResearchCenter, 1);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("32643-02.htm"))
		{
			st.takeItems(LetterToAthenia, -1);
			st.giveItems(ADENA_ID, 213876);
			st.addExpAndSp(892773, 60012);
			st.giveItems(CertificateOfSupport, 1);
			st.setState(COMPLETED);
			st.exitCurrentQuest(false);
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

		switch (npcId)
		{
		case Flauen:
			if (id == CREATED)
			{
				if (st.getPlayer().getLevel() < 82)
				{
					st.exitCurrentQuest(true);
					htmltext = "30899-00.htm";
				}
				else
				{
					htmltext = "30899-01.htm";
				}
			}
			else if (id == COMPLETED)
			{
				htmltext = "30899-09.htm";
			}
			else if (cond < 5)
			{
				htmltext = "30899-07.htm";
			}
			else
			{
				htmltext = "30899-08.htm";
			}
			break;
		case Iason:
			if (cond == 1)
			{
				st.takeItems(FlauensLetter, -1);
				htmltext = "30969-01.htm";
			}
			else if (cond > 1 && cond < 4)
			{
				htmltext = "30969-06.htm";
			}
			else if (cond == 4)
			{
				htmltext = "30969-07.htm";
			}
			else if (cond > 4)
			{
				htmltext = "30969-11.htm";
			}
			break;
		case Roman:
			if (cond == 2)
			{
				htmltext = "30897-01.htm";
			}
			else if (cond > 2)
			{
				htmltext = "30897-04.htm";
			}
			break;
		case Morelyn:
			if (cond == 3)
			{
				htmltext = "30925-01.htm";
			}
			else if (cond > 3)
			{
				htmltext = "30925-04.htm";
			}
			break;
		case Helvetica:
			if (cond == 5)
			{
				htmltext = "32641-01.htm";
			}
			else if (id == COMPLETED)
			{
				htmltext = "32641-03.htm";
			}
			break;
		case Athenia:
			if (cond == 6)
			{
				htmltext = "32643-01.htm";
			}
			else if (id == COMPLETED)
			{
				htmltext = "32643-03.htm";
			}
			break;
		default:
			break;
		}

		return htmltext;
	}
}