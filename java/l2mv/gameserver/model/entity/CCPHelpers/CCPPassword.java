package l2mv.gameserver.model.entity.CCPHelpers;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.gspackets.ChangePassword;
import l2mv.gameserver.utils.Util;

public class CCPPassword
{
	public static boolean setNewPassword(Player player, String[] parts)
	{
		if (parts == null || parts.length == 0)
		{
			player.sendMessage("Fill every field!");
			return false;
		}

		if (Config.PASSWORD_PAY_ID > 0)
		{
			if (player.getInventory().getCountOf(Config.PASSWORD_PAY_ID) < Config.PASSWORD_PAY_COUNT)
			{
				player.sendMessage("In order to change password you must pay " + Config.PASSWORD_PAY_COUNT + " " + Config.PASSWORD_PAY_ID + "");
				return false;
			}
		}

		if (parts.length != 3)
		{
			player.sendMessage("Incorrect values!");
			return false;
		}

		if (!parts[1].equals(parts[2]))
		{
			player.sendMessage("New passwords arent the same!");
			return false;
		}

		if (parts[1].equals(parts[0]))
		{
			player.sendMessage("New password cannot be the same as old one!");
			return false;
		}

		if ((parts[1].length() < 5) || (parts[1].length() > 20))
		{
			player.sendMessage("Incorrect size of the new password!");
			return false;
		}

		if (!Util.isMatchingRegexp(parts[1], Config.APASSWD_TEMPLATE))
		{
			player.sendMessage("Incorrect value in new password!");
			return false;
		}

		AuthServerCommunication.getInstance().sendPacket(new ChangePassword(player.getAccountName(), parts[0], parts[1], "0"));
		return true;
	}
}
