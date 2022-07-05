package l2f.gameserver.model.actor.recorder;

import l2f.commons.collections.CollectionUtils;
import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Element;
import l2f.gameserver.model.base.TeamType;
import l2f.gameserver.model.matching.MatchingRoom;
import l2f.gameserver.network.serverpackets.ExStorageMaxCount;

public final class PlayerStatsChangeRecorder extends CharStatsChangeRecorder<Player>
{
	public static final int BROADCAST_KARMA = 8;
	public static final int SEND_STORAGE_INFO = 16;
	public static final int SEND_MAX_LOAD = 32;
	public static final int SEND_CUR_LOAD = 64;
	public static final int BROADCAST_CHAR_INFO2 = 128;
	private int _maxCp;
	private int _maxLoad;
	private int _curLoad;
	private int[] _attackElement = new int[6];
	private int[] _defenceElement = new int[6];
	private long _exp;
	private int _sp;
	private int _karma;
	private int _pk;
	private int _pvp;
	private int _fame;
	private int _inventory;
	private int _warehouse;
	private int _clan;
	private int _trade;
	private int _recipeDwarven;
	private int _recipeCommon;
	private int _partyRoom;
	private String _title = "";
	private int _cubicsHash;

	public PlayerStatsChangeRecorder(Player activeChar)
	{
		super(activeChar);
	}

	@Override
	protected void refreshStats()
	{
		_maxCp = set(4, _maxCp, ((Player) _activeChar).getMaxCp());

		super.refreshStats();

		_maxLoad = set(34, _maxLoad, ((Player) _activeChar).getMaxLoad());
		_curLoad = set(64, _curLoad, ((Player) _activeChar).getCurrentLoad());

		for (Element e : Element.VALUES)
		{
			_attackElement[e.getId()] = set(2, _attackElement[e.getId()], ((Player) _activeChar).getAttack(e));
			_defenceElement[e.getId()] = set(2, _defenceElement[e.getId()], ((Player) _activeChar).getDefence(e));
		}

		_exp = set(2, _exp, ((Player) _activeChar).getExp());
		_sp = set(2, _sp, ((Player) _activeChar).getIntSp());
		_pk = set(2, _pk, ((Player) _activeChar).getPkKills());
		_pvp = set(2, _pvp, ((Player) _activeChar).getPvpKills());
		_fame = set(2, _fame, ((Player) _activeChar).getFame());

		_karma = set(8, _karma, ((Player) _activeChar).getKarma());

		_inventory = set(16, _inventory, ((Player) _activeChar).getInventoryLimit());
		_warehouse = set(16, _warehouse, ((Player) _activeChar).getWarehouseLimit());
		_clan = set(16, _clan, Config.WAREHOUSE_SLOTS_CLAN);
		_trade = set(16, _trade, ((Player) _activeChar).getTradeLimit());
		_recipeDwarven = set(16, _recipeDwarven, ((Player) _activeChar).getDwarvenRecipeLimit());
		_recipeCommon = set(16, _recipeCommon, ((Player) _activeChar).getCommonRecipeLimit());
		_cubicsHash = set(1, _cubicsHash, CollectionUtils.hashCode(((Player) _activeChar).getCubics()));
		_partyRoom = set(1, _partyRoom, ((((Player) _activeChar).getMatchingRoom() != null) && (((Player) _activeChar).getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING)
					&& (((Player) _activeChar).getMatchingRoom().getLeader() == _activeChar)) ? ((Player) _activeChar).getMatchingRoom().getId() : 0);
		_team = ((TeamType) set(128, _team, ((Player) _activeChar).getTeam()));
		_title = set(1, _title, ((Player) _activeChar).getTitle());
	}

	@Override
	protected void onSendChanges()
	{
		super.onSendChanges();

		if ((_changes & 0x80) == 128)
		{
			((Player) _activeChar).broadcastCharInfo();
			if (((Player) _activeChar).getPet() != null)
			{
				((Player) _activeChar).getPet().broadcastCharInfo();
			}
		}
		/* 100 */ if ((_changes & 0x1) == 1)
		{
			((Player) _activeChar).broadcastCharInfo();
		}
		else if ((_changes & 0x2) == 2)
		{
			((Player) _activeChar).sendUserInfo();
		}
		if ((_changes & 0x40) == 64)
		{
			((Player) _activeChar).sendStatusUpdate(false, false, new int[]
			{
				14
			});
		}
		if ((_changes & 0x20) == 32)
		{
			((Player) _activeChar).sendStatusUpdate(false, false, new int[]
			{
				15
			});
		}
		if ((_changes & 0x8) == 8)
		{
			((Player) _activeChar).sendStatusUpdate(true, false, new int[]
			{
				27
			});
		}
		if ((_changes & 0x10) == 16)
		{
			((Player) _activeChar).sendPacket(new ExStorageMaxCount((Player) _activeChar));
		}
	}
}