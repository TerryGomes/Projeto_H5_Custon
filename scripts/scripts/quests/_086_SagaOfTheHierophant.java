package quests;

import l2mv.gameserver.scripts.ScriptFile;

public class _086_SagaOfTheHierophant extends SagasSuperclass implements ScriptFile
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

	public _086_SagaOfTheHierophant()
	{
		super(false);

		NPC = new int[]
		{
			30191,
			31626,
			31588,
			31280,
			31591,
			31646,
			31648,
			31652,
			31654,
			31655,
			31659,
			31280
		};
		Items = new int[]
		{
			7080,
			7523,
			7081,
			7501,
			7284,
			7315,
			7346,
			7377,
			7408,
			7439,
			7089,
			0
		};
		Mob = new int[]
		{
			27269,
			27235,
			27275
		};
		classid = 98;
		prevclass = 0x11;
		X = new int[]
		{
			161719,
			124355,
			124376
		};
		Y = new int[]
		{
			-92823,
			82155,
			82127
		};
		Z = new int[]
		{
			-1893,
			-2803,
			-2796
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