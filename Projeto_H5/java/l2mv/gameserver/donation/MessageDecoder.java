package l2mv.gameserver.donation;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDecoder
{
	private static final Logger _log = LoggerFactory.getLogger(MessageDecoder.class);

	public static void decode(Message msg) throws Exception
	{
		final String from = msg.getFrom()[0].toString();

		final String str = getText(msg, new StringBuilder()).trim();
		if (!str.isEmpty() && from.endsWith("<member@paypal.com>"))
		{
			// First we check that the email format is correct
			if (!str.contains("This email confirms that you have received a donation of") || !str.contains("EUR"))
			{
				_log.error("[FATAL] Corrupted donation from:\n->" + from + " (Please check this eMail)");
				return;
			}

			// Then we must get the info for each param in the mail
			final String userMail = str.split("\\)")[0].split("\\(")[1];
			final String PID = str.split("Confirmation number:")[1].trim().substring(0, 17);
			final int amount = (int) Double.parseDouble(str.split("Total amount:")[1].trim().split(" EUR")[0].trim().replace("€", ""));

			DonationReader.getInstance().handleDonation("Paypal", userMail, PID, amount);
		}
	}

	/**
	 * @param part
	 * @param textList
	 * @return Returns the text content of the mail considering all the possible content types, returning a final string
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static String getText(Part part, StringBuilder textList) throws MessagingException, IOException
	{
		if (part.isMimeType("multipart/*"))
		{
			final Multipart mp = (Multipart) part.getContent();
			int count = mp.getCount();

			for (int i = 0; i < count; i++)
			{
				BodyPart bp = mp.getBodyPart(i);

				getText(bp, textList);
			}
		}
		else if (part.isMimeType("text/*"))
		{
			String content = (String) part.getContent();

			textList.append(content + "\n");
		}
		else if (part.isMimeType("message/rfc822"))
		{
			// nested messages need recursion
			getText((Part) part.getContent(), textList);
		}

		return textList.toString();
	}
}
