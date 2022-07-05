package npc.model;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public final class NativeCorpseInstance extends NpcInstance
{
	public NativeCorpseInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
	}

	@Override
	public void showChatWindow(Player player, String filename, Object... replace)
	{
	}

	@Override
	public void onRandomAnimation()
	{
		return;
	}
}