package l2f.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Playable;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Env;
import l2f.gameserver.utils.ItemFunctions;

public class EffectRestorationRandom extends Effect
{
	private final List<List<Item>> items;
	private final double[] chances;

	private static final Pattern groupPattern = Pattern.compile("\\{\\[([\\d:;]+?)\\]([\\d.e-]+)\\}");

	public EffectRestorationRandom(Env env, EffectTemplate template)
	{
		super(env, template);
		String[] groups = getTemplate().getParam().getString("Items").split(";");
		items = new ArrayList<List<Item>>(groups.length);
		chances = new double[groups.length];

		double prevChance = 0;
		for (int i = 0; i < groups.length; i++)
		{
			String group = groups[i];
			Matcher m = groupPattern.matcher(group);
			if (m.find())
			{
				String its = m.group(1);
				List<Item> list = new ArrayList<Item>(its.split(";").length);

				for (String item : its.split(";"))
				{
					String id = item.split(":")[0];
					String count = item.split(":")[1];

					Item it = new Item();
					it.itemId = Integer.parseInt(id);
					it.count = Long.parseLong(count);
					list.add(it);
				}
				double chance = Double.parseDouble(m.group(2));
				items.add(i, list);
				chances[i] = prevChance + chance;
				prevChance = chances[i];
			}
		}
	}

	public EffectRestorationRandom(Effect effect)
	{
		super(effect);
		final String[] groups = getTemplate().getParam().getString("Items").split(";");
		items = new ArrayList<List<Item>>(groups.length);
		chances = new double[groups.length];
		double prevChance = 0.0;
		for (int i = 0; i < groups.length; ++i)
		{
			final String group = groups[i];
			final Matcher m = EffectRestorationRandom.groupPattern.matcher(group);
			if (m.find())
			{
				final String its = m.group(1);
				final List<Item> list = new ArrayList<Item>(its.split(";").length);
				for (String item : its.split(";"))
				{
					final String id = item.split(":")[0];
					final String count = item.split(":")[1];
					final Item it = new Item();
					it.itemId = Integer.parseInt(id);
					it.count = Long.parseLong(count);
					list.add(it);
				}
				final double chance = Double.parseDouble(m.group(2));
				items.add(i, list);
				chances[i] = prevChance + chance;
				prevChance = chances[i];
			}
		}
	}

	@Override
	public void onStart()
	{
		super.onStart();
		double chance = (double) Rnd.get(0, 1000000) / 10000;

		double prevChance = 0.0D;
		int i = 0;
		for (; i < chances.length; i++)
		{
			if (chance > prevChance && chance < chances[i])
			{
				break;
			}
		}

		if (i < chances.length)
		{
			List<Item> itemList = items.get(i);
			for (Item item : itemList)
			{
				ItemFunctions.addItem((Playable) getEffected(), item.itemId, item.count, true, "EffectRestorationRandom");
			}
		}
		else
		{
			getEffected().sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
		} // иначе ничего не выдаем
	}

	@Override
	protected boolean onActionTime()
	{
		return false;
	}

	private final class Item
	{
		public int itemId;
		public long count;
	}
}
