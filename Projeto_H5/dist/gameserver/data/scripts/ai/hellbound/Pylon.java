package ai.hellbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class Pylon extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(Pylon.class);

	public Pylon(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		NpcInstance actor = getActor();
		for (int i = 0; i < 7; i++)
		{
			try
			{
				SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(22422));
				sp.setLoc(Location.findPointToStay(actor, 150, 550));
				sp.doSpawn(true);
				sp.stopRespawn();
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on Pylon Spawn", e);
			}
		}
	}
}