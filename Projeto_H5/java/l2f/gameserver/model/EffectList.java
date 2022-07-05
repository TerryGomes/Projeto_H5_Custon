package l2f.gameserver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ArrayUtils;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;
import l2f.gameserver.Config;
import l2f.gameserver.skills.EffectType;
import l2f.gameserver.skills.effects.EffectTemplate;
import l2f.gameserver.skills.skillclasses.Transformation;
import l2f.gameserver.stats.Stats;
import l2f.gameserver.stats.funcs.FuncTemplate;

public class EffectList
{
	public static final int NONE_SLOT_TYPE = -1;
	public static final int BUFF_SLOT_TYPE = 0;
	public static final int MUSIC_SLOT_TYPE = 1;
	public static final int TRIGGER_SLOT_TYPE = 2;
	public static final int DEBUFF_SLOT_TYPE = 3;

	public static final int DEBUFF_LIMIT = 8;
	public static final int MUSIC_LIMIT = 12;
	public static final int TRIGGER_LIMIT = 12;

	private final Creature _actor;
	private List<Effect> _effects;
	private final Lock lock = new ReentrantLock();

	public EffectList(Creature owner)
	{
		_actor = owner;
	}

	/**
	 * Возвращает число эффектов соответствующее данному скиллу
	 */
	public int getEffectsCountForSkill(int skill_id)
	{
		if (isEmpty())
		{
			return 0;
		}

		int count = 0;

		for (Effect e : _effects)
		{
			if (e.getSkill().getId() == skill_id)
			{
				count++;
			}
		}

		return count;
	}

	public Effect getEffectByType(EffectType et)
	{
		if (isEmpty())
		{
			return null;
		}

		for (Effect e : _effects)
		{
			if (e.getEffectType() == et)
			{
				return e;
			}
		}

		return null;
	}

	public List<Effect> getEffectsBySkill(Skill skill)
	{
		if (skill == null)
		{
			return null;
		}
		return getEffectsBySkillId(skill.getId());
	}

	public List<Effect> getEffectsBySkillId(int skillId)
	{
		if (isEmpty())
		{
			return null;
		}

		List<Effect> list = new ArrayList<Effect>(2);
		for (Effect e : _effects)
		{
			if (e.getSkill().getId() == skillId)
			{
				list.add(e);
			}
		}

		return list.isEmpty() ? null : list;
	}

	public Effect getEffectByIndexAndType(int skillId, EffectType type)
	{
		if (isEmpty())
		{
			return null;
		}
		for (Effect e : _effects)
		{
			if ((e.getSkill().getId() == skillId) && (e.getEffectType() == type))
			{
				return e;
			}
		}

		return null;
	}

	public Effect getEffectByStackType(String type)
	{
		if (isEmpty())
		{
			return null;
		}
		for (Effect e : _effects)
		{
			if (e.getStackType().equals(type))
			{
				return e;
			}
		}

		return null;
	}

	public boolean containEffectFromSkills(int[] skillIds)
	{
		if (isEmpty())
		{
			return false;
		}

		int skillId;
		for (Effect e : _effects)
		{
			skillId = e.getSkill().getId();
			if (ArrayUtils.contains(skillIds, skillId))
			{
				return true;
			}
		}

		return false;
	}

	public List<Effect> getAllEffects()
	{
		if (isEmpty())
		{
			return Collections.emptyList();
		}
		return new ArrayList<Effect>(_effects);
	}

	public boolean isEmpty()
	{
		return (_effects == null) || _effects.isEmpty();
	}

	/**
	 * Возвращает первые эффекты для всех скиллов. Нужно для отображения не более чем 1 иконки для каждого скилла.
	 */
	public Effect[] getAllFirstEffects()
	{
		if (isEmpty())
		{
			return Effect.EMPTY_L2EFFECT_ARRAY;
		}

		Map<Integer, Effect> map = new LinkedHashMap<Integer, Effect>();
		for (Effect e : _effects)
		{
			map.put(e.getSkill().getId(), e); // putIfAbsent
		}

		return map.values().toArray(new Effect[map.size()]);
	}

