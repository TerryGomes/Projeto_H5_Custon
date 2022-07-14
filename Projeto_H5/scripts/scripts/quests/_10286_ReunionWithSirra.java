package quests;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public class _10286_ReunionWithSirra extends Quest implements ScriptFile
{
	private static final int Rafforty = 32020;
	private static final int Jinia = 32760;
	private static final int Jinia2 = 32781;
	private static final int Sirra = 32762;

	public _10286_ReunionWithSirra()
	{
		super(false);
		addStartNpc(Rafforty);
		addTalkId(Jinia, Jinia2, Sirra);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("rafforty_q10286_02.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("enterinstance"))
		{
			st.setCond(2);
			enterInstance(st.getPlayer(), 141);
			return null;
		}
		else if (event.equalsIgnoreCase("sirraspawn"))
		{
			st.setCond(3);
			NpcInstance sirra = st.getPlayer().getReflection().addSpawnWithoutRespawn(Sirra, new Location(-23848, -8744, -5413, 49152), 0);
			Functions.npcSay(sirra, "You are so enthusiastic in the road and that's all you do? Ha ha ha ...");
			return null;
		}
		else if (event.equalsIgnoreCase("sirra_q10286_04.htm"))
		{
			st.giveItems(15470, 5);
			st.setCond(4);
			npc.deleteMe();
		}
		else if (event.equalsIgnoreCase("leaveinstance"))
		{
			st.setCond(5);
			st.getPlayer().getReflection().collapse();
			return null;
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
		case Rafforty:
			if (cond == 0)
			{
				QuestState qs = st.getPlayer().getQuestState(_10285_MeetingSirra.class);
				if (st.getPlayer().getLevel() >= 82 && qs != null && qs.isCompleted())
				{
					htmltext = "rafforty_q10286_01.htm";
				}
				else
				{
					htmltext = "rafforty_q10286_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1 || cond == 2 || cond == 3 || cond == 4)
			{
				htmltext = "rafforty_q10286_03.htm";
			}
			break;
		case Jinia:
			switch (cond)
			{
			case 2:
				htmltext = "jinia_q10286_01.htm";
				break;
			case 3:
				htmltext = "jinia_q10286_01a.htm";
				break;
			case 4:
				htmltext = "jinia_q10286_05.htm";
				break;
			default:
				break;
			}
			break;
		case Sirra:
			if (cond == 3)
			{
				htmltext = "sirra_q10286_01.htm";
			}
			break;
		case Jinia2:
			switch (cond)
			{
			case 5:
				htmltext = "jinia2_q10286_01.htm";
				break;
			case 6:
				htmltext = "jinia2_q10286_04.htm";
				break;
			case 7:
				htmltext = "jinia2_q10286_05.htm";
				st.addExpAndSp(2152200, 181070);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
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

	private void enterInstance(Player player, int izId)
	{
		Reflection r = player.getActiveReflection();
		if (r != null)
		{
			if (player.canReenterInstance(izId))
			{
				player.teleToLocation(r.getTeleportLoc(), r);
			}
		}
		else if (player.canEnterInstance(izId))
		{
			ReflectionUtils.enterReflection(player, izId);
		}
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