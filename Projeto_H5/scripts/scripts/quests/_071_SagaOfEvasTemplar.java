package quests;

import l2mv.gameserver.scripts.ScriptFile;

public class _071_SagaOfEvasTemplar extends SagasSuperclass implements ScriptFile
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

	public _071_SagaOfEvasTemplar()
	{
		super(false);

		NPC = new int[]
		{
			30852,
			31624,
			31278,
			30852,
			31638,
			31646,
			31648,
			31651,
			31654,
			31655,
			31658,
			31281
		};
		Items = new int[]
		{
			7080,
			7535,
			7081,
			7486,
			7269,
			7300,
			7331,
			7362,
			7393,
			7424,
			7094,
			6482
		};
		Mob = new int[]
		{
			27287,
			27220,
			27279
		};
		classid = 99;
		prevclass = 0x14;
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