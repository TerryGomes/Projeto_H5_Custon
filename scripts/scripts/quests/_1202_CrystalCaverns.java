package quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bosses.BaylorManager;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.funcs.FuncMul;
import l2mv.gameserver.utils.GCSArray;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

public class _1202_CrystalCaverns extends Quest implements ScriptFile
{
	private static final int INCSTANCED_ZONE_ID = 10;
	// Items
	// private static final int CONTAMINATED_CRYSTAL = 9690;
	private static final int BLUE_CORAL_KEY = 9698;
	private static final int RED_CORAL_KEY = 9699;
	private static final int WHITE_SEED_OF_EVIL_SHARD = 9597;
	private static final int BLACK_SEED_OF_EVIL_SHARD = 9598;
	private static final int PRISON_KEY = 10015;

	// NPC
	private static final int ORACLE_GUIDE = 32281;
	private static final int ORACLE_GUIDE2 = 32278;
	private static final int ORACLE_GUIDE3 = 32280;

	// Mobs
	private static final int GK1 = 22275;
	private static final int GK2 = 22277;
	private static final int KECHICAPTAIN = 22307;
	private static final int TOURMALINE = 22292;

	private static final int KECHI = 25532;
	private static final int DOLPH = 22299;
	private static final int DARNEL = 25531;
	private static final int TEROD = 22301;
	private static final int WEYLIN = 22298;
	private static final int GUARDIAN = 22303;
	private static final int GUARDIAN2 = 22304;

	private static final int KechisCaptain1 = 22306;
	private static final int KechisCaptain2 = 22307;
	private static final int KechisCaptain3 = 22416;
	private static final int BurningIris = 22418;
	private static final int FlameIris = 22419;
	private static final int BrimstoneIris = 22420;
	private static final int GatekeeperoftheSquare = 22276;
	private static final int GatekeeperofFire = 22278;
	private static final int RodoKnight = 22280;
	private static final int PlazaCaiman = 22281;
	private static final int ChromaticDetainee1 = 22282;
	private static final int ChromaticDetainee2 = 22284;
	private static final int PlazaGaviel = 22286;
	private static final int CrystallineUnicorn = 22287;
	private static final int EmeraldBoar = 22288;
	private static final int PlazaHelm = 22289;
	private static final int Spinel = 22293;
	private static final int ReefGolem = 22297;
	private static final int KechisGuard1 = 22309;
	private static final int KechisGuard2 = 22310;

	private static final int OG1 = 32274;
	private static final int OG2 = 32275;
	private static final int OG3 = 32276;
	private static final int OG4 = 32277;

	private static final int[] MOBLIST = new int[]
	{
		KechisCaptain1,
		KechisCaptain2,
		KechisCaptain3,
		BurningIris,
		FlameIris,
		BrimstoneIris,
		Spinel,
		ReefGolem,
		PlazaCaiman,
		ChromaticDetainee1,
		CrystallineUnicorn,
		EmeraldBoar,
		PlazaHelm
	};

	// Doors
	private static final int DOOR1 = 24220021;
	private static final int DOOR2 = 24220024;

	private static final int DOOR3 = 24220005;
	private static final int DOOR4 = 24220006;

	private static final int DOOR5 = 24220061;
	private static final int DOOR6 = 24220023;

	// -------- Start Coral Garden ------------

	private static final int TEARS = 25534;

	private static final int Garden_Stakato = 22313;
	private static final int Garden_Poison_Moth = 22314;
	private static final int Garden_Guard = 22315;
	private static final int Garden_Guardian_Tree = 22316;
	private static final int Garden_Castalia = 22317;

	private static final int CORAL_GARDEN_GATEWAY = 24220025; // Starting Room

	// --------- End Coral Garden ------------

	public class World
	{
		public int instanceId;
		public int status;
		public int killedCaptains;
		public int bosses;
		public Room OracleTriggered;
		public boolean OracleTriggeredRoom1 = true;
		public boolean OracleTriggeredRoom2 = true;
		public boolean OracleTriggeredRoom3 = true;
		public List<Integer> rewarded;
		public Room emeraldRoom;
		public Room steamRoom1;
		public Room steamRoom2;
		public Room steamRoom3;
		public Room steamRoom4;
		public Room SecretRoom1;
		public Room SecretRoom2;
		public Room SecretRoom3;
		public Room SecretRoom4;
		public Room DarnelRoom;
		public Room kechiRoom;
		public Room CoralGardenHall;
	}

	public class Room
	{
		public Map<NpcInstance, Boolean> npclist;
		public GCSArray<long[]> og;
	}

	private static TIntObjectHashMap<World> worlds = new TIntObjectHashMap<World>();

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

