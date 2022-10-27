package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;

public class ExOlympiadSpelledInfo extends L2GameServerPacket
{
	private int char_obj_id = 0;
	private List<Effect> _effects;

	class Effect
	{
		int skillId;
		int level;
		int duration;

		public Effect(int skillId, int level, int duration)
		{
			this.skillId = skillId;
			this.level = level;
			this.duration = duration;
		}
	}

	public ExOlympiadSpelledInfo()
	{
		this._effects = new ArrayList<Effect>();
	}

	public void addEffect(int skillId, int level, int duration)
	{
		this._effects.add(new Effect(skillId, level, duration));
	}

	public void addSpellRecivedPlayer(Player cha)
	{
		if (cha != null)
		{
			this.char_obj_id = cha.getObjectId();
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x7b);

		this.writeD(this.char_obj_id);
		this.writeD(this._effects.size());
		for (Effect temp : this._effects)
		{
			this.writeD(temp.skillId);
			this.writeH(temp.level);
			this.writeD(temp.duration);
		}
	}
}