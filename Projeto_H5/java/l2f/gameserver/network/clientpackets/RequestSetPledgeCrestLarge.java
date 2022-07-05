package l2f.gameserver.network.clientpackets;

import l2f.gameserver.cache.CrestCache;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class RequestSetPledgeCrestLarge extends L2GameClientPacket
{
	private int _length;
	private byte[] _data;

	/**
	 * format: chd(b)
	 */
	@Override
	protected void readImpl()
	{
		_length = readD();
		if (_length == CrestCache.LARGE_CREST_SIZE && _length == _buf.remaining())
		{
			_data = new byte[_length];
			readB(_data);
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

		Clan clan = activeChar.getClan();
		if (clan == null)
		{
			return;
		}

		if ((activeChar.getClanPrivileges() & Clan.CP_CL_EDIT_CREST) == Clan.CP_CL_EDIT_CREST)
		{
			if (clan.isPlacedForDisband())
			{
				activeChar.sendPacket(SystemMsg.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST);
				return;
			}
			if (clan.getCastle() == 0 && clan.getHasHideout() == 0)
			{
				activeChar.sendPacket(SystemMsg.THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED);
				return;
			}

			int crestId = 0;

			if (_data != null)
			{
				crestId = CrestCache.getInstance().savePledgeCrestLarge(clan.getClanId(), _data);
				activeChar.sendPacket(SystemMsg.THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED);
			}
			else if (clan.hasCrestLarge())
			{
				CrestCache.getInstance().removePledgeCrestLarge(clan.getClanId());
			}

			clan.setCrestLargeId(crestId);
			clan.broadcastClanStatus(false, true, false);
		}
	}
}