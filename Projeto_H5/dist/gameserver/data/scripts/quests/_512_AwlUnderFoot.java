package quests;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.InstantZoneHolder;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.InstantZone;
import l2f.gameserver.utils.Location;

public class _512_AwlUnderFoot extends Quest implements ScriptFile
{
	private static final Map<Integer, Integer> wardens = new HashMap<>(9);

	private final static int INSTANCE_ZONE_ID = 13; // Castles Dungeon

	private final static int FragmentOfTheDungeonLeaderMark = 9798;
	private final static int RewardMarksCount = 1500;
	private final static int KnightsEpaulette = 9912;

	private static final Map<Integer, Prison> _prisons = new ConcurrentHashMap<Integer, Prison>();

	private static final int RhiannaTheTraitor = 25546;
	private static final int TeslaTheDeceiver = 25549;
	private static final int SoulHunterChakundel = 25552;

	private static final int DurangoTheCrusher = 25553;
	private static final int BrutusTheObstinate = 25554;
	private static final int RangerKarankawa = 25557;
	private static final int SargonTheMad = 25560;

	private static final int BeautifulAtrielle = 25563;
	private static final int NagenTheTomboy = 25566;
	private static final int JaxTheDestroyer = 25569;

	private static final int[] type1 = new int[]
	{
		RhiannaTheTraitor,
		TeslaTheDeceiver,
		SoulHunterChakundel
	};
	private static final int[] type2 = new int[]
	{
		DurangoTheCrusher,
		BrutusTheObstinate,
		RangerKarankawa,
		SargonTheMad
	};
	private static final int[] type3 = new int[]
	{
		BeautifulAtrielle,
		NagenTheTomboy,
		JaxTheDestroyer
	};

