package l2mv.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class RequestLinkHtml extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestLinkHtml.class);

	// Format: cS
	private String _link;

	@Override
	protected void readImpl()
	{
		_link = readS();
	}

	@Override
	protected void runImpl()
	{

		Player actor = getClient().getActiveChar();
		if (actor == null)
		{
			return;
		}

		if (_link.contains("..") || !_link.endsWith(".htm"))
		{
			_log.warn("[RequestLinkHtml] hack? link contains prohibited characters: '" + _link + "', skipped");
			return;
		}
		try
		{
			NpcHtmlMessage msg = new NpcHtmlMessage(0);
			msg.setFile(String.valueOf(_link));
			sendPacket(msg);
		}
		catch (RuntimeException e)
		{
			_log.warn("Bad RequestLinkHtml: ", e);
		}
	}
}