package l2f.gameserver.instancemanager.games;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

/**
 * User: Keiichi
 * Date: 24.11.2008
 * Time: 23:32:22
 * Порт с SF.
 */
public class LotteryManager
{
	public static final long SECOND = 1000;
	public static final long MINUTE = 60000;

	private static LotteryManager _instance;
	private static final Logger _log = LoggerFactory.getLogger(LotteryManager.class);

	private static final String INSERT_LOTTERY = "INSERT INTO games(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_PRICE = "UPDATE games SET prize=?, newprize=? WHERE id = 1 AND idnr = ?";
	private static final String UPDATE_LOTTERY = "UPDATE games SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?";
	private static final String SELECT_LAST_LOTTERY = "SELECT idnr, prize, newprize, enddate, finished FROM games WHERE id = 1 ORDER BY idnr DESC LIMIT 1";
	private static final String SELECT_LOTTERY_ITEM = "SELECT enchant_level, custom_type2 FROM items WHERE item_id = 4442 AND custom_type1 = ?";
	private static final String SELECT_LOTTERY_TICKET = "SELECT number1, number2, prize1, prize2, prize3 FROM games WHERE id = 1 AND idnr = ?";

	protected int _number;
	protected int _prize;
	protected boolean _isSellingTickets;
	protected boolean _isStarted;
	protected long _enddate;

	public LotteryManager()
	{
		_number = 1;
		_prize = Config.SERVICES_LOTTERY_PRIZE;
		_isSellingTickets = false;
		_isStarted = false;
		_enddate = System.currentTimeMillis();

		if (Config.SERVICES_ALLOW_LOTTERY)
		{
			new startLottery().run();
		}
	}

	private class startLottery extends RunnableImpl
	{
		protected startLottery()
		{
			// Do nothing
		}

		@Override
		public void runImpl() throws Exception
		{
			if (restoreLotteryData())
			{
				announceLottery();
				scheduleEndOfLottery();
				createNewLottery();
			}
		}
	}

