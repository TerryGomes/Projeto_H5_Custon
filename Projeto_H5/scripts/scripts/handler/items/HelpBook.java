package handler.items;

import l2mv.gameserver.handler.items.ItemHandler;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.RadarControl;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

public class HelpBook extends ScriptItemHandler implements ScriptFile
{
	private static final int[] _itemIds =
	{
		5588,
		6317,
		7561,
		7063,
		7064,
		7065,
		7066,
		7082,
		7083,
		7084,
		7085,
		7086,
		7087,
		7088,
		7089,
		7090,
		7091,
		7092,
		7093,
		7094,
		7095,
		7096,
		7097,
		7098,
		7099,
		7100,
		7101,
		7102,
		7103,
		7104,
		7105,
		7106,
		7107,
		7108,
		7109,
		7110,
		7111,
		7112,
		8059,
		13130,
		13131,
		13132,
		13133,
		13134,
		13135,
		13136,
		17213
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
		if (!playable.isPlayer())
		{
			return false;
		}

		Player activeChar = (Player) playable;
		Functions.show("help/" + item.getItemId() + ".htm", activeChar, null);
		if (item.getItemId() == 7063)
		{
			activeChar.sendPacket(new RadarControl(0, 2, new Location(51995, -51265, -3104)));
		}
		activeChar.sendActionFailed();
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}