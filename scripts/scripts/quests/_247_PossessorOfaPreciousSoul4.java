package quests;

import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.scripts.ScriptFile;

public class _247_PossessorOfaPreciousSoul4 extends Quest implements ScriptFile
{
	private static int CARADINE = 31740;
	private static int LADY_OF_LAKE = 31745;

	private static int CARADINE_LETTER_LAST = 7679;
	private static int NOBLESS_TIARA = 7694;

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

	public _247_PossessorOfaPreciousSoul4()
	{
		super(false);

		addStartNpc(CARADINE);

		addTalkId(LADY_OF_LAKE);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int cond = st.getCond();
		if (cond == 0 && event.equals("caradine_q0247_03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (cond == 1)
		{
			if (event.equals("caradine_q0247_04.htm"))
			{
				return htmltext;
			}
			else if (event.equals("caradine_q0247_05.htm"))
			{
				st.setCond(2);
				st.takeItems(CARADINE_LETTER_LAST, 1);
				st.getPlayer().teleToLocation(143230, 44030, -3030);
				return htmltext;
			}
		}
		else if (cond == 2)
		{
			if (event.equals("caradine_q0247_06.htm"))
			{
				return htmltext;
			}
			else if (event.equals("caradine_q0247_05.htm"))
			{
				st.getPlayer().teleToLocation(143230, 44030, -3030);
				return htmltext;
			}
			else if (event.equals("lady_of_the_lake_q0247_02.htm") || event.equals("lady_of_the_lake_q0247_03.htm") || event.equals("lady_of_the_lake_q0247_04.htm"))
			{
				return htmltext;
			}
			else if (event.equals("lady_of_the_lake_q0247_05.htm"))
			{
				if (st.getPlayer().getLevel() >= 75)
				{
					st.giveItems(NOBLESS_TIARA, 1);
					st.addExpAndSp(93836, 0);
					st.playSound(SOUND_FINISH);
					st.unset("cond");
					st.exitCurrentQuest(false);
					Olympiad.addNoble(st.getPlayer());
					st.getPlayer().setNoble(true);
					st.getPlayer().updatePledgeClass();
					st.getPlayer().updateNobleSkills();
					st.getPlayer().sendPacket(new SkillList(st.getPlayer()));
					st.getPlayer().broadcastUserInfo(true);
				}
				else
				{
					htmltext = "lady_of_the_lake_q0247_06.htm";
				}
			}
		}
		return htmltext;
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
		int id = st.getState();
		int cond = st.getCond();
		if (npcId == CARADINE)
		{
			QuestState previous = st.getPlayer().getQuestState(_246_PossessorOfaPreciousSoul3.class);
			if (id == CREATED && previous != null && previous.getState() == COMPLETED)
			{
				if (st.getPlayer().getLevel() < 75)
				{
					htmltext = "caradine_q0247_02.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "caradine_q0247_01.htm";
				}
			}
			else if (cond == 1)
			{
				htmltext = "caradine_q0247_03.htm";
			}
			else if (cond == 2)
			{
				htmltext = "caradine_q0247_06.htm";
			}
		}
		else if (npcId == LADY_OF_LAKE && cond == 2)
		{
			if (st.getPlayer().getLevel() >= 75)
			{
				htmltext = "lady_of_the_lake_q0247_01.htm";
			}
			else
			{
				htmltext = "lady_of_the_lake_q0247_06.htm";
			}
		}
		return htmltext;
	}
}