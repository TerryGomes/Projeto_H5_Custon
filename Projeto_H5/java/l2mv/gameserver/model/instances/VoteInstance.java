/*
 * Copyright (C) 2004-2015 L2J Server
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
package l2mv.gameserver.model.instances;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.kara.vote.Site;
import l2mv.gameserver.kara.vote.SiteTemplate;
import l2mv.gameserver.kara.vote.Vote;
import l2mv.gameserver.kara.vote.VoteBuff;
import l2mv.gameserver.kara.vote.VoteManager;
import l2mv.gameserver.kara.vote.VoteReward;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.ItemFunctions;

/**
 * @author Kara`
 */
public final class VoteInstance extends NpcInstance
{
	public VoteInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");

		Site site = Site.valueOf(st.nextToken().toUpperCase());
		SiteTemplate template = VoteManager.getInstance().getSite(site);

		String action = st.nextToken();

		if (!VoteManager.getInstance().canGetReward(player.getClient().getHWID(), site.getSite()))
		{
			player.sendMessage("You already " + (action.equalsIgnoreCase("vote") ? "voted" : "rewarded") + " for " + site.getSite());
			return;
		}

		switch (action)
		{
		case "reward":

			VoteManager.getInstance().addVotedPlayer(player.getClient().getHWID(), site.getSite(), System.currentTimeMillis() + 43200000);

			for (VoteReward r : template.getRewardList())
			{
				if (Rnd.get(100) <= r.getChance())
				{
					ItemFunctions.addItem(player, r.getItemId(), r.getCount(), true, "");
				}
			}

			for (VoteBuff b : template.getBuffList())
			{
				if (Rnd.get(100) <= b.getChance())
				{
					b.getSkill().getEffects(player, player, true, false);
				}
			}
			break;
		case "vote":

			Vote vote = new Vote(site, player);

			final boolean hasVote = vote.hasVote();

			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("mods/Vote/" + (hasVote ? "Reward.htm" : "Index.htm"));
			html.replace("%objectId%", String.valueOf(getObjectId()));
			html.replace("%site%", site.getSite());
			player.sendPacket(html);
			player.sendMessage(hasVote ? "You successfuly voted in " + site.getSite() : "You didn't vote in " + site.getSite());
			break;
		}
	}

	static String nextVoteAvailable(long lastVote, SiteTemplate template)
	{
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date(System.currentTimeMillis() - (lastVote - (template.getHourToVote() * 60 * 1000)));
		return df.format(date);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("mods/Vote/Index.htm");
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
}