package l2f.gameserver.model.entity.events.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.stats.Env;
import l2f.gameserver.utils.Location;

public class DuelSnapshotObject implements Serializable
{
	private final TeamType _team;
	private final Player _player;
	private final List<Effect> _effects;
	private final Location _returnLoc;
	private final double _currentHp;
	private final double _currentMp;
	private final double _currentCp;

	private boolean _isDead;

	public DuelSnapshotObject(Player player, TeamType team)
	{
		_player = player;
		_team = team;
		_returnLoc = player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc();

		_currentCp = player.getCurrentCp();
		_currentHp = player.getCurrentHp();
		_currentMp = player.getCurrentMp();

		List<Effect> effectList = player.getEffectList().getAllEffects();
		_effects = new ArrayList<Effect>(effectList.size());
		for (Effect $effect : effectList)
		{
			if (($effect == null) || $effect.getSkill().isToggle() || $effect.getSkill().getTargetType() == Skill.SkillTargetType.TARGET_SELF)
			{
				continue;
			}
			final Effect effect = $effect.getTemplate().getEffect(new Env($effect.getEffector(), $effect.getEffected(), $effect.getSkill()));
			effect.setCount($effect.getCount());
			effect.setPeriod($effect.getCount() == 1 ? $effect.getPeriod() - $effect.getTime() : $effect.getPeriod());

			_effects.add(effect);
		}
	}

	public void restore(boolean abnormal)
	{
		if (!abnormal)
		{
			if (!_player.isInOlympiadMode())
			{
				_player.getEffectList().stopAllEffects();
				for (Effect e : _effects)
				{
					_player.getEffectList().addEffect(e);
				}

				_player.setCurrentCp(_currentCp);
				_player.setCurrentHpMp(_currentHp, _currentMp);
			}
		}
	}

	public void teleport()
	{
		_player._stablePoint = null;
		if (_player.isFrozen())
		{
			_player.stopFrozen();
		}

		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				_player.teleToLocation(_returnLoc, ReflectionManager.DEFAULT);
			}
		}, 5000L);
	}

	public Player getPlayer()
	{
		return _player;
	}

	public boolean isDead()
	{
		return _isDead;
	}

	public void setDead()
	{
		_isDead = true;
	}

	public Location getLoc()
	{
		return _returnLoc;
	}

	public TeamType getTeam()
	{
		return _team;
	}
}
