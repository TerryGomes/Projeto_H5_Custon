/*
 * Copyright (C) 2014-2015 Vote Rewarding System
 * This file is part of Vote Rewarding System.
 * Vote Rewarding System is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Vote Rewarding System is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.fandc.votingengine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2mv.gameserver.utils.DocumentParser;

/**
 * @author UnAfraid
 */
public class VotingSettings extends DocumentParser
{
	private String _votingCommand;
	private long _votingInterval;
	private String _color;
	private final RewardList _droplist = new RewardList();
	private final Map<MessageType, String> _messages = new HashMap<>();
	private final Map<String, Data> _zone = new HashMap<>();
	private boolean _enabled;

	public class Data
	{
		int _id;
		String _key;

		public Data(int id, String key)
		{
			_id = id;
			_key = key;
		}

		public int getid()
		{
			return _id;
		}

		public void setid(int id)
		{
			_id = id;
		}

		public String getkey()
		{
			return _key;
		}

		public void set_key(String key)
		{
			_key = key;
		}
	}

	protected VotingSettings()
	{
		load();
	}

	@Override
	public void load()
	{
		_droplist.getGroups().clear();
		parseFile(new File("config/VotingReward.xml"));
		_log.info(getClass().getSimpleName() + ": Loaded " + _messages.size() + " messages, " + _droplist.getGroups().size() + " drops!");
	}

	@Override
	protected void parseDocument()
	{
		NamedNodeMap attrs;
		for (Node n = getCurrentDocument().getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node b = n.getFirstChild(); b != null; b = b.getNextSibling())
				{
					switch (b.getNodeName())
					{
					case "config":
					{
						attrs = b.getAttributes();
						final boolean enabled = parseBoolean(attrs, "enableVoteReward", false);
						_enabled = enabled;
						break;
					}
					case "api":
					{
						attrs = b.getAttributes();
						final String votingzone = parseString(attrs, "zone");
						final int serverId = parseInteger(attrs, "id");
						final String apiKey = parseString(attrs, "key");
						_zone.put(votingzone, new Data(serverId, apiKey));
						break;
					}
					case "voting":
					{
						attrs = b.getAttributes();
						_votingCommand = parseString(attrs, "command", "getreward");
						_votingInterval = VotingUtil.parseTimeString(parseString(attrs, "interval", "12hours"));
						break;
					}
					case "nameColor":
					{
						attrs = b.getAttributes();
						final boolean enabled = parseBoolean(attrs, "enable");
						final String hexColor = parseString(attrs, "color");
						if (enabled)
						{
							_color = hexColor;
						}
						break;
					}
					case "messages":
					{
						for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling())
						{
							switch (a.getNodeName())
							{
							case "message":
							{
								attrs = a.getAttributes();
								final MessageType type = parseEnum(attrs, MessageType.class, "type");
								final String content = a.getTextContent();
								_messages.put(type, content);
								break;
							}
							}
						}
						break;
					}
					case "rewards":
					{
						for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling())
						{
							switch (a.getNodeName())
							{
							case "group":
							{
								attrs = a.getAttributes();
								final float groupChance = parseFloat(attrs, "chance");
								final RewardGroup group = new RewardGroup(groupChance);
								for (Node z = a.getFirstChild(); z != null; z = z.getNextSibling())
								{
									switch (z.getNodeName())
									{
									case "item":
									{
										attrs = z.getAttributes();
										final int itemId = parseInteger(attrs, "id");
										final int min = parseInteger(attrs, "min");
										final int max = parseInteger(attrs, "max");
										final float chance = parseFloat(attrs, "chance");
										group.addItem(new RewardItem(itemId, min, max, chance));
										break;
									}
									}
								}
								_droplist.addGroup(group);
								break;
							}
							}
						}
						break;
					}
					}
				}
			}
		}
	}

	public String getVotingCommand()
	{
		return _votingCommand;
	}

	public long getVotingInterval()
	{
		return _votingInterval;
	}

	public String getColor()
	{
		return _color;
	}

	public RewardList getDroplist()
	{
		return _droplist;
	}

	public String getMessage(MessageType type)
	{
		return _messages.get(type);
	}

	public Map<String, Data> getZones()
	{
		return _zone;
	}

	public int getServerId(String zoneName)
	{
		return _zone.get(zoneName).getid();
	}

	public String getAPIKey(String zoneName)
	{
		return _zone.get(zoneName).getkey();
	}

	public static enum MessageType
	{
		ON_SUCCESS, ON_NOT_VOTED, ON_REUSE,
	}

	public boolean isEnabled()
	{
		return _enabled;
	}

	public void setenabled(boolean enabled)
	{
		_enabled = enabled;
	}

	public static final VotingSettings getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final VotingSettings INSTANCE = new VotingSettings();
	}
}
