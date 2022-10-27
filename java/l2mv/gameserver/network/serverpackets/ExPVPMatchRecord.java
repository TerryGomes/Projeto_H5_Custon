package l2mv.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import l2mv.gameserver.model.entity.events.objects.UCMemberObject;
import l2mv.gameserver.model.entity.events.objects.UCTeamObject;

/**
 * @author VISTALL
 * 		<packet id="FE;007E" name="ExPVPMatchRecord" extended="true">
 			<part name="state" type="d" /> <!--0 - start, 1 - update, 2 - finish-->
 			<part name="win-index" type="d" />
 			<part name="loose-index" type="d" />
 			<part name="blue_kills" type="d" />
 			<part name="red_kills" type="d" />
 			<part name="blue_size" type="d" id="0"/>
 			<for id="0">
 				<part name="name" type="S" />
 				<part name="kills" type="d" />
 				<part name="deaths" type="d" />
 			</for>
 			<part name="red_size" type="d" id="1"/>
 			<for id="1">
 				<part name="name" type="S" />
 				<part name="kills" type="d" />
 				<part name="deaths" type="d" />
 			</for>
 		</packet>
 */
public class ExPVPMatchRecord extends L2GameServerPacket
{
	public static class Member
	{
		public String name;
		public int kills;
		public int deaths;

		public Member(String name, int kills, int deaths)
		{
			this.name = name;
			this.kills = kills;
			this.deaths = deaths;
		}
	}

	public static final int START = 0;
	public static final int UPDATE = 1;
	public static final int FINISH = 2;

	private int _type;
	private TeamType _winnerTeam;
	private int _blueKills;
	private int _redKills;

	private List<Member> _blueList;
	private List<Member> _redList;

	public ExPVPMatchRecord(int type, TeamType winnerTeam, UndergroundColiseumBattleEvent battleEvent)
	{
		this._type = type;
		this._winnerTeam = winnerTeam;

		UCTeamObject blueTeam = battleEvent.getFirstObject(TeamType.BLUE.name());
		this._blueKills = blueTeam.getKills();
		UCTeamObject redTeam = battleEvent.getFirstObject(TeamType.RED.name());
		this._redKills = redTeam.getKills();

		this._blueList = new ArrayList<Member>(9);
		for (UCMemberObject memberObject : blueTeam)
		{
			if (memberObject != null)
			{
				this._blueList.add(new Member(memberObject.getName(), memberObject.getKills(), memberObject.getDeaths()));
			}
		}

		this._redList = new ArrayList<Member>(9);
		for (UCMemberObject memberObject : redTeam)
		{
			if (memberObject != null)
			{
				this._redList.add(new Member(memberObject.getName(), memberObject.getKills(), memberObject.getDeaths()));
			}
		}
	}

	public ExPVPMatchRecord(int type, TeamType winnerTeam, int blueKills, int redKills, List<Member> blueTeam, List<Member> redTeam)
	{
		this._type = type;
		this._winnerTeam = winnerTeam;
		this._blueKills = blueKills;
		this._redKills = redKills;
		this._blueList = blueTeam;
		this._redList = redTeam;
	}

	@Override
	protected void writeImpl()
	{
		this.writeEx(0x7E);
		this.writeD(this._type);
		this.writeD(this._winnerTeam.ordinal());
		this.writeD(this._winnerTeam.revert().ordinal());
		this.writeD(this._blueKills);
		this.writeD(this._redKills);
		this.writeD(this._blueList.size());
		for (Member member : this._blueList)
		{
			this.writeS(member.name);
			this.writeD(member.kills);
			this.writeD(member.deaths);
		}
		this.writeD(this._redList.size());
		for (Member member : this._redList)
		{
			this.writeS(member.name);
			this.writeD(member.kills);
			this.writeD(member.deaths);
		}
	}
}