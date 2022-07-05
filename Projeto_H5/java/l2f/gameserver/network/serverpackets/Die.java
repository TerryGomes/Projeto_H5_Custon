package l2f.gameserver.network.serverpackets;

import java.util.HashMap;
import java.util.Map;

import l2f.gameserver.fandc.managers.GmEventManager;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.RestartType;
import l2f.gameserver.model.entity.events.GlobalEvent;
import l2f.gameserver.model.entity.tournament.ActiveBattleManager;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.model.pledge.Clan;

public class Die extends L2GameServerPacket
{
	private final int _objectId;
	private final boolean _fake;
	@SuppressWarnings("unused")
	private boolean _sweepable, isPvPevents;

	private final Map<RestartType, Boolean> _types = new HashMap<RestartType, Boolean>(RestartType.VALUES.length);

	public Die(Creature cha)
	{
		_objectId = cha.getObjectId();
		_fake = !cha.isDead();

		if (cha.isMonster())
		{
			_sweepable = ((MonsterInstance) cha).isSweepActive();
		}
		else if (cha.isPlayer() && GmEventManager.getInstance().canResurrect(cha.getPlayer()))
		{
			Player player = (Player) cha;
			put(RestartType.FIXED, player.getPlayerAccess().ResurectFixed || ((player.getInventory().getCountOf(10649) > 0 || player.getInventory().getCountOf(13300) > 0) && !player.isOnSiegeField()));
			put(RestartType.AGATHION, player.isAgathionResAvailable());
			put(RestartType.TO_VILLAGE, true);

			Clan clan = player.getClan();
			if (clan != null)
			{
				put(RestartType.TO_CLANHALL, clan.getHasHideout() > 0);
				put(RestartType.TO_CASTLE, clan.getCastle() > 0);
				put(RestartType.TO_FORTRESS, clan.getHasFortress() > 0);
			}

			for (GlobalEvent e : cha.getEvents())
			{
				e.checkRestartLocs(player, _types);
			}
			// If player is just leaving Fight club. to Giran timer is taking care of him
			if (!player.isInFightClub() && player.getReflection().getId() == ReflectionManager.FIGHT_CLUB_REFLECTION_ID)
			{
				_types.clear();
			}

			if (player.isInTournament())
			{
				_types.clear();
			}

			if (ActiveBattleManager.clearRestartTypes(player))
			{
				_types.clear();
			}

			if (player.getVar("isPvPevents") != null)
			{
				isPvPevents = true;
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		if (_fake)
		{
			return;
		}

		writeC(0x00);
		writeD(_objectId);
		writeD(get(RestartType.TO_VILLAGE)); // to nearest village
		writeD(get(RestartType.TO_CLANHALL)); // to hide away
		writeD(get(RestartType.TO_CASTLE)); // to castle
		writeD(get(RestartType.TO_FLAG));// to siege HQ
		writeD(_sweepable ? 0x01 : 0x00); // sweepable (blue glow)
		writeD(get(RestartType.FIXED));// FIXED
		writeD(get(RestartType.TO_FORTRESS));// fortress
		writeC(0); // show die animation
		writeD(get(RestartType.AGATHION));// agathion ress button
		writeD(0x00); // additional free space
	}

	private void put(RestartType t, boolean b)
	{
		_types.put(t, b);
	}

	private boolean get(RestartType t)
	{
		Boolean b = _types.get(t);
		return b != null && b;
	}
}