	private void checkSlotLimit(Effect newEffect)
	{
		if (_effects == null)
		{
			return;
		}

		int slotType = getSlotType(newEffect);
		if (slotType == NONE_SLOT_TYPE)
		{
			return;
		}

		int size = 0;
		TIntArrayList skillIds = new TIntArrayList();
		for (Effect e : _effects)
		{
			if (e.isInUse())
			{
				if (e.getSkill().equals(newEffect.getSkill()))
				{
					return;
				}

				if (!skillIds.contains(e.getSkill().getId()))
				{
					int subType = getSlotType(e);
					if (subType == slotType)
					{
						size++;
						skillIds.add(e.getSkill().getId());
					}
				}
			}
		}

		int limit = 0;
		switch (slotType)
		{
		case BUFF_SLOT_TYPE:
			limit = _actor.getBuffLimit();
			break;
		case MUSIC_SLOT_TYPE:
			limit = Config.ALT_MUSIC_LIMIT;
			break;
		case DEBUFF_SLOT_TYPE:
			limit = Config.ALT_DEBUFF_LIMIT;
			break;
		case TRIGGER_SLOT_TYPE:
			limit = Config.ALT_TRIGGER_LIMIT;
			break;
		}

		if (size < limit)
		{
			return;
		}

		int skillId = 0;
		for (Effect e : _effects)
		{
			if (e.isInUse())
			{
				if (getSlotType(e) == slotType)
				{
					skillId = e.getSkill().getId();
					break;
				}
			}
		}

		if (skillId != 0)
		{
			stopEffect(skillId);
		}
	}

	public static int getSlotType(Effect e)
	{
		if (e.getSkill().isPassive() || e.getSkill().isToggle() || (e.getSkill() instanceof Transformation) || e.getStackType().equals(EffectTemplate.HP_RECOVER_CAST) || (e.getEffectType() == EffectType.Cubic))
		{
			return NONE_SLOT_TYPE;
		}
		else if (e.getSkill().isOffensive())
		{
			return DEBUFF_SLOT_TYPE;
		}
		else if (e.getSkill().isMusic())
		{
			return MUSIC_SLOT_TYPE;
		}
		else if (e.getSkill().isTrigger())
		{
			return TRIGGER_SLOT_TYPE;
		}
		else
		{
			return BUFF_SLOT_TYPE;
		}
	}

	public static boolean checkStackType(EffectTemplate ef1, EffectTemplate ef2)
	{
		if ((!ef1._stackType.equals(EffectTemplate.NO_STACK) && ef1._stackType.equalsIgnoreCase(ef2._stackType)) || (!ef1._stackType.equals(EffectTemplate.NO_STACK) && ef1._stackType.equalsIgnoreCase(ef2._stackType2)))
		{
			return true;
		}
		if ((!ef1._stackType2.equals(EffectTemplate.NO_STACK) && ef1._stackType2.equalsIgnoreCase(ef2._stackType)) || (!ef1._stackType2.equals(EffectTemplate.NO_STACK) && ef1._stackType2.equalsIgnoreCase(ef2._stackType2)))
		{
			return true;
		}
		return false;
	}

