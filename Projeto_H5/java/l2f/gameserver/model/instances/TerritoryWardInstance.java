package l2f.gameserver.model.instances;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.templates.npc.NpcTemplate;

public class TerritoryWardInstance extends NpcInstance
{
	private final TerritoryWardObject _territoryWard;

	public TerritoryWardInstance(int objectId, NpcTemplate template, TerritoryWardObject territoryWardObject)
	{
		super(objectId, template);
		setHasChatWindow(false);
		_territoryWard = territoryWardObject;
	}

	@Override
	public void onDeath(Creature killer)
	{
		super.onDeath(killer);

		final Player player = killer.getPlayer();
		if (player == null)
		{
			return;
		}

		if (_territoryWard.canPickUp(player))
		{
			_territoryWard.pickUp(player);
			decayMe();
		}
	}

	@Override
	protected void onDecay()
	{
		decayMe();

		_spawnAnimation = 2;
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		DominionSiegeEvent siegeEvent = getEvent(DominionSiegeEvent.class);
		if (siegeEvent == null)
		{
			return false;
		}
		DominionSiegeEvent siegeEvent2 = attacker.getEvent(DominionSiegeEvent.class);
		if ((siegeEvent2 == null) || (siegeEvent == siegeEvent2) || (siegeEvent2.getResidence().getOwner() != attacker.getClan()))
		{
			return false;
		}
		return true;
	}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public Clan getClan()
	{
		return null;
	}
}
