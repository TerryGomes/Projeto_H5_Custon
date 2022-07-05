package l2f.gameserver.handler.admincommands.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ItemFunctions;

public class AdminClientSupport implements IAdminCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(AdminClientSupport.class);

	private static enum Commands
	{
		admin_setskill, admin_summon
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player player)
	{
		if (!player.getPlayerAccess().CanEditChar)
		{
			return false;
		}

		Commands c = (Commands) comm;
		GameObject target = player.getTarget();
		switch (c)
		{
		case admin_setskill:
			if ((wordList.length != 3) || target == null || !target.isPlayer())
			{
				return false;
			}
			try
			{
				Skill skill = SkillTable.getInstance().getInfo(Integer.parseInt(wordList[1]), Integer.parseInt(wordList[2]));
				if (skill == null)
				{
					player.sendMessage("Too big level, max:" + SkillTable.getInstance().getMaxLevel(Integer.parseInt(wordList[1])));
					return false;
				}
				target.getPlayer().addSkill(skill, true);
				target.getPlayer().sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));
			}
			catch (NumberFormatException e)
			{
				_log.info("AdminClientSupport:useAdminCommand(Enum,String[],String,L2Player): " + e, e);
				return false;
			}
			break;
		case admin_summon:
			if (wordList.length != 3)
			{
				return false;
			}

			try
			{
				int id = Integer.parseInt(wordList[1]);
				long count = Long.parseLong(wordList[2]);

				if (id >= 1000000)
				{
					if (target == null)
					{
						target = player;
					}

					NpcTemplate template = NpcHolder.getInstance().getTemplate(id - 1000000);

					for (int i = 0; i < count; i++)
					{
						NpcInstance npc = template.getNewInstance();
						npc.setSpawnedLoc(target.getLoc());
						npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);

						npc.spawnMe(npc.getSpawnedLoc());
					}
				}
				else
				{
					if (target == null)
					{
						target = player;
					}

					if (!target.isPlayer())
					{
						return false;
					}

					ItemTemplate template = ItemHolder.getInstance().getTemplate(id);
					if (template == null)
					{
						return false;
					}

					if (template.isStackable())
					{
						ItemInstance item = ItemFunctions.createItem(id);
						item.setCount(count);

						target.getPlayer().getInventory().addItem(item, "admin_summon");
						target.getPlayer().sendPacket(SystemMessage2.obtainItems(id, count, 0));
					}
					else
					{
						for (int i = 0; i < count; i++)
						{
							ItemInstance item = ItemFunctions.createItem(id);

							target.getPlayer().getInventory().addItem(item, "admin_summon");
							target.getPlayer().sendPacket(SystemMessage2.obtainItems(id, 1, 0));
						}
					}
				}
			}
			catch (NumberFormatException e)
			{
				_log.info("AdminClientSupport:useAdminCommand(Enum,String[],String,L2Player): " + e, e);
				return false;
			}

			break;
		}
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
