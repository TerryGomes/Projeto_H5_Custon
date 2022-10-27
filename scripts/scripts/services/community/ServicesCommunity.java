package services.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.text.TextBuilder;
import l2mv.gameserver.Config;
import l2mv.gameserver.ServicesConfig;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.ItemFunctions;

public class ServicesCommunity extends Functions implements ScriptFile, ICommunityBoardHandler
{

	static final Logger _log = LoggerFactory.getLogger(ServicesCommunity.class);
	String NameItemPice = ItemFunctions.createItem(ServicesConfig.get("LevelUpItemPice", 4357)).getName();

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("ServicesCommunity: Services Community service loaded.");
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

	@Override
	public String[] getBypassCommands()
	{
		return new String[] {};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{

		if (!checkCondition(player))
		{
			return;
		}

		if (!ServicesConfig.get("LevelUpEnable", false))
		{
			show("Service is disabled.", player);
			return;
		}

		if (bypass.startsWith("_bbsservices:level"))
		{
			String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/pages/content.htm", player);

			TextBuilder _content = new TextBuilder();
			_content.append("<table width=400><tr><td align=center> Improve Services. </td></tr></table>");
			_content.append("<table border=0 width=400><tr>");
			int LvList[] = ServicesConfig.get("LevelUpList", new int[] {});
			int LvPiceList[] = ServicesConfig.get("LevelUpPiceList", new int[] {});
			for (int i = 0; i < LvList.length; i++)
			{
				if (LvList[i] > player.getLevel())
				{
					if (i % 4 == 0)
					{
						_content.append("</tr><tr>");
					}
					_content.append("<td><center><button value=\"On " + LvList[i] + " (Price:" + LvPiceList[i] + " " + NameItemPice + ")\" action=\"bypass _bbsservices:level:up:" + LvList[i] + ":" + LvPiceList[i] + "\" width=180 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\"></center></td>");
				}
			}
			_content.append("</tr></table>");
			html = html.replace("%content%", _content.toString());
			ShowBoard.separateAndSend(html, player);
		}
		if (bypass.startsWith("_bbsservices:level:up"))
		{
			String var[] = bypass.split(":");
			if (player.getInventory().destroyItemByItemId(ServicesConfig.get("LevelUpItemPice", 4357), Integer.parseInt(var[4]), "Level Up Service"))
			{
				player.addExpAndSp(Experience.LEVEL[Integer.parseInt(var[3])] - player.getExp(), 0);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
			onBypassCommand(player, "_bbsservices:level");
		}
	}

	public boolean checkCondition(Player player)
	{
		if (/* player.isInJail() || */player.getReflectionId() != 0 || player.isDead() || player.isAlikeDead() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isFlying())
		{
			player.sendMessage("Raising is not possible");
			return false;
		}
		return false;
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}

}