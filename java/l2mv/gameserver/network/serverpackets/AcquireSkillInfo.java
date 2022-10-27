package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.model.SkillLearn;
import l2mv.gameserver.model.base.AcquireType;

/**
 * Reworked: VISTALL
 */
public class AcquireSkillInfo extends L2GameServerPacket
{
	private SkillLearn _learn;
	private AcquireType _type;
	private List<Require> _reqs = Collections.emptyList();

	public AcquireSkillInfo(AcquireType type, SkillLearn learn)
	{
		this._type = type;
		this._learn = learn;
		if (this._learn.getItemId() != 0)
		{
			this._reqs = new ArrayList<Require>(1);
			this._reqs.add(new Require(99, this._learn.getItemId(), this._learn.getItemCount(), 50));
		}
	}

	@Override
	public void writeImpl()
	{
		this.writeC(0x91);
		this.writeD(this._learn.getId());
		this.writeD(this._learn.getLevel());
		this.writeD(this._learn.getCost()); // sp/rep
		this.writeD(this._type.ordinal());

		this.writeD(this._reqs.size()); // requires size

		for (Require temp : this._reqs)
		{
			this.writeD(temp.type);
			this.writeD(temp.itemId);
			this.writeQ(temp.count);
			this.writeD(temp.unk);
		}
	}

	private static class Require
	{
		public int itemId;
		public long count;
		public int type;
		public int unk;

		public Require(int pType, int pItemId, long pCount, int pUnk)
		{
			this.itemId = pItemId;
			this.type = pType;
			this.count = pCount;
			this.unk = pUnk;
		}
	}
}