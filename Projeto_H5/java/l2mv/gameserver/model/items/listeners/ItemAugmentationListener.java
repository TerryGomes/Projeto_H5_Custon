package l2mv.gameserver.model.items.listeners;

import l2mv.gameserver.data.xml.holder.OptionDataHolder;
import l2mv.gameserver.listener.inventory.OnEquipListener;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SkillCoolTime;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.templates.OptionDataTemplate;

public final class ItemAugmentationListener implements OnEquipListener
{
	private static final ItemAugmentationListener _instance = new ItemAugmentationListener();

	public static ItemAugmentationListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable() || !item.isAugmented())
		{
			return;
		}

		Player player = actor.getPlayer();

		int stats[] = new int[2];
		stats[0] = 0x0000FFFF & item.getAugmentationId();
		stats[1] = item.getAugmentationId() >> 16;

		boolean sendList = false;
		for (int i : stats)
		{
			OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
			if (template == null)
			{
				continue;
			}

			player.removeStatsOwner(template);

			for (Skill skill : template.getSkills())
			{
				sendList = true;
				player.removeSkill(skill);
			}

			player.removeTriggers(template);
		}

		if (sendList)
		{
			player.sendPacket(new SkillList(player));
		}

		player.updateStats();
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable() || !item.isAugmented())
		{
			return;
		}

		Player player = actor.getPlayer();

		// При несоотвествии грейда аугмент не применяется
		if (player.getExpertisePenalty(item) > 0)
		{
			return;
		}

		int stats[] = new int[2];
		stats[0] = 0x0000FFFF & item.getAugmentationId();
		stats[1] = item.getAugmentationId() >> 16;

		boolean sendList = false;
		boolean sendReuseList = false;
		for (int i : stats)
		{
			OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
			if (template == null)
			{
				continue;
			}

			player.addStatFuncs(template.getStatFuncs(template));

			for (Skill skill : template.getSkills())
			{
				sendList = true;
				player.addSkill(skill);
				if (player.isSkillDisabled(skill))
				{
					sendReuseList = true;
				}
			}

			player.addTriggers(template);
		}

		if (sendList)
		{
			player.sendPacket(new SkillList(player));
		}

		if (sendReuseList)
		{
			player.sendPacket(new SkillCoolTime(player));
		}

		player.updateStats();
	}
}