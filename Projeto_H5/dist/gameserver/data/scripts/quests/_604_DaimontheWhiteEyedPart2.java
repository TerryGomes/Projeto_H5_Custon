package quests;

import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

public class _604_DaimontheWhiteEyedPart2 extends Quest implements ScriptFile
{
	// NPC
	private static final int EYE = 31683;
	private static final int ALTAR = 31541;
	// MOBS
	private static final int DAIMON = 25290;
	// ITEMS
	private static final int U_SUMMON = 7192;
	private static final int S_SUMMON = 7193;
	private static final int ESSENCE = 7194;

	// REWARDS dye +2int-2men/+2int-2wit/+2men-2int/+2men-2wit/+2wit-2int/+2wit-2men
	private static final int INT_MEN = 4595;
	private static final int INT_WIT = 4596;
	private static final int MEN_INT = 4597;
	private static final int MEN_WIT = 4598;
	private static final int WIT_INT = 4599;
	private static final int WIT_MEN = 4600;

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

	public _604_DaimontheWhiteEyedPart2()
	{
		super(true);

		addStartNpc(EYE);
		addTalkId(ALTAR);
		addKillId(DAIMON);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		NpcInstance isQuest = GameObjectsStorage.getByNpcId(DAIMON);
		if (event.equalsIgnoreCase("31683-02.htm"))
		{
			if (st.getPlayer().getLevel() < 73)
			{
				st.exitCurrentQuest(true);
				return "31683-00b.htm";
			}
			st.setCond(1);
			st.setState(STARTED);
			st.takeItems(U_SUMMON, 1);
			st.giveItems(S_SUMMON, 1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31541-02.htm"))
		{
			if (st.getQuestItemsCount(S_SUMMON) == 0)
			{
				return "31541-04.htm";
			}
			if (isQuest != null)
			{
				return "31541-03.htm";
			}
			if (ServerVariables.getLong(_604_DaimontheWhiteEyedPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
			{
				return "31541-05.htm";
			}
			st.takeItems(S_SUMMON, 1);
			isQuest = st.addSpawn(DAIMON, 186320, -43904, -3175);
			Functions.npcSay(isQuest, "Who called me?");
			isQuest.addListener(new DeathListener());
			st.playSound(SOUND_MIDDLE);
			st.setCond(2);
			st.setState(STARTED);
			st.getPlayer().sendMessage("Daimon the White-Eyed has spawned in 186320, -43904, -3175");
			st.startQuestTimer("DAIMON_Fail", 12000000);
		}
		else if (event.equalsIgnoreCase("31683-04.htm"))
		{
			if (st.getQuestItemsCount(ESSENCE) >= 1)
			{
				return "list.htm";
			}
			st.exitCurrentQuest(true);
			return "31683-05.htm";
		}
		else if (event.equalsIgnoreCase("INT_MEN"))
		{
			st.giveItems(INT_MEN, 5, true);
			st.takeItems(ESSENCE, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return null;
		}
		else if (event.equalsIgnoreCase("INT_WIT"))
		{
			st.giveItems(INT_WIT, 5, true);
			st.takeItems(ESSENCE, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return null;
		}
		else if (event.equalsIgnoreCase("MEN_INT"))
		{
			st.giveItems(MEN_INT, 5, true);
			st.takeItems(ESSENCE, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return null;
		}
		else if (event.equalsIgnoreCase("MEN_WIT"))
		{
			st.giveItems(MEN_WIT, 5, true);
			st.takeItems(ESSENCE, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return null;
		}
		else if (event.equalsIgnoreCase("WIT_INT"))
		{
			st.giveItems(WIT_INT, 5, true);
			st.takeItems(ESSENCE, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return null;
		}
		else if (event.equalsIgnoreCase("WIT_MEN"))
		{
			st.giveItems(WIT_MEN, 5, true);
			st.takeItems(ESSENCE, 1);
			st.playSound(SOUND_FINISH);
			st.exitCurrentQuest(true);
			return null;
		}
		else if (event.equalsIgnoreCase("DAIMON_Fail") && isQuest != null)
		{
			Functions.npcSay(isQuest, "Darkness could not have ray?");
			isQuest.deleteMe();
			return null;
		}
		return event;
	}

	private static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			ServerVariables.set(_604_DaimontheWhiteEyedPart2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
		}
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		NpcInstance isQuest = GameObjectsStorage.getByNpcId(DAIMON);
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (cond)
		{
		case 0:
			if (npcId == EYE)
			{
				if (st.getQuestItemsCount(U_SUMMON) >= 1)
				{
					htmltext = "31683-01.htm";
				}
				else
				{
					htmltext = "31683-00a.htm";
				}
			}
			break;
		case 1:
			if (npcId == EYE)
			{
				htmltext = "31683-02a.htm";
			}
			else if (npcId == ALTAR)
			{
				if (ServerVariables.getLong(_604_DaimontheWhiteEyedPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
				{
					htmltext = "31541-05.htm";
				}
				else
				{
					htmltext = "31541-01.htm";
				}
			}
			break;
		case 2:
			if (npcId == ALTAR)
			{
				if (isQuest != null)
				{
					htmltext = "31541-03.htm";
				}
				else if (ServerVariables.getLong(_604_DaimontheWhiteEyedPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
				{
					htmltext = "31541-05.htm";
				}
				else
				{
					isQuest = st.addSpawn(DAIMON, 186320, -43904, -3175);
					Functions.npcSay(isQuest, "Who called me?");
					st.playSound(SOUND_MIDDLE);
					st.setState(STARTED);
					st.getPlayer().sendMessage("Daimon the White-Eyed has spawned in 186320, -43904, -3175");
					isQuest.addListener(new DeathListener());
					st.startQuestTimer("DAIMON_Fail", 12000000);
				}
			}
			break;
		case 3:
			if (npcId == EYE)
			{
				if (st.getQuestItemsCount(ESSENCE) >= 1)
				{
					htmltext = "31683-03.htm";
				}
				else
				{
					htmltext = "31683-06.htm";
				}
			}
			if (npcId == ALTAR)
			{
				htmltext = "31541-05.htm";
			}
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getQuestItemsCount(S_SUMMON) > 0)
		{
			st.takeItems(S_SUMMON, 1);
			st.giveItems(ESSENCE, 1);
			st.setCond(3);
			st.setState(STARTED);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}
}