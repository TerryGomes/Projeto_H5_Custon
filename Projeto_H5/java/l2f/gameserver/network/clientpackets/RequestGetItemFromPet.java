package l2f.gameserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.PetInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.PcInventory;
import l2f.gameserver.model.items.PetInventory;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class RequestGetItemFromPet extends L2GameClientPacket
{
	private static final Logger _log = LoggerFactory.getLogger(RequestGetItemFromPet.class);

	private int _objectId;
	private long _amount;
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
		_amount = readQ();
		_unknown = readD(); // = 0 for most trades
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null || _amount < 1)
		{
			return;
		}

		if (!(activeChar.getPet() instanceof PetInstance))
		{
			activeChar.sendActionFailed();
			return;
		}

		PetInstance pet = (PetInstance) activeChar.getPet();
		if ((pet == null) || activeChar.isOutOfControl())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
			return;
		}

		if (activeChar.isInTrade())
		{
			activeChar.sendActionFailed();
			return;
		}

		if (activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}

		PetInventory petInventory = pet.getInventory();
		PcInventory playerInventory = activeChar.getInventory();

		ItemInstance item = petInventory.getItemByObjectId(_objectId);
		if (item == null || item.getCount() < _amount || item.isEquipped())
		{
			activeChar.sendActionFailed();
			return;
		}

		int slots = 0;
		long weight = item.getTemplate().getWeight() * _amount;
		if (!item.getTemplate().isStackable() || activeChar.getInventory().getItemByItemId(item.getItemId()) == null)
		{
			slots = 1;
		}

		if (!activeChar.getInventory().validateWeight(weight))
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
			return;
		}

		if (!activeChar.getInventory().validateCapacity(slots))
		{
			activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
			return;
		}

		playerInventory.addItem(petInventory.removeItemByObjectId(_objectId, _amount, "Pet " + activeChar.toString(), "GetItemFromPet"), "GiveItemToPet");

		pet.sendChanges();
		activeChar.sendChanges();
	}
}