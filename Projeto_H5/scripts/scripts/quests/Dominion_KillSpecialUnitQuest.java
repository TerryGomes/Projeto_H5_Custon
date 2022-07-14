package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.data.xml.holder.EventHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.entity.events.EventType;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2mv.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.components.NpcString;
import l2mv.gameserver.scripts.ScriptFile;

public abstract class Dominion_KillSpecialUnitQuest extends Quest implements ScriptFile
{
	private final ClassId[] _classIds;

	public Dominion_KillSpecialUnitQuest()
	{
		super(PARTY_ALL);

		_classIds = getTargetClassIds();
		DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
		for (ClassId c : _classIds)
		{
			runnerEvent.addClassQuest(c, this);
		}
	}

	protected abstract NpcString startNpcString();

	protected abstract NpcString progressNpcString();

	protected abstract NpcString doneNpcString();

	protected abstract int getRandomMin();

	protected abstract int getRandomMax();

	protected abstract ClassId[] getTargetClassIds();

	@Override
	public String onKill(Player killed, QuestState qs)
	{
		Player player = qs.getPlayer();
		if (player == null)
		{
			return null;
		}

		DominionSiegeEvent event1 = player.getEvent(DominionSiegeEvent.class);
		if (event1 == null)
		{
			return null;
		}
		DominionSiegeEvent event2 = killed.getEvent(DominionSiegeEvent.class);
		if (event1 == null || event2 == null || event2 == event1 || !ArrayUtils.contains(_classIds, killed.getClassId()))
		{
			return null;
		}

		int max_kills = qs.getInt("max_kills");
		if (max_kills == 0)
		{
			qs.setState(STARTED);
			qs.setCond(1);

			max_kills = Rnd.get(getRandomMin(), getRandomMax());
			qs.set("max_kills", max_kills);
			qs.set("current_kills", 1);
			if (player.getParty() == null)
			{
				player.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills)));
			}
			else
			{
				for (Player member : player.getParty().getMembers())
				{
					member.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills)));
				}
			}

		}
		else
		{
			int current_kills = qs.getInt("current_kills") + 1;
			if (current_kills >= max_kills)
			{
				event1.addReward(player, DominionSiegeEvent.STATIC_BADGES, 10);

				qs.setState(COMPLETED);
				qs.addExpAndSp(534000, 51000);
				qs.exitCurrentQuest(true);

				if (player.getParty() == null)
				{
					player.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false));
				}
				else
				{
					for (Player member : player.getParty().getMembers())
					{
						member.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false));
					}
				}

			}
			else
			{
				qs.set("current_kills", current_kills);
				if (player.getParty() == null)
				{
					player.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills), String.valueOf(current_kills)));
				}
				else
				{
					for (Player member : player.getParty().getMembers())
					{
						member.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills), String.valueOf(current_kills)));
					}
				}

			}
		}

		return null;
	}

	@Override
	public boolean canAbortByPacket()
	{
		return false;
	}

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
}
