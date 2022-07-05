package l2f.loginserver.gameservercon.gspackets;

import org.apache.commons.lang3.ArrayUtils;

import l2f.loginserver.accounts.SessionManager;
import l2f.loginserver.gameservercon.GameServer;
import l2f.loginserver.gameservercon.ReceivablePacket;

/**
 * @author VISTALL
 * @date 20:52/25.03.2011
 */
public class SetAccountInfo extends ReceivablePacket
{
	private String _account;
	private int _size;
	private int[] _deleteChars;

	@Override
	protected void readImpl()
	{
		_account = readS();
		_size = readC();
		int size = readD();
		if (size > 7 || size <= 0)
		{
			_deleteChars = ArrayUtils.EMPTY_INT_ARRAY;
		}
		else
		{
			_deleteChars = new int[size];
			for (int i = 0; i < _deleteChars.length; i++)
			{
				_deleteChars[i] = readD();
			}
		}
	}

	@Override
	protected void runImpl()
	{
		final GameServer gs = getGameServer();
		if (gs.isAuthed())
		{
			final SessionManager.Session session = SessionManager.getInstance().getSessionByName(_account);
			if (session == null)
			{
				return;
			}
			session.getAccount().addAccountInfo(gs.getId(), _size, _deleteChars);
		}
	}
}
