package l2f.gameserver.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetIP
{
	private static final Logger LOG = LoggerFactory.getLogger(GetIP.class);
	private static String IpAddress;

	private static void setIpAdd()
	{
		try
		{
			java.net.URL URL = new java.net.URL("http://www.whatismyip.org/");
			java.net.HttpURLConnection Conn = (HttpURLConnection) URL.openConnection();
			java.io.InputStream InStream = Conn.getInputStream();
			java.io.InputStreamReader Isr = new java.io.InputStreamReader(InStream);
			java.io.BufferedReader Br = new java.io.BufferedReader(Isr);
			// System.out.print("Your IP address is " + Br.readLine());
			// JOptionPane.showMessageDialog(null, "IP is: " + Br.readLine() );
			IpAddress = Br.readLine();
		}
		catch (MalformedURLException e)
		{
			LOG.info("MalformedURLException while getting IP", e);
		}
		catch (IOException e)
		{
			LOG.info("IP service not active", e);
		}
	}

	public static String getIpAddress()
	{
		setIpAdd();
		return IpAddress;
	}
}
