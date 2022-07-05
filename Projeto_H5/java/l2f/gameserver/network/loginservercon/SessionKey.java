package l2f.gameserver.network.loginservercon;

public class SessionKey
{
	public final int playOkID1;
	public final int playOkID2;
	public final int loginOkID1;
	public final int loginOkID2;

	private final int hashCode;

	public SessionKey(int loginOK1, int loginOK2, int playOK1, int playOK2)
	{
		playOkID1 = playOK1;
		playOkID2 = playOK2;
		loginOkID1 = loginOK1;
		loginOkID2 = loginOK2;

		int hashCode = playOK1;
		hashCode *= 17;
		hashCode += playOK2;
		hashCode *= 37;
		hashCode += loginOK1;
		hashCode *= 51;
		hashCode += loginOK2;

		this.hashCode = hashCode;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null)
		{
			return false;
		}
		if (o.getClass() == this.getClass())
		{
			final SessionKey skey = (SessionKey) o;
			return playOkID1 == skey.playOkID1 && playOkID2 == skey.playOkID2 && loginOkID1 == skey.loginOkID1 && loginOkID2 == skey.loginOkID2;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append("[playOkID1: ").append(playOkID1).append(" playOkID2: ").append(playOkID2).append(" loginOkID1: ").append(loginOkID1).append(" loginOkID2: ").append(loginOkID2)
					.append("]").toString();
	}
}
