package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _453_NotStrongEnough extends Quest implements ScriptFile
{
	private static final int Klemis = 32734;

	public static final String A_MOBS = "a_mobs";
	public static final String B_MOBS = "b_mobs";
	public static final String C_MOBS = "c_mobs";
	public static final String E_MOBS = "e_mobs";

	private static final int[] Rewards =
	{
		15815,
		15816,
		15817,
		15818,
		15819,
		15820,
		15821,
		15822,
		15823,
		15824,
		15825,
		15634,
		15635,
		15636,
		15637,
		15638,
		15639,
		15640,
		15641,
		15642,
		15643,
		15644
	};

	public _453_NotStrongEnough()
	{
		super(true);
		addStartNpc(Klemis);

		// bistakon 4 "1022746|1022747|1022748|1022749" 4 "15|15|15|15"
		addKillNpcWithLog(2, A_MOBS, 15, 22746, 22750);
		addKillNpcWithLog(2, B_MOBS, 15, 22747, 22751);
		addKillNpcWithLog(2, C_MOBS, 15, 22748, 22752);
		addKillNpcWithLog(2, E_MOBS, 15, 22749, 22753);
		// reptilikon 3 "1022754|1022755|1022756" 3 "20|20|20"
		addKillNpcWithLog(3, A_MOBS, 20, 22754, 22757);
		addKillNpcWithLog(3, B_MOBS, 20, 22755, 22758);
		addKillNpcWithLog(3, C_MOBS, 20, 22756, 22759);
		// cokrakon 3 "1022760|1022761|1022762" 3 "20|20|20"
		addKillNpcWithLog(4, A_MOBS, 20, 22760, 22763);
		addKillNpcWithLog(4, B_MOBS, 20, 22761, 22764);
		addKillNpcWithLog(4, C_MOBS, 20, 22762, 22765);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("klemis_q453_03.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("bistakon"))
		{
			htmltext = "klemis_q453_05.htm";
			st.setCond(2);
		}
		else if (event.equalsIgnoreCase("reptilicon"))
		{
			htmltext = "klemis_q453_06.htm";
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("cokrakon"))
		{
			htmltext = "klemis_q453_07.htm";
			st.setCond(4);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == Klemis)
		{
			switch (st.getState())
			{
			case CREATED:
			{
				QuestState qs = st.getPlayer().getQuestState(_10282_ToTheSeedOfAnnihilation.class);
				if (st.getPlayer().getLevel() >= 84 && qs != null && qs.isCompleted())
				{
					if (st.isNowAvailable())
					{
						htmltext = "klemis_q453_01.htm";
					}
					else
					{
						htmltext = "klemis_q453_00a.htm";
					}
				}
				else
				{
					htmltext = "klemis_q453_00.htm";
				}
				break;
			}
			case STARTED:
			{
				switch (cond)
				{
				case 1:
					htmltext = "klemis_q453_03.htm";
					break;
				case 2:
					htmltext = "klemis_q453_09.htm";
					break;
				case 3:
					htmltext = "klemis_q453_10.htm";
					break;
				case 4:
					htmltext = "klemis_q453_11.htm";
					break;
				case 5:
				{
					htmltext = "klemis_q453_12.htm";
					int rewardId = Rewards[Rnd.get(Rewards.length)];
					st.giveItems(rewardId, 1);
					st.setState(COMPLETED);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(this);
					break;
				}
				default:
					break;
				}
				break;
			}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		boolean doneKill = updateKill(npc, st);
		if (doneKill)
		{
			st.unset(A_MOBS);
			st.unset(B_MOBS);
			st.unset(C_MOBS);
			st.unset(E_MOBS);
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