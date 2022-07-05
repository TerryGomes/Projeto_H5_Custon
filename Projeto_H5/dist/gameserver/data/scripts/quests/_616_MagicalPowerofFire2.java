package quests;

import l2f.commons.util.Rnd;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;

public class _616_MagicalPowerofFire2 extends Quest implements ScriptFile
{
	// NPC
	private static final int KETRAS_HOLY_ALTAR = 31558;
	private static final int UDAN = 31379;

	// Quest items
	private static final int FIRE_HEART_OF_NASTRON = 7244;
	private static final int RED_TOTEM = 7243;

	private static int Reward_First = 4589;
	private static int Reward_Last = 4594;

	// private final int[] VARKA_NPC_LIST = new int[20];

	private static final int SoulOfFireNastron = 25306;
	private NpcInstance SoulOfFireNastronSpawn = null;

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

	public _616_MagicalPowerofFire2()
	{
		super(true);

		addStartNpc(UDAN);

		addTalkId(KETRAS_HOLY_ALTAR);

		/*
		 * VARKA_NPC_LIST[0] = 21350;
		 * VARKA_NPC_LIST[1] = 21351;
		 * VARKA_NPC_LIST[2] = 21353;
		 * VARKA_NPC_LIST[3] = 21354;
		 * VARKA_NPC_LIST[4] = 21355;
		 * VARKA_NPC_LIST[5] = 21357;
		 * VARKA_NPC_LIST[6] = 21358;
		 * VARKA_NPC_LIST[7] = 21360;
		 * VARKA_NPC_LIST[8] = 21361;
		 * VARKA_NPC_LIST[9] = 21362;
		 * VARKA_NPC_LIST[10] = 21364;
		 * VARKA_NPC_LIST[11] = 21365;
		 * VARKA_NPC_LIST[12] = 21366;
		 * VARKA_NPC_LIST[13] = 21368;
		 * VARKA_NPC_LIST[14] = 21369;
		 * VARKA_NPC_LIST[15] = 21370;
		 * VARKA_NPC_LIST[16] = 21371;
		 * VARKA_NPC_LIST[17] = 21372;
		 * VARKA_NPC_LIST[18] = 21373;
		 * VARKA_NPC_LIST[19] = 21374;
		 * for (int npcId : VARKA_NPC_LIST)
		 * addKillId(npcId);
		 */

		addKillId(SoulOfFireNastron);
		addQuestItem(FIRE_HEART_OF_NASTRON);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfFireNastron);
		String htmltext = event;
		if (event.equalsIgnoreCase("quest_accept"))
		{
			htmltext = "shaman_udan_q0616_0104.htm";
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("616_1"))
		{
			if (ServerVariables.getLong(_616_MagicalPowerofFire2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
			{
				htmltext = "totem_of_ketra_q0616_0204.htm";
			}
			else if (st.getQuestItemsCount(RED_TOTEM) >= 1 && isQuest == null)
			{
				st.takeItems(RED_TOTEM, 1);
				SoulOfFireNastronSpawn = st.addSpawn(SoulOfFireNastron, 142528, -82528, -6496);
				SoulOfFireNastronSpawn.addListener(new DeathListener());
				st.playSound(SOUND_MIDDLE);
			}
			else
			{
				htmltext = "totem_of_ketra_q0616_0203.htm";
			}
		}
		else if (event.equalsIgnoreCase("616_3"))
		{
			if (st.getQuestItemsCount(FIRE_HEART_OF_NASTRON) >= 1)
			{
				st.takeItems(FIRE_HEART_OF_NASTRON, -1);
				st.giveItems(Rnd.get(Reward_First, Reward_Last), 5, true);
				st.playSound(SOUND_FINISH);
				htmltext = "shaman_udan_q0616_0301.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "shaman_udan_q0616_0302.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfFireNastron);
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		switch (npcId)
		{
		case UDAN:
			switch (cond)
			{
			case 0:
				if (st.getPlayer().getLevel() >= 75)
				{
					if (st.getQuestItemsCount(RED_TOTEM) >= 1)
					{
						htmltext = "shaman_udan_q0616_0101.htm";
					}
					else
					{
						htmltext = "shaman_udan_q0616_0102.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				{
					htmltext = "shaman_udan_q0616_0103.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "shaman_udan_q0616_0105.htm";
				break;
			case 2:
				htmltext = "shaman_udan_q0616_0202.htm";
				break;
			default:
				if (cond == 3 && st.getQuestItemsCount(FIRE_HEART_OF_NASTRON) >= 1)
				{
					htmltext = "shaman_udan_q0616_0201.htm";
				}
				break;
			}
			break;
		case KETRAS_HOLY_ALTAR:
			if (ServerVariables.getLong(_616_MagicalPowerofFire2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
			{
				htmltext = "totem_of_ketra_q0616_0204.htm";
			}
			else if (npc.isBusy())
			{
				htmltext = "totem_of_ketra_q0616_0202.htm";
			}
			else if (cond == 1)
			{
				htmltext = "totem_of_ketra_q0616_0101.htm";
			}
			else if (cond == 2)
			{
				if (isQuest == null)
				{
					SoulOfFireNastronSpawn = st.addSpawn(SoulOfFireNastron, 142528, -82528, -6496);
					SoulOfFireNastronSpawn.addListener(new DeathListener());
					htmltext = "totem_of_ketra_q0616_0204.htm";
				}
				else
				{
					htmltext = "<html><body>Already in spawn.</body></html>";
				}
			}
			break;
		}
		return htmltext;
	}

	private static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			ServerVariables.set(_616_MagicalPowerofFire2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
		}
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		if (st.getQuestItemsCount(FIRE_HEART_OF_NASTRON) == 0)
		{
			st.giveItems(FIRE_HEART_OF_NASTRON, 1);
			st.setCond(3);

			if (SoulOfFireNastronSpawn != null)
			{
				SoulOfFireNastronSpawn.deleteMe();
			}
			SoulOfFireNastronSpawn = null;
		}
		return null;
	}
}