	public void increasePrize(int count)
	{
		_prize += count;
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE_PRICE);
			statement.setInt(1, getPrize());
			statement.setInt(2, getPrize());
			statement.setInt(3, getId());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("Lottery: Could not increase current lottery prize: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private boolean restoreLotteryData()
	{
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_LAST_LOTTERY);
			rset = statement.executeQuery();

			if (rset.next())
			{
				_number = rset.getInt("idnr");

				if (rset.getInt("finished") == 1)
				{
					_number++;
					_prize = rset.getInt("newprize");
				}
				else
				{
					_prize = rset.getInt("prize");
					_enddate = rset.getLong("enddate");

					if (_enddate <= System.currentTimeMillis() + 2 * MINUTE)
					{
						new finishLottery().run();
						return false;
					}

					if (_enddate > System.currentTimeMillis())
					{

						_isStarted = true;
						ThreadPoolManager.getInstance().schedule(new finishLottery(), _enddate - System.currentTimeMillis());

						if (_enddate > System.currentTimeMillis() + 12 * MINUTE)
						{
							_isSellingTickets = true;
							ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
						}

						return false;
					}
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("Lottery: Could not restore lottery data: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return true;
	}

	private void announceLottery()
	{
		if (Config.SERVICES_ALLOW_LOTTERY)
		{
			_log.info("Lottery: Starting ticket sell for lottery #" + getId() + ".");
		}
		_isSellingTickets = true;
		_isStarted = true;

		Announcements.getInstance().announceToAll("Lottery tickets are now available for Lucky Lottery #" + getId() + ".");
	}

	private void scheduleEndOfLottery()
	{
		// Connection con = null;
		// PreparedStatement statement;
		/** Calendar finishtime = Calendar.getInstance();
		 finishtime.setTimeInMillis(_enddate);
		 finishtime.set(Calendar.MINUTE, 0);
		 finishtime.set(Calendar.SECOND, 0);
		 finishtime.add(Calendar.DAY_OF_MONTH, 7);
		 finishtime.set(Calendar.DAY_OF_WEEK, 6);
		 finishtime.set(Calendar.HOUR_OF_DAY, 7);
		 _enddate = finishtime.getTimeInMillis();
		
		 ThreadPoolManager.getInstance().scheduleGeneral(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
		 ThreadPoolManager.getInstance().scheduleGeneral(new finishLottery(), _enddate - System.currentTimeMillis());
		 **/

		Calendar finishtime = Calendar.getInstance();
		finishtime.setTimeInMillis(_enddate);
		finishtime.set(Calendar.MINUTE, 0);
		finishtime.set(Calendar.SECOND, 0);

		if (finishtime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
		{
			finishtime.set(Calendar.HOUR_OF_DAY, 19);
			_enddate = finishtime.getTimeInMillis();
			_enddate += 604800000;
		}
		else
		{
			finishtime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			finishtime.set(Calendar.HOUR_OF_DAY, 19);
			_enddate = finishtime.getTimeInMillis();
		}

		ThreadPoolManager.getInstance().schedule(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
		ThreadPoolManager.getInstance().schedule(new finishLottery(), _enddate - System.currentTimeMillis());
	}

	private void createNewLottery()
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(INSERT_LOTTERY);
			statement.setInt(1, 1);
			statement.setInt(2, getId());
			statement.setLong(3, getEndDate());
			statement.setInt(4, getPrize());
			statement.setInt(5, getPrize());
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warn("Lottery: Could not store new lottery data: ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	private class stopSellingTickets extends RunnableImpl
	{
		protected stopSellingTickets()
		{
			// Do nothing
		}

		@Override
		public void runImpl() throws Exception
		{
			if (Config.SERVICES_ALLOW_LOTTERY)
			{
				_log.info("Lottery: Stopping ticket sell for lottery #" + getId() + ".");
			}
			_isSellingTickets = false;

			Announcements.getInstance().announceToAll(new SystemMessage(SystemMessage.LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED));
		}
	}

	private class finishLottery extends RunnableImpl
	{
		protected finishLottery()
		{
			// Do nothing
		}

		@Override
		public void runImpl() throws Exception
		{
			if (Config.SERVICES_ALLOW_LOTTERY)
			{
				_log.info("Lottery: Ending lottery #" + getId() + ".");
			}

			int[] luckynums = new int[5];
			int luckynum = 0;

			for (int i = 0; i < 5; i++)
			{
				boolean found = true;

				while (found)
				{
					luckynum = Rnd.get(20) + 1;
					found = false;

					for (int j = 0; j < i; j++)
					{
						if (luckynums[j] == luckynum)
						{
							found = true;
						}
					}
				}

				luckynums[i] = luckynum;
			}

			if (Config.SERVICES_ALLOW_LOTTERY)
			{
				_log.info("Lottery: The lucky numbers are " + luckynums[0] + ", " + luckynums[1] + ", " + luckynums[2] + ", " + luckynums[3] + ", " + luckynums[4] + ".");
			}

			int enchant = 0;
			int type2 = 0;

			for (int i = 0; i < 5; i++)
			{
				if (luckynums[i] < 17)
				{
					enchant += Math.pow(2, luckynums[i] - 1);
				}
				else
				{
					type2 += Math.pow(2, luckynums[i] - 17);
				}
			}

			if (Config.SERVICES_ALLOW_LOTTERY)
			{
				_log.info("Lottery: Encoded lucky numbers are " + enchant + ", " + type2);
			}

			int count1 = 0;
			int count2 = 0;
			int count3 = 0;
			int count4 = 0;

			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(SELECT_LOTTERY_ITEM);
				statement.setInt(1, getId());
				rset = statement.executeQuery();

				while (rset.next())
				{
					int curenchant = rset.getInt("enchant_level") & enchant;
					int curtype2 = rset.getInt("custom_type2") & type2;

					if (curenchant == 0 && curtype2 == 0)
					{
						continue;
					}

					int count = 0;

					for (int i = 1; i <= 16; i++)
					{
						int val = curenchant / 2;

						if (val != (double) curenchant / 2)
						{
							count++;
						}

						int val2 = curtype2 / 2;

						if (val2 != (double) curtype2 / 2)
						{
							count++;
						}

						curenchant = val;
						curtype2 = val2;
					}

					switch (count)
					{
					case 5:
						count1++;
						break;
					case 4:
						count2++;
						break;
					case 3:
						count3++;
						break;
					default:
						if (count > 0)
						{
							count4++;
						}
						break;
					}
				}
			}
			catch (SQLException e)
			{
				_log.warn("Lottery: Could restore lottery data: ", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement, rset);
			}

			int prize4 = count4 * Config.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
			int prize1 = 0;
			int prize2 = 0;
			int prize3 = 0;
			int newprize;

			if (count1 > 0)
			{
				prize1 = (int) ((getPrize() - prize4) * Config.SERVICES_LOTTERY_5_NUMBER_RATE / count1);
			}

			if (count2 > 0)
			{
				prize2 = (int) ((getPrize() - prize4) * Config.SERVICES_LOTTERY_4_NUMBER_RATE / count2);
			}

			if (count3 > 0)
			{
				prize3 = (int) ((getPrize() - prize4) * Config.SERVICES_LOTTERY_3_NUMBER_RATE / count3);
			}

			// TODO: Уточнить что происходит с джекпотом на оффе. Если с проигрышем всех участников джекпот уменьшается то до каких приделов.
			if (prize1 == 0 && prize2 == 0 && prize3 == 0)
			{
				newprize = getPrize();
			}
			else
			{
				newprize = getPrize() + prize1 + prize2 + prize3;
			}

			if (Config.SERVICES_ALLOW_LOTTERY)
			{
				_log.info("Lottery: Jackpot for next lottery is " + newprize + ".");
			}

			SystemMessage sm;
			if (count1 > 0)
			{
				// There are winners.
				sm = new SystemMessage(SystemMsg.THE_PRIZE_AMOUNT_FOR_THE_WINNER_OF_LOTTERY__S1__IS_S2_ADENA_WE_HAVE_S3_FIRST_PRIZE_WINNERS);
				sm.addNumber(getId());
				sm.addNumber(getPrize());
				sm.addNumber(count1);
				Announcements.getInstance().announceToAll(sm);
			}
			else
			{
				// There are no winners.
				sm = new SystemMessage(SystemMsg.THE_PRIZE_AMOUNT_FOR_LUCKY_LOTTERY__S1__IS_S2_ADENA_THERE_WAS_NO_FIRST_PRIZE_WINNER_IN_THIS_DRAWING_THEREFORE_THE_JACKPOT_WILL_BE_ADDED_TO_THE_NEXT_DRAWING);
				sm.addNumber(getId());
				sm.addNumber(getPrize());
				Announcements.getInstance().announceToAll(sm);
			}

			try
			{

				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(UPDATE_LOTTERY);
				statement.setInt(1, getPrize());
				statement.setInt(2, newprize);
				statement.setInt(3, enchant);
				statement.setInt(4, type2);
				statement.setInt(5, prize1);
				statement.setInt(6, prize2);
				statement.setInt(7, prize3);
				statement.setInt(8, getId());
				statement.execute();
			}
			catch (SQLException e)
			{
				_log.warn("Lottery: Could not store finished lottery data: ", e);
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}

			ThreadPoolManager.getInstance().schedule(new startLottery(), 1 * MINUTE);
			_number++;

			_isStarted = false;
		}
	}

	public int[] decodeNumbers(int enchant, int type2)
	{
		int res[] = new int[5];
		int id = 0;
		int nr = 1;

		while (enchant > 0)
		{
			int val = enchant / 2;
			if (val != (double) enchant / 2)
			{
				res[id++] = nr;
			}
			enchant /= 2;
			nr++;
		}

		nr = 17;

		while (type2 > 0)
		{
			int val = type2 / 2;
			if (val != (double) type2 / 2)
			{
				res[id++] = nr;
			}
			type2 /= 2;
			nr++;
		}

		return res;
	}

	public int[] checkTicket(int id, int enchant, int type2)
	{
		int res[] =
		{
			0,
			0
		};

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(SELECT_LOTTERY_TICKET);
			statement.setInt(1, id);
			rset = statement.executeQuery();

			if (rset.next())
			{
				int curenchant = rset.getInt("number1") & enchant;
				int curtype2 = rset.getInt("number2") & type2;

				if (curenchant == 0 && curtype2 == 0)
				{
					return res;
				}

				int count = 0;

				for (int i = 1; i <= 16; i++)
				{
					int val = curenchant / 2;
					if (val != (double) curenchant / 2)
					{
						count++;
					}
					int val2 = curtype2 / 2;
					if (val2 != (double) curtype2 / 2)
					{
						count++;
					}
					curenchant = val;
					curtype2 = val2;
				}

				switch (count)
				{
				case 0:
					break;
				case 5:
					res[0] = 1;
					res[1] = rset.getInt("prize1");
					break;
				case 4:
					res[0] = 2;
					res[1] = rset.getInt("prize2");
					break;
				case 3:
					res[0] = 3;
					res[1] = rset.getInt("prize3");
					break;
				default:
					res[0] = 4;
					res[1] = 200;
				}
			}
		}
		catch (SQLException e)
		{
			_log.warn("Lottery: Could not check lottery ticket #" + id + ": ", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return res;
	}

	public int[] checkTicket(ItemInstance item)
	{
		return checkTicket(item.getCustomType1(), item.getEnchantLevel(), item.getCustomType2());
	}

	public boolean isSellableTickets()
	{
		return _isSellingTickets;
	}

	public boolean isStarted()
	{
		return _isStarted;
	}

	public static LotteryManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new LotteryManager();
		}

		return _instance;
	}

	public int getId()
	{
		return _number;
	}

	public int getPrize()
	{
		return _prize;
	}

	public long getEndDate()
	{
		return _enddate;
	}
}