package ai.fog;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.ai.DefaultAI;
import l2mv.gameserver.geodata.GeoEngine;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.Location;

public class TarBeetle extends DefaultAI
{
	private static final Logger _log = LoggerFactory.getLogger(TarBeetle.class);

	public static final Location[] POSITIONS =
	{
		new Location(179256, -117160, -3608),
		new Location(179752, -115000, -3608),
		new Location(177944, -119528, -4112),
		new Location(177144, -120808, -4112),
		new Location(181224, -120088, -3672),
		new Location(181960, -117864, -3328),
		new Location(186200, -118120, -3272),
		new Location(188840, -118696, -3288),
		new Location(185448, -120536, -3088),
		new Location(183672, -119048, -3088),
		new Location(188072, -120824, -3088),
		new Location(189592, -120392, -3048),
		new Location(189448, -117464, -3288),
		new Location(188456, -115816, -3288),
		new Location(186424, -114440, -3280),
		new Location(185112, -113272, -3280),
		new Location(187768, -112952, -3288),
		new Location(189176, -111672, -3288),
		new Location(189960, -108712, -3288),
		new Location(187816, -110536, -3288),
		new Location(185368, -109880, -3288),
		new Location(181848, -109368, -3664),
		new Location(181816, -112392, -3664),
		new Location(180136, -112632, -3664),
		new Location(183608, -111432, -3648),
		new Location(178632, -108568, -3664),
		new Location(176264, -109448, -3664),
		new Location(176072, -112952, -3488),
		new Location(175720, -112136, -5520),
		new Location(178504, -112712, -5816),
		new Location(180248, -116136, -6104),
		new Location(182552, -114824, -6104),
		new Location(184248, -116600, -6104),
		new Location(181336, -110536, -5832),
		new Location(182088, -106664, -6000),
		new Location(178808, -107736, -5832),
		new Location(178776, -110120, -5824),
	};

	private boolean CAN_DEBUF = false;
	private static final long TAR_BEETLE = 18804;
	private static final long TELEPORT_PERIOD = 3 * 60 * 1000;
	private long LAST_TELEPORT = System.currentTimeMillis();

	public TarBeetle(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}

	private void CancelTarget(NpcInstance actor)
	{
		if (TAR_BEETLE != actor.getDisplayId())
		{
			for (Player player : World.getAroundPlayers(actor))
			{
				if (player.getTarget() == actor)
				{
					player.setTarget(null);
					player.abortAttack(true, false);
					player.abortCast(true, true);
				}
			}
		}
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		NpcInstance actor = getActor();
		CancelTarget(actor);
		super.onEvtAttacked(attacker, damage);
	}

	@Override
	protected boolean thinkActive()
	{

		NpcInstance actor = getActor();
		CancelTarget(actor);

		if (Rnd.chance(1))
		{
			CAN_DEBUF = true;
		}

		if (CAN_DEBUF)
		{
			for (Player player : World.getAroundPlayers(actor, 500, 200))
			{
				addEffect(actor, player);
			}
			CAN_DEBUF = false;
		}

		if (actor == null || System.currentTimeMillis() - LAST_TELEPORT < TELEPORT_PERIOD)
		{
			return false;
		}

		for (int i = 0; i < POSITIONS.length; i++)
		{
			Location loc = POSITIONS[Rnd.get(POSITIONS.length)];
			if (actor.getLoc().equals(loc))
			{
				continue;
			}

			int x = loc.x + Rnd.get(1, 8);
			int y = loc.y + Rnd.get(1, 8);
			int z = GeoEngine.getHeight(x, y, loc.z, actor.getReflection().getGeoIndex());

			actor.broadcastPacketToOthers(new MagicSkillUse(actor, actor, 4671, 1, 500, 0));
			ThreadPoolManager.getInstance().schedule(new Teleport(new Location(x, y, z)), 500);
			LAST_TELEPORT = System.currentTimeMillis();
			break;
		}
		return super.thinkActive();
	}

	@SuppressWarnings("null")
	private void addEffect(NpcInstance actor, Player player)
	{
		List<Effect> effect = player.getEffectList().getEffectsBySkillId(6142);
		if (effect != null)
		{
			int level = effect.get(0).getSkill().getLevel();
			if (level < 3)
			{
				effect.get(0).exit();
				Skill skill = SkillTable.getInstance().getInfo(6142, level + 1);
				skill.getEffects(actor, player, false, false);
				actor.broadcastPacket(new MagicSkillUse(actor, player, skill.getId(), level, skill.getHitTime(actor), 0));
			}
		}
		else
		{
			Skill skill = SkillTable.getInstance().getInfo(6142, 1);
			if (skill != null)
			{
				skill.getEffects(actor, player, false, false);
				actor.broadcastPacket(new MagicSkillUse(actor, player, skill.getId(), 1, skill.getHitTime(actor), 0));
			}
			else
			{
				_log.warn("Skill " + skill.getId() + " is null, fix it.");
			}
		}
	}
}