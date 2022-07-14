package ai;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;

public class LafiLakfi extends DefaultAI
{
	private static final int MAX_RADIUS = 500;
	private static final Skill s_display_bug_of_fortune1 = SkillTable.getInstance().getInfo(6045, 1);
	private static final Skill s_display_jackpot_firework = SkillTable.getInstance().getInfo(5778, 1);

	private long _nextEat;
	private int i_ai2, actor_lvl, prev_st;
	private boolean _firstSaid;

	public LafiLakfi(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		NpcInstance actor = getActor();

		addTimer(7778, 1000);

		if (getFirstSpawned(actor))
		{
			i_ai2 = 0;
			prev_st = 0;
		}
		else
		{
			i_ai2 = 3;
			prev_st = 3;
		}
		_firstSaid = false;

		actor_lvl = actor.getLevel();
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

		if (i_ai2 > 9)
		{
			if (!_firstSaid)
			{
				Functions.npcSayInRange(actor, 600, NpcString.IM_FULL_NOW_I_DONT_WANT_TO_EAT_ANYMORE);
				_firstSaid = true;
			}
			return;
		}
		ItemInstance closestItem = null;
		if (_nextEat < System.currentTimeMillis())
		{
			for (GameObject obj : World.getAroundObjects(actor, 20, 200))
			{
				if (obj.isItem() && ((ItemInstance) obj).getItemId() == 57)
				{
					closestItem = (ItemInstance) obj;
				}
			}

			if (closestItem != null && closestItem.getCount() >= 15000)
			{
				closestItem.deleteMe();
				actor.altUseSkill(s_display_bug_of_fortune1, actor);
				Functions.npcSayInRange(actor, 600, NpcString.YUMYUM_YUMYUM);
				_firstSaid = false;

				if (i_ai2 == 2 && getFirstSpawned(actor))
				{
					NpcInstance npc = NpcHolder.getInstance().getTemplate(getCurrActor(actor)).getNewInstance();
					npc.setLevel(actor.getLevel());
					npc.setSpawnedLoc(actor.getLoc());
					npc.setReflection(actor.getReflection());
					npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
					npc.spawnMe(npc.getSpawnedLoc());
					actor.doDie(actor);
					actor.deleteMe();
					addTimer(1500, 0, null, 60 * 60000); // config me
				}

				i_ai2++;

				_nextEat = System.currentTimeMillis() + 60 * 1000;
			}

			else if (closestItem != null && closestItem.getCount() < 15000 && !_firstSaid)
			{
				Functions.npcShout(actor, "Is this all? I want More!!! I won't eat below 15.000 Adena!!!");
				_firstSaid = true;
			}
		}
	}

	private boolean getFirstSpawned(NpcInstance actor)
	{
		if (actor.getNpcId() == 2503 || actor.getNpcId() == 2502)
		{
			return false;
		}
		return true;
	}

