package quests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

public abstract class SagasSuperclass extends Quest
{
	protected int id = 0;
	protected int classid = 0;
	protected int prevclass = 0;
	protected int[] NPC = new int[]
	{
		0,
		1,
		2,
		3,
		4,
		5,
		6,
		7,
		8,
		9,
		10
	};
	public int[] Items = new int[]
	{
		0,
		1,
		2,
		3,
		4,
		5,
		6,
		7,
		8,
		9,
		10
	};
	protected int[] Mob = new int[]
	{
		0,
		1,
		2
	};
	protected int[] X = new int[]
	{
		0,
		1,
		2
	};
	protected int[] Y = new int[]
	{
		0,
		1,
		2
	};
	protected int[] Z = new int[]
	{
		0,
		1,
		2
	};
	public String[] Text = new String[18];
	protected List<Spawn> Spawn_List = new ArrayList<Spawn>();

	private class Spawn
	{
		public final int npcId, TimeToLive;
		public final long spawned_at, charStoreId, npcStoreId;

		public Spawn(NpcInstance npc, long charStoreId, int TimeToLive)
		{
			npcId = npc.getNpcId();
			npcStoreId = npc.getStoredId();
			this.charStoreId = charStoreId;
			this.TimeToLive = TimeToLive;
			spawned_at = System.currentTimeMillis();
		}

		public NpcInstance getNPC()
		{
			return GameObjectsStorage.getAsNpc(npcStoreId);
		}
	}

	protected int[] Archon_Minions = new int[]
	{
		21646,
		21647,
		21648,
		21649,
		21650,
		21651
	};
	protected int[] Guardian_Angels = new int[]
	{
		27214,
		27215,
		27216
	};
	protected int[] Archon_Hellisha_Norm = new int[]
	{
		18212,
		18213,
		18214,
		18215,
		18216,
		18217,
		18218,
		18219
	};

	protected static Map<Integer, Class<?>> Quests = new HashMap<Integer, Class<?>>();

	static
	{
		Quests.put(67, _067_SagaOfTheDoombringer.class);
		Quests.put(68, _068_SagaOfTheSoulHound.class);
		Quests.put(69, _069_SagaOfTheTrickster.class);
		Quests.put(70, _070_SagaOfThePhoenixKnight.class);
		Quests.put(71, _071_SagaOfEvasTemplar.class);
		Quests.put(72, _072_SagaOfTheSwordMuse.class);
		Quests.put(73, _073_SagaOfTheDuelist.class);
		Quests.put(74, _074_SagaOfTheDreadnoughts.class);
		Quests.put(75, _075_SagaOfTheTitan.class);
		Quests.put(76, _076_SagaOfTheGrandKhavatari.class);
		Quests.put(77, _077_SagaOfTheDominator.class);
		Quests.put(78, _078_SagaOfTheDoomcryer.class);
		Quests.put(79, _079_SagaOfTheAdventurer.class);
		Quests.put(80, _080_SagaOfTheWindRider.class);
		Quests.put(81, _081_SagaOfTheGhostHunter.class);
		Quests.put(82, _082_SagaOfTheSagittarius.class);
		Quests.put(83, _083_SagaOfTheMoonlightSentinel.class);
		Quests.put(84, _084_SagaOfTheGhostSentinel.class);
		Quests.put(85, _085_SagaOfTheCardinal.class);
		Quests.put(86, _086_SagaOfTheHierophant.class);
		Quests.put(87, _087_SagaOfEvasSaint.class);
		Quests.put(88, _088_SagaOfTheArchmage.class);
		Quests.put(89, _089_SagaOfTheMysticMuse.class);
		Quests.put(90, _090_SagaOfTheStormScreamer.class);
		Quests.put(91, _091_SagaOfTheArcanaLord.class);
		Quests.put(92, _092_SagaOfTheElementalMaster.class);
		Quests.put(93, _093_SagaOfTheSpectralMaster.class);
		Quests.put(94, _094_SagaOfTheSoultaker.class);
		Quests.put(95, _095_SagaOfTheHellKnight.class);
		Quests.put(96, _096_SagaOfTheSpectralDancer.class);
		Quests.put(97, _097_SagaOfTheShillienTemplar.class);
		Quests.put(98, _098_SagaOfTheShillienSaint.class);
		Quests.put(99, _099_SagaOfTheFortuneSeeker.class);
		Quests.put(100, _100_SagaOfTheMaestro.class);
	}

