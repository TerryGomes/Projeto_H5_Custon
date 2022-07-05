package l2f.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.geodata.GeoEngine;
import l2f.gameserver.idfactory.IdFactory;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.World;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.instances.SymbolInstance;
import l2f.gameserver.network.serverpackets.MagicSkillLaunched;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Env;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Location;

public final class EffectSymbol extends Effect
{
	private static final Logger _log = LoggerFactory.getLogger(EffectSymbol.class);

	private NpcInstance _symbol = null;

	public EffectSymbol(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	public EffectSymbol(Effect effect)
	{
		super(effect);
	}

	@Override
	public boolean checkCondition()
	{
		if (getSkill().getTargetType() != Skill.SkillTargetType.TARGET_SELF)
		{
			_log.error("Symbol skill with target != self, id = " + getSkill().getId());
			return false;
		}

		Skill skill = getSkill().getFirstAddedSkill();
		if (skill == null)
		{
			_log.error("Not implemented symbol skill, id = " + getSkill().getId());
			return false;
		}

		if (getEffector().isInZonePeace())
		{
			getEffector().sendMessage("You cannot do that in Peace Zone!");
			return false;
		}

		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		Skill skill = getSkill().getFirstAddedSkill();

		skill.setMagicType(getSkill().getMagicType());

		Location loc = _effected.getLoc();
		if (_effected.isPlayer() && ((Player) _effected).getGroundSkillLoc() != null)
		{
			loc = ((Player) _effected).getGroundSkillLoc();
			((Player) _effected).setGroundSkillLoc(null);
		}

		NpcTemplate template = NpcHolder.getInstance().getTemplate(_skill.getSymbolId());
		if (getTemplate()._count <= 1)
		{
			_symbol = new SymbolInstance(IdFactory.getInstance().getNextId(), template, _effected, skill);
		}
		else
		{
			_symbol = new NpcInstance(IdFactory.getInstance().getNextId(), template);
		}

		_symbol.setLevel(_effected.getLevel());
		_symbol.setReflection(_effected.getReflection());
		_symbol.spawnMe(loc);
	}

	@Override
	public void onExit()
	{
		super.onExit();

		if (_symbol != null && _symbol.isVisible())
		{
			_symbol.deleteMe();
		}

		_symbol = null;
	}

	@Override
	public boolean onActionTime()
	{
		if (getTemplate()._count <= 1)
		{
			return false;
		}

		Creature effector = getEffector();
		Skill skill = getSkill().getFirstAddedSkill();
		NpcInstance symbol = _symbol;
		double mpConsume = getSkill().getMpConsume();

		if (effector == null || skill == null || symbol == null)
		{
			return false;
		}

		if (mpConsume > effector.getCurrentMp())
		{
			effector.sendPacket(SystemMsg.NOT_ENOUGH_MP);
			return false;
		}

		effector.reduceCurrentMp(mpConsume, effector);

		for (Creature cha : World.getAroundCharacters(symbol, getSkill().getSkillRadius(), 200))
		{
			if (!cha.isDoor() && cha.getEffectList().getEffectsBySkill(skill) == null && skill.checkTarget(effector, cha, cha, false, false) == null)
			{
				if (skill.isOffensive() && !GeoEngine.canSeeTarget(symbol, cha, false))
				{
					continue;
				}
				List<Creature> targets = new ArrayList<Creature>(1);
				targets.add(cha);
				effector.callSkill(skill, targets, true);
				effector.broadcastPacket(new MagicSkillLaunched(symbol.getObjectId(), getSkill().getDisplayId(), getSkill().getDisplayLevel(), cha));
			}
		}

		return true;
	}
}