package l2f.gameserver.scripts.jarloader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class JarResources
{
	// external debug flag
	public boolean debugOn = false;

	// jar resource mapping tables
	private Hashtable<String, Integer> htSizes = new Hashtable<String, Integer>();
	private Hashtable<String, byte[]> htJarContents = new Hashtable<String, byte[]>();

	// a jar file
	private String jarFileName;

	/**
	 * creates a JarResources. It extracts all resources from a Jar
	 * into an internal hashtable, keyed by resource names.
	 *
	 * @param jarFileName a jar or zip file
	 */
	public JarResources(String jarFileName)
	{
		this.jarFileName = jarFileName;
		init();
	}

	/**
	 * Extracts a jar resource as a blob.
	 *
	 * @param name a resource name.
	 */
	public byte[] getResource(String name)
	{
		return htJarContents.get(name);
	}

	public Set<String> getResources()
	{
		return htJarContents.keySet();
	}

	/**
	 * initializes internal hash tables with Jar file resources.
	 */
	private void init()
	{
		try
		{
			// extracts just sizes only.
			ZipFile zf = new ZipFile(jarFileName);
			Enumeration<?> e = zf.entries();
			while (e.hasMoreElements())
			{
				ZipEntry ze = (ZipEntry) e.nextElement();
				htSizes.put(ze.getName(), (int) ze.getSize());
			}
			zf.close();

			// extract resources and put them into the hashtable.
			FileInputStream fis = new FileInputStream(jarFileName);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ZipInputStream zis = new ZipInputStream(bis);
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null)
			{
				if (ze.isDirectory())
				{
					continue;
				}
				int size = (int) ze.getSize();
				// -1 means unknown size.
				if (size == -1)
				{
					size = htSizes.get(ze.getName());
				}
				byte[] b = new byte[size];
				int rb = 0;
				int chunk = 0;
				while (size > rb)
				{
					chunk = zis.read(b, rb, size - rb);
					if (chunk == -1)
					{
						break;
					}
					rb += chunk;
				}
				// add to internal resource hashtable
				htJarContents.put(ze.getName(), b);
			}
		}
		catch (NullPointerException e)
		{
			System.out.println("done.");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Dumps a zip entry into a string.
	 *
	 * @param ze a ZipEntry
	 */
	private String dumpZipEntry(ZipEntry ze)
	{
		StringBuffer sb = new StringBuffer();
		if (ze.isDirectory())
		{
			sb.append("d ");
		}
		else
		{
			sb.append("f ");
		}
		if (ze.getMethod() == ZipEntry.STORED)
		{
			sb.append("stored   ");
		}
		else
		{
			sb.append("defalted ");
		}
		sb.append(ze.getName());
		sb.append("\t");
		sb.append("" + ze.getSize());
		if (ze.getMethod() == ZipEntry.DEFLATED)
		{
			sb.append("/" + ze.getCompressedSize());
		}
		return sb.toString();
	}

	/**
	 * Is a test driver. Given a jar file and a resource name, it trys to
	 * extract the resource and then tells us whether it could or not.
	 * <p/>
	 * <strong>Example</strong>
	 * Let's say you have a JAR file which jarred up a bunch of gif image
	 * files. Now, by using JarResources, you could extract, create, and display
	 * those images on-the-fly.
	 * <pre>
	 *     ...
	 *     JarResources JR=new JarResources("GifBundle.jar");
	 *     Image image=Toolkit.createImage(JR.getResource("logo.gif");
	 *     Image logo=Toolkit.getDefaultToolkit().createImage(
	 *                   JR.getResources("logo.gif")
	 *                   );
	 *     ...
	 * </pre>
	 */
}