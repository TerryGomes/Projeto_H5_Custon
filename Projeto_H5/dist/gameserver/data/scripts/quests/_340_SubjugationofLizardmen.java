package quests;

import java.util.HashMap;
import java.util.Map;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Drop;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _340_SubjugationofLizardmen extends Quest implements ScriptFile
{
	// NPCs
	private static int WEITSZ = 30385;
	private static int LEVIAN = 30037;
	private static int ADONIUS = 30375;
	private static int CHEST_OF_BIFRONS = 30989;
	// Mobs
	private static int LANGK_LIZARDMAN = 20008;
	private static int LANGK_LIZARDMAN_SCOUT = 20010;
	private static int LANGK_LIZARDMAN_WARRIOR = 20014;
	private static int LANGK_LIZARDMAN_SHAMAN = 21101;
	private static int LANGK_LIZARDMAN_LEADER = 20356;
	private static int LANGK_LIZARDMAN_SENTINEL = 21100;
	private static int LANGK_LIZARDMAN_LIEUTENANT = 20357;
	private static int SERPENT_DEMON_BIFRONS = 25146;
	// Quest Items (Drop)
	private static int ROSARY = 4257;
	private static int HOLY_SYMBOL = 4256;
	private static int TRADE_CARGO = 4255;
	private static int EVIL_SPIRIT_OF_DARKNESS = 7190;

	private static Map<Integer, Drop> DROPLIST = new HashMap<Integer, Drop>();

	public _340_SubjugationofLizardmen()
	{
		super(false);
		addStartNpc(WEITSZ);
		addTalkId(LEVIAN);
		addTalkId(ADONIUS);
		addTalkId(CHEST_OF_BIFRONS);

		DROPLIST.put(LANGK_LIZARDMAN, new Drop(1, 30, 30).addItem(TRADE_CARGO));
		DROPLIST.put(LANGK_LIZARDMAN_SCOUT, new Drop(1, 30, 33).addItem(TRADE_CARGO));
		DROPLIST.put(LANGK_LIZARDMAN_WARRIOR, new Drop(1, 30, 36).addItem(TRADE_CARGO));
		DROPLIST.put(LANGK_LIZARDMAN_SHAMAN, new Drop(3, 1, 12).addItem(HOLY_SYMBOL).addItem(ROSARY));
		DROPLIST.put(LANGK_LIZARDMAN_LEADER, new Drop(3, 1, 12).addItem(HOLY_SYMBOL).addItem(ROSARY));
		DROPLIST.put(LANGK_LIZARDMAN_SENTINEL, new Drop(3, 1, 12).addItem(HOLY_SYMBOL).addItem(ROSARY));
		DROPLIST.put(LANGK_LIZARDMAN_LIEUTENANT, new Drop(3, 1, 12).addItem(HOLY_SYMBOL).addItem(ROSARY));

		addKillId(SERPENT_DEMON_BIFRONS);
		for (int kill_id : DROPLIST.keySet())
		{
			addKillId(kill_id);
		}

		addQuestItem(TRADE_CARGO);
		addQuestItem(HOLY_SYMBOL);
		addQuestItem(ROSARY);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		int cond = st.getCond();
		if (event.equalsIgnoreCase("30385-4.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30385-6.htm") && _state == STARTED && cond == 1 && st.getQuestItemsCount(TRADE_CARGO) >= 30)
		{
			st.setCond(2);
			st.takeItems(TRADE_CARGO, -1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30375-2.htm") && _state == STARTED && cond == 2)
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30989-2.htm") && _state == STARTED && cond == 5)
		{
			st.setCond(6);
			st.giveItems(EVIL_SPIRIT_OF_DARKNESS, 1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30037-4.htm") && _state == STARTED && cond == 6 && st.getQuestItemsCount(EVIL_SPIRIT_OF_DARKNESS) > 0)
		{
			st.setCond(7);
			st.takeItems(EVIL_SPIRIT_OF_DARKNESS, -1);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30385-10.htm") && _state == STARTED && cond == 7)
		{
			st.giveItems(ADENA_ID, 14700);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}
		else if (event.equalsIgnoreCase("30385-7.htm") && _state == STARTED && cond == 1 && st.getQuestItemsCount(TRADE_CARGO) >= 30)
		{
			st.takeItems(TRADE_CARGO, -1);
			st.giveItems(ADENA_ID, 4090);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(false);
		}

		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int _state = st.getState();
		int npcId = npc.getNpcId();
		if (_state == CREATED)
		{
			if (npcId != WEITSZ)
			{
				return "noquest";
			}
			if (st.getPlayer().getLevel() < 17)
			{
				st.exitCurrentQuest(true);
				return "30385-1.htm";
			}
			st.setCond(0);
			return "30385-2.htm";
		}

		if (_state != STARTED)
		{
			return "noquest";
		}
		int cond = st.getCond();

		if (npcId == WEITSZ && cond == 1)
		{
			return st.getQuestItemsCount(TRADE_CARGO) < 30 ? "30385-8.htm" : "30385-5.htm";
		}
		if (npcId == WEITSZ && cond == 2)
		{
			return "30385-11.htm";
		}
		if (npcId == WEITSZ && cond == 7)
		{
			return "30385-9.htm";
		}
		if (npcId == ADONIUS && cond == 2)
		{
			return "30375-1.htm";
		}
		if (npcId == ADONIUS && cond == 3)
		{
			if (st.getQuestItemsCount(ROSARY) == 0 || st.getQuestItemsCount(HOLY_SYMBOL) == 0)
			{
				return "30375-4.htm";
			}
			st.takeItems(ROSARY, -1);
			st.takeItems(HOLY_SYMBOL, -1);
			st.playSound(SOUND_MIDDLE);
			st.setCond(4);
			return "30375-3.htm";
		}
		if (npcId == ADONIUS && cond == 4)
		{
			return "30375-5.htm";
		}
		if (npcId == LEVIAN && cond == 4)
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
			return "30037-1.htm";
		}
		if (npcId == LEVIAN && cond == 5)
		{
			return "30037-2.htm";
		}
		if (npcId == LEVIAN && cond == 6 && st.getQuestItemsCount(EVIL_SPIRIT_OF_DARKNESS) > 0)
		{
			return "30037-3.htm";
		}
		if (npcId == LEVIAN && cond == 7)
		{
			return "30037-5.htm";
		}
		if (npcId == CHEST_OF_BIFRONS && cond == 5)
		{
			return "30989-1.htm";
		}

		return "noquest";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState qs)
	{
		if (qs.getState() != STARTED)
		{
			return null;
		}
		int npcId = npc.getNpcId();
		if (npcId == SERPENT_DEMON_BIFRONS)
		{
			qs.addSpawn(CHEST_OF_BIFRONS);
			return null;
		}

		Drop _drop = DROPLIST.get(npcId);
		if (_drop == null)
		{
			return null;
		}
		int cond = qs.getCond();

		for (int item_id : _drop.itemList)
		{
			long _count = qs.getQuestItemsCount(item_id);
			if (cond == _drop.condition && _count < _drop.maxcount && Rnd.chance(_drop.chance))
			{
				qs.giveItems(item_id, 1);
				if (_count + 1 == _drop.maxcount)
				{
					qs.playSound(SOUND_MIDDLE);
				}
				else
				{
					qs.playSound(SOUND_ITEMGET);
				}
			}
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