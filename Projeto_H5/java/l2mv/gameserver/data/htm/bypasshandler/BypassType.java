package l2mv.gameserver.data.htm.bypasshandler;

import l2mv.commons.annotations.Nullable;
import l2mv.gameserver.Config;
import l2mv.gameserver.instancemanager.BypassManager;

public enum BypassType
{
	COMMUNITY(Config.BBS_HOME_DIR, BypassManager.EncodingType.ENCODED_BBS), NPC("", BypassManager.EncodingType.ENCODED), ADMIN("", null);

	private final String _pathToHTMLs;
	private final BypassManager.EncodingType _encodingType;

	private BypassType(String pathToHTMLs, BypassManager.EncodingType encodingType)
	{
		_pathToHTMLs = pathToHTMLs;
		_encodingType = encodingType;
	}

	public String getPathToHTMLs()
	{
		return _pathToHTMLs;
	}

	public BypassManager.EncodingType getEncodingType()
	{
		return _encodingType;
	}

	@Nullable
	public static BypassType getBypassByEncoding(BypassManager.EncodingType encodingType)
	{
		for (BypassType type : values())
		{
			if (type.getEncodingType() == encodingType)
			{
				return type;
			}
		}
		return null;
	}
}
