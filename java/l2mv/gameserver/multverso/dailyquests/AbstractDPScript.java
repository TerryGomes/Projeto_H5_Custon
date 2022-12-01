/*
 * Copyright (C) 2004-2013 L2J DataPack
 * This file is part of L2J DataPack.
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2mv.gameserver.multverso.dailyquests;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2mv.commons.util.Rnd;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.multverso.dailyquests.drops.Droplist;
import l2mv.gameserver.multverso.dailyquests.drops.DroplistItem;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.stats.Env;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.DocumentParser;
import l2mv.gameserver.utils.PropertiesParser;
import l2mv.gameserver.utils.Util;

/**
 * @author UnAfraid
 */
public abstract class AbstractDPScript extends Quest
{
	protected static final Logger _log = LoggerFactory.getLogger(AbstractDPScript.class);

	private ConfigurationParser _parser = null;
	private PropertiesParser _properties = null;

	protected void load()
	{
	}

	protected void loadProperties(File file)
	{
		if (_properties == null)
		{
			_properties = new PropertiesParser(file);
		}
	}

	protected String getProperty(String key, String defaultValue)
	{
		if (_properties == null)
		{
			return defaultValue;
		}
		return _properties.getString(key, defaultValue);
	}

	protected PropertiesParser getProperties()
	{
		return _properties;
	}

	/**
	 * This method is called when using parseFile, parseDirectory methods.<br>
	 * Containing XML {@link Document} with all the data from specified file/folder.
	 * @param doc
	 */
	protected void parseDocument()
	{
	}

	/**
	 * This method is called when using parseFile, parseDirectory methods.<br>
	 * Containing XML {@link Document} with all the data from specified file/folder.
	 * @param doc
	 */
	protected void parseDocument(Document doc)
	{
	}

	private final class ConfigurationParser extends DocumentParser
	{
		protected ConfigurationParser()
		{
		}

		@Override
		public void load()
		{
		}

		@Override
		protected Document getCurrentDocument()
		{
			return super.getCurrentDocument();
		}

		@Override
		protected void parseFile(File f)
		{
			super.parseFile(f);
		}

		@Override
		protected boolean parseDirectory(File dir, boolean recursive)
		{
			return super.parseDirectory(dir, recursive);
		}

		@Override
		protected boolean parseDirectory(String path)
		{
			return super.parseDirectory(path);
		}

		@Override
		protected boolean parseDirectory(String path, boolean recursive)
		{
			return super.parseDirectory(path, recursive);
		}

		@Override
		protected void parseDocument()
		{
			AbstractDPScript.this.parseDocument();
			AbstractDPScript.this.parseDocument(getCurrentDocument());
		}

		@Override
		protected void parseDatapackFile(String path)
		{
			super.parseDatapackFile(path);
		}
	}

