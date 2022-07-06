package l2mv.gameserver.ai;

import l2mv.gameserver.model.instances.NpcInstance;

public class Priest extends DefaultAI
{
	public Priest(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		return super.thinkActive() || defaultThinkBuff(10, 5);
	}

	@Override
	protected boolean createNewTask()
	{
		return defaultFightTask();
	}

	@Override
	public int getRatePHYS()
	{
		return 25;
	}

	@Override
	public int getRateDOT()
	{
		return 40;
	}

	@Override
	public int getRateDEBUFF()
	{
		return 40;
	}

	@Override
	public int getRateDAM()
	{
		return 75;
	}

	@Override
	public int getRateSTUN()
	{
		return 10;
	}

	@Override
	public int getRateBUFF()
	{
		return 25;
	}

	@Override
	public int getRateHEAL()
	{
		return 90;
	}
}