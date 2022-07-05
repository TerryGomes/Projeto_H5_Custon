package services;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Hero;
import l2f.gameserver.model.entity.olympiad.Olympiad;
import l2f.gameserver.network.serverpackets.SocialAction;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.StatsSet;

public class BuyHero extends Functions
{
	public void list()
	{
		Player player = getSelf();
		if (!Config.SERVICES_HERO_SELL_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}
		String html = null;

		html = HtmCache.getInstance().getNotNull("scripts/services/BuyHero.htm", player);
		String add = "";
		for (int i = 0; i < Config.SERVICES_HERO_SELL_DAY.length; i++)
		{
			add += "<a action=\"bypass -h scripts_services.BuyHero:get " + i + "\">" + "for " + Config.SERVICES_HERO_SELL_DAY[i] + " days - " + Config.SERVICES_HERO_SELL_PRICE[i] + " "
						+ ItemHolder.getInstance().getTemplate(Config.SERVICES_HERO_SELL_ITEM[i]).getName() + "</a><br>";
		}
		html = html.replaceFirst("%toreplace%", add);

		show(html, player);
	}

	public void get(String[] param)
	{
		Player player = getSelf();
		if (!Config.SERVICES_HERO_SELL_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}
		int i = Integer.parseInt(param[0]);
		if ((Functions.getItemCount(player, Config.SERVICES_HERO_SELL_ITEM[i]) >= Config.SERVICES_HERO_SELL_PRICE[i]))
		{
			if (!player.isHero())
			{
				player.setVar("HeroPeriod", (System.currentTimeMillis() + 60 * 1000 * 60 * 24 * Config.SERVICES_HERO_SELL_DAY[i]), -1);
				Functions.removeItem(player, Config.SERVICES_HERO_SELL_ITEM[i], Config.SERVICES_HERO_SELL_PRICE[i], "BuyHero$get");

				StatsSet hero = new StatsSet();
				hero.set(Olympiad.CLASS_ID, player.getBaseClassId());
				hero.set(Olympiad.CHAR_ID, player.getObjectId());
				hero.set(Olympiad.CHAR_NAME, player.getName());
				hero.set(Hero.ACTIVE, 1);

				List<StatsSet> heroesToBe = new ArrayList<StatsSet>();
				heroesToBe.add(hero);

				Hero.getInstance().computeNewHeroes(heroesToBe);
				player.setHero(true);
				Hero.addSkills(player);
				player.updatePledgeClass();
				if (player.isHero())
				{
					player.broadcastPacket(new SocialAction(player.getObjectId(), 16));
				}
				player.broadcastUserInfo(true);
			}
		}
		else
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		}
	}
}