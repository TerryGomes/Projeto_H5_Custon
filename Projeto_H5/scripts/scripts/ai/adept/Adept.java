package ai.adept;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Location;

public class Adept extends DefaultAI
{
	protected Location[] _points;
	private int _lastPoint = 0;

	public Adept(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public boolean isGlobalAI()
	{
		return true;
	}

	@Override
	protected boolean thinkActive()
	{
		if (!_def_think)
		{
			startMoveTask();
		}
		return true;
	}

	@Override
	protected void onEvtArrived()
	{
		startMoveTask();
		if (Rnd.chance(30))
		{
			sayRndMsg();
		}
		super.onEvtArrived();
	}

	private void startMoveTask()
	{
		_lastPoint++;
		if (_lastPoint >= _points.length)
		{
			_lastPoint = 0;
		}
		addTaskMove(_points[_lastPoint], false);
		doTask();
	}

	private void sayRndMsg()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}
		if (Config.ADEPT_ENABLE)
		{
			NpcString ns;
			switch (Rnd.get(7))
			{
			case 1:
				ns = NpcString.CLOUDS_OF_BLOOD_ARE_GATHERING_SOON_IT_WILL_START_TO_RAIN_THE_RAIN_OF_CRIMSON_BLOOD;
				break;
			case 2:
				ns = NpcString.WHILE_THE_FOOLISH_LIGHT_WAS_ASLEEP_THE_DARKNESS_WILL_AWAKEN_FIRST_UH;
				break;
			case 3:
				ns = NpcString.IT_IS_THE_DEEPEST_DARKNESS_WITH_ITS_ARRIVAL_THE_WORLD_WILL_SOON_DIE;
				break;
			case 4:
				ns = NpcString.DEATH_IS_JUST_A_NEW_BEGINNING_HUHU_FEAR_NOT;
				break;
			case 5:
				ns = NpcString.AHH_BEAUTIFUL_GODDES_OF_DEATH_COVER_OVER_THE_FILTH_OF_THOS_WORLD_YOUR_DARKNESS;
				break;
			case 6:
				ns = NpcString.THE_GODDESS_RESURRECTION_HAS_ALREADY_BEGUN_HUHU_INSIGNIFICANT_CREATURES_LIKE_YOU_CAN_DO_NOTHING;
				break;
			default:
				ns = NpcString.A_BLACK_MOON_NOW_DO_YOU_UNDERSTAND_THAT_HE_HAS_OPENED_HIS_EYES;
				break;
			}
			Functions.npcSay(actor, ns);
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