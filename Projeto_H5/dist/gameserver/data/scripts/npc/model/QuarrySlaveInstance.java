package npc.model;

import java.util.StringTokenizer;

import l2f.gameserver.instancemanager.HellboundManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.npc.NpcTemplate;

public final class QuarrySlaveInstance extends NpcInstance
{
	public QuarrySlaveInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return attacker.isMonster();
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this) || isBusy())
		{
			return;
		}

		StringTokenizer st = new StringTokenizer(command);
		if (st.nextToken().equals("rescue") && HellboundManager.getHellboundLevel() == 5)
		{
			Functions.npcSay(this, "Sh-h! Guards are around, let's go.");
			HellboundManager.addConfidence(10);
			doDie(null);
			endDecayTask();
			// TODO: following
			// getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, Config.FOLLOW_RANGE);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public String getHtmlPath(int npcId, int val, Player player)
	{
		String pom;
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		return "hellbound/" + pom + ".htm";
	}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}
}