package l2mv.gameserver.handler.admincommands;

import l2mv.gameserver.model.Player;

public interface IAdminCommandHandler
{
	/**
	 * this is the worker method that is called when someone uses an admin command.
	 * @param fullString TODO
	 * @param activeChar
	 * @param command
	 * @return command success
	 */
	@SuppressWarnings("rawtypes")
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar);

	/**
	 * this method is called at initialization to register all the item ids automatically
	 * @return all known itemIds
	 */
	@SuppressWarnings("rawtypes")
	public Enum[] getAdminCommandEnum();
}