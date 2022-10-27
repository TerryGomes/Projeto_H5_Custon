package quests;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _357_WarehouseKeepersAmbition extends Quest implements ScriptFile
{

	// CUSTOM VALUES
	private static final int DROPRATE = 50;
	private static final int REWARD1 = 900;// This is paid per item
	private static final int REWARD2 = 10000;// #Extra reward, if > 100

	// NPC
	private static final int SILVA = 30686;
	// Mobs
	private static final int MOB1 = 20594;
	private static final int MOB2 = 20595;
	private static final int MOB3 = 20596;
	private static final int MOB4 = 20597;
	private static final int MOB5 = 20598;

	// ITEMS
	private static final int JADE_CRYSTAL = 5867;

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

	public _357_WarehouseKeepersAmbition()
	{
		super(false);
		addStartNpc(SILVA);

		addKillId(MOB1);
		addKillId(MOB2);
		addKillId(MOB3);
		addKillId(MOB4);
		addKillId(MOB5);

		addQuestItem(JADE_CRYSTAL);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if (event.equalsIgnoreCase("warehouse_keeper_silva_q0357_04.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("warehouse_keeper_silva_q0357_08.htm"))
		{
			long count = st.getQuestItemsCount(JADE_CRYSTAL);
			if (count > 0)
			{
				long reward = count * REWARD1;
				if (count >= 100)
				{
					reward = reward + REWARD2;
				}
				st.takeItems(JADE_CRYSTAL, -1);
				st.giveItems(ADENA_ID, reward);
			}
			else
			{
				htmltext = "warehouse_keeper_silva_q0357_06.htm";
			}
		}
		else if (event.equalsIgnoreCase("warehouse_keeper_silva_q0357_11.htm"))
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int id = st.getState();
		int cond = st.getCond();
		long jade = st.getQuestItemsCount(JADE_CRYSTAL);
		if (cond == 0 || id == CREATED)
		{
			if (st.getPlayer().getLevel() >= 47)
			{
				htmltext = "warehouse_keeper_silva_q0357_02.htm";
			}
			else
			{
				htmltext = "warehouse_keeper_silva_q0357_01.htm";
				st.exitCurrentQuest(true);
			}
		}
		else if (jade == 0)
		{
			htmltext = "warehouse_keeper_silva_q0357_06.htm";
		}
		else if (jade > 0)
		{
			htmltext = "warehouse_keeper_silva_q0357_07.htm";
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (Rnd.chance(DROPRATE))
		{
			st.giveItems(JADE_CRYSTAL, 1);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}