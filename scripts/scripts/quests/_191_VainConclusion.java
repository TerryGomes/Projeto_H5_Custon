package quests;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _191_VainConclusion extends Quest implements ScriptFile
{
	private static final int Kusto = 30512;
	private static final int Lorain = 30673;
	private static final int Dorothy = 30970;
	private static final int Shegfield = 30068;

	private static final int Metal = 10371;

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

	public _191_VainConclusion()
	{
		super(false);

		addTalkId(Kusto, Dorothy, Lorain, Shegfield);
		addFirstTalkId(Dorothy);
		addQuestItem(Metal);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("30970-03.htm"))
		{
			st.playSound(SOUND_ACCEPT);
			st.setCond(1);
			st.giveItems(Metal, 1);
		}
		else if (event.equalsIgnoreCase("30673-02.htm"))
		{
			st.playSound(SOUND_MIDDLE);
			st.setCond(2);
			st.takeItems(Metal, -1);
		}
		else if (event.equalsIgnoreCase("30068-03.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30512-02.htm"))
		{
			st.giveItems(ADENA_ID, 117327);
			st.addExpAndSp(309467, 20614);
			st.exitCurrentQuest(false);
			st.playSound(SOUND_FINISH);
		}

		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (st.getState() == STARTED)
		{
			switch (npcId)
			{
			case Dorothy:
				if (cond == 0)
				{
					if (st.getPlayer().getLevel() < 42)
					{
						htmltext = "30970-00.htm";
					}
					else
					{
						htmltext = "30970-01.htm";
					}
				}
				else if (cond == 1)
				{
					htmltext = "30970-04.htm";
				}
				break;
			case Lorain:
				switch (cond)
				{
				case 1:
					htmltext = "30673-01.htm";
					break;
				case 2:
					htmltext = "30673-03.htm";
					break;
				case 3:
					htmltext = "30673-04.htm";
					st.setCond(4);
					st.playSound(SOUND_MIDDLE);
					break;
				case 4:
					htmltext = "30673-05.htm";
					break;
				default:
					break;
				}
				break;
			case Shegfield:
				if (cond == 2)
				{
					htmltext = "30068-01.htm";
				}
				else if (cond == 3)
				{
					htmltext = "30068-04.htm";
				}
				break;
			case Kusto:
				if (cond == 4)
				{
					htmltext = "30512-01.htm";
				}
				break;
			default:
				break;
			}
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		QuestState qs = player.getQuestState(_188_SealRemoval.class);
		if (qs != null && qs.isCompleted() && player.getQuestState(getClass()) == null)
		{
			newQuestState(player, STARTED);
		}
		return "";
	}
}