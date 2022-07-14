package quests;

import l2mv.gameserver.scripts.ScriptFile;

public class _089_SagaOfTheMysticMuse extends SagasSuperclass implements ScriptFile
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

	public _089_SagaOfTheMysticMuse()
	{
		super(false);

		NPC = new int[]
		{
			30174,
			31627,
			31283,
			31283,
			31643,
			31646,
			31648,
			31651,
			31654,
			31655,
			31658,
			31283
		};
		Items = new int[]
		{
			7080,
			7530,
			7081,
			7504,
			7287,
			7318,
			7349,
			7380,
			7411,
			7442,
			7083,
			0
		};
		Mob = new int[]
		{
			27251,
			27238,
			27255
		};
		classid = 103;
		prevclass = 0x1b;
		X = new int[]
		{
			119518,
			181227,
			181215
		};
		Y = new int[]
		{
			-28658,
			36703,
			36676
		};
		Z = new int[]
		{
			-3811,
			-4816,
			-4812
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