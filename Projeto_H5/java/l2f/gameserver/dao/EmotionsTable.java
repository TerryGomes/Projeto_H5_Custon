package l2f.gameserver.dao;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javolution.util.FastMap;
import l2f.gameserver.Config;

public class EmotionsTable
{
	private static final Logger _log = Logger.getLogger(EmotionsTable.class.getName());
	private static Map<String, Integer> _emotions = new FastMap<>();

	public static void init()
	{
		try
		{
			_emotions.clear();

			File file = Config.findResource("/data/emotions.xml");
			DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
			factory1.setValidating(false);
			factory1.setIgnoringComments(true);
			Document doc1 = factory1.newDocumentBuilder().parse(file);
			for (Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n1.getNodeName()))
				{
					for (Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
					{
						if ("emo".equalsIgnoreCase(d1.getNodeName()))
						{
							String text = d1.getAttributes().getNamedItem("text").getNodeValue();
							int emotion = Integer.parseInt(d1.getAttributes().getNamedItem("emotionId").getNodeValue());
							_emotions.put(text, emotion);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.warning("Emoticons: Could not pharse the xml...");
			e.printStackTrace();
		}
	}

	public static Map<String, Integer> getEmoticons()
	{
		return _emotions;
	}

	public static int containsEmotion(String text)
	{
		for (Entry<String, Integer> emot : _emotions.entrySet())
		{
			if (text.contains(emot.getKey()))
			{
				return emot.getValue();
			}
		}
		return -1;
	}
}
