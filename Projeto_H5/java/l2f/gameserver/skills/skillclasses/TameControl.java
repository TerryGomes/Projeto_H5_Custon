package l2f.gameserver.skills.skillclasses;

import java.util.List;

import l2f.gameserver.Config;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.instances.TamedBeastInstance;
import l2f.gameserver.templates.StatsSet;

public class TameControl extends Skill
{
	private final int _type;

	public TameControl(StatsSet set)
	{
		super(set);
		_type = set.getInteger("type", 0);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{

		if (isSSPossible())
		{
			activeChar.unChargeShots(isMagic());
		}

		if (!activeChar.isPlayer())
		{
			return;
		}

		Player player = activeChar.getPlayer();
		if (player.getTrainedBeasts() == null)
		{
			return;
		}

		if (_type == 0)
		{
			for (Creature target : targets)
			{
				if (target != null && target instanceof TamedBeastInstance)
				{
					if (player.getTrainedBeasts().get(target.getObjectId()) != null)
					{
						((TamedBeastInstance) target).despawnWithDelay(1000);
					}
				}
			}
		}
		else if (_type > 0)
		{
			switch (_type)
			{
			case 1:
				for (TamedBeastInstance tamedBeast : player.getTrainedBeasts().values())
				{
					tamedBeast.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, Config.FOLLOW_RANGE);
				}
				break;
			case 3:
				for (TamedBeastInstance tamedBeast : player.getTrainedBeasts().values())
				{
					tamedBeast.buffOwner();
				}
				break;
			case 4:
				for (TamedBeastInstance tamedBeast : player.getTrainedBeasts().values())
				{
					tamedBeast.doDespawn();
				}
				break;
			default:
				break;
			}
		}
	}
}