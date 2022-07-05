package ai.custom;

import org.apache.commons.lang3.ArrayUtils;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;

public class FreyaEventAI extends DefaultAI
{
	private static final int[] GIFT_SKILLS =
	{
		9150,
		9151,
		9152,
		9153,
		9154,
		9155,
		9156
	};
	private static final int GIFT_CHANCE = 5;
	private static final int FREYA_GIFT = 17138;
	private static final NpcString[] SAY_TEXT = new NpcString[]
	{
		NpcString.DEAR_S1,
		NpcString.BUT_I_KIND_OF_MISS_IT,
		NpcString.I_JUST_DONT_KNOW_WHAT_EXPRESSION_I_SHOULD_HAVE_IT_APPEARED_ON_ME,
		NpcString.EVEN_THOUGH_YOU_BRING_SOMETHING_CALLED_A_GIFT_AMONG_YOUR_HUMANS_IT_WOULD_JUST_BE_PROBLEMATIC_FOR_ME,
		NpcString.THE_FEELING_OF_THANKS_IS_JUST_TOO_MUCH_DISTANT_MEMORY_FOR_ME,
		NpcString.I_AM_ICE_QUEEN_FREYA
	};

	public FreyaEventAI(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		NpcInstance actor = getActor();

		if (caster == null || !caster.isPlayer())
		{
			return;
		}

		GameObject casterTarget = caster.getTarget();
		if (casterTarget == null || casterTarget.getObjectId() != actor.getObjectId())
		{
			return;
		}

		Player player = caster.getPlayer();

		if (ArrayUtils.contains(GIFT_SKILLS, skill.getId()))
		{
			if (Rnd.chance(GIFT_CHANCE))
			{
				Functions.npcSay(actor, SAY_TEXT[0], player.getName());
				Functions.addItem(player, FREYA_GIFT, 1, "FreyaEventAI");
			}
			else if (Rnd.chance(70))
			{
				Functions.npcSay(actor, SAY_TEXT[Rnd.get(1, SAY_TEXT.length - 1)]);
			}
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
}