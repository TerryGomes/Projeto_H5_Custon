package npc.model.events;

import java.util.List;
import java.util.StringTokenizer;

import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.instancemanager.games.HandysBlockCheckerManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.events.EventType;
import l2mv.gameserver.model.entity.events.impl.KrateisCubeEvent;
import l2mv.gameserver.model.entity.events.impl.KrateisCubeRunnerEvent;
import l2mv.gameserver.model.entity.events.objects.KrateisCubePlayerObject;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date  15:52/19.11.2010
 */
public class KrateisCubeManagerInstance extends NpcInstance
{
	public KrateisCubeManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("Kratei_UnRegister"))
		{
			KrateisCubeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 2);
			for (KrateisCubeEvent cubeEvent : runnerEvent.getCubes())
			{
				List<KrateisCubePlayerObject> list = cubeEvent.getObjects(KrateisCubeEvent.REGISTERED_PLAYERS);
				KrateisCubePlayerObject krateisCubePlayer = cubeEvent.getRegisteredPlayer(player);

				if (krateisCubePlayer != null)
				{
					list.remove(krateisCubePlayer);
				}
			}

			showChatWindow(player, 4);
		}
		else if (command.startsWith("Kratei_TryRegister"))
		{
			KrateisCubeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 2);
			if (runnerEvent.isRegistrationOver())
			{
				if (runnerEvent.isInProgress())
				{
					showChatWindow(player, 3);
				}
				else
				{
					showChatWindow(player, 7);
				}
			}
			else if (player.getLevel() < 70)
			{
				showChatWindow(player, 2);
			}
			else
			{
				showChatWindow(player, 5);
			}
		}
		else if (command.startsWith("Kratei_SeeList"))
		{
			if (player.getLevel() < 70)
			{
				showChatWindow(player, 2);
			}
			else
			{
				showChatWindow(player, 5);
			}
		}
		else if (command.startsWith("Kratei_Register"))
		{
			if (Olympiad.isRegistered(player) || HandysBlockCheckerManager.isRegistered(player))
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_BE_SIMULTANEOUSLY_REGISTERED_FOR_PVP_MATCHES_SUCH_AS_THE_OLYMPIAD_UNDERGROUND_COLISEUM_AERIAL_CLEFT_KRATEIS_CUBE_AND_HANDYS_BLOCK_CHECKERS);
				return;
			}

			if (player.isCursedWeaponEquipped())
			{
				player.sendPacket(SystemMsg.YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON);
				return;
			}

			// TODO [VISTALL] Добавить проверки?

			StringTokenizer t = new StringTokenizer(command);
			if (t.countTokens() < 2)
			{
				return;
			}
			t.nextToken();
			KrateisCubeEvent cubeEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, Integer.parseInt(t.nextToken()));
			if (cubeEvent == null)
			{
				return;
			}

			if (player.getLevel() < cubeEvent.getMinLevel() || player.getLevel() > cubeEvent.getMaxLevel())
			{
				showChatWindow(player, 2);
				return;
			}

			List<KrateisCubePlayerObject> list = cubeEvent.getObjects(KrateisCubeEvent.REGISTERED_PLAYERS);
			KrateisCubePlayerObject krateisCubePlayer = cubeEvent.getRegisteredPlayer(player);

			if (krateisCubePlayer != null)
			{
				showChatWindow(player, 6);
				return;
			}

			if (list.size() >= 25)
			{
				showChatWindow(player, 9);
			}
			else
			{
				cubeEvent.addObject(KrateisCubeEvent.REGISTERED_PLAYERS, new KrateisCubePlayerObject(player));
				showChatWindow(player, 8);
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
}
