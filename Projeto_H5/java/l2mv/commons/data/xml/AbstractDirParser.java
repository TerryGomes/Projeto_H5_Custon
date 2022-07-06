package l2mv.commons.data.xml;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

public abstract class AbstractDirParser<H extends AbstractHolder> extends AbstractParser<H>
{
	protected AbstractDirParser(H holder)
	{
		super(holder);
	}

	public abstract File getXMLDir();

	public abstract boolean isIgnored(File f);

	public abstract String getDTDFileName();

	@Override
	protected final void parse()
	{
		File dir = getXMLDir();

		if (!dir.exists())
		{
			warn("Dir " + dir.getAbsolutePath() + " not exists");
			return;
		}

		File dtd = new File(dir, getDTDFileName());
		if (!dtd.exists())
		{
			info("DTD file: " + dtd.getName() + " not exists.");
			return;
		}

		initDTD(dtd);

		try
		{
			Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.directoryFileFilter());

			for (File f : files)
			{
				if (!f.isHidden())
				{
					if (!isIgnored(f))
					{
						try
						{
							parseCrypted(f);
						}
						catch (Exception e)
						{
							info("Exception: " + e + " in file: " + f.getName(), e);
						}
					}
				}
			}
		}
		catch (RuntimeException e)
		{
			error("Exception in AbstractDirParser ", e);
		}
	}
}
