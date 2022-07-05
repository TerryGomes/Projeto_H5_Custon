package l2f.gameserver.skills.skillclasses;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.templates.npc.NpcTemplate;

public class Spawn extends Skill
{
	private static final Logger _log = LoggerFactory.getLogger(Spawn.class);
	private final int _npcId;
	private final int _despawnDelay;
	private final boolean _summonSpawn;
	private final boolean _randomOffset;
	private final int _skillToCast;
	private NpcInstance _spawnNpc;

	public Spawn(StatsSet set)
	{
		super(set);
		_npcId = set.getInteger("npcId", 0);
		_despawnDelay = set.getInteger("despawnDelay", 0);
		_summonSpawn = set.getBool("isSummonSpawn", false);
		_randomOffset = set.getBool("randomOffset", true);
		_skillToCast = set.getInteger("skillToCast", 0);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (!activeChar.isPlayer())
		{
			return false;
		}
		if (activeChar.isInZone(Zone.ZoneType.peace_zone) || activeChar.isInZone(Zone.ZoneType.water) || activeChar.isInZone(Zone.ZoneType.epic) || activeChar.isInZone(Zone.ZoneType.SIEGE)
					|| activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage(activeChar.getPlayer().isLangRus() ? "In this zone, the action is disabled" : "In this zone, the action is disabled");
			return false;
		}

		if (activeChar.isFlying())
		{
			activeChar.sendMessage(activeChar.getPlayer().isLangRus() ? "In this zone, the action is disabled" : "In this state, the action is disabled");
			return false;
		}
		return true;
	}

	@Override
	public void useSkill(Creature caster, List<Creature> targets)
	{
		if (_npcId == 0)
		{
			_log.warn("NPC ID not defined for skill ID:" + getId());
			return;
		}

		if (caster.getPlayer() != null && caster.getPlayer().getBoat() != null)
		{
			return;
		}

		final NpcTemplate template = NpcHolder.getInstance().getTemplate(_npcId);
		if (template == null)
		{
			_log.warn("Spawn of the nonexisting NPC ID:" + _npcId + ", skill ID:" + getId());
			return;
		}

		try
		{
			SimpleSpawner spawn = new SimpleSpawner(template);
			spawn.setReflection(ReflectionManager.DEFAULT);
			spawn.setHeading(-1);

			if (_randomOffset)
			{
				spawn.setLocx(caster.getX() + (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20)));
				spawn.setLocy(caster.getY() + (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20)));
			}
			else
			{
				spawn.setLocx(caster.getX());
				spawn.setLocy(caster.getY());
			}
			spawn.setLocz(caster.getZ() + 20);
			spawn.doSpawn(true);
			spawn.init();
			_spawnNpc = spawn.getLastSpawn();
			if (_despawnDelay > 0)
			{
				ThreadPoolManager.getInstance().schedule(new RunnableImpl()
				{
					@Override
					public void runImpl() throws Exception
					{
						if (_spawnNpc == null)
						{
							return;
						}
						_spawnNpc.deleteMe();
					}
				}, _despawnDelay);
			}

			/*
			 * if ((npc instanceof TotemInstance) && (_skillToCast > 0))
			 * {
			 * ((L2TotemInstance) npc).setSkill(_skillToCast);
			 * ((L2TotemInstance) npc).setAITask();
			 * }
			 */
		}
		catch (Exception e)
		{
			_log.warn("Exception while spawning NPC ID: " + _npcId + ", skill ID: " + getId() + ", exception: " + e.getMessage(), e);
		}
	}
}