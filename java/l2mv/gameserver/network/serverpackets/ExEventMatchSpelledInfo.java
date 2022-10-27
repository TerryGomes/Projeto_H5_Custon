package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;

public class ExEventMatchSpelledInfo extends L2GameServerPacket
{
	// chdd(dhd)
	private int char_obj_id = 0;
	private List<Effect> _effects;

	class Effect
	{
		int skillId;
		int dat;
		int duration;

		public Effect(int skillId, int dat, int duration)
		{
			this.skillId = skillId;
			this.dat = dat;
			this.duration = duration;
		}
	}

	public ExEventMatchSpelledInfo()
	{
		this._effects = new ArrayList<Effect>();
	}

	public void addEffect(int skillId, int dat, int duration)
	{
		this._effects.add(new Effect(skillId, dat, duration));
	}

	public void addSpellRecivedPlayer(Player cha)
	{
		if (cha != null)
		{
			this.char_obj_id = cha.getObjectId();
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x04);

		this.writeD(this.char_obj_id);
		this.writeD(this._effects.size());
		for (Effect temp : this._effects)
		{
			this.writeD(temp.skillId);
			this.writeH(temp.dat);
			this.writeD(temp.duration);
		}
	}
}