package ai.seedofdestruction;

import java.util.ArrayList;
import java.util.List;

import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.ExStartScenePlayer;

public class TiatCamera extends DefaultAI
{
	private List<Player> _players = new ArrayList<Player>();

	public TiatCamera(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
		actor.startDamageBlocked();
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		for (Player p : World.getAroundPlayers(actor, 300, 300))
		{
			if (!_players.contains(p))
			{
				p.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_OPENING);
				_players.add(p);
			}
		}
		return true;
	}
}