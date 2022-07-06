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
		_questId = questId;
	}

	@Override
	protected void writeImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (_file != null) // TODO may not be very good to do it here ...
		{
			if (player.isGM())
			{
				Functions.sendDebugMessage(player, "HTML: " + _file);
			}
			String content = HtmCache.getInstance().getNotNull(_file, player);
			String content2 = HtmCache.getInstance().getNullable(_file, player);
			if (content2 == null)
			{
				setHtml(have_appends && _file.endsWith(".htm") ? "" : content);
			}
			else
			{
				setHtml(content);
			}
		}

		for (int i = 0; i < _replaces.size(); i += 2)
		{
			_html = _html.replaceAll(_replaces.get(i), _replaces.get(i + 1));
		}

		if (_html == null)
		{
			return;
		}

		Matcher m = objectId.matcher(_html);
		if (m != null)
		{
			_html = m.replaceAll(String.valueOf(_npcObjId));
		}

		_html = playername.matcher(_html).replaceAll(player.getName());

		player.cleanBypasses(BypassType.NPC);
		_html = player.encodeBypasses(_html, BypassType.NPC);

		writeEx(0x8d);
		writeD(_npcObjId);
		writeS(_html);
		writeD(_questId);
	}
}