	/**
	 * Gets the current file.
	 * @return the current file
	 */
	protected final File getCurrentFile()
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		return _parser.getCurrentFile();
	}

	/**
	 * Gets the current document.
	 * @return the current document
	 */
	protected final Document getCurrentDocument()
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		return _parser.getCurrentDocument();
	}

	/**
	 * Wrapper for {@link #parseFile(File)} method.
	 * @param path the relative path to the datapack root of the XML file to parse.
	 */
	protected final void parseDatapackFile(String path)
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		_parser.parseDatapackFile(path);
	}

	/**
	 * Parses a single XML file.<br>
	 * If the file was successfully parsed, call {@link #parseXMLDocument(Document)} for the parsed document.<br>
	 * <b>Validation is enforced.</b>
	 * @param f the XML file to parse.
	 */
	protected final void parseFile(File f)
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		_parser.parseFile(f);
	}

	/**
	 * Parses a single XML file.<br>
	 * If the file was successfully parsed, call {@link #parseXMLDocument(Document)} for the parsed document.<br>
	 * <b>Validation is enforced.</b>
	 * @param f the XML file to parse.
	 */
	protected final void parseFile(String file)
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		_parser.parseFile(new File(file));
	}

	/**
	 * Wrapper for {@link #parseDirectory(File, boolean)}.
	 * @param file the path to the directory where the XML files are.
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected final boolean parseDirectory(File file)
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		return _parser.parseDirectory(file, false);
	}

	/**
	 * Wrapper for {@link #parseDirectory(File, boolean)}.
	 * @param path the path to the directory where the XML files are.
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected final boolean parseDirectory(String path)
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		return _parser.parseDirectory(path);
	}

	/**
	 * Wrapper for {@link #parseDirectory(File, boolean)}.
	 * @param path the path to the directory where the XML files are.
	 * @param recursive parses all sub folders if there is.
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected final boolean parseDirectory(String path, boolean recursive)
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		return _parser.parseDirectory(path, recursive);
	}

	/**
	 * Loads all XML files from {@code path} and calls {@link #parseFile(File)} for each one of them.
	 * @param dir the directory object to scan.
	 * @param recursive parses all sub folders if there is.
	 * @return {@code false} if it fails to find the directory, {@code true} otherwise.
	 */
	protected final boolean parseDirectory(File dir, boolean recursive)
	{
		if (_parser == null)
		{
			_parser = new ConfigurationParser();
		}
		return _parser.parseDirectory(dir, recursive);
	}

	/**
	 * Parses a boolean value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Boolean parseBoolean(Node node, Boolean defaultValue)
	{
		return node != null ? Boolean.valueOf(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses a boolean value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Boolean parseBoolean(Node node)
	{
		return parseBoolean(node, null);
	}

	/**
	 * Parses a boolean value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Boolean parseBoolean(NamedNodeMap attrs, String name)
	{
		return parseBoolean(attrs.getNamedItem(name));
	}

	/**
	 * Parses a boolean value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Boolean parseBoolean(NamedNodeMap attrs, String name, Boolean defaultValue)
	{
		return parseBoolean(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses a byte value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Byte parseByte(Node node, Byte defaultValue)
	{
		return node != null ? Byte.valueOf(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses a byte value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Byte parseByte(Node node)
	{
		return parseByte(node, null);
	}

	/**
	 * Parses a byte value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Byte parseByte(NamedNodeMap attrs, String name)
	{
		return parseByte(attrs.getNamedItem(name));
	}

	/**
	 * Parses a byte value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Byte parseByte(NamedNodeMap attrs, String name, Byte defaultValue)
	{
		return parseByte(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses a short value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Short parseShort(Node node, Short defaultValue)
	{
		return node != null ? Short.valueOf(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses a short value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Short parseShort(Node node)
	{
		return parseShort(node, null);
	}

	/**
	 * Parses a short value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Short parseShort(NamedNodeMap attrs, String name)
	{
		return parseShort(attrs.getNamedItem(name));
	}

	/**
	 * Parses a short value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Short parseShort(NamedNodeMap attrs, String name, Short defaultValue)
	{
		return parseShort(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses an int value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected int parseInt(Node node, Integer defaultValue)
	{
		return node != null ? Integer.parseInt(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses an int value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected int parseInt(Node node)
	{
		return parseInt(node, -1);
	}

	/**
	 * Parses a int value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected int parseInt(NamedNodeMap attrs, String name, int defaultValue)
	{
		return parseInt(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses a int value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected int parseInt(NamedNodeMap attrs, String name)
	{
		return parseInt(attrs.getNamedItem(name), -1);
	}

	/**
	 * Parses an integer value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Integer parseInteger(Node node, Integer defaultValue)
	{
		return node != null ? Integer.valueOf(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses an integer value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Integer parseInteger(Node node)
	{
		return parseInteger(node, null);
	}

	/**
	 * Parses an integer value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Integer parseInteger(NamedNodeMap attrs, String name)
	{
		return parseInteger(attrs.getNamedItem(name));
	}

	/**
	 * Parses an integer value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Integer parseInteger(NamedNodeMap attrs, String name, Integer defaultValue)
	{
		return parseInteger(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses a long value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Long parseLong(Node node, Long defaultValue)
	{
		return node != null ? Long.valueOf(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses a long value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Long parseLong(Node node)
	{
		return parseLong(node, null);
	}

	/**
	 * Parses a long value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Long parseLong(NamedNodeMap attrs, String name)
	{
		return parseLong(attrs.getNamedItem(name));
	}

	/**
	 * Parses a long value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Long parseLong(NamedNodeMap attrs, String name, Long defaultValue)
	{
		return parseLong(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses a float value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Float parseFloat(Node node, Float defaultValue)
	{
		return node != null ? Float.valueOf(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses a float value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Float parseFloat(Node node)
	{
		return parseFloat(node, null);
	}

	/**
	 * Parses a float value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Float parseFloat(NamedNodeMap attrs, String name)
	{
		return parseFloat(attrs.getNamedItem(name));
	}

	/**
	 * Parses a float value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Float parseFloat(NamedNodeMap attrs, String name, Float defaultValue)
	{
		return parseFloat(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses a double value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Double parseDouble(Node node, Double defaultValue)
	{
		return node != null ? Double.valueOf(node.getNodeValue()) : defaultValue;
	}

	/**
	 * Parses a double value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Double parseDouble(Node node)
	{
		return parseDouble(node, null);
	}

	/**
	 * Parses a double value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected Double parseDouble(NamedNodeMap attrs, String name)
	{
		return parseDouble(attrs.getNamedItem(name));
	}

	/**
	 * Parses a double value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected Double parseDouble(NamedNodeMap attrs, String name, Double defaultValue)
	{
		return parseDouble(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses a string value.
	 * @param node the node to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected String parseString(Node node, String defaultValue)
	{
		return node != null ? node.getNodeValue() : defaultValue;
	}

	/**
	 * Parses a string value.
	 * @param node the node to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected String parseString(Node node)
	{
		return parseString(node, null);
	}

	/**
	 * Parses a string value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @return if the node is not null, the value of the parsed node, otherwise null
	 */
	protected String parseString(NamedNodeMap attrs, String name)
	{
		return parseString(attrs.getNamedItem(name));
	}

	/**
	 * Parses a string value.
	 * @param attrs the attributes
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null, the value of the parsed node, otherwise the default value
	 */
	protected String parseString(NamedNodeMap attrs, String name, String defaultValue)
	{
		return parseString(attrs.getNamedItem(name), defaultValue);
	}

	/**
	 * Parses an enumerated value.
	 * @param <T> the enumerated type
	 * @param node the node to parse
	 * @param clazz the class of the enumerated
	 * @param defaultValue the default value
	 * @return if the node is not null and the node value is valid the parsed value, otherwise the default value
	 */
	protected <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz, T defaultValue)
	{
		if (node == null)
		{
			return defaultValue;
		}

		try
		{
			return Enum.valueOf(clazz, node.getNodeValue());
		}
		catch (IllegalArgumentException e)
		{
			_log.warn("[" + getCurrentFile().getName() + "] Invalid value specified for node: " + node.getNodeName() + " specified value: " + node.getNodeValue() + " should be enum value of \"" + clazz.getSimpleName() + "\" using default value: " + defaultValue);
			return defaultValue;
		}
	}

	/**
	 * Parses an enumerated value.
	 * @param <T> the enumerated type
	 * @param node the node to parse
	 * @param clazz the class of the enumerated
	 * @return if the node is not null and the node value is valid the parsed value, otherwise null
	 */
	protected <T extends Enum<T>> T parseEnum(Node node, Class<T> clazz)
	{
		return parseEnum(node, clazz, null);
	}

	/**
	 * Parses an enumerated value.
	 * @param <T> the enumerated type
	 * @param attrs the attributes
	 * @param clazz the class of the enumerated
	 * @param name the name of the attribute to parse
	 * @return if the node is not null and the node value is valid the parsed value, otherwise null
	 */
	protected <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name)
	{
		return parseEnum(attrs.getNamedItem(name), clazz);
	}

	/**
	 * Parses an enumerated value.
	 * @param <T> the enumerated type
	 * @param attrs the attributes
	 * @param clazz the class of the enumerated
	 * @param name the name of the attribute to parse
	 * @param defaultValue the default value
	 * @return if the node is not null and the node value is valid the parsed value, otherwise the default value
	 */
	protected <T extends Enum<T>> T parseEnum(NamedNodeMap attrs, Class<T> clazz, String name, T defaultValue)
	{
		return parseEnum(attrs.getNamedItem(name), clazz, defaultValue);
	}

	public String getHtm(Player player, String fileName)
	{
		final String content = HtmCache.getInstance().getNotNull("DailyQuests/" + fileName, player);
		if (content == null)
		{
			_log.warn("Missing html: " + fileName + " on DailyQuests!", new IllegalStateException());
		}
		return content;
	}

	public void showHtml(Player player, String fileName)
	{
		final String html = getHtm(player, fileName);
		if (html != null)
		{
			final NpcHtmlMessage msg = new NpcHtmlMessage(0);
			msg.setHtml(html);
			player.sendPacket(msg);
		}
		else
		{
			_log.warn(getClass().getSimpleName() + ": Couldn't find " + fileName);
		}
	}

	protected final void log(String text)
	{
		_log.info(getClass().getSimpleName() + ": " + text);
	}

	protected String getItemName(int itemId)
	{
		switch (itemId)
		{
		case ItemTemplate.ITEM_ID_PC_BANG_POINTS:
		{
			return "Player Commendation Points";
		}
		case ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE:
		{
			return "Clan Reputation Points";
		}
		case ItemTemplate.ITEM_ID_FAME:
		{
			return "Fame";
		}
		case -400:
		{
			return "Experience";
		}
		case -500:
		{
			return "Skill Points";
		}
		default:
		{
			final ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
			if (item != null)
			{
				return item.getName();
			}
			break;
		}
		}
		return "N/A";
	}

	protected String getItemIcon(int itemId)
	{
		switch (itemId)
		{
		case ItemTemplate.ITEM_ID_PC_BANG_POINTS:
		{
			return "icon.etc_pccafe_point_i00";
		}
		case ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE:
		{
			return "icon.etc_bloodpledge_point_i00";
		}
		case ItemTemplate.ITEM_ID_FAME:
		{
			return "icon.pvp_point_i00";
		}
		case -400:
		{
			return "icon.etc_exp_point_i00";
		}
		case -500:
		{
			return "icon.etc_sp_point_i00";
		}
		default:
		{
			final ItemTemplate item = ItemHolder.getInstance().getTemplate(itemId);
			if (item != null)
			{
				return item.getIcon();
			}
			break;
		}
		}
		return "branchSys2.br_wing_of_sylphide_i00";
	}

	/**
	 * @param player
	 * @param droplist
	 * @param protect sets items as non sellable, tradable, droppable, freightable
	 */
	protected void rewardPlayers(Player player, Droplist droplist, boolean protect)
	{
		final List<DroplistItem> reward = Util.calculateDroplistItems(new Env(player, player, null), droplist);
		for (DroplistItem item : reward)
		{
			switch (item.getId())
			{
			case ItemTemplate.ITEM_ID_PC_BANG_POINTS:
			{
				player.addPcBangPoints((int) item.getCount(), false);
				break;
			}
			case ItemTemplate.ITEM_ID_CLAN_REPUTATION_SCORE:
			{
				if (player.getClan() != null)
				{
					player.getClan().incReputation((int) item.getCount(), true, "DailyQuest");
				}
				break;
			}
			case ItemTemplate.ITEM_ID_FAME:
			{
				player.setFame(player.getFame() + (int) item.getCount());
				player.broadcastUserInfo(true);
				break;
			}
			case -400:
			{
				player.addExpAndSp(item.getCount(), 0);
				break;
			}
			case -500:
			{
				player.addExpAndSp(0, (int) item.getCount());
				break;
			}
			default:
			{
				item.createItem();
				item.giveItem(player, true);
				// TODO: Protect support?
				/*
				 * final L2ItemInstance itemInstance = player.addItem(item);
				 * if (protect)
				 * {
				 * final ItemVariables vars = itemInstance.getVariables();
				 * vars.set(ItemVariables.SELLABLE, false);
				 * vars.set(ItemVariables.TRADABLE, false);
				 * vars.set(ItemVariables.DROPABLE, false);
				 * vars.set(ItemVariables.FREIGHTABLE, false);
				 * }
				 */
				break;
			}
			}
		}
	}

	/**
	 * Get a random integer from 0 (inclusive) to {@code max} (exclusive).<br>
	 * Use this method instead of importing {@link com.l2jserver.util.Rnd} utility.
	 * @param max the maximum value for randomization
	 * @return a random integer number from 0 to {@code max - 1}
	 */
	public static int getRandom(int max)
	{
		return Rnd.get(max);
	}

	/**
	 * Get a random integer from {@code min} (inclusive) to {@code max} (inclusive).<br>
	 * Use this method instead of importing {@link com.l2jserver.util.Rnd} utility.
	 * @param min the minimum value for randomization
	 * @param max the maximum value for randomization
	 * @return a random integer number from {@code min} to {@code max}
	 */
	public static int getRandom(int min, int max)
	{
		return Rnd.get(min, max);
	}
}