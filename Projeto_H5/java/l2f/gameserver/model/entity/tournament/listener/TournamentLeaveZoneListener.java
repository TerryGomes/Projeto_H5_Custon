package l2f.gameserver.model.entity.tournament.listener;

import java.util.List;
import java.util.Map;

import l2f.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.entity.tournament.ActiveBattleManager;
import l2f.gameserver.model.entity.tournament.BattleInstance;
import l2f.gameserver.model.entity.tournament.Team;
import l2f.gameserver.utils.ChatUtil;
import l2f.gameserver.utils.Language;

public class TournamentLeaveZoneListener implements OnZoneEnterLeaveListener
{
	private final BattleInstance _battleInstance;

	public TournamentLeaveZoneListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}

	@Override
	public void onZoneEnter(Zone zone, Creature actor)
	{
		ActiveBattleManager.onEnterZone(_battleInstance, zone, actor);
	}

	@Override
	public void onZoneLeave(Zone zone, Creature actor)
	{
		ActiveBattleManager.onLeaveZone(_battleInstance, zone, actor);
	}

	public static Map<Language, String> getWonFightMessageToShow(Team winnerTeam)
	{
		final List<Player> onlinePlayers = winnerTeam.getOnlinePlayers();
		final String[] playerNicknames = new String[onlinePlayers.size()];
		for (int i = 0; i < playerNicknames.length; ++i)
		{
			playerNicknames[i] = onlinePlayers.get(i).getName();
		}
		switch (onlinePlayers.size())
		{
		case 1:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLeaveZone9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.Won.Fight.ByLeaveZone with onlinePlayers Size = " + onlinePlayers.size());
		}
		}
	}

	@Override
	public void onEquipChanged(Zone zone, Creature actor)
	{
	}
}
