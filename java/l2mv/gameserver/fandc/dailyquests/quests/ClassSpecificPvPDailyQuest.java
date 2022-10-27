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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2mv.gameserver.fandc.dailyquests.AbstractDailyQuest;
import l2mv.gameserver.listener.actor.OnDeathListener;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.utils.HtmlUtils;
import l2mv.gameserver.utils.Util;

/**
 * @author UnAfraid
 */
public class ClassSpecificPvPDailyQuest extends AbstractDailyQuest
{
	private final List<ClassDataHolder> _classData = new CopyOnWriteArrayList<>();

	public ClassSpecificPvPDailyQuest()
	{
		CharListenerList.addGlobal(new OnDeathList());
	}

	@Override
	public void parseNode(Node n)
	{
		for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
		{
			switch (d.getNodeName())
			{
			case "classes":
			{
				for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
				{
					if ("class".equals(cd.getNodeName()))
					{
						final NamedNodeMap attrs = cd.getAttributes();
						int id = parseInt(attrs, "id");
						String name = parseString(attrs, "name");
						int min = parseInt(attrs, "min");
						int max = parseInt(attrs, "max");
						_classData.add(new ClassDataHolder(id, name, min, max));
					}
				}
				break;
			}
			}
		}
	}

	@Override
	public int getQuestIntId()
	{
		// Random quest id
		return 35000;
	}

	/**
	 * @param player
	 * @param index
	 * @return
	 */
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
		sb.append("Class info:<br>");
		sb.append("<table width=725 height=20 background=\"L2UI_CT1.Button_DF_Calculator\">");
		sb.append("<tr>");
		sb.append("<td fixwidth=\"5\"></td>");
		sb.append("<td fixwidth=\"150\">Name</td>");
		sb.append("<td fixwidth=\"60\">Min</td>");
		sb.append("<td fixwidth=\"60\">Max</td>");
		sb.append("<td fixwidth=\"5\"></td>");
		sb.append("</tr>");
		sb.append("</table>");

