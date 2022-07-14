package npc.model.residences.castle;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 21:58/23.05.2011
 * 35506
 */
public class VenomTeleporterInstance extends NpcInstance
{
	public VenomTeleporterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		Castle castle = getCastle();
		if (castle.getSiegeEvent().isInProgress())
		{
			showChatWindow(player, "residence2/castle/rune_massymore_teleporter002.htm");
		}
		else if (!checkForDominionWard(player))
		{
			player.teleToLocation(12589, -49044, -3008);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		showChatWindow(player, "residence2/castle/rune_massymore_teleporter001.htm");
	}
}
