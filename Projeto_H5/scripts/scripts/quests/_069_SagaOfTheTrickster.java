package quests;

import l2mv.gameserver.scripts.ScriptFile;

public class _069_SagaOfTheTrickster extends SagasSuperclass implements ScriptFile
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

	public _069_SagaOfTheTrickster()
	{
		super(false);

		NPC = new int[]
		{
			32138,
			31270,
			31282,
			32228,
			32237,
			31646,
			31649,
			31653,
			31654,
			31655,
			31659,
			31283
		};
		Items = new int[]
		{
			7080,
			9761,
			7081,
			9742,
			9724,
			9727,
			9730,
			9733,
			9736,
			9739,
			9718,
			0
		};
		Mob = new int[]
		{
			27333,
			27334,
			27335
		};
		classid = 134;
		prevclass = 0x82;
		X = new int[]
		{
			164014,
			124355,
			124376
		};
		Y = new int[]
		{
			-74733,
			82155,
			82127
		};
		Z = new int[]
		{
			-3093,
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