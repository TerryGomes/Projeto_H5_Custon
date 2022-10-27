package l2mv.gameserver.model.reward;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.instancemanager.SpawnManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * Created by Michał on 04.12.13.
 */
public class CalculateRewardChances
{
	public static final double CORRECT_CHANCE_TRIES = 10000.0;
	private static final Map<Integer, Integer[]> droplistsCountCache = new HashMap<>();
	private static final Map<String, String> correctedChances = new HashMap<>();

	public static List<NpcTemplate> getNpcsContainingString(CharSequence name)
	{
		List<NpcTemplate> templates = new ArrayList<>();

		for (NpcTemplate template : NpcHolder.getInstance().getAll())
		{
			if (templateExists(template) && StringUtils.containsIgnoreCase(template.getName(), name))
			{
				if (isDroppingAnything(template))
				{
					templates.add(template);
				}
			}
		}

		return templates;
	}

	public static int getDroplistsCountByItemId(int itemId, boolean drop)
	{
		if (droplistsCountCache.containsKey(itemId))
		{
			if (drop)
			{
				return droplistsCountCache.get(itemId)[0].intValue();
			}
			else
			{
				return droplistsCountCache.get(itemId)[1].intValue();
			}
		}

		int dropCount = 0;
		int spoilCount = 0;
		for (NpcTemplate template : NpcHolder.getInstance().getAll())
		{
			if (templateExists(template))
			{
				for (Map.Entry<RewardType, RewardList> rewardEntry : template.getRewards().entrySet())
				{
					for (RewardGroup group : rewardEntry.getValue())
					{
						for (RewardData data : group.getItems())
						{
							if (data.getItem().getItemId() == itemId)
							{
								if (rewardEntry.getKey() == RewardType.SWEEP)
								{
									spoilCount++;
								}
								else
								{
									dropCount++;
								}
							}
						}
					}
				}
			}
		}

		droplistsCountCache.put(itemId, new Integer[]
		{
			dropCount,
			spoilCount
		});

		if (drop)
		{
			return dropCount;
		}
		else
		{
			return spoilCount;
		}
	}

	private static boolean templateExists(NpcTemplate template)
	{
		if ((template == null) || (SpawnManager.getInstance().getSpawnedCountByNpc(template.getNpcId()) == 0))
		{
			return false;
		}
		return true;
	}

	public static boolean isItemDroppable(int itemId)
	{
		if (!droplistsCountCache.containsKey(itemId))
		{
			getDroplistsCountByItemId(itemId, true);
		}

		return droplistsCountCache.get(itemId)[0].intValue() > 0 || droplistsCountCache.get(itemId)[1].intValue() > 0;
	}

	public static List<ItemTemplate> getDroppableItems()
	{
		List<ItemTemplate> items = new ArrayList<>();
		for (NpcTemplate template : NpcHolder.getInstance().getAll())
		{
			if (templateExists(template))
			{
				for (Map.Entry<RewardType, RewardList> rewardEntry : template.getRewards().entrySet())
				{
					for (RewardGroup group : rewardEntry.getValue())
					{
						for (RewardData data : group.getItems())
						{
							if (!items.contains(data.getItem()))
							{
								items.add(data.getItem());
							}
						}
					}
				}
			}
		}
		return items;
	}

	/**
	 * Key: 0 - Drop, 1 - Spoil
	 * @param itemId
	 * @return
	 */
	public static List<NpcTemplateDrops> getNpcsByDropOrSpoil(int itemId)
	{
		List<NpcTemplateDrops> templates = new ArrayList<>();
		for (NpcTemplate template : NpcHolder.getInstance().getAll())
		{
			if ((template == null) || (SpawnManager.getInstance().getSpawnedCountByNpc(template.getNpcId()) == 0))
			{
				continue;
			}

			boolean[] dropSpoil = templateContainsItemId(template, itemId);

			if (dropSpoil[0])
			{
				templates.add(new NpcTemplateDrops(template, true));
			}
			if (dropSpoil[1])
			{
				templates.add(new NpcTemplateDrops(template, false));
			}
		}
		return templates;
	}

	public static class NpcTemplateDrops
	{
		public NpcTemplate template;
		public boolean dropNoSpoil;

