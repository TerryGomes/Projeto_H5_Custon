package services.community;

import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.model.Player;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

/**
 * info_folder = папка для категорий страниц - String
 * info_page = имя файла, название страницы - String
 * Вызов всплывающего окна у НПС - [scripts_services.ShowInfo:show info_folder info_page|Имя папки, Имя страницы]
 * Вызов всплывающего окна из комьюнити - [bypass _bbsscripts:services.community.ShowInfo:show info_folder info_page|Имя папки, Имя страницы]
**/

public class ShowInfo extends Functions implements ScriptFile
{
	public void show(String[] param)
	{
		Player player = getSelf();
		String info_folder = "";
		String info_page = "";

		if (player == null)
		{
			return;
		}

		if (param.length != 2)
		{
			String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "wiki/error_page.htm", player);
			show(html, player);
			return;
		}

		info_folder = String.valueOf(param[0]);
		info_page = String.valueOf(param[1]);

		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "wiki/" + info_folder + "/" + info_page + ".htm", player);
		show(html, player);
	}

	@Override
	public void onLoad()
	{
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