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
package l2mv.gameserver.multverso.dailyquests.quests;

import l2mv.gameserver.listener.actor.player.OnFishDieListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.multverso.dailyquests.AbstractDailyQuest;
import l2mv.gameserver.utils.HtmlUtils;

/**
 * @author Gnacik
 */
public class FishingDailyQuest extends AbstractDailyQuest
{
	public FishingDailyQuest()
	{
		CharListenerList.addGlobal(new OnFishDie());
	}

	@Override
	public int getQuestIntId()
	{
		// Random quest id
		return 35002;
	}

	@Override
	protected int writeHeight(Player player, int index)
	{
		switch (index)
		{
		case 1:
		{
			return 620;
		}
		}
		return 480;
	}

	@Override
	protected String writeQuestInfo(Player player)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("Você deve pegar aleatoriamente entre " + getMinKillsRequired() + " os " + getMaxKillsRequired() + " peixes.<br1>");
		sb.append("Grau e tipo não são importantes.<br1>");
		return sb.toString();
	}

	@Override
	protected String writeQuestProgress(Player player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return "Você deve fazer a missão para verificar seu progresso!";
		}

		final StringBuilder sb = new StringBuilder();
		sb.append("Progresso:<br>");
		sb.append(HtmlUtils.getWeightGauge(450, st.getInt("KILLS"), st.getInt("KILLS_NEEDED"), false));
		sb.append("<br>");

		sb.append("Você deve pegar " + st.getInt("KILLS_NEEDED") + " peixes para completar a missão.<br1>");
		sb.append("Tipo e grau não são importantes.<br1>");
		return sb.toString();
	}

	@Override
	public void onQuestStart(QuestState st)
	{
		st.set("KILLS", "0");
		st.set("KILLS_NEEDED", getRandomKillsRequired());
	}

	private class OnFishDie implements OnFishDieListener
	{
		@Override
		public void onFishDied(Player player, int fishId, boolean isMonster)
		{
			if (!isMonster && (fishId > 0))
			{
				final QuestState st = player.getQuestState(getName());
				if ((st == null) || st.isCompleted())
				{
					return;
				}
				st.set("KILLS", st.getInt("KILLS") + 1);
				if (st.getInt("KILLS") >= st.getInt("KILLS_NEEDED"))
				{
					st.setState(COMPLETED);
					st.setRestartTime();
					onQuestFinish(st);
				}
				else
				{
					showScreenMessage(player, "progresso " + st.get("KILLS") + "/" + st.get("KILLS_NEEDED") + " concluído!", 5000);
				}
			}
		}
	}
}
