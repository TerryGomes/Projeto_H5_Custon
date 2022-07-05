package l2f.gameserver.utils;

import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.model.Player;

public class BbsUtil
{
	public static String htmlAll(String htm, Player player)
	{
		String html_all = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "block/allpages.htm", player);
		String html_menu = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "block/menu.htm", player);
		String html_copy = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "block/copyright.htm", player);
		html_all = html_all.replace("%main_menu%", html_menu);
		html_all = html_all.replace("%body_page%", htm);
		html_all = html_all.replace("%copyright%", html_copy);
		html_all = html_all.replace("%copyrightsym%", "©");
		return html_all;
	}

	public static String htmlNotAll(String htm, Player player)
	{
		String html_all = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "block/allpages.htm", player);
		html_all = html_all.replace("%body_page%", htm);
		return html_all;
	}

	public static String htmlBuff(String htm, Player player)
	{
		String html_option = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "pages/buffer/block/option.htm", player);
		htm = htm.replace("%main_optons%", html_option);
		htm = htmlAll(htm, player);
		return htm;
	}
}