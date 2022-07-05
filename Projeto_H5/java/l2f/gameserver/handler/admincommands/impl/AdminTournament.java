package l2f.gameserver.handler.admincommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.dao.MailDAO;
import l2f.gameserver.data.StringHolder;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.tournament.ActiveBattleManager;
import l2f.gameserver.model.entity.tournament.BattleRecord;
import l2f.gameserver.model.entity.tournament.BattleScheduleManager;
import l2f.gameserver.model.entity.tournament.TournamentStatus;
import l2f.gameserver.utils.Language;

public class AdminTournament implements IAdminCommandHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(AdminTournament.class);

	private static enum Commands
	{
		admin_start_tournament_registration, admin_start_tournament, admin_tournament_check_round_over, admin_tournament_reset_current_round, admin_delay_tournament_battles_days,
		admin_delay_tournament_battles_minutes, admin_delete_tournament_mails_all, admin_change_tournament_team
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		switch (command)
		{
		case admin_start_tournament_registration:
		{
			final TournamentStatus status = TournamentStatus.getCurrentStatus();
			if (status != TournamentStatus.OVER && status != TournamentStatus.NOT_ACTIVE)
			{
				activeChar.sendMessage("Cannot start tournament. Current Status: " + status.toString());
				return false;
			}

			try
			{
				final Map<Integer, List<BattleRecord>> battlesPerRound = BattleScheduleManager.getInstance().getBattlesPerRound();
				battlesPerRound.clear();
				wipeTournamentDatabase();
				recalculateLastBattleId();
				recalculateLastBattleDate();
			}
			catch (Exception e)
			{
				LOG.error("Error while starting Tournament Registration Period", e);
				return false;
			}

			break;
		}
		case admin_start_tournament:
		{
			final TournamentStatus status = TournamentStatus.getCurrentStatus();
			if (status != TournamentStatus.REGISTRATION)
			{
				activeChar.sendMessage("Cannot start tournament. Current Status: " + status.toString());
				return false;
			}
			BattleScheduleManager.getInstance().scheduleFirstRound();
			break;
		}
		case admin_tournament_check_round_over:
		{
			BattleScheduleManager.getInstance().checkRoundOver();
			break;
		}
		case admin_tournament_reset_current_round:
		{
			try
			{
				final Map<Integer, List<BattleRecord>> battlesPerRound = BattleScheduleManager.getInstance().getBattlesPerRound();
				final int currentRoundIndex = BattleScheduleManager.getInstance().getCurrentRoundIndex();
				final List<BattleRecord> records = battlesPerRound.remove(currentRoundIndex);
				for (BattleRecord record : records)
				{
					deleteRecordFromDatabase(record.getId());
					if (record.getStartBattleThread() != null)
					{
						record.getStartBattleThread().cancel(false);
					}
				}
				recalculateLastBattleId();
				recalculateLastBattleDate();
				BattleScheduleManager.getInstance().checkRoundOver();
			}
			catch (Exception e)
			{
				LOG.error("Error while using //tournament_reset_current_round", e);
				return false;
			}
			break;
		}
		case admin_delay_tournament_battles_days:
		{
			final long days = Long.parseLong(wordList[1]);
			final long millis = TimeUnit.DAYS.toMillis(days);
			delayTournamentBattles(millis);
			activeChar.sendMessage("Delayed " + BattleScheduleManager.getInstance().getBattlesForIterate().size() + " Battles by " + days + " Days");
			break;
		}
		case admin_delay_tournament_battles_minutes:
		{
			final long minutes = Long.parseLong(wordList[1]);
			final long millis = TimeUnit.MINUTES.toMillis(minutes);
			delayTournamentBattles(millis);
			activeChar.sendMessage("Delayed " + BattleScheduleManager.getInstance().getBattlesForIterate().size() + " Battles by " + minutes + " Minutes");
			break;
		}
		case admin_delete_tournament_mails_all:
		{
			try
			{
				int deletedCount = 0;
				for (Language lang : Language.values())
				{
					deletedCount += deleteMails(StringHolder.getNotNull(lang, "Tournament.Notifications.MailAboutMatchDateTopic"));
				}
				activeChar.sendMessage("Deleted " + deletedCount + " Mails!");
			}
			catch (Exception e)
			{
				LOG.error("Error in //delete_tournament_mails_all", e);
				return false;
			}
			break;
		}
		case admin_change_tournament_team:
		{
			final int battleId = Integer.parseInt(wordList[1]);
			final int oldTeamId = Integer.parseInt(wordList[2]);
			final int newTeamId = Integer.parseInt(wordList[3]);
			try
			{
				for (BattleRecord record2 : BattleScheduleManager.getInstance().getBattlesForIterate())
				{
					if (record2.getId() == battleId)
					{
						if (record2.getTeam1Id() == oldTeamId)
						{
							record2.setTeam1Id(newTeamId);
						}
						else
						{
							if (record2.getTeam2Id() != oldTeamId)
							{
								activeChar.sendMessage("Battle Id " + record2.getId() + " Has Teams: " + record2.getTeam1Id() + ", " + record2.getTeam2Id());
								return false;
							}
							record2.setTeam2Id(newTeamId);
						}
						return true;
					}
				}
			}
			catch (Exception e)
			{
				LOG.error("Error while //change_tournament_team " + fullString, e);
				return false;
			}
			activeChar.sendMessage("Battle Id " + battleId + " was not found in current round!");
			break;
		}
		default:
		{
			return false;
		}
		}

		return true;
	}

	private static void delayTournamentBattles(long millis)
	{
		for (BattleRecord record : BattleScheduleManager.getInstance().getBattlesForIterate())
		{
			if (record.getWinnerId() < 0)
			{
				record.setBattleDate(record.getBattleDate() + millis);
				record.updateInDatabase();
				if (record.getStartBattleThread() == null)
				{
					continue;
				}
				record.getStartBattleThread().cancel(false);
			}
		}
		ActiveBattleManager.startScheduleThread();
	}

	private static int deleteMails(String mailTitle) throws SQLException
	{
		int count = 0;
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT message_id FROM mail WHERE sender_id = ? AND topic LIKE ?"))
		{
			statement.setInt(1, 1);
			statement.setString(2, mailTitle);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					MailDAO.getInstance().load(rset.getInt("message_id")).delete();
					++count;
				}
			}
		}
		return count;
	}

	private static void deleteRecordFromDatabase(int battleRecordId) throws SQLException
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM tournament_battles WHERE id = ?"))
		{
			statement.setInt(1, battleRecordId);
			statement.executeUpdate();
		}
	}

	private static void recalculateLastBattleId()
	{
		final int currentRoundIndex = BattleScheduleManager.getInstance().getCurrentRoundIndex();
		int newLastBattleId = -1;
		for (int roundIndex = 0; roundIndex <= currentRoundIndex; ++roundIndex)
		{
			for (BattleRecord record : BattleScheduleManager.getInstance().getBattlesForIterate(roundIndex))
			{
				if (record.getId() > newLastBattleId)
				{
					newLastBattleId = record.getId();
				}
			}
		}
		BattleScheduleManager.getInstance().setLastBattleObjectId(newLastBattleId);
	}

	private static void recalculateLastBattleDate()
	{
		final int currentRoundIndex = BattleScheduleManager.getInstance().getCurrentRoundIndex();
		long newLastBattleDate = -1L;
		for (int roundIndex = 0; roundIndex <= currentRoundIndex; ++roundIndex)
		{
			for (BattleRecord record : BattleScheduleManager.getInstance().getBattlesForIterate(roundIndex))
			{
				if (record.getBattleDate() > newLastBattleDate)
				{
					newLastBattleDate = record.getBattleDate();
				}
			}
		}
		BattleScheduleManager.getInstance().setLastBattleDate(newLastBattleDate);
	}

	private static void wipeTournamentDatabase() throws SQLException
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement battlesStatement = con.prepareStatement("DELETE FROM tournament_battles");
					PreparedStatement teamsStatement = con.prepareStatement("DELETE FROM tournament_teams"))
		{
			battlesStatement.executeUpdate();
			teamsStatement.executeUpdate();
		}
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
