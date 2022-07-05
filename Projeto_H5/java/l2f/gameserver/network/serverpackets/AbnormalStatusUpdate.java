package l2f.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * sample
 *
 * 0000: 85 02 00 10 04 00 00 01 00 4b 02 00 00 2c 04 00    .........K...,..
 * 0010: 00 01 00 58 02 00 00                               ...X...
 *
 *
 * format   h (dhd)
 *
 * @version $Revision: 1.3.2.1.2.6 $ $Date: 2005/04/05 19:41:08 $
 */
public class AbnormalStatusUpdate extends L2GameServerPacket
{
	public static final int INFINITIVE_EFFECT = -1;
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

	public AbnormalStatusUpdate()
	{
		_effects = new ArrayList<Effect>();
	}

	public void addEffect(int skillId, int dat, int duration)
	{
		_effects.add(new Effect(skillId, dat, duration));
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x85);

		writeH(_effects.size());

		for (Effect temp : _effects)
		{
			writeD(temp.skillId);
			writeH(temp.dat);
			writeD(temp.duration);
		}
	}
}