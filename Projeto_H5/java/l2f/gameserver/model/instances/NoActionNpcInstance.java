package l2f.gameserver.model.instances;

import l2f.gameserver.model.Player;
import l2f.gameserver.templates.npc.NpcTemplate;

@Deprecated
public class NoActionNpcInstance extends NpcInstance
{
	public NoActionNpcInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onAction(Player player, boolean dontMove)
	{
		player.sendActionFailed();
	}
}
