package ai;

import l2mv.gameserver.ai.Mystic;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.instances.NpcInstance;

/**
 * AI охраны в Pagan Temple.<br>
 * <li>Не умеют ходить
 * <li>Видит всех в режиме Silent Move
 * <li>Бьют физ атакой игроков, подошедших на расстояние удара
 * <li>Бьют магией, если были атакованы
 * <li>Социальны к собратьям, помогают атакуя магией
 * <li>В случае, если игрок вышел за пределы агро радиуса прекращают использовать дальнобойную магию
 *
 * @author SYS
 */
public class PaganGuard extends Mystic
{
	public PaganGuard(NpcInstance actor)
	{
		super(actor);
		actor.startImmobilized();
	}

	@Override
	protected boolean checkTarget(Creature target, int range)
	{
		NpcInstance actor = getActor();
		if (target != null && !actor.isInRange(target, actor.getAggroRange()))
		{
			actor.getAggroList().remove(target, true);
			return false;
		}
		return super.checkTarget(target, range);
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}