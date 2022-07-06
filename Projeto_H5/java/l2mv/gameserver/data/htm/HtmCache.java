package l2mv.gameserver.data.htm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.crypt.CryptUtil;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Language;
import l2mv.gameserver.utils.Strings;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class HtmCache
{
	public static final int DISABLED = 0;
	public static final int LAZY = 1;
	public static final int ENABLED = 2;

	private static final Logger _log = LoggerFactory.getLogger(HtmCache.class);

	private final static HtmCache _instance = new HtmCache();

	public static HtmCache getInstance()
	{
		return _instance;
	}

	private Cache[] _cache = new Cache[Language.VALUES.length];

	private HtmCache()
	{
		for (int i = 0; i < _cache.length; i++)
		{
			_cache[i] = CacheManager.getInstance().getCache(getClass().getName() + "." + Language.VALUES[i].name());
		}
	}

	public void reload()
	{
		// clear();

		switch (Config.HTM_CACHE_MODE)
		{
		case ENABLED:
			for (Language lang : Language.VALUES)
			{
				File root = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName());
				if (!root.exists())
				{
					_log.info("HtmCache: Not find html dir for lang: " + lang);
					continue;
				}
				load(lang, root, root.getAbsolutePath() + "/");
			}
			for (int i = 0; i < _cache.length; i++)
			{
				Cache c = _cache[i];
				_log.info(String.format("HtmCache: parsing %d documents; lang: %s.", c.getSize(), Language.VALUES[i]));
			}
			break;
		case LAZY:
			_log.info("HtmCache: lazy cache mode.");
			break;
		case DISABLED:
			_log.info("HtmCache: disabled.");
			break;
		}
	}

	private void load(Language lang, File f, String rootPath)
	{
		if (!f.exists())
		{
			_log.info("HtmCache: dir not exists: " + f);
			return;
		}
		File[] files = f.listFiles();

		for (File file : files)
		{
			if (file.isDirectory())
			{
				load(lang, file, rootPath);
			}
			else if (file.getName().endsWith(".htm"))
			{
				try
				{
					putContent(lang, file, rootPath);
				}
				catch (IOException e)
				{
					_log.info("HtmCache: file error" + e, e);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private String getContent(File file, String encoding) throws IOException
	{
		InputStream stream = CryptUtil.decryptOnDemand(new ByteArrayInputStream(FileUtils.readFileToString(file, encoding).getBytes()));
		@SuppressWarnings("resource")
		FileInputStream _stream = new FileInputStream(file);
		StringBuilder builder = new StringBuilder();
		if ((byte) _stream.read() == 0x00)
		{
			byte[] buffer = new byte[1024];
			int num;
			while ((num = stream.read(buffer)) >= 0)
			{
				String tmp = new String(buffer, encoding);
				builder.append(tmp, 0, num);
			}
			return builder.toString();
		}

		return FileUtils.readFileToString(file, "UTF-8");
	}

	public void putContent(Language lang, File f, String rootPath) throws IOException
	{
		String content = FileUtils.readFileToString(f, "UTF-8");
		// String content = getContent(f, "UTF-8");

		String path = f.getAbsolutePath().substring(rootPath.length()).replace("\\", "/");

		_cache[lang.ordinal()].put(new Element(path.toLowerCase(), Strings.bbParse(content)));
	}

	public String getNotNull(String fileName, Player player)
	{
		Language lang = player == null ? Language.ENGLISH : player.getLanguage();

		if (player.isGM())
		{
			Functions.sendDebugMessage(player, "HTML: " + fileName);
		}

		return getNotNull(fileName, lang);
	}

	public String getNotNull(String fileName, Language lang)
	{
		String cache = getCache(fileName, lang);

		if (StringUtils.isEmpty(cache))
		{
			cache = "Dialog not found: " + fileName + "; Lang: " + lang;
		}

		return cache;
	}

	public String getNullable(String fileName, Player player)
	{
		Language lang = player == null ? Language.ENGLISH : player.getLanguage();
		String cache = getCache(fileName, lang);

		if (StringUtils.isEmpty(cache))
		{
			return null;
		}

		return cache;
	}

	private String getCache(String file, Language lang)
	{
		if (file == null)
		{
			return null;
		}

		final String fileLower = file.toLowerCase();
		String cache = get(lang, fileLower);

		if (cache == null)
		{
			switch (Config.HTM_CACHE_MODE)
			{
			case ENABLED:
				break;
			case LAZY:
				cache = loadLazy(lang, file);
				if (cache == null && lang != Language.ENGLISH)
				{
					cache = loadLazy(Language.ENGLISH, file);
				}
				break;
			case DISABLED:
				cache = loadDisabled(lang, file);
				if (cache == null && lang != Language.ENGLISH)
				{
					cache = loadDisabled(Language.ENGLISH, file);
				}
				break;
			}
		}

		return cache;
	}

	private String loadDisabled(Language lang, String file)
	{
		String cache = null;
		File f = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName() + "/" + file);
		if (f.exists())
		{
			try
			{
				cache = FileUtils.readFileToString(f, "UTF-8");
				cache = Strings.bbParse(cache);
			}
			catch (IOException e)
			{
				_log.info("HtmCache: File error: " + file + " lang: " + lang);
			}
		}
		return cache;
	}

	private String loadLazy(Language lang, String file)
	{
		String cache = null;
		File f = new File(Config.DATAPACK_ROOT, "data/html-" + lang.getShortName() + "/" + file);
		if (f.exists())
		{
			try
			{
				cache = FileUtils.readFileToString(f, "UTF-8");
				cache = Strings.bbParse(cache);

				_cache[lang.ordinal()].put(new Element(file, cache));
			}
			catch (IOException e)
			{
				_log.info("HtmCache: File error: " + file + " lang: " + lang);
			}
		}
		return cache;
	}

	private String get(Language lang, String f)
	{
		Element element = _cache[lang.ordinal()].get(f);

		if (element == null)
		{
			element = _cache[Language.ENGLISH.ordinal()].get(f);
		}

		return element == null ? null : (String) element.getObjectValue();
	}

	public void clear()
	{
		for (int i = 0; i < _cache.length; i++)
		{
			_cache[i].removeAll();
		}
	}
}
