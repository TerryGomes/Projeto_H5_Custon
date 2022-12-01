package l2mv.gameserver.handler.admincommands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.handler.admincommands.impl.AdminAdmin;
import l2mv.gameserver.handler.admincommands.impl.AdminAnnouncements;
import l2mv.gameserver.handler.admincommands.impl.AdminAttribute;
import l2mv.gameserver.handler.admincommands.impl.AdminAugmentation;
import l2mv.gameserver.handler.admincommands.impl.AdminBan;
import l2mv.gameserver.handler.admincommands.impl.AdminCamera;
import l2mv.gameserver.handler.admincommands.impl.AdminCancel;
import l2mv.gameserver.handler.admincommands.impl.AdminChangeAccessLevel;
import l2mv.gameserver.handler.admincommands.impl.AdminChat;
import l2mv.gameserver.handler.admincommands.impl.AdminCheckBot;
import l2mv.gameserver.handler.admincommands.impl.AdminClanHall;
import l2mv.gameserver.handler.admincommands.impl.AdminClientSupport;
import l2mv.gameserver.handler.admincommands.impl.AdminCreateItem;
import l2mv.gameserver.handler.admincommands.impl.AdminCursedWeapons;
import l2mv.gameserver.handler.admincommands.impl.AdminDelete;
import l2mv.gameserver.handler.admincommands.impl.AdminDisconnect;
import l2mv.gameserver.handler.admincommands.impl.AdminDoorControl;
import l2mv.gameserver.handler.admincommands.impl.AdminEditChar;
import l2mv.gameserver.handler.admincommands.impl.AdminEffects;
import l2mv.gameserver.handler.admincommands.impl.AdminEnchant;
import l2mv.gameserver.handler.admincommands.impl.AdminEvents;
import l2mv.gameserver.handler.admincommands.impl.AdminFacebook;
import l2mv.gameserver.handler.admincommands.impl.AdminFakePlayers;
import l2mv.gameserver.handler.admincommands.impl.AdminGathering;
import l2mv.gameserver.handler.admincommands.impl.AdminGeodata;
import l2mv.gameserver.handler.admincommands.impl.AdminGiveAll;
import l2mv.gameserver.handler.admincommands.impl.AdminGlobalEvent;
import l2mv.gameserver.handler.admincommands.impl.AdminGlobalPvpEvent;
import l2mv.gameserver.handler.admincommands.impl.AdminGm;
import l2mv.gameserver.handler.admincommands.impl.AdminGmChat;
import l2mv.gameserver.handler.admincommands.impl.AdminGmEvent;
import l2mv.gameserver.handler.admincommands.impl.AdminHeal;
import l2mv.gameserver.handler.admincommands.impl.AdminHellbound;
import l2mv.gameserver.handler.admincommands.impl.AdminHelpPage;
import l2mv.gameserver.handler.admincommands.impl.AdminIP;
import l2mv.gameserver.handler.admincommands.impl.AdminInstance;
import l2mv.gameserver.handler.admincommands.impl.AdminKill;
import l2mv.gameserver.handler.admincommands.impl.AdminLevel;
import l2mv.gameserver.handler.admincommands.impl.AdminMail;
import l2mv.gameserver.handler.admincommands.impl.AdminMammon;
import l2mv.gameserver.handler.admincommands.impl.AdminManor;
import l2mv.gameserver.handler.admincommands.impl.AdminMasterwork;
import l2mv.gameserver.handler.admincommands.impl.AdminMenu;
import l2mv.gameserver.handler.admincommands.impl.AdminMonsterRace;
import l2mv.gameserver.handler.admincommands.impl.AdminNochannel;
import l2mv.gameserver.handler.admincommands.impl.AdminOlympiad;
import l2mv.gameserver.handler.admincommands.impl.AdminPSPoints;
import l2mv.gameserver.handler.admincommands.impl.AdminPetition;
import l2mv.gameserver.handler.admincommands.impl.AdminPhantoms;
import l2mv.gameserver.handler.admincommands.impl.AdminPledge;
import l2mv.gameserver.handler.admincommands.impl.AdminPoll;
import l2mv.gameserver.handler.admincommands.impl.AdminPolymorph;
import l2mv.gameserver.handler.admincommands.impl.AdminPremium;
import l2mv.gameserver.handler.admincommands.impl.AdminQuests;
import l2mv.gameserver.handler.admincommands.impl.AdminReload;
import l2mv.gameserver.handler.admincommands.impl.AdminRepairChar;
import l2mv.gameserver.handler.admincommands.impl.AdminRes;
import l2mv.gameserver.handler.admincommands.impl.AdminRide;
import l2mv.gameserver.handler.admincommands.impl.AdminSS;
import l2mv.gameserver.handler.admincommands.impl.AdminScripts;
import l2mv.gameserver.handler.admincommands.impl.AdminServer;
import l2mv.gameserver.handler.admincommands.impl.AdminShop;
import l2mv.gameserver.handler.admincommands.impl.AdminShutdown;
import l2mv.gameserver.handler.admincommands.impl.AdminSkill;
import l2mv.gameserver.handler.admincommands.impl.AdminSpawn;
import l2mv.gameserver.handler.admincommands.impl.AdminStream;
import l2mv.gameserver.handler.admincommands.impl.AdminTarget;
import l2mv.gameserver.handler.admincommands.impl.AdminTeam;
import l2mv.gameserver.handler.admincommands.impl.AdminTeleport;
import l2mv.gameserver.handler.admincommands.impl.AdminTournament;
import l2mv.gameserver.handler.admincommands.impl.AdminZone;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.utils.Log;

