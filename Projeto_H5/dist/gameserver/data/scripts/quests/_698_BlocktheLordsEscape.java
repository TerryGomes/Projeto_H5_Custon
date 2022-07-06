package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.instancemanager.SoIManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

/**
 * @author pchayka
 */

public class _698_BlocktheLordsEscape extends Quest implements ScriptFile
{
	// NPC
	private static final int TEPIOS = 32603;
	private static final int VesperNobleEnhanceStone = 14052;

	public _698_BlocktheLordsEscape()
	{
		super(PARTY_ALL);
		addStartNpc(TEPIOS);
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();

		if (npcId == TEPIOS)
		{
			if (st.getState() == CREATED)
			{
				if (player.getLevel() < 75 || player.getLevel() > 85)
				{
					st.exitCurrentQuest(true);
					return "tepios_q698_0.htm";
				}
				if (SoIManager.getCurrentStage() != 5)
				{
					st.exitCurrentQuest(true);
					return "tepios_q698_0a.htm";
				}
				return "tepios_q698_1.htm";
			}
			else if (st.getCond() == 1 && st.getInt("defenceDone") == 1)
			{
				htmltext = "tepios_q698_5.htm";
				st.giveItems(VesperNobleEnhanceStone, (int) Config.RATE_QUESTS_REWARD * Rnd.get(5, 8));
				st.playSound(SOUND_FINISH);
				st.exitCurrentQuest(true);
			}
			else
			{
				return "tepios_q698_4.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		int cond = st.getCond();

		if (event.equalsIgnoreCase("tepios_q698_3.htm"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
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