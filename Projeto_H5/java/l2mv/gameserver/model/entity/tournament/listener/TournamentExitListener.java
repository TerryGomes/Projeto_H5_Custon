package l2mv.gameserver.model.entity.tournament.listener;

import java.util.List;
import java.util.Map;

import l2mv.gameserver.listener.actor.player.OnPlayerExitListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.tournament.ActiveBattleManager;
import l2mv.gameserver.model.entity.tournament.BattleInstance;
import l2mv.gameserver.model.entity.tournament.Team;
import l2mv.gameserver.utils.ChatUtil;
import l2mv.gameserver.utils.Language;

public class TournamentExitListener implements OnPlayerExitListener
{
	private final BattleInstance _battleInstance;

	public TournamentExitListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}

	@Override
	public void onPlayerExit(Player player)
	{
		ActiveBattleManager.onExitGame(_battleInstance, player);
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
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByLogOut9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.Won.Fight.ByLogOut with onlinePlayers Size = " + onlinePlayers.size());
		}
		}
	}

	public static Map<Language, String> getWonBattleMessageToShow(Team winnerTeam)
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
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByLogOut9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.Won.Battle.ByLogOut with onlinePlayers Size = " + onlinePlayers.size());
		}
		}
	}

	public static Map<Language, String> getWonBattleGlobalMessageToShow(Team winnerTeam)
	{
		final List<Player> onlinePlayers = winnerTeam.getOnlinePlayers();
		final String[] playerNicknames = new String[onlinePlayers.size()];
		for (int i = 0; i < playerNicknames.length; ++i)
		{
			playerNicknames[i] = onlinePlayers.get(i).getName();
		}
		switch (onlinePlayers.size())
		{
		case 0:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.0", new Object[0]);
		}
		case 1:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.Walkover.9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.GlobalNotification.Walkover with onlinePlayers Size = " + onlinePlayers.size());
		}
		}
	}
}