	public _1202_CrystalCaverns()
	{
		super(true);

		addStartNpc(ORACLE_GUIDE);
		addStartNpc(ORACLE_GUIDE3);

		addFirstTalkId(ORACLE_GUIDE2);
		addFirstTalkId(OG1);
		addFirstTalkId(OG2);
		addFirstTalkId(OG3);
		addFirstTalkId(OG4);

		addKillId(GK1);
		addKillId(GK2);
		addKillId(TEROD);
		addKillId(WEYLIN);
		addKillId(DOLPH);
		addKillId(DARNEL);
		addKillId(KECHI);
		addKillId(GUARDIAN);
		addKillId(GUARDIAN2);
		addKillId(TOURMALINE);
		addKillId(KECHICAPTAIN);

		addKillId(TEARS);
		addKillId(Garden_Stakato);
		addKillId(Garden_Poison_Moth);
		addKillId(Garden_Guard);
		addKillId(Garden_Guardian_Tree);
		addKillId(Garden_Castalia);

		addSkillUseId(OG1);
		addSkillUseId(OG2);
		addSkillUseId(OG3);
		addSkillUseId(OG4);

		addKillId(MOBLIST);
	}

	@Override
	public String onSkillUse(NpcInstance npc, Skill skill, QuestState qs)
	{
		World world = worlds.get(qs.getPlayer().getReflectionId());
		int skillId = skill.getId();
		int npcId = npc.getNpcId();
		if (npcId == OG2 && (skillId == 1217 || skillId == 1218 || skillId == 1011 || skillId == 1015 || skillId == 1401 || skillId == 5146))
		{
			if (!world.OracleTriggeredRoom1)
			{
				if (npc.getCurrentHp() == npc.getMaxHp())
				{
					world.OracleTriggeredRoom1 = true;
				}
				despawnNpcF(world);
			}
		}
		else if (npcId == OG3 && (skillId == 1217 || skillId == 1218 || skillId == 1011 || skillId == 1015 || skillId == 1401 || skillId == 5146))
		{
			if (!world.OracleTriggeredRoom2)
			{
				if (npc.getCurrentHp() == npc.getMaxHp())
				{
					world.OracleTriggeredRoom2 = true;
				}
				despawnNpcF(world);
			}
		}
		else if (npcId == OG4 && (skillId == 1217 || skillId == 1218 || skillId == 1011 || skillId == 1015 || skillId == 1401 || skillId == 5146))
		{
			if (!world.OracleTriggeredRoom3)
			{
				if (npc.getCurrentHp() == npc.getMaxHp())
				{
					world.OracleTriggeredRoom3 = true;
				}
				despawnNpcF(world);
			}
		}
		return null;
	}

	private void despawnNpcF(World world)
	{
		for (long[] list : world.OracleTriggered.og)
		{
			NpcInstance npc = GameObjectsStorage.getAsNpc(OG1);
			if (npc != null)
			{
				npc.decayMe();
			}
		}
	}

	@Override
	public String onFirstTalk(NpcInstance npc, Player player)
	{
		World world = worlds.get(player.getReflectionId());
		int npcId = npc.getNpcId();
		boolean maxHp = (npc.getCurrentHp() == npc.getMaxHp());
		Location teleto = null;
		boolean spawn_captain = false;

		// If some steam room is already completed, do not trigger mobs respawn again. Lol, people exploit it and cause double, triple, quad kechi.
		if (world.status > 20)
		{
			if (world.status >= 23 && (npcId == OG2 || npcId == OG3 || npcId == OG4))
			{
				player.teleToLocation(149743, 149986, -12141); // Room 4
				return null;
			}
			else if (world.status == 22 && (npcId == OG2 || npcId == OG3))
			{
				player.teleToLocation(150194, 152610, -12169); // Room 3
				return null;
			}
			else if (world.status == 21 && npcId == OG2)
			{
				player.teleToLocation(147529, 152587, -12169); // Room 2
				return null;
			}
		}

		switch (npcId)
		{
		case ORACLE_GUIDE2:
		{
			Reflection r = ReflectionManager.getInstance().get(world.instanceId);
			r.openDoor(DOOR5);
			r.openDoor(DOOR6);
			break;
		}
		case OG1:
			spawn_captain = true;
			break;
		case OG2:
			if (world.OracleTriggeredRoom1 && maxHp)
			{
				runSteamRoom2(world);
				teleto = new Location(147529, 152587, -12169);
			}
			else
			{
				spawn_captain = true;
			}
			break;
		case OG3:
			if (world.OracleTriggeredRoom2 && maxHp)
			{
				runSteamRoom3(world);
				teleto = new Location(150194, 152610, -12169);
			}
			else
			{
				spawn_captain = true;
			}
			break;
		case OG4:
			if (world.OracleTriggeredRoom3 && maxHp)
			{
				runSteamRoom4(world);
				teleto = new Location(149743, 149986, -12141);
			}
			else
			{
				spawn_captain = true;
			}
			break;
		default:
			break;
		}

		if (spawn_captain && Rnd.chance(50))
		{
			NpcInstance captain = addSpawnToInstance(KechisCaptain3, new Location(npc.getX() - 60, npc.getY(), npc.getZ(), 32116), 0, world.instanceId);
			captain.addStatFunc(new FuncMul(Stats.POWER_ATTACK, 0x30, this, 5));
			captain.addStatFunc(new FuncMul(Stats.MAGIC_ATTACK, 0x30, this, 5));
			captain.addStatFunc(new FuncMul(Stats.POWER_DEFENCE, 0x30, this, 5));
			captain.addStatFunc(new FuncMul(Stats.MAGIC_DEFENCE, 0x30, this, 5));
			captain.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, Rnd.get(1, 100));
		}

