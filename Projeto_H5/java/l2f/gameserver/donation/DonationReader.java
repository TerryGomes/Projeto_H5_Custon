package l2f.gameserver.donation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.donation.mail.MailReader;
import l2f.gameserver.donation.mail.MailSender;
import l2f.gameserver.donation.mail.ServiceIO;

public class DonationReader
{
	private static final Logger _log = LoggerFactory.getLogger(DonationReader.class);

	private static final int CHECK_DONATIONS_DELAY = 20 * 1000;

	private ServiceIO _serviceIO;

	public DonationReader()
	{
		try
		{
			if (Config.MAIL_USER.isEmpty())
			{
				return;
			}

			_serviceIO = new ServiceIO();
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new MailReader(), 5000, CHECK_DONATIONS_DELAY);

			_log.info("Donation Reader service started");
		}
		catch (Exception e)
		{
			_serviceIO = null;
			_log.error("Donation Reader service could not be started!\n->CAUSE: " + e.getMessage(), e);
		}
	}

	public void handleDonation(String serviceName, String email, String PID, int amount)
	{
		System.out.println("==============================================================================");
		_log.info("Handling [" + serviceName + "] donation from: " + email + ", #" + PID + ", " + amount + " EUR.");

		MailSender.getInstance().sendEmail(email, PID);
		writeDonation(email, PID, amount);
	}

	public void writeDonation(String email, String PID, int amount)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement st = con.prepareStatement("INSERT INTO donations VALUES(?,?,?,?,?,?,?,?)"); PreparedStatement st2 = con.prepareStatement("SELECT * FROM donations WHERE transactionID=?");)
		{
			st2.setString(1, PID);
			try (ResultSet rs = st2.executeQuery();)
			{
				if (!rs.next())
				{
					st.setString(1, PID);
					st.setString(2, email);
					st.setInt(3, amount);
					st.setString(4, "false");
					st.setString(5, "N/A");
					st.setString(6, "N/A");
					st.setString(7, "N/A");
					st.setString(8, "N/A");
					st.executeUpdate();
				}
			}
		}
		catch (Exception e)
		{
			_log.error("WARNING: I could not save " + email + "'s donation data!\n->CAUSE: " + e.getMessage(), e);
		}
	}

	public void reloadServiceIO()
	{
		try
		{
			_serviceIO = new ServiceIO();
			_log.info("Donation Reader IO service reloaded");
		}
		catch (Exception e)
		{
			_serviceIO = null;
			_log.error("Donation Reader service could not be started!\n->CAUSE: " + e.getMessage(), e);
		}
	}

	public ServiceIO getServiceIO()
	{
		return _serviceIO;
	}

	public Store getStore()
	{
		return _serviceIO.getStore();
	}

	public Session getSession()
	{
		return _serviceIO.getSession();
	}

	public static DonationReader getInstance()
	{
		return InstanceHolder._instance;
	}

	protected static class InstanceHolder
	{
		protected static final DonationReader _instance = new DonationReader();
	}
}
