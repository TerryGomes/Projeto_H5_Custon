package l2mv.gameserver.ai;

import java.util.ArrayList;
import java.util.List;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.RaceManagerInstance;
import l2mv.gameserver.network.serverpackets.MonRaceInfo;

public class RaceManager extends DefaultAI
{
	private boolean thinking = false; // to prevent recursive thinking
	private List<Player> _knownPlayers = new ArrayList<>();

	public RaceManager(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 5000;
	}

	@Override
	public void runImpl()
	{
		onEvtThink();
	}

	@Override
	protected void onEvtThink()
	{
		final RaceManagerInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		MonRaceInfo packet = actor.getPacket();
		if (packet == null)
		{
			return;
		}

		synchronized (this)
		{
			if (thinking)
			{
				return;
			}
			thinking = true;
		}

		try
		{
			List<Player> newPlayers = new ArrayList<>();
			for (Player player : World.getAroundPlayers(actor, 1200, 200))
			{
				if (player == null)
				{
					continue;
				}
				newPlayers.add(player);
				if (!_knownPlayers.contains(player))
				{
					player.sendPacket(packet);
				}
				_knownPlayers.remove(player);
			}

			for (Player player : _knownPlayers)
			{
				actor.removeKnownPlayer(player);
			}

			_knownPlayers = newPlayers;
		}
		finally
		{
			// Stop thinking action
			thinking = false;
		}
	}

	@Override
	public RaceManagerInstance getActor()
	{
		return (RaceManagerInstance) super.getActor();
	}
}