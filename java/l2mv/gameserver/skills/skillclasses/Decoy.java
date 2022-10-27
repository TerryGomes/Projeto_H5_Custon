package l2mv.gameserver.skills.skillclasses;

import java.util.List;

import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.DecoyInstance;
import l2mv.gameserver.templates.StatsSet;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Location;

public class Decoy extends Skill
{
	private final int _npcId;
	private final int _lifeTime;

	public Decoy(StatsSet set)
	{
		super(set);

		_npcId = set.getInteger("npcId", 0);
		_lifeTime = set.getInteger("lifeTime", 1200) * 1000;
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (activeChar.isAlikeDead() || !activeChar.isPlayer() || activeChar != target || (_npcId <= 0))
		{
			return false;
		}

		if (activeChar.isInObserverMode())
		{
			return false;
		}

		/*
		 * need correct
		 * if (activeChar.getPet() != null || activeChar.getPlayer().isMounted())
		 * {
		 * activeChar.getPlayer().sendPacket(Msg.YOU_ALREADY_HAVE_A_PET);
		 * return false;
		 * }
		 */
		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature caster, List<Creature> targets)
	{
		Player activeChar = caster.getPlayer();

		NpcTemplate DecoyTemplate = NpcHolder.getInstance().getTemplate(getNpcId());
		DecoyInstance decoy = new DecoyInstance(IdFactory.getInstance().getNextId(), DecoyTemplate, activeChar, _lifeTime);

		decoy.setCurrentHp(decoy.getMaxHp(), false);
		decoy.setCurrentMp(decoy.getMaxMp());
		decoy.setHeading(activeChar.getHeading());
		decoy.setReflection(activeChar.getReflection());

		activeChar.setDecoy(decoy);

		decoy.spawnMe(Location.findAroundPosition(activeChar, 50, 70));

	}
}