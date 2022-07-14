package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.NpcUtils;

public class FortuneBug extends DefaultAI
{
	private static final int MAX_RADIUS = 500;

	private static final Skill s_display_bug_of_fortune1 = SkillTable.getInstance().getInfo(6045, 1);
	private static final Skill s_display_jackpot_firework = SkillTable.getInstance().getInfo(5778, 1);

	private int Wingless_Luckpy = 2502;
	private int Wingless_Luckpy_Gold = 2503;

	private int[] Cristall =
	{
		9552,
		9553,
		9554,
		9555,
		9556,
		9557
	};
	private int[] Cristall_Dush =
	{
		5577,
		5578,
		5579
	};

	private long _nextEat;
	@SuppressWarnings("unused")
	private int i_ai0, i_ai1, i_ai2;

	public FortuneBug(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();
		addTimer(7778, 1000);
		i_ai0 = i_ai1 = i_ai2 = 0;
	}

	@Override
	protected void onEvtArrived()
	{
		super.onEvtArrived();
		NpcInstance actor = getActor();
		if ((actor == null) || actor.getNpcId() == Wingless_Luckpy || actor.getNpcId() == Wingless_Luckpy_Gold)
		{
			return;
		}

		ItemInstance closestItem = null;
		if (_nextEat < System.currentTimeMillis())
		{
			for (GameObject obj : World.getAroundObjects(actor, 20, 200))
			{
				if (obj.isItem() && ((ItemInstance) obj).isStackable() && ((ItemInstance) obj).isAdena())
				{
					closestItem = (ItemInstance) obj;
				}
			}

			if (closestItem != null)
			{
				closestItem.deleteMe();
				actor.altUseSkill(s_display_bug_of_fortune1, actor);
				Functions.npcSayInRange(actor, 600, NpcString.YUMYUM_YUMYUM);

				i_ai0++;
				if (i_ai0 > 3 && i_ai0 <= 4)
				{
					i_ai1 = 20;
				}
				else if (i_ai0 > 4 && i_ai0 <= 6)
				{
					i_ai1 = 30;
				}
				else if (i_ai0 > 6 && i_ai0 <= 8)
				{
					i_ai1 = 50;
				}
				else if (i_ai0 > 8 && i_ai0 < 10)
				{
					i_ai1 = 80;
				}
				else if (i_ai0 >= 10)
				{
					i_ai1 = 100;
				}

				if (Rnd.chance(i_ai1))
				{
					final NpcInstance npc = NpcUtils.spawnSingle(Rnd.chance(30) ? Wingless_Luckpy : Wingless_Luckpy_Gold, actor.getLoc(), actor.getReflection());

					switch (actor.getLevel())
					{
					case 52:
						npc.addSkill(SkillTable.getInstance().getInfo(24009, 1));
						break;
					case 70:
						npc.addSkill(SkillTable.getInstance().getInfo(24009, 2));
						break;
					case 80:
						npc.addSkill(SkillTable.getInstance().getInfo(24009, 3));
						break;
					}
					npc.setLevel(actor.getLevel());
					npc.altUseSkill(s_display_jackpot_firework, npc);
					actor.deleteMe();
				}
				_nextEat = System.currentTimeMillis() + 10000;
			}
		}
		else if (_nextEat + 10 * 60 * 1000 <= System.currentTimeMillis())
		{
			actor.deleteMe();
		}
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if (actor == null || actor.isDead() || actor.getNpcId() == Wingless_Luckpy || actor.getNpcId() == Wingless_Luckpy_Gold)
		{
			return true;
		}

		if (!actor.isMoving && _nextEat < System.currentTimeMillis())
		{
			ItemInstance closestItem = null;
			for (GameObject obj : World.getAroundObjects(actor, MAX_RADIUS, 200))
			{
				if (obj.isItem() && ((ItemInstance) obj).isStackable() && ((ItemInstance) obj).isAdena())
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

	@Override
	protected void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);
		NpcInstance actor = getActor();

		if (actor == null)
		{
			return;
		}

		int lvl = actor.getLevel();

		Player player = killer.getPlayer();
		if (player != null)
		{
			if (actor.getNpcId() == Wingless_Luckpy)
			{
				switch (lvl)
				{
				case 52:
					actor.dropItem(killer.getPlayer(), 8755, Rnd.get(1, 2));
					return;
				case 70:
					actor.dropItem(killer.getPlayer(), Cristall_Dush[Rnd.get(3)], Rnd.get(1, 2));
					return;
				case 80:
					actor.dropItem(killer.getPlayer(), Cristall[Rnd.get(6)], Rnd.get(1, 2));
					return;
				}
			}
			if (actor.getNpcId() == Wingless_Luckpy_Gold)
			{
				switch (lvl)
				{
				case 52:
					actor.dropItem(killer.getPlayer(), 8755, Rnd.get(1, 2));
					actor.dropItem(killer.getPlayer(), 14678, 1);
					return;
				case 70:
					actor.dropItem(killer.getPlayer(), Cristall_Dush[Rnd.get(3)], Rnd.get(1, 2));
					actor.dropItem(killer.getPlayer(), 14679, 1);
					return;
				case 80:
					actor.dropItem(killer.getPlayer(), Cristall[Rnd.get(6)], Rnd.get(1, 2));
					actor.dropItem(killer.getPlayer(), 14680, 1);
					return;
				}
			}
		}
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		NpcInstance actor = getActor();
		if ((actor == null) || actor.getNpcId() == Wingless_Luckpy || actor.getNpcId() == Wingless_Luckpy_Gold)
		{
			return;
		}

		if (timerId == 7778)
		{
			switch (i_ai0)
			{
			case 0:
				Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.IF_YOU_HAVE_ITEMS_PLEASE_GIVE_THEM_TO_ME : NpcString.MY_STOMACH_IS_EMPTY);
				break;
			case 1:
				Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.IM_HUNGRY_IM_HUNGRY : NpcString.IM_STILL_NOT_FULL);
				break;
			case 2:
				Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.IM_STILL_HUNGRY : NpcString.I_FEEL_A_LITTLE_WOOZY);
				break;
			case 3:
				Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.GIVE_ME_SOMETHING_TO_EAT : NpcString.NOW_ITS_TIME_TO_EAT);
				break;
			case 4:
				Functions.npcSayInRange(actor, 600, Rnd.chance(50) ? NpcString.I_ALSO_NEED_A_DESSERT : NpcString.IM_STILL_HUNGRY_);
				break;
			case 5:
				Functions.npcSayInRange(actor, 600, NpcString.IM_FULL_NOW_I_DONT_WANT_TO_EAT_ANYMORE);
				break;
			}
			addTimer(7778, 10000 + Rnd.get(10) * 1000);
		}
		else
		{
			super.onEvtTimer(timerId, arg1, arg2);
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
	}

	@Override
	protected void onEvtAggression(Creature target, int aggro)
	{
	}
}
