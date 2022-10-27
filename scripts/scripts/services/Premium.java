package services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import l2mv.commons.lang.reference.HardReference;
import l2mv.gameserver.Config;
import l2mv.gameserver.dao.AccountBonusDAO;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.PremiumHolder;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.premium.PremiumAccount;
import l2mv.gameserver.model.premium.PremiumStart;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.gspackets.BonusRequest;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.ExBR_PremiumState;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.HtmlUtils;
import l2mv.gameserver.utils.ItemActionLog;
import l2mv.gameserver.utils.Log;
import l2mv.gameserver.utils.TimeUtils;
import l2mv.gameserver.utils.Util;

public class Premium extends Functions
{
	public void see()
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (Config.PREMIUM_ACCOUNT_TYPE == 0)
		{
			player.sendMessage("Service is turned off.");
			return;
		}
		String html = HtmCache.getInstance().getNotNull("scripts/services/Premium/index.htm", player);
		String button = null;
		String template = HtmCache.getInstance().getNotNull("scripts/services/Premium/button.htm", player);
		String block = null;
		for (PremiumAccount premium : PremiumHolder.getInstance().getAllPremiums())
		{
			block = template;
			block = block.replace("{name}", premium.getName());
			block = block.replace("{icon}", premium.getIcon());
			block = block.replace("{time}", TimeUtils.formatTime(premium.getTime()));
			block = block.replace("{price}", new CustomMessage("<font color=99CC66>Cost:</font> {0}.").addString(Util.formatPay(player, premium.getPriceCount(), premium.getPriceId())).toString());
			block = block.replace("{link}", "bypass -h scripts_services.Premium:info " + premium.getId());
			button = button + block;
		}
		html = html.replace("{body}", button);

