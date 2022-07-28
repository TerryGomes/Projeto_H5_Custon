package l2mv.gameserver.network.clientpackets;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.instancemanager.CursedWeaponsManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.CursedWeapon;
import l2mv.gameserver.network.serverpackets.ExCursedWeaponLocation;
import l2mv.gameserver.network.serverpackets.ExCursedWeaponLocation.CursedWeaponInfo;
import l2mv.gameserver.utils.Location;

public class RequestCursedWeaponLocation extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		Creature activeChar = this.getClient().getActiveChar();
		if (activeChar == null)
		{
			return;
		}

		List<CursedWeaponInfo> list = new ArrayList<CursedWeaponInfo>();
		for (CursedWeapon cw : CursedWeaponsManager.getInstance().getCursedWeapons())
		{
			Location pos = cw.getWorldPosition();
			if (pos != null)
			{
				list.add(new CursedWeaponInfo(pos, cw.getItemId(), cw.isActivated() ? 1 : 0));
			}
		}

		activeChar.sendPacket(new ExCursedWeaponLocation(list));
	}
}