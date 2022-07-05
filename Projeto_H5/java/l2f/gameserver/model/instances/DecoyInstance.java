package l2f.gameserver.model.instances;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.lang.reference.HardReference;
import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.AutoAttackStart;
import l2f.gameserver.network.serverpackets.CharInfo;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.MyTargetSelected;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.npc.NpcTemplate;

public class DecoyInstance extends NpcInstance
{
	private static final Logger _log = LoggerFactory.getLogger(DecoyInstance.class);

	private HardReference<Player> _playerRef;
	private int _lifeTime, _timeRemaining;
	private ScheduledFuture<?> _decoyLifeTask, _hateSpam;

	public DecoyInstance(int objectId, NpcTemplate template, Player owner, int lifeTime)
	{
		super(objectId, template);

		_playerRef = owner.getRef();
		_lifeTime = lifeTime;
		_timeRemaining = _lifeTime;
		int skilllevel = getNpcId() < 13257 ? getNpcId() - 13070 : getNpcId() - 13250;
		_decoyLifeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new DecoyLifetime(), 1000, 1000);
		_hateSpam = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HateSpam(SkillTable.getInstance().getInfo(5272, skilllevel)), 1000, 3000);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);
		if (_hateSpam != null)
		{
			_hateSpam.cancel(false);
			_hateSpam = null;
		}
		_lifeTime = 0;
	}

	class DecoyLifetime extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			try
			{
				double newTimeRemaining;
				decTimeRemaining(1000);
				newTimeRemaining = getTimeRemaining();
				if (newTimeRemaining < 0)
				{
					unSummon();
				}
			}
			catch (RuntimeException e)
			{
				_log.error("Error on Decoy Lifetime End", e);
			}
		}
	}

	class HateSpam extends RunnableImpl
	{
		private Skill _skill;

		HateSpam(Skill skill)
		{
			_skill = skill;
		}

		@Override
		public void runImpl() throws Exception
		{
			try
			{
				setTarget(DecoyInstance.this);
				doCast(_skill, DecoyInstance.this, true);
			}
			catch (RuntimeException e)
			{
				_log.error("Error while Changing Target to DecoyInstance", e);
			}
		}
	}

	public void unSummon()
	{
		if (_decoyLifeTask != null)
		{
			_decoyLifeTask.cancel(false);
			_decoyLifeTask = null;
		}
		if (_hateSpam != null)
		{
			_hateSpam.cancel(false);
			_hateSpam = null;
		}
		deleteMe();
	}

	public void decTimeRemaining(int value)
	{
		_timeRemaining -= value;
	}

	public int getTimeRemaining()
	{
		return _timeRemaining;
	}

	public int getLifeTime()
	{
		return _lifeTime;
	}

	@Override
	public Player getPlayer()
	{
		return _playerRef.get();
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		Player owner = getPlayer();
		return owner != null && owner.isAutoAttackable(attacker);
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		Player owner = getPlayer();
		return owner != null && owner.isAttackable(attacker);
	}

	@Override
	protected void onDelete()
	{
		Player owner = getPlayer();
		if (owner != null)
		{
			owner.setDecoy(null);
		}
		super.onDelete();
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if (player.getTarget() != this)
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), 0));
		}
		else if (isAutoAttackable(player))
		{
			player.getAI().Attack(this, false, shift);
		}
	}

	@Override
	public double getColRadius()
	{
		Player player = getPlayer();
		if (player == null)
		{
			return 0;
		}
		if (player.getTransformation() != 0 && player.getTransformationTemplate() != 0)
		{
			return NpcHolder.getInstance().getTemplate(player.getTransformationTemplate()).collisionRadius;
		}
		return player.getBaseTemplate().collisionRadius;
	}

	@Override
	public double getColHeight()
	{
		Player player = getPlayer();
		if (player == null)
		{
			return 0;
		}
		if (player.getTransformation() != 0 && player.getTransformationTemplate() != 0)
		{
			return NpcHolder.getInstance().getTemplate(player.getTransformationTemplate()).collisionHeight;
		}
		return player.getBaseTemplate().collisionHeight;
	}

	@Override
	public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
	{
		if (!isInCombat())
		{
			return Collections.<L2GameServerPacket>singletonList(new CharInfo(this, forPlayer));
		}
		else
		{
			List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>(2);
			list.add(new CharInfo(this, forPlayer));
			list.add(new AutoAttackStart(objectId));
			return list;
		}
	}

	@Override
	public boolean isInvul()
	{
		return _isInvul;
	}
}