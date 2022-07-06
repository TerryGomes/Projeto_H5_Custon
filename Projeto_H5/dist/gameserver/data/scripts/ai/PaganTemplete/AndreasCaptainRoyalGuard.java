package ai.PaganTemplete;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;

/**
 * @author claww
  * - AI for the monster Andreas Captain Royal Guard (22175).
  * - If you see a player in a range of 500 when its party composes more than 9 Membury.
  * - Then throw on a random coordinates of the first who saw the player.
  * - If the attack when HP is below 70%, throw a debuff and die.
  * - AI is tested and works.
 */
public class AndreasCaptainRoyalGuard extends Fighter
{
	private static int NUMBER_OF_DEATH = 0;
	private boolean _tele = true;
	private boolean _talk = true;

	public static final Location[] locs =
	{
		new Location(-16128, -35888, -10726),
		new Location(-17029, -39617, -10724),
		new Location(-15729, -42001, -10724)
	};

	public AndreasCaptainRoyalGuard(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		for (Player player : World.getAroundPlayers(actor, 500, 500))
		{
			if (player == null || !player.isInParty())
			{
				continue;
			}

			if (player.getParty().size() >= 9 && _tele)
			{
				_tele = false;
				player.teleToLocation(Rnd.get(locs));
			}
		}

		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		if (actor.getCurrentHpPercents() <= 70)
		{
			actor.doCast(SkillTable.getInstance().getInfo(4612, 9), attacker, true);
			actor.doDie(attacker);
		}
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		NUMBER_OF_DEATH++;
		// The doors to the balcony
		// Door door1 = DoorHolder.getInstance().getDoor(19160014);
		// Door door2 = DoorHolder.getInstance().getDoor(19160015);
		// The doors to the althar
		// Door door3 = DoorHolder.getInstance().getDoor(19160016);
		// Door door4 = DoorHolder.getInstance().getDoor(19160017);
		if (NUMBER_OF_DEATH == 39 && _talk)
		{
			_talk = false;
			// Reset the memory
			NUMBER_OF_DEATH = 0;
			// We have killed all the monsters on the balcony, close the doors to the balcony
			// door1.closeMe(actor);
			// door2.closeMe(actor);
			// Open the door to the altar
			// door3.openMe(actor, false);
			// door4.openMe(actor, false);
		}
		_tele = true;
		super.onEvtDead(killer);
	}
}