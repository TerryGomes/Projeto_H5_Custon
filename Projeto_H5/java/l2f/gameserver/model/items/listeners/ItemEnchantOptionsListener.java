package l2f.gameserver.model.items.listeners;

import l2f.gameserver.data.xml.holder.OptionDataHolder;
import l2f.gameserver.listener.inventory.OnEquipListener;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.SkillList;
import l2f.gameserver.stats.triggers.TriggerInfo;
import l2f.gameserver.templates.OptionDataTemplate;

public final class ItemEnchantOptionsListener implements OnEquipListener
{
	private static final ItemEnchantOptionsListener _instance = new ItemEnchantOptionsListener();

	public static ItemEnchantOptionsListener getInstance()
	{
		return _instance;
	}

	@Override
	public void onEquip(int slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable())
		{
			return;
		}
		Player player = actor.getPlayer();

		boolean needSendInfo = false;
		for (int i : item.getEnchantOptions())
		{
			OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
			if (template == null)
			{
				continue;
			}

			player.addStatFuncs(template.getStatFuncs(template));
			for (Skill skill : template.getSkills())
			{
				player.addSkill(skill, false);
				needSendInfo = true;
			}
			for (TriggerInfo triggerInfo : template.getTriggerList())
			{
				player.addTrigger(triggerInfo);
			}
		}

		if (needSendInfo)
		{
			player.sendPacket(new SkillList(player));
		}
		player.sendChanges();
	}

	@Override
	public void onUnequip(int slot, ItemInstance item, Playable actor)
	{
		if (!item.isEquipable())
		{
			return;
		}

		Player player = actor.getPlayer();

		boolean needSendInfo = false;
		for (int i : item.getEnchantOptions())
		{
			OptionDataTemplate template = OptionDataHolder.getInstance().getTemplate(i);
			if (template == null)
			{
				continue;
			}

			player.removeStatsOwner(template);
			for (Skill skill : template.getSkills())
			{
				player.removeSkill(skill, false);
				needSendInfo = true;
			}
			for (TriggerInfo triggerInfo : template.getTriggerList())
			{
				player.removeTrigger(triggerInfo);
			}
		}

		if (needSendInfo)
		{
			player.sendPacket(new SkillList(player));
		}
		player.sendChanges();
	}
}
