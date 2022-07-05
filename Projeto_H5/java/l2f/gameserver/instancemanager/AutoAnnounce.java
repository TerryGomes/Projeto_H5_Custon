package l2f.gameserver.instancemanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.model.AutoAnnounces;

public class AutoAnnounce implements Runnable
{
	private static final Logger LOG = LoggerFactory.getLogger(AutoAnnounce.class);
	private static AutoAnnounce _instance;

	static HashMap<Integer, AutoAnnounces> _lists;

	public static AutoAnnounce getInstance()
	{
		if (_instance == null)
		{
			_instance = new AutoAnnounce();
		}
		return _instance;
	}

	public static void reload()
	{
		_instance = new AutoAnnounce();
	}

	public AutoAnnounce()
	{
		_lists = new HashMap<Integer, AutoAnnounces>();
		LOG.info("AutoAnnounce: Initializing");
		load();
		LOG.info("AutoAnnounce: Loaded " + _lists.size() + " announce.");
	}

	private void load()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);

			File file = new File(Config.DATAPACK_ROOT, "/config/autoannounce.xml");
			if (!file.exists())
			{
				LOG.warn("AutoAnnounce: NO FILE (./config/autoannounce.xml)");
				return;
			}

			Document doc = factory.newDocumentBuilder().parse(file);
			int counterAnnounce = 0;
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("announce".equalsIgnoreCase(d.getNodeName()))
						{
							ArrayList<String> msg = new ArrayList<String>();
							NamedNodeMap attrs = d.getAttributes();
							int delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
							int repeat = Integer.parseInt(attrs.getNamedItem("repeat").getNodeValue());
							AutoAnnounces aa = new AutoAnnounces(counterAnnounce);
							for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
							{
								if ("message".equalsIgnoreCase(cd.getNodeName()))
								{
									msg.add(String.valueOf(cd.getAttributes().getNamedItem("text").getNodeValue()));
								}
							}
							aa.setAnnounce(delay, repeat, msg);
							_lists.put(counterAnnounce, aa);
							counterAnnounce++;
						}
					}
				}
			}
			LOG.info("AutoAnnounce: Load OK");
		}
		catch (DOMException | NumberFormatException | ParserConfigurationException | SAXException e)
		{
			LOG.warn("AutoAnnounce: Error parsing autoannounce.xml file. ", e);
		}
		catch (IOException e)
		{
			LOG.warn("AutoAnnounce: IOException parsing autoannounce.xml file. ", e);
		}
	}

	@Override
	public void run()
	{
		if (_lists.size() <= 0)
		{
			return;
		}
		for (int i = 0; i < _lists.size(); i++)
		{
			if (_lists.get(i).canAnnounce())
			{
				ArrayList<String> msg = _lists.get(i).getMessage();
				for (int c = 0; c < msg.size(); c++)
				{
					Announcements.getInstance().announceToAll(msg.get(c));
				}
				_lists.get(i).updateRepeat();
			}
		}
	}
}