	public _512_AwlUnderFoot()
	{
		super(false);

		// Wardens
		// Wardens
		int i = 1;
		for (int id : new int[]
		{
			36403,
			36404,
			36405,
			36406,
			36407,
			36408,
			36409,
			36410,
			36411
		})
		{
			wardens.put(id, i++);
		}

		addStartNpc(36403, 36404, 36405, 36406, 36407, 36408, 36409, 36410, 36411);
		addQuestItem(FragmentOfTheDungeonLeaderMark);
		addKillId(RhiannaTheTraitor, TeslaTheDeceiver, SoulHunterChakundel, DurangoTheCrusher, BrutusTheObstinate, RangerKarankawa, SargonTheMad, BeautifulAtrielle, NagenTheTomboy, JaxTheDestroyer);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		if (event.equalsIgnoreCase("gludio_prison_keeper_q0512_03.htm") || event.equalsIgnoreCase("gludio_prison_keeper_q0512_05.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("exit"))
		{
			st.exitCurrentQuest(true);
			return null;
		}
		else if (event.equalsIgnoreCase("enter"))
		{
			if (st.getState() == CREATED || !check(st.getPlayer(), wardens.getOrDefault(npc.getNpcId(), -1)))
			{
				return "gludio_prison_keeper_q0512_01a.htm";
			}
			else
			{
				return enterPrison(st.getPlayer(), wardens.getOrDefault(npc.getNpcId(), -1));
			}
		}
		return event;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (!check(st.getPlayer(), wardens.getOrDefault(npc.getNpcId(), -1)))
		{
			return "gludio_prison_keeper_q0512_01a.htm";
		}
		if (st.getState() == CREATED)
		{
			return "gludio_prison_keeper_q0512_01.htm";
		}
		if (st.getQuestItemsCount(FragmentOfTheDungeonLeaderMark) > 0)
		{
			st.giveItems(KnightsEpaulette, st.getQuestItemsCount(FragmentOfTheDungeonLeaderMark));
			st.takeItems(FragmentOfTheDungeonLeaderMark, -1);
			st.playSound(SOUND_FINISH);
			return "gludio_prison_keeper_q0512_08.htm";
		}
		return "gludio_prison_keeper_q0512_09.htm";
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		for (Prison prison : _prisons.values())
		{
			if (prison.getReflectionId() == npc.getReflectionId())
			{
				switch (npc.getNpcId())
				{
				case RhiannaTheTraitor:
				case TeslaTheDeceiver:
				case SoulHunterChakundel:
					prison.initSpawn(type2[Rnd.get(type2.length)], false);
					break;
				case DurangoTheCrusher:
				case BrutusTheObstinate:
				case RangerKarankawa:
				case SargonTheMad:
					prison.initSpawn(type3[Rnd.get(type3.length)], false);
					break;
				case BeautifulAtrielle:
				case NagenTheTomboy:
				case JaxTheDestroyer:
					Party party = st.getPlayer().getParty();
					if (party != null)
					{
						for (Player member : party.getMembers())
						{
							QuestState qs = member.getQuestState(getClass());
							if (qs != null && qs.isStarted())
							{
								qs.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount / party.size());
								qs.playSound(SOUND_ITEMGET);
								qs.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
							}
						}
					}
					else
					{
						st.giveItems(FragmentOfTheDungeonLeaderMark, RewardMarksCount);
						st.playSound(SOUND_ITEMGET);
						st.getPlayer().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
					}
					Reflection r = ReflectionManager.getInstance().get(prison.getReflectionId());
					if (r != null)
					{
						r.startCollapseTimer(300000); // All bosses killed, run a collapse after 5 minutes
					}
					break;
				}
				break;
			}
		}

		return null;
	}

	private boolean check(Player player, int castle_id)
	{
		/*
		 * Castle castle = ResidenceHolder.getInstance().getResidenceByObject(Castle.class, player);
		 * if (castle == null)
		 * return false;
		 * Clan clan = player.getClan();
		 * if (clan == null)
		 * return false;
		 * if (clan.getClanId() != castle.getOwnerId())
		 * return false;
		 */
		if (player == null || player.getClan() == null || (player.getClan().getCastle() != castle_id))
		{
			return false;
		}
		return true;
	}

	private String enterPrison(Player player, int castle_id)
	{
		/*
		 * Castle castle = ResidenceHolder.getInstance().getResidenceByObject(Castle.class, player);
		 * if (castle == null || castle.getOwner() != player.getClan())
		 * return "gludio_prison_keeper_q0512_01a.htm";
		 */
		if (!check(player, castle_id) || !areMembersSameClan(player))
		{
			return "gludio_prison_keeper_q0512_01a.htm";
		}

		if (player.canEnterInstance(INSTANCE_ZONE_ID))
		{
			InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(INSTANCE_ZONE_ID);
			Prison prison = null;
			if (!_prisons.isEmpty())
			{
				prison = _prisons.get(castle_id);
				if (prison != null && prison.isLocked())
				{
					// TODO правильное сообщение
					player.sendPacket(new SystemMessage(SystemMessage.C1_MAY_NOT_RE_ENTER_YET).addName(player));
					return null;
				}
			}

			// Synerge - Add the player to the instance again
			if (prison != null)
			{
				Reflection r = ReflectionManager.getInstance().get(prison.getReflectionId());
				if (r != null)
				{
					player.setReflection(r);
					player.teleToLocation(iz.getTeleportCoord());
					player.setVar("backCoords", r.getReturnLoc().toXYZString(), -1);
					player.setInstanceReuse(iz.getId(), System.currentTimeMillis());
					return null;
				}
			}

			prison = new Prison(castle_id, iz);
			_prisons.put(prison.getCastleId(), prison);

			Reflection r = ReflectionManager.getInstance().get(prison.getReflectionId());

			r.setReturnLoc(player.getLoc());

			for (Player member : player.getParty().getMembers())
			{
				if (member != player)
				{
					newQuestState(member, STARTED);
				}
				member.setReflection(r);
				member.teleToLocation(iz.getTeleportCoord());
				member.setVar("backCoords", r.getReturnLoc().toXYZString(), -1);
				member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
			}

			player.getParty().setReflection(r);
			r.setParty(player.getParty());
			r.startCollapseTimer(iz.getTimelimit() * 60 * 1000L);
			player.getParty().sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(iz.getTimelimit()));

			prison.initSpawn(type1[Rnd.get(type1.length)], true);
		}
		return null;
	}

	private class Prison
	{
		private int _castleId;
		private int _reflectionId;
		private long _lastEnter;

		private class PrisonSpawnTask extends RunnableImpl
		{
			int _npcId;

			public PrisonSpawnTask(int npcId)
			{
				_npcId = npcId;
			}

			@Override
			public void runImpl() throws Exception
			{
				addSpawnToInstance(_npcId, new Location(12152, -49272, -3008, 25958), 0, _reflectionId);
			}
		}

		public Prison(int id, InstantZone iz)
		{
			try
			{
				Reflection r = new Reflection();
				r.init(iz);
				_reflectionId = r.getId();
				_castleId = id;
				_lastEnter = System.currentTimeMillis();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		public void initSpawn(int npcId, boolean first)
		{
			ThreadPoolManager.getInstance().schedule(new PrisonSpawnTask(npcId), first ? 60000 : 180000);
		}

		public int getReflectionId()
		{
			return _reflectionId;
		}

		public int getCastleId()
		{
			return _castleId;
		}

		public boolean isLocked()
		{
			return System.currentTimeMillis() - _lastEnter < 4 * 60 * 60 * 1000L;
		}
	}

	private boolean areMembersSameClan(Player player)
	{
		if (player.getParty() == null)
		{
			return true;
		}

		for (Player p : player.getParty().getMembers())
		{
			if (p.getClan() != player.getClan())
			{
				return false;
			}
		}
		return true;
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