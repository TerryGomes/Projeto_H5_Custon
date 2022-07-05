package actions;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.base.Experience;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.instances.RaidBossInstance;
import l2f.gameserver.model.reward.CalculateRewardChances;
import l2f.gameserver.model.reward.RewardData;
import l2f.gameserver.model.reward.RewardGroup;
import l2f.gameserver.model.reward.RewardList;
import l2f.gameserver.model.reward.RewardType;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.stats.Stats;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.HtmlUtils;
import services.community.CommunityDropCalculator;

public abstract class RewardListInfo
{
	private static final NumberFormat pf = NumberFormat.getPercentInstance(Locale.ENGLISH);
	private static final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);

	static
	{
		pf.setMaximumFractionDigits(4);
		df.setMinimumFractionDigits(2);
	}

	public static void showInfo(Player player, NpcInstance npc)
	{
		double mod = npc.calcStat(Stats.REWARD_MULTIPLIER, 1.0, player, null);

		showInfo(player, npc.getTemplate(), npc instanceof RaidBossInstance, npc.isSiegeGuard(), mod);
	}

	public static void showInfo(Player player, NpcTemplate npcTemplate, boolean isBoss, boolean isSiegeGuard, double mod)
	{
		if (!Config.ALLOW_DROP_CALCULATOR)
		{
			return;
		}

		final int diff = NpcInstance.calculateLevelDiffForDrop(npcTemplate.level, player.isInParty() ? player.getParty().getLevel() : player.getLevel(), isBoss);
		mod *= Experience.penaltyModifier(diff, 9);

		NpcHtmlMessage htmlMessage = new NpcHtmlMessage(5);
		htmlMessage.replace("%npc_name%", npcTemplate.getName());

		// @SuppressWarnings("unused")
		// boolean icons = player.getVarB("DroplistIcons");

		if (mod <= 0)
		{
			htmlMessage.setFile("actions/rewardlist_to_weak.htm");
			player.sendPacket(htmlMessage);
			return;
		}

		if (npcTemplate.getRewards().isEmpty())
		{
			htmlMessage.setFile("actions/rewardlist_empty.htm");
			player.sendPacket(htmlMessage);
			return;
		}

		htmlMessage.setFile("actions/rewardlist_info.htm");

		StringBuilder builder = new StringBuilder(100);
		if (npcTemplate.getRewards().containsKey(RewardType.SWEEP))
		{
			builder.append("<font name=\"hs12\" color=127b21>Spoil:</font><br>");
			showListedRewards(builder, RewardType.SWEEP, npcTemplate.getRewardList(RewardType.SWEEP), player, npcTemplate);
			builder.append("<br>");
		}
		if (npcTemplate.getRewards().containsKey(RewardType.RATED_GROUPED))
		{
			builder.append("<font name=\"hs12\" color=127b21>Drop:</font><br>");
			showListedRewards(builder, RewardType.RATED_GROUPED, npcTemplate.getRewardList(RewardType.RATED_GROUPED), player, npcTemplate);
			builder.append("<br>");
		}
		RewardType[] rest =
		{
			RewardType.NOT_RATED_GROUPED,
			RewardType.NOT_RATED_NOT_GROUPED
		};
		for (RewardType type : rest)
		{
			if (npcTemplate.getRewards().containsKey(type))
			{
				showListedRewards(builder, type, npcTemplate.getRewardList(type), player, npcTemplate);
			}
		}
		htmlMessage.replace("%info%", builder.toString());
		player.sendPacket(htmlMessage);
	}

	private static void showListedRewards(StringBuilder tmp, RewardType type, RewardList rewardList, Player player, NpcTemplate template)
	{
		for (RewardGroup g : rewardList)
		{
			List<RewardData> items = g.getItems();

			tmp.append("<table>");
			for (RewardData d : items)
			{
				String icon = d.getItem().getIcon();
				if (icon == null || icon.equals(StringUtils.EMPTY))
				{
					icon = "icon.etc_question_mark_i00";
				}
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238><font color=a47a3e>").append(HtmlUtils.htmlItemName(d.getItemId())).append("</font><br1>");

				long[] counts = CalculateRewardChances.getDropCounts(player, template, type != RewardType.SWEEP, d.getItemId());
				String chance = CalculateRewardChances.getDropChance(player, template, type != RewardType.SWEEP, d.getItemId());
				tmp.append("<font color=\"b09979\">[").append(counts[0]).append("...").append(counts[1]).append("]&nbsp;");
				tmp.append(CommunityDropCalculator.formatDropChance(chance)).append("</font></td></tr>");
			}
			tmp.append("</table>");
		}
	}

	public static void notRatedGroupedRewardList(StringBuilder tmp, RewardList list, double mod)
	{
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><center><font color=\"779d2c\">").append(list.getType()).append("</font></center></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		for (RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();
			double gchance = g.getChance();

			tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
			tmp.append("<tr><td>");
			tmp.append("<table width=270 border=0 bgcolor=333333>");
			tmp.append("<tr><td width=170><font color=\"a2a0a2\">Group Chance: </font><font color=\"b09979\">").append(pf.format(gchance / RewardList.MAX_CHANCE)).append("</font></td>");
			tmp.append("<td width=100 align=right>");
			tmp.append("</td></tr>");
			tmp.append("</table>").append("</td></tr>");

			tmp.append("<tr><td><table>");
			for (RewardData d : items)
			{
				String icon = d.getItem().getIcon();
				if (icon == null || icon.equals(StringUtils.EMPTY))
				{
					icon = "icon.etc_question_mark_i00";
				}
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(Math.round(d.getMinDrop())).append("..").append(Math.round(d.getMaxDrop())).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
			}
			tmp.append("</table></td></tr>");
		}

		tmp.append("</table>");
	}

	public static void notGroupedRewardList(StringBuilder tmp, RewardList list, double rate, double mod)
	{
		tmp.append("<table width=270 border=0>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");
		tmp.append("<tr><td><table width=270 border=0><tr><td><center><font color=\"779d2c\">").append(list.getType()).append("</font></center></td></tr></table></td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareWhite\" width=270 height=1> </td></tr>");
		tmp.append("<tr><td><img src=\"L2UI.SquareBlank\" width=270 height=10> </td></tr>");

		tmp.append("<tr><td><table>");
		for (RewardGroup g : list)
		{
			List<RewardData> items = g.getItems();
			double gmod = mod;
			double grate;
			double gmult;

			if (rate == 0)
			{
				continue;
			}

			grate = rate;

			if (g.notRate())
			{
				grate = Math.min(gmod, 1.0);
			}
			else
			{
				grate *= gmod;
			}

			gmult = Math.ceil(grate);

			for (RewardData d : items)
			{
				double imult = d.notRate() ? 1.0 : gmult;
				String icon = d.getItem().getIcon();
				if (icon == null || icon.equals(StringUtils.EMPTY))
				{
					icon = "icon.etc_question_mark_i00";
				}
				tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238>").append(HtmlUtils.htmlItemName(d.getItemId())).append("<br1>");
				tmp.append("<font color=\"b09979\">[").append(d.getMinDrop()).append("..").append(Math.round(d.getMaxDrop() * imult)).append("]&nbsp;");
				tmp.append(pf.format(d.getChance() / RewardList.MAX_CHANCE)).append("</font></td></tr>");
			}
		}

		tmp.append("</table></td></tr>");
		tmp.append("</table>");
	}
}