		if (teleto != null)
		{
			Party party = player.getParty();
			if (party != null)
			{
				for (Player partyMember : party.getMembers())
				{
					partyMember.teleToLocation(teleto);
				}
			}
			else
			{
				player.teleToLocation(teleto);
			}
		}

		return null;
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();

		if (event.equalsIgnoreCase("EnterEmeraldSteam"))
		{
			st.setState(STARTED);
			enterInstance(player, 1);
			return null;
		}
		else if (event.equalsIgnoreCase("EnterCoralGarden"))
		{
			st.setState(STARTED);
			enterInstance(player, 2);
			return null;
		}
		else if (event.equalsIgnoreCase("meet"))
		{
			int state = BaylorManager.canIntoBaylorLair(player);
			switch (state)
			{
			case 1:
			case 2:
				return "meetingNo.htm";
			case 4:
				return "meetingNoParty.htm";
			case 3:
				return "teleportOut.htm";
			default:
				break;
			}
			st.giveItems(PRISON_KEY, 1, false);
			BaylorManager.entryToBaylorLair(player);
			return "meeting.htm";
		}
		else if (event.equalsIgnoreCase("out"))
		{
			if (player.getParty() != null)
			{
				player.getParty().setReflection(null);
				for (Player pl : player.getParty().getMembers())
				{
					pl.teleToLocation(149361, 172327, -945, 0);
				}
			}
			else
			{
				player.teleToLocation(149361, 172327, -945, 0);
			}
			return null;
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		if (npcId == ORACLE_GUIDE3)
		{
			int state = BaylorManager.canIntoBaylorLair(st.getPlayer());
			switch (state)
			{
			case 1:
			case 2:
				return "meetingNo.htm";
			case 4:
				return "meetingNoParty.htm";
			case 3:
				return "teleportOut.htm";
			case 0:
				return "meetingOk.htm";
			default:
				break;
			}
			return "32280.htm";
		}
		return null;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		World world = worlds.get(npc.getReflectionId());
		if (world == null)
		{
			return null;
		}

		switch (world.status)
		{
		case 0:
			if (npcId == GK1)
			{
				st.dropItem(npc, BLUE_CORAL_KEY, 1);
				runEmerald(world);
			}
			else if (npcId == GK2)
			{
				st.dropItem(npc, RED_CORAL_KEY, 1);
				runSteamRoom1(world);
			}
			break;
		case 1:
			if (checkKillProgress(npc, world.emeraldRoom))
			{
				world.status = 2;
				addSpawnToInstance(TOURMALINE, new Location(147937, 145886, -12256, 0), 0, world.instanceId);
			}
			break;
		case 2:
			if (npcId == TOURMALINE)
			{
				world.status = 3;
				addSpawnToInstance(TEROD, new Location(147191, 146855, -12266, 0), 0, world.instanceId);
			}
			break;
		case 3:
			if (npcId == TEROD)
			{
				world.status = 4;
				addSpawnToInstance(TOURMALINE, new Location(144840, 143792, -11991, 0), 0, world.instanceId);
			}
			break;
		case 4:
			if (npcId == TOURMALINE)
			{
				world.status = 5;
				addSpawnToInstance(DOLPH, new Location(142067, 145364, -12036, 0), 0, world.instanceId);
			}
			break;
		case 5:
			if (npcId == DOLPH)
			{
				world.status = 6;
			}
			break;
		case 20:
			if (npcId == KechisCaptain3)
			{
				world.killedCaptains += 1;
			}
			if (world.killedCaptains == 3)
			{
				for (NpcInstance mob : world.steamRoom1.npclist.keySet())
				{
					if (mob != null)
					{
						mob.decayMe();
					}
				}
				runSteamRoom1Oracle(world);
			}
			else if (checkKillProgress(npc, world.steamRoom1))
			{
				runSteamRoom1Oracle(world);
			}
			break;
		case 21:
			if (npcId == KechisCaptain1)
			{
				world.killedCaptains += 1;
			}
			if (world.killedCaptains == 3)
			{
				for (NpcInstance mob : world.steamRoom2.npclist.keySet())
				{
					if (mob != null)
					{
						mob.decayMe();
					}
				}
				runSteamRoom2Oracle(world);
			}
			else if (checkKillProgress(npc, world.steamRoom2))
			{
				runSteamRoom2Oracle(world);
			}
			break;
		case 22:
			if (npcId == KechisCaptain2)
			{
				world.killedCaptains += 1;
			}
			if (world.killedCaptains == 3)
			{
				for (NpcInstance mob : world.steamRoom3.npclist.keySet())
				{
					if (mob != null)
					{
						mob.decayMe();
					}
				}
				runSteamRoom3Oracle(world);
			}
			else if (checkKillProgress(npc, world.steamRoom3))
			{
				runSteamRoom3Oracle(world);
			}
			break;
		case 23:
			if (npcId == KechisCaptain2)
			{
				world.killedCaptains += 1;
			}
			if (world.killedCaptains == 3)
			{
				for (NpcInstance mob : world.steamRoom4.npclist.keySet())
				{
					if (mob != null)
					{
						mob.decayMe();
					}
				}
				addSpawnToInstance(ORACLE_GUIDE2, new Location(152243, 150152, -12141, 0), 0, world.instanceId);
				runKechi(world, npc);
			}
			else if (checkKillProgress(npc, world.steamRoom4))
			{
				addSpawnToInstance(ORACLE_GUIDE2, new Location(152243, 150152, -12141, 0), 0, world.instanceId);
				runKechi(world, npc);
			}
			break;
		case 30:
			if (checkKillProgress(npc, world.CoralGardenHall))
			{
				runCoralGardenGolems(world);
			}
			break;
		}
		if (world.status >= 1 && world.status <= 6)
		{
			if (npcId == DOLPH || npcId == TEROD || npcId == WEYLIN || npcId == GUARDIAN || npcId == GUARDIAN2)
			{
				world.bosses = world.bosses - 1;
				if (world.bosses == 0)
				{
					runDarnel(world);
				}
			}
		}
		long seedsCount = (long) (1 * Config.RATE_DROP_ITEMS);
		switch (npcId)
		{
		case DARNEL:
			addSpawnToInstance(ORACLE_GUIDE3, new Location(152760, 145944, -12584, 0), 0, world.instanceId);
			st.giveItems(Rnd.chance(50) ? WHITE_SEED_OF_EVIL_SHARD : BLACK_SEED_OF_EVIL_SHARD, seedsCount);
			break;
		case KECHI:
			addSpawnToInstance(ORACLE_GUIDE3, new Location(154072, 149528, -12152, 0), 0, world.instanceId);
			st.giveItems(Rnd.chance(50) ? WHITE_SEED_OF_EVIL_SHARD : BLACK_SEED_OF_EVIL_SHARD, seedsCount);
			npc.getReflection().startCollapseTimer(5 * 60 * 1000L);
			break;
		case TEARS:
			addSpawnToInstance(ORACLE_GUIDE3, new Location(144307, 154419, -11857, 0), 0, world.instanceId);
			st.giveItems(Rnd.chance(50) ? WHITE_SEED_OF_EVIL_SHARD : BLACK_SEED_OF_EVIL_SHARD, seedsCount);
			break;
		default:
			break;
		}
		return null;
	}

