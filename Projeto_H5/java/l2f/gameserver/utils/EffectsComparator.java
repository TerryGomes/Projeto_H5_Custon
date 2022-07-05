package l2f.gameserver.utils;

import java.io.Serializable;
import java.util.Comparator;

import l2f.gameserver.model.Effect;

public class EffectsComparator implements Comparator<Effect>, Serializable
{
	private static final EffectsComparator instance = new EffectsComparator();
	private static final long serialVersionUID = -901791557314516714L;

	public static EffectsComparator getInstance()
	{
		return instance;
	}

	@Override
	public int compare(Effect o1, Effect o2)
	{
		boolean toggle1 = o1.getSkill().isToggle();
		boolean toggle2 = o2.getSkill().isToggle();

		if (toggle1 && toggle2)
		{
			return compareStartTime(o1, o2);
		}

		if (toggle1 || toggle2)
		{
			if (toggle1)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}

		boolean music1 = o1.getSkill().isMusic();
		boolean music2 = o2.getSkill().isMusic();

		if (music1 && music2)
		{
			return compareStartTime(o1, o2);
		}

		if (music1 || music2)
		{
			if (music1)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}

		boolean offensive1 = o1.isOffensive();
		boolean offensive2 = o2.isOffensive();

		if (offensive1 && offensive2)
		{
			return compareStartTime(o1, o2);
		}

		if (offensive1 || offensive2)
		{
			if (!offensive1)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}

		boolean trigger1 = o1.getSkill().isTrigger();
		boolean trigger2 = o2.getSkill().isTrigger();

		if (trigger1 && trigger2)
		{
			return compareStartTime(o1, o2);
		}

		if (trigger1 || trigger2)
		{
			if (trigger1)
			{
				return 1;
			}
			else
			{
				return -1;
			}
		}

		return compareStartTime(o1, o2);
	}

	private static int compareStartTime(Effect o1, Effect o2)
	{
		if (o1.getStartTime() > o2.getStartTime())
		{
			return 1;
		}

		if (o1.getStartTime() < o2.getStartTime())
		{
			return -1;
		}

		return 0;
	}
}