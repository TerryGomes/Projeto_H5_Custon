package services;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.DeclensionKey;
import l2mv.gameserver.utils.Util;

public class ClearPK extends Functions
{
	public void clear()
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (Config.SERVICES_CLEAR_PK_COUNT == 0)
		{
			player.sendMessage("Service is turned off.");
			return;
		}
		if (player.getPkKills() == 0)
		{
			player.sendMessage("You don't have PK points");
			return;
		}
		if ((player.isDead()) || (player.isAlikeDead()) || (player.isCastingNow()) || (player.isInCombat()) || (player.isAttackingNow()) || (player.isFlying()))
		{
			player.sendMessage("You must be in peace zone.");
			return;
		}
		if (Util.getPay(player, Config.SERVICES_CLEAR_PK_PRICE_ITEM_ID, Config.SERVICES_CLEAR_PK_PRICE, true))
		{
			int pkCount = player.getPkKills() - Config.SERVICES_CLEAR_PK_COUNT;
			int msgpk = pkCount < 0 ? player.getPkKills() : Config.SERVICES_CLEAR_PK_COUNT;

			CustomMessage msg = new CustomMessage("{0} PK {1} is clear.").addNumber(msgpk).addString(Util.declension(pkCount, DeclensionKey.POINT));
			if (pkCount >= 0)
			{
				player.setPkKills(pkCount);
			}
			else
			{
				player.setPkKills(0);
			}
			player.sendPacket(new ExShowScreenMessage(msg.toString(), 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
			player.sendMessage(msg);
			player.sendChanges();
		}
	}
}
