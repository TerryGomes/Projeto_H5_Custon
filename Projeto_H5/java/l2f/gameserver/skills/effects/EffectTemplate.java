package l2f.gameserver.skills.effects;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.EffectList;
import l2f.gameserver.skills.AbnormalEffect;
import l2f.gameserver.skills.EffectType;
import l2f.gameserver.stats.Env;
import l2f.gameserver.stats.StatTemplate;
import l2f.gameserver.stats.conditions.Condition;
import l2f.gameserver.templates.StatsSet;

public final class EffectTemplate extends StatTemplate
{
	private static final Logger _log = LoggerFactory.getLogger(EffectTemplate.class);

	public static final EffectTemplate[] EMPTY_ARRAY = new EffectTemplate[0];

	public static final String NO_STACK = "none".intern();
	public static final String HP_RECOVER_CAST = "HpRecoverCast".intern();

	public Condition _attachCond;
	public final double _value;
	public final int _count;
	public final long _period; // in milliseconds
	public AbnormalEffect _abnormalEffect;
	public AbnormalEffect _abnormalEffect2;
	public AbnormalEffect _abnormalEffect3;

	public final EffectType _effectType;

	public final String _stackType;
	public final String _stackType2;
	public final int _stackOrder;
	public final int _displayId;
	public final int _displayLevel;

	public final boolean _applyOnCaster;
	public final boolean _applyOnSummon;
	public final boolean _cancelOnAction;
	public final boolean _isReflectable;
	private final Boolean _isSaveable;
	private final Boolean _isCancelable;
	private final Boolean _isOffensive;

	private final StatsSet _paramSet;
	private final int _chance;

	public EffectTemplate(StatsSet set)
	{
		_value = set.getDouble("value");
		_count = set.getInteger("count", 1) < 0 ? Integer.MAX_VALUE : set.getInteger("count", 1);
		_period = Math.min(Integer.MAX_VALUE, 1000 * (set.getInteger("time", 1) < 0 ? Integer.MAX_VALUE : set.getInteger("time", 1)));
		_abnormalEffect = set.getEnum("abnormal", AbnormalEffect.class);
		_abnormalEffect2 = set.getEnum("abnormal2", AbnormalEffect.class);
		_abnormalEffect3 = set.getEnum("abnormal3", AbnormalEffect.class);
		_stackType = set.getString("stackType", NO_STACK);
		_stackType2 = set.getString("stackType2", NO_STACK);
		_stackOrder = set.getInteger("stackOrder", _stackType.equals(NO_STACK) && _stackType2.equals(NO_STACK) ? 1 : 0);
		_applyOnCaster = set.getBool("applyOnCaster", false);
		_applyOnSummon = set.getBool("applyOnSummon", true);
		_cancelOnAction = set.getBool("cancelOnAction", false);
		_isReflectable = set.getBool("isReflectable", true);
		_isSaveable = set.isSet("isSaveable") ? set.getBool("isSaveable") : null;
		_isCancelable = set.isSet("isCancelable") ? set.getBool("isCancelable") : null;
		_isOffensive = set.isSet("isOffensive") ? set.getBool("isOffensive") : null;
		_displayId = set.getInteger("displayId", 0);
		_displayLevel = set.getInteger("displayLevel", 0);
		_effectType = set.getEnum("name", EffectType.class);
		_chance = set.getInteger("chance", Integer.MAX_VALUE);
		_paramSet = set;
	}

	public Effect getEffect(Env env)
	{
		if (_attachCond != null && !_attachCond.test(env))
		{
			return null;
		}
		try
		{
			return _effectType.makeEffect(env, this);
		}
		catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e)
		{
			_log.error("Error while getting Effect ", e);
		}

		return null;
	}

	public void attachCond(Condition c)
	{
		_attachCond = c;
	}

	public int getCount()
	{
		return _count;
	}

	public long getPeriod()
	{
		return _period;
	}

	public EffectType getEffectType()
	{
		return _effectType;
	}

	public Effect getSameByStackType(List<Effect> list)
	{
		for (Effect ef : list)
		{
			if (ef != null && EffectList.checkStackType(ef.getTemplate(), this))
			{
				return ef;
			}
		}
		return null;
	}

	public Effect getSameByStackType(EffectList list)
	{
		return getSameByStackType(list.getAllEffects());
	}

	public Effect getSameByStackType(Creature actor)
	{
		return getSameByStackType(actor.getEffectList().getAllEffects());
	}

	public StatsSet getParam()
	{
		return _paramSet;
	}

	public int chance(int val)
	{
		return _chance == Integer.MAX_VALUE ? val : _chance;
	}

	public boolean isSaveable(boolean def)
	{
		return _isSaveable != null ? _isSaveable.booleanValue() : def;
	}

	public boolean isCancelable(boolean def)
	{
		return _isCancelable != null ? _isCancelable.booleanValue() : def;
	}

	public boolean isOffensive(boolean def)
	{
		return _isOffensive != null ? _isOffensive.booleanValue() : def;
	}
}