package l2f.gameserver.instancemanager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import l2f.gameserver.data.htm.bypasshandler.BypassType;
import l2f.gameserver.handler.bbs.CommunityBoardManager;
import l2f.gameserver.handler.bbs.ICommunityBoardHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.utils.Log;
import l2f.gameserver.utils.Strings;

public class BypassManager
{
	private static final Pattern PATTERN_NO_H = Pattern.compile("\"(bypass +)(.+?)\"");
	private static final Pattern PATTERN_H = Pattern.compile("\"(bypass +-h +)(.+?)\"");
	private static final Map<Integer, String> lastHTMLs = new ConcurrentHashMap<Integer, String>();

	public static enum EncodingType
	{
		ENCODED, ENCODED_BBS, SIMPLE, SIMPLE_BBS, SIMPLE_DIRECT
	}

	public static EncodingType getBypassType(String bypass)
	{
		final String firstChar = bypass.substring(0, 1);
		if (StringUtils.isNumeric(firstChar))
		{
			final BypassType bypassType = BypassType.values()[Integer.parseInt(firstChar)];
			if (bypassType != null && bypassType.getEncodingType() != null)
			{
				return bypassType.getEncodingType();
			}
		}
		if (Strings.matches(bypass, "^(_mrsl|_diary|_match|manor_menu_select|_match|_olympiad).*", Pattern.DOTALL))
		{
			return EncodingType.SIMPLE;
		}

		return EncodingType.SIMPLE_DIRECT;
	}

	public static String encode(Player player, String html, Collection<String> bypassStorage, BypassType type)
	{
		final Matcher m = BypassManager.PATTERN_NO_H.matcher(html);
		final StringBuffer sb = new StringBuffer();
		while (m.find())
		{
			String code;
			final String bypass = code = m.group(2);
			String params = "";
			final int i = bypass.indexOf(" $");
			final boolean useParams = i >= 0;
			if (useParams)
			{
				code = bypass.substring(0, i);
				params = bypass.substring(i).replace("$", "\\$");
			}
			final boolean h = code.startsWith("-h");
			m.appendReplacement(sb, "\"bypass" + (h ? " -h" : "") + " " + type.ordinal() + Integer.toHexString(bypassStorage.size()) + params + "\"");
			if (h)
			{
				code = code.substring("-h ".length(), code.length());
			}
			bypassStorage.add(code);
			if (code.contains("dropCalculator") && code.contains("NAME"))
			{
				BypassManager.lastHTMLs.put(player.getObjectId(), html);
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static DecodedBypass decode(String bypass, List<String> bypassStorage, BypassType type, Player player)
	{
		synchronized (bypassStorage)
		{
			final String[] bypass_parsed = bypass.split(" ");
			int idx;
			try
			{
				idx = Integer.parseInt(bypass_parsed[0].substring(1), 16);
			}
			catch (NumberFormatException e)
			{
				Log.logIllegalActivity("Player " + player + " sent illegal Bypass: " + bypass);
				return null;
			}
			String bp;
			if (idx < 0 || bypassStorage.size() <= idx)
			{
				bp = null;
			}
			else
			{
				bp = bypassStorage.get(idx);
			}
			if (bp == null)
			{
				Log.add("Can't decode bypass (bypass not exists): [" + type.toString() + "]" + bypass + " / Player: " + player.getName() + " / Npc: "
							+ (player.getLastNpc() == null ? "null" : player.getLastNpc().getName()), "debug_bypass");
				return null;
			}
			DecodedBypass result;
			if (type == BypassType.COMMUNITY || bp.startsWith("_bbs"))
			{ // Support for using community bypasses from a npc window
				result = new DecodedBypass(bp, type, CommunityBoardManager.getInstance().getCommunityHandler(bp));
			}
			else
			{ // Support for using community bypasses from a npc window
				result = new DecodedBypass(bp, type);
			}
			for (int i = 1; i < bypass_parsed.length; ++i)
			{
				final StringBuilder sb = new StringBuilder();
				final DecodedBypass decodedBypass = result;
				decodedBypass.bypass = sb.append(decodedBypass.bypass).append(" ").append(bypass_parsed[i]).toString();
			}
			result.trim();
			if (result.bypass.startsWith("dropCalculator") && result.bypass.contains("NAME") && !result.bypass.endsWith("NAME"))
			{
				Log.logIllegalActivity(bp + " " + type + " " + bypassStorage + " " + Arrays.toString(bypass_parsed) + " " + idx + BypassManager.lastHTMLs.get(player.getObjectId()));
			}
			return result;
		}
	}

	public static class DecodedBypass
	{
		public String bypass;
		public BypassType type;
		public ICommunityBoardHandler handler;

		public DecodedBypass(String bypass, BypassType type)
		{
			this.bypass = bypass;
			this.type = type;
		}

		public DecodedBypass(String bypass, BypassType type, ICommunityBoardHandler handler)
		{
			this.bypass = bypass;
			this.type = type;
			this.handler = handler;
		}

		public DecodedBypass trim()
		{
			bypass = bypass.trim();
			return this;
		}
	}
}