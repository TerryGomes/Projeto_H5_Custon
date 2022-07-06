package l2mv.gameserver.model.instances;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.lang.reference.HardReferences;
import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.NpcInfo;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.npc.NpcTemplate;

public final class TamedBeastInstance extends FeedableBeastInstance
{
	private static final int MAX_DISTANCE_FROM_OWNER = 2000;
	private static final int MAX_DISTANCE_FOR_BUFF = 200;
	private static final int MAX_DURATION = 1200000; // 20 minutes
	private static final int DURATION_CHECK_INTERVAL = 60000; // 1 minute
	private static final int DURATION_INCREASE_INTERVAL = 20000; // 20 secs

	private HardReference<Player> _playerRef = HardReferences.emptyRef();
	private int _foodSkillId, _remainingTime = MAX_DURATION;
	private Future<?> _durationCheckTask = null;

	private final List<Skill> _skills = new ArrayList<Skill>();

	@SuppressWarnings("unchecked")
	private static final Map.Entry<NpcString, int[]>[] TAMED_DATA = new Map.Entry[6];
	static
	{
		TAMED_DATA[0] = new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.RECKLESS_S1, new int[]
		{
			6671
		});
		TAMED_DATA[1] = new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.S1_OF_BALANCE, new int[]
		{
			6431,
			6666
		});
		TAMED_DATA[2] = new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.SHARP_S1, new int[]
		{
			6432,
			6668
		});
		TAMED_DATA[3] = new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.USEFUL_S1, new int[]
		{
			6433,
			6670
		});
		TAMED_DATA[4] = new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.S1_OF_BLESSING, new int[]
		{
			6669,
			6672
		});
		TAMED_DATA[5] = new AbstractMap.SimpleImmutableEntry<NpcString, int[]>(NpcString.SWIFT_S1, new int[]
		{
			6434,
			6667
		});
	}

	public TamedBeastInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		_hasRandomWalk = false;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}

	@Override
	public void onAction(Player player, boolean dontMove)
	{
		player.setObjectTarget(this);
		// TODO [VISTALL] action shift
	}

	private void onReceiveFood()
	{
		// Eating food extends the duration by 20secs, to a max of 20minutes
		_remainingTime = _remainingTime + DURATION_INCREASE_INTERVAL;
		if (_remainingTime > MAX_DURATION)
		{
			_remainingTime = MAX_DURATION;
		}
	}

	public int getRemainingTime()
	{
		return _remainingTime;
	}

	public void setRemainingTime(int duration)
	{
		_remainingTime = duration;
	}

	public int getFoodType()
	{
		return _foodSkillId;
	}

	public void setTameType()
	{
		Map.Entry<NpcString, int[]> type = TAMED_DATA[Rnd.get(TAMED_DATA.length)];

		setNameNpcString(type.getKey());
		setName("#" + getNameNpcStringByNpcId().getId());

		for (int skillId : type.getValue())
		{
			Skill sk = SkillTable.getInstance().getInfo(skillId, 1);
			if (sk != null)
			{
				_skills.add(sk);
			}
		}
	}

	public NpcString getNameNpcStringByNpcId()
	{
		switch (getNpcId())
		{
		case 18869:
			return NpcString.ALPEN_KOOKABURRA;
		case 18870:
			return NpcString.ALPEN_COUGAR;
		case 18871:
			return NpcString.ALPEN_BUFFALO;
		case 18872:
			return NpcString.ALPEN_GRENDEL;
		}
		return NpcString.NONE;
	}

	public void buffOwner()
	{
		if (!isInRange(getPlayer(), MAX_DISTANCE_FOR_BUFF))
		{
			setFollowTarget(getPlayer());
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getPlayer(), Config.FOLLOW_RANGE);
			return;
		}

		int delay = 0;
		for (Skill skill : _skills)
		{
			ThreadPoolManager.getInstance().schedule(new Buff(this, getPlayer(), skill), delay);
			delay = delay + skill.getHitTime(this) + 500;
		}
	}

	public static class Buff extends RunnableImpl
	{
		private final NpcInstance _actor;
		private final Player _owner;
		private final Skill _skill;

		public Buff(NpcInstance actor, Player owner, Skill skill)
		{
			_actor = actor;
			_owner = owner;
			_skill = skill;
		}

		@Override
		public void runImpl() throws Exception
		{
			if (_actor != null)
			{
				_actor.doCast(_skill, _owner, true);
			}
		}
	}

	public void setFoodType(int foodItemId)
	{
		if (foodItemId > 0)
		{
			_foodSkillId = foodItemId;

			// start the duration checks start the buff tasks
			if (_durationCheckTask != null)
			{
				_durationCheckTask.cancel(false);
			}
			_durationCheckTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CheckDuration(this), DURATION_CHECK_INTERVAL, DURATION_CHECK_INTERVAL);
		}
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		if (_durationCheckTask != null)
		{
			_durationCheckTask.cancel(false);
			_durationCheckTask = null;
		}

		Player owner = getPlayer();
		if (owner != null)
		{
			owner.removeTrainedBeast(getObjectId());
		}

		_foodSkillId = 0;
		_remainingTime = 0;
	}

	@Override
	public Player getPlayer()
	{
		return _playerRef.get();
	}

	public void setOwner(Player owner)
	{
		_playerRef = owner == null ? HardReferences.<Player>emptyRef() : owner.getRef();
		if (owner != null)
		{
			setTitle(owner.getName());
			owner.addTrainedBeast(this);

			for (Player player : World.getAroundPlayers(this))
			{
				player.sendPacket(new NpcInfo(this, player));
			}

			// always and automatically follow the owner.
			setFollowTarget(getPlayer());
			getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, owner, Config.FOLLOW_RANGE);
		}
		else
		{
			doDespawn(); // despawn if no owner
		}
	}

	public void despawnWithDelay(int delay)
	{
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				doDespawn();
			}
		}, delay);
	}

	public void doDespawn()
	{
		// stop running tasks
		stopMove();

		if (_durationCheckTask != null)
		{
			_durationCheckTask.cancel(false);
			_durationCheckTask = null;
		}

		// clean up variables
		Player owner = getPlayer();
		if (owner != null)
		{
			owner.removeTrainedBeast(getObjectId());
		}

		setTarget(null);
		_foodSkillId = 0;
		_remainingTime = 0;

		// remove the spawn
		onDecay();
	}

	private static class CheckDuration extends RunnableImpl
	{
		private final TamedBeastInstance _tamedBeast;

		CheckDuration(TamedBeastInstance tamedBeast)
		{
			_tamedBeast = tamedBeast;
		}

		@Override
		public void runImpl() throws Exception
		{
			Player owner = _tamedBeast.getPlayer();

			if (owner == null || !owner.isOnline() || (_tamedBeast.getDistance(owner) > MAX_DISTANCE_FROM_OWNER))
			{
				_tamedBeast.doDespawn();
				return;
			}

			int foodTypeSkillId = _tamedBeast.getFoodType();
			_tamedBeast.setRemainingTime(_tamedBeast.getRemainingTime() - DURATION_CHECK_INTERVAL);

			// I tried to avoid this as much as possible...but it seems I can't avoid hardcoding
			// ids further, except by carrying an additional variable just for these two lines...
			// Find which food item needs to be consumed.
			ItemInstance item = null;
			int foodItemId = _tamedBeast.getItemIdBySkillId(foodTypeSkillId);
			if (foodItemId > 0)
			{
				item = owner.getInventory().getItemByItemId(foodItemId);
			}

			// if the owner has enough food, call the item handler (use the food and triffer all necessary actions)
			if (item != null && item.getCount() >= 1)
			{
				_tamedBeast.onReceiveFood();
				owner.getInventory().destroyItem(item, 1, "Tamed Beast");
			}
			else // if the owner has no food, the beast immediately despawns, except when it was only
					// newly spawned. Newly spawned beasts can last up to 5 minutes
			if (_tamedBeast.getRemainingTime() < MAX_DURATION - 300000)
			{
				_tamedBeast.setRemainingTime(-1);
			}

			if (_tamedBeast.getRemainingTime() <= 0)
			{
				_tamedBeast.doDespawn();
			}
		}
	}
}