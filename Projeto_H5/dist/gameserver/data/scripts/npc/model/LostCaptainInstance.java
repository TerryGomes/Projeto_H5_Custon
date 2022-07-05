package npc.model;

import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.entity.Reflection;
import l2f.gameserver.model.instances.ReflectionBossInstance;
import l2f.gameserver.templates.InstantZone;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;

public class LostCaptainInstance extends ReflectionBossInstance
{
	private static final int TELE_DEVICE_ID = 4314;

	public LostCaptainInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		Reflection r = getReflection();
		r.setReenterTime(System.currentTimeMillis());

		super.onDeath(killer);

		InstantZone iz = r.getInstancedZone();
		if (iz != null)
		{
			String tele_device_loc = iz.getAddParams().getString("tele_device_loc", null);
			if (tele_device_loc != null)
			{
				KamalokaGuardInstance npc = new KamalokaGuardInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(TELE_DEVICE_ID));
				npc.setSpawnedLoc(Location.parseLoc(tele_device_loc));
				npc.setReflection(r);
				npc.spawnMe(npc.getSpawnedLoc());
			}
		}
	}
}
