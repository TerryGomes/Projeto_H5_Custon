package quests;

import gnu.trove.map.hash.TIntObjectHashMap;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Race;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.utils.Location;
import l2f.gameserver.utils.ReflectionUtils;

public class _179_IntoTheLargeCavern extends Quest implements ScriptFile, OnDeathListener
{
	public class World
	{
		public int instanceId;
		public int status;
	}

	private static TIntObjectHashMap<World> worlds = new TIntObjectHashMap<World>();

	private final static int KEKROPUS = 32138;
	private final static int GardenGuard = 25529;

	private final static int GardenGuard1 = 18347;
	private final static int GardenGuard2 = 18348;
	private final static int GardenGuard3 = 18349;

	private final static int Kamael_Guard = 18352;
	private final static int Guardian_of_Records = 18353;
	private final static int Guardian_of_Observation = 18354;
	private final static int Spiculas_Guard = 18355;
	private final static int Harkilgameds_Gatekeeper = 18356;
	private final static int Rodenpiculas_Gatekeeper = 18357;
	private final static int Guardian_of_Secrets = 18358;
	private final static int Guardian_of_Arviterre = 18359;
	private final static int Katenars_Gatekeeper = 18360;
	private final static int Guardian_of_Prediction = 18361;

	private final static int Gate_Key_Kamael = 9703;
	private final static int Gate_Key_Archives = 9704;
	private final static int Gate_Key_Observation = 9705;
	private final static int Gate_Key_Spicula = 9706;
	private final static int Gate_Key_Harkilgamed = 9707;
	private final static int Gate_Key_Rodenpicula = 9708;
	private final static int Gate_Key_Arviterre = 9709;
	private final static int Gate_Key_Katenar = 9710;
	private final static int Gate_Key_Prediction = 9711;
	private final static int Gate_Key_Massive_Cavern = 9712;
	private static final int izId = 11;

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

	public _179_IntoTheLargeCavern()
	{
		super(true);

		addStartNpc(KEKROPUS);
		addTalkId(GardenGuard);
		addAttackId(GardenGuard1);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("32138-06.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("EnterNornilsGarden"))
		{
			if (st.getCond() != 1 || st.getPlayer().getRace() != Race.kamael)
			{
				return "noquest";
			}
			enterInstance(npc, st.getPlayer());
			return null;
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		String htmltext = "noquest";
		if (st.getCond() == 0)
		{
			htmltext = "32138-01.htm";
			if (player.getLevel() < 17)
			{
				htmltext = "32138-02.htm";
				st.exitCurrentQuest(true);
			}
			else if (player.getLevel() > 20 || player.getClassId().getLevel() > 1)
			{
				htmltext = "32138-02a.htm";
				st.exitCurrentQuest(true);
			}
			else if (!player.isQuestCompleted(_178_IconicTrinity.class))
			{
				htmltext = "32138-03.htm";
				st.exitCurrentQuest(true);
			}
			else if (player.getRace() != Race.kamael)
			{
				htmltext = "32138-04.htm";
				st.exitCurrentQuest(true);
			}
		}
		else
		{
			htmltext = "32138-07.htm";
		}
		return htmltext;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		World world = worlds.get(npc.getReflectionId());
		if (world != null && world.status == 0)
		{
			world.status = 1;
			addSpawnToInstance(GardenGuard3, new Location(-110016, 74512, -12533, 0), 0, world.instanceId);
			addSpawnToInstance(GardenGuard2, new Location(-109729, 74913, -12533, 0), 0, world.instanceId);
			addSpawnToInstance(GardenGuard2, new Location(-109981, 74899, -12533, 0), 0, world.instanceId);
		}
		return null;
	}

	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		if (!cha.isNpc() || killer == null || !killer.isPlayable())
		{
			return;
		}

		Player player = killer.getPlayer();
		QuestState st = player.getQuestState(this.getClass());
		if (st == null)
		{
			return;
		}
		NpcInstance npc = (NpcInstance) cha;

		switch (cha.getNpcId())
		{
		case Kamael_Guard:
			st.dropItem(npc, Gate_Key_Kamael, 1);
			break;
		case Guardian_of_Records:
			st.dropItem(npc, Gate_Key_Archives, 1);
			break;
		case Guardian_of_Observation:
			st.dropItem(npc, Gate_Key_Observation, 1);
			break;
		case Spiculas_Guard:
			st.dropItem(npc, Gate_Key_Spicula, 1);
			break;
		case Harkilgameds_Gatekeeper:
			st.dropItem(npc, Gate_Key_Harkilgamed, 1);
			break;
		case Rodenpiculas_Gatekeeper:
			st.dropItem(npc, Gate_Key_Rodenpicula, 1);
			break;
		case Guardian_of_Arviterre:
			st.dropItem(npc, Gate_Key_Arviterre, 1);
			break;
		case Katenars_Gatekeeper:
			st.dropItem(npc, Gate_Key_Katenar, 1);
			break;
		case Guardian_of_Prediction:
			st.dropItem(npc, Gate_Key_Prediction, 1);
			break;
		case Guardian_of_Secrets:
			st.dropItem(npc, Gate_Key_Massive_Cavern, 1);
			break;
		}
	}

	private void enterInstance(NpcInstance npc, Player player)
	{
		Reflection r = player.getActiveReflection();
		if (r != null)
		{
			if (player.canReenterInstance(izId))
			{
				player.teleToLocation(r.getTeleportLoc(), r);
			}
		}
		else if (player.canEnterInstance(izId))
		{
			Reflection newInstance = ReflectionUtils.enterReflection(player, izId);
			World world = new World();
			world.instanceId = newInstance.getId();
			worlds.put(newInstance.getId(), world);
		}
	}
}