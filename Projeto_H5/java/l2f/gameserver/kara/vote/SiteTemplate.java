/*
 * Copyright (C) 2004-2014 L2J Server
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Kara
 */
public class SiteTemplate
{
	private final List<VoteReward> _reward = new ArrayList<>();
	private final List<VoteBuff> _buff = new ArrayList<>();

	private String _api = "";
	private String _serverId = "";
	private String _user = "";

	private int _hourToVote;

	public Collection<VoteBuff> getBuffList()
	{
		return _buff;
	}

	public void addBuff(VoteBuff buff)
	{
		_buff.add(buff);
	}

	public Collection<VoteReward> getRewardList()
	{
		return _reward;
	}

	public void addReward(VoteReward reward)
	{
		_reward.add(reward);
	}

	public void setAPI(String api)
	{
		_api = api;
	}

	public String getAPI()
	{
		return _api;
	}

	public void setServerId(String id)
	{
		_serverId = id;
	}

	public String getServerId()
	{
		return _serverId;
	}

	public void setUser(String user)
	{
		_user = user;
	}

	public String getUser()
	{
		return _user;
	}

	public void setHourToVote(int hour)
	{
		_hourToVote = hour;
	}

	public int getHourToVote()
	{
		return _hourToVote;
	}
}