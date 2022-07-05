package l2f.gameserver.utils.proxyrequirement;

import java.net.InetAddress;
import java.net.UnknownHostException;

import l2f.gameserver.utils.ProxyRequirement;

public class IPSubnet implements ProxyRequirement
{
	final byte[] _ipAddress;
	final byte[] _mask;
	final boolean _isIPv4;

	public IPSubnet(String input) throws UnknownHostException, NumberFormatException, ArrayIndexOutOfBoundsException
	{
		final int idx = input.indexOf("/");
		if (idx > 0)
		{
			_ipAddress = InetAddress.getByName(input.substring(0, idx)).getAddress();
			_mask = getMask(Integer.parseInt(input.substring(idx + 1)), _ipAddress.length);
			_isIPv4 = _ipAddress.length == 4;
			if (!applyMask(_ipAddress))
			{
				throw new UnknownHostException(input);
			}
		}
		else
		{
			_ipAddress = InetAddress.getByName(input).getAddress();
			_mask = getMask(_ipAddress.length * 8, _ipAddress.length);
			_isIPv4 = _ipAddress.length == 4;
		}
	}

	public IPSubnet(InetAddress addr, int mask) throws UnknownHostException
	{
		super();
		_ipAddress = addr.getAddress();
		_isIPv4 = _ipAddress.length == 4;
		_mask = getMask(mask, _ipAddress.length);
		if (!applyMask(_ipAddress))
		{
			throw new UnknownHostException(addr.toString() + "/" + mask);
		}
	}

	public byte[] getAddress()
	{
		return _ipAddress;
	}

	public boolean applyMask(byte[] addr)
	{
		if (_isIPv4 == (addr.length == 4))
		{
			for (int i = 0; i < _ipAddress.length; ++i)
			{
				if ((addr[i] & _mask[i]) != _ipAddress[i])
				{
					return false;
				}
			}
		}
		else if (_isIPv4)
		{
			for (int i = 0; i < _ipAddress.length; ++i)
			{
				if ((addr[i + 12] & _mask[i]) != _ipAddress[i])
				{
					return false;
				}
			}
		}
		else
		{
			for (int i = 0; i < _ipAddress.length; ++i)
			{
				if ((addr[i] & _mask[i + 12]) != _ipAddress[i + 12])
				{
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		int size = 0;
		for (byte element : _mask)
		{
			size += Integer.bitCount(element & 0xFF);
		}
		try
		{
			return InetAddress.getByAddress(_ipAddress).toString() + "/" + size;
		}
		catch (UnknownHostException e)
		{
			return "Invalid";
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o instanceof IPSubnet)
		{
			return applyMask(((IPSubnet) o).getAddress());
		}
		return o instanceof InetAddress && applyMask(((InetAddress) o).getAddress());
	}

	private static final byte[] getMask(int n, int maxLength) throws UnknownHostException
	{
		if (n > maxLength << 3 || n < 0)
		{
			throw new UnknownHostException("Invalid netmask: " + n);
		}
		final byte[] result = new byte[maxLength];
		for (int i = 0; i < maxLength; ++i)
		{
			result[i] = -1;
		}
		for (int i = (maxLength << 3) - 1; i >= n; --i)
		{
			result[i >> 3] <<= (byte) 1;
		}
		return result;
	}

	@Override
	public boolean matches(String accountName, InetAddress ip)
	{
		return applyMask(ip.getAddress());
	}
}
