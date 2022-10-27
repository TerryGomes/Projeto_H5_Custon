package npc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import instances.Frintezza;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class FrintezzaGatekeeperInstance extends NpcInstance
{
	private static final int INSTANCE_ID = 136;
	private static final int QUEST_ITEM_ID = 8073;

	public FrintezzaGatekeeperInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("request_frintezza"))
		{
			Reflection r = player.getActiveReflection();
			if (r != null)
			{
				if (player.canReenterInstance(INSTANCE_ID))
				{
					player.teleToLocation(r.getTeleportLoc(), r);
				}
			}
			else if (player.canEnterInstance(INSTANCE_ID))
			{
				final Collection<Player> playersToJoin = getPlayersToJoin(player);
				if (checkReqiredItem(player, playersToJoin))
				{
					deleteRequiredItems(playersToJoin);
					ReflectionUtils.enterReflection(player, new Frintezza(), INSTANCE_ID);
				}
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	private static Collection<Player> getPlayersToJoin(Player player)
	{
		final Collection<Player> players = new ArrayList<>();

		if (!Config.FRINTEZZA_ALL_MEMBERS_NEED_SCROLL)
		{
			players.add(player);
			return players;
		}

		final List<Party> parties = new ArrayList<>();
		if (player.getParty().getCommandChannel() != null)
		{
			parties.addAll(player.getParty().getCommandChannel().getParties());
		}
		else
		{
			parties.add(player.getParty());
		}

		for (Party party : parties)
		{
			players.addAll(party.getMembers());
		}
		return players;
	}

	private static boolean checkReqiredItem(Player leader, Iterable<Player> allPlayers)
	{
		for (Player playerToJoin : allPlayers)
		{
			if (playerToJoin.getInventory().getCountOf(QUEST_ITEM_ID) < 1L)
			{
				if (!leader.equals(playerToJoin))
				{
					leader.sendMessage(playerToJoin.getName() + " doesn't have required item!");
				}
				playerToJoin.sendMessage("You don't have required item!");
				return false;
			}
		}

		return true;
	}

	private static void deleteRequiredItems(Iterable<Player> players)
	{
		for (Player player : players)
		{
			ItemFunctions.removeItem(player, QUEST_ITEM_ID, 1L, true, "FrintezzaGatekeeper");
		}
	}
}