package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;

public class ExEventMatchTeamInfo extends L2GameServerPacket
{
	@SuppressWarnings("unused")
	private int leader_id, loot;
	private List<EventMatchTeamInfo> members = new ArrayList<EventMatchTeamInfo>();

	public ExEventMatchTeamInfo(List<Player> party, Player exclude)
	{
		this.leader_id = party.get(0).getObjectId();
		this.loot = party.get(0).getParty().getLootDistribution();

		for (Player member : party)
		{
			if (!member.equals(exclude))
			{
				this.members.add(new EventMatchTeamInfo(member));
			}
		}
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x1C);
		// TODO dcd[dSdddddddddd]
	}

	public static class EventMatchTeamInfo
	{
		public String _name, pet_Name;
		public int _id, curCp, maxCp, curHp, maxHp, curMp, maxMp, level, class_id, race_id;
		public int pet_id, pet_NpcId, pet_curHp, pet_maxHp, pet_curMp, pet_maxMp, pet_level;

		public EventMatchTeamInfo(Player member)
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
	}
}