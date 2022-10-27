package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.network.serverpackets.components.SystemMsg;

/**
* @author VISTALL
* @date 23/03/2011
*/
public class ConfirmDlg extends SysMsgContainer<ConfirmDlg>
{
	private int _time;
	private int _requestId;

	public ConfirmDlg(SystemMsg msg, int time)
	{
		super(msg);
		this._time = time;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xf3);
		this.writeElements();
		this.writeD(this._time);
		this.writeD(this._requestId);
	}

	public void setRequestId(int requestId)
	{
		this._requestId = requestId;
	}
}