		private NpcTemplateDrops(NpcTemplate template, boolean dropNoSpoil)
		{
			this.template = template;
			this.dropNoSpoil = dropNoSpoil;
		}
	}

	private static boolean[] templateContainsItemId(NpcTemplate template, int itemId)
	{
		boolean[] dropSpoil =
		{
			false,
			false
		};
		for (Map.Entry<RewardType, RewardList> rewardEntry : template.getRewards().entrySet())
		{
			if (rewardListContainsItemId(rewardEntry.getValue(), itemId))
			{
				if (rewardEntry.getKey() == RewardType.SWEEP)
				{
					dropSpoil[1] = true;
				}
				else
				{
					dropSpoil[0] = true;
				}
			}
		}
		return dropSpoil;
	}

	private static boolean rewardListContainsItemId(RewardList list, int itemId)
	{
		for (RewardGroup group : list)
		{
			for (RewardData reward : group.getItems())
			{
				if (reward.getItemId() == itemId)
				{
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isDroppingAnything(NpcTemplate template)
	{
		for (Map.Entry<RewardType, RewardList> rewardEntry : template.getRewards().entrySet())
		{
			for (RewardGroup group : rewardEntry.getValue())
			{
				if (!group.getItems().isEmpty())
				{
					return true;
				}
			}
		}
		return false;
	}

	public static List<RewardData> getDrops(NpcTemplate template, boolean drop, boolean spoil)
	{
		List<RewardData> allRewards = new ArrayList<>();
		if (template == null)
		{
			return allRewards;
		}

		for (Map.Entry<RewardType, RewardList> rewardEntry : template.getRewards().entrySet())
		{
			if ((rewardEntry.getKey() == RewardType.SWEEP && !spoil) || (rewardEntry.getKey() != RewardType.SWEEP && !drop))
			{
				continue;
			}
			for (RewardGroup group : rewardEntry.getValue())
			{
				for (RewardData reward : group.getItems())
				{
					allRewards.add(reward);
				}
			}
		}
		return allRewards;
	}

	public static String getDropChance(Player player, NpcTemplate npc, boolean dropNoSpoil, int itemId)
	{
		TypeGroupData info = getGroupAndData(npc, dropNoSpoil, itemId);

		if (info == null)
		{
			return "0";
		}

		double mod = Experience.penaltyModifier((long) NpcInstance.calculateLevelDiffForDrop(npc.level, player.getLevel(), false), 9.0);
		double baseRate = 1.0;
		double playerRate = 1.0;
		if (info.type == RewardType.SWEEP)
		{
			baseRate = Config.RATE_DROP_SPOIL;
			playerRate = player.getRateSpoil();
		}
		else if (info.type == RewardType.RATED_GROUPED)
		{
			if (info.group.isAdena())
			{
				return getAdenaChance(info, mod);
			}
			if (npc.isRaid)
			{
				return getItemChance(info, mod, Config.RATE_DROP_RAIDBOSS, 1.0);
			}
			baseRate = Config.RATE_DROP_ITEMS;
			playerRate = player.getRateItems();
		}

		return getItemChance(info, mod, baseRate, playerRate);
	}

	private static String getAdenaChance(TypeGroupData info, double mod)
	{
		if (mod <= 0)
		{
			return "0";
		}

		double groupChance = info.group.getChance();
		if (mod > 10)
		{
			groupChance = (double) RewardList.MAX_CHANCE;
		}

		double itemChance = info.data.getChance();

		groupChance /= (double) RewardList.MAX_CHANCE;
		itemChance /= (double) RewardList.MAX_CHANCE;
		double finalChance = groupChance * itemChance;
		return String.valueOf(finalChance * 100);
	}

	private static String getItemChance(TypeGroupData info, double mod, double baseRate, double playerRate)
	{
		if (mod <= 0.0)
		{
			return "0";
		}

		double rate;
		if (info.group.notRate())
		{
			rate = Math.min(mod, 1.0);
		}
		else
		{
			rate = baseRate * playerRate * mod;
		}

		double mult = Math.ceil(rate);

		BigDecimal totalChance = BigDecimal.valueOf(0.0);
		for (double n = 0.0; n < mult; n++)
		{
			BigDecimal groupChance = BigDecimal.valueOf(info.group.getChance() * Math.min(rate - n, 1.0));
			BigDecimal itemChance = BigDecimal.valueOf(info.data.getChance());
			groupChance = groupChance.divide(BigDecimal.valueOf((long) RewardList.MAX_CHANCE));
			itemChance = itemChance.divide(BigDecimal.valueOf((long) RewardList.MAX_CHANCE));
			totalChance = totalChance.add(groupChance.multiply(itemChance));
		}
		String totalChanceString = totalChance.multiply(BigDecimal.valueOf(100.0)).toString();

		return getCorrectedChance(totalChanceString, info.group.getChance() / 10000.0, info.data.getChance() / 10000.0, mult);
	}

	private static String getCorrectedChance(String totalChanceString, double groupChance, double itemChance, double mult)
	{
		Comparable<BigDecimal> totalChance = new BigDecimal(totalChanceString);
		if (totalChance.compareTo(BigDecimal.valueOf(5.0)) < 0)
		{
			return totalChance.toString();
		}

		if (correctedChances.containsKey(totalChanceString))
		{
			return correctedChances.get(totalChanceString);
		}

		double totalPassed = 0.0;
		double x;
		for (double i = 0.0; i < CORRECT_CHANCE_TRIES; i++)
		{
			for (x = 0.0; x < mult; x++)
			{
				if (Rnd.chance(groupChance))
				{
					if (Rnd.chance(itemChance))
					{
						totalPassed++;
						break;
					}
				}
			}
		}
		String finalValue = String.valueOf(totalPassed / (CORRECT_CHANCE_TRIES / 100.0));
		correctedChances.put(totalChanceString, finalValue);
		return finalValue;
	}

	private static class TypeGroupData
	{
		private final RewardType type;
		private final RewardGroup group;
		private final RewardData data;

		private TypeGroupData(RewardType type, RewardGroup group, RewardData data)
		{
			this.type = type;
			this.group = group;
			this.data = data;
		}
	}

	public static long[] getDropCounts(Player player, NpcTemplate npc, boolean dropNoSpoil, int itemId)
	{
		TypeGroupData info = getGroupAndData(npc, dropNoSpoil, itemId);

		if (info == null)
		{
			return new long[]
			{
				0L,
				0L
			};
		}

		double mod = Experience.penaltyModifier((long) NpcInstance.calculateLevelDiffForDrop(npc.level, player.getLevel(), false), 9.0);
		double baseRate = 1.0;
		double playerRate = 1.0;
		if (info.type == RewardType.SWEEP)
		{
			baseRate = Config.RATE_DROP_SPOIL;
			playerRate = player.getRateSpoil();
		}
		else if (info.type == RewardType.RATED_GROUPED)
		{
			if (info.group.isAdena())
			{
				baseRate = Config.RATE_DROP_ADENA;
				playerRate = player.getRateAdena();
			}
			else
			{
				baseRate = Config.RATE_DROP_ITEMS;
				playerRate = player.getRateItems();
			}
		}
		double imult;
		if (info.data.notRate() && itemId != ItemTemplate.ITEM_ID_ADENA)
		{
			imult = 1.0;
		}
		else
		{
			imult = baseRate * playerRate * mod;
		}

		long minDrop = info.data.getMinDrop();
		if (itemId == ItemTemplate.ITEM_ID_ADENA)
		{
			minDrop *= (long) imult;
		}
		long maxDrop = (long) ((double) info.data.getMaxDrop() * Math.ceil(imult));
		return new long[]
		{
			minDrop,
			maxDrop
		};
	}

	private static TypeGroupData getGroupAndData(NpcTemplate npc, boolean dropNoSpoil, int itemId)
	{
		for (Map.Entry<RewardType, RewardList> rewardEntry : npc.getRewards().entrySet())
		{
			if ((rewardEntry.getKey() == RewardType.SWEEP && dropNoSpoil) || (rewardEntry.getKey() != RewardType.SWEEP && !dropNoSpoil))
			{
				continue;
			}

			for (RewardGroup group : rewardEntry.getValue())
			{
				for (RewardData reward : group.getItems())
				{
					if (reward.getItemId() == itemId)
					{
						return new TypeGroupData(rewardEntry.getKey(), group, reward);
					}
				}
			}
		}
		return null;
	}
}