	public void addEffect(Effect effect)
	{
		// TODO [G1ta0] gag on the stat increase HP / MP / CP
		double hp = _actor.getCurrentHp();
		double mp = _actor.getCurrentMp();
		double cp = _actor.getCurrentCp();

		String stackType = effect.getStackType();
		boolean add = false;

		lock.lock();
		try
		{
			if (_effects == null)
			{
				_effects = new CopyOnWriteArrayList<Effect>();
			}

			if (stackType.equals(EffectTemplate.NO_STACK))
			{
				// Delete the same effects
				for (Effect e : _effects)
				{
					if (!e.isInUse())
					{
						continue;
					}

					if (e.getStackType().equals(EffectTemplate.NO_STACK) && (e.getSkill().getId() == effect.getSkill().getId()) && (e.getEffectType() == effect.getEffectType()))
					{
						// If the remaining duration of the effect of the old more than the duration of the new, the old reserve.
						if (effect.getTimeLeft() > e.getTimeLeft())
						{
							e.exit();
						}
						else
						{
							return;
						}
					}
				}
			}
			else
			{
				// Проверяем, нужно ли накладывать эффект, при совпадении StackType.
				// Новый эффект накладывается только в том случае, если у него больше StackOrder и больше длительность.
				// Если условия подходят - удаляем старый.
				for (Effect e : _effects)
				{
					if (!e.isInUse() || !checkStackType(e.getTemplate(), effect.getTemplate()))
					{
						continue;
					}

					if ((e.getSkill().getId() == effect.getSkill().getId()) && (e.getEffectType() != effect.getEffectType()))
					{
						break;
					}

					// Эффекты со StackOrder == -1 заменить нельзя (например, Root).
					if ((e.getStackOrder() == -1) || !e.maybeScheduleNext(effect))
					{
						return;
					}
				}
			}

			// Проверяем на лимиты бафов/дебафов
			checkSlotLimit(effect);

			// Добавляем новый эффект
			if (add = _effects.add(effect))
			{
				effect.setInUse(true);
			}
		}
		finally
		{
			lock.unlock();
		}

		if (!add)
		{
			return;
		}

		// Запускаем эффект
		effect.start();

		// TODO [G1ta0] затычка на статы повышающие HP/MP/CP
		for (FuncTemplate ft : effect.getTemplate().getAttachedFuncs())
		{
			if (ft._stat == Stats.MAX_HP)
			{
				_actor.setCurrentHp(hp, false);
			}
			else if (ft._stat == Stats.MAX_MP)
			{
				_actor.setCurrentMp(mp);
			}
			else if (ft._stat == Stats.MAX_CP)
			{
				_actor.setCurrentCp(cp);
			}
		}

		// Обновляем иконки
		_actor.updateStats();
		_actor.updateEffectIcons();
	}

	/**
	 * Удаление эффекта из списка
	 * @param effect эффект для удаления
	 */
	public void removeEffect(Effect effect)
	{
		if (effect == null)
		{
			return;
		}

		boolean remove = false;

		lock.lock();
		try
		{
			if ((_effects == null) || ((remove = _effects.remove(effect)) == false))
			{
				return;
			}
		}
		finally
		{
			lock.unlock();
		}

		if (!remove)
		{
			return;
		}

		_actor.updateStats();
		_actor.updateEffectIcons();
	}

	public void stopAllEffects()
	{
		if (isEmpty())
		{
			return;
		}

		lock.lock();
		try
		{
			for (Effect e : _effects)
			{
				e.exit();
			}
		}
		finally
		{
			lock.unlock();
		}

		_actor.updateStats();
		_actor.updateEffectIcons();
	}

	public void stopEffect(int skillId)
	{
		if (isEmpty())
		{
			return;
		}

		for (Effect e : _effects)
		{
			if (e.getSkill().getId() == skillId)
			{
				e.exit();
			}
		}
	}

	public void stopEffect(Skill skill)
	{
		if (skill != null)
		{
			stopEffect(skill.getId());
		}
	}

	public void stopEffectByDisplayId(int skillId)
	{
		if (isEmpty())
		{
			return;
		}

		for (Effect e : _effects)
		{
			if (e.getSkill().getDisplayId() == skillId)
			{
				e.exit();
			}
		}
	}

	public void stopEffects(EffectType type)
	{
		if (isEmpty())
		{
			return;
		}

		for (Effect e : _effects)
		{
			if (e.getEffectType() == type)
			{
				e.exit();
			}
		}
	}

	/**
	 * Находит скиллы с указанным эффектом, и останавливает у этих скиллов все эффекты (не только указанный).
	 */
	public void stopAllSkillEffects(EffectType type)
	{
		if (isEmpty())
		{
			return;
		}

		TIntHashSet skillIds = new TIntHashSet();

		for (Effect e : _effects)
		{
			if (e.getEffectType() == type)
			{
				skillIds.add(e.getSkill().getId());
			}
		}

		for (int skillId : skillIds.toArray())
		{
			stopEffect(skillId);
		}
	}
}