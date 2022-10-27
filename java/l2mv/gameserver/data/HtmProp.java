package l2mv.gameserver.data;

public class HtmProp
{
	private final String keyWord;
	private final String text;

	public HtmProp(String keyWord, String text)
	{
		this.keyWord = keyWord;
		this.text = text;
	}

	public String getKeyWord()
	{
		return keyWord;
	}

	public String getText()
	{
		return text;
	}
}