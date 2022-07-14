package services;

import java.util.ArrayList;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.SimpleSpawner;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

public class FightClub extends Functions implements ScriptFile
{
	private static final ArrayList<SimpleSpawner> _spawns_fight_club_manager = new ArrayList<SimpleSpawner>();

	public static int FIGHT_CLUB_MANAGER = 32500;

	private void spawnFightClub()
	{
		final int FIGHT_CLUB_MANAGER_SPAWN[][] =
		{
			{
				82168,
				149711,
				-3464,
				30212
			} // Giran
		};

		SpawnNPCs(FIGHT_CLUB_MANAGER, FIGHT_CLUB_MANAGER_SPAWN, _spawns_fight_club_manager);
	}

	@Override
	public void onLoad()
	{
		if (Config.FIGHT_CLUB_ENABLED)
		{
			spawnFightClub();
		}
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}