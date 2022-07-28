package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;

/**
 * format   ddd+[dSddddddddddddd{ddSddddd}]
 */
public class PartySmallWindowAll extends L2GameServerPacket
{
	private final int leaderId, loot;
	private final List<PartySmallWindowMemberInfo> members = new ArrayList<PartySmallWindowMemberInfo>();

	public PartySmallWindowAll(Party party, Player exclude)
	{
		this.leaderId = party.getLeader().getObjectId();
		this.loot = party.getLootDistribution();

		for (Player member : party.getMembers())
		{
			if (member != exclude)
			{
				this.members.add(new PartySmallWindowMemberInfo(member));
			}
		}
	}

	public PartySmallWindowAll(int leaderId, int loot, List<PartySmallWindowMemberInfo> members)
	{
		this.leaderId = leaderId;
		this.loot = loot;
		this.members.addAll(members);
	}

	@Override
	protected final void writeImpl()
	{
		this.writeC(0x4E);
		this.writeD(this.leaderId); // c3 party leader id
		this.writeD(this.loot); // c3 party loot type (0,1,2,....)
		this.writeD(this.members.size());
		for (PartySmallWindowMemberInfo member : this.members)
		{
			this.writeD(member._id);
			this.writeS(member._name);
			this.writeD(member.curCp);
			this.writeD(member.maxCp);
			this.writeD(member.curHp);
			this.writeD(member.maxHp);
			this.writeD(member.curMp);
			this.writeD(member.maxMp);
			this.writeD(member.level);
			this.writeD(member.class_id);
			this.writeD(0);// writeD(0x01); ??
			this.writeD(member.race_id);
			this.writeD(0);
			this.writeD(0);

			if (member.pet_id != 0)
			{
				this.writeD(member.pet_id);
				this.writeD(member.pet_NpcId);
				this.writeS(member.pet_Name);
				this.writeD(member.pet_curHp);
				this.writeD(member.pet_maxHp);
				this.writeD(member.pet_curMp);
				this.writeD(member.pet_maxMp);
				this.writeD(member.pet_level);
			}
			else
			{
				this.writeD(0);
			}
		}
	}

	public static class PartySmallWindowMemberInfo
	{
		public String _name, pet_Name;
		public int _id, curCp, maxCp, curHp, maxHp, curMp, maxMp, level, class_id, race_id;
		public int pet_id, pet_NpcId, pet_curHp, pet_maxHp, pet_curMp, pet_maxMp, pet_level;

		public PartySmallWindowMemberInfo(Player member)
		{
			this._name = member.getName();
			this._id = member.getObjectId();
			this.curCp = (int) member.getCurrentCp();
			this.maxCp = member.getMaxCp();
			this.curHp = (int) member.getCurrentHp();
			this.maxHp = member.getMaxHp();
			this.curMp = (int) member.getCurrentMp();
			this.maxMp = member.getMaxMp();
			this.level = member.getLevel();
			this.class_id = member.getClassId().getId();
			this.race_id = member.getRace().ordinal();

			Summon pet = member.getPet();
			if (pet != null)
			{
				this.pet_id = pet.getObjectId();
				this.pet_NpcId = pet.getNpcId() + 1000000;
				this.pet_Name = pet.getName();
				this.pet_curHp = (int) pet.getCurrentHp();
				this.pet_maxHp = pet.getMaxHp();
				this.pet_curMp = (int) pet.getCurrentMp();
				this.pet_maxMp = pet.getMaxMp();
				this.pet_level = pet.getLevel();
			}
			else
			{
				this.pet_id = 0;
			}
		}

		public PartySmallWindowMemberInfo(String name, int objectId, int level, int classId, int raceId, boolean barsFull)
		{
			this._name = name;
			this._id = objectId;
			if (barsFull)
			{
				this.curCp = 1;
				this.maxCp = 1;
				this.curHp = 1;
				this.maxHp = 1;
				this.curMp = 1;
				this.maxMp = 1;
			}
			else
			{
				this.curCp = 0;
				this.maxCp = 1;
				this.curHp = 0;
				this.maxHp = 1;
				this.curMp = 0;
				this.maxMp = 1;
			}
			this.level = level;
			this.class_id = classId;
			this.race_id = raceId;
			this.pet_id = 0;
		}
	}
}