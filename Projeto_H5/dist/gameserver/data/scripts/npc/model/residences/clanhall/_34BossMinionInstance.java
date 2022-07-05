package npc.model.residences.clanhall;

import l2f.gameserver.model.Creature;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.npc.NpcTemplate;
import npc.model.residences.SiegeGuardInstance;

/**
 * @author VISTALL
 * @date 17:50/13.05.2011
 */
public abstract class _34BossMinionInstance extends SiegeGuardInstance implements _34SiegeGuard
{
	public _34BossMinionInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onDeath(Creature killer)
	{
		setCurrentHp(1, true);
	}

	@Override
	public void onSpawn()
	{
		super.onSpawn();

		Functions.npcShout(this, spawnChatSay());
	}

	public abstract NpcString spawnChatSay();

	@Override
	public abstract NpcString teleChatSay();
}
