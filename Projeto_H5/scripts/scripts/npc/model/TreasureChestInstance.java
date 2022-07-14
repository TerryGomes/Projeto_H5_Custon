package npc.model;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.ChestInstance;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class TreasureChestInstance extends ChestInstance
{
	private static final int TREASURE_BOMB_ID = 4143;

	public TreasureChestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void tryOpen(Player opener, Skill skill)
	{
		double chance = calcChance(opener, skill);
		if (Rnd.chance(chance))
		{
			getAggroList().addDamageHate(opener, 10000, 0);
			doDie(opener);
		}
		else
		{
			fakeOpen(opener);
		}
	}

	public double calcChance(Player opener, Skill skill)
	{

		double chance = skill.getActivateRate();
		int npcLvl = getLevel();
		if (!isCommonTreasureChest())
		{
			double levelmod = (double) skill.getMagicLevel() - npcLvl;
			chance += levelmod * skill.getLevelModifier();
			if (npcLvl - opener.getLevel() >= 5) // Custom way to prevent low level players opening top level chests.
			{
				chance += (opener.getLevel() - npcLvl) * 10; // 10% penalty for each next level.
			}
		}
		else
		{
			int openerLvl = opener.getLevel();
			int lvlDiff = Math.max(openerLvl - npcLvl, 0);
			if ((openerLvl <= 77 && lvlDiff >= 6) || (openerLvl >= 78 && lvlDiff >= 5))
			{
				chance = 0;
			}
		}
		if (chance < 0)
		{
			chance = 1;
		}
		return chance;
	}

	private void fakeOpen(Creature opener)
	{
		Skill bomb = SkillTable.getInstance().getInfo(TREASURE_BOMB_ID, getBombLvl());
		if (bomb != null)
		{
			doCast(bomb, opener, false);
		}
		onDecay();
	}

	private int getBombLvl()
	{
		int npcLvl = getLevel();
		int lvl = 1;
		if (npcLvl >= 78)
		{
			lvl = 10;
		}
		else if (npcLvl >= 72)
		{
			lvl = 9;
		}
		else if (npcLvl >= 66)
		{
			lvl = 8;
		}
		else if (npcLvl >= 60)
		{
			lvl = 7;
		}
		else if (npcLvl >= 54)
		{
			lvl = 6;
		}
		else if (npcLvl >= 48)
		{
			lvl = 5;
		}
		else if (npcLvl >= 42)
		{
			lvl = 4;
		}
		else if (npcLvl >= 36)
		{
			lvl = 3;
		}
		else if (npcLvl >= 30)
		{
			lvl = 2;
		}
		return lvl;
	}

	private boolean isCommonTreasureChest()
	{
		int npcId = getNpcId();
		if (npcId >= 18265 && npcId <= 18286)
		{
			return true;
		}
		return false;
	}

	@Override
	public void onReduceCurrentHp(final double damage, final Creature attacker, Skill skill, final boolean awake, final boolean standUp, boolean directHp)
	{
		if (!isCommonTreasureChest())
		{
			fakeOpen(attacker);
		}
	}

	@Override
	public boolean isTreasureChest()
	{
		return true;
	}
}