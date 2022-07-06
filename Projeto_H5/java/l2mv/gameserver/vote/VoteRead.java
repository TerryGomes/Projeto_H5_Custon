package l2mv.gameserver.vote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;

public class VoteRead
{
	private static final Logger _log = LoggerFactory.getLogger(VoteRead.class);

	public static volatile long _siteBlockTime = 0;

	/**
	 * @param IP
	 * @return Returns true if the selected ip has voted
	 */
	public static long checkVotedIP(String IP)
	{
		if (IP == null)
		{
			return 0;
		}

		long voteDate = 0L;

		try
		{
			final URL url = new URL(Config.VOTE_ADDRESS + IP);
			try (InputStreamReader isr = new InputStreamReader(url.openStream()))
			{
				String strLine;
				BufferedReader br = new BufferedReader(isr);

				while ((strLine = br.readLine()) != null)
				{
					if (strLine.trim().equalsIgnoreCase("FALSE"))
					{
						continue;
					}

					voteDate = System.currentTimeMillis() / 1000L;
				}
			}
		}
		catch (MalformedURLException e)
		{
			_log.error("MalformedURLException while reading votes, IP:" + IP + " Address:" + Config.VOTE_ADDRESS, e);
			_siteBlockTime = System.currentTimeMillis() + 30 * 60 * 1000; // Block voting for 30 minutes if a web error appears
			return 0L;
		}
		catch (IOException e)
		{
			_log.error("IOException while reading votes, IP:" + IP + " Address:" + Config.VOTE_ADDRESS + " " + e.toString());
			_siteBlockTime = System.currentTimeMillis() + 15 * 60 * 1000; // Block voting for 15 minutes if a web error appears
			return 0L;
		}
		catch (Exception e)
		{
			return 0L;
		}

		return voteDate;
	}
}