package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Zone;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.utils.Location;

public class RequestDropItem extends L2GameClientPacket
{
	private int _objectId;
	private long _count;
	private Location _loc;

	@Override
	protected void readImpl()
	{
		this._objectId = this.readD();
		this._count = this.readQ();
		this._loc = new Location(this.readD(), this.readD(), this.readD());
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		if (this._count < 1 || this._loc.isNull() || activeChar.isActionsDisabled() || activeChar.isBlocked())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (!Config.ALLOW_DISCARDITEM || (!Config.ALLOW_DISCARDITEM_AT_PEACE && activeChar.isInPeaceZone() && !activeChar.isGM()))
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.RequestDropItem.Disallowed", activeChar));
			return;
		}

		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if (activeChar.isSitting() || activeChar.isDropDisabled() || activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		if (!activeChar.isInRangeSq(this._loc, 22500) || Math.abs(this._loc.z - activeChar.getZ()) > 50)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU);
			return;
		}

		ItemInstance item = activeChar.getInventory().getItemByObjectId(this._objectId);
		if (item == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if (!item.canBeDropped(activeChar, false))
		{
			activeChar.sendPacket(SystemMsg.THAT_ITEM_CANNOT_BE_DISCARDED);
			return;
		}

		if (activeChar.isInZone(Zone.ZoneType.SIEGE) || item.getAttachment() != null && !activeChar.isGM())
		{
			activeChar.sendMessage("Cannot drop items in Siege Zone!");
			return;
		}

		item.getTemplate().getHandler().dropItem(activeChar, item, this._count, this._loc);
	}
}