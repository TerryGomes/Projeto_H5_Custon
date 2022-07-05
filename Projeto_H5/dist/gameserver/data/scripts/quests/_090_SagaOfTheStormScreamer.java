package quests;

import l2f.gameserver.scripts.ScriptFile;

public class _090_SagaOfTheStormScreamer extends SagasSuperclass implements ScriptFile
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

	public _090_SagaOfTheStormScreamer()
	{
		super(false);
		NPC = new int[]
		{
			30175,
			31627,
			31287,
			31287,
			31598,
			31646,
			31649,
			31652,
			31654,
			31655,
			31659,
			31287
		};
		Items = new int[]
		{
			7080,
			7531,
			7081,
			7505,
			7288,
			7319,
			7350,
			7381,
			7412,
			7443,
			7084,
			0
		};
		Mob = new int[]
		{
			27252,
			27239,
			27256
		};
		classid = 110;
		prevclass = 0x28;
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