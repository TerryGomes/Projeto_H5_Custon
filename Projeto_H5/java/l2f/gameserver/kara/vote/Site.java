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
package l2f.gameserver.kara.vote;

/**
 * @author ProjectX
 */
public enum Site
{
	TOPZONE("Topzone", "http://l2topzone.com/api.php?API_KEY=%API%&SERVER_ID=%ID%&IP=%IP%"), HOPZONE("Hopzone", "https://api.hopzone.net/lineage2/vote?token=%API%&ip_address=%IP%"),
	NETWORK("Network", "https://l2network.eu/index.php?a=in&u=%USER%&ipc=%IP%");

	String _site;
	String _link;

	Site(String site, String link)
	{
		_site = site;
		_link = link;
	}

	public String getSite()
	{
		return _site;
	}

	public String getLink()
	{
		return _link;
	}
}