package ai.PaganTemplete;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

/**
 * @author PaInKiLlEr
  * - AI for the monster Triols Layperson (22142).
  * - If you see a player in a range of 500 when its party composes more than 4 Membury.
  * - Then throw on a random coordinates of the first who saw the player.
  * - AI is tested and works.
 */
public class TriolsLayperson extends Fighter
{
	private boolean _tele = true;

	public static final Location[] locs =
	{
		new Location(-16128, -35888, -10726),
		new Location(-17029, -39617, -10724),
		new Location(-15729, -42001, -10724)
	};

	public TriolsLayperson(NpcInstance actor)
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

			if (player.getParty().size() >= 5 && _tele)
			{
				_tele = false;
				player.teleToLocation(Rnd.get(locs));
			}
		}

		return true;
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		_tele = true;
		super.onEvtDead(killer);
	}
}