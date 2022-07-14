package services;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.instancemanager.ReflectionManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Util;

/**
 * Service that clears a certain instance reuse for players
 *
 * @author Synerge
 */
public class ClearInstance extends Functions
{
	private static final List<Integer> INSTANCES = new ArrayList<>();
	static
	{
		INSTANCES.add(133); // Daytime Zaken
		INSTANCES.add(135); // Ultra Daytime Zaken
		INSTANCES.add(139); // Freya Castle - Normal Mode
		INSTANCES.add(136); // Last Imperial Tomb
	}

	public void list()
	{
		Player player = getSelf();
		if (!Config.SERVICES_WASH_PK_ENABLED)
		{
			show(HtmCache.getInstance().getNotNull("npcdefault.htm", player), player);
			return;
		}

		String html = HtmCache.getInstance().getNotNull("scripts/services/ClearInstance.htm", player);

		// Instances
		final StringBuilder instances = new StringBuilder();
		for (int instanceId : INSTANCES)
		{
			final Reflection reflection = ReflectionManager.getInstance().get(instanceId);
			if (reflection == null)
			{
				continue;
			}

			instances.append(getInstanceButton(reflection));
		}

		html = html.replace("%price%", String.valueOf(Config.SERVICES_CLEAR_INSTANCE_PRICE_COUNT));
		html = html.replace("%instanceList%", instances.toString());
		show(html, player);
	}

	private static String getInstanceButton(Reflection reflection)
	{
		return "<a action=\"bypass -h scripts_services.ClearInstance:clear " + reflection.getId() + "\"> " + reflection.getName() + "</a><br>";
	}

	public void clear(String[] param)
	{
		final Player player = getSelf();
		if (player == null || param == null || param.length < 1)
		{
			return;
		}

		if (Config.SERVICES_CLEAR_INSTANCE_PRICE_COUNT == 0)
		{
			player.sendMessage("Service is turned off.");
			return;
		}

		final int instanceId = Integer.parseInt(param[0]);
		final Reflection reflection = ReflectionManager.getInstance().get(instanceId);
		if (reflection == null)
		{
			player.sendMessage("The selected instance id is invalid");
			return;
		}
		if ((player.isDead()) || (player.isAlikeDead()) || (player.isCastingNow()) || (player.isInCombat()) || (player.isAttackingNow()) || (player.isFlying()) || player.getReflection() != ReflectionManager.DEFAULT)
		{
			player.sendMessage("You must be in peace zone.");
			return;
		}
		if (Util.getPay(player, Config.SERVICES_CLEAR_INSTANCE_PRICE_ID, Config.SERVICES_CLEAR_INSTANCE_PRICE_COUNT, true))
		{
			player.removeInstanceReuse(reflection.getId());
			player.sendPacket(new ExShowScreenMessage("Cleared the reuse of the instance " + reflection.getName(), 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
		}
	}
}
