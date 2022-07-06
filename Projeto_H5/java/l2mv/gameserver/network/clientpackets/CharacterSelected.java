package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.GameClient.GameClientState;
import l2mv.gameserver.network.serverpackets.ActionFail;
import l2mv.gameserver.network.serverpackets.CharSelected;
import l2mv.gameserver.utils.AutoBan;

public class CharacterSelected extends L2GameClientPacket
{
	private int _charSlot;

	/**
	 * Format: cdhddd
	 */
	@Override
	protected void readImpl()
	{
		_charSlot = readD();
	}

	@Override
	protected void runImpl()
	{
		GameClient client = getClient();

		if (Config.SECOND_AUTH_ENABLED && !client.getSecondaryAuth().isAuthed())
		{
			client.getSecondaryAuth().openDialog();
			return;
		}

		if (client.getActiveChar() != null)
		{
			return;
		}

		int objId = client.getObjectIdForSlot(_charSlot);
		if (AutoBan.checkIsBanned(objId))
		{
			sendPacket(ActionFail.STATIC);
			return;
		}

		Player activeChar = client.loadCharFromDisk(_charSlot, objId);
		if (activeChar == null)
		{
			sendPacket(ActionFail.STATIC);
			return;
		}

		if (activeChar.getAccessLevel() < 0)
		{
			activeChar.setAccessLevel(0);
		}

		client.setState(GameClientState.IN_GAME);

		sendPacket(new CharSelected(activeChar, client.getSessionKey().playOkID1));
	}
}