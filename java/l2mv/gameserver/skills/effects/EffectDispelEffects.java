package l2mv.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.taskmanager.CancelTaskManager;

/**
 * Cancellation, Touch of Death, Insane Crusher, Banes, Infinity Spear effect
 */
public class EffectDispelEffects extends Effect
{
	private static final Pattern STACK_TYPE_SEPARATOR = Pattern.compile(";");

	private final String dispelType;
	private final int cancelRate;
	private final String[] stackTypes;
	private final int negateMin;
	private final int negateMax;

	/*
	 * cancelRate is skill dependant constant:
	 * Cancel - 25
	 * Touch of Death/Insane Crusher - 25
	 * Mage/Warrior Bane - 80
	 * Mass Mage/Warrior Bane - 40
	 * Infinity Spear - 10
	 */
	public EffectDispelEffects(Env env, EffectTemplate template)
	{
		super(env, template);
		dispelType = template.getParam().getString("dispelType", "");
		cancelRate = template.getParam().getInteger("cancelRate", 0);
		negateMin = template.getParam().getInteger("negateMin", 2);
		negateMax = template.getParam().getInteger("negateCount", 5);
		stackTypes = STACK_TYPE_SEPARATOR.split(template.getParam().getString("negateStackTypes", ""));
	}

	public EffectDispelEffects(Effect effect)
	{
		super(effect);
		dispelType = getTemplate().getParam().getString("dispelType", "");
		cancelRate = getTemplate().getParam().getInteger("cancelRate", 0);
		negateMin = getTemplate().getParam().getInteger("negateMin", 1);
		negateMax = getTemplate().getParam().getInteger("negateCount", 5);
		stackTypes = STACK_TYPE_SEPARATOR.split(getTemplate().getParam().getString("negateStackTypes", ""));
	}

	@Override
	public void onStart()
	{
		List<Effect> effectList = createEffectList();
		if (effectList.isEmpty())
		{
			return;
		}

		final boolean calcEachChance = dispelType.equalsIgnoreCase("cancellation");
		if (!calcEachChance && !Rnd.chance(cancelRate))
		{
			return;
		}

		int currentCancelChance = cancelRate; // Synerge - Now the first buff has 100% of chances
		final LinkedList<Effect> cancelledEffects = new LinkedList<Effect>();

		effectList = effectList.subList(0, Math.min(effectList.size(), negateMax * 2));

		int count = 0;
		int negated = 1; // Start from 1
		for (Effect effect : effectList)
		{
			if (effect == null)
			{
				continue;
			}

			negated++;

			// We estimate the success of the cancel on this effect if currentCancelChance is not 100 it makes 100
			if (calcEachChance && currentCancelChance < 100 && !calcCancelSuccess(_effector, _effected, effect, getSkill(), currentCancelChance))
			{
				continue;
			}

			cancelledEffects.add(effect);
			effect.exit();
			_effected.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effect.getSkill().getId(), effect.getSkill().getLevel()));
			count++;

			// Synerge - For each buff we reduce the chances by 40%, starting from 100%
			if (calcEachChance && negated >= negateMin)
			{
				currentCancelChance *= 0.6;
			}

			if (negateMax > 0 && count >= negateMax)
			{
				break;
			}
		}

		if (_effected.isPlayable() && !cancelledEffects.isEmpty() && !dispelType.equalsIgnoreCase("cleanse"))
		{
			CancelTaskManager.getInstance().addNewCancelTask((Playable) _effected, cancelledEffects);
		}
	}

	private List<Effect> createEffectList()
	{
		final List<Effect> musicList = new ArrayList<>();
		final List<Effect> buffList = new ArrayList<>();

		for (Effect e : _effected.getEffectList().getAllEffects())
		{
			switch (dispelType)
			{
			case "cancellation":
				if (!e.isOffensive() && !e.getSkill().isToggle() && e.isCancelable())
				{
					if (e.getSkill().isMusic())
					{
						musicList.add(e);
					}
					else
					{
						buffList.add(e);
					}
				}
				break;
			case "bane":
				if (!e.isOffensive() && (ArrayUtils.contains(stackTypes, e.getStackType()) || ArrayUtils.contains(stackTypes, e.getStackType2())) && e.isCancelable())
				{
					buffList.add(e);
				}
				break;
			case "cleanse":
				if (e.isOffensive() && e.isCancelable())
				{
					buffList.add(e);
				}
				break;
			}
		}

		List<Effect> effectList = new ArrayList<>();
		Collections.reverse(musicList);
		effectList.addAll(musicList);
		Collections.reverse(buffList);
		effectList.addAll(buffList);

		// Synerge - Now we sort the buffs to make random lists
		Collections.shuffle(effectList);

		return effectList;
	}

	public static final boolean calcCancelSuccess(Creature activeChar, Creature target, Effect buff, Skill cancelSk, double dispelRate)
	{
		final int cancelLvl = cancelSk.getMagicLevel();
		double rate = dispelRate;
		final double vuln = target.calcStat(Stats.CANCEL_RESIST, 0, activeChar, cancelSk);

		double resMod = 1 + (vuln / 100);

		rate = rate / resMod;

		rate *= buff.getSkill().getMagicLevel() > 0 ? 1 + ((cancelLvl - buff.getSkill().getMagicLevel()) / 100.) : 1;

		if (rate > 99)
		{
			rate = 99;
		}
		else if (rate < 1)
		{
			rate = 1;
		}

		return Rnd.chance(rate);
	}

	@Override
	protected boolean onActionTime()
	{
		return false;
	}
}