public class AdminCommandHandler extends AbstractHolder
{
	private static final AdminCommandHandler _instance = new AdminCommandHandler();

	public static AdminCommandHandler getInstance()
	{
		return _instance;
	}

	private final Map<String, IAdminCommandHandler> _datatable = new HashMap<String, IAdminCommandHandler>();

	private AdminCommandHandler()
	{
		registerAdminCommandHandler(new AdminAdmin());
		registerAdminCommandHandler(new AdminAnnouncements());
		registerAdminCommandHandler(new AdminAttribute());
		registerAdminCommandHandler(new AdminBan());
		registerAdminCommandHandler(new AdminCamera());
		registerAdminCommandHandler(new AdminCancel());
		registerAdminCommandHandler(new AdminChangeAccessLevel());
		registerAdminCommandHandler(new AdminClanHall());
		registerAdminCommandHandler(new AdminPoll());
		registerAdminCommandHandler(new AdminClientSupport());
		registerAdminCommandHandler(new AdminCreateItem());
		registerAdminCommandHandler(new AdminCursedWeapons());
		registerAdminCommandHandler(new AdminDelete());
		registerAdminCommandHandler(new AdminDisconnect());
		registerAdminCommandHandler(new AdminDoorControl());
		registerAdminCommandHandler(new AdminEditChar());
		registerAdminCommandHandler(new AdminEffects());
		registerAdminCommandHandler(new AdminFakePlayers());
		registerAdminCommandHandler(new AdminEnchant());
		registerAdminCommandHandler(new AdminEvents());
		registerAdminCommandHandler(new AdminGeodata());
		registerAdminCommandHandler(new AdminGiveAll());
		registerAdminCommandHandler(new AdminGlobalEvent());
		registerAdminCommandHandler(new AdminGm());
		registerAdminCommandHandler(new AdminGmChat());
		registerAdminCommandHandler(new AdminHeal());
		registerAdminCommandHandler(new AdminHellbound());
		registerAdminCommandHandler(new AdminHelpPage());
		registerAdminCommandHandler(new AdminInstance());
		registerAdminCommandHandler(new AdminIP());
		registerAdminCommandHandler(new AdminLevel());
		registerAdminCommandHandler(new AdminMammon());
		registerAdminCommandHandler(new AdminManor());
		registerAdminCommandHandler(new AdminMenu());
		registerAdminCommandHandler(new AdminMonsterRace());
		registerAdminCommandHandler(new AdminNochannel());
		registerAdminCommandHandler(new AdminOlympiad());
		registerAdminCommandHandler(new AdminPetition());
		registerAdminCommandHandler(new AdminPledge());
		registerAdminCommandHandler(new AdminPolymorph());
		registerAdminCommandHandler(new AdminPSPoints());
		registerAdminCommandHandler(new AdminQuests());
		registerAdminCommandHandler(new AdminReload());
		registerAdminCommandHandler(new AdminRepairChar());
		registerAdminCommandHandler(new AdminRes());
		registerAdminCommandHandler(new AdminRide());
		registerAdminCommandHandler(new AdminServer());
		registerAdminCommandHandler(new AdminShop());
		registerAdminCommandHandler(new AdminShutdown());
		registerAdminCommandHandler(new AdminSkill());
		registerAdminCommandHandler(new AdminScripts());
		registerAdminCommandHandler(new AdminSpawn());
		registerAdminCommandHandler(new AdminSS());
		registerAdminCommandHandler(new AdminTarget());
		registerAdminCommandHandler(new AdminTeleport());
		registerAdminCommandHandler(new AdminTeam());
		registerAdminCommandHandler(new AdminZone());
		registerAdminCommandHandler(new AdminKill());
		registerAdminCommandHandler(new AdminMail());
		registerAdminCommandHandler(new AdminMasterwork());

		// Synerge
		registerAdminCommandHandler(new AdminAugmentation());
		registerAdminCommandHandler(new AdminGmEvent());
		registerAdminCommandHandler(new AdminPremium());
		registerAdminCommandHandler(new AdminTournament());
		registerAdminCommandHandler(new AdminFacebook());
		registerAdminCommandHandler(new AdminStream());
		registerAdminCommandHandler(new AdminChat());
		registerAdminCommandHandler(new AdminGlobalPvpEvent());
		registerAdminCommandHandler(new AdminGathering());
		registerAdminCommandHandler(new AdminPhantoms());
		registerAdminCommandHandler(new AdminCheckBot());
	}

