package l2mv.gameserver.network.serverpackets;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.data.htm.bypasshandler.BypassType;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.ChatType;

public class ShowBoard extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(ShowBoard.class);

	public static final ShowBoard CLOSE_STATIC = new ShowBoard();

	private boolean _isShow = true;
	private String _htmlCode;
	private String _id;
	private List<String> _arg;
	private String _addFav = "";

	private static void sendErrorMessageToPlayer(Player player, String msg)
	{
		player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Error", msg));
	}

	public static void separateAndSend(String html, Player player)
	{
		separateAndSend(html, player, false);
	}

	public static void separateAndSend(String html, Player player, boolean force)
	{
		// Synerge - Dont show community board on players below lvl 6
		if (player.getLevel() < 6 && !force)
		{
			sendErrorMessageToPlayer(player, "Request Level 6 to Community Board.");
			return;
		}

		// Synerge - Remove tabs and enters before sending the html to make it smaller
		html = html.replace("\t", "");
		html = html.replace("\r", "");
		html = html.replace("\n", "");

		// Synerge - The bypasses encoding should be done before splitting the html, or some bypasses could end cut in half
		player.cleanBypasses(BypassType.COMMUNITY);
		html = player.encodeBypasses(html, BypassType.COMMUNITY);

		if (html.length() < 8180)
		{
			player.sendPacket(new ShowBoard(html, "101", player));
			player.sendPacket(new ShowBoard(null, "102", player));
			player.sendPacket(new ShowBoard(null, "103", player));
		}
		else if (html.length() < 8180 * 2)
		{
			player.sendPacket(new ShowBoard(html.substring(0, 8180), "101", player));
			player.sendPacket(new ShowBoard(html.substring(8180, html.length()), "102", player));
			player.sendPacket(new ShowBoard(null, "103", player));
		}
		else if (html.length() < 8180 * 3)
		{
			player.sendPacket(new ShowBoard(html.substring(0, 8180), "101", player));
			player.sendPacket(new ShowBoard(html.substring(8180, 8180 * 2), "102", player));
			player.sendPacket(new ShowBoard(html.substring(8180 * 2, html.length()), "103", player));
		}
	}

	public ShowBoard()
	{
		_id = "0";
		_isShow = false;
		_htmlCode = null;
	}

	public ShowBoard(String htmlCode, String id, Player player)
	{
		if (htmlCode != null && htmlCode.length() > 8180) // html code must not exceed 8192 bytes
		{
			_log.warn("Html '" + htmlCode + "' is too long! this will crash the client!");
			_htmlCode = "<html><body>Html was too long</body></html>";
			return;
		}
		_id = id;
		_isShow = true;

		if (player.getSessionVar("add_fav") != null)
		{
			_addFav = "bypass _bbsaddfav_List";
		}

		_htmlCode = htmlCode;
	}

	public ShowBoard(List<String> arg)
	{
		_id = "1002";
		_htmlCode = null;
		_arg = arg;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x7b);
		writeC(_isShow ? 0x01 : 0x00); // c4 1 to show community 00 to hide
		writeS("bypass _bbshome");
		writeS("bypass _bbsgetfav");
		writeS("bypass _bbsloc");
		writeS("bypass _bbsclan");
		writeS("bypass _bbsmemo");
		writeS("bypass _maillist_0_1_0_");
		writeS("bypass _friendlist_0_");
		writeS(_addFav);
		String str = _id + "\u0008";
		if (!_id.equals("1002"))
		{
			if (_htmlCode != null)
			{
				str += _htmlCode;
			}
		}
		else
		{
			for (String arg : _arg)
			{
				str += arg + " \u0008";
			}
		}
		writeS(str);
	}
}