package ai.freya;

import java.util.List;

import l2mv.commons.threading.RunnableImpl;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.Fighter;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.Zone.ZoneType;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.tables.SkillTable;

public class Maguen extends Fighter
{
	private static final int[] maguenStatsSkills =
	{
		6343,
		6365,
		6366
	};
	private static final int[] maguenRaceSkills =
	{
		6367,
		6368,
		6369
	};

	public Maguen(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		ThreadPoolManager.getInstance().schedule(new Plasma(), 2000L);
		ThreadPoolManager.getInstance().schedule(new Despawn(), 10000L);
		List<Creature> around = getActor().getAroundCharacters(800, 300);
		if (!getActor().isInZone(ZoneType.dummy) && around != null && !around.isEmpty())
		{
			ExShowScreenMessage sm = new ExShowScreenMessage(NpcString.MAGUEN_APPEARANCE, 5000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true);
			for (Creature character : around)
			{
				if (character.isPlayer())
				{
					character.sendPacket(sm);
				}
			}
		}
	}

	@Override
	protected void onEvtSeeSpell(Skill skill, Creature caster)
	{
		if (skill.getId() != 9060)
		{
			return;
		}
		NpcInstance actor = getActor();
		if (actor.isInZone(ZoneType.dummy))
		{
			switch (actor.getNpcState())
			{
			case 1:
				if (Rnd.chance(80))
				{
					actor.doCast(SkillTable.getInstance().getInfo(maguenRaceSkills[0], Rnd.get(2, 3)), caster, true);
				}
				else
				{
					actor.doCast(SkillTable.getInstance().getInfo(maguenStatsSkills[0], Rnd.get(1, 2)), caster, true);
				}
				break;
			case 2:
				if (Rnd.chance(80))
				{
					actor.doCast(SkillTable.getInstance().getInfo(maguenRaceSkills[1], Rnd.get(2, 3)), caster, true);
				}
				else
				{
					actor.doCast(SkillTable.getInstance().getInfo(maguenStatsSkills[1], Rnd.get(1, 2)), caster, true);
				}
				break;
			case 3:
				if (Rnd.chance(80))
				{
					actor.doCast(SkillTable.getInstance().getInfo(maguenRaceSkills[2], Rnd.get(2, 3)), caster, true);
				}
				else
				{
					actor.doCast(SkillTable.getInstance().getInfo(maguenStatsSkills[2], Rnd.get(1, 2)), caster, true);
				}
				break;
			default:
				break;
			}
		}
		else
		{
			switch (actor.getNpcState())
			{
			case 1:
				actor.doCast(SkillTable.getInstance().getInfo(maguenRaceSkills[0], 1), caster, true);
				break;
			case 2:
				actor.doCast(SkillTable.getInstance().getInfo(maguenRaceSkills[1], 1), caster, true);
				break;
			case 3:
				actor.doCast(SkillTable.getInstance().getInfo(maguenRaceSkills[2], 1), caster, true);
				break;
			default:
				break;
			}
		}
		getActor().setNpcState(4);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if ((attacker == null) || attacker.isPlayable())
		{
			return;
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected boolean checkAggression(Creature target, boolean avoidAttack)
	{
		if (target.isPlayable())
		{
			return false;
		}

		return super.checkAggression(target, avoidAttack);
	}

	private class Plasma extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			getActor().setNpcState(Rnd.get(1, 3));
		}
	}

	private class Despawn extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			getActor().setNpcState(4);
			getActor().doDie(null);
		}
	}
}