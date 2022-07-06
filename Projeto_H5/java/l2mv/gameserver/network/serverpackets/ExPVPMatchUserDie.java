package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.base.TeamType;
import l2mv.gameserver.model.entity.events.impl.UndergroundColiseumBattleEvent;
import l2mv.gameserver.model.entity.events.objects.UCTeamObject;

/**
 * @author VISTALL
 */
public class ExPVPMatchUserDie extends L2GameServerPacket
{
	private int _blueKills, _redKills;

	public ExPVPMatchUserDie(UndergroundColiseumBattleEvent e)
	{
		UCTeamObject team = e.getFirstObject(TeamType.BLUE.name());
		_blueKills = team.getKills();
		team = e.getFirstObject(TeamType.RED.name());
		_redKills = team.getKills();
	}

	public ExPVPMatchUserDie(int blueKills, int redKills)
	{
		_blueKills = blueKills;
		_redKills = redKills;
	}

	@Override
	protected final void writeImpl()
	{
		writeEx(0x7F);
		writeD(_blueKills);
		writeD(_redKills);
	}
}