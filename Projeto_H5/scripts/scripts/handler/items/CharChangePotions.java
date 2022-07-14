package handler.items;

import l2mv.gameserver.handler.items.ItemHandler;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.ScriptFile;

public class CharChangePotions extends ScriptItemHandler implements ScriptFile
{
	private static final int[] _itemIds =
	{
		5235,
		5236,
		5237, // Face
		5238,
		5239,
		5240,
		5241, // Hair Color
		5242,
		5243,
		5244,
		5245,
		5246,
		5247,
		5248 // Hair Style
	};

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (playable == null || !playable.isPlayer())
		{
			return false;
		}
		Player player = (Player) playable;

		int itemId = item.getItemId();

		if (!player.getInventory().destroyItem(item, 1, "CharChangePotions"))
		{
			player.sendActionFailed();
			return false;
		}

		player.getSex();
		int face = player.getFace();
		int hairStyle = player.getHairStyle();
		int hairColor = player.getHairColor();
		switch (itemId)
		{
		case 5235:
			player.setFace(0);
			break;
		case 5236:
			player.setFace(1);
			break;
		case 5237:
			player.setFace(2);
			break;
		case 5238:
			player.setHairColor(0);
			break;
		case 5239:
			player.setHairColor(1);
			break;
		case 5240:
			player.setHairColor(2);
			break;
		case 5241:
			player.setHairColor(3);
			break;
		case 5242:
			player.setHairStyle(0);
			break;
		case 5243:
			player.setHairStyle(1);
			break;
		case 5244:
			player.setHairStyle(2);
			break;
		case 5245:
			player.setHairStyle(3);
			break;
		case 5246:
			player.setHairStyle(4);
			break;
		case 5247:
			player.setHairStyle(5);
			break;
		case 5248:
			player.setHairStyle(6);
			break;
		}

		player.broadcastPacket(new MagicSkillUse(player, player, 2003, 1, 1, 0));
		if (face != player.getFace() || hairColor != player.getHairColor() || hairStyle != player.getHairStyle())
		{
			player.broadcastUserInfo(true);
		}
		return true;
	}

	@Override
	public final int[] getItemIds()
	{
		return _itemIds;
	}
}