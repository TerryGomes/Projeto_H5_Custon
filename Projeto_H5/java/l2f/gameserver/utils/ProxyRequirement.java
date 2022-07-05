package l2f.gameserver.utils;

import java.net.InetAddress;

public interface ProxyRequirement
{
	boolean matches(String p0, InetAddress p1);
}
