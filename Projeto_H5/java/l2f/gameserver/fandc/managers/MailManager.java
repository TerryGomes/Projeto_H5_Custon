/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.fandc.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2f.gameserver.Config;
import l2f.gameserver.dao.MailDAO;
import l2f.gameserver.model.Player;

/**
 * Manager para mas que todo controlar los mails que se envian, los reusos, penalties, etc
 *
 * @author Synerge
 */
public class MailManager
{
	private static final int MAIL_SEND_REUSE = 1 * 60 * 1000; // 1 minutos
	private static final int MAX_MAILS_COUNT = 20;

	private final Map<Integer, List<Long>> _mailsSent = new ConcurrentHashMap<>();

	protected MailManager()
	{
	}

	/**
	 * Adds a new mail sent to the selected player
	 *
	 * @param player
	 */
	public void addNewMailSent(Player player)
	{
		// Los gms no poseen penalties
		if (player.getAccessLevel() > 0)
		{
			return;
		}

		if (!_mailsSent.containsKey(player.getObjectId()))
		{
			_mailsSent.put(player.getObjectId(), new ArrayList<Long>());
		}

		// Penalty normal de 2 minutos para enviar otro mail
		_mailsSent.get(player.getObjectId()).add(System.currentTimeMillis() + MAIL_SEND_REUSE);
	}

	/**
	 * @param player
	 * @return Returns true if the player can send the mail or is under penalty
	 */
	public boolean canPlayerSendMail(Player player)
	{
		// Los gms no poseen penalties
		if (player.getAccessLevel() > 0)
		{
			return true;
		}

		// Players only 80+ can send mails
		if (player.getLevel() < Config.ALT_MAIL_MIN_LVL)
		{
			return false;
		}

		// Putting a limit to the ammount of mails that the player can have on the sent box at the same time. If the limit is reached, he must deleted some before sending another
		if (MailDAO.getInstance().getSentMailByOwnerId(player.getObjectId()).size() >= MAX_MAILS_COUNT)
		{
			player.sendMessage("You can only send " + MAX_MAILS_COUNT + " mails. If you want to send more please delete some mails from your sent box");
			return false;
		}

		if (!_mailsSent.containsKey(player.getObjectId()))
		{
			return true;
		}

		final List<Long> mails = _mailsSent.get(player.getObjectId());
		if (mails.isEmpty())
		{
			return true;
		}

		final long maxTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000; // 24 Hours limit
		final Iterator<Long> it = mails.iterator();
		while (it.hasNext())
		{
			// If the mail is older than 24 hours, remove it from the list as it already expired for the penalty
			if (it.next() < maxTime)
			{
				it.remove();
			}
		}

		if (mails.isEmpty())
		{
			return true;
		}

		if (mails.size() >= MAX_MAILS_COUNT)
		{
			player.sendMessage("You have the maximum allowed mails (" + MAX_MAILS_COUNT + "). Please wait for some time to be able to send more.");
			return false;
		}

		/*
		 * if (System.currentTimeMillis() > mails.get(mails.size() - 1))
		 * {
		 * player.sendMessage("You must wait " + (MAIL_SEND_REUSE / 60000) + " minute/s between each mail sent");
		 * return false;
		 * }
		 */

		return true;
	}

	public static MailManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final MailManager _instance = new MailManager();
	}
}
