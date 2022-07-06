package l2mv.gameserver.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.model.Player;

public class Files
{
	private static final Logger LOG = LoggerFactory.getLogger(Files.class);

	/**
	 * Saves the string to a file in UTF-8. <br>
	 * If the file exists, overwrite it.
	 * @param Path path to the file
	 * @param String to store the string
	 */
	public static void writeFile(String path, String string)
	{
		try
		{
			FileUtils.writeStringToFile(new File(path), string, "UTF-8");
		}
		catch (IOException e)
		{
			LOG.error("Error while saving file : " + path, e);
		}
	}

	public static boolean copyFile(String srcFile, String destFile)
	{
		try
		{
			FileUtils.copyFile(new File(srcFile), new File(destFile), false);
			return true;
		}
		catch (IOException e)
		{
			LOG.error("Error while copying file : " + srcFile + " to " + destFile, e);
		}

		return false;
	}

	public static String read(String name)
	{
		if (name == null)
		{
			return null;
		}

		File file = new File("./" + name);
		// File file = new File(Config.DATAPACK_ROOT + "/" + name);

		if (!file.exists())
		{
			return null;
		}

		String content = null;

		try (BufferedReader br = new BufferedReader(new UnicodeReader(new FileInputStream(file), "UTF-8")))
		{
			StringBuffer sb = new StringBuffer();
			String s = "";
			while ((s = br.readLine()) != null)
			{
				sb.append(s).append('\n');
			}
			content = sb.toString();
		}
		catch (IOException e)
		{
			LOG.error("Error while reading \"Files\"!", e);
		}

		return content;
	}

	/**
	* Saves the string to a file in UTF-8. <br>
	* If the file exists, overwrite it.
	*
	* @param Path path to the file
	* @param String to store the string
	*/
	public static void writeFile1(String path, String string)
	{
		if (string == null || string.length() == 0)
		{
			return;
		}

		File target = new File(path);

		if (!target.exists())
		{
			try
			{
				target.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace(System.err);
			}
		}

		try
		{
			FileOutputStream fos = new FileOutputStream(target);
			fos.write(string.getBytes("UTF-8"));
			fos.close();
		}
		catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			LOG.error("Error while writing File1 in Files", e);
		}
		catch (IOException e)
		{
			LOG.error("IOException while writing File1 in Files", e);
		}
	}

	public static String read(String name, Player player)
	{
		if (player == null)
		{
			return "";
		}
		return read(name, player.getLang());
	}

	public static String read(String name, String lang)
	{
		String tmp = langFileName(name, lang);

		long last_modif = lastModified(tmp); // modification time of a localized file
		if (last_modif > 0) // if it exists
		{
			if (last_modif >= lastModified(name)) // and later the original file
			{
				return Strings.bbParse(read(tmp)); // then return the localized
			}

			LOG.warn("Last modify of " + name + " more then " + tmp); // if it exists but is outdated - swear to the log
		}

		return Strings.bbParse(read(name)); // if the file is not localized to return the original
	}

	public static String langFileName(String name, String lang)
	{
		if (lang == null || lang.equalsIgnoreCase("en"))
		{
			lang = "";
		}

		String tmp;

		tmp = name.replaceAll("(.+)(\\.htm)", "$1-" + lang + "$2");
		if (!tmp.equals(name) && lastModified(tmp) > 0)
		{
			return tmp;
		}

		tmp = name.replaceAll("(.+)(/[^/].+\\.htm)", "$1/" + lang + "$2");
		if (!tmp.equals(name) && lastModified(tmp) > 0)
		{
			return tmp;
		}

		tmp = name.replaceAll("(.+?/html)/", "$1-" + lang + "/");
		if (!tmp.equals(name) && lastModified(tmp) > 0)
		{
			return tmp;
		}

		if (lastModified(name) > 0)
		{
			return name;
		}

		return null;
	}

	public static long lastModified(String name)
	{
		if (name == null)
		{
			return 0;
		}

		return new File(name).lastModified();
	}
}