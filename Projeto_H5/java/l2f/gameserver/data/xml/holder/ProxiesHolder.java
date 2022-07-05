package l2f.gameserver.data.xml.holder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.utils.ProxyRequirement;

public final class ProxiesHolder extends AbstractHolder
{
	private static final Logger LOG = LoggerFactory.getLogger(ProxiesHolder.class);

	private final LinkedHashMap<ProxyRequirement, InetAddress> _proxies;

	private ProxiesHolder()
	{
		_proxies = new LinkedHashMap<ProxyRequirement, InetAddress>();
	}

	public void addNewProxy(ProxyRequirement requirement, String ipName)
	{
		try
		{
			final InetAddress ip = InetAddress.getByName(ipName);
			_proxies.put(requirement, ip);
		}
		catch (UnknownHostException e)
		{
			LOG.error("Error while adding New Proxy! Requirement: " + requirement + " Proxy IP Name: " + ipName, e);
		}
	}

	public LinkedHashMap<ProxyRequirement, InetAddress> getAllProxies()
	{
		return _proxies;
	}

	public LinkedHashMap<ProxyRequirement, InetAddress> getProxiesCopy()
	{
		return new LinkedHashMap<ProxyRequirement, InetAddress>(_proxies);
	}

	@Override
	public int size()
	{
		return _proxies.size();
	}

	@Override
	public void clear()
	{
		_proxies.clear();
	}

	public static ProxiesHolder getInstance()
	{
		return SingletonHolder.instance;
	}

	@Override
	public String toString()
	{
		return "ProxiesHolder{proxies=" + _proxies + '}';
	}

	private static class SingletonHolder
	{
		private static final ProxiesHolder instance = new ProxiesHolder();
	}
}
