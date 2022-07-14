package npc.model;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;
import quests._10288_SecretMission;

/**
 * @author pchayka
 */
public class PriestAquilaniInstance extends NpcInstance
{

	public PriestAquilaniInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (player.getQuestState(_10288_SecretMission.class) != null && player.getQuestState(_10288_SecretMission.class).isCompleted())
		{
			player.sendPacket(new NpcHtmlMessage(player, this, "default/32780-1.htm", val));
			return;
		}
		else
		{
			player.sendPacket(new NpcHtmlMessage(player, this, "default/32780.htm", val));
			return;
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("teleport"))
		{
			player.teleToLocation(new Location(118833, -80589, -2688));
			return;
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}