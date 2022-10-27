//package services.community;
//
//import l2mv.gameserver.cache.Msg;
//import l2mv.gameserver.data.ClanRequest;
//import l2mv.gameserver.model.GameObjectsStorage;
//import l2mv.gameserver.model.Player;
//import l2mv.gameserver.model.pledge.Clan;
//import l2mv.gameserver.model.pledge.UnitMember;
//import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
//import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
//import l2mv.gameserver.tables.ClanTable;
//import l2mv.gameserver.utils.HtmlUtils;
//
//public class ClanData
//{
//	private static final ClanData _instance = new ClanData();
//
//	public static ClanData getInstance()
//	{
//		return _instance;
//	}
//
//	public boolean checkClanInvite(Player player, int clanId, int obj, int unity)
//	{
//		ClanRequest request = ClanRequest.getClanInvitePlayer(clanId, obj);
//		if(request == null)
//		{
//			player.sendMessage("Error, this player cannot invited to the clan.");
//			return false;
//		}
//		Player requestor = request.getPlayer();
//		Clan rclan = requestor.getClan();
//		if(rclan != null)
//		{
//			ClanRequest.removeClanInvitePlayer(clanId, obj);
//			player.sendMessage("This player cannot invite to the clan becouse he is in othr clan " + rclan.getName() + ".");
//			return false;
//		}
//
//		Clan clan = player.getClan();
//		if(clan != null)
//		{
//			if(clan.getUnitMembersSize(unity) > clan.getSubPledgeLimit(unity))
//			{
//				player.sendMessage("Unity " + clan.getUnitName(unity) + " is full!");
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	public Player restore(int obj)
//	{
//		Player object = GameObjectsStorage.getPlayer(obj);
//		if(object != null)
//			return object;
//		else
//			return null;
//	}
//
//	public void inviteRemove(Player player, String id)
//	{
//		Clan clan = player.getClan();
//
//		if(clan == null)
//			return;
//
//		if((player.getClanPrivileges() & Clan.CP_CL_INVITE_CLAN) != Clan.CP_CL_INVITE_CLAN)
//		{
//			player.sendPacket(Msg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
//			return;
//		}
//
//		int obj = Integer.parseInt(id);
//		for(ClanRequest request : clan.getInviteList())
//		{
//			Player remove = request.getPlayer();
//			int r_obj = remove.getObjectId();
//			if(r_obj == obj)
//			{
//				if(!remove.isOnline())
//				{
//					Player restore = restore(r_obj);
//					if(restore != null)
//						remove = restore;
//				}
//
//				ClanRequest.removeClanInvitePlayer(clan.getClanId(), remove.getObjectId());
//
//				if(remove.isOnline())
//				{
//					remove.sendPacket(new ExShowScreenMessage("Clan '" + clan.getName() + "' rejected your application to join!", 10000, ScreenMessageAlign.TOP_CENTER, true));
//					remove.sendMessage("Clan '" + clan.getName() + "' rejected your application to join!");
//				}
//			}
//		}
//	}
//
//	public boolean checkClanWar(Clan clan, Clan targetClan, Player player, boolean msg)
//	{
//		if(clan == null || targetClan == null)
//		{
//			if(msg)
//				player.sendPacket(Msg.THE_DECLARATION_OF_WAR_CANT_BE_MADE_BECAUSE_THE_CLAN_DOES_NOT_EXIST_OR_ACT_FOR_A_LONG_PERIOD);
//			return false;
//		}
//		else if(!((player.getClanPrivileges() & Clan.CP_CL_CLAN_WAR) == Clan.CP_CL_CLAN_WAR))
//		{
//			if(msg)
//				player.sendActionFailed();
//			return false;
//		}
//		else if(clan.getWarsCount() >= 30)
//		{
//			if(msg)
//				player.sendPacket(Msg.A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CANT_BE_MADE_AT_THE_SAME_TIME);
//			return false;
//		}
//		else if(clan.getLevel() < 3 || clan.getAllSize() < 15)
//		{
//			if(msg)
//				player.sendPacket(Msg.A_CLAN_WAR_CAN_BE_DECLARED_ONLY_IF_THE_CLAN_IS_LEVEL_THREE_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER);
//			return false;
//		}
//		else if(clan.equals(targetClan))
//		{
//			if(msg)
//				player.sendPacket(Msg.FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN);
//			return false;
//		}
//		else if(clan.getAllyId() == targetClan.getAllyId() && clan.getAllyId() != 0)
//		{
//			if(msg)
//				player.sendPacket(Msg.A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE);
//			return false;
//		}
//		else if(targetClan.getLevel() < 3 || targetClan.getAllSize() < 15)
//		{
//			if(msg)
//				player.sendPacket(Msg.A_CLAN_WAR_CAN_BE_DECLARED_ONLY_IF_THE_CLAN_IS_LEVEL_THREE_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER);
//			return false;
//		}
//		else
//			return true;
//	}
//
//	public void checkAndStartWar(Player player, int war)
//	{
//		Clan clan = player.getClan();
//		Clan targetClan = ClanTable.getInstance().getClan(war);
//
//		if(checkClanWar(targetClan, clan, player, true))
//		{
//			if(clan.isAtWarWith(targetClan.getClanId()))
//			{
//				player.sendPacket(Msg.THE_DECLARATION_OF_WAR_HAS_BEEN_ALREADY_MADE_TO_THE_CLAN);
//				return;
//			}
//			else
//				ClanTable.getInstance().startClanWar(player.getClan(), targetClan);
//		}
//	}
//
//	public void checkAndStopWar(Player player, int war)
//	{
//		Clan clan = player.getClan();
//		Clan targetClan = ClanTable.getInstance().getClan(war);
//
//		if(clan == null || targetClan == null)
//		{
//			player.sendPacket(Msg.THE_DECLARATION_OF_WAR_CANT_BE_MADE_BECAUSE_THE_CLAN_DOES_NOT_EXIST_OR_ACT_FOR_A_LONG_PERIOD);
//			return;
//		}
//		else if(!((player.getClanPrivileges() & Clan.CP_CL_CLAN_WAR) == Clan.CP_CL_CLAN_WAR))
//		{
//			player.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
//			return;
//		}
//		else if(!clan.isAtWarWith(targetClan.getClanId()))
//		{
//			player.sendPacket(Msg.YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_TO_S1_CLAN);
//			return;
//		}
//
//		for(UnitMember mbr : clan)
//		{
//			if(mbr.isOnline() && mbr.getPlayer().isInCombat())
//			{
//				player.sendPacket(Msg.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE);
//				return;
//			}
//		}
//
//		ClanTable.getInstance().stopClanWar(clan, targetClan);
//	}
//
//	public boolean haveWars(Clan clan)
//	{
//		for(Clan war : clan.getEnemyClans())
//		{
//			if(war.isAtWarWith(clan.getClanId()))
//				return true;
//		}
//
//		return false;
//	}
//
//	public final String name(int id)
//	{
//		return HtmlUtils.htmlResidenceName(id);
//	}
//
//	public void sendInviteTask(Player player, String clanId, String note, boolean invite)
//	{
//		int id = Integer.parseInt(clanId);
//		Clan clan = ClanTable.getInstance().getClan(id);
//
//		if(player == null || clan == null)
//			return;
//
//		if(invite)
//		{
//			if(!clan.checkInviteList(player.getObjectId()))
//			{
//				clan.getInviteList().add(new ClanRequest(System.currentTimeMillis(), player, id, note));
//				for(UnitMember members : clan.getAllMembers())
//				{
//					Player member = members.getPlayer();
//					if(member != null)
//					{
//						if((member.getClanPrivileges() & Clan.CP_CL_INVITE_CLAN) == Clan.CP_CL_INVITE_CLAN)
//						{
//							member.sendPacket(new ExShowScreenMessage("Received a request to join the clan at player: " + player.getName(), 10000, ScreenMessageAlign.TOP_CENTER, true));
//							member.sendMessage("Received a request to join the clan at player: " + player.getName());
//						}
//					}
//				}
//			}
//			else
//				player.sendMessage("You have already submitted an request to the clan!");
//		}
//		else
//		{
//			if(ClanRequest.removeClanInvitePlayer(clan.getClanId(), player))
//			{
//				Player leader = clan.getLeader().getPlayer();
//				if(leader != null)
//				{
//					leader.sendPacket(new ExShowScreenMessage(player.getName() + " deleted his request to join the clan!", 10000, ScreenMessageAlign.TOP_CENTER, true));
//					leader.sendMessage(player.getName() + " deleted his request to join the clan!");
//				}
//			}
//		}
//	}
//}
