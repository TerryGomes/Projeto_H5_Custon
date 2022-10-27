package l2mv.gameserver.network.clientpackets;

import java.util.Collections;
import java.util.List;

import l2mv.commons.math.SafeMath;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.instancemanager.CastleManorManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Manor;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.instances.ManorManagerInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.manor.CropProcure;

@SuppressWarnings("unused")
// TODO
public class RequestProcureCrop extends L2GameClientPacket
{
	// format: cddb
	private int _manorId;
	private int _count;
	private int[] _items;
	private long[] _itemQ;
	private List<CropProcure> _procureList = Collections.emptyList();

	@Override
	protected void readImpl()
	{
		this._manorId = this.readD();
		this._count = this.readD();
		if (this._count * 16 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}
		this._items = new int[this._count];
		this._itemQ = new long[this._count];
		for (int i = 0; i < this._count; i++)
		{
			this.readD(); // service
			this._items[i] = this.readD();
			this._itemQ[i] = this.readQ();
			if (this._itemQ[i] < 1)
			{
				this._count = 0;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || this._count == 0)
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

		if (activeChar.isInTrade() || (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()))
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

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
		if (castle == null)
		{
			return;
		}

		int slots = 0;
		long weight = 0;

		try
		{
			for (int i = 0; i < this._count; i++)
			{
				int itemId = this._items[i];
				long count = this._itemQ[i];

				CropProcure crop = castle.getCrop(itemId, CastleManorManager.PERIOD_CURRENT);
				if (crop == null)
				{
					return;
				}

				int rewradItemId = Manor.getInstance().getRewardItem(itemId, castle.getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());
				long rewradItemCount = Manor.getInstance().getRewardAmountPerCrop(castle.getId(), itemId, castle.getCropRewardType(itemId));

				rewradItemCount = SafeMath.mulAndCheck(count, rewradItemCount);

				ItemTemplate template = ItemHolder.getInstance().getTemplate(rewradItemId);
				if (template == null)
				{
					return;
				}

				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, template.getWeight()));
				if (!template.isStackable() || activeChar.getInventory().getItemByItemId(itemId) == null)
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

			this._procureList = castle.getCropProcure(CastleManorManager.PERIOD_CURRENT);

			for (int i = 0; i < this._count; i++)
			{
				int itemId = this._items[i];
				long count = this._itemQ[i];

				int rewardItemId = Manor.getInstance().getRewardItem(itemId, castle.getCrop(itemId, CastleManorManager.PERIOD_CURRENT).getReward());
				long rewardItemCount = Manor.getInstance().getRewardAmountPerCrop(castle.getId(), itemId, castle.getCropRewardType(itemId));

				rewardItemCount = SafeMath.mulAndCheck(count, rewardItemCount);

				if (!activeChar.getInventory().destroyItemByItemId(itemId, count, "RequestProduceCrop"))
				{
					continue;
				}

				ItemInstance item = activeChar.getInventory().addItem(rewardItemId, rewardItemCount, "RequestProduceCrop");
				if (item == null)
				{
					continue;
				}

				// Send Char Buy Messages
				activeChar.sendPacket(SystemMessage2.obtainItems(rewardItemId, rewardItemCount, 0));
			}
		}
		catch (ArithmeticException ae)
		{
			// TODO audit
			this._count = 0;
		}
		finally
		{
			activeChar.getInventory().writeUnlock();
		}

		activeChar.sendChanges();
	}
}