	private int getCurrActor(NpcInstance npc)
	{
		if (Rnd.chance(20))
		{
			return 2503;
		}
		return 2502;

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
				if (obj.isItem() && ((ItemInstance) obj).getItemId() == 57)
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

	public int getChance(int stage)
	{
		switch (stage)
		{
		case 4:
			return 10;
		case 5:
			return 20;
		case 6:
			return 40;
		case 7:
			return 60;
		case 8:
			return 70;
		case 9:
			return 80;
		case 10:
			return 100;
		default:
			return 0;
		}
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

		if (killer != null)
		{
			if (i_ai2 >= 0 && i_ai2 < 3)
			{
				Functions.npcSayInRange(actor, 600, NpcString.I_HAVENT_EATEN_ANYTHING_IM_SO_WEAK);
				return;
			}
			else
			{
				actor.broadcastPacket(new MagicSkillUse(actor, s_display_jackpot_firework.getId(), 1, s_display_jackpot_firework.getHitTime(actor), 0));
			}

			int _chance = getChance(i_ai2);

			if (Rnd.chance(_chance))
			{
				int random = Rnd.get(0, 100);

				switch (actor_lvl)
				{
				case 52:
					if (actor.getNpcId() == 2502)
					{

						if (random <= 50)
						{
							actor.dropItem(killer.getPlayer(), 8755, 1);
						}
						else
						{
							actor.dropItem(killer.getPlayer(), 8755, 2);
						}
					}

					else if (actor.getNpcId() == 2503)
					{
						actor.dropItem(killer.getPlayer(), 14678, 1);
					}
					break;
				case 70:
					if (actor.getNpcId() == 2502)
					{

						if (random <= 16)
						{
							actor.dropItem(killer.getPlayer(), 5577, 1);
						}
						else if (random > 16 && random < 32)
						{
							actor.dropItem(killer.getPlayer(), 5578, 1);
						}
						else if (random > 32 && random < 48)
						{
							actor.dropItem(killer.getPlayer(), 5579, 2);
						}
						else if (random > 48 && random < 64)
						{
							actor.dropItem(killer.getPlayer(), 5577, 1);
						}
						else if (random > 64 && random < 80)
						{
							actor.dropItem(killer.getPlayer(), 5578, 1);
						}
						else if (random > 80)
						{
							actor.dropItem(killer.getPlayer(), 5579, 1);
						}

					}

					else if (actor.getNpcId() == 2503)
					{
						actor.dropItem(killer.getPlayer(), 14679, 1);
					}
					break;
				case 80:
					if (actor.getNpcId() == 2502)
					{

						if (random <= 8)
						{
							actor.dropItem(killer.getPlayer(), 9552, 1);
						}
						else if (random > 8 && random < 16)
						{
							actor.dropItem(killer.getPlayer(), 9552, 2);
						}
						else if (random > 16 && random < 24)
						{
							actor.dropItem(killer.getPlayer(), 9554, 1);
						}
						else if (random > 24 && random < 32)
						{
							actor.dropItem(killer.getPlayer(), 9554, 2);
						}
						else if (random > 32 && random < 40)
						{
							actor.dropItem(killer.getPlayer(), 9556, 1);
						}
						else if (random > 40 && random < 48)
						{
							actor.dropItem(killer.getPlayer(), 9556, 2);
						}
						else if (random > 48 && random <= 56)
						{
							actor.dropItem(killer.getPlayer(), 9553, 1);
						}
						else if (random > 56 && random < 64)
						{
							actor.dropItem(killer.getPlayer(), 9553, 2);
						}
						else if (random > 64 && random < 72)
						{
							actor.dropItem(killer.getPlayer(), 9555, 1);
						}
						else if (random > 72 && random < 80)
						{
							actor.dropItem(killer.getPlayer(), 9555, 2);
						}
						else if (random > 80 && random < 90)
						{
							actor.dropItem(killer.getPlayer(), 9557, 1);
						}
						else if (random > 90)
						{
							actor.dropItem(killer.getPlayer(), 9557, 2);
						}

					}

					else if (actor.getNpcId() == 2503)
					{
						actor.dropItem(killer.getPlayer(), 14680, 1);
					}
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	protected void onEvtTimer(int timerId, Object arg1, Object arg2)
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		if (timerId == 7778)
		{
			switch (i_ai2)
			{
			case 0:
				Functions.npcSayInRange(actor, 600, NpcString.IF_YOU_HAVE_ITEMS_PLEASE_GIVE_THEM_TO_ME);
				break;
			case 1:
				Functions.npcSayInRange(actor, 600, NpcString.MY_STOMACH_IS_EMPTY);
				break;
			case 2:
				Functions.npcSayInRange(actor, 600, NpcString.IM_HUNGRY_IM_HUNGRY);
				break;
			case 3:
				Functions.npcSayInRange(actor, 600, NpcString.I_FEEL_A_LITTLE_WOOZY);
				break;
			case 4:
				Functions.npcSayInRange(actor, 600, NpcString.IM_STILL_NOT_FULL);
				break;
			case 5:
				Functions.npcSayInRange(actor, 600, NpcString.IM_STILL_HUNGRY);
				break;
			case 6:
				Functions.npcSayInRange(actor, 600, NpcString.NOW_ITS_TIME_TO_EAT);
				break;
			case 7:
				Functions.npcSayInRange(actor, 600, NpcString.GIVE_ME_SOMETHING_TO_EAT);
				break;
			case 8:
				Functions.npcSayInRange(actor, 600, NpcString.IM_STILL_HUNGRY_);
				break;
			case 9:
				Functions.npcSayInRange(actor, 600, NpcString.I_ALSO_NEED_A_DESSERT);
				break;
			case 10:
				Functions.npcSayInRange(actor, 600, NpcString.IM_FULL_NOW_I_DONT_WANT_TO_EAT_ANYMORE);
				break;
			}

			addTimer(7778, 10000 + Rnd.get(10) * 1000);
		}

		if (timerId == 1500)
		{
			if (prev_st == i_ai2 && prev_st != 0 && i_ai2 != 10)
			{
				actor.doDie(actor);
			}
			else
			{
				prev_st = i_ai2;
				addTimer(1500, 60 * 60000);
			}

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
