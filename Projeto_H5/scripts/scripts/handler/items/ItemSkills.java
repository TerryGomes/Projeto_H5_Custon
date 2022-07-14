package handler.items;

import gnu.trove.set.hash.TIntHashSet;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.handler.items.ItemHandler;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.templates.item.ItemTemplate;

public class ItemSkills extends ScriptItemHandler implements ScriptFile
{
	private int[] _itemIds;

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	public ItemSkills()
	{
		TIntHashSet set = new TIntHashSet();
		for (ItemTemplate template : ItemHolder.getInstance().getAllTemplates())
		{
			if (template == null)
			{
				continue;
			}

			for (Skill skill : template.getAttachedSkills())
			{
				if (skill.isHandler())
				{
					set.add(template.getItemId());
				}
			}
		}
		_itemIds = set.toArray();
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		Player player;
		if (playable.isPlayer())
		{
			player = (Player) playable;
		}
		else if (playable.isPet())
		{
			player = playable.getPlayer();
		}
		else
		{
			return false;
		}

		Skill[] skills = item.getTemplate().getAttachedSkills();

		for (int i = 0; i < skills.length; i++)
		{
			Skill skill = skills[i];
			Creature aimingTarget = skill.getAimingTarget(player, player.getTarget());
			if (skill.checkCondition(player, aimingTarget, ctrl, false, true))
			{
				player.getAI().Cast(skill, aimingTarget, ctrl, false);
			}
			else if (i == 0) // FIXME [VISTALL] всегда первый скил идет вместо конда?
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public int[] getItemIds()
	{
		return _itemIds;
	}
}
