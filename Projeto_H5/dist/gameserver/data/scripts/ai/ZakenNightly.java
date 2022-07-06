package ai;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.PlaySound;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;

/**
 * Night Zaken. Refine behavior
 *
 * @author pchayka
 */
public class ZakenNightly extends Fighter
{
	private static final int doll_blader_b = 29023;
	private static final int vale_master_b = 29024;
	private static final int pirates_zombie_captain_b = 29026;
	private static final int pirates_zombie_b = 29027;

	private static final Location[] _locations = new Location[]
	{
		new Location(55272, 219112, -3496),
		new Location(56296, 218072, -3496),
		new Location(54232, 218072, -3496),
		new Location(54248, 220136, -3496),
		new Location(56296, 220136, -3496),
		new Location(55272, 219112, -3224),
		new Location(56296, 218072, -3224),
		new Location(54232, 218072, -3224),
		new Location(54248, 220136, -3224),
		new Location(56296, 220136, -3224),
		new Location(55272, 219112, -2952),
		new Location(56296, 218072, -2952),
		new Location(54232, 218072, -2952),
		new Location(54248, 220136, -2952),
		new Location(56296, 220136, -2952)
	};

	private long _teleportSelfTimer = 0L;
	private long _teleportSelfReuse = 30000L; // 30 secs
	private NpcInstance actor = getActor();
	private int _stage = 0;

	public ZakenNightly(NpcInstance actor)
	{
		super(actor);
		MAX_PURSUE_RANGE = Integer.MAX_VALUE / 2;
	}

	@Override
	protected void thinkAttack()
	{
		if (_teleportSelfTimer + _teleportSelfReuse < System.currentTimeMillis())
		{
			_teleportSelfTimer = System.currentTimeMillis();
			if (Rnd.chance(20))
			{
				actor.doCast(SkillTable.getInstance().getInfo(4222, 1), actor, false);
				ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl()
					{
						actor.teleToLocation(_locations[Rnd.get(_locations.length)]);
						actor.getAggroList().clear(true);
					}
				}, 500);
			}
		}

		double actor_hp_precent = actor.getCurrentHpPercents();
		Reflection r = actor.getReflection();
		switch (_stage)
		{
		case 0:
			if (actor_hp_precent < 90)
			{
				r.addSpawnWithoutRespawn(pirates_zombie_captain_b, actor.getLoc(), 300);
				_stage++;
			}
			break;
		case 1:
			if (actor_hp_precent < 80)
			{
				r.addSpawnWithoutRespawn(doll_blader_b, actor.getLoc(), 300);
				_stage++;
			}
			break;
		case 2:
			if (actor_hp_precent < 70)
			{
				r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
				r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
				_stage++;
			}
			break;
		case 3:
			if (actor_hp_precent < 60)
			{
				for (int i = 0; i < 5; i++)
				{
					r.addSpawnWithoutRespawn(pirates_zombie_b, actor.getLoc(), 300);
				}
				_stage++;
			}
			break;
		case 4:
			if (actor_hp_precent < 50)
			{
				for (int i = 0; i < 5; i++)
				{
					r.addSpawnWithoutRespawn(doll_blader_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(pirates_zombie_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(pirates_zombie_captain_b, actor.getLoc(), 300);
				}
				_stage++;
			}
			break;
		case 5:
			if (actor_hp_precent < 40)
			{
				for (int i = 0; i < 6; i++)
				{
					r.addSpawnWithoutRespawn(doll_blader_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(pirates_zombie_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(pirates_zombie_captain_b, actor.getLoc(), 300);
				}
				_stage++;
			}
			break;
		case 6:
			if (actor_hp_precent < 30)
			{
				for (int i = 0; i < 7; i++)
				{
					r.addSpawnWithoutRespawn(doll_blader_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(pirates_zombie_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(vale_master_b, actor.getLoc(), 300);
					r.addSpawnWithoutRespawn(pirates_zombie_captain_b, actor.getLoc(), 300);
				}
				_stage++;
			}
			break;
		default:
			break;
		}
		super.thinkAttack();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		Reflection r = actor.getReflection();
		r.setReenterTime(System.currentTimeMillis());
		actor.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, actor.getObjectId(), actor.getLoc()));
		super.onEvtDead(killer);
	}

	@Override
	protected void teleportHome()
	{
		return;
	}
}