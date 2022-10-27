package npc.model.residences.fortress.siege;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Spawner;
import l2mv.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2mv.gameserver.model.entity.events.objects.SpawnExObject;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 19:36/19.04.2011
 */
public class ControlUnitInstance extends NpcInstance
{
	private static final int ITEM_ID = 10014;
	private static final int COND_CAN_OPEN = 0;
	private static final int COND_NO_ITEM = 1;
	private static final int COND_POWER = 2;

	public ControlUnitInstance(int objectId, NpcTemplate template)
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

		int cond = getCond(player);
		if (cond == COND_CAN_OPEN)
		{
			if (player.consumeItem(ITEM_ID, 1))
			{
				FortressSiegeEvent event = getEvent(FortressSiegeEvent.class);

				event.doorAction(FortressSiegeEvent.MACHINE_DOORS, true);
				event.spawnAction(FortressSiegeEvent.OUT_POWER_UNITS, false);
			}
			else
			{
				showChatWindow(player, "residence2/fortress/fortress_controller002.htm");
			}
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		int cond = getCond(player);
		switch (cond)
		{
		case COND_CAN_OPEN:
			showChatWindow(player, "residence2/fortress/fortress_controller001.htm");
			break;
		case COND_NO_ITEM:
			showChatWindow(player, "residence2/fortress/fortress_controller002.htm");
			break;
		case COND_POWER:
			showChatWindow(player, "residence2/fortress/fortress_controller003.htm");
			break;
		}
	}

	private int getCond(Player player)
	{
		FortressSiegeEvent event = getEvent(FortressSiegeEvent.class);
		if (event == null)
		{
			return COND_POWER;
		}

		SpawnExObject object = event.getFirstObject(FortressSiegeEvent.OUT_POWER_UNITS);

		boolean allPowerDisabled = true;
		for (int i = 0; i < 4; i++)
		{
			Spawner spawn = object.getSpawns().get(i);
			if (spawn.getFirstSpawned() != null)
			{
				allPowerDisabled = false;
			}
		}

		if (allPowerDisabled)
		{
			if (player.getInventory().getCountOf(ITEM_ID) > 0)
			{
				return COND_CAN_OPEN;
			}
			else
			{
				return COND_NO_ITEM;
			}
		}
		else
		{
			return COND_POWER;
		}
	}
}
