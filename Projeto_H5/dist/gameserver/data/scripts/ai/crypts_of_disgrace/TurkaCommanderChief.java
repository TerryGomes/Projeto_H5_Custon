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
 * @date 11:46/19.02.12
 * @description: AI моба Turka Commander in Chief (Ghost) в локации Crypts of Disgrace. (ID 27707)
 * - При спауне зовет на помощь миньенов.
 * - При убийстве с неким шансом появляется Guardian of the Burial Grounds (AI crypts_of_disgrace.GuardoftheGrave)
 */
public class TurkaCommanderChief extends Fighter
{
	private static final int TurkaCommanderMinion = 22706; // Миньен
	private static final int MinionCount = 2; // Количество миньенов
	private static final int Guardian = 18815; // Guardian of the Burial Grounds
	private static final int CHANCE = 10;

	public TurkaCommanderChief(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		super.onEvtSpawn();

		for (int i = 0; i < MinionCount; i++) // При спауне главного спауним и миньенов
		{
			npcSpawn(TurkaCommanderMinion);
		}
	}

	@Override
	protected void onEvtDead(Creature killer)
	{
		if (Rnd.chance(CHANCE)) // Если повезло
		{
			// Спауним гварда
			NpcInstance npc = npcSpawn(Guardian);

			// И натравливаем его
			if (killer.isPet() || killer.isSummon())
			{
				npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(2, 100));
			}
			npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer.getPlayer(), Rnd.get(1, 100));
		}

		super.onEvtDead(killer);
	}

	private NpcInstance npcSpawn(int template)
	{
		NpcInstance actor = getActor();
		SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(template));
		sp.setLoc(Location.findPointToStay(actor, 100, 120));
		return sp.doSpawn(true);
	}
}
