package l2f.gameserver.model.instances;

import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.templates.npc.NpcTemplate;

public class ChestInstance extends MonsterInstance
{
	public ChestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public void tryOpen(Player opener, Skill skill)
	{
		getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, opener, 100);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}