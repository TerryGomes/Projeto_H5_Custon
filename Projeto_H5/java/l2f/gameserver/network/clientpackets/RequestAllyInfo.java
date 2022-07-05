package l2f.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.pledge.Alliance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.tables.ClanTable;

public class RequestAllyInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Alliance ally = player.getAlliance();
		if (ally == null)
		{
			return;
		}

		int clancount = 0;
		Clan leaderclan = player.getAlliance().getLeader();
		clancount = ClanTable.getInstance().getAlliance(leaderclan.getAllyId()).getMembers().length;
		int[] online = new int[clancount + 1];
		int[] count = new int[clancount + 1];
		Clan[] clans = player.getAlliance().getMembers();
		for (int i = 0; i < clancount; i++)
		{
			online[i + 1] = clans[i].getOnlineMembers(0).size();
			count[i + 1] = clans[i].getAllSize();
			online[0] += online[i + 1];
			count[0] += count[i + 1];
		}

		List<L2GameServerPacket> packets = new ArrayList<L2GameServerPacket>(7 + 5 * clancount);
		packets.add(new SystemMessage2(SystemMsg.ALLIANCE_INFORMATION));
		packets.add(new SystemMessage2(SystemMsg.ALLIANCE_NAME_S1).addString(player.getClan().getAlliance().getAllyName()));
		packets.add(new SystemMessage2(SystemMsg.CONNECTION_S1__TOTAL_S2).addInteger(online[0]).addInteger(count[0])); // Connection
		packets.add(new SystemMessage2(SystemMsg.ALLIANCE_LEADER_S2_OF_S1).addString(leaderclan.getName()).addString(leaderclan.getLeaderName()));
		packets.add(new SystemMessage2(SystemMsg.AFFILIATED_CLANS_TOTAL_S1_CLANS).addInteger(clancount)); // clan count
		packets.add(new SystemMessage2(SystemMsg.CLAN_INFORMATION));
		for (int i = 0; i < clancount; i++)
		{
			packets.add(new SystemMessage2(SystemMsg.CLAN_NAME_S1).addString(clans[i].getName()));
			packets.add(new SystemMessage2(SystemMsg.CLAN_LEADER__S1).addString(clans[i].getLeaderName()));
			packets.add(new SystemMessage2(SystemMsg.CLAN_LEVEL_S1).addInteger(clans[i].getLevel()));
			packets.add(new SystemMessage2(SystemMsg.CONNECTION_S1__TOTAL_S2).addInteger(online[i + 1]).addInteger(count[i + 1]));
			packets.add(new SystemMessage2(SystemMsg.__DASHES__));
		}
		packets.add(new SystemMessage2(SystemMsg.__EQUALS__));

		player.sendPacket(packets);
	}
}