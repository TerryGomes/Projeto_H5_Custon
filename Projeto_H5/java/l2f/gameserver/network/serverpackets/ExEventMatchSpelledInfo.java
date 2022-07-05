package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.model.Player;

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
		_effects = new ArrayList<Effect>();
	}

	public void addEffect(int skillId, int dat, int duration)
	{
		_effects.add(new Effect(skillId, dat, duration));
	}

	public void addSpellRecivedPlayer(Player cha)
	{
		if (cha != null)
		{
			char_obj_id = cha.getObjectId();
		}
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x04);

		writeD(char_obj_id);
		writeD(_effects.size());
		for (Effect temp : _effects)
		{
			writeD(temp.skillId);
			writeH(temp.dat);
			writeD(temp.duration);
		}
	}
}