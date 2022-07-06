package l2mv.gameserver.handler.admincommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.PremiumHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.database.LoginDatabaseFactory;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.premium.PremiumAccount;
import l2mv.gameserver.model.premium.PremiumStart;
import l2mv.gameserver.network.serverpackets.ExBR_PremiumState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Admin handler to give premium bonuses
 *
 * @author Synerge
 */
public class AdminPremium implements IAdminCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(AdminPremium.class);

	private static enum Commands
	{
		admin_setpremium, admin_removepremium, admin_premiumall
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		switch (command)
		{
		case admin_setpremium:
		{
			final int premiumId = Integer.parseInt(wordList[1]);
			final PremiumAccount premium = PremiumHolder.getInstance().getPremium(premiumId);
			if (premium == null)
			{
				activeChar.sendMessage("Invalid premium Id");
				return true;
			}

			if (Config.PREMIUM_ACCOUNT_TYPE != 2)
			{
				activeChar.sendMessage("Cannot give premium with the current premium account type");
				return true;
			}

			if (!(activeChar.getTarget() instanceof Player))
			{
				activeChar.sendMessage("Invalid target, you must select the character you want to give the premium bonus");
				return true;
			}

			final Player target = activeChar.getTarget().getPlayer();
			final int current = (int) (System.currentTimeMillis() / 1000L);
			final int newBonusTime = current + premium.getTime();

			// Now we must add the premium to all accounts in the db, but avoiding those that already have a bonus
			try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("REPLACE INTO account_bonus(account, bonus, bonus_expire) VALUES (?,?,?)"))
			{
				statement.setString(1, target.getAccountName());
				statement.setDouble(2, premiumId);
				statement.setInt(3, newBonusTime);
				statement.execute();
			}
			catch (Exception e)
			{
				activeChar.sendMessage("An error ocurred when trying to update the bonuses on the db");
				return true;
			}

			// Give the bonus to targeted player so its updated in real time
			target.stopBonusTask(true);

			target.getNetConnection().setBonus(premiumId);
			target.getNetConnection().setBonusExpire(newBonusTime);

			PremiumStart.getInstance().start(target);
			PremiumStart.getInstance().updateItems(false, target);
			if (target.getParty() != null)
			{
				target.getParty().recalculatePartyData();
			}
			target.sendPacket(new ExBR_PremiumState(target, true));

			target.sendPacket(new ExShowScreenMessage("You got a premium pack gifted by an Admin", 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false));

			activeChar.sendMessage("Given the bonus service to the target " + target.getName());
			break;
		}
		case admin_removepremium:
		{
			if (!(activeChar.getTarget() instanceof Player))
			{
				activeChar.sendMessage("Invalid target, you must select the character you want to remove the premium bonus");
				return true;
			}

			final Player target = activeChar.getTarget().getPlayer();
			if (!target.hasBonus())
			{
				activeChar.sendMessage("The targeted player doesnt have any active bonus");
				return true;
			}

			// Remove the bonus
			try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM account_bonus WHERE account=?");)
			{
				statement.setString(1, target.getAccountName());
				statement.executeUpdate();
			}
			catch (SQLException e)
			{
				_log.error("PremiumAccountsTable: Failed to remove data for player " + target.getName(), e);
			}

			// Update the bonus
			target.stopBonusTask(true);

			target.getNetConnection().setBonus(0);
			target.getNetConnection().setBonusExpire(0);

			PremiumStart.getInstance().start(target);
			PremiumStart.getInstance().updateItems(true, target);
			if (target.getParty() != null)
			{
				target.getParty().recalculatePartyData();
			}
			target.sendPacket(new ExBR_PremiumState(target, false));

			target.sendPacket(new ExShowScreenMessage("Your premium pack was removed by an Admin", 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false));

			activeChar.sendMessage("Removed bonus service from the target " + target.getName());
			break;
		}
		case admin_premiumall:
		{
			final int premiumId = Integer.parseInt(wordList[1]);
			final PremiumAccount premium = PremiumHolder.getInstance().getPremium(premiumId);
			if (premium == null)
			{
				activeChar.sendMessage("Invalid premium Id");
				return true;
			}

			if (Config.PREMIUM_ACCOUNT_TYPE != 2)
			{
				activeChar.sendMessage("Cannot give premium with the current premium account type");
				return true;
			}

			final int current = (int) (System.currentTimeMillis() / 1000L);
			final int newBonusTime = current + premium.getTime();

			// First we must get all the account names from the db
			final List<String> accounts = new ArrayList<>();
			try (Connection con = LoginDatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT login FROM accounts"); ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					accounts.add(rset.getString("login"));
				}
			}
			catch (Exception e)
			{
				activeChar.sendMessage("An error ocurred when trying to get all accounts from db");
				return true;
			}

			// Now we must add the premium to all accounts in the db, but avoiding those that already have a bonus
			try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("INSERT IGNORE INTO account_bonus(account, bonus, bonus_expire) VALUES (?,?,?)"))
			{
				// We create a batch to make this massive query, disabling auto commit
				con.setAutoCommit(false);
				for (String account : accounts)
				{
					statement.setString(1, account);
					statement.setDouble(2, premiumId);
					statement.setInt(3, newBonusTime);
					statement.addBatch();
				}

				statement.executeBatch();
				con.setAutoCommit(true);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("An error ocurred when trying to update the bonuses on the db");
				return true;
			}

			// Give the bonus to all players ingame so its updated in real time
			for (Player player : GameObjectsStorage.getAllPlayers())
			{
				// Dont give premium to those that already have it
				if (player == null || player.getNetConnection() == null || (player.getNetConnection().getBonusExpire() > current))
				{
					continue;
				}

				player.stopBonusTask(true);

				player.getNetConnection().setBonus(premiumId);
				player.getNetConnection().setBonusExpire(newBonusTime);

				PremiumStart.getInstance().start(player);
				PremiumStart.getInstance().updateItems(false, player);
				if (player.getParty() != null)
				{
					player.getParty().recalculatePartyData();
				}
				player.sendPacket(new ExBR_PremiumState(player, true));

				player.sendPacket(new ExShowScreenMessage("You got a premium pack gifted by an Admin", 6000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false));
			}

			activeChar.sendMessage("Delivered the premium bonus to " + accounts.size() + " accounts. Those accounts that had premium didnt get any extra bonus");
			break;
		}
		}

		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}