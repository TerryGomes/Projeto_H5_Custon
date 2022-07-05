package l2f.gameserver.model.base;

public enum RestartType
{
	TO_VILLAGE, TO_CLANHALL, TO_CASTLE, TO_FORTRESS, TO_FLAG, FIXED, AGATHION;

	public static final RestartType[] VALUES = values();
}
