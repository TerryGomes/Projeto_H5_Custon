package ai.monastery_of_silence;

import java.util.List;

import l2f.gameserver.ai.Fighter;
import l2f.gameserver.instancemanager.SpawnManager;
import l2f.gameserver.model.instances.NpcInstance;

/**
 * @author Bonux
**/
public class DivinityMonster extends Fighter
{
	private final boolean _isDefault;
	private final String _nextMakerName;

	public DivinityMonster(NpcInstance actor)
	{
		super(actor);

		_isDefault = actor.getParameter("is_default", false);
		_nextMakerName = actor.getParameter("next_maker_name", null);
	}

	@Override
	public void onEvtDeSpawn()
	{
		if (_isDefault)
		{
			List<NpcInstance> maker = SpawnManager.getInstance().getAllSpawned(_nextMakerName);
			if (maker.isEmpty())
			{
				SpawnManager.getInstance().spawn(_nextMakerName);
			}
		}
		super.onEvtDeSpawn();
	}
}