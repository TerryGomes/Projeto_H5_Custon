package npc.model;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

public final class CabaleBufferInstance extends NpcInstance
{
	public CabaleBufferInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
	}

	@Override
	public void showChatWindow(Player player, String filename, Object... ar)
	{
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
	}
}