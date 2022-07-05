package l2f.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import l2f.commons.math.SafeMath;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.MultiSellHolder;
import l2f.gameserver.data.xml.holder.MultiSellHolder.MultiSellListContainer;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.MultiSellEntry;
import l2f.gameserver.model.base.MultiSellIngredient;
import l2f.gameserver.model.entity.residence.Castle;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ItemAttributes;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.items.PcInventory;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.utils.ItemFunctions;

public class RequestMultiSellChoose extends L2GameClientPacket
{
	private int _listId;
	private int _entryId;
	private long _amount;

	private class ItemData
	{
		private final int _id;
		private final long _count;
		private final ItemInstance _item;

		public ItemData(int id, long count, ItemInstance item)
		{
			_id = id;
			_count = count;
			_item = item;
		}

		public int getId()
		{
			return _id;
		}

		public long getCount()
		{
			return _count;
		}

		public ItemInstance getItem()
		{
			return _item;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof ItemData))
			{
				return false;
			}

			ItemData i = (ItemData) obj;

			return (_id == i._id) && (_count == i._count) && (_item == i._item);
		}
	}

	@Override
	protected void readImpl()
	{
		_listId = readD();
		_entryId = readD();
		_amount = readQ();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if ((activeChar == null) || (_amount < 1))
		{
			return;
		}

		MultiSellListContainer list1 = activeChar.getMultisell();
		// Check to see whether the replaced id
		if ((list1 == null) || (list1.getListId() != _listId))
		{
			// TODO audit
			activeChar.sendActionFailed();
			activeChar.setMultisell(null);
			return;
		}

		if (activeChar.getVar("isPvPevents") != null)
		{
			activeChar.sendMessage("You can follow any responses did not leave while participating in the event!");
			activeChar.sendActionFailed();
			activeChar.setMultisell(null);
			return;
		}

		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("At the Olympics to use the exchange is forbidden!");
			activeChar.sendActionFailed();
			activeChar.setMultisell(null);
			return;
		}

		if (activeChar.isActionsDisabled() || activeChar.isBlocked())
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

		if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && (activeChar.getKarma() > 0) && !activeChar.isGM())
		{
			activeChar.sendActionFailed();
			return;
		}

		MultiSellEntry entry = null;
		for (MultiSellEntry $entry : list1.getEntries())
		{
			if ($entry.getEntryId() == _entryId)
			{
				entry = $entry;
				break;
			}
		}

		if (entry == null)
		{
			return;
		}

		final boolean keepenchant = list1.isKeepEnchant();
		final boolean notax = list1.isNoTax();
		final List<ItemData> items = new ArrayList<ItemData>();

		PcInventory inventory = activeChar.getInventory();

		long totalPrice = 0;

		NpcInstance merchant = activeChar.getLastNpc();
		Castle castle = merchant != null ? merchant.getCastle(activeChar) : null;

		inventory.writeLock();
		try
		{
			long tax = SafeMath.mulAndCheck(entry.getTax(), _amount);

			long slots = 0;
			long weight = 0;
			for (MultiSellIngredient i : entry.getProduction())
			{
				if (i.getItemId() <= 0)
				{
					continue;
				}
				ItemTemplate item = ItemHolder.getInstance().getTemplate(i.getItemId());

				weight = SafeMath.addAndCheck(weight, SafeMath.mulAndCheck(SafeMath.mulAndCheck(i.getItemCount(), _amount), item.getWeight()));
				if (item.isStackable())
				{
					if (inventory.getItemByItemId(i.getItemId()) == null)
					{
						slots++;
					}
				}
				else
				{
					slots = SafeMath.addAndCheck(slots, _amount);
				}
			}

			if (!inventory.validateWeight(weight))
			{
				activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
				activeChar.sendActionFailed();
				return;
			}

			if (!inventory.validateCapacity(slots))
			{
				activeChar.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
				activeChar.sendActionFailed();
				return;
			}

			if (entry.getIngredients().size() == 0)
			{
				activeChar.sendActionFailed();
				activeChar.setMultisell(null);
				return;
			}

			// Search of all the ingredients, check availability and create a list of the intake
			for (MultiSellIngredient ingridient : entry.getIngredients())
			{
				int ingridientItemId = ingridient.getItemId();
				long ingridientItemCount = ingridient.getItemCount();
				int ingridientEnchant = ingridient.getItemEnchant();
				long totalAmount = !ingridient.getMantainIngredient() ? SafeMath.mulAndCheck(ingridientItemCount, _amount) : ingridientItemCount;

				switch (ingridientItemId)
				{
				case ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE:
					if (activeChar.getClan() == null)
					{
						activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
						return;
					}
					if (activeChar.getClan().getLevel() < 5)
					{
						activeChar.sendMessage("The level of the clan must be at least the 5th!");
						return;
					}
					if (activeChar.getClan().getReputationScore() < totalAmount)
					{
						activeChar.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
						return;
					}
					if (activeChar.getClan().getLeaderId() != activeChar.getObjectId())
					{
						activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_IS_NOT_A_CLAN_LEADER).addString(activeChar.getName()));
						return;
					}
					if (!ingridient.getMantainIngredient())
					{
						items.add(new ItemData(ingridientItemId, totalAmount, null));
					}
					break;
				case ItemTemplate.ITEM_ID_PC_BANG_POINTS:
					if (activeChar.getPcBangPoints() < totalAmount)
					{
						activeChar.sendPacket(SystemMsg.YOU_ARE_SHORT_OF_ACCUMULATED_POINTS);
						return;
					}
					if (!ingridient.getMantainIngredient())
					{
						items.add(new ItemData(ingridientItemId, totalAmount, null));
					}
					break;
				case ItemTemplate.ITEM_ID_FAME:
					if (activeChar.getFame() < totalAmount)
					{
						activeChar.sendPacket(SystemMsg.YOU_DONT_HAVE_ENOUGH_REPUTATION_TO_DO_THAT);
						return;
					}
					if (!ingridient.getMantainIngredient())
					{
						items.add(new ItemData(ingridientItemId, totalAmount, null));
					}
					break;
				default:
				{
					ItemTemplate template = ItemHolder.getInstance().getTemplate(ingridientItemId);
					if (!template.isStackable())
					{
						for (int i = 0; i < (ingridientItemCount * _amount); i++)
						{
							List<ItemInstance> list = inventory.getItemsByItemId(ingridientItemId);
							// Если энчант имеет значение - то ищем вещи с точно таким энчантом
							if (keepenchant)
							{
								ItemInstance itemToTake = null;
								for (ItemInstance item : list)
								{
									ItemData itmd = new ItemData(item.getItemId(), item.getCount(), item);
									if (((item.getEnchantLevel() == ingridientEnchant) || !item.getTemplate().isEquipment()) && !items.contains(itmd) && item.canBeExchanged(activeChar))
									{
										itemToTake = item;
										break;
									}
								}

								if (itemToTake == null)
								{
									activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
									return;
								}

								if (!ingridient.getMantainIngredient())
								{
									items.add(new ItemData(itemToTake.getItemId(), 1, itemToTake));
								}
							}
							// Если энчант не обрабатывается берется вещь с наименьшим энчантом
							else
							{
								ItemInstance itemToTake = null;
								for (ItemInstance item : list)
								{
									if (!items.contains(new ItemData(item.getItemId(), item.getCount(), item)) && ((itemToTake == null) || (item.getEnchantLevel() < itemToTake.getEnchantLevel()))
												&& !item.isShadowItem() && !item.isTemporalItem() && (!item.isAugmented() || Config.ALT_ALLOW_DROP_AUGMENTED)
												&& ItemFunctions.checkIfCanDiscard(activeChar, item))
									{
										itemToTake = item;
										if (itemToTake.getEnchantLevel() == 0)
										{
											break;
										}
									}
								}

								if (itemToTake == null)
								{
									activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
									return;
								}

								if (!ingridient.getMantainIngredient())
								{
									items.add(new ItemData(itemToTake.getItemId(), 1, itemToTake));
								}
							}
						}
					}
					else
					{
						if (ingridientItemId == 57)
						{
							totalPrice = SafeMath.addAndCheck(totalPrice, SafeMath.mulAndCheck(ingridientItemCount, _amount));
						}
						ItemInstance item = inventory.getItemByItemId(ingridientItemId);

						if ((item == null) || (item.getCount() < totalAmount))
						{
							activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
							return;
						}

						if (!ingridient.getMantainIngredient())
						{
							items.add(new ItemData(item.getItemId(), totalAmount, item));
						}
					}
					break;
				}
				}

				if (activeChar.getAdena() < totalPrice)
				{
					activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
					return;
				}
			}

			ItemTemplate.Grade ingredientCrystalType = null;
			int enchantLevel = 0;
			ItemAttributes attributes = null;
			int augmentationId = 0;
			for (ItemData id : items)
			{
				long count = id.getCount();
				if (count > 0)
				{
					if (id.getId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
					{
						if (activeChar.getClan() == null)
						{
							activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
							return;
						}
						if (activeChar.getClan().getLevel() < 5)
						{
							activeChar.sendMessage("The level of the clan must be at least the 5th!");
							return;
						}
						activeChar.getClan().incReputation((int) -count, false, "MultiSell");
						activeChar.sendPacket(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_ADDED_1S_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber((int) count));
					}
					else if (id.getId() == ItemTemplate.ITEM_ID_PC_BANG_POINTS)
					{
						activeChar.reducePcBangPoints((int) count);
					}
					else if (id.getId() == ItemTemplate.ITEM_ID_FAME)
					{
						activeChar.setFame(activeChar.getFame() - (int) count, "MultiSell");
						activeChar.sendPacket(new SystemMessage2(SystemMsg.S2_S1_HAS_DISAPPEARED).addInteger(count).addString("Fame"));
					}
					else
					{
						if (inventory.destroyItem(id.getItem(), count, "Multisell"))
						{
							if (keepenchant && id.getItem().canBeEnchanted(true))
							{
								enchantLevel = id.getItem().getEnchantLevel();
								attributes = id.getItem().getAttributes();
								augmentationId = id.getItem().getAugmentationId();
								ingredientCrystalType = id.getItem().getCrystalType();
							}

							activeChar.sendPacket(SystemMessage2.removeItems(id.getId(), count));
							continue;
						}

						// TODO audit
						return;
					}
				}
			}

			if ((tax > 0) && !notax)
			{
				if (castle != null)
				{
					activeChar.sendMessage(new CustomMessage("trade.HavePaidTax", activeChar).addNumber(tax));
					if ((merchant != null) && (merchant.getReflection() == ReflectionManager.DEFAULT))
					{
						castle.addToTreasury(tax, true, false);
					}
				}
			}

			for (MultiSellIngredient in : entry.getProduction())
			{
				if (in.getItemId() <= 0)
				{
					if (in.getItemId() == ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE)
					{
						if (activeChar.getClan() == null)
						{
							activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
							return;
						}
						if (activeChar.getClan().getLevel() < 5)
						{
							activeChar.sendMessage("The level of the clan must be at least the 5th!");
							return;
						}
						activeChar.getClan().incReputation((int) (in.getItemCount() * _amount), false, "MultiSell");
						activeChar.sendPacket(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_ADDED_1S_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber((int) (in.getItemCount() * _amount)));
					}
					else if (in.getItemId() == ItemTemplate.ITEM_ID_PC_BANG_POINTS)
					{
						activeChar.addPcBangPoints((int) (in.getItemCount() * _amount), false);
					}
					else if (in.getItemId() == ItemTemplate.ITEM_ID_FAME)
					{
						activeChar.setFame(activeChar.getFame() + (int) (in.getItemCount() * _amount), "MultiSell");
					}
				}
				else if (ItemHolder.getInstance().getTemplate(in.getItemId()).isStackable())
				{
					long total = SafeMath.mulAndLimit(in.getItemCount(), _amount);
					ItemFunctions.addItem(activeChar, in.getItemId(), total, true, "Multisell");
				}
				else
				{
					for (int i = 0; i < _amount; i++)
					{
						ItemInstance product = ItemFunctions.createItem(in.getItemId());

						if (keepenchant)
						{
							if (product.canBeEnchanted(true) && ingredientCrystalType != null && ingredientCrystalType.equals(product.getCrystalType()))
							{
								product.setEnchantLevel(enchantLevel);
								if (attributes != null)
								{
									product.setAttributes(attributes.clone());
								}
								if (augmentationId != 0)
								{
									product.setAugmentationId(augmentationId);
								}
							}
						}
						else
						{
							product.setEnchantLevel(in.getItemEnchant());
							product.setAttributes(in.getItemAttributes().clone());
						}

						activeChar.sendPacket(SystemMessage2.obtainItems(product));
						inventory.addItem(product, "Multisell");
					}
				}
			}
		}
		catch (ArithmeticException ae)
		{
			// TODO audit
			activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
			return;
		}
		finally
		{
			inventory.writeUnlock();
		}

		activeChar.sendChanges();

		if (!list1.isShowAll())
		{
			MultiSellHolder.getInstance().SeparateAndSend(list1, activeChar, castle == null ? 0 : castle.getTaxRate());
		}
	}
}