package l2mv.gameserver.network.serverpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.net.nio.impl.SendablePacket;
import l2mv.gameserver.GameServer;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.base.MultiSellIngredient;
import l2mv.gameserver.model.items.ItemInfo;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.templates.item.ItemTemplate;

public abstract class L2GameServerPacket extends SendablePacket<GameClient> implements IStaticPacket
{
	private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);

	@Override
	public final boolean write()
	{
		try
		{
			this.writeImpl();
			return true;
		}
		catch (RuntimeException e)
		{
			_log.error("Client: " + this.getClient() + " - Failed writing: " + this.getType() + " - Server Version: " + GameServer.getInstance().getVersion().getRevisionNumber(), e);
		}
		return false;
	}

	protected abstract void writeImpl();

	protected void writeEx(int value)
	{
		this.writeC(0xFE);
		this.writeH(value);
	}

	protected void writeD(boolean b)
	{
		this.writeD(b ? 1 : 0);
	}

	/**
	 * Отсылает число позиций + массив
	 */
	protected void writeDD(int[] values, boolean sendCount)
	{
		if (sendCount)
		{
			this.getByteBuffer().putInt(values.length);
		}
		for (int value : values)
		{
			this.getByteBuffer().putInt(value);
		}
	}

	protected void writeDD(int[] values)
	{
		this.writeDD(values, false);
	}

	protected void writeItemInfo(ItemInstance item)
	{
		this.writeItemInfo(item, item.getCount());
	}

	protected void writeItemInfo(ItemInstance item, long count)
	{
		this.writeD(item.getObjectId());
		this.writeD(item.getItemId());
		this.writeD(item.getEquipSlot());
		this.writeQ(count);
		this.writeH(item.getTemplate().getType2ForPackets());
		this.writeH(item.getCustomType1());
		this.writeH(item.isEquipped() ? 1 : 0);
		this.writeD(item.getBodyPart());
		this.writeH(item.getEnchantLevel());
		this.writeH(item.getCustomType2());
		this.writeD(item.getAugmentationId());
		this.writeD(item.getShadowLifeTime());
		this.writeD(item.getTemporalLifeTime());
		this.writeH(item.getAttackElement().getId());
		this.writeH(item.getAttackElementValue());
		this.writeH(item.getDefenceFire());
		this.writeH(item.getDefenceWater());
		this.writeH(item.getDefenceWind());
		this.writeH(item.getDefenceEarth());
		this.writeH(item.getDefenceHoly());
		this.writeH(item.getDefenceUnholy());
		this.writeH(item.getEnchantOptions()[0]);
		this.writeH(item.getEnchantOptions()[1]);
		this.writeH(item.getEnchantOptions()[2]);
	}

	protected void writeItemInfo(ItemInfo item)
	{
		this.writeItemInfo(item, item.getCount());
	}

	protected void writeItemInfo(ItemInfo item, long count)
	{
		this.writeD(item.getObjectId());
		this.writeD(item.getItemId());
		this.writeD(item.getEquipSlot());
		this.writeQ(count);
		this.writeH(item.getItem().getType2ForPackets());
		this.writeH(item.getCustomType1());
		this.writeH(item.isEquipped() ? 1 : 0);
		this.writeD(item.getItem().getBodyPart());
		this.writeH(item.getEnchantLevel());
		this.writeH(item.getCustomType2());
		this.writeD(item.getAugmentationId());
		this.writeD(item.getShadowLifeTime());
		this.writeD(item.getTemporalLifeTime());
		this.writeH(item.getAttackElement());
		this.writeH(item.getAttackElementValue());
		this.writeH(item.getDefenceFire());
		this.writeH(item.getDefenceWater());
		this.writeH(item.getDefenceWind());
		this.writeH(item.getDefenceEarth());
		this.writeH(item.getDefenceHoly());
		this.writeH(item.getDefenceUnholy());
		this.writeH(item.getEnchantOptions()[0]);
		this.writeH(item.getEnchantOptions()[1]);
		this.writeH(item.getEnchantOptions()[2]);
	}

	protected void writeItemElements(MultiSellIngredient item)
	{
		if (item.getItemId() <= 0)
		{
			this.writeItemElements();
			return;
		}
		ItemTemplate i = ItemHolder.getInstance().getTemplate(item.getItemId());
		if (item.getItemAttributes().getValue() > 0)
		{
			if (i.isWeapon())
			{
				Element e = item.getItemAttributes().getElement();
				this.writeH(e.getId()); // attack element (-1 - none)
				this.writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e)); // attack element value
				this.writeH(0); // водная стихия (fire pdef)
				this.writeH(0); // огненная стихия (water pdef)
				this.writeH(0); // земляная стихия (wind pdef)
				this.writeH(0); // воздушная стихия (earth pdef)
				this.writeH(0); // темная стихия (holy pdef)
				this.writeH(0); // светлая стихия (dark pdef)
			}
			else if (i.isArmor())
			{
				this.writeH(-1); // attack element (-1 - none)
				this.writeH(0); // attack element value
				for (Element e : Element.VALUES)
				{
					this.writeH(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e));
				}
			}
			else
			{
				this.writeItemElements();
			}
		}
		else
		{
			this.writeItemElements();
		}
	}

	protected void writeItemElements()
	{
		this.writeH(-1); // attack element (-1 - none)
		this.writeH(0x00); // attack element value
		this.writeH(0x00); // водная стихия (fire pdef)
		this.writeH(0x00); // огненная стихия (water pdef)
		this.writeH(0x00); // земляная стихия (wind pdef)
		this.writeH(0x00); // воздушная стихия (earth pdef)
		this.writeH(0x00); // темная стихия (holy pdef)
		this.writeH(0x00); // светлая стихия (dark pdef)
	}

	public String getType()
	{
		return "[S] " + this.getClass().getSimpleName();
	}

	@Override
	public L2GameServerPacket packet(Player player)
	{
		return this;
	}

}