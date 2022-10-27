package l2mv.gameserver.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import l2mv.gameserver.Config;
import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
//import l2mv.gameserver.model.entity.CCPHelpers.CCPAccountRecover;
import l2mv.gameserver.model.entity.CCPHelpers.CCPCWHPrivilages;
import l2mv.gameserver.model.entity.CCPHelpers.CCPOffline;
import l2mv.gameserver.model.entity.CCPHelpers.CCPPassword;
import l2mv.gameserver.model.entity.CCPHelpers.CCPPoll;
import l2mv.gameserver.model.entity.CCPHelpers.CCPRepair;
import l2mv.gameserver.model.entity.CCPHelpers.CCPSecondaryPassword;
import l2mv.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2mv.gameserver.model.entity.CCPHelpers.itemLogs.CCPItemLogs;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.DeleteObject;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;

public class CharacterControlPanel
{
	private static CharacterControlPanel _instance;

	public String useCommand(Player activeChar, String text, String bypass)
	{
		// While some1 is currently writing secondary password
		if (activeChar.isBlocked() && !text.contains("secondaryPass"))
		{
			return null;
		}

		String[] param = text.split(" ");
		if (param.length == 0)
		{
			return "char.htm";
		}
		else if (param[0].equalsIgnoreCase("grief"))
		{
			CCPSmallCommands.setAntiGrief(activeChar);
		}
		// Block Experience
		else if (param[0].equalsIgnoreCase("noe"))
		{
			if (activeChar.getVar("NoExp") == null)
			{
				activeChar.setVar("NoExp", "1", -1);
			}
			else
			{
				activeChar.unsetVar("NoExp");
			}
		}
		// Auto Shoulshots
		else if (param[0].equalsIgnoreCase("soulshot"))
		{
			if (activeChar.getVar("soulshot") == null)
			{
				activeChar.setVar("soulshot", "1", -1);
			}
			else
			{
				activeChar.unsetVar("soulshot");
			}
		}
		// Show Online Players
		else if (param[0].equalsIgnoreCase("online"))
		{
			activeChar.sendMessage(CCPSmallCommands.showOnlineCount());
		}
		else if (param[0].equalsIgnoreCase("changeLog"))
		{
			Quest q = QuestManager.getQuest(QuestManager.TUTORIAL_QUEST_ID);
			if (q != null)
			{
				QuestState st = activeChar.getQuestState(q.getName());
				if (st != null)
				{
					String change = ChangeLogManager.getInstance().getChangeLog(ChangeLogManager.getInstance().getLatestChangeId());
					st.showTutorialHTML(change);
				}
			}
		}
		// Item logs
		else if (param[0].equalsIgnoreCase("itemLogs"))
		{
			CCPItemLogs.showPage(activeChar);
			return null;
		}
		// Show private stores Hide private stores / Fixed
		else if (param[0].equalsIgnoreCase(Player.NO_TRADERS_VAR))
		{
			if (activeChar.getVar(Player.NO_TRADERS_VAR) == null)
			{
				ArrayList<L2GameServerPacket> pls = new ArrayList<>();
				List<Player> list = World.getAroundPlayers(activeChar);
				for (Player player : list)
				{
					if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
					{
						pls.add(new DeleteObject(player));
					}
				}

				list.clear();

				activeChar.sendPacket(pls);
				activeChar.setNotShowTraders(true);
				activeChar.setVar(Player.NO_TRADERS_VAR, "1", -1);
			}
			else
			{
				activeChar.setNotShowTraders(false);
				activeChar.unsetVar(Player.NO_TRADERS_VAR);

				List<Player> list = World.getAroundPlayers(activeChar);
				for (Player player : list)
				{
					if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
					{
						player.broadcastUserInfo(true);
					}
				}
			}
		}
		// Show skill animations
		else if (param[0].equalsIgnoreCase(Player.NO_ANIMATION_OF_CAST_VAR))
		{
			if (activeChar.getVar(Player.NO_ANIMATION_OF_CAST_VAR) == null)
			{
				activeChar.setNotShowBuffAnim(true);
				activeChar.setVar(Player.NO_ANIMATION_OF_CAST_VAR, "true", -1);
			}
			else
			{
				activeChar.setNotShowBuffAnim(false);
				activeChar.unsetVar(Player.NO_ANIMATION_OF_CAST_VAR);
			}
		}
		// Change auto loot
		else if (param[0].equalsIgnoreCase("autoloot"))
		{
			setAutoLoot(activeChar);
		}
		else if (param[0].equalsIgnoreCase("repairCharacter"))
		{
			if (param.length > 1)
			{
				CCPRepair.repairChar(activeChar, param[1]);
			}
			else
			{
				return null;
			}
		}
		else if (param[0].equalsIgnoreCase("offlineStore"))
		{
			boolean result = CCPOffline.setOfflineStore(activeChar);
			if (result)
			{
				return null;
			}
			else
			{
				return "char.htm";
			}
		}
		else if (param[0].startsWith("poll") || param[0].startsWith("Poll"))
		{
			CCPPoll.bypass(activeChar, param);
			return null;
		}
		else if (param[0].equals("combine"))
		{
			CCPSmallCommands.combineTalismans(activeChar);
			return null;
		}
		else if (param[0].equals("otoad"))
		{
			CCPSmallCommands.openToad(activeChar, -1);
			return null;
		}
		else if (param[0].equals("hwidPage"))
		{
			// if (Config.ALLOW_SMARTGUARD)
			// {
			if (activeChar.getHwidLock() != null)
			{
				return "cfgUnlockHwid.htm";
			}
			else
			{
				return "cfgLockHwid.htm";
				// }
			}
		}
		else if (param[0].equals("lockHwid"))
		{
			// if (Config.ALLOW_SMARTGUARD)
			// {
			boolean shouldLock = Boolean.parseBoolean(param[1]);
			if (shouldLock)
			{
				activeChar.setHwidLock(activeChar.getHWID());
				activeChar.sendMessage("Character is now Secured!");
			}
			else
			{
				activeChar.setHwidLock(null);
				activeChar.sendMessage("Character is now Unsecured!");
			}
			// }
		}
		/*
		 * else if (param[0].equalsIgnoreCase("setupPRecover"))
		 * {
		 * CCPPasswordRecover.startPasswordRecover(activeChar);
		 * return null;
		 * }
		 * else if (param[0].startsWith("setupPRecover"))
		 * {
		 * CCPPasswordRecover.setup(activeChar, text);
		 * return null;
		 * }
		 * else if (param[0].startsWith("cfgSPPassword") || param[0].startsWith("cfgSPRecover"))
		 * {
		 * CCPPasswordRecover.reset(activeChar, text);
		 * return null;
		 * }
		 */
		else if (param[0].startsWith("secondaryPass"))
		{
			CCPSecondaryPassword.startSecondaryPasswordSetup(activeChar, text);
			return null;
		}
		else if (param[0].equalsIgnoreCase("showPassword"))
		{
			return "cfgPassword.htm";
		}
		else if (param[0].equals("changePassword"))
		{
			StringTokenizer st = new StringTokenizer(text, " | ");
			String[] passes = new String[st.countTokens() - 1];
			st.nextToken();
			for (int i = 0; i < passes.length; i++)
			{
				passes[i] = st.nextToken();
			}
			boolean newDialog = CCPPassword.setNewPassword(activeChar, passes);
			if (newDialog)
			{
				return null;
			}
			else
			{
				return "cfgPassword.htm";
			}
		}
		else if (param[0].equalsIgnoreCase("showRepair"))
		{
			return "cfgRepair.htm";
		}
		else if (param[0].equalsIgnoreCase("ping"))
		{
			CCPSmallCommands.getPing(activeChar);
			return null;
		}
		else if (param[0].equalsIgnoreCase("cwhPrivs"))
		{
			if (param.length > 1)
			{
				String args = param[1] + (param.length > 2 ? " " + param[2] : "");
				return CCPCWHPrivilages.clanMain(activeChar, args);
			}
			else
			{
				return "cfgClan.htm";
			}
		}
		else if (param[0].equals("delevel"))
		{
			if (param.length > 1 && StringUtils.isNumeric(param[1]))
			{
				boolean success = CCPSmallCommands.decreaseLevel(activeChar, Integer.parseInt(param[1]));
				if (success)
				{
					return null;
				}
			}

			return "cfgDelevel.htm";
		}
		// Synerge - Account recover
		/*
		 * else if (param[0].equalsIgnoreCase("accountRecover"))
		 * {
		 * if (param.length > 1)
		 * {
		 * CCPAccountRecover.recoverAccounts(activeChar, param[1]);
		 * return null;
		 * }
		 * else
		 * {
		 * CCPAccountRecover.sendMainHtml(activeChar);
		 * return null;
		 * }
		 * }
		 */
		// Synerge - Show emotions
		else if (param[0].equalsIgnoreCase(Player.NO_EMOTIONS_VAR))
		{
			if (activeChar.getVar(Player.NO_EMOTIONS_VAR) == null)
			{
				activeChar.setNotShowEmotions(true);
				activeChar.setVar(Player.NO_EMOTIONS_VAR, "true", -1);
			}
			else
			{
				activeChar.setNotShowEmotions(false);
				activeChar.unsetVar(Player.NO_EMOTIONS_VAR);
			}
		}
		// Synerge - Hide olympiad announcements
		else if (param[0].equalsIgnoreCase(Player.NO_OLYMPIAD_ANNOUNCEMENTS_VAR))
		{
			if (activeChar.getVar(Player.NO_OLYMPIAD_ANNOUNCEMENTS_VAR) == null)
			{
				activeChar.setNotShowOlympiadAnnouncements(true);
				activeChar.setVar(Player.NO_OLYMPIAD_ANNOUNCEMENTS_VAR, "true", -1);
			}
			else
			{
				activeChar.setNotShowOlympiadAnnouncements(false);
				activeChar.unsetVar(Player.NO_OLYMPIAD_ANNOUNCEMENTS_VAR);
			}
		}

		return "char.htm";
	}

