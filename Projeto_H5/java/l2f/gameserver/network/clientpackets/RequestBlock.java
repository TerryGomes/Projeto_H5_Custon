package l2f.gameserver.network.clientpackets;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class RequestBlock extends L2GameClientPacket
{
	// format: cd(S)
	private static final Logger _log = LoggerFactory.getLogger(RequestBlock.class);

	private final static int BLOCK = 0;
	private final static int UNBLOCK = 1;
	private final static int BLOCKLIST = 2;
	private final static int ALLBLOCK = 3;
	private final static int ALLUNBLOCK = 4;

	private Integer _type;
	private String targetName = null;

	@Override
	protected void readImpl()
	{
		_type = readD(); // 0x00 - block, 0x01 - unblock, 0x03 - allblock, 0x04 - allunblock

		if (_type == BLOCK || _type == UNBLOCK)
		{
			targetName = readS(16);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		switch (_type)
		{
		case BLOCK:
			activeChar.addToBlockList(targetName);
			break;
		case UNBLOCK:
			activeChar.removeFromBlockList(targetName);
			break;
		case BLOCKLIST:
			Collection<String> blockList = activeChar.getBlockList();

			if (blockList != null)
			{
				activeChar.sendPacket(SystemMsg.IGNORE_LIST);

				for (String name : blockList)
				{
					activeChar.sendMessage(name);
				}

				activeChar.sendPacket(SystemMsg.__EQUALS__);
			}
			break;
		case ALLBLOCK:
			activeChar.setBlockAll(true);
			activeChar.sendPacket(SystemMsg.YOU_ARE_NOW_BLOCKING_EVERYTHING);
			activeChar.sendEtcStatusUpdate();
			break;
		case ALLUNBLOCK:
			activeChar.setBlockAll(false);
			activeChar.sendPacket(SystemMsg.YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING);
			activeChar.sendEtcStatusUpdate();
			break;
		default:
			_log.info("Unknown 0x0a block type: " + _type);
		}
	}
}