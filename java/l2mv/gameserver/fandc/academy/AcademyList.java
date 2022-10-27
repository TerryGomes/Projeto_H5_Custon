package l2mv.gameserver.fandc.academy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Request;
import l2mv.gameserver.model.Request.L2RequestType;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.SubUnit;
import l2mv.gameserver.model.pledge.UnitMember;
import l2mv.gameserver.network.serverpackets.ExNoticePostArrived;
import l2mv.gameserver.network.serverpackets.JoinPledge;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListAdd;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import l2mv.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import l2mv.gameserver.network.serverpackets.PledgeSkillList;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.tables.ClanTable;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.Util;

/**
 * @author claww
 */
public class AcademyList
{
	private static final Logger _log = LoggerFactory.getLogger(AcademyList.class);
	private static List<Player> _academyList = new ArrayList<Player>();

	public static void addToAcademy(Player player)
	{
		_academyList.add(player);
	}

	public static void deleteFromAcdemyList(Player player)
	{
		_academyList.remove(player);
	}

	public static List<Player> getAcademyList()
	{
		return _academyList;
	}

	public static boolean isInAcademyList(Player player)
	{
		for (Player plr : _academyList)
		{
			if (plr == null)
			{
				continue;
			}
			if (plr.getName().equalsIgnoreCase(player.getName()))
			{
				return true;
			}
		}
		return false;
	}

