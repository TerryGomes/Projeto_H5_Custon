/*
 * Copyright (C) 2004-2013 L2J Server
 * This file is part of L2J Server.
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.fandc.dailyquests.quests;

import l2mv.gameserver.Config;
import l2mv.gameserver.fandc.dailyquests.AbstractDailyQuest;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.utils.HtmlUtils;

/**
 * @author UnAfraid
 */
public class PKHunterDailyQuest extends AbstractDailyQuest
{
	public PKHunterDailyQuest()
	{
		CharListenerList.addGlobal(new OnDeathList());
	}

	@Override
	public int getQuestIntId()
	{
		// Random quest id
		return 35004;
	}

	@Override
	protected int writeHeight(Player player, int index)
	{
		switch (index)
		{
		case 1:
		{
			return 480;
		}
		case 2:
		{
			return 680;
		}
		}
		return 480;
	}

	@Override
	protected String writeQuestInfo(Player player)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("You must hunt down randomly between " + getMinKillsRequired() + " and " + getMaxKillsRequired() + " players with karma in order to complete the quest.<br1>");
		sb.append("Each time you accept the quest the amount of players will be randomly selected.<br1>");
		sb.append("Rules are simple:<br1>");
		sb.append("1. Non Party/Channel/Clan/Alliance member!<br1>");
		sb.append("2. Not participant of event or olympiad!<br1>");
		sb.append("3. Not killed with karma!<br1>");
		sb.append("4. Not killed again within last " + (Config.ANTIFEED_INTERVAL / 60000) + " mins!<br1>");
		sb.append("5. Not killed into Arena!<br1>");
		sb.append("6. Target must be at least level " + getMinLevel() + " or above to be counted!<br1>");
		sb.append("<font color=\"LEVEL\">Zariche and Akamanah gives 2 points!!</font><br1>");
		return sb.toString();
	}

	@Override
	protected String writeQuestProgress(Player player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return "You must take the quest to check your progress!";
		}

		final StringBuilder sb = new StringBuilder();
		sb.append("Progress:<br>");
		sb.append(HtmlUtils.getWeightGauge(450, st.getInt("KILLS"), st.getInt("KILLS_NEEDED"), false));
		sb.append("<br>");

		sb.append("You must hunt down from " + st.getInt("KILLS_NEEDED") + " players with karma in order to complete the quest.<br1>");
		sb.append("Each time you accept the quest the amount of players will be randomly selected.<br1>");
		sb.append("Rules are simple:<br1>");
		sb.append("1. Non Party/Channel/Clan/Alliance member!<br1>");
		sb.append("2. Not participant of event or olympiad!<br1>");
		sb.append("3. Not killed with karma!<br1>");
		sb.append("4. Not killed again within last " + (Config.ANTIFEED_INTERVAL / 60000) + " mins!<br1>");
		sb.append("5. Not killed into Arena!<br1>");
		sb.append("6. Target must be at least level " + getMinLevel() + " or above to be counted!<br1>");
		sb.append("<font color=\"LEVEL\">Zariche and Akamanah gives 2 points!!</font><br1>");
		return sb.toString();
	}

	@Override
	public void onQuestStart(QuestState st)
	{
		st.set("KILLS", "0");
		st.set("KILLS_NEEDED", getRandomKillsRequired());
	}

	private class OnDeathList implements OnDeathListener
	{
		@Override
		public void onDeath(Creature actor, Creature killer)
		{
			if (!actor.isPlayer())
			{
				return;
			}

			final Player player = actor.getPlayer();
			if (!validateKill(player, killer != null ? killer.getPlayer() : null) || (player.getKarma() < 1))
			{
				return;
			}

			final Player attacker = killer != null ? killer.getPlayer() : null;
			final Player attackerMember = getRandomPartyMember(attacker);
			final QuestState st = attackerMember != null ? attackerMember.getQuestState(getName()) : null;
			if ((attackerMember == null) || (st == null) || st.isCompleted())
			{
				return;
			}
			st.set("KILLS", st.getInt("KILLS") + (player.isCursedWeaponEquipped() ? 2 : 1));
			if (st.getInt("KILLS") >= st.getInt("KILLS_NEEDED"))
			{
				st.setState(COMPLETED);
				st.setRestartTime();
				onQuestFinish(st);
			}
			else
			{
				showScreenMessage(attackerMember, "progress " + st.get("KILLS") + "/" + st.get("KILLS_NEEDED") + " completed!", 5000);
			}
		}
	}
}
