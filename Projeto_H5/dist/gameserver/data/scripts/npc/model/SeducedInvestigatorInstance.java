package npc.model;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.templates.npc.NpcTemplate;

public class SeducedInvestigatorInstance extends MonsterInstance
{
	public SeducedInvestigatorInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setHasChatWindow(true);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		player.sendPacket(new NpcHtmlMessage(player, this, "common/seducedinvestigator.htm", val));
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		Player player = attacker.getPlayer();
		if ((player == null) || player.isPlayable())
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean isMovementDisabled()
	{
		return true;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}