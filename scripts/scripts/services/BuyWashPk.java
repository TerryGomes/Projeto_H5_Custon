package services;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;

public class BuyWashPk extends Functions
{
	private static final int[] PKS =
	{
		1,
		2,
		5,
		10,
		25,
		50,
		100,
		250,
		500,
		1000
	};
	private static final int[] KARMA =
	{
		1000,
		5000,
		10000,
		20000,
		50000
	};

	public void list()
	{
		Player player = getSelf();
		if (!Config.SERVICES_WASH_PK_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		String html = HtmCache.getInstance().getNotNull("scripts/services/BuyWashPk.htm", player);

		// Pks
		final StringBuilder pks = new StringBuilder();
		for (int pk : PKS)
		{
			if (player.getPkKills() <= pk)
			{
				break;
			}
			pks.append(getPkButton(pk));
		}
		pks.append(getPkButton(player.getPkKills()));

		// Karma
		final StringBuilder karmas = new StringBuilder();
		for (int karma : KARMA)
		{
			if (player.getKarma() <= karma)
			{
				break;
			}
			karmas.append(getKarmaButton(karma));
		}
		karmas.append(getKarmaButton(player.getKarma()));

		html = html.replace("%pkList%", pks.toString());
		html = html.replace("%karmaList%", karmas.toString());
		show(html, player);
	}

	private static String getPkButton(int i)
	{
		return "<a action=\"bypass -h scripts_services.BuyWashPk:cleanPk " + i + "\"> for " + i + " PK - " + Config.SERVICES_WASH_PK_PRICE * i + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_PK_ITEM).getName() + "</a><br>";
	}

	private static String getKarmaButton(int i)
	{
		return "<a action=\"bypass -h scripts_services.BuyWashPk:cleanKarma " + i + "\"> for " + i + " Karma - " + (Config.SERVICES_WASH_KARMA_PRICE * i) / 100 + " " + ItemHolder.getInstance().getTemplate(Config.SERVICES_WASH_KARMA_ITEM).getName() + "</a><br>";
	}

	public void cleanPk(String[] param)
	{
		Player player = getSelf();
		if (!Config.SERVICES_WASH_PK_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		if (player.getPkKills() <= 0)
		{
			player.sendMessage("No pks to remove");
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		int i = Integer.parseInt(param[0]);
		if (!player.getInventory().destroyItemByItemId(Config.SERVICES_WASH_PK_ITEM, Config.SERVICES_WASH_PK_PRICE * i, "BuyWashPk$cleanPk"))
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT_);
		}
		else
		{
			int kills = player.getPkKills();
			player.setPkKills(kills - i);
			player.broadcastCharInfo();
			player.sendMessage(i + " PKs removed");
		}
	}

	public void cleanKarma(String[] param)
	{
		Player player = getSelf();
		if (!Config.SERVICES_WASH_PK_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		if (player.getKarma() <= 0)
		{
			player.sendMessage("No karma to remove");
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		int i = Integer.parseInt(param[0]);
		if (!player.getInventory().destroyItemByItemId(Config.SERVICES_WASH_KARMA_ITEM, (Config.SERVICES_WASH_KARMA_PRICE * i) / 100, "BuyWashPk$cleanKarma"))
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT_);
		}
		else
		{
			int karma = player.getKarma();
			player.setKarma(karma - i);
			player.broadcastCharInfo();
			player.sendMessage(i + " of Karma removed");
		}
	}
}