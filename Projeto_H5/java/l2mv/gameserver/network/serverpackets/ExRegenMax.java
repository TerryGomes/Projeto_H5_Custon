package l2mv.gameserver.network.serverpackets;

public class ExRegenMax extends L2GameServerPacket
{
	private double _max;
	private int _count;
	private int _time;

	public ExRegenMax(double max, int count, int time)
	{
		this._max = max * .66;
		this._count = count;
		this._time = time;
	}

	public static final int POTION_HEALING_GREATER = 16457;
	public static final int POTION_HEALING_MEDIUM = 16440;
	public static final int POTION_HEALING_LESSER = 16416;

	/**
	 * Пример пакета - Пришло после использования Healing Potion (инфа для Interlude, в Kamael пакет не изменился)
	 *
	 * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 38 40 // Healing Potion
	 * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 49 40 // Greater Healing Potion
	 * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 20 40 // Lesser Healing Potion
	 *
	 * FE - тип
	 * 01 00 - субтип
	 * 01 00 00 00 - хз что
	 * 0F 00 00 00 - count?
	 * 03 00 00 00 - время?
	 * 00 00 00 00 00 00 38 40 - максимум?
	 */
	@Override
	protected void writeImpl()
	{
		this.writeEx(0x01);
		this.writeD(1);
		this.writeD(this._count);
		this.writeD(this._time);
		this.writeF(this._max);
	}
}