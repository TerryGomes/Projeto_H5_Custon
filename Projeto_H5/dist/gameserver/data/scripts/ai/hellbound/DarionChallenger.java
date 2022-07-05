package ai.hellbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

public class DarionChallenger extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(DarionChallenger.class);
	private static final int TeleportCube = 32467;

	public DarionChallenger(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (checkAllDestroyed())
		{
			try
			{
				SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(TeleportCube));
				sp.setLoc(new Location(-12527, 279714, -11622, 16384));
				sp.doSpawn(true);
				sp.stopRespawn();
				ThreadPoolManager.getInstance().schedule(new Unspawn(), 600 * 1000L); // 10 mins
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on Darino Challanger Spawn", e);
			}
		}
		super.onEvtDead(killer);
	}

	private static boolean checkAllDestroyed()
	{
		if (!GameObjectsStorage.getAllByNpcId(25600, true).isEmpty() || !GameObjectsStorage.getAllByNpcId(25601, true).isEmpty() || !GameObjectsStorage.getAllByNpcId(25602, true).isEmpty())
		{
			return false;
		}

		return true;
	}

	private class Unspawn extends RunnableImpl
	{
		public Unspawn()
		{
		}

		@Override
		public void runImpl()
		{
			for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(TeleportCube, true))
			{
				npc.deleteMe();
			}
		}
	}
}