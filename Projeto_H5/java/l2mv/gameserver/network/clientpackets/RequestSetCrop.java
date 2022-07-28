package l2mv.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.instancemanager.CastleManorManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.Castle;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.templates.manor.CropProcure;

/**
 * Format: (ch) dd [dddc]
 * d - manor id
 * d - size
 * [
 * d - crop id
 * d - sales
 * d - price
 * c - reward type
 * ]
 */
public class RequestSetCrop extends L2GameClientPacket
{
	private int _count, _manorId;

	private long[] _items; // _size*4

	@Override
	protected void readImpl()
	{
		this._manorId = this.readD();
		this._count = this.readD();
		if (this._count * 21 > this._buf.remaining() || this._count > Short.MAX_VALUE || this._count < 1)
		{
			this._count = 0;
			return;
		}
		this._items = new long[this._count * 4];
		for (int i = 0; i < this._count; i++)
		{
			this._items[i * 4 + 0] = this.readD();
			this._items[i * 4 + 1] = this.readQ();
			this._items[i * 4 + 2] = this.readQ();
			this._items[i * 4 + 3] = this.readC();
			if (this._items[i * 4 + 0] < 1 || this._items[i * 4 + 1] < 0 || this._items[i * 4 + 2] < 0)
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

		List<CropProcure> crops = new ArrayList<CropProcure>(this._count);
		for (int i = 0; i < this._count; i++)
		{
			int id = (int) this._items[i * 4 + 0];
			long sales = this._items[i * 4 + 1];
			long price = this._items[i * 4 + 2];
			int type = (int) this._items[i * 4 + 3];
			if (id > 0)
			{
				CropProcure s = CastleManorManager.getInstance().getNewCropProcure(id, sales, type, price, sales);
				crops.add(s);
			}
		}

		caslte.setCropProcure(crops, CastleManorManager.PERIOD_NEXT);
		caslte.saveCropData(CastleManorManager.PERIOD_NEXT);
	}
}