	private void enterInstance(Player player, int type)
	{
		Reflection r = player.getActiveReflection();
		if (r != null)
		{
			if (player.canReenterInstance(INCSTANCED_ZONE_ID))
			{
				player.teleToLocation(r.getTeleportLoc(), r);
			}
		}
		else if (player.canEnterInstance(INCSTANCED_ZONE_ID))
		{
			Reflection ref = ReflectionUtils.enterReflection(player, INCSTANCED_ZONE_ID);
			World world = new World();
			world.rewarded = new ArrayList<Integer>();
			world.instanceId = ref.getId();
			world.bosses = 5;
			worlds.put(ref.getId(), world);
			for (Player member : player.getParty().getMembers())
			{
				if (member != player)
				{
					newQuestState(member, STARTED);
				}
			}

			if (type == 1)
			{
				runEmeraldAndSteamFirstRoom(world);
				ref.openDoor(DOOR1);
				ref.openDoor(DOOR2);
			}
			else if (type == 2)
			{
				runCoralGardenHall(world);
				ref.openDoor(CORAL_GARDEN_GATEWAY);
			}
		}
	}

	// -------- Start Emerald Steam -----------

	private void runEmeraldAndSteamFirstRoom(World world)
	{
		world.status = 0;
		addSpawnToInstance(GK1, new Location(148206, 149486, -12140, 32308), 0, world.instanceId);
		addSpawnToInstance(GK2, new Location(148203, 151093, -12140, 31100), 0, world.instanceId);
		addSpawnToInstance(GatekeeperofFire, new Location(147182, 151091, -12140, 32470), 0, world.instanceId);
		addSpawnToInstance(GatekeeperoftheSquare, new Location(147193, 149487, -12140, 32301), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(144289, 150685, -12140, 49394), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(144335, 149846, -12140, 38440), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(144188, 149230, -12140, 13649), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(144442, 149234, -12140, 19083), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(145949, 149477, -12140, 32941), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(146792, 149545, -12140, 40543), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(145441, 151178, -12140, 36154), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(146735, 150981, -12140, 25702), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(144115, 151086, -12140, 51316), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(145009, 149475, -12140, 31393), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(146952, 151228, -12140, 38140), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(145499, 149614, -12140, 38775), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(144308, 151420, -12140, 48469), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(144214, 149514, -12140, 15265), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(145358, 150956, -12140, 26056), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(145780, 151225, -12140, 39635), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(146644, 151325, -12140, 42053), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(146459, 150968, -12140, 11232), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(145699, 149508, -12140, 34774), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(145397, 149262, -12140, 16218), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(145750, 150944, -12140, 30099), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(144421, 151087, -12140, 21857), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee1, new Location(144154, 150261, -12140, 39283), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(146359, 149355, -12140, 23301), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(147819, 150915, -12140, 25958), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(146507, 149650, -12140, 50727), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(146542, 149262, -12140, 18038), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(147918, 149636, -12140, 36636), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(147643, 149334, -12140, 29038), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(146491, 151144, -12140, 28915), 0, world.instanceId);
		addSpawnToInstance(ChromaticDetainee2, new Location(147783, 151257, -12140, 37421), 0, world.instanceId);
	}

