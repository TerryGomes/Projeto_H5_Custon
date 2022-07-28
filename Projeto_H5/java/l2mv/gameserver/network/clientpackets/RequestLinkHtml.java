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
		this._link = this.readS();
	}

	@Override
	protected void runImpl()
	{

		Player actor = this.getClient().getActiveChar();
		if (actor == null)
		{
			return;
		}

		if (this._link.contains("..") || !this._link.endsWith(".htm"))
		{
			_log.warn("[RequestLinkHtml] hack? link contains prohibited characters: '" + this._link + "', skipped");
			return;
		}
		try
		{
			NpcHtmlMessage msg = new NpcHtmlMessage(0);
			msg.setFile(String.valueOf(this._link));
			this.sendPacket(msg);
		}
		catch (RuntimeException e)
		{
			_log.warn("Bad RequestLinkHtml: ", e);
		}
	}
}