package l2f.gameserver.instancemanager.achievements_engine.conditions;

import java.util.Map;

import l2f.gameserver.instancemanager.RaidBossSpawnManager;
import l2f.gameserver.instancemanager.achievements_engine.base.Condition;
import l2f.gameserver.model.Player;

public class RaidPoints extends Condition
{
	public RaidPoints(Object value)
	{
		super(value);
		setName("Raid Points");
	}

	@Override
	public boolean meetConditionRequirements(Player player)
	{
		if (getValue() == null)
		{
			return false;
		}

		int val = Integer.parseInt(getValue().toString());

		// RaidBossPointsManager.getInstance();
		// if (Raidboss.getPointsByOwnerId(player.getObjectId()) >= val)
		Map<Integer, Integer> points = RaidBossSpawnManager.getInstance().getPointsForOwnerId(player.getObjectId());
		int totalPoints = 0;
		if (points != null && !points.isEmpty())
		{
			for (Map.Entry<Integer, Integer> e : points.entrySet())
			{
				switch (e.getKey())
				{
				case 0: // RaidBossSpawnManager.KEY_TOTAL_POINTS
					totalPoints = e.getValue();
					break;
				}
			}
		}

		if (totalPoints >= val)
		{
			return true;
		}
		return false;
	}
}