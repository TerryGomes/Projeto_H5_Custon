package quests;

import l2f.gameserver.scripts.ScriptFile;

public class _088_SagaOfTheArchmage extends SagasSuperclass implements ScriptFile
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

	public _088_SagaOfTheArchmage()
	{
		super(false);

		NPC = new int[]
		{
			30176,
			31627,
			31282,
			31282,
			31590,
			31646,
			31647,
			31650,
			31654,
			31655,
			31657,
			31282
		};
		Items = new int[]
		{
			7080,
			7529,
			7081,
			7503,
			7286,
			7317,
			7348,
			7379,
			7410,
			7441,
			7082,
			0
		};
		Mob = new int[]
		{
			27250,
			27237,
			27254
		};
		classid = 94;
		prevclass = 0x0C;
		X = new int[]
		{
			191046,
			46066,
			46087
		};
		Y = new int[]
		{
			-40640,
			-36396,
			-36372
		};
		Z = new int[]
		{
			-3042,
			-1685,
			-1685
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