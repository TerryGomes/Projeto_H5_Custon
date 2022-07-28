package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;

/**
 * @author VISTALL
 */
public class RequestExBR_LectureMark extends L2GameClientPacket
{
	public static final int INITIAL_MARK = 1;
	public static final int EVANGELIST_MARK = 2;
	public static final int OFF_MARK = 3;

	private int _mark;

	@Override
	protected void readImpl()
	{
		this._mark = this.readC();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if ((player == null) || !Config.EX_LECTURE_MARK)
		{
			return;
		}

		switch (this._mark)
		{
		case INITIAL_MARK:
		case EVANGELIST_MARK:
		case OFF_MARK:
			// TODO [VISTALL]check whether you can include - from the first 6 months of the chara
			player.setLectureMark(this._mark);
			player.broadcastUserInfo(true);
			break;
		}
	}
}