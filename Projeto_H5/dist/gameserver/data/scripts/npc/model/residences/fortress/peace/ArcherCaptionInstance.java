package npc.model.residences.fortress.peace;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 16:14/17.04.2011
 */
public class ArcherCaptionInstance extends NpcInstance
{
	public ArcherCaptionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		showChatWindow(player, "residence2/fortress/fortress_archer.htm");
	}
}
