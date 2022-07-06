/*
 * Copyright (C) 2004-2018 L2J Server
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
package l2mv.gameserver.kara.vote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import l2mv.gameserver.model.Player;

/**
 * @author ProjectX
 */
public class Vote
{
	private final Player _player;
	private final SiteTemplate _template;
	private final Site _site;

	public Vote(Site site, Player player)
	{
		_player = player;
		_site = site;
		_template = VoteManager.getInstance().getSite(site);
	}

	public boolean hasVote()
	{
		String link = _site.getLink();

		if (link.contains("%API%"))
		{
			link = link.replace("%API%", _template.getAPI());
		}
		if (link.contains("%ID%"))
		{
			link = link.replace("%ID%", _template.getServerId());
		}
		if (link.contains("%USER%"))
		{
			link = link.replace("%USER%", _template.getUser());
		}
		if (link.contains("%IP%"))
		{
			link = link.replace("%IP%", _player.getClient().getConnection().getClient().getIpAddr());
		}

		try
		{
			URL url = new URL(link);
			url.openConnection().setConnectTimeout(5000);
			InputStream is = url.openStream();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is)))
			{
				String line;
				while ((line = br.readLine()) != null)
				{
					switch (_site)
					{
					case TOPZONE:
					case HOPZONE:
						if (line.contains("TRUE") || line.contains("true"))
						{
							return true;
						}
						break;
					case NETWORK:
						if (line.contains("1"))
						{
							return true;
						}
						break;
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
}