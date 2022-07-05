package l2f.gameserver.network.clientpackets;

import org.apache.commons.lang3.tuple.Pair;

import l2f.gameserver.listener.actor.player.OnAnswerListener;
import l2f.gameserver.model.Player;

public class ConfirmDlg extends L2GameClientPacket
{
	private int _answer, _requestId;

	@Override
	protected void readImpl()
	{
		readD();
		_answer = readD();
		_requestId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(true);
		if (entry == null || entry.getKey() != _requestId)
		{
			return;
		}

		OnAnswerListener listener = entry.getValue();
		if (_answer == 1)
		{
			listener.sayYes();
		}
		else
		{
			listener.sayNo();
		}
	}
}