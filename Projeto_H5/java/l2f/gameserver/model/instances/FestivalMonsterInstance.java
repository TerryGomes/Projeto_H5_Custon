package l2f.gameserver.model.instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Party;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.reward.RewardList;
import l2f.gameserver.model.reward.RewardType;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ItemFunctions;

public class FestivalMonsterInstance extends MonsterInstance
{
	protected int _bonusMultiplier = 1;

	public FestivalMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		_hasRandomWalk = false;
	}

	public void setOfferingBonus(int bonusMultiplier)
	{
		_bonusMultiplier = bonusMultiplier;
	}

	@Override
	protected void onSpawn()
	{
		super.onSpawn();

		List<Player> pl = World.getAroundPlayers(this);
		if (pl.isEmpty())
		{
			return;
		}
		List<Player> alive = new ArrayList<Player>(9);
		for (Player p : pl)
		{
			if (!p.isDead())
			{
				alive.add(p);
			}
		}
		if (alive.isEmpty())
		{
			return;
		}

		Player target = alive.get(Rnd.get(alive.size()));
		getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, target, 1);
	}

	/**
	 * Actions:
	 * <li>Check if the killing object is a player, and then find the party they belong to.</li>
	 * <li>Add a blood offering item to the leader of the party.</li>
	 * <li>Update the party leader's inventory to show the new item addition.</li>
	 */
	@Override
	public void rollRewards(Map.Entry<RewardType, RewardList> entry, Creature lastAttacker, Creature topDamager)
	{
		super.rollRewards(entry, lastAttacker, topDamager);

		if ((entry.getKey() != RewardType.RATED_GROUPED) || !topDamager.isPlayable())
		{
			return;
		}

		Player topDamagerPlayer = topDamager.getPlayer();
		Party associatedParty = topDamagerPlayer.getParty();

		if (associatedParty == null)
		{
			return;
		}

		Player partyLeader = associatedParty.getLeader();
		if (partyLeader == null)
		{
			return;
		}

		ItemInstance bloodOfferings = ItemFunctions.createItem(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING);

		bloodOfferings.setCount(_bonusMultiplier);
		partyLeader.getInventory().addItem(bloodOfferings, "FestivalMonster Offerings");
		partyLeader.sendPacket(SystemMessage2.obtainItems(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING, _bonusMultiplier, 0));
	}

	@Override
	public boolean isAggressive()
	{
		return true;
	}

	@Override
	public int getAggroRange()
	{
		return 1000;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}