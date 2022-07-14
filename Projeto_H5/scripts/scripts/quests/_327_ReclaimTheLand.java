package quests;

import java.util.HashMap;
import java.util.Map;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Drop;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _327_ReclaimTheLand extends Quest implements ScriptFile
{
	// NPCs
	private static int Piotur = 30597;
	private static int Iris = 30034;
	private static int Asha = 30313;
	// Quest Items
	private static int TUREK_DOGTAG = 1846;
	private static int TUREK_MEDALLION = 1847;
	private static int CLAY_URN_FRAGMENT = 1848;
	private static int BRASS_TRINKET_PIECE = 1849;
	private static int BRONZE_MIRROR_PIECE = 1850;
	private static int JADE_NECKLACE_BEAD = 1851;
	private static int ANCIENT_CLAY_URN = 1852;
	private static int ANCIENT_BRASS_TIARA = 1853;
	private static int ANCIENT_BRONZE_MIRROR = 1854;
	private static int ANCIENT_JADE_NECKLACE = 1855;
	// Chances
	private static int Exchange_Chance = 80;

	private static Map<Integer, Drop> DROPLIST = new HashMap<Integer, Drop>();
	private static Map<Integer, Integer> EXP = new HashMap<Integer, Integer>();

	public _327_ReclaimTheLand()
	{
		super(false);
		addStartNpc(Piotur);
		addTalkId(Iris);
		addTalkId(Asha);

		DROPLIST.put(20495, new Drop(1, 0xFFFF, 13).addItem(TUREK_MEDALLION));
		DROPLIST.put(20496, new Drop(1, 0xFFFF, 9).addItem(TUREK_DOGTAG));
		DROPLIST.put(20497, new Drop(1, 0xFFFF, 11).addItem(TUREK_MEDALLION));
		DROPLIST.put(20498, new Drop(1, 0xFFFF, 10).addItem(TUREK_DOGTAG));
		DROPLIST.put(20499, new Drop(1, 0xFFFF, 8).addItem(TUREK_DOGTAG));
		DROPLIST.put(20500, new Drop(1, 0xFFFF, 7).addItem(TUREK_DOGTAG));
		DROPLIST.put(20501, new Drop(1, 0xFFFF, 12).addItem(TUREK_MEDALLION));
		EXP.put(ANCIENT_CLAY_URN, 913);
		EXP.put(ANCIENT_BRASS_TIARA, 1065);
		EXP.put(ANCIENT_BRONZE_MIRROR, 1065);
		EXP.put(ANCIENT_JADE_NECKLACE, 1294);

		for (int kill_id : DROPLIST.keySet())
		{
			addKillId(kill_id);
		}

		addQuestItem(TUREK_MEDALLION);
		addQuestItem(TUREK_DOGTAG);
	}

	private static boolean ExpReward(QuestState st, int item_id)
	{
		Integer exp = EXP.get(item_id);
		if (exp == null)
		{
			exp = 182;
		}
		long exp_reward = st.getQuestItemsCount(item_id * exp);
		if (exp_reward == 0)
		{
			return false;
		}
		st.takeItems(item_id, -1);
		st.addExpAndSp(exp_reward, 0);
		st.playSound(SOUND_MIDDLE);
		return true;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		if (event.equalsIgnoreCase("piotur_q0327_03.htm") && _state == CREATED)
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("piotur_q0327_06.htm") && _state == STARTED)
		{
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
		}
		else if (event.equalsIgnoreCase("trader_acellopy_q0327_02.htm") && _state == STARTED && st.getQuestItemsCount(CLAY_URN_FRAGMENT) >= 5)
		{
			st.takeItems(CLAY_URN_FRAGMENT, 5);
			if (!Rnd.chance(Exchange_Chance))
			{
				return "trader_acellopy_q0327_10.htm";
			}
			st.giveItems(ANCIENT_CLAY_URN, 1);
			st.playSound(SOUND_MIDDLE);
			return "trader_acellopy_q0327_03.htm";
		}
		else if (event.equalsIgnoreCase("trader_acellopy_q0327_04.htm") && _state == STARTED && st.getQuestItemsCount(BRASS_TRINKET_PIECE) >= 5)
		{
			st.takeItems(BRASS_TRINKET_PIECE, 5);
			if (!Rnd.chance(Exchange_Chance))
			{
				return "trader_acellopy_q0327_10.htm";
			}
			st.giveItems(ANCIENT_BRASS_TIARA, 1);
			st.playSound(SOUND_MIDDLE);
			return "trader_acellopy_q0327_05.htm";
		}
		else if (event.equalsIgnoreCase("trader_acellopy_q0327_06.htm") && _state == STARTED && st.getQuestItemsCount(BRONZE_MIRROR_PIECE) >= 5)
		{
			st.takeItems(BRONZE_MIRROR_PIECE, 5);
			if (!Rnd.chance(Exchange_Chance))
			{
				return "trader_acellopy_q0327_10.htm";
			}
			st.giveItems(ANCIENT_BRONZE_MIRROR, 1);
			st.playSound(SOUND_MIDDLE);
			return "trader_acellopy_q0327_07.htm";
		}
		else if (event.equalsIgnoreCase("trader_acellopy_q0327_08.htm") && _state == STARTED && st.getQuestItemsCount(JADE_NECKLACE_BEAD) >= 5)
		{
			st.takeItems(JADE_NECKLACE_BEAD, 5);
			if (!Rnd.chance(Exchange_Chance))
			{
				return "trader_acellopy_q0327_09.htm";
			}
			st.giveItems(ANCIENT_JADE_NECKLACE, 1);
			st.playSound(SOUND_MIDDLE);
			return "trader_acellopy_q0327_07.htm";
		}
		else if (event.equalsIgnoreCase("iris_q0327_03.htm") && _state == STARTED)
		{
			if (!ExpReward(st, CLAY_URN_FRAGMENT))
			{
				return "iris_q0327_02.htm";
			}
		}
		else if (event.equalsIgnoreCase("iris_q0327_04.htm") && _state == STARTED)
		{
			if (!ExpReward(st, BRASS_TRINKET_PIECE))
			{
				return "iris_q0327_02.htm";
			}
		}
		else if (event.equalsIgnoreCase("iris_q0327_05.htm") && _state == STARTED)
		{
			if (!ExpReward(st, BRONZE_MIRROR_PIECE))
			{
				return "iris_q0327_02.htm";
			}
		}
		else if (event.equalsIgnoreCase("iris_q0327_06.htm") && _state == STARTED)
		{
			if (!ExpReward(st, JADE_NECKLACE_BEAD))
			{
				return "iris_q0327_02.htm";
			}
		}
		else if (event.equalsIgnoreCase("iris_q0327_07.htm") && _state == STARTED)
		{
			if (!(ExpReward(st, ANCIENT_CLAY_URN) || ExpReward(st, ANCIENT_BRASS_TIARA) || ExpReward(st, ANCIENT_BRONZE_MIRROR) || ExpReward(st, ANCIENT_JADE_NECKLACE)))
			{
				return "iris_q0327_02.htm";
			}
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
			if (npcId != Piotur)
			{
				return "noquest";
			}
			if (st.getPlayer().getLevel() < 25)
			{
				st.exitCurrentQuest(true);
				return "piotur_q0327_01.htm";
			}
			st.setCond(0);
			return "piotur_q0327_02.htm";
		}

		if (_state != STARTED)
		{
			return "noquest";
		}

		if (npcId == Piotur)
		{
			long reward = st.getQuestItemsCount(TUREK_DOGTAG) * 40 + st.getQuestItemsCount(TUREK_MEDALLION) * 50;
			if (reward == 0)
			{
				return "piotur_q0327_04.htm";
			}
			st.takeItems(TUREK_DOGTAG, -1);
			st.takeItems(TUREK_MEDALLION, -1);
			st.giveItems(ADENA_ID, reward);
			st.playSound(SOUND_MIDDLE);
			return "piotur_q0327_05.htm";
		}
		if (npcId == Iris)
		{
			return "iris_q0327_01.htm";
		}
		if (npcId == Asha)
		{
			return "trader_acellopy_q0327_01.htm";
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

		Drop _drop = DROPLIST.get(npcId);
		if (_drop == null)
		{
			return null;
		}

		if (Rnd.chance(_drop.chance))
		{
			int n = Rnd.get(100);
			if (n < 25)
			{
				qs.giveItems(CLAY_URN_FRAGMENT, 1);
			}
			else if (n < 50)
			{
				qs.giveItems(BRASS_TRINKET_PIECE, 1);
			}
			else if (n < 75)
			{
				qs.giveItems(BRONZE_MIRROR_PIECE, 1);
			}
			else
			{
				qs.giveItems(JADE_NECKLACE_BEAD, 1);
			}
		}
		qs.giveItems(_drop.itemList[0], 1);
		qs.playSound(SOUND_ITEMGET);

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