		sb.append("<table width=725 height=20 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
		for (ClassDataHolder holder : _classData)
		{
			sb.append("<tr>");
			sb.append("<td fixwidth=5></td>");
			sb.append("<td fixwidth=150>" + holder.getClassName() + "</td>");
			sb.append("<td fixwidth=60>" + holder.getMinKills() + "</td>");
			sb.append("<td fixwidth=60>" + holder.getMaxKills() + "</td>");
			sb.append("<td fixwidth=5></td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("You must hunt down randomly between " + getMinKillsRequired() + " and " + getMaxKillsRequired() + " classes from the list above in order to complete the quest.<br1>");
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

		if (!st.isSet("CLASS_COUNT"))
		{
			st.set("CLASS_COUNT", 5);
		}

		final StringBuilder sb = new StringBuilder();
		sb.append("Class info:<br>");
		sb.append("<table width=730 height=20 background=\"L2UI_CT1.Button_DF_Calculator\">");
		sb.append("<tr>");
		sb.append("<td fixwidth=\"5\"></td>");
		sb.append("<td fixwidth=\"60\">Name</td>");
		sb.append("<td fixwidth=\"120\">Progress</td>");
		sb.append("<td fixwidth=\"5\"></td>");
		sb.append("</tr>");
		sb.append("</table>");

		sb.append("<table width=725 height=20 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");
		for (int i = 0; i < st.getInt("CLASS_COUNT"); i++)
		{
			sb.append("<tr>");
			sb.append("<td fixwidth=5></td>");
			sb.append("<td fixwidth=60>" + getClassNameById(st.getInt("KILLS_" + i + "_CLASS")) + "</td>");
			sb.append("<td fixwidth=120>" + HtmlUtils.getWeightGauge(420, st.getInt("KILLS_" + i), st.getInt("KILLS_" + i + "_NEEDED"), false) + "</td>");
			sb.append("<td fixwidth=5></td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("You must hunt down " + st.getInt("CLASS_COUNT") + " classes from the list above in order to complete the quest.<br1>");
		return sb.toString();
	}

	@Override
	public void onQuestStart(QuestState st)
	{
		final List<ClassDataHolder> list = getRandomClassList();
		int i = 0;
		st.set("CLASS_COUNT", list.size());
		for (ClassDataHolder holder : list)
		{
			st.set("KILLS_" + i, 0);
			st.set("KILLS_" + i + "_CLASS", holder.getClassId());
			st.set("KILLS_" + i + "_NEEDED", getRandom(holder.getMinKills(), holder.getMaxKills()));
			i++;
		}
	}

	@Override
	protected boolean validateRandomPartyMember(QuestState st, Player killer, Player member)
	{
		if (st != null)
		{
			if (!st.isSet("CLASS_COUNT"))
			{
				st.set("CLASS_COUNT", 5);
			}
			if ((st.getState() == Quest.STARTED) && Util.checkIfInRange(1000, killer, member, true))
			{
				for (int i = 0; i < st.getInt("CLASS_COUNT"); i++)
				{
					if (st.getInt("KILLS_" + i + "_CLASS") == member.getActiveClass().getClassId())
					{
						return st.getInt("KILLS_" + i) < st.getInt("KILLS_" + i + "_NEEDED");
					}
				}
			}
		}
		return false;
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
			if (!validateKill(player, killer != null ? killer.getPlayer() : null))
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

			if (!st.isSet("CLASS_COUNT"))
			{
				st.set("CLASS_COUNT", 5);
			}

			for (int i = 0; i < st.getInt("CLASS_COUNT"); i++)
			{
				if (st.getInt("KILLS_" + i + "_CLASS") == player.getActiveClass().getClassId())
				{
					if (st.getInt("KILLS_" + i) < st.getInt("KILLS_" + i + "_NEEDED"))
					{
						st.set("KILLS_" + i, st.getInt("KILLS_" + i) + 1);
					}
					showScreenMessage(attackerMember, "progress " + getClassNameById(player.getActiveClass().getClassId()) + " " + st.getInt("KILLS_" + i) + "/" + st.getInt("KILLS_" + i + "_NEEDED"), 5000);
					break;
				}
			}
			boolean allCompleted = true;
			for (int i = 0; i < st.getInt("CLASS_COUNT"); i++)
			{
				if ((st.getInt("KILLS_" + i) < st.getInt("KILLS_" + i + "_NEEDED")))
				{
					allCompleted = false;
				}
			}
			if (allCompleted)
			{
				st.setState(COMPLETED);
				st.setRestartTime();
				onQuestFinish(st);
			}
		}
	}

	private String getClassNameById(int id)
	{
		for (ClassDataHolder holder : _classData)
		{
			if (holder.getClassId() == id)
			{
				return holder.getClassName();
			}
		}
		return null;
	}

	private List<ClassDataHolder> getRandomClassList()
	{
		final List<ClassDataHolder> list = new ArrayList<>();
		final int classCount = getRandomKillsRequired();
		while (list.size() != classCount)
		{
			final ClassDataHolder holder = _classData.get(getRandom(_classData.size()));
			if (!list.contains(holder))
			{
				list.add(holder);
			}
		}
		return list;
	}

	private static class ClassDataHolder
	{
		private final int _classId;
		private final String _className;
		private final int _minKills;
		private final int _maxKills;

		public ClassDataHolder(int classId, String className, int minKills, int maxKills)
		{
			_classId = classId;
			_className = className;
			_minKills = minKills;
			_maxKills = maxKills;
		}

		public int getClassId()
		{
			return _classId;
		}

		public String getClassName()
		{
			return _className;
		}

		public int getMinKills()
		{
			return _minKills;
		}

		public int getMaxKills()
		{
			return _maxKills;
		}
	}
}
