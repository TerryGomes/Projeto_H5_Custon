package l2mv.gameserver.utils;

import java.util.Map;

import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;

public class ItemActionLog
{
	private final ItemStateLog futureType;
	private final String actionType;
	private final String player;
	private final String item;
	private final long count;
	private final boolean _isGm;

	public ItemActionLog(ItemStateLog futureType, String actionType, String player, ItemInstance item, long count)
	{
		this.futureType = futureType;
		this.actionType = actionType;
		this.player = player;
		this.item = itemToString(item, item.getCount() - count);
		this.count = count;
		final Player pl = GameObjectsStorage.getPlayer(player);
		_isGm = (pl != null ? pl.getAccessLevel() > 0 : false);
	}

	public ItemActionLog(ItemStateLog futureType, String actionType, Player player, ItemInstance item, long count)
	{
		this.futureType = futureType;
		this.actionType = actionType;
		this.player = player.toString();
		this.item = itemToString(item, item.getCount() - count);
		this.count = count;
		_isGm = player.getAccessLevel() > 0;
	}

	public ItemActionLog(ItemStateLog futureType, String actionType, int playerObjectId, ItemInstance item, long count)
	{
		this.futureType = futureType;
		this.actionType = actionType;
		final Player player = GameObjectsStorage.getPlayer(playerObjectId);
		if (player == null)
		{
			this.player = CharacterDAO.getNameByObjectId(playerObjectId) + "[" + playerObjectId + "]";
		}
		else
		{
			this.player = player.toString();
		}
		this.item = itemToString(item, item.getCount() - count);
		this.count = count;
		_isGm = (player != null ? player.getAccessLevel() > 0 : false);
	}

	public ItemActionLog(ItemStateLog futureType, String actionType, String playerString, String item, long count)
	{
		this.futureType = futureType;
		this.actionType = actionType;
		player = playerString;
		this.item = item;
		this.count = count;
		final Player pl = GameObjectsStorage.getPlayer(playerString);
		_isGm = (pl != null ? pl.getAccessLevel() > 0 : false);
	}

	public ItemActionLog(ItemStateLog futureType, String actionType, Clan clan, String item, long count)
	{
		this.futureType = futureType;
		this.actionType = actionType;
		player = clan.toString();
		this.item = item;
		this.count = count;
		_isGm = false;
	}

	public ItemActionLog(ItemStateLog futureType, String actionType, Player player, String item, long count)
	{
		this.futureType = futureType;
		this.actionType = actionType;
		this.player = player.toString();
		this.item = item;
		this.count = count;
		_isGm = player.getAccessLevel() > 0;
	}

	public ItemActionLog(ItemStateLog futureType, String actionType, Clan clan, ItemInstance item, long count)
	{
		this.futureType = futureType;
		this.actionType = actionType;
		player = clan.toString();
		this.item = itemToString(item, item.getCount() - count);
		this.count = count;
		_isGm = false;
	}

	public ItemActionLog(ItemStateLog futureType, String actionType, SubUnit sub, String item, long count)
	{
		this.futureType = futureType;
		this.actionType = actionType;
		player = sub.toString();
		this.item = item;
		this.count = count;
		_isGm = false;
	}

	public String getItem()
	{
		return item;
	}

	public boolean isGm()
	{
		return _isGm;
	}

	@Override
	public String toString()
	{
		return futureType + " " + actionType + " " + item + " " + player + " " + Util.getNumberWithCommas(count);
	}

	private static String itemToString(ItemInstance item, long count)
	{
		if (item == null)
		{
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(item.getTemplate().getItemId());
		sb.append(' ');
		if (item.getEnchantLevel() > 0)
		{
			sb.append('+');
			sb.append(item.getEnchantLevel());
			sb.append(' ');
		}
		sb.append(item.getTemplate().getName());
		if (!item.getTemplate().getAdditionalName().isEmpty())
		{
			sb.append(' ');
			sb.append('\\').append(item.getTemplate().getAdditionalName()).append('\\');
		}
		for (Map.Entry<Element, Integer> attribute : item.getAttributes().getElements().entrySet())
		{
			sb.append(' ');
			sb.append(attribute.getKey().name());
			sb.append('=');
			sb.append(attribute.getValue());
			sb.append(' ');
		}
		if (item.getAugmentationId() > 0)
		{
			sb.append("Augment=");
			sb.append(item.getAugmentationId());
			sb.append(' ');
		}
		sb.append('(');
		sb.append(Util.getNumberWithCommas(count));
		sb.append(')');
		sb.append('[');
		sb.append(item.getObjectId());
		sb.append(']');
		return sb.toString();
	}
}