	public void registerAdminCommandHandler(IAdminCommandHandler handler)
	{
		for (Enum<?> e : handler.getAdminCommandEnum())
		{
			_datatable.put(e.toString().toLowerCase(), handler);
		}
	}

	public IAdminCommandHandler getAdminCommandHandler(String adminCommand)
	{
		String command = adminCommand;
		if (adminCommand.indexOf(" ") != -1)
		{
			command = adminCommand.substring(0, adminCommand.indexOf(" "));
		}
		return _datatable.get(command);
	}

	public void useAdminCommandHandler(Player activeChar, String adminCommand)
	{
		if (!(activeChar.isGM() || activeChar.getPlayerAccess().CanUseGMCommand))
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.SendBypassBuildCmd.NoCommandOrAccess", activeChar).addString(adminCommand));
			return;
		}

		String[] wordList = adminCommand.split(" ");

		// Synerge - Support to allow only the commands listed here
		if (!activeChar.getPlayerAccess().AllowedCommands.isEmpty() && !activeChar.getPlayerAccess().AllowedCommands.contains(wordList[0]))
		{
			activeChar.sendMessage(new CustomMessage("l2mv.gameserver.clientpackets.SendBypassBuildCmd.NoCommandOrAccess", activeChar).addString(adminCommand));
			return;
		}

		IAdminCommandHandler handler = _datatable.get(wordList[0]);
		if (handler != null)
		{
			boolean success = false;
			try
			{
				for (Enum<?> e : handler.getAdminCommandEnum())
				{
					if (e.toString().equalsIgnoreCase(wordList[0]))
					{
						success = handler.useAdminCommand(e, wordList, adminCommand, activeChar);
						break;
					}
				}
			}
			catch (RuntimeException e)
			{
				error("Error while using Admin Command! ", e);
			}

			Log.LogCommand(activeChar, activeChar.getTarget(), adminCommand, success);
		}
	}

	@Override
	public void process()
	{

	}

	@Override
	public int size()
	{
		return _datatable.size();
	}

	@Override
	public void clear()
	{
		_datatable.clear();
	}

	/**
	 * Получение списка зарегистрированных админ команд
	 * @return список команд
	 */
	public Set<String> getAllCommands()
	{
		return _datatable.keySet();
	}
}