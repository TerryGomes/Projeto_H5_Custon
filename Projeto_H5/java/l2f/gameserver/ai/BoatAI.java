package l2f.gameserver.ai;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.entity.boat.Boat;

public class BoatAI extends CharacterAI
{
	public BoatAI(Creature actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtArrived()
	{
		Boat actor = (Boat) getActor();
		if (actor == null)
		{
			return;
		}

		actor.onEvtArrived();
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}
}
