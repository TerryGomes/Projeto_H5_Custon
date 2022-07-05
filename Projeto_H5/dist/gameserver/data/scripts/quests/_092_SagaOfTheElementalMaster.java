package quests;

import l2f.gameserver.scripts.ScriptFile;

public class _092_SagaOfTheElementalMaster extends SagasSuperclass implements ScriptFile
{
	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	public _092_SagaOfTheElementalMaster()
	{
		super(false);

		NPC = new int[]
		{
			30174,
			31281,
			31614,
			31614,
			31629,
			31646,
			31648,
			31652,
			31654,
			31655,
			31659,
			31614
		};
		Items = new int[]
		{
			7080,
			7605,
			7081,
			7507,
			7290,
			7321,
			7352,
			7383,
			7414,
			7445,
			7111,
			0
		};
		Mob = new int[]
		{
			27314,
			27241,
			27311
		};
		classid = 104;
		prevclass = 0x1C;
		X = new int[]
		{
			161719,
			124376,
			124355
		};
		Y = new int[]
		{
			-92823,
			82127,
			82155
		};
		Z = new int[]
		{
			-1893,
			-2796,
			-2803
		};
		Text = new String[]
		{
			"PLAYERNAME! Pursued to here! However, I jumped out of the Banshouren boundaries! You look at the giant as the sign of power!",
			"... Oh ... good! So it was ... let's begin!",
			"I do not have the patience ..! I have been a giant force ...! Cough chatter ah ah ah!",
			"Paying homage to those who disrupt the orderly will be PLAYERNAME's death!",
			"Now, my soul freed from the shackles of the millennium, Halixia, to the back side I come ...",
			"Why do you interfere others' battles?",
			"This is a waste of time.. Say goodbye...!",
			"...That is the enemy",
			"...Goodness! PLAYERNAME you are still looking?",
			"PLAYERNAME ... Not just to whom the victory. Only personnel involved in the fighting are eligible to share in the victory.",
			"Your sword is not an ornament. Don't you think, PLAYERNAME?",
			"Goodness! I no longer sense a battle there now.",
			"let...",
			"Only engaged in the battle to bar their choice. Perhaps you should regret.",
			"The human nation was foolish to try and fight a giant's strength.",
			"Must...Retreat... Too...Strong.",
			"PLAYERNAME. Defeat...by...retaining...and...Mo...Hacker",
			"....! Fight...Defeat...It...Fight...Defeat...It..."
		};

		registerNPCs();
	}
}