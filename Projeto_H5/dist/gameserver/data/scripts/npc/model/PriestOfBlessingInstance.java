package npc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.Util;

public class PriestOfBlessingInstance extends NpcInstance
{
	private static class Hourglass
	{
		public int minLevel;
		public int maxLevel;
		public int itemPrice;
		public int[] itemId;

		public Hourglass(int min, int max, int price, int[] id)
		{
			minLevel = min;
			maxLevel = max;
			itemPrice = price;
			itemId = id;
		}
	}

	private static List<Hourglass> hourglassList = new ArrayList<Hourglass>();
	static
	{
		hourglassList.add(new Hourglass(1, 19, 4000, new int[]
		{
			17095,
			17096,
			17097,
			17098,
			17099
		})); // 1-19
		hourglassList.add(new Hourglass(20, 39, 30000, new int[]
		{
			17100,
			17101,
			17102,
			17103,
			17104
		})); // 20-39
		hourglassList.add(new Hourglass(40, 51, 110000, new int[]
		{
			17105,
			17106,
			17107,
			17108,
			17109
		})); // 40-51
		hourglassList.add(new Hourglass(52, 60, 310000, new int[]
		{
			17110,
			17111,
			17112,
			17113,
			17114
		})); // 52-60
		hourglassList.add(new Hourglass(61, 75, 970000, new int[]
		{
			17115,
			17116,
			17117,
			17118,
			17119
		})); // 61-75
		hourglassList.add(new Hourglass(76, 79, 2160000, new int[]
		{
			17120,
			17121,
			17122,
			17123,
			17124
		})); // 76-79
		hourglassList.add(new Hourglass(80, 85, 5000000, new int[]
		{
			17125,
			17126,
			17127,
			17128,
			17129
		})); // 80-85
	}

	public PriestOfBlessingInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}

		if (command.startsWith("BuyHourglass"))
		{
			int val = Integer.parseInt(command.substring(13));
			Hourglass hg = getHourglass(player);
			int itemId = getHourglassId(hg);
			buyLimitedItem(player, "hourglass" + hg.minLevel + hg.maxLevel, itemId, val, false);
		}
		else if (command.startsWith("BuyVoice"))
		{
			buyLimitedItem(player, "nevitsVoice" + player.getAccountName(), 17094, 100000, true);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		if (val == 0)
		{
			Hourglass hg = getHourglass(player);
			NpcHtmlMessage html = new NpcHtmlMessage(player, this);
			html.setFile(getHtmlPath(getNpcId(), val, player));
			html.replace("%price%", String.valueOf(hg.itemPrice));
			html.replace("%priceBreak%", Util.formatAdena(hg.itemPrice));
			html.replace("%minLvl%", String.valueOf(hg.minLevel));
			html.replace("%maxLvl%", String.valueOf(hg.maxLevel));
			player.sendPacket(html);
			return;
		}
		super.showChatWindow(player, val);
	}

	private static Hourglass getHourglass(Player player)
	{
		for (Hourglass hg : hourglassList)
		{
			if (player.getLevel() >= hg.minLevel && player.getLevel() <= hg.maxLevel)
			{
				return hg;
			}
		}

		return null;
	}

	private static int getHourglassId(Hourglass hg)
	{
		int id = hg.itemId[Rnd.get(hg.itemId.length)];
		return id;
	}

	private void buyLimitedItem(Player player, String var, int itemId, int price, boolean isGlobalVar)
	{
		long _remaining_time;
		long _reuse_time = 20 * 60 * 60 * 1000;
		long _curr_time = System.currentTimeMillis();
		String _last_use_time = player.getVar(var);
		// TODO: Тупая заглушка, для ограничения покупки итема на одном аккаунте
		if (isGlobalVar)
		{
			Map<Integer, String> chars = player.getAccountChars();
			if (chars != null)
			{
				long use_time = 0;
				for (int objId : chars.keySet())
				{
					String val = Player.getVarFromPlayer(objId, var);
					if (val != null)
					{
						if (Long.parseLong(val) > use_time)
						{
							use_time = Long.parseLong(val);
						}
					}
				}
				if (use_time > 0)
				{
					_last_use_time = String.valueOf(use_time);
				}
			}
		}

		if (_last_use_time != null)
		{
			_remaining_time = _curr_time - Long.parseLong(_last_use_time);
		}
		else
		{
			_remaining_time = _reuse_time;
		}

		if (_remaining_time >= _reuse_time)
		{
			if (player.reduceAdena(price, true, "PriestOfBlessingInstance"))
			{
				Functions.addItem(player, itemId, 1, "PriestOfBlessingInstance");
				player.setVar(var, String.valueOf(_curr_time), -1);
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addItemName(57).addNumber(price));
			}
		}
		else
		{
			int hours = (int) (_reuse_time - _remaining_time) / 3600000;
			int minutes = (int) (_reuse_time - _remaining_time) % 3600000 / 60000;
			if (hours > 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
			}
			else if (minutes > 0)
			{
				player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
			}
			else if (player.reduceAdena(price, true, "PriestOfBlessingInstance"))
			{
				Functions.addItem(player, itemId, 1, "PriestOfBlessingInstance");
				player.setVar(var, String.valueOf(_curr_time), -1);
			}
			else
			{
				player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addItemName(57).addNumber(price));
			}
		}
	}
}