package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.serverpackets.CharacterSelectionInfo;

public class CharacterRestore extends L2GameClientPacket
{
	// cd
	private int _charSlot;

	@Override
	protected void readImpl()
	{
		_charSlot = readD();
	}

	@Override
	protected void runImpl()
	{
		GameClient client = getClient();

		client.markRestoredChar(_charSlot);
		CharacterSelectionInfo cl = new CharacterSelectionInfo(client.getLogin(), client.getSessionKey().playOkID1);
		sendPacket(cl);
		client.setCharSelection(cl.getCharInfo());
	}
}