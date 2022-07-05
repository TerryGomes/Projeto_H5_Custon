package npc.model;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется NPC Snowman в эвенте Saving Snowman
 * @author SYS
 */
public class SnowmanInstance extends NpcInstance
{
	public SnowmanInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		player.sendActionFailed();
	}
}