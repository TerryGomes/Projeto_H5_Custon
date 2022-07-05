package l2f.gameserver.handler.voicecommands.impl;

import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.model.Player;

/**
 * Opens the community board for the main page of the facebook system
 *
 * @author Synerge
 */
public class FacebookVoice implements IVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		"fb",
		"facebook"
	};

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		final ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler("_bbsfacebook");
		if (handler != null)
		{
			handler.onBypassCommand(player, "_bbsfacebook_main");
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}