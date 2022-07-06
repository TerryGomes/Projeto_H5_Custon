package ai.hellbound;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.Location;

public class DarionFaithfulServant extends Fighter
{
	private static final Logger LOG = LoggerFactory.getLogger(DarionFaithfulServant.class);
	private static final int MysteriousAgent = 32372;

	public DarionFaithfulServant(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (Rnd.chance(15))
		{
			try
			{
				SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(MysteriousAgent));
				sp.setLoc(new Location(-11984, 278880, -13599, -4472));
				sp.doSpawn(true);
				sp.stopRespawn();
				ThreadPoolManager.getInstance().schedule(new Unspawn(), 600 * 1000L); // 10 mins
			}
			catch (RuntimeException e)
			{
				LOG.error("Error on Darion Faithful Servanth Death", e);
			}
		}
		super.onEvtDead(killer);
	}

	private class Unspawn extends RunnableImpl
	{
		public Unspawn()
		{
		}

		@Override
		public void runImpl()
		{
			for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(MysteriousAgent, true))
			{
				npc.deleteMe();
			}
		}
	}

}