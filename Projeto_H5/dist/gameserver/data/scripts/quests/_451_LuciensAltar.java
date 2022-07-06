package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;

public class _451_LuciensAltar extends Quest implements ScriptFile
{
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

	private static int DAICHIR = 30537;

	private static int REPLENISHED_BEAD = 14877;
	private static int DISCHARGED_BEAD = 14878;

	private static int ALTAR_1 = 32706;
	private static int ALTAR_2 = 32707;
	private static int ALTAR_3 = 32708;
	private static int ALTAR_4 = 32709;
	private static int ALTAR_5 = 32710;

	private static int[] ALTARS = new int[]
	{
		ALTAR_1,
		ALTAR_2,
		ALTAR_3,
		ALTAR_4,
		ALTAR_5
	};

	public _451_LuciensAltar()
	{
		super(false);

		addStartNpc(DAICHIR);
		addTalkId(ALTARS);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("30537-03.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.giveItems(REPLENISHED_BEAD, 5);
			st.playSound(SOUND_ACCEPT);
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();

		if (npcId == DAICHIR)
		{
			switch (cond)
			{
			case 0:
				if (player.getLevel() < 80)
				{
					htmltext = "30537-00.htm";
					st.exitCurrentQuest(true);
				}
				else if (!canEnter(player))
				{
					htmltext = "30537-06.htm";
					st.exitCurrentQuest(true);
				}
				else
				{
					htmltext = "30537-01.htm";
				}
				break;
			case 1:
				htmltext = "30537-04.htm";
				break;
			case 2:
				htmltext = "30537-05.htm";
				st.giveItems(ADENA_ID, 127690);
				st.takeItems(DISCHARGED_BEAD, -1);
				st.exitCurrentQuest(true);
				st.playSound(SOUND_FINISH);
				st.getPlayer().setVar(getName(), String.valueOf(System.currentTimeMillis()), -1);
				break;
			default:
				break;
			}
		}
		else if (cond == 1 && ArrayUtils.contains(ALTARS, npcId))
		{
			if (npcId == ALTAR_1 && st.getInt("Altar1") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar1", 1);
			}
			else if (npcId == ALTAR_2 && st.getInt("Altar2") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar2", 1);
			}
			else if (npcId == ALTAR_3 && st.getInt("Altar3") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar3", 1);
			}
			else if (npcId == ALTAR_4 && st.getInt("Altar4") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar4", 1);
			}
			else if (npcId == ALTAR_5 && st.getInt("Altar5") < 1)
			{
				htmltext = "recharge.htm";
				onAltarCheck(st);
				st.set("Altar5", 1);
			}
			else
			{
				htmltext = "findother.htm";
			}
		}
		return htmltext;
	}

	private void onAltarCheck(QuestState st)
	{
		st.takeItems(REPLENISHED_BEAD, 1);
		st.giveItems(DISCHARGED_BEAD, 1);
		st.playSound(SOUND_ITEMGET);
		if (st.getQuestItemsCount(DISCHARGED_BEAD) >= 5)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
	}

	private boolean canEnter(Player player)
	{
		if (player.isGM())
		{
			return true;
		}
		String var = player.getVar(getName());
		if (var == null)
		{
			return true;
		}
		return Long.parseLong(var) - System.currentTimeMillis() > 24 * 60 * 60 * 1000;
	}
}