package l2f.gameserver.model.entity.CCPHelpers.itemLogs;

public enum ItemActionType
{
	DROPPED_ON_PURPOSE("Dropped on Purpose", false), DROPPED_BY_KARMA("Dropped by Karma", false), CRYSTALIZED("Crystalized", true), DESTROYED_ON_PURPOSE("Destroyed on Purpose", true), SOLD_TO_NPC("Sold to Npc", true), BOUGHT_IN_STORE("Bought in Store", true), SOLD_IN_STORE("Sold in Store", true), TRADE("Trade", true), MAIL("Mail", true);

	private final String niceName;
	private final boolean isReceiverKnown;

	private ItemActionType(String niceName, boolean isReceiverKnown)
	{
		this.niceName = niceName;
		this.isReceiverKnown = isReceiverKnown;
	}

	public String getNiceName()
	{
		return niceName;
	}

	public boolean isReceiverKnown()
	{
		return isReceiverKnown;
	}
}