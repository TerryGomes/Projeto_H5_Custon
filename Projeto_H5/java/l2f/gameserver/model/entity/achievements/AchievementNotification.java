package l2f.gameserver.model.entity.achievements;

import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.listener.actor.OnKillListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.instances.ChestInstance;
import l2f.gameserver.model.instances.GuardInstance;
import l2f.gameserver.network.serverpackets.TutorialShowQuestionMark;

/**
 * @author Nik (total rework)
 * @author Midnex
 * @author Promo (htmls)
 */
public class AchievementNotification
{
	private static AchievementNotification _instance;
	private static Listener _listener;

	public static AchievementNotification getInstance()
	{
		if (_instance == null)
		{
			_instance = new AchievementNotification(3000);
		}
		return _instance;
	}

	private ScheduledFuture<?> _globalNotification;

	public AchievementNotification(int intervalInMiliseconds)
	{
		_listener = new Listener();
		CharListenerList.addGlobal(_listener);

		if (Config.ENABLE_ACHIEVEMENTS)
		{
			_globalNotification = ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
			{
				try
				{
					for (Player player : GameObjectsStorage.getAllPlayersForIterate())
					{
						if ((player == null) || (player.getAchievements() == null))
						{
							continue;
						}

						for (Entry<Integer, Integer> arco : player.getAchievements().entrySet())
						{
							int achievementId = arco.getKey();
							int achievementLevel = arco.getValue();
							if (Achievements.getInstance().getMaxLevel(achievementId) <= achievementLevel)
							{
								continue;
							}

							Achievement nextLevelAchievement = Achievements.getInstance().getAchievement(achievementId, ++achievementLevel);
							if ((nextLevelAchievement != null) && nextLevelAchievement.isDone(player.getCounters().getPoints(nextLevelAchievement.getType())))
							{
								// Make a question mark button.
								player.sendPacket(new TutorialShowQuestionMark(player.getObjectId()));
								break;
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}, intervalInMiliseconds, intervalInMiliseconds);
		}
	}

	public void stopNotification()
	{
		if (_globalNotification != null)
		{
			_globalNotification.cancel(true);
			_globalNotification = null;
		}
	}

	private static class Listener implements OnKillListener
	{
		@Override
		public void onKill(Creature actor, Creature victim)
		{
			if (!Config.ENABLE_ACHIEVEMENTS)
			{
				return;
			}

			Player player = actor.getPlayer();
			if (player == null)
			{
				return;
			}

			if (victim.isPlayer())
			{
				victim.getPlayer().getCounters().timesDied++;
			}

			if (victim.isNpc())
			{
				if (victim instanceof ChestInstance)
				{
					player.getCounters().treasureBoxesOpened++;
				}
				else if (victim instanceof GuardInstance)
				{
					player.getCounters().townGuardsKilled++;
				}
				else if (victim.isSiegeGuard())
				{
					player.getCounters().siegeGuardsKilled++;
				}
			}

			if ((player.getLevel() - victim.getLevel()) >= 10)
			{
				return;
			}

			if (victim.isMonster())
			{
				player.getCounters().mobsKilled++;
			}

			if (victim.isRaid())
			{
				player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().raidsKilled++);
			}

			if (victim.isChampion())
			{
				player.getCounters().championsKilled++;
			}

			if (victim.isNpc())
			{
				switch (victim.getNpcId())
				{
				case 29001: // Queen Ant
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().antQueenKilled++);
					break;
				case 29006: // Core
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().coreKilled++);
					break;
				case 29014: // Orfen
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().orfenKilled++);
					break;
				case 29019: // Antharas
				case 29066: // Antharas
				case 29067: // Antharas
				case 29068: // Antharas
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().antharasKilled++);
					break;
				case 29020: // Baium
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().baiumKilled++);
					break;
				case 29022: // Zaken Lv. 60
				case 29176: // Zaken Lv. 60
				case 29181: // Zaken Lv. 83
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().zakenKilled++);
					break;
				case 29028: // Valakas
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().valakasKilled++);
					break;
				case 29047: // Scarlet van Halisha / Frintezza
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().frintezzaKilled++);
					break;
				case 29065: // Sailren
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().sailrenKilled++);
					break;
				case 29099: // Baylor
				case 29186: // Baylor
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().baylorKilled++);
					break;
				case 29118: // Beleth
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().belethKilled++);
					break;
				case 29163: // Tiat
				case 29175: // Tiat
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().tiatKilled++);
					break;
				case 29179: // Freya Normal
				case 29180: // Freya Hard
					player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().freyaKilled++);
					break;
				}
			}
		}

		@Override
		public boolean ignorePetOrSummon()
		{
			return true;
		}

	}
}
