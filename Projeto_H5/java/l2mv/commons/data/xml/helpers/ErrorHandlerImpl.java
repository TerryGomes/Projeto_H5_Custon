package l2mv.commons.data.xml.helpers;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import l2mv.commons.data.xml.AbstractParser;

public class ErrorHandlerImpl implements ErrorHandler
{
	private AbstractParser<?> _parser;

	public ErrorHandlerImpl(AbstractParser<?> parser)
	{
		_parser = parser;
	}

	@Override
	public void warning(SAXParseException exception) throws SAXException
	{
		_parser.warn("File: " + _parser.getCurrentFileName() + ":" + exception.getLineNumber() + " warning: " + exception.getMessage());
	}

	@Override
	public void error(SAXParseException exception) throws SAXException
	{
		_parser.error("File: " + _parser.getCurrentFileName() + ":" + exception.getLineNumber() + " error: " + exception.getMessage());
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException
	{
		_parser.error("File: " + _parser.getCurrentFileName() + ":" + exception.getLineNumber() + " fatal: " + exception.getMessage());
	}
}
