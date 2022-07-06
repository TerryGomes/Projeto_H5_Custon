package l2mv.commons.data.xml.helpers;

import java.io.File;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SimpleDTDEntityResolver implements EntityResolver
{
	private String _fileName;

	public SimpleDTDEntityResolver(File f)
	{
		_fileName = f.getAbsolutePath();
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
	{
		return new InputSource(_fileName);
	}
}
