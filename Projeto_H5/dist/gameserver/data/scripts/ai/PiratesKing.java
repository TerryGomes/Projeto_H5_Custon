package ai;

import events.PiratesTreasure.PiratesTreasure;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Playable;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.utils.ItemFunctions;

/**
 * @author l2f.Team
 */
public class PiratesKing extends Fighter
{

	public PiratesKing(NpcInstance actor)
	{
		super(actor);
	}

	private boolean isFind = false;
	long _wait_timeout = 0;
	private boolean isFirst = true;

	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		actor.setTargetable(false); // Not yet found a pirate, take it to the Target can not be

		ThreadPoolManager.getInstance().schedule(new RunnableImpl() // The problem of it OnDespawn
		{
			@Override
			public void runImpl() throws Exception
			{
				NpcInstance actor = getActor();
				actor.deleteMe();
			}
		}, 30 * 60000); // 30 minutes to find and kill
		super.onEvtSpawn();
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null || actor.isDead())
		{
			return true;
		}

		if (_wait_timeout < System.currentTimeMillis() && !isFind)
		{
			_wait_timeout = System.currentTimeMillis() + 60000;
			ThreadPoolManager.getInstance().schedule(new RunnableImpl()
			{
				@Override
				public void runImpl() throws Exception
				{
					PiratesTreasure.annoncePointInfo();
				}
			}, 60000);
			return true;
		}
		if (PiratesTreasure.eventStoped) // likely delirium
		{
			actor.deleteMe();
		}
		return super.thinkActive();
	}

	@Override
	protected void onIntentionAttack(Creature target)
	{
		NpcInstance actor = getActor();
		actor.setTargetable(true);

		if (isFirst)
		{
			ItemFunctions.addItem((Playable) target, 6673, 5, true, "PirateKing"); // Awards first finder
			isFirst = false; // protection against cheating
			Announcements.getInstance().announceToAll("The Pirate King of Darkness founded!");
		}
		isFind = true;
		super.onIntentionAttack(target);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		Announcements.getInstance().announceToAll("The Pirate King of Darkness is defeated!");
		super.onEvtDead(killer);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	@Override
	protected boolean randomAnimation()
	{
		return false;
	}

	@Override
	protected boolean canSeeInSilentMove(Playable target)
	{
		return true;
	}

	@Override
	protected boolean canSeeInHide(Playable target)
	{
		return true;
	}

}
