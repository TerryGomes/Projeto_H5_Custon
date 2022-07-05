package quests;

import l2f.gameserver.scripts.ScriptFile;

public class _091_SagaOfTheArcanaLord extends SagasSuperclass implements ScriptFile
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

	public _091_SagaOfTheArcanaLord()
	{
		super(false);

		NPC = new int[]
		{
			31605,
			31622,
			31585,
			31608,
			31586,
			31646,
			31647,
			31651,
			31654,
			31655,
			31658,
			31608
		};
		Items = new int[]
		{
			7080,
			7604,
			7081,
			7506,
			7289,
			7320,
			7351,
			7382,
			7413,
			7444,
			7110,
			0
		};
		Mob = new int[]
		{
			27313,
			27240,
			27310
		};
		classid = 96;
		prevclass = 0x0E;
		X = new int[]
		{
			119518,
			181215,
			181227
		};
		Y = new int[]
		{
			-28658,
			36676,
			36703
		};
		Z = new int[]
		{
			-3811,
			-4812,
			-4816
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