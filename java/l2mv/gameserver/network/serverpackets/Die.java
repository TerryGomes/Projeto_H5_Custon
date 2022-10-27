package l2mv.gameserver.network.serverpackets;

import java.util.HashMap;
import java.util.Map;

import l2mv.gameserver.fandc.managers.GmEventManager;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.RestartType;
import l2mv.gameserver.model.entity.events.GlobalEvent;
import l2mv.gameserver.model.entity.tournament.ActiveBattleManager;
import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.model.pledge.Clan;

public class Die extends L2GameServerPacket
{
	private final int _objectId;
	private final boolean _fake;
	@SuppressWarnings("unused")
	private boolean _sweepable, isPvPevents;

	private final Map<RestartType, Boolean> _types = new HashMap<RestartType, Boolean>(RestartType.VALUES.length);

	public Die(Creature cha)
	{
		this._objectId = cha.getObjectId();
		this._fake = !cha.isDead();

		if (cha.isMonster())
		{
			this._sweepable = ((MonsterInstance) cha).isSweepActive();
		}
		else if (cha.isPlayer() && GmEventManager.getInstance().canResurrect(cha.getPlayer()))
		{
			Player player = (Player) cha;
			this.put(RestartType.FIXED, player.getPlayerAccess().ResurectFixed || ((player.getInventory().getCountOf(10649) > 0 || player.getInventory().getCountOf(13300) > 0) && !player.isOnSiegeField()));
			this.put(RestartType.AGATHION, player.isAgathionResAvailable());
			this.put(RestartType.TO_VILLAGE, true);

			Clan clan = player.getClan();
			if (clan != null)
			{
				this.put(RestartType.TO_CLANHALL, clan.getHasHideout() > 0);
				this.put(RestartType.TO_CASTLE, clan.getCastle() > 0);
				this.put(RestartType.TO_FORTRESS, clan.getHasFortress() > 0);
			}

			for (GlobalEvent e : cha.getEvents())
			{
				e.checkRestartLocs(player, this._types);
			}
			// If player is just leaving Fight club. to Giran timer is taking care of him
			if (!player.isInFightClub() && player.getReflection().getId() == ReflectionManager.FIGHT_CLUB_REFLECTION_ID)
			{
				this._types.clear();
			}

			if (player.isInTournament())
			{
				this._types.clear();
			}

			if (ActiveBattleManager.clearRestartTypes(player))
			{
				this._types.clear();
			}

			if (player.getVar("isPvPevents") != null)
			{
				this.isPvPevents = true;
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		if (this._fake)
		{
			return;
		}

		this.writeC(0x00);
		this.writeD(this._objectId);
		this.writeD(this.get(RestartType.TO_VILLAGE)); // to nearest village
		this.writeD(this.get(RestartType.TO_CLANHALL)); // to hide away
		this.writeD(this.get(RestartType.TO_CASTLE)); // to castle
		this.writeD(this.get(RestartType.TO_FLAG));// to siege HQ
		this.writeD(this._sweepable ? 0x01 : 0x00); // sweepable (blue glow)
		this.writeD(this.get(RestartType.FIXED));// FIXED
		this.writeD(this.get(RestartType.TO_FORTRESS));// fortress
		this.writeC(0); // show die animation
		this.writeD(this.get(RestartType.AGATHION));// agathion ress button
		this.writeD(0x00); // additional free space
	}

	private void put(RestartType t, boolean b)
	{
		this._types.put(t, b);
	}

	private boolean get(RestartType t)
	{
		Boolean b = this._types.get(t);
		return b != null && b;
	}
}