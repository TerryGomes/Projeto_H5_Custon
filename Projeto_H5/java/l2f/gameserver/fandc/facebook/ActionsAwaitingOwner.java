package l2f.gameserver.fandc.facebook;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import l2f.commons.annotations.Nullable;

public final class ActionsAwaitingOwner
{
	private final HashMap<FacebookAction, EnumMap<FacebookActionType, CopyOnWriteArrayList<FacebookAction>>> _actionsAwaitingOwner;

	private ActionsAwaitingOwner()
	{
		_actionsAwaitingOwner = new HashMap<FacebookAction, EnumMap<FacebookActionType, CopyOnWriteArrayList<FacebookAction>>>();
	}

	public void addNewFather(OfficialPost father)
	{
		final EnumMap<FacebookActionType, CopyOnWriteArrayList<FacebookAction>> rewardedTypes = new EnumMap<FacebookActionType, CopyOnWriteArrayList<FacebookAction>>(FacebookActionType.class);
		for (FacebookActionType rewardedType : father.getRewardedActionsForIterate())
		{
			rewardedTypes.put(rewardedType, new CopyOnWriteArrayList<FacebookAction>());
		}
		_actionsAwaitingOwner.put(father, rewardedTypes);
	}

	public void removeOldFather(FacebookAction father)
	{
	}

	public void addNewExtractedAction(FacebookAction action)
	{
		_actionsAwaitingOwner.get(action.getFather()).get(action.getActionType()).add(action);
	}

	public void removeAction(FacebookAction action)
	{
		_actionsAwaitingOwner.get(action.getFather()).get(action.getActionType()).remove(action);
	}

	public CopyOnWriteArrayList<FacebookAction> getActionsForIterate(@Nullable final FacebookAction father, FacebookActionType type)
	{
		return _actionsAwaitingOwner.get(father).get(type);
	}

	public ArrayList<FacebookAction> getActionsCopy(@Nullable final FacebookAction father, FacebookActionType type)
	{
		return new ArrayList<FacebookAction>(_actionsAwaitingOwner.get(father).get(type));
	}

	public static ActionsAwaitingOwner getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final ActionsAwaitingOwner INSTANCE = new ActionsAwaitingOwner();
	}

	@Override
	public String toString()
	{
		return "ActionsAwaitingOwner{actionsAwaitingOwner=" + _actionsAwaitingOwner + '}';
	}
}
