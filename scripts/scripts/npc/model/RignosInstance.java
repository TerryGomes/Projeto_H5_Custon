package npc.model;

import java.util.concurrent.Future;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ItemFunctions;

public class RignosInstance extends NpcInstance
{
	private static final long serialVersionUID = -1L;

	private class EndRaceTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			_raceTask = null;
		}
	}

	private static final Skill SKILL_EVENT_TIMER = SkillTable.getInstance().getInfo(5239, 5);
	private static final int RACE_STAMP = 10013;
	private static final int SECRET_KEY = 9694;

	private Future<?> _raceTask;

	public RignosInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.equalsIgnoreCase("startRace"))
		{
			if (_raceTask != null)
			{
				return;
			}

			altUseSkill(SKILL_EVENT_TIMER, player);
			ItemFunctions.removeItem(player, RACE_STAMP, ItemFunctions.getItemCount(player, RACE_STAMP), true, "RignosInstance");
			_raceTask = ThreadPoolManager.getInstance().schedule(new EndRaceTask(), 30 * 60 * 1000L);
		}
		else if (command.equalsIgnoreCase("endRace"))
		{
			if (_raceTask == null)
			{
				return;
			}

			long count = ItemFunctions.getItemCount(player, RACE_STAMP);
			if (count >= 4)
			{
				ItemFunctions.removeItem(player, RACE_STAMP, count, true, "RignosInstance");
				ItemFunctions.addItem(player, SECRET_KEY, 1, true, "RignosInstance");
				player.getEffectList().stopEffect(SKILL_EVENT_TIMER);
				_raceTask.cancel(false);
				_raceTask = null;
			}
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (ItemFunctions.getItemCount(player, RACE_STAMP) >= 4)
		{
			showChatWindow(player, "race_start001a.htm");
		}
		else if (player.getLevel() >= 78 && _raceTask == null)
		{
			showChatWindow(player, "race_start001.htm");
		}
		else
		{
			showChatWindow(player, "race_start002.htm");
		}
	}
}
