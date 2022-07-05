package l2f.gameserver.model.entity.CCPHelpers.itemLogs;

public class ItemActionLog
{
	private final int actionId;
	private final int playerObjectId;
	private final ItemActionType actionType;
	private final long time;
	private SingleItemLog[] itemsLost;
	private SingleItemLog[] itemsReceived;
	private final boolean isSavedInDatabase;

	public ItemActionLog(int actionId, int playerObjectId, ItemActionType actionType, long time, SingleItemLog[] itemsLost, SingleItemLog[] itemsReceived)
	{
		this(actionId, playerObjectId, actionType, time, itemsLost, itemsReceived, false);
	}

	public ItemActionLog(int actionId, int playerObjectId, ItemActionType actionType, long time, SingleItemLog[] itemsLost, SingleItemLog[] itemsReceived, boolean isSavedInDatabase)
	{
		this.actionId = actionId;
		this.playerObjectId = playerObjectId;
		this.actionType = actionType;
		this.time = time;
		this.itemsLost = itemsLost;
		this.itemsReceived = itemsReceived;
		this.isSavedInDatabase = isSavedInDatabase;
	}

	public int getActionId()
	{
		return actionId;
	}

	public int getPlayerObjectId()
	{
		return playerObjectId;
	}

	public ItemActionType getActionType()
	{
		return actionType;
	}

	public long getTime()
	{
		return time;
	}

	public SingleItemLog[] getItemsLost()
	{
		return itemsLost;
	}

	public SingleItemLog[] getItemsReceived()
	{
		return itemsReceived;
	}

	public void addItemLog(SingleItemLog item, boolean lost)
	{
		final SingleItemLog[] oldArray = lost ? itemsLost : itemsReceived;
		final SingleItemLog[] newArray = new SingleItemLog[oldArray.length + 1];
		for (int i = 0; i < oldArray.length; ++i)
		{
			newArray[i] = oldArray[i];
		}
		newArray[newArray.length - 1] = item;
		if (lost)
		{
			itemsLost = newArray;
		}
		else
		{
			itemsReceived = newArray;
		}
	}

	public boolean isSavedInDatabase()
	{
		return isSavedInDatabase;
	}
}