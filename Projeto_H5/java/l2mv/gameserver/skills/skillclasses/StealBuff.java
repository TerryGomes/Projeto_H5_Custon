package l2mv.gameserver.skills.skillclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.skills.EffectType;
import l2mv.gameserver.skills.effects.EffectDispelEffects;
import l2mv.gameserver.skills.effects.EffectTemplate;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.taskmanager.CancelTaskManager;
import l2mv.gameserver.templates.StatsSet;

public class StealBuff extends Skill
{
	private final int stealCount;
	private final int stealChance;

	public StealBuff(StatsSet set)
	{
		super(set);
		stealCount = set.getInteger("stealCount", 1);
		stealChance = set.getInteger("stealChance", 100);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if ((target == null) || !target.isPlayer())
		{
			activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target == null || !target.isPlayer())
			{
				continue;
			}

			List<Effect> effectList = createEffectList(target);
			if (effectList.isEmpty())
			{
				continue;
			}

			final LinkedList<Effect> cancelledEffects = new LinkedList<Effect>();
			effectList = effectList.subList(0, Math.min(effectList.size(), stealCount * 2));

			int count = 0;
			for (Effect effect : effectList)
			{
				// We estimate the success of the cancel on this effect
				if ((effect == null) || !EffectDispelEffects.calcCancelSuccess(activeChar, target, effect, this, stealChance))
				{
					continue;
				}

				Effect stolenEffect = cloneEffect(activeChar, effect);
				if (stolenEffect != null)
				{
					activeChar.getEffectList().addEffect(stolenEffect);
				}

				cancelledEffects.add(effect);
				effect.exit();
				target.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effect.getSkill().getId(), effect.getSkill().getLevel()));
				count++;

				if (stealCount > 0 && count >= stealCount)
				{
					break;
				}
			}
			getEffects(activeChar, target, getActivateRate() > 0, false);

			if (target.isPlayable())
			{
				CancelTaskManager.getInstance().addNewCancelTask((Playable) target, cancelledEffects);
			}
		}
		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}

	private static List<Effect> createEffectList(Creature target)
	{
		final List<Effect> musicList = new ArrayList<>();
		final List<Effect> buffList = new ArrayList<>();

		for (Effect e : target.getEffectList().getAllEffects())
		{
			if (!canBeStolen(e))
			{
				continue;
			}

			if (e.getSkill().isMusic())
			{
				musicList.add(e);
			}
			else
			{
				buffList.add(e);
			}
		}

		// Synerge - Instead of puttin all the songs/dances before the buffs, we put 1 song 1 buff, alternated so the steal is better
		Collections.reverse(musicList);
		Collections.reverse(buffList);
		final List<Effect> effectList = new ArrayList<>();
		for (int i = 0; i < Math.max(musicList.size(), buffList.size()); i++)
		{
			if (musicList.size() > i)
			{
				effectList.add(musicList.get(i));
			}
			if (buffList.size() > i)
			{
				effectList.add(buffList.get(i));
			}
		}

		return effectList;
	}

	private static boolean canBeStolen(Effect e)
	{
		if ((e == null) || !e.isInUse() || !e.isCancelable() || e.getSkill().isToggle())
		{
			return false;
		}
		if (e.getSkill().isPassive() || e.getSkill().isOffensive() || e.getEffectType() == EffectType.Vitality || e.getEffectType() == EffectType.VitalityMaintenance)
		{
			return false;
		}
		if (e.getTemplate()._applyOnCaster)
		{
			return false;
		}
		return true;
	}

	private static Effect cloneEffect(Creature cha, Effect eff)
	{
		Skill skill = eff.getSkill();

		for (EffectTemplate et : skill.getEffectTemplates())
		{
			Effect effect = et.getEffect(new Env(cha, cha, skill));
			if (effect != null)
			{
				effect.setCount(eff.getCount());
				effect.setPeriod(eff.getCount() == 1 ? eff.getPeriod() - eff.getTime() : eff.getPeriod());
				return effect;
			}
		}
		return null;
	}
}