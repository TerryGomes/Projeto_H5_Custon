package l2mv.gameserver.model.entity.events;

import l2mv.commons.lang.reference.HardReference;
import l2mv.commons.lang.reference.HardReferences;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.scripts.Functions;

public abstract class GameEvent
{
	public HardReference<Player> self = HardReferences.emptyRef();
	public HardReference<NpcInstance> npc = HardReferences.emptyRef();
	public static final int STATE_INACTIVE = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_RUNNING = 2;

	public int getState()
	{
		return 0;
	}

	public abstract String getName();

	public long getNextTime()
	{
		return 0;
	}

	public boolean isRunning()
	{
		return getState() == 2;
	}

	public boolean canRegister(Player player, boolean first)
	{
		return (getState() == 1) && (!isParticipant(player)) && (player._event == null);
	}

	public abstract boolean isParticipant(Player paramPlayer);

	public abstract boolean register(Player paramPlayer);

	public abstract void unreg(Player paramPlayer);

	public abstract void remove(Player paramPlayer);

	public abstract void start();

	public abstract void finish();

	public abstract void abort();

	public Boolean canAttack(Creature attacker, Creature target)
	{
		return true;
	}

	public boolean checkPvP(Creature attacker, Creature target)
	{
		return getState() != 2;
	}

	public boolean canUseItem(Player actor, ItemInstance item)
	{
		return true;
	}

	public boolean canUseSkill(Creature caster, Creature target, Skill skill)
	{
		return true;
	}

	public abstract void onLogout(Player paramPlayer);

	public abstract void doDie(Creature paramCreature1, Creature paramCreature2);

	public boolean canTeleportOnDie(Player player)
	{
		return getState() != 2;
	}

	public boolean canLostExpOnDie()
	{
		return getState() != 2;
	}

	public int getCountPlayers()
	{
		return 0;
	}

	public StringBuffer getInformation(Player player)
	{
		return null;
	}

	public boolean talkWithNpc(Player player, NpcInstance npc)
	{
		return false;
	}

	public static void unRide(Player player)
	{
		Functions.unRide(player);
	}

	public static void unSummonPet(Player player, boolean onlyPets)
	{
		Functions.unSummonPet(player, onlyPets);
	}

	public Player getSelf()
	{
		return self.get();
	}

	public NpcInstance getNpc()
	{
		return npc.get();
	}
}