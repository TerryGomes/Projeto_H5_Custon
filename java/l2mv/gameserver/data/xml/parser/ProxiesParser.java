package l2mv.gameserver.data.xml.parser;

import java.io.File;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.data.xml.AbstractFileParser;
import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.ProxiesHolder;
import l2mv.gameserver.utils.ProxyRequirement;
import l2mv.gameserver.utils.proxyrequirement.CharactersLevel;
import l2mv.gameserver.utils.proxyrequirement.IPSubnet;
import l2mv.gameserver.utils.proxyrequirement.MinimumCharMessages;

public final class ProxiesParser extends AbstractFileParser<ProxiesHolder>
{
	private static final Logger LOG = LoggerFactory.getLogger(ProxiesParser.class);

	private ProxiesParser()
	{
		super(ProxiesHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "config/proxies.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "proxies.dtd";
	}

	@Override
	protected void readData(Element rootElement)
	{
		final Iterator<Element> proxyIterator = rootElement.elementIterator();
		while (proxyIterator.hasNext())
		{
			final Element proxyData = proxyIterator.next();
			final String ip = proxyData.attributeValue("ip");
			final ProxyRequirement requirement = parseRequirement(proxyData);
			getHolder().addNewProxy(requirement, ip);
		}
	}

	private static ProxyRequirement parseRequirement(Element data)
	{
		final String name = data.getName();
		switch (name)
		{
		case "minChatMessages":
		{
			return parseMinChatMessages(data);
		}
		case "level":
		{
			return parseLevel(data);
		}
		case "mask":
		{
			return parseMask(data);
		}
		default:
		{
			throw new AssertionError("Couldn't parse Proxy with Name: " + data.getName());
		}
		}
	}

	private static ProxyRequirement parseMinChatMessages(Element data)
	{
		final int all = data.attributeValue("all") == null ? -1 : Integer.parseInt(data.attributeValue("all"));
		final int shout = data.attributeValue("shout") == null ? -1 : Integer.parseInt(data.attributeValue("shout"));
		final int pm = data.attributeValue("pm") == null ? -1 : Integer.parseInt(data.attributeValue("pm"));
		final int trade = data.attributeValue("trade") == null ? -1 : Integer.parseInt(data.attributeValue("trade"));
		final int party = data.attributeValue("party") == null ? -1 : Integer.parseInt(data.attributeValue("party"));
		final int clan = data.attributeValue("clan") == null ? -1 : Integer.parseInt(data.attributeValue("clan"));
		final int ally = data.attributeValue("ally") == null ? -1 : Integer.parseInt(data.attributeValue("ally"));
		final int hero = data.attributeValue("hero") == null ? -1 : Integer.parseInt(data.attributeValue("hero"));
		return new MinimumCharMessages(all, shout, pm, trade, party, clan, ally, hero);
	}

	private static ProxyRequirement parseLevel(Element data)
	{
		final int minLevel = Integer.parseInt(data.attributeValue("min"));
		final int maxLevel = Integer.parseInt(data.attributeValue("max"));
		return new CharactersLevel(minLevel, maxLevel);
	}

	private static ProxyRequirement parseMask(Element data)
	{
		try
		{
			return new IPSubnet(data.attributeValue("mask"));
		}
		catch (UnknownHostException e)
		{
			LOG.error("Error while parsing Mask: " + data.attributeValue("mask"), e);
			return null;
		}
	}

	public static ProxiesParser getInstance()
	{
		return SingletonHolder.instance;
	}

	private static class SingletonHolder
	{
		private static final ProxiesParser instance = new ProxiesParser();
	}
}
