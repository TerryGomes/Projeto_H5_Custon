package npc.model.fightClub;

import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.fightclub.FFATreasureHuntEvent;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.ActionFail;
import l2f.gameserver.network.serverpackets.MyTargetSelected;
import l2f.gameserver.network.serverpackets.StatusUpdate;
import l2f.gameserver.network.serverpackets.ValidateLocation;
import l2f.gameserver.templates.npc.NpcTemplate;

public class TreasureChestInstance extends NpcInstance
{
	public TreasureChestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if (!isTargetable())
		{
			player.sendActionFailed();
			return;
		}

		if (player.getTarget() == null || !player.getTarget().equals(this))
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()), makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
			player.sendPacket(new ValidateLocation(this), ActionFail.STATIC);
			return;
		}

		if (!isInRange(player, (long) INTERACTION_DISTANCE))
		{
			if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
			{
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
			}
			return;
		}

		if (player.isSitting() || player.isAlikeDead())
		{
			return;
		}

		player.sendActionFailed();
		player.stopMove(false);

		if (player.isInFightClub())
		{
			boolean shouldDisappear = false;
			if (player.getFightClubEvent() instanceof FFATreasureHuntEvent)
			{
				shouldDisappear = ((FFATreasureHuntEvent) player.getFightClubEvent()).openTreasure(player, this);
			}

			if (shouldDisappear)
			{
				deleteMe();
			}
		}
	}
}
