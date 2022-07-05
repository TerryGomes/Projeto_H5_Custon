package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.SkillList;

public class RequestMagicSkillList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		sendPacket(new SkillList(activeChar));
	}
}