	protected static int[][] QuestClass = new int[][]
	{
		{
			0x7f
		},
		{
			0x80,
			0x81
		},
		{
			0x82
		},
		{
			0x05
		},
		{
			0x14
		},
		{
			0x15
		},
		{
			0x02
		},
		{
			0x03
		},
		{
			0x2e
		},
		{
			0x30
		},
		{
			0x33
		},
		{
			0x34
		},
		{
			0x08
		},
		{
			0x17
		},
		{
			0x24
		},
		{
			0x09
		},
		{
			0x18
		},
		{
			0x25
		},
		{
			0x10
		},
		{
			0x11
		},
		{
			0x1e
		},
		{
			0x0c
		},
		{
			0x1b
		},
		{
			0x28
		},
		{
			0x0e
		},
		{
			0x1c
		},
		{
			0x29
		},
		{
			0x0d
		},
		{
			0x06
		},
		{
			0x22
		},
		{
			0x21
		},
		{
			0x2b
		},
		{
			0x37
		},
		{
			0x39
		}
	};

	private void cleanTempVars()
	{
		Connection con = null;
		PreparedStatement st = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			st = con.prepareStatement("DELETE FROM character_quests WHERE name=? AND (var='spawned' OR var='kills' OR var='Archon' OR var LIKE 'Mob_%')");
			st.setString(1, getName());
			st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, st);
		}
	}

	private void FinishQuest(QuestState st, Player player)
	{
		st.addExpAndSp(2586527, 0);
		st.giveItems(ADENA_ID, 5000000);
		st.giveItems(6622, 1, true);
		st.exitCurrentQuest(true);
		player.setClassId(getClassId(player), false, true);
		if (!player.isSubClassActive() && player.getBaseClassId() == getPrevClass(player))
		{
			player.setBaseClass(getClassId(player));
		}
		player.broadcastCharInfo();
		Cast(st.findTemplate(NPC[0]), player, 4339, 1);
	}

	public SagasSuperclass(boolean party)
	{
		super(party);
		cleanTempVars();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new SpawnCleaner(), 60000, 10000);
	}

	protected void registerNPCs()
	{
		addStartNpc(NPC[0]);
		addAttackId(Mob[2]);
		addFirstTalkId(NPC[4]);

		for (int npc : NPC)
		{
			addTalkId(npc);
		}

		for (int mobid : Mob)
		{
			addKillId(mobid);
		}

		for (int mobid : Archon_Minions)
		{
			addKillId(mobid);
		}

		for (int mobid : Guardian_Angels)
		{
			addKillId(mobid);
		}

		for (int mobid : Archon_Hellisha_Norm)
		{
			addKillId(mobid);
		}

		for (int ItemId : Items)
		{
			if (ItemId != 0 && ItemId != 7080 && ItemId != 7081 && ItemId != 6480 && ItemId != 6482)
			{
				addQuestItem(ItemId);
			}
		}
	}

	protected int getClassId(Player player)
	{
		return classid;
	}

	protected int getPrevClass(Player player)
	{
		return prevclass;
	}

	protected void Cast(NpcInstance npc, Creature target, int skillId, int level)
	{
		target.broadcastPacket(new MagicSkillUse(target, target, skillId, level, 6000, 1));
		target.broadcastPacket(new MagicSkillUse(npc, npc, skillId, level, 6000, 1));
	}

	public class SpawnCleaner extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			synchronized (Spawn_List)
			{
				long curr_time = System.currentTimeMillis();
				Iterator<Spawn> itr = Spawn_List.iterator();
				while (itr.hasNext())
				{
					Spawn spawn = itr.next();
					NpcInstance npc = spawn.getNPC();
					if (curr_time - spawn.spawned_at > spawn.TimeToLive || npc == null)
					{
						if (npc != null)
						{
							npc.deleteMe();
						}
						itr.remove();
					}
				}
			}
		}
	}

	protected void AddSpawn(Player player, NpcInstance mob, int TimeToLive)
	{
		synchronized (Spawn_List)
		{
			Spawn_List.add(new Spawn(mob, player.getStoredId(), TimeToLive));
		}
	}

	protected NpcInstance FindMySpawn(Player player, int npcId)
	{
		if (npcId == 0 || player == null)
		{
			return null;
		}
		long charStoredId = player.getStoredId();
		synchronized (Spawn_List)
		{
			for (Spawn spawn : Spawn_List)
			{
				if (spawn.charStoreId == charStoredId && spawn.npcId == npcId)
				{
					return spawn.getNPC();
				}
			}
		}
		return null;
	}

	protected void DeleteSpawn(long charStoredId, int npcId)
	{
		if (npcId == 0 || charStoredId == 0)
		{
			return;
		}
		synchronized (Spawn_List)
		{
			Iterator<Spawn> it = Spawn_List.iterator();
			while (it.hasNext())
			{
				Spawn spawn = it.next();
				if (spawn.charStoreId == charStoredId && spawn.npcId == npcId)
				{
					NpcInstance npc = spawn.getNPC();
					if (npc != null)
					{
						npc.deleteMe();
					}
					it.remove();
				}
			}
		}
	}

	protected void DeleteMySpawn(Player player, int npcId)
	{
		if (npcId > 0 && player != null)
		{
			DeleteSpawn(player.getStoredId(), npcId);
		}
	}

	protected NpcInstance spawn(int id, Location loc)
	{
		NpcTemplate template = NpcHolder.getInstance().getTemplate(id);
		SimpleSpawner spawn;
		try
		{
			spawn = new SimpleSpawner(template);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		spawn.setLoc(loc);
		NpcInstance npc = spawn.doSpawn(true);
		spawn.stopRespawn();
		return npc;
	}

	public void giveHallishaMark(QuestState st)
	{
		if (GameObjectsStorage.getNpc(st.getInt("Archon")) != null)
		{
			return; // Не убили, или убили чужого
		}

		st.cancelQuestTimer("Archon Hellisha has despawned");

		if (st.getQuestItemsCount(Items[3]) < 700)
		{
			st.giveItems(Items[3], Rnd.get(1, 4)); // freya change
		}
		else
		{
			st.takeItems(Items[3], 20);
			NpcInstance Archon = spawn(Mob[1], st.getPlayer().getLoc());
			AddSpawn(st.getPlayer(), Archon, 600000);
			int ArchonId = Archon.getObjectId();
			st.set("Archon", str(ArchonId));
			st.startQuestTimer("Archon Hellisha has despawned", 600000, Archon);
			Archon.setRunning();
			Archon.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, new Object[]
			{
				st.getPlayer(),
				100000
			});
			AutoChat(Archon, Text[13].replace("PLAYERNAME", st.getPlayer().getName()));
		}
	}

	protected QuestState findRightState(Player player, NpcInstance npc)
	{
		if (player == null || npc == null)
		{
			return null;
		}
		long npcStoredId = npc.getStoredId(), charStoredId = player.getStoredId();

		synchronized (Spawn_List)
		{
			for (Spawn spawn : Spawn_List)
			{
				if (spawn.charStoreId == charStoredId && spawn.npcStoreId == npcStoredId)
				{
					return player.getQuestState(getName());
				}
			}

			for (Spawn spawn : Spawn_List)
			{
				if (spawn.npcStoreId == npcStoredId)
				{
					player = GameObjectsStorage.getAsPlayer(spawn.charStoreId);
					return player == null ? null : player.getQuestState(getName());
				}
			}
		}

		return null;
	}

	public static QuestState findQuest(Player player)
	{
		QuestState st = null;
		for (Integer q : Quests.keySet())
		{
			st = player.getQuestState(Quests.get(q));
			if (st != null)
			{
				int[] qc = QuestClass[q - 67];
				for (int c : qc)
				{
					if (player.getClassId().getId() == c)
					{
						return st;
					}
				}
			}
		}
		return null;
	}

	public static void process_step_15to16(QuestState st)
	{
		if (st == null || st.getCond() != 15)
		{
			return;
		}
		int Halishas_Mark = ((SagasSuperclass) st.getQuest()).Items[3];
		int Resonance_Amulet = ((SagasSuperclass) st.getQuest()).Items[8];

		st.takeItems(Halishas_Mark, -1);
		if (st.getQuestItemsCount(Resonance_Amulet) == 0)
		{
			st.giveItems(Resonance_Amulet, 1);
		}
		st.setCond(16);
		st.playSound(SOUND_MIDDLE);
	}

	protected void AutoChat(NpcInstance npc, String text)
	{
		if (npc != null)
		{
			Functions.npcSay(npc, text);
		}
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = ""; // simple initialization...if none of the events match, return nothing.
		Player player = st.getPlayer();

		if (event.equalsIgnoreCase("0-011.htm") || event.equalsIgnoreCase("0-012.htm") || event.equalsIgnoreCase("0-013.htm") || event.equalsIgnoreCase("0-014.htm") || event.equalsIgnoreCase("0-015.htm"))
		{
			htmltext = event;
		}
		else if (event.equalsIgnoreCase("accept"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
			st.giveItems(Items[10], 1);
			htmltext = "0-03.htm";
		}
		else if (event.equalsIgnoreCase("0-1"))
		{
			if (player.getLevel() < 76)
			{
				htmltext = "0-02.htm";
				st.exitCurrentQuest(true);
			}
			else
			{
				htmltext = "0-05.htm";
			}
		}
		else if (event.equalsIgnoreCase("0-2"))
		{
			if (player.getLevel() >= 76)
			{
				htmltext = "0-07.htm";
				st.takeItems(Items[10], -1);
				FinishQuest(st, player);
			}
			else
			{
				st.takeItems(Items[10], -1);
				st.playSound(SOUND_MIDDLE);
				st.setCond(20);
				htmltext = "0-08.htm";
			}
		}
		else if (event.equalsIgnoreCase("1-3"))
		{
			st.setCond(3);
			htmltext = "1-05.htm";
		}
		else if (event.equalsIgnoreCase("1-4"))
		{
			st.setCond(4);
			st.takeItems(Items[0], 1);
			if (Items[11] != 0)
			{
				st.takeItems(Items[11], 1);
			}
			st.giveItems(Items[1], 1);
			htmltext = "1-06.htm";
		}
		else if (event.equalsIgnoreCase("2-1"))
		{
			st.setCond(2);
			htmltext = "2-05.htm";
		}
		else if (event.equalsIgnoreCase("2-2"))
		{
			st.setCond(5);
			st.takeItems(Items[1], 1);
			st.giveItems(Items[4], 1);
			htmltext = "2-06.htm";
		}
		else if (event.equalsIgnoreCase("3-5"))
		{
			htmltext = "3-07.htm";
		}
		else if (event.equalsIgnoreCase("3-6"))
		{
			st.setCond(11);
			htmltext = "3-02.htm";
		}
		else if (event.equalsIgnoreCase("3-7"))
		{
			st.setCond(12);
			htmltext = "3-03.htm";
		}
		else if (event.equalsIgnoreCase("3-8"))
		{
			st.setCond(13);
			st.takeItems(Items[2], 1);
			st.giveItems(Items[7], 1);
			htmltext = "3-08.htm";
		}
		else if (event.equalsIgnoreCase("4-1"))
		{
			htmltext = "4-010.htm";
		}
		else if (event.equalsIgnoreCase("4-2"))
		{
			st.giveItems(Items[9], 1);
			st.setCond(18);
			st.playSound(SOUND_MIDDLE);
			htmltext = "4-011.htm";
		}
		else if (event.equalsIgnoreCase("4-3"))
		{
			st.giveItems(Items[9], 1);
			st.setCond(18);
			st.set("Quest0", "0");
			st.playSound(SOUND_MIDDLE);
			NpcInstance Mob_2 = FindMySpawn(player, NPC[4]);
			if (Mob_2 != null)
			{
				AutoChat(Mob_2, Text[13].replace("PLAYERNAME", player.getName()));
				DeleteMySpawn(player, NPC[4]);
				st.cancelQuestTimer("Mob_2 has despawned");
				st.cancelQuestTimer("NPC_4 Timer");
			}
			return null;
		}
		else if (event.equalsIgnoreCase("5-1"))
		{
			st.setCond(6);
			st.takeItems(Items[4], 1);
			Cast(st.findTemplate(NPC[5]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "5-02.htm";
		}
		else if (event.equalsIgnoreCase("6-1"))
		{
			st.setCond(8);
			st.takeItems(Items[5], 1);
			Cast(st.findTemplate(NPC[6]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "6-03.htm";
		}
		else if (event.equalsIgnoreCase("7-1"))
		{
			if (FindMySpawn(player, Mob[0]) == null)
			{
				NpcInstance Mob_1 = spawn(Mob[0], new Location(X[0], Y[0], Z[0]));
				AddSpawn(player, Mob_1, 180000);
				st.startQuestTimer("Mob_0 Timer", 500L, Mob_1);
				st.startQuestTimer("Mob_1 has despawned", 120000L, Mob_1);
				htmltext = "7-02.htm";
			}
			else
			{
				htmltext = "7-03.htm";
			}
		}
		else if (event.equalsIgnoreCase("7-2"))
		{
			st.setCond(10);
			st.takeItems(Items[6], 1);
			Cast(st.findTemplate(NPC[7]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "7-06.htm";
		}
		else if (event.equalsIgnoreCase("8-1"))
		{
			st.setCond(14);
			st.takeItems(Items[7], 1);
			Cast(st.findTemplate(NPC[8]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "8-02.htm";
		}
		else if (event.equalsIgnoreCase("9-1"))
		{
			st.setCond(17);
			st.takeItems(Items[8], 1);
			Cast(st.findTemplate(NPC[9]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "9-03.htm";
		}
		else if (event.equalsIgnoreCase("10-1"))
		{
			if (st.getInt("Quest0") == 0 || FindMySpawn(player, NPC[4]) == null)
			{
				DeleteMySpawn(player, NPC[4]);
				DeleteMySpawn(player, Mob[2]);
				st.set("Quest0", "1");
				st.set("Quest1", "45");

				NpcInstance NPC_4 = spawn(NPC[4], new Location(X[2], Y[2], Z[2]));
				NpcInstance Mob_2 = spawn(Mob[2], new Location(X[1], Y[1], Z[1]));
				AddSpawn(player, Mob_2, 300000);
				AddSpawn(player, NPC_4, 300000);
				st.startQuestTimer("Mob_2 Timer", 1000, Mob_2);
				st.startQuestTimer("Mob_2 despawn", 59000, Mob_2);
				st.startQuestTimer("NPC_4 Timer", 500, NPC_4);
				st.startQuestTimer("NPC_4 despawn", 60000, NPC_4);
				htmltext = "10-02.htm";
			}
			else if (st.getInt("Quest1") == 45)
			{
				htmltext = "10-03.htm";
			}
			else if (st.getInt("Tab") == 1)
			{
				NpcInstance Mob_2 = FindMySpawn(player, NPC[4]);
				if (Mob_2 == null || !st.getPlayer().knowsObject(Mob_2))
				{
					DeleteMySpawn(player, NPC[4]);
					Mob_2 = spawn(NPC[4], new Location(X[2], Y[2], Z[2]));
					AddSpawn(player, Mob_2, 300000);
					st.set("Quest0", "1");
					st.set("Quest1", "0"); // На всякий случай
					st.startQuestTimer("NPC_4 despawn", 180000, Mob_2);
				}
				htmltext = "10-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("10-2"))
		{
			st.setCond(19);
			st.takeItems(Items[9], 1);
			Cast(st.findTemplate(NPC[10]), player, 4546, 1);
			st.playSound(SOUND_MIDDLE);
			htmltext = "10-06.htm";
		}
		else if (event.equalsIgnoreCase("11-9"))
		{
			st.setCond(15);
			htmltext = "11-03.htm";
		}
		else if (event.equalsIgnoreCase("Mob_0 Timer"))
		{
			AutoChat(FindMySpawn(player, Mob[0]), Text[0].replace("PLAYERNAME", player.getName()));
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_1 has despawned"))
		{
			AutoChat(FindMySpawn(player, Mob[0]), Text[1].replace("PLAYERNAME", player.getName()));
			DeleteMySpawn(player, Mob[0]);
			return null;
		}
		else if (event.equalsIgnoreCase("Archon Hellisha has despawned"))
		{
			AutoChat(npc, Text[6].replace("PLAYERNAME", player.getName()));
			DeleteMySpawn(player, Mob[1]);
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_2 Timer"))
		{
			NpcInstance NPC_4 = FindMySpawn(player, NPC[4]);
			NpcInstance Mob_2 = FindMySpawn(player, Mob[2]);
			if (NPC_4.knowsObject(Mob_2))
			{
				NPC_4.setRunning();
				NPC_4.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, Mob_2, null);
				Mob_2.setRunning();
				Mob_2.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, NPC_4, null);
				AutoChat(Mob_2, Text[14].replace("PLAYERNAME", player.getName()));
			}
			else
			{
				st.startQuestTimer("Mob_2 Timer", 1000, npc);
			}
			return null;
		}
		else if (event.equalsIgnoreCase("Mob_2 despawn"))
		{
			NpcInstance Mob_2 = FindMySpawn(player, Mob[2]);
			AutoChat(Mob_2, Text[15].replace("PLAYERNAME", player.getName()));
			st.set("Quest0", "2");
			if (Mob_2 != null)
			{
				Mob_2.reduceCurrentHp(9999999, Mob_2, null, true, true, false, false, false, false, false);
			}
			DeleteMySpawn(player, Mob[2]);
			return null;
		}
		else if (event.equalsIgnoreCase("NPC_4 Timer"))
		{
			AutoChat(FindMySpawn(player, NPC[4]), Text[7].replace("PLAYERNAME", player.getName()));
			st.startQuestTimer("NPC_4 Timer 2", 1500, npc);
			if (st.getInt("Quest1") == 45)
			{
				st.set("Quest1", "0");
			}
			return null;
		}
		else if (event.equalsIgnoreCase("NPC_4 Timer 2"))
		{
			AutoChat(FindMySpawn(player, NPC[4]), Text[8].replace("PLAYERNAME", player.getName()));
			st.startQuestTimer("NPC_4 Timer 3", 10000, npc);
			return null;
		}
		else if (event.equalsIgnoreCase("NPC_4 Timer 3"))
		{
			if (st.getInt("Quest0") == 0)
			{
				st.startQuestTimer("NPC_4 Timer 3", 13000, npc);
				AutoChat(FindMySpawn(player, NPC[4]), Text[Rnd.get(9, 10)].replace("PLAYERNAME", player.getName()));
			}
			return null;
		}
		else if (event.equalsIgnoreCase("NPC_4 despawn"))
		{
			st.set("Quest1", str(st.getInt("Quest1") + 1));
			NpcInstance NPC_4 = FindMySpawn(player, NPC[4]);
			if (st.getInt("Quest0") == 1 || st.getInt("Quest0") == 2 || st.getInt("Quest1") > 3)
			{
				st.set("Quest0", "0");
				AutoChat(NPC_4, Text[Rnd.get(11, 12)].replace("PLAYERNAME", player.getName()));
				if (NPC_4 != null)
				{
					NPC_4.reduceCurrentHp(9999999, NPC_4, null, true, true, false, false, false, false, false);
				}
				DeleteMySpawn(player, NPC[4]);
			}
			else
			{
				st.startQuestTimer("NPC_4 despawn", 1000, npc);
			}
			return null;
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		if (player.getClassId().getId() != getPrevClass(player))
		{
			st.exitCurrentQuest(true);
			return htmltext;
		}

		switch (cond)
		{
		case 0:
			if (npcId == NPC[0])
			{
				htmltext = "0-01.htm";
			}
			break;
		case 1:
			if (npcId == NPC[0])
			{
				htmltext = "0-04.htm";
			}
			else if (npcId == NPC[2])
			{
				htmltext = "2-01.htm";
			}
			break;
		case 2:
			if (npcId == NPC[2])
			{
				htmltext = "2-02.htm";
			}
			else if (npcId == NPC[1])
			{
				htmltext = "1-01.htm";
			}
			break;
		case 3:
			if (npcId == NPC[1])
			{
				if (st.getQuestItemsCount(Items[0]) > 0)
				{
					if (Items[11] == 0)
					{
						htmltext = "1-03.htm";
					}
					else if (st.getQuestItemsCount(Items[11]) > 0)
					{
						htmltext = "1-03.htm";
					}
					else
					{
						htmltext = "1-02.htm";
					}
				}
				else
				{
					htmltext = "1-02.htm";
				}
			}
			else if (npcId == 31537)
			{
				if (st.getQuestItemsCount(7546) == 0)
				{
					htmltext = "tunatun_q72_01.htm";
					st.giveItems(7546, 1);
					return null;
				}
				else
				{
					htmltext = "tunatun_q72_02.htm";
				}
			}
			break;
		case 4:
			if (npcId == NPC[1])
			{
				htmltext = "1-04.htm";
			}
			else if (npcId == NPC[2])
			{
				htmltext = "2-03.htm";
			}
			break;
		case 5:
			if (npcId == NPC[2])
			{
				htmltext = "2-04.htm";
			}
			else if (npcId == NPC[5])
			{
				htmltext = "5-01.htm";
			}
			break;
		case 6:
			if (npcId == NPC[5])
			{
				htmltext = "5-03.htm";
			}
			else if (npcId == NPC[6])
			{
				htmltext = "6-01.htm";
			}
			break;
		case 7:
			if (npcId == NPC[6])
			{
				htmltext = "6-02.htm";
			}
			break;
		case 8:
			if (npcId == NPC[6])
			{
				htmltext = "6-04.htm";
			}
			else if (npcId == NPC[7])
			{
				htmltext = "7-01.htm";
			}
			break;
		case 9:
			if (npcId == NPC[7])
			{
				htmltext = "7-05.htm";
			}
			break;
		case 10:
			if (npcId == NPC[7])
			{
				htmltext = "7-07.htm";
			}
			else if (npcId == NPC[3])
			{
				htmltext = "3-01.htm";
			}
			break;
		case 11:
		case 12:
			if (npcId == NPC[3])
			{
				if (st.getQuestItemsCount(Items[2]) > 0)
				{
					htmltext = "3-05.htm";
				}
				else
				{
					htmltext = "3-04.htm";
				}
			}
			break;
		case 13:
			if (npcId == NPC[3])
			{
				htmltext = "3-06.htm";
			}
			else if (npcId == NPC[8])
			{
				htmltext = "8-01.htm";
			}
			break;
		case 14:
			if (npcId == NPC[8])
			{
				htmltext = "8-03.htm";
			}
			else if (npcId == NPC[11])
			{
				htmltext = "11-01.htm";
			}
			break;
		case 15:
			if (npcId == NPC[11])
			{
				htmltext = "11-02.htm";
			}
			else if (npcId == NPC[9])
			{
				htmltext = "9-01.htm";
			}
			break;
		case 16:
			if (npcId == NPC[9])
			{
				htmltext = "9-02.htm";
			}
			break;
		case 17:
			if (npcId == NPC[9])
			{
				htmltext = "9-04.htm";
			}
			else if (npcId == NPC[10])
			{
				htmltext = "10-01.htm";
			}
			break;
		case 18:
			if (npcId == NPC[10])
			{
				htmltext = "10-05.htm";
			}
			break;
		case 19:
			if (npcId == NPC[10])
			{
				htmltext = "10-07.htm";
			}
			if (npcId == NPC[0])
			{
				htmltext = "0-06.htm";
			}
			break;
		case 20:
			if (npcId == NPC[0])
			{
				if (player.getLevel() >= 76)
				{
					htmltext = "0-09.htm";
					if (getClassId(player) < 131 || getClassId(player) > 135)
					{
						FinishQuest(st, player);
					}
				}
				else
				{
					htmltext = "0-010.htm";
				}
			}
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		String htmltext = "";
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (npcId == NPC[4])
		{
			if (cond == 17)
			{
				QuestState st2 = findRightState(player, npc);
				if (st2 != null)
				{
					if (st == st2)
					{
						if (st.getInt("Tab") == 1)
						{
							if (st.getInt("Quest0") == 0)
							{
								htmltext = "4-04.htm";
							}
							else if (st.getInt("Quest0") == 1)
							{
								htmltext = "4-06.htm";
							}
						}
						else if (st.getInt("Quest0") == 0)
						{
							htmltext = "4-01.htm";
						}
						else if (st.getInt("Quest0") == 1)
						{
							htmltext = "4-03.htm";
						}
					}
					else if (st.getInt("Tab") == 1)
					{
						if (st.getInt("Quest0") == 0)
						{
							htmltext = "4-05.htm";
						}
						else if (st.getInt("Quest0") == 1)
						{
							htmltext = "4-07.htm";
						}
					}
					else if (st.getInt("Quest0") == 0)
					{
						htmltext = "4-02.htm";
					}
				}
			}
			else if (cond == 18)
			{
				htmltext = "4-08.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		if (st.getCond() == 17)
		{
			if (npc.getNpcId() == Mob[2])
			{
				QuestState st2 = findRightState(player, npc);
				if (st == st2)
				{
					st.set("Quest0", str(st.getInt("Quest0") + 1));
					if (st.getInt("Quest0") == 1)
					{
						AutoChat(npc, Text[16].replace("PLAYERNAME", player.getName()));
					}
					if (st.getInt("Quest0") > 15)
					{
						st.set("Quest0", "1");
						AutoChat(npc, Text[17].replace("PLAYERNAME", player.getName()));
						npc.reduceCurrentHp(9999999, npc, null, true, true, false, false, false, false, false);
						DeleteMySpawn(player, Mob[2]);
						st.cancelQuestTimer("Mob_2 despawn");
						st.set("Tab", "1");
					}
				}
			}
		}
		return null;
	}

	protected boolean isArchonMinions(int npcId)
	{
		for (int id : Archon_Minions)
		{
			if (id == npcId)
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isArchonHellishaNorm(int npcId)
	{
		for (int id : Archon_Hellisha_Norm)
		{
			if (id == npcId)
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isGuardianAngels(int npcId)
	{
		for (int id : Guardian_Angels)
		{
			if (id == npcId)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		if (player.getActiveClassId() != getPrevClass(player))
		{
			return null;
		}

		if (isArchonMinions(npcId))
		{
			Party party = player.getParty();
			if (party != null)
			{
				for (Player player1 : party.getMembers())
				{
					if (player1.getDistance(player) <= Config.ALT_PARTY_DISTRIBUTION_RANGE)
					{
						QuestState st1 = findQuest(player1);
						if (st1 != null && st1.getCond() == 15)
						{
							((SagasSuperclass) st1.getQuest()).giveHallishaMark(st1);
						}
					}
				}
			}
			else
			{
				QuestState st1 = findQuest(player);
				if (st1 != null && st1.getCond() == 15)
				{
					((SagasSuperclass) st1.getQuest()).giveHallishaMark(st1);
				}
			}
		}
		else if (isArchonHellishaNorm(npcId))
		{
			QuestState st1 = findQuest(player);
			if (st1 != null)
			{
				if (st1.getCond() == 15)
				{
					// This is just a guess....not really sure what it actually says, if anything
					AutoChat(npc, ((SagasSuperclass) st1.getQuest()).Text[4].replace("PLAYERNAME", st1.getPlayer().getName()));
					process_step_15to16(st1);
				}
			}
		}
		else if (isGuardianAngels(npcId))
		{
			QuestState st1 = findQuest(player);
			if (st1 != null)
			{
				if (st1.getCond() == 6)
				{
					if (st1.getInt("kills") < 9)
					{
						st1.set("kills", str(st1.getInt("kills") + 1));
					}
					else
					{
						st1.playSound(SOUND_MIDDLE);
						st1.giveItems(((SagasSuperclass) st1.getQuest()).Items[5], 1);
						st1.setCond(7);
					}
				}
			}
		}
		else
		{
			int cond = st.getCond();
			if (npcId == Mob[0] && cond == 8)
			{
				QuestState st2 = findRightState(player, npc);
				if (st2 != null)
				{
					if (!player.isInParty())
					{
						if (st == st2)
						{
							AutoChat(npc, Text[12].replace("PLAYERNAME", player.getName()));
							st.giveItems(Items[6], 1);
							st.setCond(9);
							st.playSound(SOUND_MIDDLE);
						}
					}
					st.cancelQuestTimer("Mob_1 has despawned");
					DeleteMySpawn(st2.getPlayer(), Mob[0]);
				}
			}
			else if (npcId == Mob[1] && cond == 15)
			{
				QuestState st2 = findRightState(player, npc);
				if (st2 != null)
				{
					if (!player.isInParty())
					{
						if (st == st2)
						{
							AutoChat(npc, Text[4].replace("PLAYERNAME", player.getName()));
							process_step_15to16(st);
						}
						else
						{
							AutoChat(npc, Text[5].replace("PLAYERNAME", player.getName()));
						}
					}
					st.cancelQuestTimer("Archon Hellisha has despawned");
					DeleteMySpawn(st2.getPlayer(), Mob[1]);
				}
			}
			else if (npcId == Mob[2] && cond == 17)
			{
				QuestState st2 = findRightState(player, npc);
				if (st == st2)
				{
					st.set("Quest0", "1");
					AutoChat(npc, Text[17].replace("PLAYERNAME", player.getName()));
					npc.reduceCurrentHp(9999999, npc, null, true, true, false, false, false, false, false);
					DeleteMySpawn(player, Mob[2]);
					st.cancelQuestTimer("Mob_2 despawn");
					st.set("Tab", "1");
				}
			}
		}
		return null;
	}
}
