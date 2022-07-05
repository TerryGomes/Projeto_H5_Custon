package l2f.gameserver.data;

public class StringNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = -7541844048669519803L;

	public StringNotFoundException()
	{
		super();
	}

	public StringNotFoundException(String s)
	{
		super(s);
	}
}
