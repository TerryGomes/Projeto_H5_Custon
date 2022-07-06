package events.TheFlowOfTheHorror;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.CtrlIntention;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.MonsterInstance;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.funcs.FuncMul;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;

public class GilmoreAI extends Fighter
{
	static final Location[] points_stage1 =
	{
		new Location(73195, 118483, -3722),
		new Location(73535, 117945, -3754),
		new Location(73446, 117334, -3752),
		new Location(72847, 117311, -3711),
		new Location(72296, 117720, -3694),
		new Location(72463, 118401, -3694),
		new Location(72912, 117895, -3723)
	};

	static final Location[] points_stage2 =
	{
		new Location(73615, 117629, -3765)
	};

	static final String[] text_stage1 =
	{
		"Text1",
		"Text2",
		"Text3",
		"Text4",
		"Text5",
		"Text6",
		"Text7"
	};

	static final String[] text_stage2 =
	{
		"Are you ready? ",
		" Let's begin, there is not a moment to lose!"
	};

	private long wait_timeout = 0;
	private boolean wait = false;
	private int index;
	private int step_stage2 = 1;

	public GilmoreAI(NpcInstance actor)
	{
		super(actor);
		AI_TASK_ATTACK_DELAY = 250;
	}

	@Override
	public boolean isGlobalAI()
	{
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

		if (_def_think)
		{
			doTask();
			return true;
		}

		if (System.currentTimeMillis() > wait_timeout)
		{
			if (!wait)
			{
				switch (TheFlowOfTheHorror.getStage())
				{
				case 1:
					if (Rnd.chance(30))
					{
						index = Rnd.get(text_stage1.length);
						Functions.npcSay(actor, text_stage1[index]);
						wait_timeout = System.currentTimeMillis() + 10000;
						wait = true;
						return true;
					}
					break;
				case 2:
					switch (step_stage2)
					{
					case 1:
						Functions.npcSay(actor, text_stage2[0]);
						wait_timeout = System.currentTimeMillis() + 10000;
						wait = true;
						return true;
					case 2:
						break;
					}
					break;
				}
			}

			wait_timeout = 0;
			wait = false;

			actor.setRunning();

			switch (TheFlowOfTheHorror.getStage())
			{
			case 1:
				index = Rnd.get(points_stage1.length);
				addTaskMove(points_stage1[index], true);
				doTask();
				return true;
			case 2:
				switch (step_stage2)
				{
				case 1:
					Functions.npcSay(actor, text_stage2[1]);
					addTaskMove(points_stage2[0], true);
					doTask();
					step_stage2 = 2;
					return true;
				case 2:
					actor.setHeading(0);
					actor.stopMove();
					actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 454, 1, 3000, 0));
					step_stage2 = 3;
					return true;
				case 3:
					actor.addStatFunc(new FuncMul(Stats.MAGIC_ATTACK_SPEED, 0x40, actor, 5));
					actor.addStatFunc(new FuncMul(Stats.MAGIC_DAMAGE, 0x40, actor, 10));
					actor.addStatFunc(new FuncMul(Stats.PHYSICAL_DAMAGE, 0x40, actor, 10));
					actor.addStatFunc(new FuncMul(Stats.RUN_SPEED, 0x40, actor, 3));
					actor.addSkill(SkillTable.getInstance().getInfo(1467, 1));
					actor.sendChanges();
					step_stage2 = 4;
					return true;
				case 4:
					setIntention(CtrlIntention.AI_INTENTION_ATTACK, null);
					return true;
				case 10:
					actor.removeStatsOwner(this);
					step_stage2 = 11;
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected boolean createNewTask()
	{
		clearTasks();
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return true;
		}

		for (NpcInstance npc : World.getAroundNpc(actor, 1000, 200))
		{
			if (Rnd.chance(10) && npc != null && npc.getNpcId() == 20235)
			{
				MonsterInstance monster = (MonsterInstance) npc;
				if (Rnd.chance(20))
				{
					addTaskCast(monster, actor.getKnownSkill(1467));
				}
				else
				{
					addTaskAttack(monster);
				}
				return true;
			}
		}
		return true;
	}
}