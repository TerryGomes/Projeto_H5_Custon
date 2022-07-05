package l2f.gameserver.utils;

public enum Language
{
	ENGLISH("en"), RUSSIAN("ru");

	public static final Language[] VALUES = Language.values();

	private String _shortName;

	Language(String shortName)
	{
		_shortName = shortName;
	}

	public String getShortName()
	{
		return _shortName;
	}
}
