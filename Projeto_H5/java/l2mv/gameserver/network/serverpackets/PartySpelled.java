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
		this._objId = activeChar.getObjectId();
		this._type = activeChar.isPet() ? 1 : activeChar.isSummon() ? 2 : 0;
		// 0 - L2Player // 1 - петы // 2 - саммоны
		this._effects = new ArrayList<Effect>();
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
		this.writeC(0xf4);
		this.writeD(this._type);
		this.writeD(this._objId);
		this.writeD(this._effects.size());
		for (Effect temp : this._effects)
		{
			this.writeD(temp._skillId);
			this.writeH(temp._level);
			this.writeD(temp._duration);
		}
	}

	public void addPartySpelledEffect(int skillId, int level, int duration)
	{
		this._effects.add(new Effect(skillId, level, duration));
	}

	static class Effect
	{
		final int _skillId;
		final int _level;
		final int _duration;

		public Effect(int skillId, int level, int duration)
		{
			this._skillId = skillId;
			this._level = level;
			this._duration = duration;
		}
	}
}