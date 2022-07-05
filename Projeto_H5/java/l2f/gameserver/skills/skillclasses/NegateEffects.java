package l2f.gameserver.skills.skillclasses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.skills.EffectType;
import l2f.gameserver.stats.Formulas;
import l2f.gameserver.templates.StatsSet;

public class NegateEffects extends Skill
{
	private Map<EffectType, Integer> _negateEffects = new HashMap<EffectType, Integer>();
	private Map<String, Integer> _negateStackType = new HashMap<String, Integer>();
	private final boolean _onlyPhysical;
	private final boolean _negateDebuffs;

	public NegateEffects(StatsSet set)
	{
		super(set);

		String[] negateEffectsString = set.getString("negateEffects", "").split(";");
		for (int i = 0; i < negateEffectsString.length; i++)
		{
			if (!negateEffectsString[i].isEmpty())
			{
				String[] entry = negateEffectsString[i].split(":");
				_negateEffects.put(Enum.valueOf(EffectType.class, entry[0]), entry.length > 1 ? Integer.decode(entry[1]) : Integer.MAX_VALUE);
			}
		}

		String[] negateStackTypeString = set.getString("negateStackType", "").split(";");
		for (int i = 0; i < negateStackTypeString.length; i++)
		{
			if (!negateStackTypeString[i].isEmpty())
			{
				String[] entry = negateStackTypeString[i].split(":");
				_negateStackType.put(entry[0], entry.length > 1 ? Integer.decode(entry[1]) : Integer.MAX_VALUE);
			}
		}

		_onlyPhysical = set.getBool("onlyPhysical", false);
		_negateDebuffs = set.getBool("negateDebuffs", true);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		for (Creature target : targets)
		{
			if (target != null)
			{
				if (!_negateDebuffs && !Formulas.calcSkillSuccess(activeChar, target, this, getActivateRate()))
				{
					activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(getId(), getLevel()));
					continue;
				}

				if (!_negateEffects.isEmpty())
				{
					for (Map.Entry<EffectType, Integer> e : _negateEffects.entrySet())
					{
						negateEffectAtPower(target, e.getKey(), e.getValue().intValue());
					}
				}

				if (!_negateStackType.isEmpty())
				{
					for (Map.Entry<String, Integer> e : _negateStackType.entrySet())
					{
						negateEffectAtPower(target, e.getKey(), e.getValue().intValue());
					}
				}

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}
		}

		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}
	}

	private void negateEffectAtPower(Creature target, EffectType type, int power)
	{
		for (Effect e : target.getEffectList().getAllEffects())
		{
			Skill skill = e.getSkill();
			if (_onlyPhysical && skill.isMagic() || !skill.isCancelable() || skill.isOffensive() && !_negateDebuffs)
			{
				continue;
			}
			// Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
			if (!skill.isOffensive() && skill.getMagicLevel() > getMagicLevel() && Rnd.chance(skill.getMagicLevel() - getMagicLevel()))
			{
				continue;
			}
			if (e.getEffectType() == type && e.getStackOrder() <= power)
			{
				e.exit();
			}
		}
	}

	private void negateEffectAtPower(Creature target, String stackType, int power)
	{
		for (Effect e : target.getEffectList().getAllEffects())
		{
			Skill skill = e.getSkill();
			if (_onlyPhysical && skill.isMagic() || !skill.isCancelable() || skill.isOffensive() && !_negateDebuffs)
			{
				continue;
			}
			// Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
			if (!skill.isOffensive() && skill.getMagicLevel() > getMagicLevel() && Rnd.chance(skill.getMagicLevel() - getMagicLevel()))
			{
				continue;
			}
			if (e.checkStackType(stackType) && e.getStackOrder() <= power)
			{
				e.exit();
			}
		}
	}
}