//package l2f.gameserver.data;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.List;
//
//import l2f.gameserver.model.Player;
//import l2f.gameserver.model.pledge.Clan;
//import l2f.gameserver.tables.ClanTable;
//
//public class ClanRequest
//{
//	private static List<Clan> clanList = new ArrayList<Clan>();
//	private static List<ClanRequest> _inviteList = new ArrayList<ClanRequest>();
//
//	private long time;
//	private Player player;
//	private int clanId;
//	private String note;
//
//	public ClanRequest(long time, Player player, int clanId, String note)
//	{
//		this.time = time;
//		this.player = player;
//		this.clanId = clanId;
//		this.note = note;
//		_inviteList.add(this);
//	}
//
//	public long getTime()
//	{
//		return time;
//	}
//
//	public Player getPlayer()
//	{
//		return player;
//	}
//
//	public int getClanId()
//	{
//		return clanId;
//	}
//
//	public String getNote()
//	{
//		return note;
//	}
//
//	public static List<ClanRequest> getInviteList(int clanId)
//	{
//		List<ClanRequest> _invite = new ArrayList<ClanRequest>();
//
//		for(ClanRequest request : _inviteList)
//		{
//			if (request.getClanId() == clanId)
//				_invite.add(request);
//		}
//
//		return _invite;
//	}
//
//	public static ClanRequest getClanInvitePlayer(int clanId, int obj)
//	{
//		for(ClanRequest request : _inviteList)
//		{
//			if (request.getClanId() == clanId && request.getPlayer().getObjectId() == obj)
//				return request;
//		}
//
//		return null;
//	}
//
//	public static void removeClanInvitePlayer(int clanId, int obj)
//	{
//		for(ClanRequest request : _inviteList)
//		{
//			if (request.getClanId() == clanId && request.getPlayer().getObjectId() == obj)
//			{
//				_inviteList.remove(request);
//				break;
//			}
//		}
//	}
//
//	public static boolean removeClanInvitePlayer(int clanId, Player player)
//	{
//		for(ClanRequest request : _inviteList)
//		{
//			if (request.getClanId() == clanId && request.getPlayer() == player)
//			{
//				int time = (int) (((request.getTime() + 60000) - System.currentTimeMillis()) / 1000);
//				if (time <= 0)
//				{
//					_inviteList.remove(request);
//					return true;
//				}
//				else
//				{
//					player.sendMessage("You can remove request only affter " + time + " seconds!");
//					return false;
//				}
//			}
//		}
//		return false;
//	}
//
//	public static void updateList()
//	{
//		Clan[] clans = ClanTable.getInstance().getClans();
//		Arrays.sort(clans, new Comparator<Clan>(){
//			@Override
//			public int compare(Clan o1, Clan o2)
//			{
//				return o2.getLevel() - o1.getLevel();
//			}
//		});
//
//		clanList.clear();
//		for(Clan clan : clans)
//		{
//			if (clan.getLevel() > 0)
//				clanList.add(clan);
//		}
//	}
//
//	public static List<Clan> getClanList()
//	{
//		return clanList;
//	}
//}
