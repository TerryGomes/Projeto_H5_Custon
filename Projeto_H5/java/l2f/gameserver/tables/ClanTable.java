package l2f.gameserver.tables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.pledge.Alliance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.model.pledge.SubUnit;
import l2f.gameserver.model.pledge.UnitMember;
import l2f.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.TimeUtils;
import l2f.gameserver.utils.Util;

public class ClanTable
{
	private static final Logger LOG = LoggerFactory.getLogger(ClanTable.class);

	private static ClanTable _instance;

	private final Map<Integer, Clan> _clans = new ConcurrentHashMap<Integer, Clan>();
	private final Map<Integer, Alliance> _alliances = new ConcurrentHashMap<Integer, Alliance>();

	public static ClanTable getInstance()
	{
		if (_instance == null)
		{
			new ClanTable();
		}
		return _instance;
	}

	public Clan[] getClans()
	{
		return _clans.values().toArray(new Clan[_clans.size()]);
	}

	public Alliance[] getAlliances()
	{
		return _alliances.values().toArray(new Alliance[_alliances.size()]);
	}

	private ClanTable()
	{
		_instance = this;

		restoreClans();
		restoreAllies();
		restoreWars();
	}

	public Clan getClan(int clanId)
	{
		if (clanId <= 0)
		{
			return null;
		}
		return _clans.get(clanId);
	}

	public String getClanName(int clanId)
	{
		Clan c = getClan(clanId);
		return c != null ? c.getName() : StringUtils.EMPTY;
	}

	public Clan getClanByCharId(int charId)
	{
		if (charId <= 0)
		{
			return null;
		}
		for (Clan clan : getClans())
		{
			if (clan != null && clan.isAnyMember(charId))
			{
				return clan;
			}
		}
		return null;
	}

	public Alliance getAlliance(int allyId)
	{
		if (allyId <= 0)
		{
			return null;
		}
		return _alliances.get(allyId);
	}

	public Alliance getAllianceByCharId(int charId)
	{
		if (charId <= 0)
		{
			return null;
		}
		Clan charClan = getClanByCharId(charId);
		return charClan == null ? null : charClan.getAlliance();
	}

	public Map.Entry<Clan, Alliance> getClanAndAllianceByCharId(int charId)
	{
		Player player = GameObjectsStorage.getPlayer(charId);
		Clan charClan = player != null ? player.getClan() : getClanByCharId(charId);
		return new SimpleEntry<Clan, Alliance>(charClan, charClan == null ? null : charClan.getAlliance());
	}

	public void restoreClans()
	{
		List<Integer> clanIds = new ArrayList<Integer>();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT clan_id FROM clan_data"); ResultSet result = statement.executeQuery())
		{
			while (result.next())
			{
				clanIds.add(result.getInt("clan_id"));
			}
		}
		catch (SQLException e)
		{
			LOG.warn("Error while restoring clans!!! ", e);
		}

		for (int clanId : clanIds)
		{
			Clan clan = Clan.restore(clanId);
			if (clan == null)
			{
				LOG.warn("Error while restoring clanId: " + clanId);
				continue;
			}

			if (clan.getAllSize() <= 0)
			{
				LOG.warn("membersCount = 0 for clanId: " + clanId);
				continue;
			}

			if (clan.getLeader() == null)
			{
				LOG.warn("Not found leader for clanId: " + clanId);
				continue;
			}

			_clans.put(clan.getClanId(), clan);
			if (clan.getDissolvedClanTime() != 0)
			{
				LOG.info("ClanDissolve: clanId=" + clan.getClanId() + " dissolvedClanTime=" + TimeUtils.convertDateToString(clan.getDissolvedClanTime()) + " systemTime="
							+ TimeUtils.convertDateToString(System.currentTimeMillis()));
				if (clan.getDissolvedClanTime() <= System.currentTimeMillis())
				{
					deleteClan(clan.getClanId());
				}
				else
				{
					scheduleRemoveClan(clan.getClanId(), clan.getDissolvedClanTime());
				}
			}
		}
	}

	public void restoreClan(int clanId)
	{
		final Clan clan = Clan.restore(clanId);
		if (clan == null)
		{
			LOG.warn("Error while restoring clanId: " + clanId);
			return;
		}
		_clans.put(clan.getClanId(), clan);
	}

	public void restoreAllies()
	{
		List<Integer> allyIds = new ArrayList<Integer>();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT ally_id FROM ally_data"); ResultSet result = statement.executeQuery())
		{
			while (result.next())
			{
				allyIds.add(result.getInt("ally_id"));
			}
		}
		catch (SQLException e)
		{
			LOG.warn("Error while restoring allies!!! ", e);
		}

		for (int allyId : allyIds)
		{
			Alliance ally = new Alliance(allyId);

			if (ally.getMembersCount() <= 0)
			{
				LOG.warn("membersCount = 0 for allyId: " + allyId);
				continue;
			}

			if (ally.getLeader() == null)
			{
				LOG.warn("Not found leader for allyId: " + allyId);
				continue;
			}

			_alliances.put(ally.getAllyId(), ally);
		}
	}

