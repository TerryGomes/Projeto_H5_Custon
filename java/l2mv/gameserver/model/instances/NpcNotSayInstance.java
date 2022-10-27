package l2mv.gameserver.model.instances;

import l2mv.gameserver.templates.npc.NpcTemplate;

public class NpcNotSayInstance extends NpcInstance
{
	public NpcNotSayInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
		setHasChatWindow(false);
	}
}
