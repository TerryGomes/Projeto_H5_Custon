package l2f.gameserver.model.instances.residences;

import java.util.Set;

import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Spawner;
import l2f.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 5:47/07.06.2011
 */
public abstract class SiegeToggleNpcInstance extends NpcInstance
{
	private NpcInstance _fakeInstance;
	private int _maxHp;

	public SiegeToggleNpcInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		setHasChatWindow(false);
	}

	public void setMaxHp(int maxHp)
	{
		_maxHp = maxHp;
	}

	public void setZoneList(Set<String> set)
	{

	}

	public void register(Spawner spawn)
	{

	}

	public void initFake(int fakeNpcId)
	{
		_fakeInstance = NpcHolder.getInstance().getTemplate(fakeNpcId).getNewInstance();
		_fakeInstance.setCurrentHpMp(1, _fakeInstance.getMaxMp());
		_fakeInstance.setHasChatWindow(false);
	}

	public abstract void onDeathImpl(Creature killer);

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		setCurrentHp(Math.max(getCurrentHp() - damage, 0), false);

		if (getCurrentHp() < 0.5)
		{
			doDie(attacker);

			onDeathImpl(attacker);

			decayMe();

			_fakeInstance.spawnMe(getLoc());
		}
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		if (attacker == null)
		{
			return false;
		}
		Player player = attacker.getPlayer();
		if (player == null)
		{
			return false;
		}

		SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
		if (siegeEvent == null || !siegeEvent.isInProgress() || (siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, player.getClan()) != null)
					|| siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(player.getObjectId()))
		{
			return false;
		}

		return true;
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return isAutoAttackable(attacker);
	}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public boolean hasRandomAnimation()
	{
		return false;
	}

	@Override
	public boolean isFearImmune()
	{
		return true;
	}

	@Override
	public boolean isParalyzeImmune()
	{
		return true;
	}

	@Override
	public boolean isLethalImmune()
	{
		return true;
	}

	public void decayFake()
	{
		_fakeInstance.decayMe();
	}

	@Override
	public int getMaxHp()
	{
		return _maxHp;
	}

	@Override
	protected void onDecay()
	{
		decayMe();

		_spawnAnimation = 2;
	}

	@Override
	public Clan getClan()
	{
		return null;
	}
}