	public String replacePage(String currentPage, Player activeChar, String additionalText, String bypass)
	{
		currentPage = currentPage.replaceFirst("%online%", CCPSmallCommands.showOnlineCount());
		currentPage = currentPage.replaceFirst("%antigrief%", getEnabledDisabled(activeChar.getVarB("antigrief")));
		currentPage = currentPage.replaceFirst("%noe%", getEnabledDisabled(activeChar.getVarB("NoExp")));
		currentPage = currentPage.replaceFirst("%soulshot%", getEnabledDisabled(activeChar.getVarB("soulshot")));
		currentPage = currentPage.replaceFirst("%notraders%", getEnabledDisabled(activeChar.getVarB("notraders")));
		currentPage = currentPage.replaceFirst("%notShowBuffAnim%", getEnabledDisabled(activeChar.getVarB("notShowBuffAnim")));
		currentPage = currentPage.replaceFirst("%notShowEmotions%", getEnabledDisabled(activeChar.getVarB(Player.NO_EMOTIONS_VAR)));
		currentPage = currentPage.replaceFirst("%notShowOlyAnnounces%", getEnabledDisabled(activeChar.getVarB(Player.NO_OLYMPIAD_ANNOUNCEMENTS_VAR)));
		currentPage = currentPage.replaceFirst("%autoLoot%", getEnabledDisabled(activeChar.isAutoLootEnabled()));
		if (currentPage.contains("%charsOnAccount%"))
		{
			currentPage = currentPage.replaceFirst("%charsOnAccount%", CCPRepair.getCharsOnAccount(activeChar.getName(), activeChar.getAccountName()));
		}

		return currentPage;
	}

	private String getEnabledDisabled(boolean enabled)
	{
		if (enabled)
		{
			return "Enabled";
		}
		else
		{
			return "Disabled";
		}
	}

	public void setAutoLoot(Player player)
	{
		if (Config.AUTO_LOOT_INDIVIDUAL)
		{
			player.setAutoLoot(!player.isAutoLootEnabled());
		}
	}

	public static CharacterControlPanel getInstance()
	{
		if (_instance == null)
		{
			_instance = new CharacterControlPanel();
		}
		return _instance;
	}
}
