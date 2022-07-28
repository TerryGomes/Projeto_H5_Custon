package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.mail.Mail;

public class ExChangePostState extends L2GameServerPacket
{
	private boolean _receivedBoard;
	private Mail[] _mails;
	private int _changeId;

	public ExChangePostState(boolean receivedBoard, int type, Mail... n)
	{
		this._receivedBoard = receivedBoard;
		this._mails = n;
		this._changeId = type;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0xB3);
		this.writeD(this._receivedBoard ? 1 : 0);
		this.writeD(this._mails.length);
		for (Mail mail : this._mails)
		{
			this.writeD(mail.getMessageId()); // postId
			this.writeD(this._changeId); // state
		}
	}
}