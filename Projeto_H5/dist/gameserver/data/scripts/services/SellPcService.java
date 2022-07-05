package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.item.ItemTemplate;

/**
 * @author Buemo
 * @editor RuleZzz
 * @update Buemo
 * @date 30.12.11
 */
public class SellPcService extends Functions implements ScriptFile
{
	private static final Logger _log = LoggerFactory.getLogger(Player.class);

	public void dialog()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		show("scripts/services/SellPcService.htm", player);
	}

	public void pay(String[] param)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		int points = Integer.parseInt(param[0]); // поинты (очки)
		int itemId = Integer.parseInt(param[1]); // ид предмета, который взымается
		int itemCount = Integer.parseInt(param[2]); // количество предмета, который взымается

		ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId); // id итема

		if (item == null)
		{
			return;
		}

		ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
		if (pay != null && pay.getCount() >= itemCount) // кол-во денег
		{
			player.addPcBangPoints(points, false);
			player.getInventory().destroyItem(pay, itemCount, "SellPcService");
			player.sendMessage("You have purchased " + points + " PC-Points");
		}
		else
		{ // кол-во денег
			player.sendMessage("You are not " + item.getName());
		}
	}

	@Override
	public void onLoad()
	{
		_log.info("Loaded Service: SellPcService");
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}
