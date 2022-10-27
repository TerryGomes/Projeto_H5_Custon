package l2mv.gameserver.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;

public class HtmPropHolder
{
	private static final Logger LOG = LoggerFactory.getLogger(HtmPropHolder.class);

	private static final String[] HTM_FILE_ENDINGS =
	{
		".htm",
		".html"
	};
	private static final String USUAL_PROP_NAME_ADDON = ".prop";
	private static final String STARTING_PATH = "data/html-en/";
	private static final char LINE_SEPARATOR = '\n';
	private static final String START_BOUNDARY = "{";
	private static final String FINISH_BOUNDARY = "}";
	private final Map<String, HtmPropList> lists;

	private HtmPropHolder()
	{
		lists = new ConcurrentHashMap<>();
	}

	public static HtmPropList getListFromHtmPathCommunity(String htmFilePath)
	{
		String communityPath = new StringBuilder().append(Config.BBS_HOME_DIR).append(htmFilePath).toString();
		return getListFromHtmPath(communityPath);
	}

	public static HtmPropList getListFromHtmPath(String htmFilePath)
	{
		for (String ending : HTM_FILE_ENDINGS)
		{
			if (!htmFilePath.endsWith(ending))
			{
				continue;
			}
			String propPath = new StringBuilder().append(htmFilePath.substring(0, htmFilePath.length() - ending.length())).append(".prop").append(ending).toString();
			return getList(propPath);
		}
		return null;
	}

	public static HtmPropList getListCommunity(String filePath)
	{
		String communityPath = new StringBuilder().append(Config.BBS_HOME_DIR).append(filePath).toString();
		return getList(communityPath);
	}

	public static HtmPropList getList(String filePath)
	{
		String additionalEnding = "";
		for (int endingIndex = -1; endingIndex < HTM_FILE_ENDINGS.length; endingIndex++)
		{
			if (endingIndex >= 0)
			{
				additionalEnding = HTM_FILE_ENDINGS[endingIndex];
			}
			HtmPropList list = getInstance().justGetList(new StringBuilder().append("data/html-en/").append(filePath).append(additionalEnding).toString());
			if (list == null)
			{
				list = getInstance().loadFile(new StringBuilder().append("data/html-en/").append(filePath).append(additionalEnding).toString());
			}
			if (list != null)
			{
				return list;
			}
		}
		return null;
	}

	private HtmPropList justGetList(String filePath)
	{
		return lists.get(filePath);
	}

	private HtmPropList loadFile(String filePath)
	{
		Path path = Paths.get(filePath, new String[0]);

		if (!new File(path.toString()).exists())
		{
			return null;
		}
		List<HtmProp> list = new ArrayList<>();
		try
		{
			String keyWord = "";
			boolean started = false;
			StringBuilder text = new StringBuilder();
			for (String line : Files.readAllLines(path))
			{
				line = line.replace("\t", "");

				String lineTrim = line.trim();
				if (keyWord.isEmpty())
				{
					if (!lineTrim.isEmpty())
					{
						keyWord = lineTrim;
					}
					if (lineTrim.contains("{"))
					{
						started = true;
					}

				}
				else if (started)
				{
					if (lineTrim.contains("}"))
					{
						HtmProp prop = new HtmProp(keyWord, text.toString());
						list.add(prop);

						keyWord = "";
						text.setLength(0);
						started = false;
					}
					else if (!lineTrim.isEmpty())
					{
						if (text.length() > 0)
						{
							text.append('\n');
						}
						text.append(lineTrim);
					}

				}
				else if (lineTrim.contains("{"))
				{
					started = true;
				}
			}
		}
		catch (IOException e)
		{
			LOG.error(new StringBuilder().append("Error while loading HtmPropFile. Path: ").append(filePath).append(" Error: ").toString(), e);
		}

		if (!list.isEmpty())
		{
			HtmPropList propList = new HtmPropList(list);
			lists.put(filePath, propList);
			return propList;
		}
		return null;
	}

	public static void clearProps()
	{
		getInstance().clearAll();
	}

	private void clearAll()
	{
		lists.clear();
	}

	private static HtmPropHolder getInstance()
	{
		return HtmPropHolderHolder.instance;
	}

	private static class HtmPropHolderHolder
	{
		private static final HtmPropHolder instance = new HtmPropHolder();
	}
}