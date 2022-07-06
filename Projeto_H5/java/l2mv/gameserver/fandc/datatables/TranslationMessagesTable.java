///*
// * This program is free software: you can redistribute it and/or modify it under
// * the terms of the GNU General Public License as published by the Free Software
// * Foundation, either version 3 of the License, or (at your option) any later
// * version.
// *
// * This program is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// * details.
// *
// * You should have received a copy of the GNU General Public License along with
// * this program. If not, see <http://www.gnu.org/licenses/>.
// */
//package l2mv.gameserver.fandc.datatables;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//
//import l2mv.gameserver.Config;
//import l2mv.gameserver.model.Player;
//import l2mv.gameserver.util.filter.XMLFilter;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//
//public class TranslationMessagesTable
//{
//	private static final Logger _log = LoggerFactory.getLogger(TranslationMessagesTable.class);
//
//	private final Map<String, TranslationMessage> _translationTable = new HashMap<>();
//
//	protected TranslationMessagesTable()
//	{
//		load();
//	}
//
//	public void reload()
//	{
//		_translationTable.clear();
//		load();
//	}
//
//	public void load()
//	{
//		File spawnsDir = new File(Config.DATAPACK_ROOT, "data/translation/");
//		for (File xml: spawnsDir.listFiles(new XMLFilter()))
//		{
//			parseTranslationFile(xml);
//		}
//
//		_log.info(getClass().getSimpleName() + ": Loaded " + _translationTable.size() + " translation messages");
//	}
//
//	public void parseTranslationFile(File file)
//	{
//		Document doc = null;
//
//		try
//		{
//			TranslationMessage message;
//
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			factory.setValidating(false);
//			factory.setIgnoringComments(true);
//			doc = factory.newDocumentBuilder().parse(file);
//
//			for (Node d = doc.getFirstChild(); d != null; d = d.getNextSibling())
//			{
//	            if ("list".equalsIgnoreCase(d.getNodeName()))
//	            {
//	                for (Node h = d.getFirstChild(); h != null; h = h.getNextSibling())
//	                {
//	                	if ("message".equalsIgnoreCase(h.getNodeName()))
//	    				{
//	                		final String id = h.getAttributes().getNamedItem("id").getNodeValue();
//		                	message = new TranslationMessage(id);
//
//	                		Node first = h.getFirstChild();
//	    					for (Node n = first; n != null; n = n.getNextSibling())
//	    					{
//	    						if ("set".equalsIgnoreCase(n.getNodeName()))
//	    						{
//	    							message.addNewMessage(n.getAttributes().getNamedItem("lang").getNodeValue(), n.getAttributes().getNamedItem("val").getNodeValue());
//	    						}
//	    					}
//
//	    					 _translationTable.put(id, message);
//	    				}
//	                }
//	            }
//			}
//		}
//		catch (IOException e)
//		{
//			_log.error(getClass().getSimpleName() + ": can not find " + file.getAbsolutePath() + " ! " + e.getMessage(), e);
//		}
//		catch (Exception e)
//		{
//			_log.error(getClass().getSimpleName() + ": error while loading " + file.getAbsolutePath() + " ! " + e.getMessage(), e);
//		}
//	}
//
//	public String getMessage(Player player, String id)
//	{
//		if (player == null || player.getLang() == null || !_translationTable.containsKey(id))
//			return id;
//
//		if (_translationTable.get(id).getMessageByLang(player.getLang()) != null)
//			return _translationTable.get(id).getMessageByLang(player.getLang());
//
//		return _translationTable.get(id).getMessageByLang("en");
//	}
//
//	public class TranslationMessage
//	{
//		private final String _id;
//		private final Map<String, String> _messages = new HashMap<>();
//
//		public TranslationMessage(String id)
//		{
//			_id = id;
//		}
//
//		public void addNewMessage(String lang, String message)
//		{
//			_messages.put(lang, message);
//		}
//
//		public String getTranslationId()
//		{
//			return _id;
//		}
//
//		public String getMessageByLang(String lang)
//		{
//			return _messages.get(lang);
//		}
//	}
//
//	public static TranslationMessagesTable getInstance()
//	{
//		return SingletonHolder._instance;
//	}
//
//	private static class SingletonHolder
//	{
//		protected static final TranslationMessagesTable _instance = new TranslationMessagesTable();
//	}
//}