	private void runEmerald(World world)
	{
		world.status = 1;
		runSecretRoom1(world);
		runSecretRoom2(world);
		runSecretRoom3(world);
		runSecretRoom4(world);
		world.emeraldRoom = new Room();
		world.emeraldRoom.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(Spinel, new Location(144158, 143424, -11957, 29058), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(144044, 143448, -11949, 27778), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(142580, 143091, -11872, 7458), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(144013, 142556, -11890, 26562), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(144138, 143833, -12003, 35900), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(143759, 143251, -11916, 24854), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(142588, 144861, -12011, 47303), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(142094, 144289, -11940, 38219), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(142076, 143774, -11883, 48980), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(142653, 143778, -11915, 9493), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(143308, 144206, -11992, 37435), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(143367, 145048, -12034, 16679), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(143597, 145175, -12033, 15198), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(142998, 143444, -11901, 12969), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(144089, 143956, -12014, 38107), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ChromaticDetainee1, new Location(144394, 147711, -12141, 453), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ChromaticDetainee1, new Location(145165, 147331, -12128, 29058), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(EmeraldBoar, new Location(145103, 146978, -12069, 23007), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(EmeraldBoar, new Location(144732, 147205, -12089, 18082), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(EmeraldBoar, new Location(143859, 146571, -12036, 9955), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(EmeraldBoar, new Location(142857, 145851, -12038, 19739), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(CrystallineUnicorn, new Location(144917, 146979, -12057, 26485), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(CrystallineUnicorn, new Location(144240, 146965, -12070, 1552), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(CrystallineUnicorn, new Location(144238, 146428, -12034, 16770), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(CrystallineUnicorn, new Location(143937, 146699, -12039, 32559), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(CrystallineUnicorn, new Location(144711, 146645, -12036, 29130), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(CrystallineUnicorn, new Location(144407, 146617, -12035, 7391), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaCaiman, new Location(144502, 146926, -12050, 12678), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(143816, 146656, -12039, 10414), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(143753, 146466, -12037, 7091), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(143608, 145754, -12036, 48284), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(143240, 145454, -12037, 39901), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(142606, 144827, -12009, 41533), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(142996, 144395, -11994, 64068), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(142732, 145762, -12038, 54764), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(143312, 145772, -12039, 45440), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(144369, 142957, -11890, 29784), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(144954, 143832, -11976, 37294), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(145367, 143588, -11845, 30279), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(145099, 143959, -11942, 29249), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(145241, 143436, -11883, 26892), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(147631, 145941, -12236, 55236), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(148004, 146336, -12283, 44613), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(149430, 145844, -12336, 43268), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(149467, 145353, -12303, 19506), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(147850, 144090, -12227, 9285), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(147723, 143307, -12227, 49819), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(149033, 143103, -12229, 31151), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaCaiman, new Location(148920, 143400, -12238, 34526), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ChromaticDetainee1, new Location(148653, 142813, -12231, 28363), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ChromaticDetainee1, new Location(147485, 143590, -12227, 62369), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaCaiman, new Location(148426, 145886, -12296, 42769), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(148658, 144958, -12282, 49451), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(148648, 144098, -12240, 44077), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(149156, 143936, -12238, 42632), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(148388, 143092, -12227, 11429), 0, world.instanceId);
		world.emeraldRoom.npclist.put(newNpc, false);
	}

	private void runSecretRoom1(World world)
	{
		world.SecretRoom1 = new Room();
		world.SecretRoom1.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(EmeraldBoar, new Location(143114, 140027, -11888, 15025), 0, world.instanceId);
		world.SecretRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(EmeraldBoar, new Location(142173, 140973, -11888, 55698), 0, world.instanceId);
		world.SecretRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(143210, 140577, -11888, 17164), 0, world.instanceId);
		world.SecretRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(142638, 140107, -11888, 6571), 0, world.instanceId);
		world.SecretRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(142547, 140938, -11888, 48556), 0, world.instanceId);
		world.SecretRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(WEYLIN, new Location(142690, 140479, -11887, 7663), 0, world.instanceId);
		world.SecretRoom1.npclist.put(newNpc, false);
		// Blacksmith
		addSpawnToInstance(32359, new Location(142110, 139896, -11888, 8033), 0, world.instanceId);
	}

	private void runSecretRoom2(World world)
	{
		world.SecretRoom2 = new Room();
		world.SecretRoom2.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(GUARDIAN, new Location(146272, 141484, -11888, 15025), 0, world.instanceId);
		world.SecretRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(146870, 140906, -11888, 23832), 0, world.instanceId);
		world.SecretRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaHelm, new Location(146833, 141741, -11888, 37869), 0, world.instanceId);
		world.SecretRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(EmeraldBoar, new Location(146591, 142040, -11888, 34969), 0, world.instanceId);
		world.SecretRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(EmeraldBoar, new Location(145744, 141146, -11888, 12266), 0, world.instanceId);
		world.SecretRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(CrystallineUnicorn, new Location(146044, 142006, -11888, 38094), 0, world.instanceId);
		world.SecretRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(CrystallineUnicorn, new Location(146276, 140847, -11888, 22210), 0, world.instanceId);
		world.SecretRoom2.npclist.put(newNpc, false);
	}

	private void runSecretRoom3(World world)
	{
		world.SecretRoom3 = new Room();
		world.SecretRoom3.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(Spinel, new Location(144868, 143439, -12816, 5588), 0, world.instanceId);
		world.SecretRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(145369, 144040, -12816, 42939), 0, world.instanceId);
		world.SecretRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(22294, new Location(145315, 143436, -12813, 27523), 0, world.instanceId);
		world.SecretRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Spinel, new Location(145043, 143854, -12815, 56775), 0, world.instanceId);
		world.SecretRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(145355, 143729, -12815, 63378), 0, world.instanceId);
		world.SecretRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ReefGolem, new Location(145126, 143697, -12815, 33214), 0, world.instanceId);
		world.SecretRoom3.npclist.put(newNpc, false);
	}

	private void runSecretRoom4(World world)
	{
		world.SecretRoom4 = new Room();
		world.SecretRoom4.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(ChromaticDetainee1, new Location(150930, 141920, -12116, 21592), 0, world.instanceId);
		world.SecretRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(ChromaticDetainee1, new Location(150212, 141905, -12116, 7201), 0, world.instanceId);
		world.SecretRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaGaviel, new Location(150661, 141859, -12116, 15452), 0, world.instanceId);
		world.SecretRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(PlazaGaviel, new Location(150411, 141935, -12116, 13445), 0, world.instanceId);
		world.SecretRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(RodoKnight, new Location(150280, 142241, -12116, 9672), 0, world.instanceId);
		world.SecretRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(RodoKnight, new Location(150738, 142110, -12115, 14903), 0, world.instanceId);
		world.SecretRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(GUARDIAN2, new Location(150564, 142231, -12115, 4836), 0, world.instanceId);
		world.SecretRoom4.npclist.put(newNpc, false);
	}

