package ai;

import java.util.List;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.templates.npc.NpcTemplate;

/**
 * Author: VISTALL
 * Date:  9:03/17.11.2010
 * npc Id : 18602
 */
public class KrateisCubeWatcherBlue extends DefaultAI
{
	private static final int RESTORE_CHANCE = 60;

	public KrateisCubeWatcherBlue(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ACTIVE_DELAY = 3000;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtThink()
	{
		NpcInstance actor = getActor();
		List<Creature> around = World.getAroundCharacters(actor, 600, 300);
		if (around.isEmpty())
		{
			return;
		}

		for (Creature cha : around)
		{
			if (cha.isPlayer() && !cha.isDead() && Rnd.chance(RESTORE_CHANCE))
			{
				double valCP = cha.getMaxCp() - cha.getCurrentCp();
				if (valCP > 0)
				{
					cha.setCurrentCp(valCP + cha.getCurrentCp());
					cha.sendPacket(new SystemMessage2(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addInteger(Math.round(valCP)));
				}

				double valHP = cha.getMaxHp() - cha.getCurrentHp();
				if (valHP > 0)
				{
					cha.setCurrentHp(valHP + cha.getCurrentHp(), false);
					cha.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(valHP)));
				}

				double valMP = cha.getMaxMp() - cha.getCurrentMp();
				if (valMP > 0)
				{
					cha.setCurrentMp(valMP + cha.getCurrentMp());
					cha.sendPacket(new SystemMessage2(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(valMP)));
				}
			}
		}
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		final NpcInstance actor = getActor();
		super.onEvtDead(killer);

		actor.deleteMe();
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				NpcTemplate template = NpcHolder.getInstance().getTemplate(18601);
				if (template != null)
				{
					NpcInstance a = template.getNewInstance();
					a.setCurrentHpMp(a.getMaxHp(), a.getMaxMp());
					a.spawnMe(actor.getLoc());
				}
			}
		}, 10000L);
	}
}
