package services;

import l2mv.gameserver.Config;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Util;

public class HairChange extends Functions
{
	private static final int[] Male =
	{
		1,
		1,
		1,
		1,
		1,
		0,
		0
	};
	private static final int[] Female =
	{
		1,
		1,
		1,
		1,
		1,
		1,
		1
	};

	public void show()
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (Config.SERVICES_HAIR_CHANGE_ITEM_ID == -1)
		{
			player.sendMessage("This Service is turned off.");
			return;
		}

		NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("scripts/services/HairChange/index.htm");

		for (int i = 0; i < 7; i++)
		{
			String button = "<button action=\"bypass -h scripts_services.HairChange:ask " + i + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
			String prohibited = "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">";
			boolean result = (player.getHairStyle() != i) && (player.getSex() == 0 ? Male[i] != 0 : Female[i] != 0);

			html.replace("%hair_" + (i + 1) + "%", result ? button : prohibited);
			html.replace("%color_" + (i + 1) + "%", result ? "99CC00" : "CC3333");
		}

		html.replace("%now%", HairTypeName(player.getHairStyle()));

		player.sendPacket(html);
	}

	public void ask(String[] arg)
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		int id = Util.isNumber(arg[0]) ? Integer.parseInt(arg[0]) : 0;

		String msg = new CustomMessage("Want to change your hairstyle from Type {0} to Type {1}? Change cost: {2}").addString(HairTypeName(player.getHairStyle())).addString(HairTypeName(id)).addString(Util.formatPay(player, Config.SERVICES_HAIR_CHANGE_COUNT, Config.SERVICES_HAIR_CHANGE_ITEM_ID)).toString();
		ConfirmDlg ask = new ConfirmDlg(SystemMsg.S1, 60000);
		ask.addString(msg);

		player.ask(ask, new AnswerListener(player, id));
	}

	private static final boolean isCorrect(int id)
	{
		return (id >= 0) || (id <= 6);
	}

	private static final String HairTypeName(int id)
	{
		switch (id)
		{
		case 0:
			return "A";
		case 1:
			return "B";
		case 2:
			return "C";
		case 3:
			return "D";
		case 4:
			return "E";
		case 5:
			return "F";
		case 6:
			return "G";
		}
		return "?";
	}

	private static final void changeHair(Player player, int id)
	{
		if (Util.getPay(player, Config.SERVICES_HAIR_CHANGE_ITEM_ID, Config.SERVICES_HAIR_CHANGE_COUNT, true))
		{
			player.setHairStyle(id);
			player.sendMessage("Hairstyle successfully changed.");
			player.broadcastPacket(new MagicSkillUse(player, player, 6696, 1, 1000, 0L));
			player.broadcastCharInfo();
		}
	}

	private class AnswerListener implements OnAnswerListener
	{
		private final Player _player;
		private final int _id;

		protected AnswerListener(Player player, int id)
		{
			_player = player;
			_id = id;
		}

		@Override
		public void sayYes()
		{
			if (_player == null || !_player.isOnline() || !isCorrect(_id))
			{
				return;
			}
			changeHair(_player, _id);
			show();
		}

		@Override
		public void sayNo()
		{
		}
	}
}
