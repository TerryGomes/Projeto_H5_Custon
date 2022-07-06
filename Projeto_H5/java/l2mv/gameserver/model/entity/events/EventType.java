package l2mv.gameserver.model.entity.events;

public enum EventType
{
	MAIN_EVENT, // 1 - Dominion Siege Runner, 2 - Underground Coliseum Event Runner, 3 - Kratei Cube Runner
	SIEGE_EVENT, // Siege of Castle, Fortress, Clan Hall, Dominion
	PVP_EVENT, // Kratei Cube, Cleft, Underground Coliseum
	BOAT_EVENT, //
	FUN_EVENT, //
	FIGHT_CLUB_EVENT; //

	private int _step;

	EventType()
	{
		_step = ordinal() * 1000;
	}

	public int step()
	{
		return _step;
	}
}
