package ai.residences.castle;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.NpcString;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.NpcUtils;

/**
 * @author VISTALL
 * @date 22:01/23.05.2011
 * 29054
 */
public class Venom extends Fighter
{
	public Venom(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	public void onEvtSpawn()
	{
		super.onEvtSpawn();

		Functions.npcShout(getActor(), NpcString.WHO_DARES_TO_COVET_THE_THRONE_OF_OUR_CASTLE__LEAVE_IMMEDIATELY_OR_YOU_WILL_PAY_THE_PRICE_OF_YOUR_AUDACITY_WITH_YOUR_VERY_OWN_BLOOD);
	}

	@Override
	public void onEvtDead(Creature killer)
	{
		super.onEvtDead(killer);

		Functions.npcShout(getActor(), NpcString.ITS_NOT_OVER_YET__IT_WONT_BE__OVER__LIKE_THIS__NEVER);

		NpcUtils.spawnSingle(29055, 12589, -49044, -3008, 120000);
	}
}
