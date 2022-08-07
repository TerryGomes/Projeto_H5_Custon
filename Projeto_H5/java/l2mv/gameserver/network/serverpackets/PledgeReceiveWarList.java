package l2mv.gameserver.network.serverpackets;

import java.util.List;

import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.ClanWar;

/**
 * @author GodWorld
 * @reworked by Bonux
**/
public class PledgeReceiveWarList extends L2GameServerPacket
{
	private Clan _clan;
	private int _state;
	private int _page;

	public PledgeReceiveWarList(Clan clan, int state, int page)
	{
		this._clan = clan;
		this._page = page;
		this._state = state;
	}

	@Override
	protected final void writeImpl()
	{
		this.writeEx(0x3f);
		this.writeD(this._page);

		List<ClanWar> wars = this._clan.getClanWars();

		this.writeD(wars.size());
		for (ClanWar war : wars)
		{
			Clan opposingClan = war.getAttackerClan();
			if (opposingClan == this._clan)
			{
				opposingClan = war.getOpposingClan();
			}

			if (opposingClan == null)
			{
				continue;
			}

			int pointDiff = war.getPointDiff(this._clan);
			int duration = (int) (war.getPeriodDuration() / 1000L);
			if (war.getClanWarState(this._clan).ordinal() >= 3)
			{
				duration += 172800;
			}
			else if (war.getClanWarState(this._clan).ordinal() <= 1)
			{
				duration += 345600;
			}

			this.writeS(opposingClan.getName());
			this.writeD(war.getClanWarState(this._clan).ordinal());
			this.writeD(duration);

			this.writeD(pointDiff);
			this.writeD(war.calculateWarProgress(pointDiff).ordinal());
			this.writeD(opposingClan.getAllSize());
		}
	}
}