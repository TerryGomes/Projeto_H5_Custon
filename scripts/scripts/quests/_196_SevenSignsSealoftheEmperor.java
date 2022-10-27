package quests;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExStartScenePlayer;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;
import l2mv.gameserver.utils.ReflectionUtils;

public class _196_SevenSignsSealoftheEmperor extends Quest implements ScriptFile
{
	// NPCs
	private static int IasonHeine = 30969;
	private static int MerchantofMammon = 32584;
	private static int PromiseofMammon = 32585;
	private static int Shunaiman = 32586;
	private static int Leon = 32587;
	private static int DisciplesGatekeeper = 32657;
	private static int CourtMagician = 32598;
	// private static int EmperorsSealDevice = 27384;
	private static int Wood = 32593;

	private NpcInstance MerchantofMammonSpawn;

	// ITEMS
	private static int ElmoredenHolyWater = 13808;
	private static int CourtMagiciansMagicStaff = 13809;
	private static int SealOfBinding = 13846;
	private static int SacredSwordofEinhasad = 15310;

	// Doors
	private static final int door11 = 17240111;

	private static final int izId = 112;

	public _196_SevenSignsSealoftheEmperor()
	{
		super(false);

		addStartNpc(IasonHeine);
		addTalkId(IasonHeine, MerchantofMammon, PromiseofMammon, Shunaiman, Leon, DisciplesGatekeeper, CourtMagician, Wood);
		addQuestItem(ElmoredenHolyWater, CourtMagiciansMagicStaff, SealOfBinding, SacredSwordofEinhasad);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		Reflection ref = player.getReflection();

		if (event.equalsIgnoreCase("iasonheine_q196_1d.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("iasonheine_q196_2.htm"))
		{
			if (GameObjectsStorage.getAllByNpcId(MerchantofMammon, false).isEmpty())
			{
				MerchantofMammonSpawn = st.addSpawn(MerchantofMammon, 109763, 219944, -3512, 16384, 0, 120 * 1000);
				Functions.npcSay(MerchantofMammonSpawn, "Who dares summon the Merchant of Mammon?!");
			}
		}
		else if (event.equalsIgnoreCase("merchantofmammon_q196_2.htm"))
		{
			if (MerchantofMammonSpawn != null)
			{
				MerchantofMammonSpawn.deleteMe();
				MerchantofMammonSpawn = null;
			}
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("teleport_instance"))
		{
			if ((st.getCond() == 3 || st.getCond() == 4))
			{
				enterInstance(player);
			}
			else
			{
				player.sendMessage("You can only access the Necropolis of Dawn while carrying Seal of the Emperor quest.");
			}
			return null;
		}
		else if (event.equalsIgnoreCase("collapse_instance"))
		{
			ref.collapse();
			htmltext = "leon_q196_1.htm";
		}
		else if (event.equalsIgnoreCase("shunaiman_q196_2.htm"))
		{
			st.setCond(4);
			st.playSound(SOUND_MIDDLE);
			player.sendPacket(new SystemMessage(SystemMessage.BY_USING_THE_SKILL_OF_EINHASAD_S_HOLY_SWORD_DEFEAT_THE_EVIL_LILIMS));
			player.sendPacket(new SystemMessage(SystemMessage.BY_USING_THE_HOLY_WATER_OF_EINHASAD_OPEN_THE_DOOR_POSSESSED_BY_THE_CURSE_OF_FLAMES));
			st.giveItems(SacredSwordofEinhasad, 1);
			st.giveItems(ElmoredenHolyWater, 1);
		}
		else if (event.equalsIgnoreCase("courtmagician_q196_2.htm"))
		{
			st.playSound(SOUND_ITEMGET);
			st.giveItems(CourtMagiciansMagicStaff, 1);
			player.sendPacket(new SystemMessage(SystemMessage.BY_USING_THE_COURT_MAGICIAN_S_MAGIC_STAFF_OPEN_THE_DOOR_ON_WHICH_THE_MAGICIAN_S_BARRIER_IS));
		}
		else if (event.equalsIgnoreCase("free_anakim"))
		{
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ_SEALING_EMPEROR_1ST);
			player.sendPacket(new SystemMessage(SystemMessage.IN_ORDER_TO_HELP_ANAKIM_ACTIVATE_THE_SEALING_DEVICE_OF_THE_EMPEROR_WHO_IS_POSSESED_BY_THE_EVIL));
			ref.openDoor(door11);
			ThreadPoolManager.getInstance().schedule(new SpawnLilithRoom(ref), 17000);
			return null;
		}
		else if (event.equalsIgnoreCase("shunaiman_q196_4.htm"))
		{
			st.setCond(5);
			st.playSound(SOUND_MIDDLE);
			st.takeItems(SealOfBinding, -1);
			st.takeItems(ElmoredenHolyWater, -1);
			st.takeItems(CourtMagiciansMagicStaff, -1);
			st.takeItems(SacredSwordofEinhasad, -1);
		}
		else if (event.equalsIgnoreCase("leon_q196_2.htm"))
		{
			player.getReflection().collapse();
		}
		else if (event.equalsIgnoreCase("iasonheine_q196_6.htm"))
		{
			st.setCond(6);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("wood_q196_2.htm"))
		{
			if (player.getBaseClassId() == player.getActiveClassId())
			{
				st.addExpAndSp(25000000, 2500000);
				st.setState(COMPLETED);
				st.exitCurrentQuest(false);
				st.playSound(SOUND_FINISH);
			}
			else
			{
				return "subclass_forbidden.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		String htmltext = "noquest";
		if (npcId == IasonHeine)
		{
			QuestState qs = player.getQuestState(_195_SevenSignsSecretRitualofthePriests.class);
			switch (cond)
			{
			case 0:
				if (player.getLevel() >= 79 && qs != null && qs.isCompleted())
				{
					htmltext = "iasonheine_q196_1.htm";
				}
				else
				{
					htmltext = "iasonheine_q196_0.htm";
					st.exitCurrentQuest(true);
				}
				break;
			case 1:
				htmltext = "iasonheine_q196_1a.htm";
				break;
			case 2:
				st.setCond(3);
				st.playSound(SOUND_MIDDLE);
				htmltext = "iasonheine_q196_3.htm";
				break;
			case 3:
			case 4:
				htmltext = "iasonheine_q196_4.htm";
				break;
			case 5:
				htmltext = "iasonheine_q196_5.htm";
				break;
			case 6:
				htmltext = "iasonheine_q196_6a.htm";
				break;
			default:
				break;
			}
		}
		else if (npcId == MerchantofMammon)
		{
			if (cond == 1 && MerchantofMammonSpawn != null)
			{
				htmltext = "merchantofmammon_q196_1.htm";
			}
			else
			{
				htmltext = "merchantofmammon_q196_0.htm";
			}
		}
		else if (npcId == Shunaiman)
		{
			if (cond == 3)
			{
				htmltext = "shunaiman_q196_1.htm";
			}
			else if (cond == 4 && st.getQuestItemsCount(SealOfBinding) >= 4)
			{
				htmltext = "shunaiman_q196_3.htm";
			}
			else if (cond == 4 && st.getQuestItemsCount(SealOfBinding) < 4)
			{
				htmltext = "shunaiman_q196_3a.htm";
			}
			else if (cond == 5)
			{
				htmltext = "shunaiman_q196_4a.htm";
			}
		}
		else if (npcId == CourtMagician)
		{
			if (cond == 4 && st.getQuestItemsCount(CourtMagiciansMagicStaff) < 1)
			{
				htmltext = "courtmagician_q196_1.htm";
			}
			else
			{
				htmltext = "courtmagician_q196_1a.htm";
			}
		}
		else if (npcId == DisciplesGatekeeper)
		{
			if (cond == 4)
			{
				htmltext = "disciplesgatekeeper_q196_1.htm";
			}
		}
		else if (npcId == Leon)
		{
			if (cond == 5)
			{
				htmltext = "leon_q196_1.htm";
			}
			else
			{
				htmltext = "leon_q196_1a.htm";
			}
		}
		else if (npcId == Wood)
		{
			if (cond == 6)
			{
				htmltext = "wood_q196_1.htm";
			}
		}
		return htmltext;
	}

	private void enterInstance(Player player)
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
			ReflectionUtils.enterReflection(player, izId);
		}
	}

	private class SpawnLilithRoom extends RunnableImpl
	{
		Reflection _r;

		public SpawnLilithRoom(Reflection r)
		{
			_r = r;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_r != null)
			{
				_r.addSpawnWithoutRespawn(32715, new Location(-83175, 217021, -7504, 49151), 0); // Lilith
				_r.addSpawnWithoutRespawn(32718, new Location(-83179, 216479, -7504, 16384), 0); // Anakim
				_r.addSpawnWithoutRespawn(32717, new Location(-83222, 217055, -7504, 49151), 0); // liliths_shadow_guard_ssq
				_r.addSpawnWithoutRespawn(32716, new Location(-83127, 217056, -7504, 49151), 0); // liliths_agent_wizard_ssq
				_r.addSpawnWithoutRespawn(32719, new Location(-83227, 216443, -7504, 16384), 0); // anakims_holly_ssq
				_r.addSpawnWithoutRespawn(32721, new Location(-83179, 216432, -7504, 16384), 0); // anakims_sacred_ssq
				_r.addSpawnWithoutRespawn(32720, new Location(-83134, 216443, -7504, 16384), 0); // anakims_divine_ssq
			}
		}
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