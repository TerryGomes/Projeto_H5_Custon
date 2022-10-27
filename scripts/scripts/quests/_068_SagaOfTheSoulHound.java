package quests;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.scripts.ScriptFile;

public class _068_SagaOfTheSoulHound extends SagasSuperclass implements ScriptFile
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

	public _068_SagaOfTheSoulHound()
	{
		super(false);

		NPC = new int[]
		{
			32138,
			31272,
			31269,
			31317,
			32235,
			31646,
			31648,
			31652,
			31654,
			31655,
			31657,
			32241
		};
		Items = new int[]
		{
			7080,
			9802,
			7081,
			9741,
			9723,
			9726,
			9729,
			9732,
			9735,
			9738,
			9719,
			0
		};
		Mob = new int[]
		{
			27327,
			27329,
			27328
		};
		classid = 132; // see getClassId
		prevclass = 128; // see getPrevClass
		X = new int[]
		{
			161719,
			46087,
			46066
		};
		Y = new int[]
		{
			-92823,
			-36372,
			-36396
		};
		Z = new int[]
		{
			-1893,
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

	@Override
	protected int getClassId(Player player)
	{
		return player.getSex() == 1 ? 133 : 132;
	}

	@Override
	protected int getPrevClass(Player player)
	{
		return player.getSex() == 1 ? 129 : 128;
	}
}