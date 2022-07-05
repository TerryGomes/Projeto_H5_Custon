package quests;

// Created by Artful

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _659_IdRatherBeCollectingFairyBreath extends Quest implements ScriptFile
{
	// NPC
	public final int GALATEA = 30634;
	// Mobs
	public final int[] MOBS =
	{
		20078,
		21026,
		21025,
		21024,
		21023
	};
	// Quest Item
	public final int FAIRY_BREATH = 8286;

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

	public _659_IdRatherBeCollectingFairyBreath()
	{
		super(false);

		addStartNpc(GALATEA);
		addTalkId(GALATEA);
		addTalkId(GALATEA);
		addTalkId(GALATEA);

		for (int i : MOBS)
		{
			addKillId(i);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("high_summoner_galatea_q0659_0103.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("high_summoner_galatea_q0659_0203.htm"))
		{
			long count = st.getQuestItemsCount(FAIRY_BREATH);
			if (count > 0)
			{
				long reward = 0;
				if (count < 10)
				{
					reward = count * 50;
				}
				else
				{
					reward = count * 50 + 5365;
				}
				st.takeItems(FAIRY_BREATH, -1);
				st.giveItems(ADENA_ID, reward);
			}
		}
		else if (event.equalsIgnoreCase("high_summoner_galatea_q0659_0204.htm"))
		{
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int id = st.getState();
		int cond = 0;
		if (id != CREATED)
		{
			cond = st.getCond();
		}
		if (npcId == GALATEA)
		{
			if (st.getPlayer().getLevel() < 26)
			{
				htmltext = "high_summoner_galatea_q0659_0102.htm";
				st.exitCurrentQuest(true);
			}
			else if (cond == 0)
			{
				htmltext = "high_summoner_galatea_q0659_0101.htm";
			}
			else if (cond == 1)
			{
				if (st.getQuestItemsCount(FAIRY_BREATH) == 0)
				{
					htmltext = "high_summoner_galatea_q0659_0105.htm";
				}
				else
				{
					htmltext = "high_summoner_galatea_q0659_0105.htm";
				}
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == 1)
		{
			for (int i : MOBS)
			{
				if (npcId == i && Rnd.chance(30))
				{
					st.giveItems(FAIRY_BREATH, 1);
					st.playSound(SOUND_ITEMGET);
				}
			}
		}
		return null;
	}
}