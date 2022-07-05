package l2f.gameserver.network.clientpackets;

import l2f.commons.math.SafeMath;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.instancemanager.CastleManorManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.instances.ManorManagerInstance;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.manor.SeedProduction;

/**
 * Format: cdd[dd]
 * c    // id (0xC5)
 *
 * d    // manor id
 * d    // seeds to buy
 * [
 * d    // seed id
 * d    // count
 * ]
 */
public class RequestBuySeed extends L2GameClientPacket
{
	private int _count, _manorId;
	private int[] _items;
	private long[] _itemQ;

	@Override
	protected void readImpl()
	{
		_manorId = readD();
		_count = readD();

		if (_count * 12 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1)
		{
			_count = 0;
			return;
		}

		_items = new int[_count];
		_itemQ = new long[_count];

		for (int i = 0; i < _count; i++)
		{
			_items[i] = readD();
			_itemQ[i] = readQ();
			if (_itemQ[i] < 1)
			{
				_count = 0;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if (activeChar == null || _count == 0)
		{
			return;
		}

		if (activeChar.isActionsDisabled())
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

		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		GameObject target = activeChar.getTarget();

		ManorManagerInstance manor = target != null && target instanceof ManorManagerInstance ? (ManorManagerInstance) target : null;
		if (!activeChar.isGM() && (manor == null || !activeChar.isInRange(manor, Creature.INTERACTION_DISTANCE)))
		{
			activeChar.sendActionFailed();
			return;
		}

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _manorId);
		if (castle == null)
		{
			return;
		}

		long totalPrice = 0;
		int slots = 0;
		long weight = 0;

		try
		{
			for (int i = 0; i < _count; i++)
			{
				int seedId = _items[i];
				long count = _itemQ[i];
				long price = 0;
				long residual = 0;

				SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
				price = seed.getPrice();
				residual = seed.getCanProduce();

				if ((price < 1) || (residual < count))
				{
					return;
				}

				totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(count, price));

				ItemTemplate item = ItemHolder.getInstance().getTemplate(seedId);
				if (item == null)
				{
					return;
				}

				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, item.getWeight()));
				if (!item.isStackable() || activeChar.getInventory().getItemByItemId(seedId) == null)
				{
					slots++;
				}
			}

		}
		catch (ArithmeticException ae)
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}

		activeChar.getInventory().writeLock();
		try
		{
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

			if (!activeChar.reduceAdena(totalPrice, true, "RequestBuySeed"))
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}

			// Adding to treasury for Manor Castle
			castle.addToTreasuryNoTax(totalPrice, false, true);

			// Proceed the purchase
			for (int i = 0; i < _count; i++)
			{
				int seedId = _items[i];
				long count = _itemQ[i];

				// Update Castle Seeds Amount
				SeedProduction seed = castle.getSeed(seedId, CastleManorManager.PERIOD_CURRENT);
				seed.setCanProduce(seed.getCanProduce() - count);
				castle.updateSeed(seed.getId(), seed.getCanProduce(), CastleManorManager.PERIOD_CURRENT);

				// Add item to Inventory and adjust update packet
				activeChar.getInventory().addItem(seedId, count, "RequestBuySeed");
				activeChar.sendPacket(SystemMessage2.obtainItems(seedId, count, 0));
			}
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		activeChar.sendChanges();
	}
}