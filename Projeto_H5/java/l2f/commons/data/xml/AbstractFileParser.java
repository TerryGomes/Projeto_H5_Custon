package l2f.commons.data.xml;

import java.io.File;

public abstract class AbstractFileParser<H extends AbstractHolder> extends AbstractParser<H>
{
	protected AbstractFileParser(H holder)
	{
		super(holder);
	}

	public abstract File getXMLFile();

	public abstract String getDTDFileName();

	@Override
	protected final void parse()
	{
		File file = getXMLFile();

		if (!file.exists())
		{
			warn("file " + file.getAbsolutePath() + " not exists");
			return;
		}

		File dtd = new File(file.getParent(), getDTDFileName());
		if (!dtd.exists())
		{
			info("DTD file: " + dtd.getName() + " not exists.");
			return;
		}

		initDTD(dtd);

		try
		{
			parseCrypted(file);
			// parseDocument(new FileInputStream(file), file.getName());
		}
		catch (Exception e)
		{
			error("Exception in AbstractFileParser ", e);
		}
	}
}
