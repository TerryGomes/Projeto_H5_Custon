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
		this._id = "0";
		this._isShow = false;
		this._htmlCode = null;
	}

	public ShowBoard(String htmlCode, String id, Player player)
	{
		if (htmlCode != null && htmlCode.length() > 8180) // html code must not exceed 8192 bytes
		{
			_log.warn("Html '" + htmlCode + "' is too long! this will crash the client!");
			this._htmlCode = "<html><body>Html was too long</body></html>";
			return;
		}
		this._id = id;
		this._isShow = true;

		if (player.getSessionVar("add_fav") != null)
		{
			this._addFav = "bypass _bbsaddfav_List";
		}

		this._htmlCode = htmlCode;
	}

	public ShowBoard(List<String> arg)
	{
		this._id = "1002";
		this._htmlCode = null;
		this._arg = arg;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x7b);
		this.writeC(this._isShow ? 0x01 : 0x00); // c4 1 to show community 00 to hide
		this.writeS("bypass _bbshome");
		this.writeS("bypass _bbsgetfav");
		this.writeS("bypass _bbsloc");
		this.writeS("bypass _bbsclan");
		this.writeS("bypass _bbsmemo");
		this.writeS("bypass _maillist_0_1_0_");
		this.writeS("bypass _friendlist_0_");
		this.writeS(this._addFav);
		String str = this._id + "\u0008";
		if (!this._id.equals("1002"))
		{
			if (this._htmlCode != null)
			{
				str += this._htmlCode;
			}
		}
		else
		{
			for (String arg : this._arg)
			{
				str += arg + " \u0008";
			}
		}
		this.writeS(str);
	}
}