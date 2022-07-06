package services.community;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone.ZoneType;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.WarehouseFunctions;

/**
 * @author RuleZzz
 */
public class CommunityWarehouse implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityWarehouse.class);

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_bbswarehouse",
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		if (!Config.ALLOW_BBS_WAREHOUSE || (player == null))
		{
			return;
		}
		if (!player.isGM())
		{
			if (!Config.BBS_WAREHOUSE_ALLOW_PK && player.getKarma() > 0)
			{
				player.sendChatMessage(0, ChatType.TELL.ordinal(), "Warehouse", player.isLangRus() ? "PK нельзя использовать склад" : "PK can not use a warehouse");
				return;
			}
			if (!player.isInZone(ZoneType.peace_zone) && !player.isInZone(ZoneType.RESIDENCE))
			{
				player.sendChatMessage(0, ChatType.TELL.ordinal(), "Warehouse", player.isLangRus() ? "Вы должны быть в зону мира, чтобы использовать эту функцию." : "You must be inside peace zone to use this function.");
				return;
			}
			if (player.isCursedWeaponEquipped() || player.isInJail() || player.getReflectionId() != ReflectionManager.DEFAULT.getId() /* || player.getPvpFlag() != 0 */ || player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isFlying() || player.isTerritoryFlagEquipped() || player.isInZone(ZoneType.no_escape) || player.isInZone(ZoneType.SIEGE) || player.isInZone(ZoneType.epic))
			{
				player.sendChatMessage(0, ChatType.TELL.ordinal(), "Warehouse", "You cannot use Warehouse due restrictions. Please try again later.");
				return;
			}
		}
		final StringTokenizer st = new StringTokenizer(bypass, ":");
		st.nextToken();
		final String action = st.hasMoreTokens() ? st.nextToken() : "";
		if (action.equalsIgnoreCase("private_deposit"))
		{
			WarehouseFunctions.showDepositWindow(player);
		}
		else if (action.equalsIgnoreCase("private_retrieve"))
		{
			WarehouseFunctions.showRetrieveWindow(player, getVal(st.nextToken()));
		}
		else if (action.equalsIgnoreCase("clan_deposit"))
		{
			WarehouseFunctions.showDepositWindowClan(player);
		}
		else if (action.equalsIgnoreCase("clan_retrieve"))
		{
			WarehouseFunctions.showWithdrawWindowClan(player, getVal(st.nextToken()));
		}
		showMain(player);
	}

	private int getVal(String name)
	{
		name = name.trim();
		if (name.equalsIgnoreCase("Оружие") || name.equalsIgnoreCase("Weapon") || name.equalsIgnoreCase("1"))
		{
			return 1;
		}
		else if (name.equalsIgnoreCase("Броня") || name.equalsIgnoreCase("Armor") || name.equalsIgnoreCase("2"))
		{
			return 2;
		}
		else if (name.equalsIgnoreCase("Бижутерия") || name.equalsIgnoreCase("Jewelry") || name.equalsIgnoreCase("3"))
		{
			return 3;
		}
		else if (name.equalsIgnoreCase("Украшения") || name.equalsIgnoreCase("Accessory") || name.equalsIgnoreCase("4"))
		{
			return 4;
		}
		else if (name.equalsIgnoreCase("Предметы снабжения") || name.equalsIgnoreCase("Consumable") || name.equalsIgnoreCase("5"))
		{
			return 5;
		}
		else if (name.equalsIgnoreCase("Материалы") || name.equalsIgnoreCase("Material") || name.equalsIgnoreCase("6"))
		{
			return 6;
		}
		else if (name.equalsIgnoreCase("Ключевые материалы") || name.equalsIgnoreCase("Key Material") || name.equalsIgnoreCase("7"))
		{
			return 7;
		}
		else if (name.equalsIgnoreCase("Рецепты") || name.equalsIgnoreCase("Recipe") || name.equalsIgnoreCase("8"))
		{
			return 8;
		}
		else if (name.equalsIgnoreCase("Книги") || name.equalsIgnoreCase("Books") || name.equalsIgnoreCase("9"))
		{
			return 9;
		}
		else if (name.equalsIgnoreCase("Разное") || name.equalsIgnoreCase("Misc") || name.equalsIgnoreCase("10"))
		{
			return 10;
		}
		else if (name.equalsIgnoreCase("Прочее") || name.equalsIgnoreCase("Other") || name.equalsIgnoreCase("11"))
		{
			return 11;
		}
		return 0;
	}

	private void showMain(Player player)
	{
		if (player == null)
		{
			return;
		}
		String htm = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/warehouse.htm", player);
		final StringBuilder sb = new StringBuilder();
		htm = htm.replace("<?content?>", sb.toString());
		ShowBoard.separateAndSend(htm, player);
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("CommunityBoard: Warehouse loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			CommunityBoardManager.getInstance().removeHandler(this);
		}
	}

	@Override
	public void onShutdown()
	{
	}
}
