package services.community;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.ConfigHolder;
import l2f.gameserver.cache.ImagesCache;
import l2f.gameserver.dao.CharacterDAO;
import l2f.gameserver.data.HtmPropHolder;
import l2f.gameserver.data.HtmPropList;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.database.merge.MergeAuthDatabaseFactory;
import l2f.gameserver.database.merge.MergeDatabaseFactory;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.scripts.ScriptFile;

public class MergeCommunity implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(MergeCommunity.class);

	private static final Pattern ACCOUNT_NAME_PATTERN = Pattern.compile("[A-Za-z0-9]{4,14}");
	private static final Pattern CHAR_NAME_PATTERN = Pattern.compile("[A-Za-z0-9]{4,16}");
	private static final Pattern CLAN_NAME_PATTERN = Pattern.compile("([0-9A-Za-z]{3,16})|([0-9\\u0410]{3,16})");
	private static final String PATH_TO_MAIN_HTM = "merge/start.htm";
	private static final String PATH_TO_ENDING_HTM = "merge/ending.htm";
	private static final String STARTED_MERGE_VAR = "mergeStarted";
	private static final String ACCOUNT_DATA_VAR = "mergeNewAccountName";
	private static final String CHAR_NAME_DATA_VAR = "mergeNewCharName_";
	private static final String CLAN_NAME_DATA_VAR = "mergeClanNewName";

	@Override
	public void onLoad()
	{
		if (ConfigHolder.getBool("EnableMergeCommunity"))
		{
			LOG.info("Merge Community Board has been Enabled!");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (ConfigHolder.getBool("EnableMergeCommunity"))
		{
			CommunityBoardManager.getInstance().removeHandler(this);
		}
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_merge",
			"_mergeFinalize",
			"_bbshome",
			"_bbsgetfav",
			"_bbsloc",
			"_bbsclan",
			"_bbsmemo",
			"_maillist_0_1_0_",
			"_friendlist_0_"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if (!ConfigHolder.getBool("EnableMergeCommunity"))
		{
			return;
		}

		if (!player.isBlocked())
		{
			player.block();
		}
		final StringTokenizer st = new StringTokenizer(bypass, "_");
		final String cmd = st.nextToken();
		if (cmd.equals("mergeFinalize"))
		{
			final boolean finished = checkCorrectDataSuccess(player, st);
			if (finished)
			{
				finalizeMerge(player);
				showEndingPage(player, player.getQuickVarS("mergeNewAccountName", new String[0]));
				return;
			}
		}
		else
		{
			final String newAccountName = getNewAccountName(player.getAccountName());
			if (!newAccountName.isEmpty())
			{
				showEndingPage(player, newAccountName);
				return;
			}
		}
		showMainPage(player);
	}

	private static void showMainPage(Player player)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "merge/start.htm", player);
		final HtmPropList props = HtmPropHolder.getListFromHtmPath(Config.BBS_HOME_DIR + "merge/start.htm");
		final boolean init = player.getQuickVarB("mergeStarted", true);
		if (init)
		{
			checkSameValuesPossible(player);
		}
		html = html.replace("%accountNameValue%", player.getAccountName());
		if (player.containsQuickVar("mergeNewAccountName"))
		{
			html = html.replace("%accountNameTextColor%", props.getText("correct_data_color"));
			html = html.replace("%newAccount%", props.getText("correct_value_with_color").replace("%value%", player.getQuickVarS("mergeNewAccountName", new String[0])));
		}
		else
		{
			if (init)
			{
				html = html.replace("%accountNameTextColor%", props.getText("init_data_color"));
			}
			else
			{
				html = html.replace("%accountNameTextColor%", props.getText("wrong_data_color"));
			}
			html = html.replace("%newAccount%", props.getText("account_name_edit"));
		}
		if (player.isClanLeader())
		{
			final Clan clan = player.getClan();
			html = html.replace("%clanNameText%", "Clan Name");
			html = html.replace("%clanNameValue%", clan.getName());
			if (player.containsQuickVar("mergeClanNewName"))
			{
				html = html.replace("%clanNameTextColor%", props.getText("correct_data_color"));
				html = html.replace("%newClan%", props.getText("correct_value_with_color").replace("%value%", player.getQuickVarS("mergeClanNewName", new String[0])));
			}
			else
			{
				if (init)
				{
					html = html.replace("%clanNameTextColor%", props.getText("init_data_color"));
				}
				else
				{
					html = html.replace("%clanNameTextColor%", props.getText("wrong_data_color"));
				}
				html = html.replace("%newClan%", props.getText("clan_name_edit"));
			}
		}
		else
		{
			html = html.replace("%clanNameText%", "");
			html = html.replace("%clanNameValue%", "");
			html = html.replace("%clanNameTextColor%", "");
			html = html.replace("%newClan%", "");
		}

		final List<String> charNamesToChange = CharacterDAO.getCharNamesFromAccount(player.getAccountName());
		for (int i = 0; i < 8; ++i)
		{
			final String charName = charNamesToChange.size() > i ? charNamesToChange.get(i) : "";
			if (charName.isEmpty())
			{
				html = html.replace("%oldChar" + (i + 1) + '%', "<br>");
				html = html.replace("%newChar" + (i + 1) + '%', "<br>");
				html = html.replace("%charNameText" + (i + 1) + '%', "<br>");
			}
			else if (player.containsQuickVar("mergeNewCharName_" + charName))
			{
				html = html.replace("%oldChar" + (i + 1) + '%', charName);
				html = html.replace("%newChar" + (i + 1) + '%', props.getText("correct_value_with_color").replace("%value%", player.getQuickVarS("mergeNewCharName_" + charName, new String[0])));
				html = html.replace("%charNameText" + (i + 1) + '%', props.getText("char_name_text_with_color").replace("%color%", props.getText("correct_data_color")));
			}
			else
			{
				html = html.replace("%oldChar" + (i + 1) + '%', charName);
				html = html.replace("%newChar" + (i + 1) + '%', props.getText("char_name_" + (i + 1) + "_edit"));
				if (init)
				{
					html = html.replace("%charNameText" + (i + 1) + '%', props.getText("char_name_text_with_color").replace("%color%", props.getText("init_data_color")));
				}
				else
				{
					html = html.replace("%charNameText" + (i + 1) + '%', props.getText("char_name_text_with_color").replace("%color%", props.getText("wrong_data_color")));
				}
			}
		}
		if (init)
		{
			player.addQuickVar("mergeStarted", false);
		}
		html = html.replace("%serverId%", String.valueOf(Config.REQUEST_ID));
		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private static void showEndingPage(Player player, String newAccountName)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "merge/ending.htm", player);
		html = html.replace("%newAccountName%", newAccountName);
		html = html.replace("%serverId%", String.valueOf(Config.REQUEST_ID));
		ImagesCache.getInstance().sendUsedImages(html, player);
		ShowBoard.separateAndSend(html, player);
	}

	private static void checkSameValuesPossible(Player player)
	{
		if (checkNewAccountName(player.getAccountName()))
		{
			player.addQuickVar("mergeNewAccountName", player.getAccountName());
		}
		if (player.isClanLeader() && checkNewClanName(player.getClanId(), player.getClan().getName()))
		{
			player.addQuickVar("mergeClanNewName", player.getClan().getName());
		}

		final List<String> characters = CharacterDAO.getCharNamesFromAccount(player.getAccountName());
		for (String oldChar : characters)
		{
			if (checkNewCharName(oldChar))
			{
				player.addQuickVar("mergeNewCharName_" + oldChar, oldChar);
			}
		}
	}

	private static boolean checkCorrectDataSuccess(Player player, StringTokenizer st)
	{
		boolean correct = true;
		final String accountNameValue = st.nextToken().trim();
		if (player.containsQuickVar("mergeNewAccountName"))
		{
			if (!checkNewAccountName(player.getQuickVarS("mergeNewAccountName", new String[0])))
			{
				player.deleteQuickVar("mergeNewAccountName");
				correct = false;
			}
		}
		else if (checkNewAccountName(accountNameValue))
		{
			player.addQuickVar("mergeNewAccountName", accountNameValue);
		}
		else
		{
			correct = false;
		}

		final String newClanNameValue = st.nextToken().trim();
		if (player.isClanLeader() && !player.containsQuickVar("mergeClanNewName"))
		{
			if (checkNewClanName(player.getClanId(), newClanNameValue))
			{
				player.addQuickVar("mergeClanNewName", newClanNameValue);
			}
			else
			{
				correct = false;
			}
		}

		final List<String> charNamesToChange = CharacterDAO.getCharNamesFromAccount(player.getAccountName());
		for (String oldCharName : charNamesToChange)
		{
			final String charNameValue = st.nextToken().trim();
			if (player.containsQuickVar("mergeNewCharName_" + oldCharName))
			{
				if (checkNewCharName(player.getQuickVarS("mergeNewCharName_" + oldCharName, new String[0])))
				{
					continue;
				}
				player.deleteQuickVar("mergeNewCharName_" + oldCharName);
				correct = false;
			}
			else if (checkNewCharName(charNameValue) && !checkForDuplicateValues(player, charNamesToChange, charNameValue))
			{
				player.addQuickVar("mergeNewCharName_" + oldCharName, charNameValue);
			}
			else
			{
				correct = false;
			}
		}
		return correct;
	}

	private static boolean checkForDuplicateValues(Player player, Iterable<String> oldCharNames, String newCharNameToAdd)
	{
		for (String oldCharName : oldCharNames)
		{
			if (player.containsQuickVar("mergeNewCharName_" + oldCharName) && player.getQuickVarS("mergeNewCharName_" + oldCharName, new String[0]).equalsIgnoreCase(newCharNameToAdd))
			{
				return true;
			}
		}
		return false;
	}

	private void finalizeMerge(Player player)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement(
						"INSERT INTO merge_data (old_login, new_login, email, finished, old_char_id_1, new_char_name_1, old_char_id_2, new_char_name_2, old_char_id_3, new_char_name_3, old_char_id_4, new_char_name_4, old_char_id_5, new_char_name_5, old_char_id_6, new_char_name_6, old_char_id_7, new_char_name_7, old_char_id_8, new_char_name_8) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						PreparedStatement charNamesStatement = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name = ?"))
			{
				int i = 0;
				statement.setString(++i, player.getAccountName());
				statement.setString(++i, player.getQuickVarS("mergeNewAccountName", new String[0]));
				statement.setString(++i, "");
				statement.setInt(++i, 0);
				charNamesStatement.setString(1, player.getAccountName());
				int charIndex = 1;
				try (ResultSet rset = charNamesStatement.executeQuery())
				{
					while (rset.next())
					{
						statement.setInt(++i, rset.getInt("obj_Id"));
						statement.setString(++i, player.getQuickVarS("mergeNewCharName_" + rset.getString("char_name"), new String[0]));
						++charIndex;
					}
				}
				while (charIndex <= 8)
				{
					statement.setInt(++i, -1);
					statement.setString(++i, "");
					++charIndex;
				}
				statement.executeUpdate();
			}
			catch (SQLException e)
			{
				LOG.error("Error while inserting into merge_data! Player: " + player.toString() + ' ' + player.getQuickVarsToPrint(), e);
			}

			if (player.isClanLeader() && player.containsQuickVar("mergeClanNewName"))
			{
				try (PreparedStatement statement = con.prepareStatement("UPDATE merge_data_clan SET new_clan_name = ? WHERE old_clan_id = ?"))
				{
					statement.setString(1, player.getQuickVarS("mergeClanNewName", new String[0]));
					statement.setInt(2, player.getClanId());
					statement.executeUpdate();
				}
				catch (SQLException e)
				{
					LOG.error("Error while updating merge_data_clan of Player: " + player.toString() + " Clan: " + player.getClan() + " New Name: " + player.getQuickVarS("mergeClanNewName", new String[0]), e);
				}
			}
		}
		catch (SQLException e2)
		{
			LOG.error("Couldn't connect to the Database during Merge!", e2);
		}
	}

	private static boolean checkNewAccountName(String accountNameToCheck)
	{
		if (!MergeCommunity.ACCOUNT_NAME_PATTERN.matcher(accountNameToCheck).matches())
		{
			return false;
		}

		try (Connection con = MergeAuthDatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT COUNT(login) as existingLogins FROM accounts WHERE login = ?"))
		{
			statement.setString(1, accountNameToCheck);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next() && rset.getInt("existingLogins") > 0)
				{
					return false;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while checking How many Logins exists for Account Name: " + accountNameToCheck + " in accounts of 2nd server!", e);
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT COUNT(new_login) AS existingLogins FROM merge_data WHERE new_login = ?"))
		{
			statement.setString(1, accountNameToCheck);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next() && rset.getInt("existingLogins") > 0)
				{
					return false;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while checking How many Logins exists for Account Name: " + accountNameToCheck + " in merge_data!", e);
		}
		return true;
	}

	private static boolean checkNewCharName(String charNameToCheck)
	{
		if (!MergeCommunity.CHAR_NAME_PATTERN.matcher(charNameToCheck).matches())
		{
			return false;
		}
		try (Connection con = MergeDatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("SELECT COUNT(char_name) as existingChars FROM characters WHERE char_name = ?"))
		{
			statement.setString(1, charNameToCheck);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next() && rset.getInt("existingChars") > 0)
				{
					return false;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while checking How many Chars exists for Char Name: " + charNameToCheck + " in charscters of 2nd server!", e);
		}

		try (Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement mergeCharsStatement = con.prepareStatement(
								"SELECT old_login FROM merge_data WHERE new_char_name_1 = ? OR new_char_name_2 = ? OR new_char_name_3 = ? OR new_char_name_4 = ? OR new_char_name_5 = ? OR new_char_name_6 = ? OR new_char_name_7 = ? OR new_char_name_8 = ?"))
		{
			mergeCharsStatement.setString(1, charNameToCheck);
			mergeCharsStatement.setString(2, charNameToCheck);
			mergeCharsStatement.setString(3, charNameToCheck);
			mergeCharsStatement.setString(4, charNameToCheck);
			mergeCharsStatement.setString(5, charNameToCheck);
			mergeCharsStatement.setString(6, charNameToCheck);
			mergeCharsStatement.setString(7, charNameToCheck);
			mergeCharsStatement.setString(8, charNameToCheck);
			try (ResultSet rset = mergeCharsStatement.executeQuery())
			{
				if (rset.next() && !rset.getString("old_login").isEmpty())
				{
					return false;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while checking How many characters exists for Char Name: " + charNameToCheck + " in merge_data!", e);
		}
		return true;
	}

	private static boolean checkNewClanName(int oldClanId, String clanNameToCheck)
	{
		if (!MergeCommunity.CLAN_NAME_PATTERN.matcher(clanNameToCheck).matches())
		{
			return false;
		}
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT new_clan_name FROM merge_data_clan WHERE old_clan_id = ?"))
		{
			statement.setInt(1, oldClanId);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					final String chosenNewClanName = rset.getString("new_clan_name");
					if (!chosenNewClanName.isEmpty() && chosenNewClanName.equalsIgnoreCase(clanNameToCheck))
					{
						return true;
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while checking New Clan Name from merge_data_clan. OldClanId: " + oldClanId + ", ClanNameToCheck: " + clanNameToCheck, e);
		}

		try (Connection con = MergeDatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("SELECT COUNT(name) as existingNames FROM clan_subpledges WHERE name = ? AND type = 0"))
		{
			statement.setString(1, clanNameToCheck);
			try (ResultSet rset = statement.executeQuery())
			{
				if (rset.next() && rset.getInt("existingNames") > 0)
				{
					return false;
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while checking How many Clan Names exists for Clan Name: " + clanNameToCheck + " in clan_subpledges of 2nd server!", e);
		}
		return true;
	}

	private static String getNewAccountName(String accountName)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement mergeCharsStatement = con.prepareStatement("SELECT new_login FROM merge_data WHERE old_login = ?"))
		{
			mergeCharsStatement.setString(1, accountName);
			try (ResultSet rset = mergeCharsStatement.executeQuery())
			{
				if (rset.next())
				{
					return rset.getString("new_login");
				}
			}
		}
		catch (SQLException e)
		{
			LOG.error("Error while checking New Login Name for: " + accountName + " in merge_data!", e);
		}
		return "";
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}

	@Override
	public void onShutdown()
	{
	}
}
