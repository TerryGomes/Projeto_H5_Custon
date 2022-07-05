package npc.model;

import instances.ErosionHallAttack;
import instances.ErosionHallDefence;
import instances.SufferingHallAttack;
import instances.SufferingHallDefence;
import l2f.gameserver.instancemanager.SoIManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class EkimusMouthInstance extends NpcInstance
{
	private static final int hosattackIzId = 115;
	private static final int hoeattackIzId = 119;

	private static final int hosdefenceIzId = 116;
	private static final int hoedefenceIzId = 120;

	public EkimusMouthInstance(int objectId, NpcTemplate template)
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

		if (command.equalsIgnoreCase("hos_enter"))
		{
			Reflection r = player.getActiveReflection();
			if (SoIManager.getCurrentStage() == 1)
			{
				if (r != null)
				{
					if (player.canReenterInstance(hosattackIzId))
					{
						player.teleToLocation(r.getTeleportLoc(), r);
					}
				}
				else if (player.canEnterInstance(hosattackIzId))
				{
					ReflectionUtils.enterReflection(player, new SufferingHallAttack(), hosattackIzId);
				}
			}
			else if (SoIManager.getCurrentStage() == 4)
			{
				if (r != null)
				{
					if (player.canReenterInstance(hosdefenceIzId))
					{
						player.teleToLocation(r.getTeleportLoc(), r);
					}
				}
				else if (player.canEnterInstance(hosdefenceIzId))
				{
					ReflectionUtils.enterReflection(player, new SufferingHallDefence(), hosdefenceIzId);
				}
			}
		}
		else if (command.equalsIgnoreCase("hoe_enter"))
		{
			Reflection r = player.getActiveReflection();
			if (SoIManager.getCurrentStage() == 1)
			{
				if (r != null)
				{
					if (player.canReenterInstance(hoeattackIzId))
					{
						player.teleToLocation(r.getTeleportLoc(), r);
					}
				}
				else if (player.canEnterInstance(hoeattackIzId))
				{
					ReflectionUtils.enterReflection(player, new ErosionHallAttack(), hoeattackIzId);
				}
			}
			else if (SoIManager.getCurrentStage() == 4)
			{
				if (r != null)
				{
					if (player.canReenterInstance(hoedefenceIzId))
					{
						player.teleToLocation(r.getTeleportLoc(), r);
					}
				}
				else if (player.canEnterInstance(hoedefenceIzId))
				{
					ReflectionUtils.enterReflection(player, new ErosionHallDefence(), hoedefenceIzId);
				}
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}