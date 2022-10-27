package l2mv.gameserver.network.serverpackets;

import java.util.regex.Matcher;

import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.htm.bypasshandler.BypassType;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.scripts.Functions;

/**
 * @author VISTALL
 * @date 16:25/24.04.2011
 */
public class ExNpcQuestHtmlMessage extends NpcHtmlMessage
{
	private final int _questId;

	public ExNpcQuestHtmlMessage(int npcObjId, int questId)
	{
		super(npcObjId);
		this._questId = questId;
	}

	@Override
	protected void writeImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (this._file != null) // TODO may not be very good to do it here ...
		{
			if (player.isGM())
			{
				Functions.sendDebugMessage(player, "HTML: " + this._file);
			}
			String content = HtmCache.getInstance().getNotNull(this._file, player);
			String content2 = HtmCache.getInstance().getNullable(this._file, player);
			if (content2 == null)
			{
				this.setHtml(this.have_appends && this._file.endsWith(".htm") ? "" : content);
			}
			else
			{
				this.setHtml(content);
			}
		}

		for (int i = 0; i < this._replaces.size(); i += 2)
		{
			this._html = this._html.replaceAll(this._replaces.get(i), this._replaces.get(i + 1));
		}

		if (this._html == null)
		{
			return;
		}

		Matcher m = objectId.matcher(this._html);
		if (m != null)
		{
			this._html = m.replaceAll(String.valueOf(this._npcObjId));
		}

		this._html = playername.matcher(this._html).replaceAll(player.getName());

		player.cleanBypasses(BypassType.NPC);
		this._html = player.encodeBypasses(this._html, BypassType.NPC);

		this.writeEx(0x8d);
		this.writeD(this._npcObjId);
		this.writeS(this._html);
		this.writeD(this._questId);
	}
}
