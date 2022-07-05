package l2f.gameserver.model.instances;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.entity.boat.Boat;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;

public class AirShipControllerInstance extends NpcInstance
{
	public AirShipControllerInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("board"))
		{
			SystemMsg msg = canBoard(player);
			if (msg != null)
			{
				player.sendPacket(msg);
				return;
			}

			Boat boat = getDockedAirShip();
			if (boat == null)
			{
				player.sendActionFailed();
				return;
			}

			if (player.getBoat() != null && player.getBoat().getObjectId() != boat.getObjectId())
			{
				player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_BOARDED_ANOTHER_AIRSHIP);
				return;
			}

			player._stablePoint = player.getLoc().setH(0);
			boat.addPlayer(player, new Location());
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	protected Boat getDockedAirShip()
	{
		for (Creature cha : World.getAroundCharacters(this, 1000, 500))
		{
			if (cha.isAirShip() && ((Boat) cha).isDocked())
			{
				return (Boat) cha;
			}
		}

		return null;
	}

	private static SystemMsg canBoard(Player player)
	{
		if (player.getTransformation() != 0)
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED;
		}
		if (player.isParalyzed())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED;
		}
		if (player.isDead() || player.isFakeDeath())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD;
		}
		if (player.isFishing())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING;
		}
		if (player.isInCombat())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE;
		}
		if (player.isInDuel())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL;
		}
		if (player.isSitting())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING;
		}
		if (player.isCastingNow())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING;
		}
		if (player.isCursedWeaponEquipped())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHEN_A_CURSED_WEAPON_IS_EQUIPPED;
		}
		if (player.getActiveWeaponFlagAttachment() != null)
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG;
		}
		if (player.getPet() != null || player.isMounted())
		{
			return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED;
		}

		return null;
	}
}