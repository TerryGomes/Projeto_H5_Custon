package l2f.gameserver.utils;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.math.SafeMath;
import l2f.gameserver.Config;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.CCPHelpers.itemLogs.ItemActionType;
import l2f.gameserver.model.entity.CCPHelpers.itemLogs.ItemLogHandler;
import l2f.gameserver.model.entity.auction.AuctionManager;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.TradeItem;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public final class TradeHelper
{
	private TradeHelper()
	{
	}

	public static boolean checksIfCanOpenStore(Player player, int storeType)
	{
		if (!player.getPlayerAccess().UseTrade)
		{
			player.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
			return false;
		}

		if (player.getLevel() < Config.SERVICES_TRADE_MIN_LEVEL)
		{
			player.sendMessage(new CustomMessage("trade.NotHavePermission", player).addNumber(Config.SERVICES_TRADE_MIN_LEVEL));
			return false;
		}

		String tradeBan = player.getVar("tradeBan");
		if (tradeBan != null && (tradeBan.equals("-1") || Long.parseLong(tradeBan) >= System.currentTimeMillis()))
		{
			player.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_BLOCKED_FROM_USING_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP);
			return false;
		}

		String BLOCK_ZONE = storeType == Player.STORE_PRIVATE_MANUFACTURE ? Zone.BLOCKED_ACTION_PRIVATE_WORKSHOP : Zone.BLOCKED_ACTION_PRIVATE_STORE;
		if (storeType != Player.STORE_PRIVATE_BUFF && player.isActionBlocked(BLOCK_ZONE))
		{
			if (!Config.SERVICES_NO_TRADE_ONLY_OFFLINE || Config.SERVICES_NO_TRADE_ONLY_OFFLINE && player.isInOfflineMode())
			{
				player.sendPacket(storeType == Player.STORE_PRIVATE_MANUFACTURE ? SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE : SystemMsg.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
				return false;
			}
		}

		if (player.isCastingNow())
		{
			player.sendPacket(SystemMsg.A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL);
			return false;
		}

		if (player.isInCombat())
		{
			player.sendPacket(SystemMsg.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
			return false;
		}

		if (player.isInFightClub() && !player.getFightClubEvent().canOpenStore(player))
		{
			return false;
		}

		if (player.isActionsDisabled() || player.isMounted() || player.isInOlympiadMode() || player.isInDuel() || player.isProcessingRequest() || player.getTransformation() > 0)
		{
			return false;
		}

		if (Config.SERVICES_TRADE_ONLY_FAR)
		{
			boolean tradenear = false;
			for (Player p : World.getAroundPlayers(player, Config.SERVICES_TRADE_RADIUS, 200))
			{
				if (p.isInStoreMode())
				{
					tradenear = true;
					break;
				}
			}

			if (World.getAroundNpc(player, Config.SERVICES_TRADE_RADIUS + 100, 200).size() > 0)
			{
				tradenear = true;
			}

			if (tradenear)
			{
				player.sendMessage(new CustomMessage("trade.OtherTradersNear", player));
				return false;
			}
		}

		return true;
	}

	public final static void purchaseItem(Player buyer, Player seller, TradeItem item)
	{
		long price = item.getCount() * item.getOwnersPrice();
		if (!item.getItem().isStackable())
		{
			if (item.getEnchantLevel() > 0)
			{
				seller.sendPacket(new SystemMessage2(SystemMsg.S2S3_HAS_BEEN_SOLD_TO_C1_AT_THE_PRICE_OF_S4_ADENA).addString(buyer.getName()).addInteger(item.getEnchantLevel()).addItemName(item.getItemId()).addLong(price));
				buyer.sendPacket(new SystemMessage2(SystemMsg.S2S3_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S4_ADENA).addString(seller.getName()).addInteger(item.getEnchantLevel()).addItemName(item.getItemId()).addLong(price));
			}
			else
			{
				seller.sendPacket(new SystemMessage2(SystemMsg.S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA).addString(buyer.getName()).addItemName(item.getItemId()).addLong(price));
				buyer.sendPacket(new SystemMessage2(SystemMsg.S2_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S3_ADENA).addString(seller.getName()).addItemName(item.getItemId()).addLong(price));
			}
		}
		else
		{
			seller.sendPacket(new SystemMessage2(SystemMsg.S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA).addString(buyer.getName()).addItemName(item.getItemId()).addLong(item.getCount()).addLong(price));
			buyer.sendPacket(new SystemMessage2(SystemMsg.S3_S2_HAS_BEEN_PURCHASED_FROM_C1_FOR_S4_ADENA).addString(seller.getName()).addItemName(item.getItemId()).addLong(item.getCount()).addLong(price));
		}
	}

	public final static long getTax(Player seller, long price)
	{
		long tax = (long) (price * Config.SERVICES_TRADE_TAX / 100);
		if (seller.isInZone(Zone.ZoneType.offshore))
		{
			tax = (long) (price * Config.SERVICES_OFFSHORE_TRADE_TAX / 100);
		}
		if (Config.SERVICES_TRADE_TAX_ONLY_OFFLINE && !seller.isInOfflineMode())
		{
			tax = 0;
		}
		if (Config.SERVICES_PARNASSUS_NOTAX && seller.getReflection() == ReflectionManager.PARNASSUS)
		{
			tax = 0;
		}

		return tax;
	}

	public static void cancelStore(Player activeChar)
	{
		activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
		if (activeChar.isInOfflineMode())
		{
			activeChar.setOfflineMode(false);
			activeChar.kick();
		}
		else
		{
			activeChar.broadcastCharInfo();
		}
	}

	public static void buyFromStore(Player seller, Player buyer, int _count, int[] _items, long[] _itemQ, long[] _itemP)
	{
		List<TradeItem> sellList = seller.getSellList();
		if (sellList.isEmpty())
		{
			buyer.sendPacket(SystemMsg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
			buyer.sendActionFailed();
			return;
		}

		List<TradeItem> buyList = new ArrayList<TradeItem>();

		long totalCost = 0;
		int slots = 0;
		long weight = 0;

		buyer.getInventory().writeLock();
		seller.getInventory().writeLock();
		try
		{
			loop:
			for (int i = 0; i < _count; i++)
			{
				int objectId = _items[i];
				long count = _itemQ[i];
				long price = _itemP[i];

				TradeItem bi = null;
				for (TradeItem si : sellList)
				{
					if (si.getObjectId() == objectId)
					{
						if (si.getOwnersPrice() == price)
						{
							if (count > si.getCount())
							{
								break loop;
							}

							ItemInstance item = seller.getInventory().getItemByObjectId(objectId);
							if (item == null || item.getCount() < count || !item.canBeTraded(seller))
							{
								break loop;
							}

							totalCost = SafeMath.addAndCheck(totalCost, SafeMath.mulAndCheck(count, price));
							weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(count, item.getTemplate().getWeight()));
							if (!item.isStackable() || buyer.getInventory().getItemByItemId(item.getItemId()) == null)
							{
								slots++;
							}

							bi = new TradeItem();
							bi.setObjectId(objectId);
							bi.setItemId(item.getItemId());
							bi.setCount(count);
							bi.setOwnersPrice(price);
							if (si.getCount() == count)
							{
								bi.setAuctionId(si.getAuctionId());
							}
							else
							{
								AuctionManager.getInstance().setNewCount(si.getAuctionId(), si.getCount() - count);
							}
							buyList.add(bi);
							break;
						}
					}
				}
			}
		}
		catch (ArithmeticException ae)
		{
			// TODO audit
			buyList.clear();
			seller.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		finally
		{
			try
			{
				if (buyList.size() != _count || (seller.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE && buyList.size() != sellList.size()))
				{
					buyer.sendPacket(SystemMsg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
					buyer.sendActionFailed();
					return;
				}

				if (!buyer.getInventory().validateWeight(weight))
				{
					buyer.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
					buyer.sendActionFailed();
					return;
				}

				if (!buyer.getInventory().validateCapacity(slots))
				{
					buyer.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
					buyer.sendActionFailed();
					return;
				}

				final List<ItemActionLog> logs = new ArrayList<ItemActionLog>(buyList.size() * 2 + 2);
				final ItemInstance adenaToRemoveFull = buyer.getInventory().getItemByItemId(57);
				logs.add(new ItemActionLog(ItemStateLog.EXCHANGE_LOSE, "PrivateStore", buyer, adenaToRemoveFull, totalCost));
				if (!buyer.reduceAdena(totalCost, null))
				{
					buyer.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					buyer.sendActionFailed();
					return;
				}

				for (TradeItem bi2 : buyList)
				{
					final ItemInstance itemToRemoveFull = seller.getInventory().getItemByObjectId(bi2.getObjectId());
					logs.add(new ItemActionLog(ItemStateLog.EXCHANGE_LOSE, "PrivateStore", seller, itemToRemoveFull, bi2.getCount()));
					final ItemInstance item2 = seller.getInventory().removeItemByObjectId(bi2.getObjectId(), bi2.getCount(), "Sold In Store To " + buyer.toString());
					for (TradeItem si : sellList)
					{
						if (si.getObjectId() == bi2.getObjectId())
						{
							si.setCount(si.getCount() - bi2.getCount());
							if (si.getCount() < 1L)
							{
								sellList.remove(si);
								break;
							}
							break;
						}
					}
					final ItemInstance gainedFullItem = buyer.getInventory().addItem(item2, null);
					logs.add(new ItemActionLog(ItemStateLog.EXCHANGE_GAIN, "PrivateStore", buyer, gainedFullItem, bi2.getCount()));
					purchaseItem(buyer, seller, bi2);
					AuctionManager.getInstance().removeStore(seller, bi2.getAuctionId());
				}
				final long tax = getTax(seller, totalCost);
				if (tax > 0L)
				{
					totalCost -= tax;
					seller.sendMessage(new CustomMessage("trade.HavePaidTax", seller, new Object[0]).addNumber(tax));
				}
				final ItemInstance gainedTotalAdena = seller.addAdena(totalCost, "Reward From Store Sell to " + buyer.toString());
				logs.add(new ItemActionLog(ItemStateLog.EXCHANGE_GAIN, "PrivateStore", buyer, gainedTotalAdena, totalCost));
				Log.logItemActions(buyer.toString() + " Private Store Exchange with " + seller.toString(), logs);
				seller.saveTradeList();
			}
			finally
			{
				seller.getInventory().writeUnlock();
				buyer.getInventory().writeUnlock();
			}
		}

		if (sellList.isEmpty())
		{
			cancelStore(seller);
		}

		seller.sendChanges();
		buyer.sendChanges();

		buyer.sendActionFailed();

		ItemLogHandler.getInstance().addLog(seller, buyer, buyList, totalCost, ItemActionType.SOLD_IN_STORE, ItemActionType.BOUGHT_IN_STORE);
	}
}