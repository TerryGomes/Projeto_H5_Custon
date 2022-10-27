package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.cache.CrestCache;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestSetPledgeCrest extends L2GameClientPacket
{
	private int _length;
	private byte[] _data;

	@Override
	protected void readImpl()
	{
		this._length = this.readD();
		if (this._length == CrestCache.CREST_SIZE && this._length == this._buf.remaining())
		{
			this._data = new byte[this._length];
			this.readB(this._data);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		Clan clan = activeChar.getClan();
		if ((activeChar.getClanPrivileges() & Clan.CP_CL_EDIT_CREST) == Clan.CP_CL_EDIT_CREST)
		{
			if (clan.isPlacedForDisband())
			{
				activeChar.sendPacket(SystemMsg.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST);
				return;
			}
			if (clan.getLevel() < 3)
			{
				activeChar.sendPacket(SystemMsg.A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLANS_SKILL_LEVEL_IS_3_OR_ABOVE);
				return;
			}

			int crestId = 0;

			if (this._data != null)
			{
				crestId = CrestCache.getInstance().savePledgeCrest(clan.getClanId(), this._data);
			}
			else if (clan.hasCrest())
			{
				CrestCache.getInstance().removePledgeCrest(clan.getClanId());
			}

			clan.setCrestId(crestId);
			clan.broadcastClanStatus(false, true, false);
		}
	}
}