package l2f.gameserver.handler.bypass;

import java.util.HashMap;
import java.util.Map;

import l2f.gameserver.data.htm.bypasshandler.BypassType;
import l2f.gameserver.instancemanager.BypassManager;
import l2f.gameserver.instancemanager.BypassManager.DecodedBypass;
import l2f.gameserver.model.Player;

/**
 * Bypass handler
 *
 * @author Synerge
 */
public class BypassHandler
{
	private static final BypassHandler _instance = new BypassHandler();

	private final Map<String, IBypassHandler> _datatable = new HashMap<>();

	public static BypassHandler getInstance()
	{
		return _instance;
	}

	public void registerBypass(IBypassHandler handler)
	{
		for (String bypass : handler.getBypasses())
		{
			_datatable.put(bypass, handler);
		}
	}

	public boolean useBypassCommandHandler(Player player, String bypass)
	{
		String[] wordList = bypass.split(" ");
		IBypassHandler handler = _datatable.get(wordList[0]);
		if (handler != null)
		{
			handler.onBypassFeedback(null, player, bypass);
			return true;
		}
		return false;
	}

	public DecodedBypass tryDecodeSimpleDirect(String bypass)
	{
		final String[] wordList = bypass.split(" ");
		final IBypassHandler handler = _datatable.get(wordList[0]);
		if (handler != null)
		{
			return new BypassManager.DecodedBypass(handler.getBypasses()[0], BypassType.NPC);
		}
		return null;
	}
}