	public Clan getClanByName(String clanName)
	{
		if (!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE))
		{
			return null;
		}

		for (Clan clan : _clans.values())
		{
			if (clan.getName().equalsIgnoreCase(clanName))
			{
				return clan;
			}
		}

		return null;
	}

	public Alliance getAllyByName(String allyName)
	{
		if (!Util.isMatchingRegexp(allyName, Config.ALLY_NAME_TEMPLATE))
		{
			return null;
		}

		for (Alliance ally : _alliances.values())
		{
			if (ally.getAllyName().equalsIgnoreCase(allyName))
			{
				return ally;
			}
		}

		return null;
	}

	public Clan createClan(Player player, String clanName)
	{
		if (getClanByName(clanName) == null)
		{
			UnitMember leader = new UnitMember(player);
			leader.setLeaderOf(Clan.SUBUNIT_MAIN_CLAN);

			Clan clan = new Clan(IdFactory.getInstance().getNextId());

			SubUnit unit = new SubUnit(clan, Clan.SUBUNIT_MAIN_CLAN, leader, clanName);
			unit.addUnitMember(leader);
			clan.addSubUnit(unit, false); // Ä�ËťÄ�Âµ Ä�ËťĹ�ďż˝Ä�Â¶Ä�ËťÄ�Äľ Ĺ�ďż˝Ä�ÄľÄ�Ë›Ä�Â°Ĺ�â€šĹ�Ĺš Ä�Ë› Ä�Â±Ä�Â°Ä�Â·Ĺ�ďż˝. Ä�ĹĽÄ�Â¸Ĺ�â€¦Ä�Â°Ä�ÂµĹ�â€šĹ�ďż˝Ĺ�Ĺą Ä�ËťÄ�Â¸Ä�Â¶Ä�Âµ

			clan.store();

			player.setPledgeType(Clan.SUBUNIT_MAIN_CLAN);
			player.setClan(clan);
			player.setPowerGrade(6);

			leader.setPlayerInstance(player, false);

			_clans.put(clan.getClanId(), clan);

			return clan;
		}
		else
		{
			return null;
		}
	}

	public void dissolveClan(Player player)
	{
		final Clan clan = player.getClan();
		final long curtime = System.currentTimeMillis();
		player.removeSiegeSkills();
		for (Player clanMember : clan.getOnlineMembers(0))
		{
			clanMember.setClan(null);
			clanMember.setTitle(null);
			clanMember.sendPacket(PledgeShowMemberListDeleteAll.STATIC, SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN);
			clanMember.broadcastCharInfo();
			clanMember.setLeaveClanTime(curtime);
		}
		clan.flush();
		deleteClanFromDb(clan.getClanId());
		_clans.remove(clan.getClanId());
		player.sendPacket(SystemMsg.CLAN_HAS_DISPERSED);
		player.setDeleteClanTime(curtime);
	}

	public void deleteClanFromDb(int clanId)
	{
		long curtime = System.currentTimeMillis();

		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement("UPDATE characters SET clanid=0,title='',pledge_type=0,pledge_rank=0,lvl_joined_academy=0,apprentice=0,leaveclan=? WHERE clanid=?"))
			{
				statement.setLong(1, curtime / 1000L);
				statement.setInt(2, clanId);
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_data WHERE clan_id=?"))
			{
				statement.setInt(1, clanId);
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_subpledges WHERE clan_id=?"))
			{
				statement.setInt(1, clanId);
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_privs WHERE clan_id=?"))
			{
				statement.setInt(1, clanId);
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_skills WHERE clan_id=?"))
			{
				statement.setInt(1, clanId);
				statement.execute();
			}
		}
		catch (SQLException e)
		{
			LOG.warn("could not dissolve clan:" + e);
		}
	}

	public Alliance createAlliance(Player player, String allyName)
	{
		Alliance alliance = null;

		if (getAllyByName(allyName) == null)
		{
			Clan leader = player.getClan();
			alliance = new Alliance(IdFactory.getInstance().getNextId(), allyName, leader);
			alliance.store();
			_alliances.put(alliance.getAllyId(), alliance);

			player.getClan().setAllyId(alliance.getAllyId());
			for (Player temp : player.getClan().getOnlineMembers(0))
			{
				temp.broadcastCharInfo();
			}
		}

		return alliance;
	}

	public void dissolveAlly(Player player)
	{
		int allyId = player.getAllyId();
		for (Clan member : player.getAlliance().getMembers())
		{
			member.setAllyId(0);
			member.broadcastClanStatus(false, true, false);
			member.broadcastToOnlineMembers(SystemMsg.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE);
			member.setLeavedAlly();
		}
		deleteAllyFromDb(allyId);
		_alliances.remove(allyId);
		player.sendPacket(SystemMsg.THE_ALLIANCE_HAS_BEEN_DISSOLVED);
		player.getClan().setDissolvedAlly();
	}

	public void deleteAllyFromDb(int allyId)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{

			try (PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE ally_id=?"))
			{
				statement.setInt(1, allyId);
				statement.execute();
			}

			try (PreparedStatement statement = con.prepareStatement("DELETE FROM ally_data WHERE ally_id=?"))
			{
				statement.setInt(1, allyId);
				statement.execute();
			}
		}
		catch (SQLException e)
		{
			LOG.warn("could not dissolve clan:" + e);
		}
	}

	public void startClanWar(Clan clan1, Clan clan2)
	{
		// clan1 is declaring war against clan2
		clan1.setEnemyClan(clan2);
		clan2.setAttackerClan(clan1);
		clan1.broadcastClanStatus(false, false, true);
		clan2.broadcastClanStatus(false, false, true);

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO clan_wars (clan1, clan2) VALUES(?,?)"))
		{
			statement.setInt(1, clan1.getClanId());
			statement.setInt(2, clan2.getClanId());
			statement.execute();
		}
		catch (SQLException e)
		{
			LOG.warn("Could not store clan war data:", e);
		}

		clan1.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.A_CLAN_WAR_HAS_BEEN_DECLARED_AGAINST_THE_CLAN_S1).addString(clan2.getName()));
		clan2.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.S1_HAS_DECLARED_A_CLAN_WAR).addString(clan1.getName()));
	}

	public void stopClanWar(Clan clan1, Clan clan2)
	{
		// clan1 is ceases war against clan2
		clan1.deleteEnemyClan(clan2);
		clan2.deleteAttackerClan(clan1);

		clan1.broadcastClanStatus(false, false, true);
		clan2.broadcastClanStatus(false, false, true);

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM clan_wars WHERE clan1=? AND clan2=?"))
		{
			statement.setInt(1, clan1.getClanId());
			statement.setInt(2, clan2.getClanId());
			statement.execute();
		}
		catch (SQLException e)
		{
			LOG.warn("Could not delete war data:", e);
		}

		clan1.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.THE_WAR_AGAINST_S1_CLAN_HAS_BEEN_STOPPED).addString(clan2.getName()));
		clan2.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.THE_CLAN_S1_HAS_DECIDED_TO_STOP_THE_WAR).addString(clan1.getName()));
	}

	private void restoreWars()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT clan1, clan2 FROM clan_wars"); ResultSet rset = statement.executeQuery())
		{
			Clan clan1;
			Clan clan2;
			while (rset.next())
			{
				clan1 = getClan(rset.getInt("clan1"));
				clan2 = getClan(rset.getInt("clan2"));
				if (clan1 != null && clan2 != null)
				{
					clan1.setEnemyClan(clan2);
					clan2.setAttackerClan(clan1);
				}
			}
		}
		catch (SQLException e)
		{
			LOG.warn("Could not restore clan wars data:", e);
		}
	}

	public static void unload()
	{
		if (_instance != null)
		{
			try
			{
				_instance.finalize();
			}
			catch (Throwable e)
			{
			}
		}
	}

	public void scheduleRemoveClan(final int clanId, long dissolvedClanTime)
	{
		ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			@Override
			public void run()
			{
				destroyClan(clanId);
			}
		}, dissolvedClanTime - System.currentTimeMillis());
	}

	public void deleteClan(int clanId)
	{
		final Clan clan = getClan(clanId);
		clan.flush();
		deleteClanFromDb(clan.getClanId());
		_clans.remove(clan.getClanId());
	}

	public void destroyClan(int clanId)
	{
		final Clan clan = getClan(clanId);
		if ((clan == null) || (clan.getDissolvedClanTime() <= 0))
		{
			return;
		}
		final long curtime = System.currentTimeMillis();
		for (Player clanMember : clan.getOnlineMembers(0))
		{
			if (clanMember.isClanLeader())
			{
				clanMember.setDeleteClanTime(curtime);
				clanMember.sendPacket(SystemMsg.CLAN_HAS_DISPERSED);
				clanMember.removeSiegeSkills();
			}
			clanMember.setClan(null);
			clanMember.setTitle(null);
			clanMember.sendPacket(new IStaticPacket[]
			{
				PledgeShowMemberListDeleteAll.STATIC,
				SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN
			});
			clanMember.setLeaveClanTime(curtime);
			clanMember.broadcastUserInfo(true);
		}
		clan.flush();
		_clans.remove(clan.getClanId());
		deleteClanFromDb(clan.getClanId());
	}
}