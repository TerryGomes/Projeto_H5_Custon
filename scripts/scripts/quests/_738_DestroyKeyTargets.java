package quests;

import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.network.serverpackets.components.NpcString;

/**
 * @author VISTALL
 * @date 16:23/12.04.2011
 */
public class _738_DestroyKeyTargets extends Dominion_KillSpecialUnitQuest
{
	public _738_DestroyKeyTargets()
	{
		super();
	}

	@Override
	protected NpcString startNpcString()
	{
		return NpcString.DEFEAT_S1_WARSMITHS_AND_OVERLORDS;
	}

	@Override
	protected NpcString progressNpcString()
	{
		return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_WARSMITHS_AND_OVERLORDS;
	}

	@Override
	protected NpcString doneNpcString()
	{
		return NpcString.YOU_DESTROYED_THE_ENEMYS_PROFESSIONALS;
	}

	@Override
	protected int getRandomMin()
	{
		return 3;
	}

	@Override
	protected int getRandomMax()
	{
		return 8;
	}

	@Override
	protected ClassId[] getTargetClassIds()
	{
		return new ClassId[]
		{
			ClassId.necromancer,
			ClassId.swordSinger,
			ClassId.bladedancer,
			ClassId.overlord,
			ClassId.warsmith,
			ClassId.soultaker,
			ClassId.swordMuse,
			ClassId.spectralDancer,
			ClassId.dominator,
			ClassId.maestro,
			ClassId.inspector,
			ClassId.judicator
		};
	}
}
