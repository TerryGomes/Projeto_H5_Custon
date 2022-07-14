package quests;

import java.util.StringTokenizer;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public class _10287_StoryOfThoseLeft extends Quest implements ScriptFile
{
	private static final int Rafforty = 32020;
	private static final int Jinia = 32760;
	private static final int Jinia2 = 32781;
	private static final int Kegor = 32761;

	public _10287_StoryOfThoseLeft()
	{
		super(false);
		addStartNpc(Rafforty);
		addTalkId(Jinia, Jinia2, Kegor);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("rafforty_q10287_02.htm"))
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
		else if (event.equalsIgnoreCase("jinia_q10287_03.htm"))
		{
			st.setCond(3);
		}
		else if (event.equalsIgnoreCase("kegor_q10287_03.htm"))
		{
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("exitinstance"))
		{
			st.setCond(5);
			st.getPlayer().getReflection().collapse();
			return null;
		}
		else if (event.startsWith("exgivebook"))
		{
			StringTokenizer str = new StringTokenizer(event);
			str.nextToken();
			int id = Integer.parseInt(str.nextToken());
			htmltext = "rafforty_q10287_05.htm";
			switch (id)
			{
			case 1:
				st.giveItems(10549, 1);
				break;
			case 2:
				st.giveItems(10550, 1);
				break;
			case 3:
				st.giveItems(10551, 1);
				break;
			case 4:
				st.giveItems(10552, 1);
				break;
			case 5:
				st.giveItems(10553, 1);
				break;
			case 6:
				st.giveItems(14219, 1);
				break;
			}
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
		int cond = st.getCond();
		switch (npcId)
		{
		case Rafforty:
			if (cond == 0)
			{
				QuestState qs = st.getPlayer().getQuestState(_10286_ReunionWithSirra.class);
				if (st.getPlayer().getLevel() >= 82 && qs != null && qs.isCompleted())
				{
					htmltext = "rafforty_q10287_01.htm";
				}
				else
				{
					htmltext = "rafforty_q10287_00.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond >= 1 && cond < 5)
			{
				htmltext = "rafforty_q10287_02.htm";
			}
			else if (cond == 5)
			{
				htmltext = "rafforty_q10287_03.htm";
			}
			else
			{
				htmltext = "rafforty_q10287_06.htm";
			}
			break;
		case Jinia:
			switch (cond)
			{
			case 2:
				htmltext = "jinia_q10287_01.htm";
				break;
			case 3:
				htmltext = "jinia_q10287_04.htm";
				break;
			case 4:
				htmltext = "jinia_q10287_05.htm";
				break;
			default:
				break;
			}
			break;
		case Kegor:
			if (cond == 3)
			{
				htmltext = "kegor_q10287_01.htm";
			}
			else if (cond == 2 || cond == 4)
			{
				htmltext = "kegor_q10287_04.htm";
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