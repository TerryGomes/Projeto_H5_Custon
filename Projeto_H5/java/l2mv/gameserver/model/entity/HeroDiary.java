package l2mv.gameserver.model.entity;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Map;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.utils.HtmlUtils;

public class HeroDiary
{
	private static final SimpleDateFormat SIMPLE_FORMAT = new SimpleDateFormat("HH:** dd.MM.yyyy");

	public static final int ACTION_RAID_KILLED = 1;
	public static final int ACTION_HERO_GAINED = 2;
	public static final int ACTION_CASTLE_TAKEN = 3;

	private int _id;
	private long _time;
	private int _param;

	public HeroDiary(int id, long time, int param)
	{
		_id = id;
		_time = time;
		_param = param;
	}

	public Map.Entry<String, String> toString(Player player)
	{
		CustomMessage message = null;
		switch (_id)
		{
		case ACTION_RAID_KILLED:
			message = new CustomMessage("l2mv.gameserver.model.entity.Hero.RaidBossKilled", player).addString(HtmlUtils.htmlNpcName(_param));
			break;
		case ACTION_HERO_GAINED:
			message = new CustomMessage("l2mv.gameserver.model.entity.Hero.HeroGained", player);
			break;
		case ACTION_CASTLE_TAKEN:
			message = new CustomMessage("l2mv.gameserver.model.entity.Hero.CastleTaken", player).addString(HtmlUtils.htmlResidenceName(_param));
			break;
		default:
			return null;
		}

		return new AbstractMap.SimpleEntry<String, String>(SIMPLE_FORMAT.format(_time), message.toString());
	}
}
