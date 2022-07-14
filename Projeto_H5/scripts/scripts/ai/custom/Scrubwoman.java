package ai.custom;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Location;

/**
 * @autor 4ipolino
 * AI уборщицы
 * <br> - Собирает все предметы в радиусе MAX_RADIUS
 * <br> - убивает всех кто нанес удар (_firstTimeAttacked = true;) с выводом сообщения
 */
public class Scrubwoman extends DefaultAI
{
	private static final int MAX_RADIUS = 900;
	private long _nextEat;
	protected Location[] _points;

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected void onEvtArrived()
	{
		super.onEvtArrived();
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		ItemInstance closestItem = null;
		if (_nextEat < System.currentTimeMillis())
		{
			for (GameObject obj : World.getAroundObjects(actor, MAX_RADIUS, 200))
			{
				if (obj.isItem() && ((ItemInstance) obj).getItemId() > 1)
				{
					closestItem = (ItemInstance) obj;
				}
			}

			if (closestItem != null)
			{
				closestItem.deleteMe();
				Functions.npcSay(actor, "What a mess! Already throw things! Neither shame nor conscience!");

				if (getFirstSpawned(actor))
				{
					NpcInstance npc = NpcHolder.getInstance().getTemplate(getCurrActor(actor)).getNewInstance();
					npc.setLevel(actor.getLevel());
					npc.setSpawnedLoc(actor.getLoc());
					npc.setReflection(actor.getReflection());
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
					npc.spawnMe(npc.getSpawnedLoc());
					actor.doDie(actor);
					actor.deleteMe();
				}
				_nextEat = System.currentTimeMillis() + 1 * 1000;
			}

		}
	}

	private int getCurrActor(NpcInstance npc)
	{
		if (Rnd.chance(90))
		{
			return 18583;
		}
		return 18583;

	}

	public Scrubwoman(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 5000; // 30 sek
	}

	private boolean getFirstSpawned(NpcInstance actor)
	{
		if (actor.getNpcId() == 18583)
		{
			return false;
		}
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null || actor.isDead())
		{
			return true;
		}
		if (!actor.isMoving && _nextEat < System.currentTimeMillis())
		{
			ItemInstance closestItem = null;
			for (GameObject obj : World.getAroundObjects(actor, MAX_RADIUS, 200))
			{
				if (obj.isItem() && ((ItemInstance) obj).getItemId() > 1)
				{
					closestItem = (ItemInstance) obj;
				}
			}

			if (closestItem != null)
			{
				actor.moveToLocation(closestItem.getLoc(), 0, true);
			}
		}

		return false;
	}
}