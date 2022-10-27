package l2mv.gameserver.model.instances;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

/**
 * @author Byldas
 * @date 26.09.11
 * @Instance for Mobs Furnace is MOS
 */

public class FurnaceInstance extends NpcInstance
{
	private static final Logger _log = LoggerFactory.getLogger(FurnaceInstance.class);
	private static int[][] locs =
	{
		{
			113125,
			-73174,
			-598
		},
		{
			113126,
			-73289,
			-598
		},
		{
			113126,
			-73403,
			-598
		},
		{
			113126,
			-73517,
			-598
		},
		{
			113122,
			-71873,
			-600
		},
		{
			113121,
			-72011,
			-600
		},
		{
			113120,
			-72125,
			-600
		},
		{
			113120,
			-72243,
			-600
		},
		{
			112385,
			-80802,
			-1639
		},
		{
			112383,
			-80913,
			-1639
		},
		{
			112384,
			-81024,
			-1639
		},
		{
			112383,
			-81131,
			-1639
		},
		{
			112384,
			-79512,
			-1639
		},
		{
			112383,
			-79628,
			-1638
		},
		{
			112383,
			-79734,
			-1638
		},
		{
			112383,
			-79841,
			-1638
		},
		{
			108528,
			-76098,
			-1120
		},
		{
			108408,
			-76096,
			-1120
		},
		{
			108300,
			-76097,
			-1120
		},
		{
			108178,
			-76095,
			-1120
		},
		{
			109468,
			-76098,
			-1119
		},
		{
			109574,
			-76094,
			-1119
		},
		{
			109682,
			-76095,
			-1119
		},
		{
			109803,
			-76093,
			-1119
		},
	};

	private static final int MobsID = 18914;

	public FurnaceInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onSpawn()
	{
		try
		{
			int idm = 1;
			for (int[] loc : locs)
			{
				NpcInstance burner = new NpcInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(MobsID));
				burner.setSpawnedLoc(new Location(loc[0], loc[1], loc[2]));
				burner.onSpawn();
				{
					switch (idm)
					{
					case 1:
					{
						burner.setTitle("Furnace of Magic Power");
						idm++;
						break;
					}
					case 2:
					{
						burner.setTitle("Furnace of Fighting Spirit");
						idm++;
						break;
					}
					case 3:
					{
						burner.setTitle("Furnace of Protection");
						idm++;
						break;
					}
					case 4:
					{
						burner.setTitle("Furnace of Balance");
						idm = 1;
						break;
					}
					}
				}

				burner.spawnMe(burner.getSpawnedLoc());

			}
		}
		catch (RuntimeException e)
		{
			_log.error("Could not spawn Npc " + MobsID, e);
		}
		super.onSpawn();
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return true;
	}

	@Override
	public boolean isInvul()
	{
		return true;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

}