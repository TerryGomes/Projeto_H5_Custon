package l2mv.gameserver.instancemanager.achievements_engine.conditions;

import java.util.StringTokenizer;

import l2mv.gameserver.instancemanager.achievements_engine.base.Condition;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.utils.ItemFunctions;

public class ItemsCount extends Condition
{
	public ItemsCount(Object value)
	{
		super(value);
		setName("Items Count");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}
		String s = getValue().toString();
		StringTokenizer st = new StringTokenizer(s, ",");
		int id = 0;
		int ammount = 0;

		try
		{
			id = Integer.parseInt(st.nextToken());
			ammount = Integer.parseInt(st.nextToken());
			// if (player.getInventory().getInventoryItemCount(id, 0) >= ammount)
			if (ItemFunctions.getItemCount(player, id) >= ammount)
			{
				return true;
			}
		}
		catch (NumberFormatException nfe)
		{
			nfe.printStackTrace();
		}
		return false;
	}
}