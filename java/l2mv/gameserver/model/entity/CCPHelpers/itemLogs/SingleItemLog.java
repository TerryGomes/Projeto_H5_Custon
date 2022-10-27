package l2mv.gameserver.model.entity.CCPHelpers.itemLogs;

public class SingleItemLog
{
	private final int itemTemplateId;
	private final long itemCount;
	private final int itemEnchantLevel;
	private final int itemObjectId;
	private String receiverName;
	private boolean nameChanged;

	public SingleItemLog(int itemTemplateId, long itemCount, int itemEnchantLevel, int itemObjectId)
	{
		this(itemTemplateId, itemCount, itemEnchantLevel, itemObjectId, "");
	}

	public SingleItemLog(int itemTemplateId, long itemCount, int itemEnchantLevel, int itemObjectId, String receiverName)
	{
		this.itemTemplateId = itemTemplateId;
		this.itemCount = itemCount;
		this.itemEnchantLevel = itemEnchantLevel;
		this.itemObjectId = itemObjectId;
		this.receiverName = receiverName;
		nameChanged = false;
	}

	public int getItemTemplateId()
	{
		return itemTemplateId;
	}

	public long getItemCount()
	{
		return itemCount;
	}

	public int getItemEnchantLevel()
	{
		return itemEnchantLevel;
	}

	public int getItemObjectId()
	{
		return itemObjectId;
	}

	public void setReceiverName(String receiverName)
	{
		this.receiverName = receiverName;
		nameChanged = true;
	}

	public String getReceiverName()
	{
		return receiverName;
	}

	public boolean didNameChange()
	{
		return nameChanged;
	}
}