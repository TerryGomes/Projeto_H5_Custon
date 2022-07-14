package services.community;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.entity.auction.AccessoryItemType;
import l2mv.gameserver.model.entity.auction.ArmorItemType;
import l2mv.gameserver.model.entity.auction.Auction;
import l2mv.gameserver.model.entity.auction.AuctionItemTypes;
import l2mv.gameserver.model.entity.auction.AuctionManager;
import l2mv.gameserver.model.entity.auction.EtcAuctionItemType;
import l2mv.gameserver.model.entity.auction.PetItemType;
import l2mv.gameserver.model.entity.auction.SuppliesItemType;
import l2mv.gameserver.model.entity.auction.WeaponItemType;
import l2mv.gameserver.model.items.AuctionStorage;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.ItemInstance.ItemLocation;
import l2mv.gameserver.model.items.PcInventory;
import l2mv.gameserver.model.items.TradeItem;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.stats.funcs.FuncTemplate;
import l2mv.gameserver.templates.item.ItemTemplate.Grade;
import l2mv.gameserver.utils.Util;

public class CommunityAuctionHouse implements ScriptFile, ICommunityBoardHandler
{
	private static final Logger _log = LoggerFactory.getLogger(CommunityAuctionHouse.class);

	@Override
	public void onLoad()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			_log.info("CommunityBoard: Auction System Service loaded.");
			CommunityBoardManager.getInstance().registerHandler(this);
		}
	}

	@Override
	public void onReload()
	{
		if (Config.COMMUNITYBOARD_ENABLED)
		{
			CommunityBoardManager.getInstance().removeHandler(this);
		}
	}

	@Override
	public void onShutdown()
	{
	}

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_maillist",
			"_bbsAuction_",
			"_bbsNewAuction"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		StringTokenizer st = new StringTokenizer(bypass, "_");
		String cmd = st.nextToken();
		player.setSessionVar("add_fav", null);
		String html = "";

		if (!Config.ENABLE_AUCTION_SYSTEM)
		{
			String msg = "<html><body><br><br><br><center>Auction System is currently disabled!</center></body></html>";
			ShowBoard.separateAndSend(msg, player);
		}

		if ("maillist".equals(cmd))
		{

			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_mail_list.htm", player);

			html = fillAuctionListPage(player, html, 1, new int[]
			{
				-1,
				-1
			}, "All", null, 1, 0, 0, 0);

		}
		// Auction List page
		else if (cmd.equals("bbsAuction"))
		{
			try
			{
				// page
				int page = Integer.parseInt(st.nextToken().trim());
				// Item types
				int[] itemTypes = new int[2];
				int i = 0;
				for (String type : st.nextToken().trim().split(" "))
				{
					itemTypes[i] = Integer.parseInt(type);
					i++;
				}
				// grade
				String grade = st.nextToken().trim();
				// search
				String search = st.nextToken().trim();
				// sorts
				int itemSort = Integer.parseInt(st.nextToken().trim());
				int gradeSort = Integer.parseInt(st.nextToken().trim());
				int quantitySort = Integer.parseInt(st.nextToken().trim());
				int priceSort = Integer.parseInt(st.nextToken().trim());

				// If button has been clicked
				if (st.hasMoreTokens())
				{
					// action 1 - buying item
					int action = Integer.parseInt(st.nextToken().trim());
					int auctionId = Integer.parseInt(st.nextToken().trim());

					if (action == 1)
					{
						// getting auction and sending info about buying item
						if (!st.hasMoreTokens())
						{
							player.sendMessage("Fill all the fields first!");
						}
						else
						{
							String quantity = st.nextToken().trim();
							Auction auction = AuctionManager.getInstance().getAuction(auctionId);
							if (auction == null || auction.getItem() == null)
							{
								player.sendMessage("Item has been already sold!");
							}
							else
							{
								long realPrice;
								try
								{
									realPrice = auction.getPricePerItem() * Long.parseLong(quantity);
								}
								catch (NumberFormatException e)
								{
									player.sendMessage("Invalid Quantity!");
									return;
								}
								ItemInstance item = auction.getItem();
								ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString("Are you sure, you want to buy " + quantity + ' ' + item.getName() + " for " + Util.getNumberWithCommas(realPrice) + " adena?");
								player.ask(packet, new ButtonClick(player, item, Buttons.Buy_Item, quantity));
							}
						}
					}
					// sending buy_item html
					html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_mail_buy_item.htm", player);
					html = fillPurchasePage(player, html, page, itemTypes, grade, search, itemSort, gradeSort, quantitySort, priceSort, auctionId);

				}
				else
				{
					// sending auction list html
					html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_mail_list.htm", player);
					html = fillAuctionListPage(player, html, page, itemTypes, grade, search, itemSort, gradeSort, quantitySort, priceSort);
				}
			}
			catch (NumberFormatException e)
			{
			}
		}
		else if (cmd.equals("bbsNewAuction"))
		{
			if (player.isInStoreMode())
			{
				player.sendMessage("You cannot open a new auction while operating with a private store.");
				return;
			}
			String currentItem = (st.hasMoreTokens() ? st.nextToken().trim() : "c0");
			int currentObjectId = Integer.parseInt(currentItem.substring(1));
			currentItem = currentItem.substring(0, 1);
			int line = Integer.parseInt((st.hasMoreTokens() ? st.nextToken().trim() : "0"));
			String buttonClicked = st.hasMoreTokens() ? st.nextToken().trim() : null;
			if (buttonClicked != null)
			{
				// New auction button clicked
				if (buttonClicked.equals("0"))
				{
					ItemInstance item = player.getInventory().getItemByObjectId(currentObjectId);
					// If player didnt fill textboxes
					boolean error = false;

					String[] vars = new String[2];// {quantity, salePrice}

					for (int i = 0; i < 2; i++)
					{
						if (st.hasMoreTokens())
						{
							vars[i] = st.nextToken().trim();
							if (vars[i].isEmpty())
							{
								error = true;
							}
						}
						else
						{
							error = true;
						}
					}

					if (error)
					{
						player.sendMessage("Fill all the fields!");
					}
					else if (item == null)
					{
						player.sendMessage("Item doesn't exist anymore!");
					}
					else
					{
						ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString("Are you sure, you want to sell " + item.getName() + "?");
						player.ask(packet, new ButtonClick(player, item, Buttons.New_Auction, vars[0], vars[1]));
					}
				}
				// Cancel auction button clicked
				else if (buttonClicked.equals("1"))
				{
					ItemInstance item = AuctionStorage.getInstance().getItemByObjectId(currentObjectId);
					if (item == null)
					{
						player.sendMessage("Item has been already sold");
						// TODO: I think that with just exiting should be enough, or the sale will still stay here?. This happens when i have the html open and meanwhile the item is
						// sold
						/*
						 * Collection<Auction> auctions = AuctionManager.getInstance().getMyAuctions(player.getObjectId());
						 * for (Auction a : auctions)
						 * {
						 * if (a.getItem() == null)
						 * _log.error("Auction bugged! Item:null itemId:" + currentObjectId + " auctionId:" + a.getAuctionId() + " Count:" + a.getCountToSell() + " Price:" +
						 * a.getPricePerItem() + " Seller:" + a.getSellerName() + "[" + a.getSellerObjectId() + "] store:" + a.isPrivateStore());
						 * else
						 * _log.error("Auction bugged! Item:" + a.getItem().getName() + " itemId:" + currentObjectId + " playerInv:" +
						 * player.getInventory().getItemByObjectId(player.getObjectId()) + " auctionId:" + a.getAuctionId() + " Count:" + a.getCountToSell() + " Price:" +
						 * a.getPricePerItem() + " Seller:" + a.getSellerName() + "[" + a.getSellerObjectId() + "] store:" + a.isPrivateStore());
						 * AuctionManager.getInstance().removeStore(player, a.getAuctionId());
						 * }
						 */
					}
					else if (!player.hasDialogAskActive())
					{
						ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString("Are you sure, you want to cancel " + item.getName() + " auction?");
						player.ask(packet, new ButtonClick(player, item, Buttons.Cancel_Auction));
					}
				}
			}

			// sending my auction page
			html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_mail_new_auction.htm", player);
			html = fillNewAuctionPage(player, html, currentItem.equals("n"), currentObjectId, line);
		}

		// sending community board
		ShowBoard.separateAndSend(html, player);
	}

	/**
	 * Auction List page
	 * @param player
	 * @param html
	 * @param page
	 * @param itemTypes
	 * @param itemGrade
	 * @param search
	 * @param itemSort
	 * @param gradeSort
	 * @param quantitySort
	 * @param priceSort
	 * @return
	 */
	private String fillAuctionListPage(Player player, String html, int page, int[] itemTypes, String itemGrade, String search, int itemSort, int gradeSort, int quantitySort, int priceSort)
	{
		int heightToBeUsed = 220;
		for (int i = 1; i <= 6; i++)
		{
			if (itemTypes[0] == i)
			{
				AuctionItemTypes[] types = getGroupsInType(itemTypes[0]);
				html = html.replace("%plusMinusBtn" + i + "%", "<button value=\"\" action=\"bypass _bbsAuction_ 1 _ -1 -1 _ %grade% _ %search% _ %itemSort% _ %gradeSort% _ %quantitySort% _ %priceSort%\" width=15 height=15 back=\"L2UI_CH3.QuestWndMinusBtn\" fore=\"L2UI_CH3.QuestWndMinusBtn\">");
				html = html.replace("%itemListHeight" + i + "%", String.valueOf(types.length * 5));
				heightToBeUsed -= types.length * 15;

				StringBuilder builder = new StringBuilder();
				builder.append("<table>");
				int count = 0;
				for (AuctionItemTypes itemType : types)
				{
					builder.append("<tr><td><table width=150 bgcolor=").append(count % 2 == 1 ? "22211d" : "1b1a15").append(">");
					builder.append("<tr><td width=150 height=17><font color=93886c>");
					builder.append("<button value=\"").append(itemType.toString().replace("_", " ")).append("\" action=\"bypass _bbsAuction_ 1 _ ").append(itemTypes[0]).append(" ").append(count).append(" _ %grade% _ %search% _ %itemSort% _ %gradeSort% _ %quantitySort% _ %priceSort%\" width=150 height=17 back=\"L2UI_CT1.emptyBtn\" fore=\"L2UI_CT1.emptyBtn\">");
					builder.append("</font></td></tr></table></td></tr>");
					count++;
				}
				builder.append("</table>");
				html = html.replace("%itemList" + i + "%", builder.toString());
			}
			else
			{
				html = html.replace("%plusMinusBtn" + i + "%", "<button value=\"\" action=\"bypass _bbsAuction_ 1 _ " + (i) + " -1 _ %grade% _ %search% _ %itemSort% _ %gradeSort% _ %quantitySort% _ %priceSort%\" width=15 height=15 back=\"L2UI_CH3.QuestWndPlusBtn\" fore=\"L2UI_CH3.QuestWndPlusBtn\">");
				html = html.replace("%itemListHeight" + i + "%", "0");
				html = html.replace("%itemList" + i + "%", "");
			}
		}
		html = html.replace("%lastItemHeight%", String.valueOf(heightToBeUsed - 40));

		StringBuilder builder = new StringBuilder();
		Collection<Auction> allAuctions = AuctionManager.getInstance().getAllAuctions();
		List<Auction> auctions = getRightAuctions(allAuctions, itemTypes, itemGrade, search);
		auctions = sortAuctions(auctions, itemSort, gradeSort, quantitySort, priceSort);

		int maxPage = (int) Math.ceil((double) auctions.size() / 10);
		// making loop, for getting single item from inventory
		for (int i = 10 * (page - 1); i < Math.min(auctions.size(), 10 * (page)); i++)
		{
			Auction auction;
			try
			{
				auction = auctions.get(i);
			}
			catch (RuntimeException e)
			{
				break;
			}
			ItemInstance item = auction.getItem();

			builder.append("<table border=0 cellspacing=1 cellpadding=0 width=558 height=30 bgcolor=").append(i % 2 == 1 ? "1a1914" : "23221d").append(">");

			builder.append("<tr><td width=280 height=25><table border=0 width=280 height=30><tr>");

			builder.append("<td width=32 background=" + item.getTemplate().getIcon() + "><button value=\"\" action=\"bypass _bbsAuction_ %page% _ %type% _ %grade% _ %search% _ %itemSort% _ %gradeSort% _ %quantitySort% _ %priceSort% _ 0 _ ").append(auction.getAuctionId()).append("\" width=32 height=32 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"></td>");
			builder.append(getItemName(item, 248, 25, auction.isPrivateStore()));
			builder.append("</tr></table></td><td width=40 height=30><center>");
			if (item.getCrystalType() != Grade.NONE)
			{
				builder.append("<img src=").append(getGradeIcon(item.getCrystalType())).append(" width=15 height=15>");
			}
			else
			{
				builder.append("None");
			}
			builder.append("</center></td><td width=65 height=30>");
			builder.append("<center>").append(auction.getCountToSell()).append("</center>");
			builder.append("</td><td width=120 height=30 valign=top align=right>");
			builder.append(Util.getNumberWithCommas(auction.getPricePerItem()));
			builder.append("</td></tr></table>");
		}
		html = html.replace("%auctionItems%", builder.toString());
		html = html.replace("%type%", itemTypes[0] + " " + itemTypes[1]);
		html = html.replace("%grade%", itemGrade);
		html = html.replace("%search%", search == null ? "" : search);
		html = html.replace("%totalItems%", String.valueOf(auctions.size()));
		html = html.replace("%itemSort%", String.valueOf(itemSort));
		html = html.replace("%gradeSort%", String.valueOf(gradeSort));
		html = html.replace("%quantitySort%", String.valueOf(quantitySort));
		html = html.replace("%priceSort%", String.valueOf(priceSort));
		html = html.replace("%changeItemSort%", "" + (itemSort <= 0 ? 1 : -1));
		html = html.replace("%changeGradeSort%", "" + (gradeSort <= 0 ? 1 : -1));
		html = html.replace("%changeQuantitySort%", "" + (quantitySort <= 0 ? 1 : -1));
		html = html.replace("%changePriceSort%", "" + (priceSort <= 0 ? 1 : -1));
		html = html.replace("%page%", "" + page);
		html = html.replace("%prevPage%", String.valueOf(Math.max(1, page - 1)));
		html = html.replace("%nextPage%", String.valueOf(Math.min(maxPage, page + 1)));
		html = html.replace("%lastPage%", String.valueOf(maxPage));
		html = html.replace("%adena%", Util.getNumberWithCommas(player.getAdena()));
		return html;
	}

	/**
	 * @param player
	 * @param html
	 * @param page
	 * @param itemTypes
	 * @param itemGrade
	 * @param search
	 * @param itemSort
	 * @param gradeSort
	 * @param quantitySort
	 * @param priceSort
	 * @param auctionId
	 * @return Purchase Page
	 */
	private String fillPurchasePage(Player player, String html, int page, int[] itemTypes, String itemGrade, String search, int itemSort, int gradeSort, int quantitySort, int priceSort, int auctionId)
	{

		Auction auction = AuctionManager.getInstance().getAuction(auctionId);
		if (auction == null || auction.getItem() == null)
		{
			return "";
		}
		ItemInstance choosenItem = auction.getItem();

		// Filling auctionDescription
		StringBuilder builder = new StringBuilder();
		// Enchant
		if (choosenItem.getEnchantLevel() > 0)
		{
			builder.append("<center><font color=b3a683>+").append(choosenItem.getEnchantLevel()).append(" </font>");
		}
		// Name
		builder.append("<center>" + choosenItem.getName());

		// Special Ability
		if (!choosenItem.getTemplate().getAdditionalName().isEmpty())
		{
			builder.append("<font color=b3a683> ").append(choosenItem.getTemplate().getAdditionalName()).append(" </font>");
		}
		// Grade
		builder.append("<br><center><img src=").append(getGradeIcon(choosenItem.getCrystalType())).append(" width=15 height=15>");
		// Seller
		builder.append("<br><br><br><font color=827d78><br><br>Seller:</font> <font color=94775b>" + auction.getSellerName() + "</font>");
		if (choosenItem.isEquipable())
		{
			// P Atk
			int pAtk = getFunc(choosenItem, Stats.POWER_ATTACK);
			if (pAtk > 0)
			{
				builder.append("<br><br><font color=827d78>P. Atk:</font> <font color=94775b>").append(pAtk).append(" </font>");
			}
			// M Atk
			int mAtk = getFunc(choosenItem, Stats.MAGIC_ATTACK);
			if (mAtk > 0)
			{
				builder.append("<br><font color=827d78>M. Atk:</font> <font color=94775b>").append(mAtk).append(" </font>");
			}
			// P Def
			int pDef = getFunc(choosenItem, Stats.POWER_DEFENCE);
			if (pDef > 0)
			{
				builder.append("<br><font color=827d78>P. Def:</font> <font color=94775b>").append(pDef).append(" </font>");
			}
			// M Def
			int mDef = getFunc(choosenItem, Stats.MAGIC_DEFENCE);
			if (mDef > 0)
			{
				builder.append("<br><font color=827d78>M. Def:</font> <font color=94775b>").append(mDef).append(" </font>");
			}
			// Element
			if (choosenItem.getAttackElement() != Element.NONE)
			{
				builder.append("<br><br><br><font color=827d78>").append(getElementName(choosenItem.getAttackElement())).append(" Atk ").append(choosenItem.getAttackElementValue());
				builder.append("</font><br><img src=L2UI_CT1.Gauge_DF_Attribute_").append(getElementName(choosenItem.getAttackElement())).append(" width=100 height=10>");
			}
			if (choosenItem.isArmor())
			{
				for (Element element : Element.values())
				{
					if (element != Element.NONE)
					{
						if (choosenItem.getAttributeElementValue(element, false) > 0)
						{
							builder.append("<br><font color=827d78>").append(getElementName(element)).append(" Def ").append(getElementName(element)).append(" </font><img src=L2UI_CT1.Gauge_DF_Attribute_").append(getElementName(element)).append(" width=100 height=10>");
						}
					}
				}
			}
		}
		builder.append("</center>");

		// Filling other values
		html = html.replace("%page%", String.valueOf(page));
		html = html.replace("%type%", itemTypes[0] + " " + itemTypes[1]);
		html = html.replace("%grade%", itemGrade);
		html = html.replace("%search%", search == null ? "" : search);
		html = html.replace("%itemSort%", String.valueOf(itemSort));
		html = html.replace("%gradeSort%", String.valueOf(gradeSort));
		html = html.replace("%quantitySort%", String.valueOf(quantitySort));
		html = html.replace("%priceSort%", String.valueOf(priceSort));
		html = html.replace("%auctionId%", String.valueOf(auctionId));
		html = html.replace("%icon%", "<img src=icon." + choosenItem.getTemplate().getIcon() + " width=32 height=32>");
		html = html.replace("%fullName%", "<table width=240 height=50><tr>" + getItemName(choosenItem, 240, 50, auction.isPrivateStore(), (auction.getCountToSell() > 1 ? " x" + auction.getCountToSell() : "")) + "</tr></table>");
		html = html.replace("%quantity%", (auction.getCountToSell() > 1 ? "<edit var=\"quantity\" type=number value=\"\" width=160 height=12>" : "<center><font color=94775b>1</font></center>"));
		if (auction.getCountToSell() <= 1)
		{
			html = html.replace("$quantity", "1");
		}
		html = html.replace("%pricePerItem%", "<font color=94775b>" + String.valueOf(Util.getNumberWithCommas(auction.getPricePerItem())) + "</font>");
		html = html.replace("%totalPrice%", "<font color=94775b>" + String.valueOf(Util.getNumberWithCommas(auction.getCountToSell() * auction.getPricePerItem())) + "</font>");
		html = html.replace("%totalAdena%", "<font color=94775b>" + String.valueOf(Util.getNumberWithCommas(player.getAdena())) + "</font>");
		html = html.replace("%fullAuctionDescription%", builder.toString());

		return html;
	}

	/**
	 * My Auctions page
	 * @param player
	 * @param html
	 * @param newItem
	 * @param currentItem
	 * @param line
	 * @return
	 */
	private String fillNewAuctionPage(Player player, String html, boolean newItem, int currentItem, int line)
	{
		// getting items from inventory, that can be auctioned
		List<ItemInstance> itemsToAuction = getItemsToAuction(player);
		int maxLine = (int) Math.ceil((double) itemsToAuction.size() / 6);
		StringBuilder builder = new StringBuilder();
		int index = 0;

		// making loop, for getting single item from inventory
		for (int i = 6 * (line); i < 6 * (line + 3); i++)
		{
			// getting item
			ItemInstance item = i >= 0 && itemsToAuction.size() > i ? itemsToAuction.get(i) : null;
			// adding new line in table
			if (index % 6 == 0)
			{
				builder.append("<tr>");
			}

			// Making button from icon of the item
			builder.append("<td width=32 align=center valign=top background=\"L2UI_CT1.ItemWindow_DF_SlotBox\">");
			if (item != null)
			{
				builder.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=" + item.getTemplate().getIcon() + ">");
			}
			else
			{
				builder.append("<table border=0 cellspacing=0 cellpadding=0 width=32 height=32>");
			}
			builder.append("<tr>");
			builder.append("<td width=32 height=32 align=center valign=top>");
			if (item != null)
			{
				builder.append("<button value=\"\" action=\"bypass _bbsNewAuction_ n").append(item.getObjectId()).append(" _ ").append(line).append("\" width=32 height=32 back=L2UI_CT1.ItemWindow_DF_Frame_Down fore=L2UI_CT1.ItemWindow_DF_Frame />");
			}
			else
			{
				builder.append("<br>");
			}
			builder.append("</td>");
			builder.append("</tr>");
			builder.append("</table>");
			builder.append("</td>");
			// ending line in table
			if (index % 6 == 5)
			{
				builder.append("</tr>");
			}

			index++;
		}
		// adding ending of line, just in case
		if (index % 6 != 5)
		{
			builder.append("</tr>");
		}

		html = html.replace("%auctionableItems%", builder.toString());

		builder = new StringBuilder();

		// getting my current auctions
		Collection<Auction> myAuctions = AuctionManager.getInstance().getMyAuctions(player);
		Auction[] auctions = myAuctions.toArray(new Auction[myAuctions.size()]);
		// Filter the items from private store from my auctions
		boolean pakage = player.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE;
		if (pakage || player.getPrivateStoreType() == Player.STORE_PRIVATE_SELL)
		{
			for (TradeItem ti : player.getSellList())
			{
				for (Auction auction : auctions) // Dont use myAuctions here...
				{
					if (auction.getItem() != null && auction.getItem().getObjectId() == ti.getObjectId())
					{
						myAuctions.remove(auction);
					}
				}
			}
		}

		auctions = myAuctions.toArray(new Auction[myAuctions.size()]);

		int i = 0;
		for (; i < 10; i++)
		{

			if (auctions.length <= i)
			{
				break;
			}
			// getting single auction
			Auction auction = auctions[i];
			ItemInstance item = auction.getItem();
			// making new table
			builder.append("<table border=0 cellspacing=0 cellpadding=0 width=470 bgcolor=").append(i % 2 == 1 ? "1a1914" : "23221d").append(">");
			// starting line
			builder.append("<tr><td width=260><table border=0 width=260><tr><td width=32 height=32 background=" + item.getTemplate().getIcon() + ">");
			// button with image of item icon
			if (!player.hasDialogAskActive())
			{
				builder.append("<button value=\"\" action=\"bypass _bbsNewAuction_ c").append(item.getObjectId()).append(" _ ").append(line).append(" _ 1\" width=32 height=32 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\">");
			}
			else
			{
				builder.append("<button width=32 height=32 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\">");
			}
			builder.append("</td>");
			// getting item name
			builder.append(getItemName(item, 228, 25, auction.isPrivateStore()));
			builder.append("</tr></table></td><td width=55><center>");
			// getting item grade
			if (item.getCrystalType() != Grade.NONE)
			{
				builder.append("<img src=").append(getGradeIcon(item.getCrystalType())).append(" width=15 height=15>");
			}
			else
			{
				builder.append("None");
			}
			builder.append("</center></td><td width=55>");
			// getting item count
			builder.append("<center>").append(auction.getCountToSell()).append("</center>");
			builder.append("</td><td width=100 align=right>");
			// getting item price
			builder.append(Util.getNumberWithCommas(auction.getPricePerItem()));
			builder.append("</td></tr></table>");
		}
		// making longer table, in case there are less items than 10
		if (i < 10)
		{
			builder.append("<table border=0 cellspacing=0 cellpadding=0 width=470 height=").append((10 - i) * 35).append("><tr><td width=260><br></td><td width=55></td><td width=55></td><td width=100></td></tr></table>");
		}

		// replacements
		html = html.replace("%auctionItems%", builder.toString());
		html = html.replace("%auctioned%", "" + auctions.length);
		html = html.replace("%totalPrice%", Util.getNumberWithCommas(0));
		html = html.replace("%saleFee%", Util.getNumberWithCommas(Config.AUCTION_FEE));
		html = html.replace("%adena%", Util.getNumberWithCommas(player.getAdena()));
		html = html.replace("%currentItem%", (newItem ? "n" : "c") + currentItem);
		html = html.replace("%prevLine%", String.valueOf(Math.max(0, line - 1)));
		html = html.replace("%curLine%", String.valueOf(line));
		html = html.replace("%nextLine%", String.valueOf(Math.min(maxLine - 3, line + 1)));
		html = html.replace("%lastLine%", String.valueOf(Math.max(1, maxLine - 3)));

		ItemInstance choosenItem = (currentItem > 0 ? player.getInventory().getItemByObjectId(currentItem) : null);

		html = html.replace("%choosenImage%", (choosenItem != null ? "<img src=icon." + choosenItem.getTemplate().getIcon() + " width=32 height=32>" : ""));
		html = html.replace("%choosenItem%", (choosenItem != null ? (getItemName(choosenItem, 180, 45, false, (choosenItem.getCount() > 1 ? " x" + choosenItem.getCount() : ""))) : ""));
		html = html.replace("%quantity%", (choosenItem == null || choosenItem.getCount() > 1 ? "<edit var=\"quantity\" type=number value=\"\" width=160 height=12>" : "<center>1</center>"));
		html = html.replace("%NewAuctionButton%", (choosenItem != null ? "<center><button value=\"New Auction\" action=\"bypass _bbsNewAuction_ " + (newItem ? "n" : "c") + currentItem + " _ " + line + " _ 0 _ " + (choosenItem == null || choosenItem.getCount() > 1 ? "$quantity" : "1") + " _ $sale_price\" width=90 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>" : ""));

		return html;
	}

	/**
	 * searching for auctions, that fits requirements
	 * @param allAuctions
	 * @param splitedTypes
	 * @param itemGrade
	 * @param search
	 * @return
	 */
	private List<Auction> getRightAuctions(Collection<Auction> allAuctions, int[] splitedTypes, String itemGrade, String search)
	{
		List<Auction> auctions = new ArrayList<Auction>();
		for (Auction auction : allAuctions)
		{
			if (splitedTypes != null && splitedTypes[0] >= 0)
			{
				boolean found = false;
				AuctionItemTypes realItemType = auction.getItemType();
				AuctionItemTypes[] lookedTypes = getGroupsInType(splitedTypes[0]);

				if (splitedTypes[1] >= 0)
				{
					AuctionItemTypes lookedType = lookedTypes[splitedTypes[1]];
					lookedTypes = new AuctionItemTypes[1];
					lookedTypes[0] = lookedType;
				}
				for (AuctionItemTypes itemType : lookedTypes)
				{
					if (realItemType == itemType)
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					continue;
				}
			}
			if (!itemGrade.equals("All"))
			{
				if (!auction.getItem().getCrystalType().name().equalsIgnoreCase(itemGrade))
				{
					continue;
				}
			}
			if (search != null)
			{
				if (!StringUtils.containsIgnoreCase(auction.getItem().getName(), search))
				{
					continue;
				}
			}
			auctions.add(auction);
		}
		return auctions;
	}

	/**
	 * sending element name with first letter as big
	 * @param element
	 * @return
	 */
	private String getElementName(Element element)
	{
		String name = element.name();
		name = name.substring(0, 1) + name.substring(1).toLowerCase();
		return name;
	}

	/**
	 * @param player
	 * @return Items from inventory, that can be auctioned
	 */
	private List<ItemInstance> getItemsToAuction(Player player)
	{
		PcInventory inventory = player.getInventory();
		List<ItemInstance> items = new ArrayList<ItemInstance>();
		if (player.isInStoreMode())
		{
			return items; // Empty list to prevent exploit during store mode.
		}
		for (ItemInstance item : inventory.getItems())
		{
			if (item.isAdena() || !item.getTemplate().isTradeable() || (item.getLocation() == ItemLocation.AUCTION) || item.getTemplate().isQuest())
			{
				continue;
			}
			if (item.isAugmented())
			{
				continue;
			}
			if (item.isStackable())
			{
				for (Auction playerAuction : AuctionManager.getInstance().getMyAuctions(player))
				{
					if (playerAuction.getItem().getItemId() == item.getItemId())
					{
						continue;
					}
				}
			}
			if (item.isEquipped())
			{
				continue;
			}
			items.add(item);
		}
		return items;
	}

	/**
	 * sending item stat like pAtk or mDef
	 * @param item
	 * @param stat
	 * @return
	 */
	private int getFunc(ItemInstance item, Stats stat)
	{
		for (FuncTemplate func : item.getTemplate().getAttachedFuncs())
		{
			if (func._stat == stat)
			{
				return (int) func._value;
			}
		}
		return 0;
	}

	/**
	 * @param item
	 * @param windowWidth - 1=Auction List Item 2. Current Auction item 3. Items to be auctioned
	 * @param windowHeight
	 * @param isPrivStore
	 * @param addToItemName
	 * @return
	 */
	private String getItemName(ItemInstance item, int windowWidth, int windowHeight, boolean isPrivStore, String... addToItemName)
	{
		StringBuilder builder = new StringBuilder();

		if (item.getEnchantLevel() > 0)
		{
			builder.append("<font color=b3a683>+").append(item.getEnchantLevel()).append(" </font>");
		}

		final String[] parts = item.getName().split(" - ");
		String itemName = item.getName();
		itemName = itemName.replace("<", "&lt;").replace(">", "&gt;");

		// Synerge - Masterwork items
		if (item.getTemplate().isMasterwork() || parts.length > 2 || ((item.isArmor() || item.isAccessory()) && item.getName().endsWith("of Chaos")))
		{
			builder.append("<font color=d4ce25>").append(itemName).append("</font>");
		}
		else
		{
			builder.append(itemName);
		}

		if (!item.getTemplate().getAdditionalName().isEmpty())
		{
			builder.append("<font color=b3a683> ").append(item.getTemplate().getAdditionalName()).append(" </font>");
		}
		if (item.getAttackElement() != Element.NONE)
		{
			builder.append("<font color=").append(getElementColor(item.getAttackElement())).append("> ").append(getElementName(item.getAttackElement())).append(" +").append(item.getAttackElementValue()).append("</font>");
		}
		if (item.isArmor())
		{
			for (Element element : Element.values())
			{
				if (element != Element.NONE)
				{
					if (item.getAttributeElementValue(element, false) > 0)
					{
						builder.append(" <font color=").append(getElementColor(element)).append(">").append(getElementName(element)).append("</font>");
					}
				}
			}
		}
		if (isPrivStore)
		{
			builder.append(" <font color=DE9DE8>(Private Store)</font>");
		}

		return "<td align=left width=228 height=25>" + builder.toString() + (addToItemName.length > 0 ? addToItemName[0] : "") + "</td>";
	}

	/**
	 * returning font color for each element
	 * @param element
	 * @return
	 */
	private String getElementColor(Element element)
	{
		switch (element)
		{
		case EARTH:
			return "94775b";
		case FIRE:
			return "b36464";
		case HOLY:
			return "8c8787";
		case UNHOLY:
			return "4c558f";
		case WATER:
			return "528596";
		case WIND:
			return "768f91";
		default:
			return "768f91";
		}
	}

	/**
	 * sending icon of the grade, like: L2UI_CT1.Icon_DF_ItemGrade_S
	 * @param grade
	 * @return
	 */
	private String getGradeIcon(Grade grade)
	{
		if (grade != Grade.NONE)
		{
			return "L2UI_CT1.Icon_DF_ItemGrade_" + grade.toString().replace("S8", "8");
		}
		else
		{
			return "";
		}
	}

	private static final AuctionItemTypes[][] ALL_AUCTION_ITEM_TYPES =
	{
		AccessoryItemType.values(),
		ArmorItemType.values(),
		EtcAuctionItemType.values(),
		PetItemType.values(),
		SuppliesItemType.values(),
		WeaponItemType.values()
	};

	/**
	 * returns all item groups in type, for example type = 1 will return everything that is in AccessoryItemType
	 * @param type
	 * @return
	 */
	private AuctionItemTypes[] getGroupsInType(int type)
	{
		if (type > 0 && type < 7)
		{
			return ALL_AUCTION_ITEM_TYPES[type - 1];
		}
		return null;
	}

	private List<Auction> sortAuctions(List<Auction> auctionsToSort, int itemSort, int gradeSort, int quantitySort, int priceSort)
	{
		if (itemSort != 0)
		{
			Collections.sort(auctionsToSort, new ItemNameComparator(itemSort == 1 ? true : false));
		}
		else if (gradeSort != 0)
		{
			Collections.sort(auctionsToSort, new GradeComparator(gradeSort == 1 ? true : false));
		}
		else if (quantitySort != 0)
		{
			Collections.sort(auctionsToSort, new QuantityComparator(quantitySort == 1 ? true : false));
		}
		else if (priceSort != 0)
		{
			Collections.sort(auctionsToSort, new PriceComparator(priceSort == 1 ? true : false));
		}

		return auctionsToSort;
	}

	private static enum Buttons
	{
		New_Auction, Cancel_Auction, Buy_Item
	}

	private class ButtonClick implements OnAnswerListener
	{
		private final Player _player;
		private final ItemInstance _item;
		private final Buttons _button;
		private final String[] _args;

		private ButtonClick(Player player, ItemInstance item, Buttons button, String... args)
		{
			_player = player;
			_item = item;
			_button = button;
			_args = args;
		}

		@Override
		public void sayYes()
		{
			switch (_button)
			{
			case New_Auction:
				String sQuantity = _args[0].replace(",", "").replace(".", "");
				String sPricePerItem = _args[1].replace(",", "").replace(".", "");
				long quantity;
				long pricePerItem;
				try
				{
					quantity = Long.parseLong(sQuantity);
					pricePerItem = Long.parseLong(sPricePerItem);
				}
				catch (NumberFormatException e)
				{
					onBypassCommand(_player, "_bbsNewAuction_ c0 _ 0");
					return;
				}
				AuctionManager.getInstance().checkAndAddNewAuction(_player, _item, quantity, pricePerItem);
				onBypassCommand(_player, "_bbsNewAuction_ c0 _ 0");
				break;
			case Cancel_Auction:
				AuctionManager.getInstance().deleteAuction(_player, _item);
				onBypassCommand(_player, "_bbsNewAuction_ c0 _ 0");
				break;
			case Buy_Item:
				AuctionManager.getInstance().buyItem(_player, _item, Long.parseLong(_args[0]));
				onBypassCommand(_player, "_maillist");
				break;
			}
		}

		@Override
		public void sayNo()
		{
			switch (_button)
			{
			case New_Auction:
			case Cancel_Auction:
				onBypassCommand(_player, "_bbsNewAuction_ c0 _ 0");
				break;
			case Buy_Item:
				onBypassCommand(_player, "_maillist");
				break;
			}
		}
	}

	private static class ItemNameComparator implements Comparator<Auction>, Serializable
	{
		private static final long serialVersionUID = 7850753246573158288L;
		private final boolean rightOrder;

		private ItemNameComparator(boolean rightOrder)
		{
			this.rightOrder = rightOrder;
		}

		@Override
		public int compare(Auction o1, Auction o2)
		{
			if (rightOrder)
			{
				return o1.getItem().getName().compareTo(o2.getItem().getName());
			}
			else
			{
				return o2.getItem().getName().compareTo(o1.getItem().getName());
			}
		}
	}

	private static class GradeComparator implements Comparator<Auction>, Serializable
	{
		private static final long serialVersionUID = 4096813325789557518L;
		private final boolean rightOrder;

		private GradeComparator(boolean rightOrder)
		{
			this.rightOrder = rightOrder;
		}

		@Override
		public int compare(Auction o1, Auction o2)
		{
			int grade1 = o1.getItem().getCrystalType().ordinal();
			int grade2 = o2.getItem().getCrystalType().ordinal();

			if (rightOrder)
			{
				return Integer.compare(grade1, grade2);
			}
			else
			{
				return Integer.compare(grade2, grade1);
			}
		}
	}

	private static class QuantityComparator implements Comparator<Auction>, Serializable
	{
		private static final long serialVersionUID = 1572294088027593791L;
		private final boolean rightOrder;

		private QuantityComparator(boolean rightOrder)
		{
			this.rightOrder = rightOrder;
		}

		@Override
		public int compare(Auction o1, Auction o2)
		{
			if (rightOrder)
			{
				return Long.compare(o1.getCountToSell(), o2.getCountToSell());
			}
			else
			{
				return Long.compare(o2.getCountToSell(), o1.getCountToSell());
			}
		}
	}

	private static class PriceComparator implements Comparator<Auction>, Serializable
	{
		private static final long serialVersionUID = 7065225580068613464L;
		private final boolean rightOrder;

		private PriceComparator(boolean rightOrder)
		{
			this.rightOrder = rightOrder;
		}

		@Override
		public int compare(Auction o1, Auction o2)
		{
			if (rightOrder)
			{
				return Long.compare(o1.getPricePerItem(), o2.getPricePerItem());
			}
			else
			{
				return Long.compare(o2.getPricePerItem(), o1.getPricePerItem());
			}
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}
}
