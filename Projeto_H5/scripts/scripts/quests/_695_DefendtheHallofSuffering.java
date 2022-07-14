package quests;

import l2mv.gameserver.instancemanager.SoIManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _695_DefendtheHallofSuffering extends Quest implements ScriptFile
{
	private static final int TEPIOS = 32603;

	public _695_DefendtheHallofSuffering()
	{
		super(PARTY_ALL);
		addStartNpc(TEPIOS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("tepios_q695_3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		int cond = st.getCond();
		if (npcId == TEPIOS)
		{
			if (cond == 0)
			{
				if (player.getLevel() >= 75 && player.getLevel() <= 85)
				{
					if (SoIManager.getCurrentStage() == 4)
					{
						htmltext = "tepios_q695_1.htm";
					}
					else
					{
						htmltext = "tepios_q695_0a.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "tepios_q695_0.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == 1)
			{
				htmltext = "tepios_q695_4.htm";
			}
		}

		return htmltext;
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