	public static void inviteInAcademy(Player activeChar, Player academyChar)
	{
		if (activeChar == null)
		{
			academyChar.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
			academyChar.sendActionFailed();
			return;
		}
		final Request request = activeChar.getRequest();
		if (request == null || !request.isTypeOf(L2RequestType.CLAN))
		{
			return;
		}
		if (!request.isInProgress())
		{
			request.cancel();
			academyChar.sendActionFailed();
			return;
		}
		if (activeChar.isOutOfControl())
		{
			request.cancel();
			activeChar.sendActionFailed();
			return;
		}
		final Clan clan = activeChar.getClan();
		if (clan == null)
		{
			request.cancel();
			academyChar.sendActionFailed();
			return;
		}
		if (!academyChar.canJoinClan())
		{
			request.cancel();
			academyChar.sendPacket(SystemMsg.AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN);
			return;
		}
		final int pledgeId = request.getInteger("pledgeType");
		final SubUnit subUnit = clan.getSubUnit(pledgeId);
		if (subUnit == null)
		{
			activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Create a academy in your clan....");
			academyChar.sendChatMessage(academyChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Failed to join in clan because requester clan dont have academy created.");
			return;
		}
		try
		{
			if (academyChar.getPledgePrice() != 0)
			{
				final int itemId = academyChar.getPledgeItemId();
				final long price = academyChar.getPledgePrice();
				if (ItemFunctions.removeItem(activeChar, itemId, price, true, "AcademyList") == 0)
				{
					activeChar.sendChatMessage(activeChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "You dont have enough items to invite " + academyChar.getName() + " in academy!");
					academyChar.sendChatMessage(academyChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "Sorry but " + activeChar.getName() + " does not have enough items to invite you in academy.");
					return;
				}
				// Register academy into sql.
				registerAcademy(activeChar.getClan(), academyChar, itemId, price);
				// Start invation to clan ...
				academyChar.sendPacket(new JoinPledge(activeChar.getClanId()));
				final UnitMember unitMember = new UnitMember(clan, academyChar.getName(), academyChar.getTitle(), academyChar.getLevel(), academyChar.getClassId().getId(), academyChar.getObjectId(), pledgeId, academyChar.getPowerGrade(), academyChar.getApprentice(), academyChar.getSex(), -128);
				subUnit.addUnitMember(unitMember);
				academyChar.setPledgeType(pledgeId);
				academyChar.setClan(clan);
				unitMember.setPlayerInstance(academyChar, false);
				if (pledgeId == -1)
				{
					academyChar.setLvlJoinedAcademy(academyChar.getLevel());
				}
				unitMember.setPowerGrade(clan.getAffiliationRank(academyChar.getPledgeType()));
				clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(unitMember), academyChar);
				clan.broadcastToOnlineMembers(new L2GameServerPacket[]
				{
					new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_CLAN).addString(academyChar.getName()),
					new PledgeShowInfoUpdate(clan)
				});
				academyChar.sendChatMessage(academyChar.getObjectId(), ChatType.BATTLEFIELD.ordinal(), "Academy", "You have accepted to join " + activeChar.getName() + "'s clan. On academy finish you will recive " + Util.formatAdena(academyChar.getPledgePrice()) + " " + ItemHolder.getInstance().getTemplateName(itemId) + ".");
				academyChar.sendPacket(SystemMsg.ENTERED_THE_CLAN);
				academyChar.sendPacket(academyChar.getClan().listAll());
				academyChar.setLeaveClanTime(0);
				academyChar.updatePledgeClass();
				clan.addSkillsQuietly(academyChar);
				academyChar.sendPacket(new PledgeSkillList(clan));
				academyChar.sendPacket(new SkillList(academyChar));
				EventHolder.getInstance().findEvent(academyChar);
				if (clan.getWarDominion() > 0)
				{
					final DominionSiegeEvent dominionEvent = academyChar.getEvent(DominionSiegeEvent.class);
					dominionEvent.updatePlayer(academyChar, true);
				}
				else
				{
					academyChar.broadcastCharInfo();
				}
				academyChar.store(false);
				Log.add("AcademyChar: " + academyChar.getName() + " accepted to join in " + activeChar.getName() + " clan. Payment Item: " + itemId + " Price: " + price + " ", "AcademyService");
			}
		}
		finally
		{
			request.done();
		}
	}

	private static void registerAcademy(Clan clan, Player player, int itemId, long price)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement("INSERT INTO character_academy (clanId,charId,itemId,price,time) values(?,?,?,?,?)");
			statement.setInt(1, clan.getClanId());
			statement.setInt(2, player.getObjectId());
			statement.setInt(3, itemId);
			statement.setLong(4, price);
			statement.setLong(5, System.currentTimeMillis() + Config.MAX_TIME_IN_ACADEMY);
			statement.execute();
			deleteFromAcdemyList(player);
			scheduledDeleteTask(clan);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(connection, statement);
		}
	}

	public static boolean isAcademyChar(int objId)
	{
		String result = "";
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM `character_academy` WHERE `charId` = ?");
			statement.setInt(1, objId);
			rset = statement.executeQuery();
			if (rset.next())
			{
				result = rset.getString("clanId");
			}
		}
		catch (final Exception e)
		{
			_log.error("isAcademyChar: ", e);
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return result != "";
	}

	public static void removeAcademyFromDB(Clan clan, int charId, boolean giveReward, boolean kick)
	{
		Player player = null;
		Connection connection = null;
		PreparedStatement statement = null;
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try
		{
			if (giveReward)
			{
				connection = DatabaseFactory.getInstance().getConnection();
				statement = connection.prepareStatement("SELECT itemId, price FROM character_academy WHERE clanId=? AND charId=?");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, charId);
				rs = statement.executeQuery();
				while (rs.next())
				{
					try
					{
						final int itemId = rs.getInt("itemId");
						final long price = rs.getLong("price");
						// Give rewrad to the academy char.
						final Mail letter = new Mail();
						final String charName = CharacterDAO.getNameByObjectId(charId);
						final ItemInstance item = ItemFunctions.createItem(itemId);
						// Send a mail to the academy character.
						letter.setSenderId(1);
						letter.setSenderName("Academy");
						letter.setReceiverId(charId);
						letter.setReceiverName(charName);
						letter.setTopic("You finished Academy!");
						letter.setBody("Hello " + charName + ",\nYou have completed Academy and bring reputation score to the clan you was joined. \n Thats why you recived a payment of " + Util.formatAdena(price) + " " + item.getName() + " ");
						letter.setType(Mail.SenderType.NONE);
						letter.setUnread(true);
						letter.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
						item.setLocation(ItemInstance.ItemLocation.MAIL);
						item.setCount(price);
						item.save();
						letter.addAttachment(item);
						letter.save();
						player = World.getPlayer(charId);
						if (player != null)
						{
							player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
							player.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
						}
						Log.add("AcademyReward: " + charName + " just finished academy taks for clan " + clan.getName() + " and recived payment of " + price + " " + item.getName(), "AcademyService");
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
				}
				DbUtils.closeQuietly(connection, statement, rs);
			}
			if (kick)
			{
				connection = DatabaseFactory.getInstance().getConnection();
				statement1 = connection.prepareStatement("SELECT itemId, price FROM character_academy WHERE clanId=? AND charId=?");
				statement1.setInt(1, clan.getClanId());
				statement1.setInt(2, charId);
				rs1 = statement1.executeQuery();
				while (rs1.next())
				{
					try
					{
						// Retrun the fee to the clan owner.
						final Mail letter = new Mail();
						final int itemID = rs1.getInt("itemId");
						final long price = rs1.getLong("price");
						final ItemInstance item = ItemFunctions.createItem(itemID);
						final String acadCharName = CharacterDAO.getNameByObjectId(charId);
						// Send a mail to the clan laeder.
						letter.setSenderId(1);
						letter.setSenderName("Academy");
						letter.setReceiverId(clan.getLeaderId());
						letter.setReceiverName(CharacterDAO.getNameByObjectId(clan.getLeaderId()));
						letter.setTopic("Academy recruit Failed!");
						letter.setBody("Hello " + clan.getLeaderName() + ",\nUnfortunately " + acadCharName + " has left the Academy. \nYour invested money are returned back.");
						letter.setType(Mail.SenderType.NONE);
						letter.setUnread(true);
						letter.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
						item.setLocation(ItemInstance.ItemLocation.MAIL);
						item.setCount(price);
						item.save();
						letter.addAttachment(item);
						letter.save();
						player = World.getPlayer(clan.getLeaderId());
						if (player != null)
						{
							player.sendPacket(ExNoticePostArrived.STATIC_TRUE);
							player.sendPacket(SystemMsg.THE_MAIL_HAS_ARRIVED);
						}
						clan.removeClanMember(charId);
						Log.add("AcademyKick: " + acadCharName + " has been kicked from " + clan.getName() + " Refund send to the clan leader.", "AcademyService");
					}
					catch (final Exception e)
					{
						e.printStackTrace();
					}
				}
				DbUtils.closeQuietly(connection, statement1, rs1);
			}
			connection = DatabaseFactory.getInstance().getConnection();
			statement2 = connection.prepareStatement("DELETE FROM character_academy WHERE clanId=? AND charId=?");
			statement2.setInt(1, clan.getClanId());
			statement2.setInt(2, charId);
			statement2.execute();
			Log.add("AcademyDelete: clan " + clan.getName() + " [" + clan.getClanId() + "] and charId " + charId, "AcademyService");
			if (!giveReward && !kick)
			{
				Log.add("AcademyLeaderKick: " + charId + " has been kicked from " + clan.getName() + " by his leader. No payment will be refund.", "AcademyService");
			}
			DbUtils.closeQuietly(connection, statement2);
		}
		catch (final Exception e)
		{
			_log.error("Could not delete char from character_academy:" + e);
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(connection, statement, rs);
		}
	}

	public static void restore()
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement("SELECT clanId FROM character_academy");
			rs = statement.executeQuery();
			while (rs.next())
			{
				try
				{
					final int clanId = rs.getInt("clanId");
					final Clan clan = ClanTable.getInstance().getClan(clanId);
					if (clan != null)
					{
						scheduledDeleteTask(clan);
					}
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (final Exception e)
		{
			_log.error("Could not restore clan for academy:" + e);
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(connection, statement, rs);
		}
	}

	private static void scheduledDeleteTask(final Clan clan)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try
		{
			connection = DatabaseFactory.getInstance().getConnection();
			statement = connection.prepareStatement("SELECT charId,time FROM character_academy WHERE clanId=?");
			statement.setInt(1, clan.getClanId());
			rs = statement.executeQuery();
			while (rs.next())
			{
				try
				{
					final int charId = rs.getInt("charId");
					final long date = rs.getLong("time");
					ThreadPoolManager.getInstance().schedule(new RunnableImpl()
					{
						@Override
						public void runImpl() throws Exception
						{
							if (clan == null)
							{
								_log.error("AcademyList: Clan was null for charID " + charId);
								return;
							}
							final String charName = CharacterDAO.getNameByObjectId(charId);
							final UnitMember member = clan.getAnyMember(charName);
							if (member == null)
							{
								return;
							}
							if (member.getLevel() < 40 && member.getPledgeType() == -1)
							{
								removeAcademyFromDB(clan, charId, false, true);
								final int subUnitType = member.getPledgeType();
								clan.removeClanMember(subUnitType, member.getObjectId());
								clan.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED).addString(charName), new PledgeShowMemberListDelete(charName));
								Log.add("AcademySchedule: " + member.getName() + " has been offline for some time and system kicked him from clan " + clan.getName(), "AcademyService");
								final Player memberPlayer = World.getPlayer(charId);
								if (memberPlayer == null)
								{
									return;
								}
								if (subUnitType == Clan.SUBUNIT_ACADEMY)
								{
									memberPlayer.setLvlJoinedAcademy(0);
								}
								memberPlayer.setClan(null);
								if (!memberPlayer.isNoble())
								{
									memberPlayer.setTitle("");
								}
								memberPlayer.setLeaveClanCurTime();
								memberPlayer.broadcastCharInfo();
								// memberPlayer.broadcastRelationChanged();
								memberPlayer.store(true);
								memberPlayer.sendPacket(SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN, PledgeShowMemberListDeleteAll.STATIC);
							}
						}
					}, date - System.currentTimeMillis());
				}
				catch (final Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (final Exception e)
		{
			_log.error("Could not select bad academy sql query:" + e);
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(connection, statement, rs);
		}
	}
}
