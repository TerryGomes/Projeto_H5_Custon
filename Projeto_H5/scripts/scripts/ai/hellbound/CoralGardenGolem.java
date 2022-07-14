package ai.hellbound;

import instances.CrystalCaverns;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;

/**
 * @author pchayka
 */
public class CoralGardenGolem extends DefaultAI
{
	private boolean fedCrystal = false;
	private boolean trapped = false;
	private static final NpcString[] phrases_idle =
	{
		NpcString.HELLO_IS_ANYONE_THERE,
		NpcString.IS_NO_ONE_THERE_HOW_LONG_HAVE_I_BEEN_HIDING_I_HAVE_BEEN_STARVING_FOR_DAYS_AND_CANNOT_HOLD_OUT_ANYMORE,
		NpcString.IF_SOMEONE_WOULD_GIVE_ME_SOME_OF_THOSE_TASTY_CRYSTAL_FRAGMENTS_I_WOULD_GLADLY_TELL_THEM_WHERE_TEARS_IS_HIDING_YUMMY_YUMMY,
		NpcString.HEY_YOU_FROM_ABOVE_THE_GROUND_LETS_SHARE_SOME_CRYSTAL_FRAGMENTS_IF_YOU_HAVE_ANY
	};
	private static final NpcString[] phrases_eat =
	{
		NpcString.CRISPY_AND_COLD_FEELING_TEEHEE_DELICIOUS,
		NpcString.YUMMY_THIS_IS_SO_TASTY,
		NpcString.HOW_INSENSITIVE_ITS_NOT_NICE_TO_GIVE_ME_JUST_A_PIECE_CANT_YOU_GIVE_ME_MORE,
		NpcString.SNIFF_SNIFF_GIVE_ME_MORE_CRYSTAL_FRAGMENTS,
		NpcString.AH__IM_HUNGRY
	};

	public CoralGardenGolem(NpcInstance actor)
	{
		super(actor);
		actor.setHasChatWindow(false);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (!fedCrystal && Rnd.chance(1))
		{
			Functions.npcShout(actor, phrases_idle[Rnd.get(phrases_idle.length)]);
		}
		if (!actor.isMoving && !trapped)
		{
			ItemInstance closestItem = null;
			for (GameObject obj : World.getAroundObjects(actor, 200, 200))
			{
				if (obj.isItem() && ((ItemInstance) obj).getItemId() == 9693) // Crystal Fragment
				{
					closestItem = (ItemInstance) obj;
				}
			}
			if (closestItem != null)
			{
				actor.moveToLocation(closestItem.getLoc(), 0, true);
			}
		}
		return false;
	}

	@Override
	protected void onEvtArrived()
	{
		super.onEvtArrived();
		NpcInstance actor = getActor();
		ItemInstance closestItem;
		for (GameObject obj : World.getAroundObjects(actor, 20, 200))
		{
			if (obj.isItem() && ((ItemInstance) obj).getItemId() == 9693)
			{
				fedCrystal = true;
				closestItem = (ItemInstance) obj;
				closestItem.deleteMe();
				Functions.npcShout(actor, phrases_eat[Rnd.get(phrases_eat.length)]);
			}
			else
			{
				actor.moveToLocation(actor.getSpawnedLoc(), 0, true);
			}
		}

		if (!trapped && (actor.isInZone("[cry_cav_cor_gar_golem_trap_1]") || actor.isInZone("[cry_cav_cor_gar_golem_trap_2]")))
		{
			trapped = true;
			actor.broadcastPacket(new MagicSkillUse(actor, actor, 5441, 1, 3000, 0));
			if (!actor.getReflection().isDefault() && actor.getReflection().getInstancedZoneId() == 10)
			{
				((CrystalCaverns) actor.getReflection()).notifyGolemTrapped();
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

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}