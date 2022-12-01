package l2mv.gameserver.handler.voicecommands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;

/**
 * This class ...
 *
 * @version $Revision: 1.1.4.2 $ $Date: 2005/03/27 15:30:09 $
 */
public interface IVoicedCommandHandler
{
	public static Logger _log = LoggerFactory.getLogger(IVoicedCommandHandler.class);

	/**
	 * this is the worker method that is called when someone uses an admin command.
	 * @param activeChar
	 * @param command
	 * @param target
	 * @return command success
	 */
	public boolean useVoicedCommand(String command, Player activeChar, String target);

	/**
	 * this method is called at initialization to register all the item ids automatically
	 * @return all known itemIds
	 */
	public String[] getVoicedCommandList();
}
