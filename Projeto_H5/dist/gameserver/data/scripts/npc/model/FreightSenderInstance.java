package npc.model;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.MerchantInstance;
import l2f.gameserver.network.serverpackets.PackageToList;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.WarehouseFunctions;

/**
 * @author VISTALL
 * @date 20:32/16.05.2011
 */
public class FreightSenderInstance extends MerchantInstance
{
	public FreightSenderInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("deposit_items"))
		{
			player.sendPacket(new PackageToList(player));
		}
		else if (command.equalsIgnoreCase("withdraw_items"))
		{
			WarehouseFunctions.showFreightWindow(player);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
