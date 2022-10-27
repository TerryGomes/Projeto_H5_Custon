package npc.model;

import java.util.concurrent.Future;

import bosses.FourSepulchersManager;
import bosses.FourSepulchersSpawn;
import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.RaidBossInstance;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class SepulcherRaidInstance extends RaidBossInstance
{
	public int mysteriousBoxId = 0;
	private Future<?> _onDeadEventTask = null;

	public SepulcherRaidInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	protected void onDeath(Creature killer)
	{
		super.onDeath(killer);

		Player player = killer.getPlayer();
		if (player != null)
		{
			giveCup(player);
		}
		if (_onDeadEventTask != null)
		{
			_onDeadEventTask.cancel(false);
		}
		_onDeadEventTask = ThreadPoolManager.getInstance().schedule(new OnDeadEvent(this), 8500);
	}

	@Override
	protected void onDelete()
	{
		if (_onDeadEventTask != null)
		{
			_onDeadEventTask.cancel(false);
			_onDeadEventTask = null;
		}

		super.onDelete();
	}

	private void giveCup(Player player)
	{
		String questId = FourSepulchersManager.QUEST_ID;
		int cupId = 0;
		int oldBrooch = 7262;

		switch (getNpcId())
		{
		case 25339:
			cupId = 7256;
			break;
		case 25342:
			cupId = 7257;
			break;
		case 25346:
			cupId = 7258;
			break;
		case 25349:
			cupId = 7259;
			break;
		}

		if (player.getParty() != null)
		{
			for (Player mem : player.getParty().getMembers())
			{
				QuestState qs = mem.getQuestState(questId);
				if (qs != null && (qs.isStarted() || qs.isCompleted()) && mem.getInventory().getItemByItemId(oldBrooch) == null && player.isInRange(mem, 700))
				{
					Functions.addItem(mem, cupId, 1, "SepulcherRaidInstance");
				}
			}
		}
		else
		{
			QuestState qs = player.getQuestState(questId);
			if (qs != null && (qs.isStarted() || qs.isCompleted()) && player.getInventory().getItemByItemId(oldBrooch) == null)
			{
				Functions.addItem(player, cupId, 1, "SepulcherRaidInstance");
			}
		}
	}

	private class OnDeadEvent extends RunnableImpl
	{
		SepulcherRaidInstance _activeChar;

		public OnDeadEvent(SepulcherRaidInstance activeChar)
		{
			_activeChar = activeChar;
		}

		@Override
		public void runImpl() throws Exception
		{
			FourSepulchersSpawn.spawnEmperorsGraveNpc(_activeChar.mysteriousBoxId);
		}
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}