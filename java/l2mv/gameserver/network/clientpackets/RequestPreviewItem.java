package l2mv.gameserver.network.clientpackets;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.data.xml.holder.BuyListHolder;
import l2mv.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.network.serverpackets.ShopPreviewInfo;
import l2mv.gameserver.network.serverpackets.ShopPreviewList;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.WeaponTemplate.WeaponType;

public class RequestPreviewItem extends L2GameClientPacket
{
	// format: cdddb
	private static final Logger _log = LoggerFactory.getLogger(RequestPreviewItem.class);

	@SuppressWarnings("unused")
	private int _unknow;
	private int _listId;
	private int _count;
	private int[] _items;

	@Override
	protected void readImpl()
	{
		this._unknow = this.readD();
		this._listId = this.readD();
		this._count = this.readD();
		if (this._count * 4 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}
		this._items = new int[this._count];
		for (int i = 0; i < this._count; i++)
		{
			this._items[i] = this.readD();
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

		NpcInstance merchant = activeChar.getLastNpc();
		boolean isValidMerchant = merchant != null && merchant.isMerchantNpc();
		if (!activeChar.isGM() && (merchant == null || !isValidMerchant || !activeChar.isInRange(merchant, Creature.INTERACTION_DISTANCE)))
		{
			activeChar.sendActionFailed();
			return;
		}

		NpcTradeList list = BuyListHolder.getInstance().getBuyList(this._listId);
		if (list == null)
		{
			// TODO audit
			activeChar.sendActionFailed();
			return;
		}

		int slots = 0;
		long totalPrice = 0; // Цена на примерку каждого итема 10 Adena.

		Map<Integer, Integer> itemList = new HashMap<Integer, Integer>();
		try
		{
			for (int i = 0; i < this._count; i++)
			{
				int itemId = this._items[i];
				if (list.getItemByItemId(itemId) == null)
				{
					activeChar.sendActionFailed();
					return;
				}

				ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
				if ((template == null) || !template.isEquipable())
				{
					continue;
				}

				int paperdoll = Inventory.getPaperdollIndex(template.getBodyPart());
				if (paperdoll < 0)
				{
					continue;
				}

				if (activeChar.getRace() == Race.kamael)
				{
					if (template.getItemType() == ArmorType.HEAVY || template.getItemType() == ArmorType.MAGIC || template.getItemType() == ArmorType.SIGIL || template.getItemType() == WeaponType.NONE)
					{
						continue;
					}
				}
				else if (template.getItemType() == WeaponType.CROSSBOW || template.getItemType() == WeaponType.RAPIER || template.getItemType() == WeaponType.ANCIENTSWORD)
				{
					continue;
				}

				if (itemList.containsKey(paperdoll))
				{
					activeChar.sendPacket(SystemMsg.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
					return;
				}
				else
				{
					itemList.put(paperdoll, itemId);
				}

				totalPrice += ShopPreviewList.getWearPrice(template);
			}

			if (!activeChar.reduceAdena(totalPrice, "ItemPreview"))
			{
				activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
				return;
			}
		}
		catch (ArithmeticException ae)
		{
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}

		if (!itemList.isEmpty())
		{
			activeChar.sendPacket(new ShopPreviewInfo(itemList));
			// Schedule task
			ThreadPoolManager.getInstance().schedule(new RemoveWearItemsTask(activeChar), Config.WEAR_DELAY * 1000);
		}
	}

	public static class RemoveWearItemsTask extends RunnableImpl
	{
		private final Player _activeChar;

		public RemoveWearItemsTask(Player activeChar)
		{
			this._activeChar = activeChar;
		}

		@Override
		public void runImpl() throws Exception
		{
			this._activeChar.sendPacket(SystemMsg.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT_);
			this._activeChar.sendUserInfo(true);
		}
	}
}