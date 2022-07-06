package npc.model.residences.castle;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.CastleSiegeInfo;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class CastleMessengerInstance extends NpcInstance
{
	public CastleMessengerInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		Castle castle = getCastle();

		if (player.isCastleLord(castle.getId()))
		{
			if (castle.getSiegeEvent().isInProgress())
			{
				showChatWindow(player, "residence2/castle/sir_tyron021.htm");
			}
			else
			{
				showChatWindow(player, "residence2/castle/sir_tyron007.htm");
			}
		}
		else if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress())
		{
			showChatWindow(player, "residence2/castle/sir_tyron021.htm");
		}
		else
		{
			player.sendPacket(new CastleSiegeInfo(castle, player));
		}
	}
}