		NpcHtmlMessage parse = new NpcHtmlMessage(5).setHtml(HtmlUtils.bbParse(html));
		player.sendPacket(parse);
	}

	public void info(String[] arg)
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (Config.PREMIUM_ACCOUNT_TYPE == 0)
		{
			player.sendMessage("Service is turned off.");
			return;
		}
		String html = HtmCache.getInstance().getNotNull("scripts/services/Premium/info.htm", player);

		int val = Util.isNumber(arg[0]) ? Integer.parseInt(arg[0]) : 1;
		PremiumAccount premium = PremiumHolder.getInstance().getPremium(val);
		html = html.replace("{id}", String.valueOf(premium.getId()));
		html = html.replace("{name}", premium.getName());
		html = html.replace("{icon}", premium.getIcon());
		html = html.replace("{price}", new CustomMessage("<font color=99CC66>Cost:</font> {0}.").addString(Util.formatPay(player, premium.getPriceCount(), premium.getPriceId())).toString());

		html = html.replace("{xp}", "+" + Util.cutOff((premium.getExp() - 1) * 100, 0) + "%");
		html = html.replace("{sp}", "+" + Util.cutOff((premium.getSp() - 1) * 100, 0) + "%");
		html = html.replace("{adena}", "+" + Util.cutOff((premium.getAdena() - 1) * 100, 0) + "%");
		html = html.replace("{items}", "+" + Util.cutOff((premium.getItems() - 1) * 100, 0) + "%");
		html = html.replace("{spoil}", "+" + Util.cutOff((premium.getSpoil() - 1) * 100, 0) + "%");
		html = html.replace("{epaulette}", "+" + Util.cutOff((premium.getEpaulette() - 1) * 100, 0) + "%");
		html = html.replace("{weight}", "+" + Util.cutOff((premium.getWeight() - 1) * 100, 0) + "%");
		html = html.replace("{masterwork}", "+" + premium.getMasterWorkChance() + "%");
		html = html.replace("{craft}", "+" + premium.getCraftChance() + "%");
		html = html.replace("{attribute}", "+" + premium.getAttributeChance() + "%");

		html = html.replace("{xp_f}", String.valueOf(Util.cutOff(Config.RATE_XP * premium.getExp(), 2)));
		html = html.replace("{sp_f}", String.valueOf(Util.cutOff(Config.RATE_SP * premium.getSp(), 2)));
		html = html.replace("{adena_f}", String.valueOf(Util.cutOff(Config.RATE_DROP_ADENA * premium.getAdena(), 2)));
		html = html.replace("{items_f}", String.valueOf(Util.cutOff(Config.RATE_DROP_ITEMS * premium.getItems(), 2)));
		html = html.replace("{spoil_f}", String.valueOf(Util.cutOff(Config.RATE_DROP_SPOIL * premium.getSpoil(), 2)));
		html = html.replace("{epaulette_f}", String.valueOf(Util.cutOff(Config.RATE_DROP_SIEGE_GUARD * premium.getEpaulette(), 2)));
		html = html.replace("{weight_f}", String.valueOf(Util.cutOff(player.getMaxLoad() * premium.getWeight(), 2)));
		html = html.replace("{masterwork_f}", String.valueOf(Util.cutOff(Config.CRAFT_MASTERWORK_CHANCE + premium.getMasterWorkChance(), 2)) + "%");
		html = html.replace("{attribute_f}", String.valueOf(Util.cutOff(Config.ENCHANT_ATTRIBUTE_CRYSTAL_CHANCE + premium.getAttributeChance(), 2) + "%"));

		html = html.replace("{time}", TimeUtils.formatTime(premium.getTime()));

		NpcHtmlMessage parse = new NpcHtmlMessage(5).setHtml(HtmlUtils.bbParse(html));
		player.sendPacket(parse);
	}

	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	public void choice(String[] arg)
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (Config.PREMIUM_ACCOUNT_TYPE == 0)
		{
			player.sendMessage("Service is turned off.");
			return;
		}
		if (arg[0].isEmpty())
		{
			return;
		}
		int current = (int) (System.currentTimeMillis() / 1000L);
		int bonusExpire = player.getNetConnection().getBonusExpire();
		if ((bonusExpire != 0) && (bonusExpire < current))
		{
			player.sendMessage("Error!");
			return;
		}
		int val = Util.isNumber(arg[0]) ? Integer.parseInt(arg[0]) : 1;
		PremiumAccount premium = PremiumHolder.getInstance().getPremium(val);
		int item = premium.getPriceId();
		long count = premium.getPriceCount();
		int id = premium.getId();

		int time = premium.getTime();

		int newBonusTime = bonusExpire == 0 ? current + time : bonusExpire + time;

		boolean isLogin = Config.PREMIUM_ACCOUNT_TYPE == 1;
		if ((isLogin) && (AuthServerCommunication.getInstance().isShutdown()))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(5).setHtml("scripts/services/Premium/loginoff.htm");
			player.sendPacket(html);
			return;
		}
		final ItemActionLog log = Util.getPay(player, item, count, "DonatePremium_" + val, true);
		if (log != null)
		{
			if (isLogin)
			{
				AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), id, newBonusTime));
			}
			else
			{
				AccountBonusDAO.getInstance().insert(player.getAccountName(), id, newBonusTime);
			}
			player.stopBonusTask(true);

			player.getNetConnection().setBonus(id);
			player.getNetConnection().setBonusExpire(newBonusTime);

			PremiumStart.getInstance().start(player);
			PremiumStart.getInstance().updateItems(false, player);
			if (player.getParty() != null)
			{
				player.getParty().recalculatePartyData();
			}
			player.sendPacket(new ExBR_PremiumState(player, true));
			String html;
			if (isLogin)
			{
				html = HtmCache.getInstance().getNotNull("scripts/services/Premium/login.htm", player);
			}
			else
			{
				html = HtmCache.getInstance().getNotNull("scripts/services/Premium/game.htm", player);
			}
			String end = TIME_FORMAT.format(new Date(newBonusTime * 1000L));
			html = html.replace("{time}", TimeUtils.formatTime(time));
			html = html.replace("{end}", end);

			NpcHtmlMessage parse = new NpcHtmlMessage(5).setHtml(HtmlUtils.bbParse(html));
			player.sendPacket(parse);

			player.broadcastPacket(new MagicSkillUse(player, player, 6463, 1, 0, 0L));
			// Log.service("Player " + player + " buy Premium-Account (id:" + id + ", name: " + premium.getName() + ") at " + TimeUtils.formatTime(time) + ", Price: " +
			// Util.formatAdena(count) + " " + Util.getItemName(item) + " End: " + end + ".", getClass().getName());
			Log.logItemActions(log);
		}
	}

	public void giftAsk(String[] arg)
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (Config.PREMIUM_ACCOUNT_TYPE == 0)
		{
			player.sendMessage("Service is turned off.");
			return;
		}
		if ((Config.PREMIUM_ACCOUNT_TYPE == 1) && (AuthServerCommunication.getInstance().isShutdown()))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(5).setHtml("scripts/services/Premium/loginoff.htm");
			player.sendPacket(html);
			return;
		}
		Player target = Util.isNumber(arg[0]) ? GameObjectsStorage.getPlayer(Integer.parseInt(arg[0])) : null;
		if (target == null)
		{
			player.sendMessage(new CustomMessage("Error."));
			return;
		}
		int bonusExpire = target.getNetConnection().getBonusExpire();
		if ((bonusExpire != 0) && (bonusExpire < (int) (System.currentTimeMillis() / 1000L)))
		{
			player.sendMessage("Service is turned off.");
			return;
		}
		PremiumAccount premium = PremiumHolder.getInstance().getPremium(Config.PREMIUM_ACCOUNT_PARTY_GIFT_ID);
		int item = premium.getPriceId();
		long cost = premium.getPriceCount();
		int time = premium.getTime();
		int id = premium.getId();
		ConfirmDlg dlg = new ConfirmDlg(SystemMsg.S1, 60000).addString(new CustomMessage("Do you really want to give Premium Account {0} at {1} for player {2}? Cost of operation: {3}").addString(premium.getName()).addString(TimeUtils.formatTime(time)).addString(target.getName()).addString(Util.formatPay(player, cost, item)).toString());

		player.ask(dlg, new Ask(player, target, time, cost, item, id));
	}

	private class Ask implements OnAnswerListener
	{
		private final Player _player;
		private final HardReference<Player> _target;
		private final int days;
		private final long cost;
		private final int item;
		private final int rate;

		protected Ask(Player player, Player target, int days, long cost, int item, int rate)
		{
			_player = player;
			this.days = days;
			this.cost = cost;
			this.item = item;
			this.rate = rate;
			_target = target.getRef();
		}

		@Override
		public void sayYes()
		{
			Player target = _target.get();
			if (_player == null || (!_player.isOnline()) || (target == null) || (!target.isOnline()))
			{
				return;
			}
			if (Util.getPay(_player, item, cost, "DonatePremium_" + item, true) != null)
			{
				gift(target, days, rate);
			}
		}

		@Override
		public void sayNo()
		{
			//
		}
	}

	public void gift(Player player, int time, int rate)
	{
		int current = (int) (System.currentTimeMillis() / 1000L);
		int bonusExpire = player.getNetConnection().getBonusExpire();
		int newBonusTime = bonusExpire == 0 ? current + time : bonusExpire + time;
		if (Config.PREMIUM_ACCOUNT_TYPE == 1)
		{
			AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), rate, newBonusTime));
		}
		else
		{
			AccountBonusDAO.getInstance().insert(player.getAccountName(), rate, newBonusTime);
		}
		player.stopBonusTask(true);

		player.getNetConnection().setBonus(rate);
		player.getNetConnection().setBonusExpire(newBonusTime);

		PremiumStart.getInstance().start(player);
		PremiumStart.getInstance().updateItems(false, player);
		if (player.getParty() != null)
		{
			player.getParty().recalculatePartyData();
		}
		player.sendPacket(new ExBR_PremiumState(player, true));

		player.broadcastPacket(new MagicSkillUse(player, player, 6463, 1, 0, 0L));
		Log.service("Player " + player + " buy gift Premium-Account id:" + rate + " at " + TimeUtils.formatTime(time) + ".", getClass().getName());
	}
}
