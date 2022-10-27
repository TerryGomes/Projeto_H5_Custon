package quests;

import l2mv.gameserver.scripts.ScriptFile;

public class _087_SagaOfEvasSaint extends SagasSuperclass implements ScriptFile
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

	public _087_SagaOfEvasSaint()
	{
		super(false);

		NPC = new int[]
		{
			30191,
			31626,
			31588,
			31280,
			31620,
			31646,
			31649,
			31653,
			31654,
			31655,
			31657,
			31280
		};
		Items = new int[]
		{
			7080,
			7524,
			7081,
			7502,
			7285,
			7316,
			7347,
			7378,
			7409,
			7440,
			7088,
			0
		};
		Mob = new int[]
		{
			27266,
			27236,
			27276
		};
		classid = 105;
		prevclass = 0x1E;
		X = new int[]
		{
			164650,
			46087,
			46066
		};
		Y = new int[]
		{
			-74121,
			-36372,
			-36396
		};
		Z = new int[]
		{
			-2871,
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