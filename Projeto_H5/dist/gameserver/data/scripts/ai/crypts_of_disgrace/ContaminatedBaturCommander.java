package ai.crypts_of_disgrace;

import l2f.commons.util.Rnd;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.Fighter;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.SimpleSpawner;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

/**
 * @author Kolobrodik
 * @date 11:19/19.02.12
 * @description: AI для Contaminated Batur Commander в локации Crypts of Disgrace. (ID 22705)
 * - Когда вы убиваете Contaminated Batur Commander, обычно респавнится такой же моб.
 *   Однако есть шанс, что появится Turka Commander in Chief, котоырй призовет к себе на помощь
 *   много Turka Troop Commanders.
 */
public class ContaminatedBaturCommander extends Fighter
{
	private static final int TurkaCommanderChief = 22707; // Turka Commander in Chief
	private static final int CHANCE = 10; // Шанс спауна Turka Commander in Chief и миньенов

	public ContaminatedBaturCommander(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (Rnd.chance(CHANCE)) // Если повезло
		{
			// Спауним
			NpcInstance actor = getActor();
			SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(TurkaCommanderChief));
			sp.setLoc(Location.findPointToStay(actor, 100, 120));
			NpcInstance npc = sp.doSpawn(true);

			// Натравливаем на атакующего
			if (killer.isPet() || killer.isSummon())
			{
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(2, 100));
			}
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer.getPlayer(), Rnd.get(1, 100));
		}

		super.onEvtDead(killer);
	}
}
