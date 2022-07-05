package quests;

import java.util.HashMap;
import java.util.Map;

import l2f.commons.util.Rnd;
import l2f.gameserver.data.xml.holder.MultiSellHolder;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _382_KailsMagicCoin extends Quest implements ScriptFile
{
	// Quest items
	private static int ROYAL_MEMBERSHIP = 5898;
	// NPCs
	private static int VERGARA = 30687;
	// MOBs and CHANCES
	private static final Map<Integer, int[]> MOBS = new HashMap<Integer, int[]>();

	static
	{
		MOBS.put(21017, new int[]
		{
			5961
		}); // Fallen Orc
		MOBS.put(21019, new int[]
		{
			5962
		}); // Fallen Orc Archer
		MOBS.put(21020, new int[]
		{
			5963
		}); // Fallen Orc Shaman
		MOBS.put(21022, new int[]
		{
			5961,
			5962,
			5963
		}); // Fallen Orc Captain
		// MOBS.put(21258, new int[] { 5961, 5962, 5963 }); // Fallen Orc Shaman - WereTiger
		// MOBS.put(21259, new int[] { 5961, 5962, 5963 }); // Fallen Orc Shaman - WereTiger, transformed
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

	public _382_KailsMagicCoin()
	{
		super(false);

		addStartNpc(VERGARA);

		for (int mobId : MOBS.keySet())
		{
			addKillId(mobId);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("head_blacksmith_vergara_q0382_03.htm"))
		{
			if (st.getPlayer().getLevel() >= 55 && st.getQuestItemsCount(ROYAL_MEMBERSHIP) > 0)
			{
				st.setCond(1);
				st.setState(STARTED);
				st.playSound(SOUND_ACCEPT);
			}
			else
			{
				htmltext = "head_blacksmith_vergara_q0382_01.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("list"))
		{
			MultiSellHolder.getInstance().SeparateAndSend(382, st.getPlayer(), 0);
			htmltext = null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int cond = st.getCond();
		if (st.getQuestItemsCount(ROYAL_MEMBERSHIP) == 0 || st.getPlayer().getLevel() < 55)
		{
			htmltext = "head_blacksmith_vergara_q0382_01.htm";
			st.exitCurrentQuest(true);
		}
		else if (cond == 0)
		{
			htmltext = "head_blacksmith_vergara_q0382_02.htm";
		}
		else
		{
			htmltext = "head_blacksmith_vergara_q0382_04.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getState() != STARTED || st.getQuestItemsCount(ROYAL_MEMBERSHIP) == 0)
		{
			return null;
		}

		int[] droplist = MOBS.get(npc.getNpcId());
		st.rollAndGive(droplist[Rnd.get(droplist.length)], 1, 10);
		return null;
	}
}