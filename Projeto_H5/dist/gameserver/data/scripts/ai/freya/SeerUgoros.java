package ai.freya;

import l2mv.gameserver.ai.CtrlEvent;
import l2mv.gameserver.ai.Mystic;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;
import quests._288_HandleWithCare;

public class SeerUgoros extends Mystic
{
	private int _weeds = 0;
	private static final Skill _skill = SkillTable.getInstance().getInfo(6426, 1);

	public SeerUgoros(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		super.thinkActive();
		if (!getActor().getReflection().isDefault() && !getActor().getReflection().getPlayers().isEmpty())
		{
			for (Player p : getActor().getReflection().getPlayers())
			{
				notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
			}
		}
		return true;
	}

	@Override
	protected void thinkAttack()
	{
		NpcInstance actor = getActor();
		if (!actor.isMuted(_skill) && actor.getCurrentHpPercents() < 80)
		{
			for (NpcInstance n : actor.getAroundNpc(2000, 300))
			{
				if (n.getNpcId() == 18867 && !n.isDead())
				{
					actor.doCast(_skill, n, true);
					actor.setCurrentHp(actor.getMaxHp(), false);
					actor.broadcastCharInfo();
					_weeds++;
					return;
				}
			}
		}
		super.thinkAttack();
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		QuestState qs = killer.getPlayer().getQuestState(_288_HandleWithCare.class);
		if (qs != null && qs.getCond() == 1)
		{
			if (_weeds < 5)
			{
				qs.giveItems(15497, 1);
				qs.setCond(3);
			}
			else
			{
				qs.giveItems(15498, 1);
				qs.setCond(2);
			}
		}
		_weeds = 0;
		if (!getActor().getReflection().isDefault())
		{
			getActor().getReflection().addSpawnWithoutRespawn(32740, new Location(95688, 85688, -3757, 0), 0);
		}
		super.onEvtDead(killer);
	}
}