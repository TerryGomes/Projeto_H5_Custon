package ai.dragonvalley;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.Location;

/**
 * @author pchayka
 */

public class Knoriks extends Patrollers
{
	private static int KNORIKS_ACTIVATE_SKILL_CHANGE = 5; // chance for activate skill
	private static int KNORIKS_SEARCH_RADIUS = 600; // search around players
	private static int KNORIKS_SKILL_DBUFF_ID = 6744; // dbuff id (Dark Storm)

	public Knoriks(NpcInstance actor)
	{
		super(actor);
		_points = new Location[]
		{
			new Location(141848, 121592, -3912),
			new Location(140440, 120264, -3912),
			new Location(140664, 118328, -3912),
			new Location(142104, 117400, -3912),
			new Location(142968, 117816, -3912),
			new Location(142648, 119672, -3912),
			new Location(143864, 121016, -3896),
			new Location(144504, 119320, -3896),
			new Location(145448, 117624, -3912),
			new Location(146824, 118328, -3984),
			new Location(147080, 119320, -4288),
			new Location(147432, 121224, -4768),
			new Location(149640, 119480, -4864),
			new Location(150616, 118312, -4936),
			new Location(152936, 116664, -5256),
			new Location(153208, 115224, -5256),
			new Location(151656, 115080, -5472),
			new Location(148824, 114888, -5472),
			new Location(151128, 114520, -5464),
			new Location(153320, 112728, -5520),
			new Location(153096, 111800, -5520),
			new Location(150504, 111256, -5520),
			new Location(149512, 111080, -5488),
			new Location(149304, 109672, -5216),
			new Location(151864, 109368, -5152),
			new Location(153320, 109032, -5152),
			new Location(153048, 108040, -5152),
			new Location(150888, 107320, -4800),
			new Location(149320, 108456, -4424),
			new Location(147704, 107256, -4048),
			new Location(146648, 108376, -3664),
			new Location(146408, 110200, -3472),
			new Location(146568, 111784, -3552),
			new Location(147896, 112584, -3720),
			new Location(148904, 113208, -3720),
			new Location(149256, 114824, -3720),
			new Location(149688, 116344, -3704),
			new Location(150680, 117880, -3688),
			new Location(152056, 118968, -3808),
			new Location(152696, 120040, -3808),
			new Location(151928, 121352, -3808),
			new Location(152856, 121752, -3808),
			new Location(154440, 121208, -3808)
		};
	}

	@Override
	public void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();

		super.onEvtAttacked(attacker, damage);

		if (!attacker.isPlayer() || actor.isDead() || !Rnd.chance(KNORIKS_ACTIVATE_SKILL_CHANGE))
		{
			return;
		}

		if (attacker != null)
		{
			actor.doCast(SkillTable.getInstance().getInfo(KNORIKS_SKILL_DBUFF_ID, 1), (Player) attacker, false);
		}
	}

	public static int getKNORIKS_SEARCH_RADIUS()
	{
		return KNORIKS_SEARCH_RADIUS;
	}

	public static void setKNORIKS_SEARCH_RADIUS(int kNORIKS_SEARCH_RADIUS)
	{
		KNORIKS_SEARCH_RADIUS = kNORIKS_SEARCH_RADIUS;
	}
}
