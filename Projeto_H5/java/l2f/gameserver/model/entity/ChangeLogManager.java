package l2f.gameserver.model.entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import l2f.gameserver.Config;
import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.hwid.HwidEngine;
import l2f.gameserver.hwid.HwidGamer;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.Language;

public class ChangeLogManager
{
	private static final Logger LOG = LoggerFactory.getLogger(ChangeLogManager.class);
	private final List<Change> changeList;

	private enum FixType
	{
		BUG, NEW_FEATURE, IMPROVEMENT
	}

	public ChangeLogManager()
	{
		changeList = new LinkedList<>();
		loadChangeLog();
	}

	public int getNotSeenChangeLog(Player player)
	{
		if (Config.ALLOW_HWID_ENGINE)
		{
			HwidGamer gamer = HwidEngine.getInstance().getGamerByHwid(player.getHWID());
			if (gamer != null)
			{
				int lastSeen = gamer.getSeenChangeLog();
				if (lastSeen < getLatestChangeId())
				{
					return getLatestChangeId();
				}
			}
		}
		return -1;
	}

	public int getLatestChangeId()
	{
		return changeList.size() - 1;
	}

	public String getChangeLog(int index)
	{
		Change change = changeList.get(index);

		StringBuilder fixesBuilder = new StringBuilder();
		for (Fix singleFix : change.getFixes())
		{
			fixesBuilder.append("<table width=280>");
			fixesBuilder.append("<tr><td align=left><font color=\"").append(getTextColor(singleFix.getType())).append("\"> - ");
			fixesBuilder.append(singleFix.getDesc());
			fixesBuilder.append("</font></td></tr></table>");
		}

		StringBuilder pagesBuilder = new StringBuilder();
		pagesBuilder.append("<table><tr>");
		if (index > 0)
		{
			pagesBuilder.append("<td><button value=\"Previous\" action=\"bypass -h ShowChangeLogPage ").append(index - 1).append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		}
		if (index < getLatestChangeId())
		{
			pagesBuilder.append("<td><button value=\"Next\" action=\"bypass -h ShowChangeLogPage ").append(index + 1).append("\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		}

		pagesBuilder.append("</tr></table>");

		String html = HtmCache.getInstance().getNotNull("command/changeLog.htm", Language.ENGLISH);
		html = html.replace("%date%", change.getDate());
		html = html.replace("%fixes%", fixesBuilder.toString());
		html = html.replace("%leftPageBtn%", index > 0 ? "<button value=\"Previous\" action=\"bypass -h ShowChangeLogPage " + (index - 1) + "\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">" : "<br>");
		html = html.replace("%rightPageBtn%", index < getLatestChangeId() ? "<button value=\"Next\" action=\"bypass -h ShowChangeLogPage " + (index + 1) + "\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">" : "<br>");
		return html;
	}

	private static String getTextColor(FixType type)
	{
		switch (type)
		{
		case BUG:
			return "9b2626";
		case NEW_FEATURE:
			return "30b33a";
		case IMPROVEMENT:
			return "b5b71f";
		}
		return "ffffff";
	}

	private static class Change
	{
		private final int index;
		private final String date;
		private final List<Fix> fixes;

		protected Change(int index, String date)
		{
			this.index = index;
			this.date = date;
			fixes = new ArrayList<>();
		}

		public int getIndex()
		{
			return index;
		}

		public String getDate()
		{
			return date;
		}

		public void addFix(Fix fix)
		{
			fixes.add(fix);
		}

		public List<Fix> getFixes()
		{
			return fixes;
		}
	}

	private static class Fix
	{
		private final FixType type;
		private final String desc;

		protected Fix(FixType type, String desc)
		{
			this.type = type;
			this.desc = desc;
		}

		protected FixType getType()
		{
			return type;
		}

		protected String getDesc()
		{
			return desc;
		}
	}

	public void reloadChangeLog()
	{
		changeList.clear();
		loadChangeLog();
	}

	private void loadChangeLog()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		Document doc;
		try
		{
			doc = factory.newDocumentBuilder().parse(new File(Config.DATAPACK_ROOT, "data/changeLog.xml"));

			NodeList list = doc.getElementsByTagName("change");
			for (int i = 0; i < list.getLength(); i++)
			{
				Element element = (Element) list.item(i);
				Change newChange = parseNewChange(i, element);

				changeList.add(newChange);
			}
		}
		catch (DOMException | IOException | IllegalArgumentException | ParserConfigurationException | SAXException e)
		{
			LOG.error("Error while loading ChangeLog ", e);
		}
		Collections.reverse(changeList);
	}

	private static Change parseNewChange(int index, Element element)
	{
		String date = element.getAttribute("date");

		Change change = new Change(index, date);
		NodeList firstNodeElementList = element.getElementsByTagName("fix");
		boolean ended = false;
		int line = 0;
		while (!ended)
		{
			try
			{
				Fix newFix = parseFix(line, firstNodeElementList);
				change.addFix(newFix);
				line++;
			}
			catch (NullPointerException e)
			{
				ended = true;
			}
		}
		return change;
	}

	private static Fix parseFix(int line, NodeList firstNodeElementList)
	{
		Node element1 = firstNodeElementList.item(line);
		NodeList firstNodeList = element1.getChildNodes();
		String type = ((Node) firstNodeList).getAttributes().item(1).getNodeValue();
		String desc = ((Node) firstNodeList).getAttributes().item(0).getNodeValue();
		FixType realType = FixType.valueOf(type);
		return new Fix(realType, desc);
	}

	public static ChangeLogManager getInstance()
	{
		return ChangeLogManagerHolder.instance;
	}

	private static class ChangeLogManagerHolder
	{
		protected static final ChangeLogManager instance = new ChangeLogManager();
	}
}
