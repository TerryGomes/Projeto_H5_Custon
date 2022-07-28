package l2mv.gameserver.network.serverpackets;

import java.util.List;

/**
 * Format: ch ddd [ddd]
 */
public class ExGetBossRecord extends L2GameServerPacket
{
	private List<BossRecordInfo> _bossRecordInfo;
	private int _ranking;
	private int _totalPoints;

	public ExGetBossRecord(int ranking, int totalScore, List<BossRecordInfo> bossRecordInfo)
	{
		this._ranking = ranking; // char ranking
		this._totalPoints = totalScore; // char total points
		this._bossRecordInfo = bossRecordInfo;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x34);

		this.writeD(this._ranking); // char ranking
		this.writeD(this._totalPoints); // char total points

		this.writeD(this._bossRecordInfo.size()); // list size
		for (BossRecordInfo w : this._bossRecordInfo)
		{
			this.writeD(w._bossId);
			this.writeD(w._points);
			this.writeD(w._unk1);// don`t know
		}
	}

	public static class BossRecordInfo
	{
		public int _bossId;
		public int _points;
		public int _unk1;

		public BossRecordInfo(int bossId, int points, int unk1)
		{
			this._bossId = bossId;
			this._points = points;
			this._unk1 = unk1;
		}
	}
}