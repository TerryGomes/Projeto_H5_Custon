package l2f.gameserver.model.entity.CCPHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dao.JdbcEntityState;
import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.dao.ItemsDAO;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.ItemInstance.ItemLocation;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;

public class CCPRepair
{
	private static final Logger _log = LoggerFactory.getLogger(CCPRepair.class);

	public static boolean repairChar(Player activeChar, String target)
	{
		if (!target.isEmpty())
		{
			if (activeChar.getName().equalsIgnoreCase(target))
			{
				Functions.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.YouCantRepairYourself", activeChar), activeChar);
				return false;
			}

			int objId = 0;

			for (Map.Entry<Integer, String> e : activeChar.getAccountChars().entrySet())
			{
				if (e.getValue().equalsIgnoreCase(target))
				{
					objId = e.getKey();
					break;
				}
			}

			if (objId == 0)
			{
				Functions.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.YouCanRepairOnlyOnSameAccount", activeChar), activeChar);
				return false;
			}
			else if (World.getPlayer(objId) != null)
			{
				Functions.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.CharIsOnline", activeChar), activeChar);
				return false;
			}

			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rs = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement("SELECT karma FROM characters WHERE obj_Id=?");
				statement.setInt(1, objId);
				statement.execute();
				rs = statement.getResultSet();

				int karma = 0;

				rs.next();

				karma = rs.getInt("karma");

				DbUtils.close(statement, rs);

				if (karma > 0)
				{
					statement = con.prepareStatement("UPDATE characters SET x=17144, y=170156, z=-3502 WHERE obj_Id=?");
					statement.setInt(1, objId);
					statement.execute();
					DbUtils.close(statement);
				}
				else
				{
					statement = con.prepareStatement("UPDATE characters SET x=0, y=0, z=0 WHERE obj_Id=?");
					statement.setInt(1, objId);
					statement.execute();
					DbUtils.close(statement);

					Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(objId, ItemLocation.PAPERDOLL);
					for (ItemInstance item : items)
					{
						if (item.isEquipped())
						{
							item.setEquipped(false);
							item.setJdbcState(JdbcEntityState.UPDATED);
							item.update();
						}
					}
				}

				statement = con.prepareStatement("DELETE FROM character_variables WHERE obj_id=? AND type='user-var' AND name='reflection'");
				statement.setInt(1, objId);
				statement.execute();
				DbUtils.close(statement);

				Functions.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.RepairDone", activeChar), activeChar);
				return true;
			}
			catch (SQLException e)
			{
				_log.error("Error while repairing Char", e);
				return false;
			}
			finally
			{
				DbUtils.closeQuietly(con, statement, rs);
			}
		}
		else
		{
			activeChar.sendMessage(".repair <name>");
			return false;
		}
	}

	public static String getCharsOnAccount(String myCharName, String accountName)
	{
		List<String> chars = new ArrayList<String>();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters WHERE account_name=?"))
		{
			statement.setString(1, accountName);

			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					chars.add(rset.getString("char_name"));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while getting Chars on Account", e);
		}

		StringBuilder resultBuilder = new StringBuilder();
		for (String charName : chars)
		{
			if (!charName.equalsIgnoreCase(myCharName))
			{
				resultBuilder.append(charName).append(';');
			}
		}

		if (resultBuilder.length() == 0)
		{
			return "";
		}

		return resultBuilder.substring(0, resultBuilder.length() - 1);
	}
}
