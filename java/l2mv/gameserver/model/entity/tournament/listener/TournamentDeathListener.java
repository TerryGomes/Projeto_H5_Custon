package l2mv.gameserver.model.entity.tournament.listener;

import java.util.List;
import java.util.Map;

import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.tournament.ActiveBattleManager;
import l2mv.gameserver.model.entity.tournament.BattleInstance;
import l2mv.gameserver.model.entity.tournament.Team;
import l2mv.gameserver.utils.ChatUtil;
import l2mv.gameserver.utils.Language;

public class TournamentDeathListener implements OnDeathListener
{
	private final BattleInstance _battleInstance;

	public TournamentDeathListener(BattleInstance battleInstance)
	{
		_battleInstance = battleInstance;
	}

	@Override
	public void onDeath(Creature actor, Creature killer)
	{
		ActiveBattleManager.onDeath(_battleInstance, actor, killer);
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
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Fight.ByKill9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.Won.Fight.ByKill with onlinePlayers Size = " + onlinePlayers.size());
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
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.Won.Battle.ByKill9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.Won.Battle.ByKill with onlinePlayers Size = " + onlinePlayers.size());
		}
		}
	}

	public static Map<Language, String> getWonFightGlobalMessageToShow(Team winnerTeam)
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
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonFight.9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.GlobalNotification.WonFight with onlinePlayers Size = " + onlinePlayers.size());
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
		case 1:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.1", (Object[]) playerNicknames);
		}
		case 2:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.2", (Object[]) playerNicknames);
		}
		case 3:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.3", (Object[]) playerNicknames);
		}
		case 4:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.4", (Object[]) playerNicknames);
		}
		case 5:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.5", (Object[]) playerNicknames);
		}
		case 6:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.6", (Object[]) playerNicknames);
		}
		case 7:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.7", (Object[]) playerNicknames);
		}
		case 8:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.8", (Object[]) playerNicknames);
		}
		case 9:
		{
			return ChatUtil.getMessagePerLang("Tournament.GlobalNotification.WonBattle.9", (Object[]) playerNicknames);
		}
		default:
		{
			throw new AssertionError("Couldn't find String for Tournament.GlobalNotification.WonBattle with onlinePlayers Size = " + onlinePlayers.size());
		}
		}
	}
}
