package quests;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.instancemanager.ServerVariables;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class _625_TheFinestIngredientsPart2 extends Quest implements ScriptFile
{
	// NPCs
	private static int Jeremy = 31521;
	private static int Yetis_Table = 31542;
	// Mobs
	private static int RB_Icicle_Emperor_Bumbalump = 25296;
	// Items
	private static int Soy_Sauce_Jar = 7205;
	private static int Food_for_Bumbalump = 7209;
	private static int Special_Yeti_Meat = 7210;
	private static int Reward_First = 4589;
	private static int Reward_Last = 4594;

	public _625_TheFinestIngredientsPart2()
	{
		super(true);
		addStartNpc(Jeremy);
		addTalkId(Yetis_Table);
		addKillId(RB_Icicle_Emperor_Bumbalump);
		addQuestItem(Food_for_Bumbalump, Special_Yeti_Meat);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		int _state = st.getState();
		int cond = st.getCond();
		if (event.equalsIgnoreCase("jeremy_q0625_0104.htm") && _state == CREATED)
		{
			if (st.getQuestItemsCount(Soy_Sauce_Jar) == 0)
			{
				st.exitCurrentQuest(true);
				return "jeremy_q0625_0102.htm";
			}
			st.setState(STARTED);
			st.setCond(1);
			st.takeItems(Soy_Sauce_Jar, 1);
			st.giveItems(Food_for_Bumbalump, 1);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("jeremy_q0625_0301.htm") && _state == STARTED && cond == 3)
		{
			st.exitCurrentQuest(true);
			if (st.getQuestItemsCount(Special_Yeti_Meat) == 0)
			{
				return "jeremy_q0625_0302.htm";
			}
			st.takeItems(Special_Yeti_Meat, 1);
			st.giveItems(Rnd.get(Reward_First, Reward_Last), 5, true);
		}
		else if (event.equalsIgnoreCase("yetis_table_q0625_0201.htm") && _state == STARTED && cond == 1)
		{
			if (ServerVariables.getLong(_625_TheFinestIngredientsPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
			{
				return "yetis_table_q0625_0204.htm";
			}
			if (st.getQuestItemsCount(Food_for_Bumbalump) == 0)
			{
				return "yetis_table_q0625_0203.htm";
			}
			if (BumbalumpSpawned())
			{
				return "yetis_table_q0625_0202.htm";
			}
			st.takeItems(Food_for_Bumbalump, 1);
			st.setCond(2);
			ThreadPoolManager.getInstance().schedule(new BumbalumpSpawner(), 1000);
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
			if (npcId != Jeremy)
			{
				return "noquest";
			}
			if (st.getPlayer().getLevel() < 73)
			{
				st.exitCurrentQuest(true);
				return "jeremy_q0625_0103.htm";
			}
			if (st.getQuestItemsCount(Soy_Sauce_Jar) == 0)
			{
				st.exitCurrentQuest(true);
				return "jeremy_q0625_0102.htm";
			}
			st.setCond(0);
			return "jeremy_q0625_0101.htm";
		}

		if (_state != STARTED)
		{
			return "noquest";
		}
		int cond = st.getCond();

		if (npcId == Jeremy)
		{
			switch (cond)
			{
			case 1:
				return "jeremy_q0625_0105.htm";
			case 2:
				return "jeremy_q0625_0202.htm";
			case 3:
				return "jeremy_q0625_0201.htm";
			default:
				break;
			}
		}

		if (npcId == Yetis_Table)
		{
			if (ServerVariables.getLong(_625_TheFinestIngredientsPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
			{
				return "yetis_table_q0625_0204.htm";
			}
			switch (cond)
			{
			case 1:
				return "yetis_table_q0625_0101.htm";
			case 2:
				if (BumbalumpSpawned())
				{
					return "yetis_table_q0625_0202.htm";
				}
				ThreadPoolManager.getInstance().schedule(new BumbalumpSpawner(), 1000);
				return "yetis_table_q0625_0201.htm";
			case 3:
				return "yetis_table_q0625_0204.htm";
			default:
				break;
			}
		}

		return "noquest";
	}

	private static class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			ServerVariables.set(_625_TheFinestIngredientsPart2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
		}
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{

		if (st.getCond() == 1 || st.getCond() == 2)
		{
			if (st.getQuestItemsCount(Food_for_Bumbalump) > 0)
			{
				st.takeItems(Food_for_Bumbalump, 1);
			}
			st.giveItems(Special_Yeti_Meat, 1);
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
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

	private static boolean BumbalumpSpawned()
	{
		return GameObjectsStorage.getByNpcId(RB_Icicle_Emperor_Bumbalump) != null;
	}

	public class BumbalumpSpawner extends RunnableImpl
	{
		private SimpleSpawner _spawn = null;
		private int tiks = 0;

		public BumbalumpSpawner()
		{
			if (BumbalumpSpawned())
			{
				return;
			}
			NpcTemplate template = NpcHolder.getInstance().getTemplate(RB_Icicle_Emperor_Bumbalump);
			if (template == null)
			{
				return;
			}
			try
			{
				_spawn = new SimpleSpawner(template);
			}
			catch (Exception E)
			{
				return;
			}
			_spawn.setLocx(158240);
			_spawn.setLocy(-121536);
			_spawn.setLocz(-2253);
			_spawn.setHeading(Rnd.get(0, 0xFFFF));
			_spawn.setAmount(1);
			_spawn.doSpawn(true);
			_spawn.stopRespawn();
			for (NpcInstance _npc : _spawn.getAllSpawned())
			{
				_npc.addListener(new DeathListener());
			}
		}

		public void Say(String test)
		{
			for (NpcInstance _npc : _spawn.getAllSpawned())
			{
				Functions.npcSay(_npc, test);
			}
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_spawn == null)
			{
				return;
			}
			if (tiks == 0)
			{
				Say("I will crush you!");
			}
			if (tiks < 1200 && BumbalumpSpawned())
			{
				tiks++;
				if (tiks == 1200)
				{
					Say("May the gods forever condemn you! Your power weakens!");
				}
				ThreadPoolManager.getInstance().schedule(this, 1000);
				return;
			}
			_spawn.deleteAll();
		}
	}
}