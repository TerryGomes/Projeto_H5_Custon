package l2f.gameserver.model.instances;

import l2f.gameserver.ai.CharacterAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.Die;
import l2f.gameserver.templates.npc.NpcTemplate;

public class DeadManInstance extends NpcInstance
{
	public DeadManInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setAI(new CharacterAI(this));
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();
		setCurrentHp(0, false);
		broadcastPacket(new Die(this));
		setWalking();
	}

	@Override
	public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
	}

	@Override
	public boolean isInvul()
	{
		return true;
	}

	@Override
	public boolean isBlocked()
	{
		return true;
	}
}