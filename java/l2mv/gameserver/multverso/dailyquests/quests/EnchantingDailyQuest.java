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

import l2mv.gameserver.listener.item.OnItemEnchantListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.multverso.dailyquests.AbstractDailyQuest;
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
		sb.append("Você deve encantar " + getMinKillsRequired() + " os " + getMaxKillsRequired() + " items (S-Grade).<br1>");
		sb.append("O nível mínimo para iniciar a missão é " + getMinLevel() + "<br>");
		sb.append("O nível mínimo de encantamento de uma armadura a ser contada é " + getSettings().getParameters().getString("ArmorMinEnchant") + ".<br>");
		sb.append("O nível mínimo de encantamento de uma arma a ser contada é " + getSettings().getParameters().getString("WeaponMinEnchant") + ".<br>");
		sb.append("Toda vez que você encantar um item acima do nível mínimo de encantamento, você ganhará um ponto<br1> o que significa que se você encantar uma armadura para +" + getSettings().getParameters().getString("ArmorMinEnchant") + " você ganhará um ponto ou uma arma para +" + getSettings().getParameters().getString("WeaponMinEnchant") + "<br>");
		sb.append("Por exemplo se você tem " + getSettings().getParameters().getString("WeaponMinEnchant") + " armas para encantar você pode encantar uma arma<br1> a partir de +" + getSettings().getParameters().getString("WeaponMinEnchant") + " a +" + (getSettings().getParameters().getInteger("WeaponMinEnchant") * 2) + " para concluir esta etapa ou " + getSettings().getParameters().getString("WeaponMinEnchant") + " armas diferentes para +" + getSettings().getParameters().getString("WeaponMinEnchant") + "<br>");
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
		sb.append("<font color=\"LEVEL\">Você deve encantar " + (needed - enchanted) + " S-Grade itens para completar a missão.</font><br>");
		sb.append("O nível mínimo de encantamento de uma armadura a ser contada é " + getSettings().getParameters().getString("ArmorMinEnchant") + ".<br1>");
		sb.append("O nível mínimo de encantamento de uma arma a ser contada é " + getSettings().getParameters().getString("WeaponMinEnchant") + ".<br>");
		sb.append("Toda vez que você encantar e um item acima do nível mínimo de encantamento, você ganhará um ponto<br1> o que significa que se você encantar uma armadura para +4, você ganhará um ponto ou uma arma para +7<br1>");
		sb.append("Por exemplo, se você tiver 7 armas para encantar, você pode encantar uma arma<br1> de +7 a +14 para concluir esta etapa ou 7 armas diferentes a +7<br>");
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
