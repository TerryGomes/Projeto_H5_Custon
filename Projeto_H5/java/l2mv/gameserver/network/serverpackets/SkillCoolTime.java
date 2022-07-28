package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.skills.TimeStamp;

public class SkillCoolTime extends L2GameServerPacket
{
	private List<Skill> _list = Collections.emptyList();

	public SkillCoolTime(Player player)
	{
		Collection<TimeStamp> list = player.getSkillReuses();
		this._list = new ArrayList<Skill>(list.size());
		for (TimeStamp stamp : list)
		{
			if (!stamp.hasNotPassed())
			{
				continue;
			}
			l2mv.gameserver.model.Skill skill = player.getKnownSkill(stamp.getId());
			if (skill == null)
			{
				continue;
			}
			Skill sk = new Skill();
			sk.skillId = skill.getId();
			sk.level = skill.getLevel();
			sk.reuseBase = (int) Math.round(stamp.getReuseBasic() / 1000.);
			sk.reuseCurrent = (int) Math.round(stamp.getReuseCurrent() / 1000.);
			this._list.add(sk);
		}
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0xc7); // packet type
		this.writeD(this._list.size()); // Size of list
		for (int i = 0; i < this._list.size(); i++)
		{
			Skill sk = this._list.get(i);
			this.writeD(sk.skillId); // Skill Id
			this.writeD(sk.level); // Skill Level
			this.writeD(sk.reuseBase); // Total reuse delay, seconds
			this.writeD(sk.reuseCurrent); // Time remaining, seconds
		}
	}

	private static class Skill
	{
		public int skillId;
		public int level;
		public int reuseBase;
		public int reuseCurrent;
	}
}