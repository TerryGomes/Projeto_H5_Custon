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

import l2mv.gameserver.fandc.dailyquests.AbstractDailyQuest;
import l2mv.gameserver.listener.item.OnItemEnchantListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.templates.item.ItemTemplate.Grade;
import l2mv.gameserver.utils.HtmlUtils;

/**
 * @author Gnacik
 */
public class EnchantingDailyQuest extends AbstractDailyQuest
{
	private static final String[] PREFIXES =
	{
		"ARMOR_",
		"WEAPON_"
	};

	public EnchantingDailyQuest()
	{
		CharListenerList.addGlobal(new OnItemEnchant());
	}

	@Override
	public int getQuestIntId()
	{
		// Random quest id
		return 35001;
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
		sb.append("You must enchant between " + getMinKillsRequired() + " and " + getMaxKillsRequired() + " items (S-Grade).<br1>");
		sb.append("Minimum level to start the quest is " + getMinLevel() + "<br>");
		sb.append("Minimum enchant level of an armor to be counted is " + getSettings().getParameters().getString("ArmorMinEnchant") + ".<br>");
		sb.append("Minimum enchant level of an weapon to be counted is " + getSettings().getParameters().getString("WeaponMinEnchant") + ".<br>");
		sb.append("Everytime you enchant and item above it's minimal enchant level you will gain one point<br1> which means if u enchant an armor to +" + getSettings().getParameters().getString("ArmorMinEnchant") + " you will gain a point or a weapon to +" + getSettings().getParameters().getString("WeaponMinEnchant") + "<br>");
		sb.append("For example if u have " + getSettings().getParameters().getString("WeaponMinEnchant") + " weapons to enchant you could enchant one weapon<br1> from +" + getSettings().getParameters().getString("WeaponMinEnchant") + " to +" + (getSettings().getParameters().getInteger("WeaponMinEnchant") * 2) + " in order to complete this step or " + getSettings().getParameters().getString("WeaponMinEnchant") + " different weapons to +"
					+ getSettings().getParameters().getString("WeaponMinEnchant") + "<br>");
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

		int armor = st.getInt("ARMOR_ENCHANTED");
		int armorNeed = st.getInt("ARMOR_ENCHANT_NEEDED");
		if (armorNeed <= 0)
		{
			armorNeed = getRandomKillsRequired();
			st.set("ARMOR_ENCHANT_NEEDED", armorNeed);
		}

		int weapon = st.getInt("WEAPON_ENCHANTED");
		int weaponNeed = st.getInt("WEAPON_ENCHANT_NEEDED");
		if (weaponNeed <= 0)
		{
			weaponNeed = getRandomKillsRequired();
			st.set("WEAPON_ENCHANT_NEEDED", weaponNeed);
		}

		final int needed = armorNeed + weaponNeed;
		final int enchanted = armor + weapon;

		final StringBuilder sb = new StringBuilder();
		sb.append("Progress info:<br>");
		sb.append("<table width=730 height=20 background=\"L2UI_CT1.Button_DF_Calculator\">");
		sb.append("<tr>");
		sb.append("<td fixwidth=\"5\"></td>");
		sb.append("<td fixwidth=\"30\">Type</td>");
		sb.append("<td fixwidth=\"120\">Progress</td>");
		sb.append("<td fixwidth=\"5\"></td>");
		sb.append("</tr>");
		sb.append("</table>");

		sb.append("<table width=725 height=20 background=\"L2UI_CT1.Windows.Windows_DF_TooltipBG\">");

		sb.append("<tr>");
		sb.append("<td fixwidth=5></td>");
		sb.append("<td fixwidth=\"30\">Armor</td>");
		sb.append("<td fixwidth=120>" + HtmlUtils.getWeightGauge(490, armor, armorNeed, false) + "</td>");
		sb.append("<td fixwidth=5></td>");
		sb.append("</tr>");

		sb.append("<tr>");
		sb.append("<td fixwidth=5></td>");
		sb.append("<td fixwidth=\"30\">Weapon</td>");
		sb.append("<td fixwidth=120>" + HtmlUtils.getWeightGauge(490, weapon, weaponNeed, false) + "</td>");
		sb.append("<td fixwidth=5></td>");
		sb.append("</tr>");

		sb.append("</table>");
		sb.append("<font color=\"LEVEL\">You must enchant " + (needed - enchanted) + " S-Grade items in order to complete the quest.</font><br>");
		sb.append("Minimum enchant level of an armor to be counted is " + getSettings().getParameters().getString("ArmorMinEnchant") + ".<br1>");
		sb.append("Minimum enchant level of an weapon to be counted is " + getSettings().getParameters().getString("WeaponMinEnchant") + ".<br>");
		sb.append("Everytime you enchant and item above it's minimal enchant level you will gain one point<br1> which means if u enchant an armor to +4 you will gain a point or a weapon to +7<br1>");
		sb.append("For example if u have 7 weapons to enchant you could enchant one weapon<br1> from +7 to +14 in order to complete this step or 7 different weapons to +7<br>");
		return sb.toString();
	}

	@Override
	public void onQuestStart(QuestState st)
	{
		st.set("ARMOR_ENCHANTED", "0");
		st.set("ARMOR_ENCHANT_NEEDED", getRandomKillsRequired());
		st.set("WEAPON_ENCHANTED", "0");
		st.set("WEAPON_ENCHANT_NEEDED", getRandomKillsRequired());
	}

	private class OnItemEnchant implements OnItemEnchantListener
	{
		@Override
		public void onEnchantFinish(Player player, ItemInstance item, boolean succeed)
		{
			if (succeed && (item != null))
			{
				final QuestState st = player.getQuestState(getName());
				if ((st == null) || st.isCompleted() || (item.getTemplate().getItemGradeSPlus() != Grade.S))
				{
					return;
				}
				final String prefix = item.isArmor() ? "ARMOR_" : item.isWeapon() ? "WEAPON_" : "";
				if (prefix.isEmpty())
				{
					return;
				}
				if ((item.isArmor() && (item.getEnchantLevel() < getSettings().getParameters().getInteger("ArmorMinEnchant", 4))) || (item.isWeapon() && (item.getEnchantLevel() < getSettings().getParameters().getInteger("WeaponMinEnchant", 7))))
				{
					return;
				}
				if (st.getInt(prefix + "ENCHANTED") < st.getInt(prefix + "ENCHANT_NEEDED"))
				{
					st.set(prefix + "ENCHANTED", st.getInt(prefix + "ENCHANTED") + 1);
					showScreenMessage(player, "progress " + st.get(prefix + "ENCHANTED") + "/" + st.get(prefix + "ENCHANT_NEEDED") + " completed!", 5000);
				}
				boolean allCompleted = true;
				for (String pref : PREFIXES)
				{
					if (st.getInt(pref + "ENCHANTED") < st.getInt(pref + "ENCHANT_NEEDED"))
					{
						allCompleted = false;
						break;
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
	}
}
