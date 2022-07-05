package l2f.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.FacebookCommentsHolder;

public final class FacebookCommentsParser extends AbstractFileParser<FacebookCommentsHolder>
{
	private static final String SERVER_NAME_REPLACEMENT = "%serverName%";

	private FacebookCommentsParser()
	{
		super(FacebookCommentsHolder.getInstance());
	}

	@Override
	public File getXMLFile()
	{
		return new File(Config.DATAPACK_ROOT, "data/facebook_comments.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "facebook_comments.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		final Iterator<Element> typeIterator = rootElement.elementIterator("type");
		while (typeIterator.hasNext())
		{
			final Element typeElement = typeIterator.next();
			final String typeName = typeElement.attributeValue("name");
			final Iterator<Element> commentIterator = typeElement.elementIterator("comment");
			while (commentIterator.hasNext())
			{
				final Element commentElement = commentIterator.next();
				String comment = commentElement.attributeValue("value");
				comment = comment.replace(SERVER_NAME_REPLACEMENT, Config.SERVER_NAME);
				getHolder().addNewComment(typeName, comment);
			}
		}
	}

	public static FacebookCommentsParser getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final FacebookCommentsParser INSTANCE = new FacebookCommentsParser();
	}
}
