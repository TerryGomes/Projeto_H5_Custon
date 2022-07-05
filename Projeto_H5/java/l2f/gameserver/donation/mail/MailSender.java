package l2f.gameserver.donation.mail;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.donation.DonationReader;

public class MailSender
{
	private static final Logger _log = LoggerFactory.getLogger(MailSender.class);

	public void sendEmail(String emailTo, String TransactionID)
	{
		try
		{
			final Message message = new MimeMessage(DonationReader.getInstance().getSession());
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
			message.setSubject(Config.MAIL_SUBJECT);
			message.setText(Config.MAIL_MESSAGE.replace("%PIN%", TransactionID));
			// Transport.send(message, Config.MAIL_USER, Config.MAIL_PASS);

			_log.info("Donation feedback succesfully sent to " + emailTo);
			System.out.println("==============================================================================");
		}
		catch (Exception e)
		{
			_log.error("WARNING: Could not send donation feedback to " + emailTo + "\n->CAUSE: " + e.getMessage(), e);
		}
	}

	public static MailSender getInstance()
	{
		return InstanceHolder._instance;
	}

	protected static class InstanceHolder
	{
		protected static final MailSender _instance = new MailSender();
	}
}
