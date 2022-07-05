package ai.other.PailakaDevilsLegacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class Lematan extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(Lematan.class);
	private boolean _teleported = false;

	private static int LEMATAN_FOLLOWER = 18634;

	private static Location _position[] =
	{
		new Location(84840, -208488, -3336, 0),
		new Location(85160, -208488, -3336, 0),
		new Location(84696, -208744, -3336, 0),
		new Location(85264, -208744, -3336, 0),
		new Location(84840, -209000, -3336, 0),
		new Location(85160, -209000, -3336, 0)
	};

	public Lematan(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		if (getActor().isInRange(getActor().getSpawnedLoc(), 10000L))
		{
			return true;
		}
		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		if (actor.getCurrentHp() < (actor.getMaxHp() / 2) && !_teleported)
		{
			// мы на корабле но у нас половина ХП и мы не телепортнулись
			actor.setSpawnedLoc(new Location(84984, -208744, -3336));
			actor.teleToLocation(new Location(84984, -208744, -3336));
			attacker.teleToLocation(new Location(85128, -208744, -3336));

			_teleported = true;
			SimpleSpawner spawn = null;

			try
			{
				for (Location loc : _position)
				{
					spawn = new SimpleSpawner(NpcHolder.getInstance().getTemplate(LEMATAN_FOLLOWER));
					spawn.setLoc(loc);
					spawn.setAmount(1);
					spawn.setHeading(actor.getHeading());
					spawn.setRespawnDelay(30);
					spawn.setReflection(actor.getReflection());
					spawn.init();
				}
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on Lematan Death", e);
			}
		}
		super.onEvtAttacked(attacker, damage);
	}
}