package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import l2mv.gameserver.model.Playable;
import l2mv.gameserver.utils.EffectsComparator;

public class PartySpelled extends L2GameServerPacket
{
	private final int _type;
	private final int _objId;
	private final List<Effect> _effects;

	public PartySpelled(Playable activeChar, boolean full)
	{
		_objId = activeChar.getObjectId();
		_type = activeChar.isPet() ? 1 : activeChar.isSummon() ? 2 : 0;
		// 0 - L2Player // 1 - петы // 2 - саммоны
		_effects = new ArrayList<Effect>();
		if (full)
		{
			l2mv.gameserver.model.Effect[] effects = activeChar.getEffectList().getAllFirstEffects();
			Arrays.sort(effects, EffectsComparator.getInstance());
			for (l2mv.gameserver.model.Effect effect : effects)
			{
				if (effect != null && effect.isInUse())
				{
					effect.addPartySpelledIcon(this);
				}
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xf4);
		writeD(_type);
		writeD(_objId);
		writeD(_effects.size());
		for (Effect temp : _effects)
		{
			writeD(temp._skillId);
			writeH(temp._level);
			writeD(temp._duration);
		}
	}

	public void addPartySpelledEffect(int skillId, int level, int duration)
	{
		_effects.add(new Effect(skillId, level, duration));
	}

	static class Effect
	{
		final int _skillId;
		final int _level;
		final int _duration;

		public Effect(int skillId, int level, int duration)
		{
			_skillId = skillId;
			_level = level;
			_duration = duration;
		}
	}
}