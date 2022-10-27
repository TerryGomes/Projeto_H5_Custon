package l2mv.gameserver.network.clientpackets;

import org.apache.commons.lang3.tuple.Pair;

import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.Player;

public class ConfirmDlg extends L2GameClientPacket
{
	private int _answer, _requestId;

	@Override
	protected void readImpl()
	{
		this.readD();
		this._answer = this.readD();
		this._requestId = this.readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(true);
		if (entry == null || entry.getKey() != this._requestId)
		{
			return;
		}

		OnAnswerListener listener = entry.getValue();
		if (this._answer == 1)
		{
			listener.sayYes();
		}
		else
		{
			listener.sayNo();
		}
	}
}