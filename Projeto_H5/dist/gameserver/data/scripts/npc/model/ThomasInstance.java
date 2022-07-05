package npc.model;

import events.SavingSnowman.SavingSnowman;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * Данный инстанс используется мобом Thomas D. Turkey в эвенте Saving Snowman
 * @author SYS
 */
public class ThomasInstance extends MonsterInstance
{
	public ThomasInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void reduceCurrentHp(double i, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage)
	{
		i = 10;
		if (attacker.getActiveWeaponInstance() != null)
		{
			switch (attacker.getActiveWeaponInstance().getItemId())
			{
			// Хроно оружие наносит больший урон
			case 4202: // Chrono Cithara
			case 5133: // Chrono Unitus
			case 5817: // Chrono Campana
			case 7058: // Chrono Darbuka
			case 8350: // Chrono Maracas
				i = 100;
				break;
			default:
				i = 10;
			}
		}

		super.reduceCurrentHp(i, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		Creature topdam = getAggroList().getTopDamager();
		if (topdam == null)
		{
			topdam = killer;
		}
		SavingSnowman.freeSnowman(topdam);
		super.onDeath(killer);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}