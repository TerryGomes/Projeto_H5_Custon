package l2mv.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.instancemanager.CastleManorManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.templates.manor.SeedProduction;

/**
 * Format: (ch) dd [ddd]
 * d - manor id
 * d - size
 * [
 * d - seed id
 * d - sales
 * d - price
 * ]
 */
public class RequestSetSeed extends L2GameClientPacket
{
	private int _count, _manorId;

	private long[] _items; // _size*3

	@Override
	protected void readImpl()
	{
		this._manorId = this.readD();
		this._count = this.readD();
		if (this._count * 20 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}
		this._items = new long[this._count * 3];
		for (int i = 0; i < this._count; i++)
		{
			this._items[i * 3 + 0] = this.readD();
			this._items[i * 3 + 1] = this.readQ();
			this._items[i * 3 + 2] = this.readQ();
			if (this._items[i * 3 + 0] < 1 || this._items[i * 3 + 1] < 0 || this._items[i * 3 + 2] < 0)
			{
				this._count = 0;
				return;
			}
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		if (activeChar == null || this._count == 0)
		{
			return;
		}

		if (activeChar.getClan() == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		Castle caslte = ResidenceHolder.getInstance().getResidence(Castle.class, this._manorId);
		if (caslte.getOwnerId() != activeChar.getClanId() // clan owns castle
					|| (activeChar.getClanPrivileges() & Clan.CP_CS_MANOR_ADMIN) != Clan.CP_CS_MANOR_ADMIN) // has manor rights
		{
			activeChar.sendActionFailed();
			return;
		}

		List<SeedProduction> seeds = new ArrayList<SeedProduction>(this._count);
		for (int i = 0; i < this._count; i++)
		{
			int id = (int) this._items[i * 3 + 0];
			long sales = this._items[i * 3 + 1];
			long price = this._items[i * 3 + 2];
			if (id > 0)
			{
				SeedProduction s = CastleManorManager.getInstance().getNewSeedProduction(id, sales, price, sales);
				seeds.add(s);
			}
		}

		caslte.setSeedProduction(seeds, CastleManorManager.PERIOD_NEXT);
		caslte.saveSeedData(CastleManorManager.PERIOD_NEXT);
	}
}