package l2mv.gameserver.model.premium;

import l2mv.gameserver.Config;
import l2mv.gameserver.dao.AccountBonusDAO;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.Bonus;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.gspackets.BonusRequest;
import l2mv.gameserver.network.serverpackets.ExBR_PremiumState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;

public class PremiumEnd
{
	private static PremiumEnd _instance = new PremiumEnd();

	public static PremiumEnd getInstance()
	{
		return _instance;
	}

	public void done(Player player)
	{
		off(player.getAccountName());
		resetBonuses(player);

		GameClient client = player.getNetConnection();
		client.setBonusExpire(0);
		String html = null;
		if (Config.ENTER_WORLD_SHOW_HTML_PREMIUM_DONE)
		{
			if (player.getClan() == null)
			{
				html = HtmCache.getInstance().getNotNull("scripts/services/Premium/done.htm", player);
				html.replace("%playername%", player.getName());
			}
			else
			{
				String msg = "You don't have Premium Account, you can buy it from Community Board.";
				player.sendPacket(new ExShowScreenMessage(msg, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false));
				player.sendMessage(msg);
			}
		}
	}

	public void stopBonusTask(Player player, boolean silence)
	{
		if (player.getExpiration() != null)
		{
			player.getExpiration().cancel(false);
			player.setExpiration(null);
			if (silence)
			{
				resetBonuses(player);
			}
			else
			{
				stopBonuses(player);
			}
		}
	}

	public void resetBonuses(Player player)
	{
		Bonus bonus = player.getBonus();
		bonus.setRateXp(1.);
		bonus.setRateSp(1.);
		bonus.setDropSiege(1.);
		bonus.setDropAdena(1.);
		bonus.setDropItems(1.);
		bonus.setDropSpoil(1.);
		bonus.setWeight(1.);
		bonus.setCraftChance(0);
		bonus.setMasterWorkChance(0);
		bonus.setAttributeChance(0);

		Party party = player.getParty();
		if (party != null)
		{
			party.recalculatePartyData();
		}
	}

	private void off(String account)
	{
		if (Config.PREMIUM_ACCOUNT_TYPE == 2)
		{
			AccountBonusDAO.getInstance().delete(account);
		}
		else
		{
			AuthServerCommunication.getInstance().sendPacket(new BonusRequest(account, 0, 0));
		}
	}

	public void stopBonuses(Player player)
	{
		if (player.getQuickVarB("PremiumEnd", new boolean[]
		{
			false
		}))
		{
			return;
		}
		GameClient client = player.getNetConnection();
		if (client != null)
		{
			client.setBonusExpire(0);
		}
		resetBonuses(player);

		String msg = "Your luck is now at normal value.";
		player.sendPacket(new ExShowScreenMessage("", 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false));
		player.sendPacket(new ExBR_PremiumState(player, false));
		player.sendMessage(msg);
		off(player.getAccountName());

		PremiumStart.getInstance().updateItems(true, player);
		player.broadcastUserInfo(true);
		player.getQuickVarB("PremiumEnd", Boolean.valueOf(true));
	}
}
