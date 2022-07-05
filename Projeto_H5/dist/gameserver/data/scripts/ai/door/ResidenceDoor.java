package ai.door;

import l2f.gameserver.ai.DoorAI;
import l2f.gameserver.data.xml.holder.ResidenceHolder;
import l2f.gameserver.listener.actor.player.OnAnswerListener;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.instances.DoorInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.ConfirmDlg;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

public class ResidenceDoor extends DoorAI
{
	public ResidenceDoor(DoorInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtTwiceClick(final Player player)
	{
		final DoorInstance door = getActor();

		Residence residence = ResidenceHolder.getInstance().getResidence(door.getTemplate().getAIParams().getInteger("residence_id"));
		if (residence.getOwner() != null && player.getClan() != null && player.getClan() == residence.getOwner() && (player.getClanPrivileges() & Clan.CP_CS_ENTRY_EXIT) == Clan.CP_CS_ENTRY_EXIT)
		{
			SystemMsg msg = door.isOpen() ? SystemMsg.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE : SystemMsg.WOULD_YOU_LIKE_TO_OPEN_THE_GATE;
			player.ask(new ConfirmDlg(msg, 0), new OnAnswerListener()
			{
				@Override
				public void sayYes()
				{
					if (door.isOpen())
					{
						door.closeMe(player, true);
					}
					else
					{
						door.openMe(player, true);
					}
				}

				@Override
				public void sayNo()
				{
					//
				}
			});
		}
	}
}
