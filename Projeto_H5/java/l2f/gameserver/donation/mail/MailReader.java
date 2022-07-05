package l2f.gameserver.donation.mail;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.FlagTerm;

import l2f.gameserver.donation.DonationReader;
import l2f.gameserver.donation.MessageDecoder;

public class MailReader implements Runnable
{
	private static final FlagTerm flag = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

	@Override
	public void run()
	{
		try
		{
			// If for some reason the service got disconnected or couldnt be loaded, then we try to reconnect again
			if (DonationReader.getInstance().getServiceIO() == null || !DonationReader.getInstance().getStore().isConnected())
			{
				DonationReader.getInstance().reloadServiceIO();
				return;
			}

			final Folder inbox = DonationReader.getInstance().getStore().getFolder("inbox");
			inbox.open(2);
			final Message[] messages = inbox.search(MailReader.flag);
			for (int length = messages.length, i = 0; i < length; ++i)
			{
				final Message message = messages[i];
				new DecodeMessage(message);
			}
			inbox.close(true);
		}
		catch (MessagingException e)
		{
			// Synerge - If we have a connection problem, then we should reload the service io, that it may have been disconnected on time
			DonationReader.getInstance().reloadServiceIO();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static class DecodeMessage
	{
		public DecodeMessage(Message msg)
		{
			try
			{
				msg.setFlag(Flags.Flag.SEEN, true);
				MessageDecoder.decode(msg);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