	private void runDarnel(World world)
	{
		world.status = 7;
		world.DarnelRoom = new Room();
		world.DarnelRoom.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(DARNEL, new Location(152759, 145949, -12588, 21592), 0, world.instanceId);
		world.DarnelRoom.npclist.put(newNpc, false);
		Reflection r = ReflectionManager.getInstance().get(world.instanceId);
		r.openDoor(DOOR3);
		r.openDoor(DOOR4);
	}

	private void runSteamRoom1(World world)
	{
		world.status = 20;
		world.killedCaptains = 0;
		world.steamRoom1 = new Room();
		world.steamRoom1.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(KechisCaptain1, new Location(148755, 152573, -12170, 65497), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain3, new Location(146862, 152734, -12169, 42584), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain3, new Location(146014, 152607, -12172, 23694), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain3, new Location(145346, 152585, -12172, 31490), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(146972, 152421, -12172, 28476), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(145714, 152821, -12172, 58705), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(145336, 152805, -12172, 39590), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(146530, 152762, -12172, 60307), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(145941, 152412, -12172, 14182), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(146243, 152807, -12172, 38832), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(145152, 152410, -12172, 21338), 0, world.instanceId);
		world.steamRoom1.npclist.put(newNpc, false);
	}

	private void runSteamRoom1Oracle(World world)
	{
		world.OracleTriggeredRoom1 = false;
		world.OracleTriggered = new Room();
		world.OracleTriggered.og = new GCSArray<long[]>();
		NpcInstance NewNpc1;
		NpcInstance NewNpc2;
		NpcInstance NewNpc3;
		NpcInstance NewNpc4;
		NewNpc1 = addSpawnToInstance(OG1, new Location(147090, 152505, -12169, 31613), 0, world.instanceId);
		NewNpc1.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc1.getObjectId()
		});
		NewNpc2 = addSpawnToInstance(OG2, new Location(147090, 152575, -12169, 31613), 0, world.instanceId);
		NewNpc2.setCurrentHp(1, false, true);
		NewNpc3 = addSpawnToInstance(OG1, new Location(147090, 152645, -12169, 31613), 0, world.instanceId);
		NewNpc3.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc3.getObjectId()
		});
		NewNpc4 = addSpawnToInstance(OG1, new Location(147090, 152715, -12169, 31613), 0, world.instanceId);
		NewNpc4.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc4.getObjectId()
		});
	}

	private void runSteamRoom2(World world)
	{
		world.status = 21;
		world.killedCaptains = 0;
		world.steamRoom2 = new Room();
		world.steamRoom2.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(148815, 152804, -12172, 44197), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(149414, 152478, -12172, 25651), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(148482, 152388, -12173, 32189), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(147908, 152861, -12172, 61173), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(147835, 152484, -12172, 7781), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(148176, 152627, -12173, 3336), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(148813, 152453, -12172, 50373), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(149233, 152773, -12172, 36765), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain1, new Location(149550, 152718, -12172, 37301), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain1, new Location(148881, 152601, -12172, 24054), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain1, new Location(148183, 152486, -12172, 5289), 0, world.instanceId);
		world.steamRoom2.npclist.put(newNpc, false);
	}

	private void runSteamRoom2Oracle(World world)
	{
		world.OracleTriggeredRoom2 = false;
		world.OracleTriggered = new Room();
		world.OracleTriggered.og = new GCSArray<long[]>();
		NpcInstance NewNpc1;
		NpcInstance NewNpc2;
		NpcInstance NewNpc3;
		NpcInstance NewNpc4;
		NewNpc1 = addSpawnToInstance(OG1, new Location(149783, 152505, -12169, 31613), 0, world.instanceId);
		NewNpc1.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc1.getObjectId()
		});
		NewNpc2 = addSpawnToInstance(OG1, new Location(149783, 152575, -12169, 31613), 0, world.instanceId);
		NewNpc2.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc2.getObjectId()
		});
		NewNpc3 = addSpawnToInstance(OG3, new Location(149783, 152645, -12169, 31613), 0, world.instanceId);
		NewNpc3.setCurrentHp(1, false, true);
		NewNpc4 = addSpawnToInstance(OG1, new Location(149783, 152715, -12169, 31613), 0, world.instanceId);
		NewNpc4.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc4.getObjectId()
		});
	}

	private void runSteamRoom3(World world)
	{
		world.status = 22;
		world.killedCaptains = 0;
		world.steamRoom3 = new Room();
		world.steamRoom3.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(FlameIris, new Location(150751, 152430, -12172, 29190), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(150613, 152778, -12172, 19574), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(151242, 152832, -12172, 40116), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(151473, 152656, -12172, 28951), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(151090, 152401, -12172, 1909), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(151625, 152372, -12172, 31372), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(152283, 152577, -12172, 15323), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(151906, 152699, -12172, 49605), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(151134, 152626, -12172, 59956), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(152105, 152766, -12172, 59956), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(150416, 152567, -12173, 53744), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain2, new Location(150689, 152618, -12172, 34932), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain2, new Location(151329, 152558, -12172, 55102), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain2, new Location(152054, 152557, -12172, 40959), 0, world.instanceId);
		world.steamRoom3.npclist.put(newNpc, false);
	}

	private void runSteamRoom3Oracle(World world)
	{
		world.OracleTriggeredRoom3 = false;
		world.OracleTriggered = new Room();
		world.OracleTriggered.og = new GCSArray<long[]>();
		NpcInstance NewNpc1;
		NpcInstance NewNpc2;
		NpcInstance NewNpc3;
		NpcInstance NewNpc4;
		NewNpc1 = addSpawnToInstance(OG1, new Location(152461, 152505, -12169, 31613), 0, world.instanceId);
		NewNpc1.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc1.getObjectId()
		});
		NewNpc2 = addSpawnToInstance(OG1, new Location(152461, 152575, -12169, 31613), 0, world.instanceId);
		NewNpc2.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc2.getObjectId()
		});
		NewNpc3 = addSpawnToInstance(OG1, new Location(152461, 152645, -12169, 31613), 0, world.instanceId);
		NewNpc3.setCurrentHp(1, false, true);
		world.OracleTriggered.og.add(new long[]
		{
			NewNpc3.getObjectId()
		});
		NewNpc4 = addSpawnToInstance(OG4, new Location(152461, 152715, -12169, 31613), 0, world.instanceId);
		NewNpc4.setCurrentHp(1, false, true);
	}

	private void runSteamRoom4(World world)
	{
		world.status = 23;
		world.killedCaptains = 0;
		world.steamRoom4 = new Room();
		world.steamRoom4.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(KechisCaptain2, new Location(150454, 149976, -12173, 28435), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain2, new Location(151186, 150140, -12173, 37604), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisCaptain2, new Location(151718, 149805, -12172, 26672), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(150755, 149852, -12173, 31074), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BurningIris, new Location(150457, 150173, -12172, 34736), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(151649, 150194, -12172, 35198), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(151254, 149876, -12172, 26433), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(BrimstoneIris, new Location(151819, 150010, -12172, 33680), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(150852, 150030, -12173, 32002), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(FlameIris, new Location(150031, 149797, -12172, 16560), 0, world.instanceId);
		world.steamRoom4.npclist.put(newNpc, false);
	}

	private void runKechi(World world, NpcInstance captain)
	{
		world.status = 24;
		world.kechiRoom = new Room();
		world.kechiRoom.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(KechisGuard1, new Location(154409, 149680, -12151, 8790), 0, world.instanceId);
		world.kechiRoom.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(KechisGuard2, new Location(154165, 149734, -12159, 4087), 0, world.instanceId);
		world.kechiRoom.npclist.put(newNpc, false);
		if (captain.getReflection().getAllByNpcId(KECHI, true).isEmpty())// Fix for Second KECHI exploit
		{
			newNpc = addSpawnToInstance(KECHI, new Location(154069, 149525, -12158, 51165), 0, world.instanceId);
			world.kechiRoom.npclist.put(newNpc, false);
		}
	}

	// --------- End Emerald Steam ------------

	// -------- Start Coral Garden ------------

	private void runCoralGardenHall(World world)
	{
		world.status = 30;
		world.CoralGardenHall = new Room();
		world.CoralGardenHall.npclist = new HashMap<NpcInstance, Boolean>();
		NpcInstance newNpc;
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(141740, 150330, -11817, 6633), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(141233, 149960, -11817, 49187), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(141866, 150723, -11817, 13147), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(142276, 151105, -11817, 7823), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(142102, 151640, -11817, 20226), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(142093, 152269, -11817, 3445), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(141569, 152994, -11817, 22617), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(141083, 153210, -11817, 28405), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(140469, 152415, -11817, 41700), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(140180, 151635, -11817, 45729), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(140490, 151126, -11817, 54857), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(140930, 150269, -11817, 17591), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(141203, 150210, -11817, 64400), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(141360, 150357, -11817, 9093), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(142255, 151694, -11817, 14655), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(141920, 151124, -11817, 8191), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(141911, 152734, -11817, 21600), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(141032, 152929, -11817, 32791), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(140317, 151837, -11817, 43864), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(140183, 151939, -11817, 25981), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guardian_Tree, new Location(140944, 152724, -11817, 12529), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guardian_Tree, new Location(141301, 154428, -11817, 17207), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guardian_Tree, new Location(142499, 154437, -11817, 65478), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(142664, 154612, -11817, 8498), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(142711, 154137, -11817, 28756), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Stakato, new Location(142705, 154378, -11817, 26017), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(141605, 154490, -11817, 31128), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(141115, 154674, -11817, 28781), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Stakato, new Location(141053, 154431, -11817, 46546), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Stakato, new Location(141423, 154130, -11817, 60888), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(142249, 154395, -11817, 64346), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(141530, 152803, -11817, 53953), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(142020, 152272, -11817, 55995), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(142134, 151667, -11817, 52687), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(141958, 151021, -11817, 42965), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(140979, 150233, -11817, 38924), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(140509, 150983, -11817, 23466), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(140151, 151410, -11817, 23661), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Castalia, new Location(140446, 152370, -11817, 13192), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Stakato, new Location(140249, 152133, -11817, 41391), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Stakato, new Location(140664, 152655, -11817, 8720), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Stakato, new Location(141610, 152988, -11817, 57460), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Poison_Moth, new Location(141189, 154197, -11817, 16792), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(142315, 154368, -11817, 30260), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guard, new Location(142577, 154774, -11817, 45981), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Stakato, new Location(141338, 153089, -11817, 26387), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
		newNpc = addSpawnToInstance(Garden_Guardian_Tree, new Location(140800, 150707, -11817, 55884), 0, world.instanceId);
		world.CoralGardenHall.npclist.put(newNpc, false);
	}

	private void runCoralGardenGolems(World world)
	{
		world.status = 31;
		addSpawnToInstance(TEARS, new Location(144298, 154420, -11854, 63371), 0, world.instanceId); // Tears
		addSpawnToInstance(32328, new Location(140547, 151670, -11813, 32767), 0, world.instanceId);
		addSpawnToInstance(32328, new Location(141941, 151684, -11813, 63371), 0, world.instanceId);
	}

	// ----------- End Coral Garden ------------

	private boolean checkKillProgress(NpcInstance npc, Room room)
	{
		if (room.npclist.containsKey(npc))
		{
			room.npclist.put(npc, true);
		}
		for (boolean value : room.npclist.values())
		{
			if (!value)
			{
				return false;
			}
		}
		return true;
	}
}