package npc.model;

import java.util.StringTokenizer;

import l2f.gameserver.Config;
import l2f.gameserver.cache.Msg;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.model.instances.MerchantInstance;
import l2f.gameserver.network.serverpackets.MagicSkillUse;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.SocialAction;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.item.ItemTemplate;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.HtmlUtils;
import l2f.gameserver.utils.Util;

public final class ClassMasterInstance extends MerchantInstance
{
	/**
	 * FandC
	 */
	private static final long serialVersionUID = -6206315361251464210L;

	public ClassMasterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	private String makeMessage(Player player)
	{
		ClassId classId = player.getClassId();
		int jobLevelTemp = 0;
		switch (classId.getLevel())
		{
		case 1:
			jobLevelTemp = 1;
			break;
		case 2:
			jobLevelTemp = 2;
			break;
		case 3:
			jobLevelTemp = 3;
			break;
		default:
			jobLevelTemp = 0;
			break;
		}
		int jobLevel = classId.getLevel();
		int level = player.getLevel();

		StringBuilder html = new StringBuilder();
		if (Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevelTemp))
		{
			jobLevel = 4;
		}

		if ((level >= 20 && jobLevel == 1 || level >= 40 && jobLevel == 2 || level >= 76 && jobLevel == 3) && Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevelTemp))
		{
			ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);
			if (Config.CLASS_MASTERS_PRICE_LIST[jobLevel] > 0)
			{
				html.append("Price: ").append(Util.formatAdena(Config.CLASS_MASTERS_PRICE_LIST[jobLevel])).append(" ").append(item.getName()).append("<br1>");
			}
			for (ClassId cid : ClassId.VALUES)
			{
				// Inspector is heir trooper and warder, but to replace it as a profession can not be
				// As this subclass. Inherited from their parents in order to obtain skills.
				if (cid == ClassId.inspector)
				{
					continue;
				}
				if (cid.childOf(classId) && cid.getLevel() == classId.getLevel() + 1)
				{
					html.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_change_class ").append(cid.getId()).append(" ").append(Config.CLASS_MASTERS_PRICE_LIST[jobLevel]).append("\">")
								.append(HtmlUtils.htmlClassName(cid.getId())).append("</a><br>");
				}
			}
			player.sendPacket(new NpcHtmlMessage(player, this).setHtml(html.toString()));
		}
		else
		{
			switch (jobLevel)
			{
			case 1:
				html.append("Come back here when you reached level 20 to change your class.");
				break;
			case 2:
				html.append("Come back here when you reached level 40 to change your class.");
				break;
			case 3:
				html.append("Come back here when you reached level 76 to change your class.");
				break;
			case 0:
				html.append("There is no class changes for you any more.");
				break;
			default:
				html.append("There is no class changes for you right now !");
				break;
			}
		}
		return html.toString();
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
		msg.setFile("custom/31860.htm");
		msg.replace("%classmaster%", makeMessage(player));
		msg.replace("%nick%", player.getName());
		player.sendPacket(msg);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		StringTokenizer st = new StringTokenizer(command);
		if (st.nextToken().equals("change_class"))
		{
			int val = Integer.parseInt(st.nextToken());
			long price = Long.parseLong(st.nextToken());
			if (player.getInventory().destroyItemByItemId(Config.CLASS_MASTERS_PRICE_ITEM, price, "ClassMasterInstance"))
			{
				changeClass(player, val);
			}
			else if (Config.CLASS_MASTERS_PRICE_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	private void changeClass(Player player, int val)
	{
		if (player.getClassId().getLevel() == 3)
		{
			player.sendPacket(Msg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS); // ??? 3 ?????
		}
		else
		{
			player.sendPacket(Msg.CONGRATULATIONS_YOU_HAVE_TRANSFERRED_TO_A_NEW_CLASS); // ??? 1 ? 2 ?????
		}

		player.setClassId(val, false, false);
		player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.VICTORY));
		final MagicSkillUse msu = new MagicSkillUse(player, player, 2527, 1, 0, 500);
		player.broadcastPacket(msu);
		player.broadcastCharInfo();
	}
}