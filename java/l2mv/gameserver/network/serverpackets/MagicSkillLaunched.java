package l2mv.gameserver.network.serverpackets;

import java.util.Collection;
import java.util.Collections;

import l2mv.gameserver.model.Creature;

public class MagicSkillLaunched extends L2GameServerPacket
{
	private final int _casterId;
	private final int _skillId;
	private final int _skillLevel;
	private final Collection<Creature> _targets;

	public MagicSkillLaunched(int casterId, int skillId, int skillLevel, Creature target)
	{
		this._casterId = casterId;
		this._skillId = skillId;
		this._skillLevel = skillLevel;
		this._targets = Collections.singletonList(target);
	}

	public MagicSkillLaunched(int casterId, int skillId, int skillLevel, Collection<Creature> targets)
	{
		this._casterId = casterId;
		this._skillId = skillId;
		this._skillLevel = skillLevel;
		this._targets = targets;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x54);
		this.writeD(this._casterId);
		this.writeD(this._skillId);
		this.writeD(this._skillLevel);
		this.writeD(this._targets.size());
		for (Creature target : this._targets)
		{
			if (target != null)
			{
				this.writeD(target.getObjectId());
			}
		}
	}
}