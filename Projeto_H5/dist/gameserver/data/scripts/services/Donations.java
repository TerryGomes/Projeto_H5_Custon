package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.Config;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.DonationHolder;
import l2mv.gameserver.data.xml.holder.SkillAcquireHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.SkillLearn;
import l2mv.gameserver.model.base.AcquireType;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.donatesystem.Attribution;
import l2mv.gameserver.model.donatesystem.DonateItem;
import l2mv.gameserver.model.donatesystem.Donation;
import l2mv.gameserver.model.donatesystem.Enchant;
import l2mv.gameserver.model.donatesystem.FoundList;
import l2mv.gameserver.model.donatesystem.SimpleList;
import l2mv.gameserver.model.entity.events.impl.SiegeEvent;
import l2mv.gameserver.model.entity.olympiad.Olympiad;
import l2mv.gameserver.model.items.Inventory;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.items.PcInventory;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.pledge.UnitMember;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.gspackets.ChangeAccessLevel;
import l2mv.gameserver.network.serverpackets.InventoryUpdate;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2mv.gameserver.network.serverpackets.PledgeStatusChanged;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.SkillList;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.utils.AutoBan;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.SiegeUtils;
import l2mv.gameserver.utils.Util;
import quests._234_FatesWhisper;

public class Donations extends Functions
{
	private static final String[] _vars = new String[]
	{
		"FOUNDATION",
		"ENCHANT",
		"ATTRIBUTION"
	};
	private static final String Active = "<font color=669900>You buy it!</font>";
	private static final String NotActive = "<font color=FF0000>Not buy!</font>";

	public void list(String[] arg)
	{
		final Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!arg[0].isEmpty() && Util.isNumber(arg[0]) && (arg.length <= 1 || arg[1].isEmpty() || Util.isNumber(arg[1])))
		{
			int id = Integer.parseInt(arg[0]);
			removeVars(player);
			NpcHtmlMessage html = new NpcHtmlMessage(0).setFile("scripts/services/Donate/index.htm");
			String template = HtmCache.getInstance().getNotNull("scripts/services/Donate/template.htm", player);
			String block = "";
			String list = "";
			List<Donation> _donate = DonationHolder.getInstance().getGroup(id);
			final int perpage = 6;
			int page = arg.length > 1 ? Integer.parseInt(arg[1]) : 1;
			int counter = 0;

			for (int i = (page - 1) * perpage; i < _donate.size(); i++)
			{
				Donation pack = _donate.get(i);
				block = template.replace("{bypass}", "bypass -h scripts_services.Donations:open " + pack.getId());
				block = block.replace("{name}", pack.getName());
				block = block.replace("{icon}", pack.getIcon());
				SimpleList simple = pack.getSimple();
				block = block.replace("{cost}", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, simple.getCount(), simple.getId()));
				list += block;
				counter++;
				if (counter >= perpage)
				{
					break;
				}
			}

			double count = Math.ceil((double) _donate.size() / (double) perpage); // Use rounding to obtain the last page with the remainder!
			int inline = 1;
			String navigation = "";

			for (int i = 1; i <= count; ++i)
			{
				if (i == page)
				{
					navigation = navigation + "<td width=25 align=center valign=top><button value=\"[" + i + "]\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				else
				{
					navigation = navigation + "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h scripts_services.Donations:list " + id + " " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
				}
				if (inline % 7 == 0)
				{
					navigation = navigation + "</tr><tr>";
				}
				inline++;
			}

			if (inline == 2)
			{
				navigation = "<td width=30 align=center valign=top>...</td>";
			}

			html.replace("%list%", list);
			html.replace("%navigation%", navigation);
			player.sendPacket(html);
		}
	}

	private void removeVars(Player player)
	{
		for (String var : _vars)
		{
			player.deleteQuickVar(var);
		}
		for (int i = 1; i <= 3; i++)
		{
			player.deleteQuickVar("att_" + i);
		}
	}

	public void open(String[] arg)
	{
		if (!Util.isNumber(arg[0]))
		{
			return;
		}

		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		int id = Integer.parseInt(arg[0]);
		Donation donate = DonationHolder.getInstance().getDonate(id);

		NpcHtmlMessage html = new NpcHtmlMessage(0).setFile("scripts/services/Donate/open.htm");
		String content = "";

		Map<Integer, Long> price = new HashMap<Integer, Long>();

		html.replace("%name%", donate.getName());
		html.replace("%icon%", donate.getIcon());
		html.replace("%id%", String.valueOf(donate.getId()));
		html.replace("%group%", String.valueOf(donate.getGroup()));

		SimpleList simple = donate.getSimple();
		html.replace("%cost%", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, simple.getCount(), simple.getId()));
		price.put(Integer.valueOf(simple.getId()), Long.valueOf(simple.getCount()));
		if (donate.haveFound())
		{
			boolean enchant = isVar(player, _vars[0]);
			FoundList found = donate.getFound();
			String block = HtmCache.getInstance().getNotNull("scripts/services/Donate/foundation.htm", player);

			block = block.replace("{bypass}", "bypass -h scripts_services.Donations:var " + _vars[0] + " " + (enchant ? 0 : 1) + " " + donate.getId());
			block = block.replace("{status}", enchant ? Active : NotActive);
			block = block.replace("{cost}", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, found.getCount(), found.getId()));
			block = block.replace("{action}", enchant ? "Cancel" : "Buy");
			if (enchant)
			{
				updatePrice(price, found.getId(), found.getCount());
			}

			content += block;
		}

		Enchant enchant = donate.getEnchant();
		if (enchant != null)
		{
			boolean is = isVar(player, _vars[1]);
			String block = HtmCache.getInstance().getNotNull("scripts/services/Donate/enchant.htm", player);
			block = block.replace("{bypass}", "bypass -h scripts_services.Donations:var " + _vars[1] + " " + (is ? 0 : 1) + " " + donate.getId());
			block = block.replace("{status}", is ? Active : NotActive);
			block = block.replace("{ench}", "+" + enchant.getEnchant());
			block = block.replace("{cost}", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, enchant.getCount(), enchant.getId()));
			block = block.replace("{action}", is ? "Cancel" : "Buy");
			if (is)
			{
				updatePrice(price, enchant.getId(), enchant.getCount());
			}

			content += block;
		}
		Attribution att = donate.getAttribution();
		if (att != null && att.getSize() >= 1)
		{
			boolean is = isVar(player, _vars[2]);
			if (is && checkAttVars(player, att.getSize()))
			{
				is = false;
				player.unsetVar(_vars[2]);
				var(new String[]
				{
					_vars[2],
					"0"
				});
			}

			String block = HtmCache.getInstance().getNotNull("scripts/services/Donate/attribute.htm", player);
			block = block.replace("{bypass}", is ? "bypass -h scripts_services.Donations:var " + _vars[2] + " " + 0 + " " + donate.getId() : "bypass -h scripts_services.Donations:attribute " + donate.getId());
			block = block.replace("{status}", is ? Active : NotActive);
			block = block.replace("{cost}", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, att.getCount(), att.getId()));
			block = block.replace("{action}", is ? "Cancel" : "Buy");
			if (is)
			{
				updatePrice(price, att.getId(), att.getCount());
			}

			content += block;
		}

		String total = "";

		for (Entry<Integer, Long> map : price.entrySet())
		{
			total += Util.formatPay(player, map.getValue(), map.getKey()) + "<br1>";
		}

		html.replace("%content%", content);
		html.replace("%total%", total);

		player.sendPacket(html);
	}

	private boolean checkAttVars(Player player, int size)
	{
		int count = 0;

		for (int i = 1; i <= 3; i++)
		{
			int var = player.getQuickVarI("att_" + i, -1);
			if (var != -1)
			{
				count++;
			}
		}
		return count != size;
	}

	private void updatePrice(Map<Integer, Long> price, int id, long count)
	{
		if (price.containsKey(id))
		{
			price.put(id, count + price.get(id));
		}
		else
		{
			price.put(id, count);
		}
	}

	public void var(String[] arg)
	{
		if (arg.length < 3)
		{
			return;
		}

		Player player = getSelf();
		if (!Util.isNumber(arg[1]) || !Util.isNumber(arg[2]))
		{
			return;
		}

		int action = Integer.parseInt(arg[1]);
		String var = arg[0];
		player.addQuickVar(var, action);

		if (action == 0)
		{
			player.deleteQuickVar(var);
			if (var.equals(_vars[2]))
			{
				for (int i = 1; i <= 3; i++)
				{
					player.deleteQuickVar("att_" + i);
				}
			}
		}

		open(new String[]
		{
			arg[2]
		});
	}

	public void attribute(String[] arg)
	{
		if ((arg.length < 1) || !Util.isNumber(arg[0]))
		{
			return;
		}

		Player player = getSelf();
		int id = Integer.parseInt(arg[0]);
		Donation donate = DonationHolder.getInstance().getDonate(id);
		if (donate == null)
		{
			return;
		}

		Attribution atribute = donate.getAttribution();

		if (atribute == null)
		{
			return;
		}

		if (atribute.getSize() < 1)
		{
			open(arg);
			return;
		}

		NpcHtmlMessage html = new NpcHtmlMessage(0).setFile("scripts/services/Donate/attribute_choice.htm");
		html.replace("%name%", donate.getName());
		html.replace("%icon%", donate.getIcon());
		html.replace("%bypass%", "bypass -h scripts_services.Donations:open " + donate.getId());
		html.replace("%value%", String.valueOf(atribute.getValue()));
		html.replace("%size%", String.valueOf(atribute.getSize()));
		html.replace("%id%", String.valueOf(donate.getId()));

		int att_1 = player.getQuickVarI("att_1", -1);
		int att_2 = player.getQuickVarI("att_2", -1);
		int att_3 = player.getQuickVarI("att_3", -1);
		html.replace("%att_1%", atribute.getSize() >= 1 ? (att_1 == -1 ? "..." : elementName(att_1)) : "<font color=FF0000>Slot is block</font>");
		html.replace("%att_2%", atribute.getSize() >= 2 ? (att_2 == -1 ? "..." : elementName(att_2)) : "<font color=FF0000>Slot is block</font>");
		html.replace("%att_3%", atribute.getSize() == 3 ? (att_3 == -1 ? "..." : elementName(att_3)) : "<font color=FF0000>Slot is block</font>");

		build(player, html, donate, att_1, att_2, att_3);
		player.sendPacket(html);
	}

	private String elementName(int id)
	{
		String name = "";
		switch (id)
		{
		case 0:
			name = "Attributes Fire";
			break;
		case 1:
			name = "Attributes Water";
			break;
		case 2:
			name = "Attributes Wind";
			break;
		case 3:
			name = "Attributes Earth";
			break;
		case 4:
			name = "Attributes Holy";
			break;
		case 5:
			name = "Attributes Unholy";
			break;
		default:
			name = "NONE";
			break;
		}

		return name;
	}

	private String button(int att, int id)
	{
		return "<button action=\"bypass -h scripts_services.Donations:put " + id + " " + att + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
	}

	public void put(String[] arg)
	{
		if ((arg.length < 2) || !Util.isNumber(arg[0]) || !Util.isNumber(arg[1]))
		{
			return;
		}

		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		int att = Integer.parseInt(arg[1]);
		if (player.getQuickVarI("att_1", -1) == -1)
		{
			player.addQuickVar("att_1", att);
		}
		else if (player.getQuickVarI("att_2", -1) == -1)
		{
			player.addQuickVar("att_2", att);
		}
		else if (player.getQuickVarI("att_3", -1) == -1)
		{
			player.addQuickVar("att_3", att);
		}

		player.addQuickVar(_vars[2], 1);

		attribute(arg);
	}

	public void clear_att(String[] arg)
	{
		if ((arg.length < 1) || !Util.isNumber(arg[0]))
		{
			return;
		}

		Player player = getSelf();
		for (int i = 1; i <= 3; i++)
		{
			player.deleteQuickVar("att_" + i);
		}

		attribute(arg);
	}

	private NpcHtmlMessage build(Player player, NpcHtmlMessage html, Donation donate, int att_1, int att_2, int att_3)
	{
		String slotclose = "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">";
		int id = donate.getId();
		int size = donate.getAttribution().getSize();
		boolean block = false;
		if (size == 1 && (att_1 != -1 || att_2 != -1 || att_3 != -1))
		{
			block = true;
		}
		else if (size == 2 && (att_1 != -1 || att_2 != -1) && (att_1 != -1 || att_3 != -1) && (att_2 != -1 || att_3 != -1))
		{
			block = true;
		}
		else if (size == 3 && att_1 != -1 && att_2 != -1 && att_3 != -1)
		{
			block = true;
		}

		boolean one = block(player, 0, 1) || block;
		String fire = one ? slotclose : button(0, id);
		String water = one ? slotclose : button(1, id);
		boolean two = block(player, 2, 3) || block;
		String wind = two ? slotclose : button(2, id);
		String earth = two ? slotclose : button(3, id);
		boolean three = block(player, 4, 5) || block;
		String holy = three ? slotclose : button(4, id);
		String unholy = three ? slotclose : button(5, id);
		html.replace("%fire%", fire);
		html.replace("%water%", water);
		html.replace("%wind%", wind);
		html.replace("%earth%", earth);
		html.replace("%holy%", holy);
		html.replace("%unholy%", unholy);

		return html;
	}

	private boolean block(Player player, int id, int id2)
	{
		for (int i = 1; i <= 3; i++)
		{
			int var = player.getQuickVarI("att_" + i, -1);
			if (var == id || var == id2)
			{
				return true;
			}
		}

		return false;
	}

	private boolean isVar(Player player, String var)
	{
		return player.getQuickVarI(var, 0) != 0;
	}

	public void buy(String[] arg)
	{
		if ((arg.length < 1) || !Util.isNumber(arg[0]))
		{
			return;
		}

		Player player = getSelf();
		int id = Integer.parseInt(arg[0]);
		Donation donate = DonationHolder.getInstance().getDonate(id);
		if (donate == null)
		{
			return;
		}

		Map<Integer, Long> price = new HashMap<Integer, Long>();
		SimpleList simple = donate.getSimple();

		price.put(simple.getId(), simple.getCount());

		FoundList foundation = donate.getFound();
		boolean found_list = donate.haveFound() && foundation != null && player.getQuickVarI(_vars[0], -1) != -1;
		if (found_list)
		{
			updatePrice(price, foundation.getId(), foundation.getCount());
		}

		Enchant enchant = donate.getEnchant();
		boolean enchanted = enchant != null && player.getQuickVarI(_vars[1], -1) != -1;
		if (enchanted)
		{
			updatePrice(price, enchant.getId(), enchant.getCount());
		}

		Attribution att = donate.getAttribution();
		boolean attribution = att != null && player.getQuickVarI(_vars[2], -1) != -1;
		if (attribution)
		{
			updatePrice(price, att.getId(), att.getCount());
		}

		// player.getInventory().checkDoubleItems();

		for (Entry<Integer, Long> map : price.entrySet())
		{
			int _id = map.getKey();
			long _count = map.getValue();

			if (ItemFunctions.getItemCount(player, map.getKey()) < map.getValue())
			{
				player.sendMessage("It is not enough " + Util.formatPay(player, _count, _id));
				open(arg);
				return;
			}
		}

		// List<ItemActionLog> logs = new ArrayList<>(price.size() + (found_list ? foundation.getList().size() : simple.getList().size()));
		for (Entry<Integer, Long> map : price.entrySet())
		{
			int _id = map.getKey();
			long _count = map.getValue();
			player.sendMessage("Disappeared: " + Util.formatPay(player, _count, _id));
			ItemInstance itemToRemove = player.getInventory().getItemByItemId(_id);
			// logs.add(new ItemActionLog(ItemStateLog.EXCHANGE_LOSE, "DonateEquipment", player, itemToRemove, _count));
			player.getInventory().destroyItem(itemToRemove, _count, null);
		}

		for (DonateItem _donate : (found_list ? foundation.getList() : simple.getList()))
		{
			ItemInstance item = player.getInventory().addItem(_donate.getId(), _donate.getCount(), null);

			int enchant_level = 0;
			if (enchanted)
			{
				enchant_level = enchant.getEnchant();
			}
			else if (_donate.getEnchant() > 0)
			{
				enchant_level = _donate.getEnchant();
			}

			if (enchant_level > 0 && item.canBeEnchanted(false))
			{
				item.setEnchantLevel(enchant_level);
			}

			if ((item.isArmor() || item.isWeapon()) && attribution) // Add all elements
			{
				for (int i = 1; i <= att.getSize(); i++)
				{
					int element_id = player.getQuickVarI("att_" + i, -1);
					if (element_id != -1)
					{
						Element element = Element.getElementById(element_id);

						if (item.isArmor()) // If is armor need reverse element Water to Fire and etc..
						{
							element = Element.getReverseElement(element);
						}
						item.setAttributeElement(element, att.getValue());
					}
				}
			}

			item.setJdbcState(JdbcEntityState.UPDATED);
			item.update();

			if (item.isEquipable() && ItemFunctions.checkIfCanEquip(player, item) == null)
			{
				player.getInventory().equipItem(item);
			}

			player.sendPacket(new InventoryUpdate().addModifiedItem(item));
			player.broadcastCharInfo();

			// logs.add(new ItemActionLog(ItemStateLog.EXCHANGE_GAIN, "DonateEquipment", player, item, _donate.getCount()));
		}

		// Log.logItemActions(logs);

		removeVars(player);
		player.sendMessage("You buy: " + donate.getName());
	}

	// Synerge - Function to buy 255 recommends
	public void buy_recommends()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_BUY_RECOMMENDS_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (player.getRecomHave() == 255)
		{
			player.sendMessage("You already have the maximum amount of recs!");
			return;
		}
		if (getItemCount(player, Config.SERVICES_BUY_RECOMMENDS_ITEM) < Config.SERVICES_BUY_RECOMMENDS_PRICE)
		{
			if (Config.SERVICES_BUY_RECOMMENDS_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendMessage("You don't have " + Config.SERVICES_BUY_RECOMMENDS_PRICE + " Donator Coins!");
			}
			return;
		}
		removeItem(player, Config.SERVICES_BUY_RECOMMENDS_ITEM, Config.SERVICES_BUY_RECOMMENDS_PRICE, "Donations$buy_recommends");
		player.setRecomHave(255);
		player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Donation", "You bought 255 recommendations!"));
	}

	// Synerge - Function to buy 40k of clan reputation
	public void buy_clan_reputation()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_BUY_CLAN_REPUTATION_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (player.getClan() == null)
		{
			player.sendMessage("You dont have any clan!");
			return;
		}
		if (player.getClan().getLevel() < 5)
		{
			player.sendMessage("Your clan must be lvl 5 or higher");
			return;
		}
		if (player.getEvent(SiegeEvent.class) != null)
		{
			player.sendMessage("Your clan is on siege right now!");
			return;
		}
		if (getItemCount(player, Config.SERVICES_BUY_CLAN_REPUTATION_ITEM) < Config.SERVICES_BUY_CLAN_REPUTATION_PRICE)
		{
			if (Config.SERVICES_BUY_CLAN_REPUTATION_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendMessage("You don't have " + Config.SERVICES_BUY_CLAN_REPUTATION_PRICE + " Donator Coins!");
			}
			return;
		}
		removeItem(player, Config.SERVICES_BUY_CLAN_REPUTATION_ITEM, Config.SERVICES_BUY_CLAN_REPUTATION_PRICE, "Donations$buy_clan_reputation");
		player.getClan().incReputation(Config.SERVICES_BUY_CLAN_REPUTATION_COUNT);
		player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Donation", "You bought " + Config.SERVICES_BUY_CLAN_REPUTATION_COUNT + " Reputation for your clan!"));
	}

	// Synerge - Function to buy 15k fame
	public void buy_fame()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_BUY_FAME_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if (getItemCount(player, Config.SERVICES_BUY_FAME_ITEM) < Config.SERVICES_BUY_FAME_PRICE)
		{
			if (Config.SERVICES_BUY_FAME_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendMessage("You don't have " + Config.SERVICES_BUY_FAME_PRICE + " Donator Coins!");
			}
			return;
		}
		removeItem(player, Config.SERVICES_BUY_FAME_ITEM, Config.SERVICES_BUY_FAME_PRICE, "Donations$buy_fame");
		player.setFame(player.getFame() + Config.SERVICES_BUY_FAME_COUNT);
		player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Donation", "You bought " + Config.SERVICES_BUY_FAME_COUNT + " Fame"));
	}

	// Synerge - Opens the Olf Shirt Store page
	public void olf_shirt_page()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_OLF_STORE_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		player.sendPacket(new NpcHtmlMessage(0).setFile("scripts/services/communityPVP/pages/Donate/OlfStore.htm"));
	}

	// Synerge - Function to buy Olf Shirt +X
	public void buy_olf_shirt(String[] arg)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_OLF_STORE_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if ((arg.length < 1) || !Util.isNumber(arg[0]))
		{
			return;
		}
		final int enchant = Integer.parseInt(arg[0]);
		final int price;
		switch (enchant)
		{
		case 0:
			price = Config.SERVICES_OLF_STORE_0_PRICE;
			break;
		case 6:
			price = Config.SERVICES_OLF_STORE_6_PRICE;
			break;
		case 7:
			price = Config.SERVICES_OLF_STORE_7_PRICE;
			break;
		case 8:
			price = Config.SERVICES_OLF_STORE_8_PRICE;
			break;
		case 9:
			price = Config.SERVICES_OLF_STORE_9_PRICE;
			break;
		case 10:
			price = Config.SERVICES_OLF_STORE_10_PRICE;
			break;
		default:
			return;
		}
		if (getItemCount(player, Config.SERVICES_OLF_STORE_ITEM) < price)
		{
			if (Config.SERVICES_OLF_STORE_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
			}
			return;
		}
		removeItem(player, Config.SERVICES_OLF_STORE_ITEM, price, "Donations$buy_olf_shirt");

		final int OLF_SHIRT_ITEM_ID = 21580; // Olf's TShirt ID - Scroll of Enchant Olf ID : 21582
		ItemInstance item = ItemFunctions.createItem(OLF_SHIRT_ITEM_ID);
		item.setEnchantLevel(enchant);
		player.getInventory().addItem(item, "OlfShirtStore");
		player.sendPacket(SystemMessage2.obtainItems(OLF_SHIRT_ITEM_ID, 1, enchant));

		// Unequip the current player's armor slot
		player.getInventory().unEquipItemInBodySlot(item.getBodyPart());

		// Equip the new item
		player.getInventory().equipItem(item);

		player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Donation", "You bought an " + item.getName()));
	}

	// Synerge - Opens the Clan Level page
	public void clan_level_page()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!Config.SERVICES_CLAN_LEVEL_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		player.sendPacket(new NpcHtmlMessage(0).setFile("scripts/services/communityPVP/pages/Donate/ClanLevel.htm"));
	}

	public void buy_nobless()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (!Config.DONATE_NOBLESS_ENABLE)
		{
			player.sendMessage("Donate for nobless is disabled");
			return;
		}
		if (player.isNoble())
		{
			player.sendMessage("You are already Nobless!");
			return;
		}
		if (!Config.SERVICES_NOBLESS_SELL_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if ((player.getLevel() < 75) && (player.getActiveClass().isBase()))
		{
			player.sendMessage("You need to be over 75 level to purchase noblesse!");
			return;
		}
		if (player.getSubClasses().size() < 2)
		{
			player.sendMessage("You need to have a subclass to purchase noblesse!");
			return;
		}

		if (player.getInventory().destroyItemByItemId(Config.DONATE_NOBLESS_SELL_ITEM, Config.DONATE_NOBLESS_SELL_PRICE, "Donate_NoblessSell"))
		{
			makeSubQuests();
			becomeNoble();
		}
		else
		{
			player.sendMessage("You need " + Config.DONATE_NOBLESS_SELL_PRICE + " " + Util.getItemName(Config.DONATE_NOBLESS_SELL_ITEM));
		}
	}

	// Synerge - Function to change the clan lvl
	public void change_clan_level(String[] arg)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CLAN_LEVEL_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if ((arg.length < 1) || !Util.isNumber(arg[0]))
		{
			return;
		}
		final int lvl = Integer.parseInt(arg[0]);
		final Clan clan = player.getClan();
		if (clan == null)
		{
			player.sendMessage("You dont have any clan!");
			return;
		}
		if (clan.getLevel() >= lvl)
		{
			player.sendMessage("Your clan is already higher than the selected level");
			return;
		}
		if (!player.isClanLeader())
		{
			player.sendMessage("Only the clan leader can change the level of the clan");
			return;
		}
		if (player.getEvent(SiegeEvent.class) != null)
		{
			player.sendMessage("Your clan is on siege right now!");
			return;
		}
		final int price;
		switch (lvl)
		{
		case 8:
			price = Config.SERVICES_CLAN_LEVEL_8_PRICE;
			break;
		case 9:
			price = Config.SERVICES_CLAN_LEVEL_9_PRICE;
			break;
		case 10:
			price = Config.SERVICES_CLAN_LEVEL_10_PRICE;
			break;
		case 11:
			price = Config.SERVICES_CLAN_LEVEL_11_PRICE;
			break;
		default:
			return;
		}
		if (getItemCount(player, Config.SERVICES_CLAN_LEVEL_ITEM) < price)
		{
			if (Config.SERVICES_CLAN_LEVEL_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendMessage("You don't have enough Donator Coins!");
			}
			return;
		}
		removeItem(player, Config.SERVICES_CLAN_LEVEL_ITEM, price, "Donations$change_clan_level");
		// Change the clan lvl
		clan.setLevel(lvl);
		clan.updateClanInDB();
		player.broadcastCharInfo();
		if (clan.getLevel() >= 4)
		{
			SiegeUtils.addSiegeSkills(player);
		}
		if (clan.getLevel() == 5)
		{
			player.sendPacket(Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
		}
		// notify all the members about it
		PledgeShowInfoUpdate pu = new PledgeShowInfoUpdate(clan);
		PledgeStatusChanged ps = new PledgeStatusChanged(clan);
		for (UnitMember mbr : clan)
		{
			if (mbr.isOnline())
			{
				mbr.getPlayer().updatePledgeClass();
				mbr.getPlayer().sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, pu, ps);
				mbr.getPlayer().broadcastCharInfo();
			}
		}
		player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Donation", "You changed your clan's level to " + lvl));
	}

	// Synerge - Opens the Clan Skills page
	public void clan_skills_page()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CLAN_SKILLS_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		player.sendPacket(new NpcHtmlMessage(0).setFile("scripts/services/communityPVP/pages/Donate/ClanSkills.htm"));
	}

	// Synerge - Function to give clan skills
	public void give_clan_skills(String[] arg)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CLAN_SKILLS_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		if ((arg.length < 1) || !Util.isNumber(arg[0]))
		{
			return;
		}
		final int lvl = Integer.parseInt(arg[0]);
		final Clan clan = player.getClan();
		if (clan == null)
		{
			player.sendMessage("You don't have any clan!");
			return;
		}
		if (!player.isClanLeader())
		{
			player.sendMessage("Only the clan leader can change the level of the clan");
			return;
		}
		if (clan.getLevel() < lvl)
		{
			player.sendMessage("Your clan needs to be lvl " + lvl + " in order to adcquire this skills");
			return;
		}
		if (player.getEvent(SiegeEvent.class) != null)
		{
			player.sendMessage("Your clan is on siege right now!");
			return;
		}
		final int price;
		switch (lvl)
		{
		case 8:
			price = Config.SERVICES_CLAN_SKILLS_8_PRICE;
			break;
		case 9:
			price = Config.SERVICES_CLAN_SKILLS_9_PRICE;
			break;
		case 10:
			price = Config.SERVICES_CLAN_SKILLS_10_PRICE;
			break;
		case 11:
			price = Config.SERVICES_CLAN_SKILLS_11_PRICE;
			break;
		default:
			return;
		}
		if (getItemCount(player, Config.SERVICES_CLAN_SKILLS_ITEM) < price)
		{
			if (Config.SERVICES_CLAN_SKILLS_ITEM == 57)
			{
				player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
			}
			else
			{
				player.sendMessage("You don't have enough Donator Coins!");
			}
			return;
		}
		removeItem(player, Config.SERVICES_CLAN_SKILLS_ITEM, price, "Donations$change_clan_level");
		// Give clan skills
		Skill skill = null;
		for (int i = 0; i < 10; i++) // Lazy hack to give clan skills at max level for the specific clan level.
		{
			Collection<SkillLearn> clanSkills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.CLAN, null, lvl);
			for (SkillLearn sl : clanSkills)
			{
				skill = SkillTable.getInstance().getInfo(sl.getId(), sl.getLevel());
				clan.addSkill(skill, true);
			}
		}
		clan.broadcastSkillListToOnlineMembers();
		player.sendPacket(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Donation", "You gave your clan all skills of lvl " + lvl));
	}

	public void olf(String[] arg)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		int i = player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_UNDER);
		int j = 21580;
		if (i != j)
		{
			player.sendMessage("Olf's T-shirt must be equiped!");
			return;
		}
		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/transfer/olf.htm");
		localNpcHtmlMessage.replace("%price%", Util.formatPay(player, Config.SERVICES_OLF_TRANSFER_ITEM[1], Config.SERVICES_OLF_TRANSFER_ITEM[0]));
		player.sendPacket(localNpcHtmlMessage);
	}

	public void olf_send()
	{
		// Special Case
		olf_send(new String[]
		{
			""
		});
	}

	public void olf_send(String[] arg)
	{
		if (arg.length != 1)
		{
			return;
		}
		Player player = getSelf();
		int itemId = 21580;
		ItemInstance currentItem = player.getInventory().getPaperdollItem(0);
		if ((currentItem == null) || (currentItem.getItemId() != itemId))
		{
			player.sendMessage("Olf's T-shirt must be equiped!");
			return;
		}
		String str = arg[0];
		Player localPlayer2 = GameObjectsStorage.getPlayer(str);
		if (localPlayer2 == null)
		{
			player.sendMessage("Can't find player " + str + " in game!");
			return;
		}
		if ((str.isEmpty() || Util.getPay(player, Config.SERVICES_OLF_TRANSFER_ITEM[0], Config.SERVICES_OLF_TRANSFER_ITEM[1], true)) && (player.getInventory().destroyItemByObjectId(currentItem.getObjectId(), currentItem.getCount(), "Services: Olf Transfer!")))
		{
			PcInventory localPcInventory = localPlayer2.getInventory();
			ItemInstance newItem = ItemFunctions.createItem(itemId);
			newItem.setEnchantLevel(currentItem.getEnchantLevel());
			localPcInventory.addItem(newItem, "Services: Olf Transfer!");
			newItem.setJdbcState(JdbcEntityState.UPDATED);
			newItem.update();
			if (ItemFunctions.checkIfCanEquip(player, newItem) == null)
			{
				localPcInventory.equipItem(newItem);
			}
			player.sendMessage("You transfer Olf's T-shirt to player " + localPlayer2.getName());
			localPlayer2.sendMessage("Player " + player.getName() + " send you Olf's T-shirt. (Is Automatically equiped)");
		}
	}

	public void cloak(String[] arg)
	{
		Player player = getSelf();
		int i = player.getInventory().getPaperdollItemId(13);
		if (!isValidCloak(i))
		{
			player.sendMessage("Soul cloak must be equiped!");
			return;
		}
		NpcHtmlMessage html = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/transfer/cloaks.htm");
		html.replace("%price%", Util.formatPay(player, Config.SERVICES_SOUL_CLOAK_TRANSFER_ITEM[1], Config.SERVICES_SOUL_CLOAK_TRANSFER_ITEM[0]));
		player.sendPacket(html);
	}

	public void cloak_send()
	{
		// Special Case
		cloak_send(new String[]
		{
			""
		});
	}

	public void cloak_send(String[] args)
	{
		if (args.length != 1)
		{
			return;
		}
		Player sender = getSelf();
		ItemInstance currentItem = sender.getInventory().getPaperdollItem(13);
		if (!isValidCloak(currentItem.getItemId()))
		{
			sender.sendMessage("Soul cloak must be equiped!");
			return;
		}
		String str = args[0];
		Player reciver = GameObjectsStorage.getPlayer(str);
		if (reciver == null)
		{
			sender.sendMessage("Can't find player " + str + " in game!");
			return;
		}
		if (Util.getPay(sender, Config.SERVICES_SOUL_CLOAK_TRANSFER_ITEM[0], Config.SERVICES_SOUL_CLOAK_TRANSFER_ITEM[1], true))
		{
			int i = currentItem.getItemId();
			if (sender.getInventory().destroyItemByObjectId(currentItem.getObjectId(), currentItem.getCount(), "Services: Soul Cloak Transfer!"))
			{
				PcInventory inventory = reciver.getInventory();
				ItemInstance newItem = ItemFunctions.createItem(i);
				newItem.setEnchantLevel(currentItem.getEnchantLevel());
				inventory.addItem(newItem, "Services: Soul Cloak Transfer!");
				newItem.setJdbcState(JdbcEntityState.UPDATED);
				newItem.update();
				sender.sendMessage("You transfer " + newItem.getName() + " to player " + reciver.getName());
				if (ItemFunctions.checkIfCanEquip(reciver, newItem) == null)
				{
					inventory.equipItem(newItem);
					reciver.sendMessage("Player " + sender.getName() + " send you " + newItem.getName() + ". (Is Automatically equiped)");
				}
				else
				{
					reciver.sendMessage("Player " + sender.getName() + " send you " + newItem.getName() + ". (Is in inventory)");
				}
			}
		}
	}

	private boolean isValidCloak(int paramInt)
	{
		int[] arrayOfInt1 =
		{
			21719,
			21720,
			21721
		};
		for (int k : arrayOfInt1)
		{
			if (paramInt == k)
			{
				return true;
			}
		}
		return false;
	}

	public void unban()
	{
		Player localPlayer = getSelf();
		NpcHtmlMessage localNpcHtmlMessage = new NpcHtmlMessage(0).setFile("scripts/services/DonateFunction/unban/index.htm");
		localNpcHtmlMessage.replace("%price%", Util.formatPay(localPlayer, Config.SERVICES_UNBAN_ITEM[1], Config.SERVICES_UNBAN_ITEM[0]));
		localPlayer.sendPacket(localNpcHtmlMessage);
	}

	public void unban(String[] params)
	{
		if ((params != null) && (!params[0].isEmpty()))
		{
			Player localPlayer = getSelf();
			if ((Util.getPay(localPlayer, Config.SERVICES_UNBAN_ITEM[0], Config.SERVICES_UNBAN_ITEM[1], true)) && (!unbanChar(params[0])))
			{
				ItemFunctions.addItem(localPlayer, Config.SERVICES_UNBAN_ITEM[0], Config.SERVICES_UNBAN_ITEM[1], true, "Donate System: Unban refund");
				localPlayer.sendMessage("Can't find account or account and character is not is ban!");
			}
		}
	}

	public void makeSubQuests()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		Quest q = QuestManager.getQuest(_234_FatesWhisper.class);
		QuestState qs = player.getQuestState(q.getClass());

		if (qs != null)
		{
			qs.exitCurrentQuest(true);
		}
		q.newQuestState(player, Quest.COMPLETED);

		if (player.getRace() == Race.kamael)
		{
			q = QuestManager.getQuest("_236_SeedsOfChaos");
			qs = player.getQuestState(q.getClass());
			if (qs != null)
			{
				qs.exitCurrentQuest(true);
			}
			q.newQuestState(player, Quest.COMPLETED);
		}
		else
		{
			q = QuestManager.getQuest("_235_MimirsElixir");
			qs = player.getQuestState(q.getClass());
			if (qs != null)
			{
				qs.exitCurrentQuest(true);
			}
			q.newQuestState(player, Quest.COMPLETED);
		}
	}

	public void becomeNoble()
	{
		Player player = getSelf();
		if (player == null || player.isNoble())
		{
			return;
		}

		Olympiad.addNoble(player);
		player.setNoble(true);
		player.updatePledgeClass();
		player.updateNobleSkills();
		player.sendPacket(new SkillList(player));
		player.getInventory().addItem(7694, 1L, "nobleTiara");
		player.sendMessage("Congratulations! You gained noblesse rank.");
		player.broadcastUserInfo(true);
		player.broadcastPacket(new L2GameServerPacket[]
		{
			new MagicSkillUse(player, player, 6696, 1, 1000, 0)
		});
	}

	private boolean unbanChar(String accountName)
	{
		boolean bool = false;
		Player player = getSelf();

		try (Connection con = DatabaseFactory.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement("SELECT `obj_id`, `accesslevel`, `char_name` FROM `characters` WHERE `account_name` = ?"))
		{
			ps.setString(1, accountName);
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					int accessLvl = rs.getInt("accesslevel");
					int playerId = rs.getInt("obj_id");
					String charName = rs.getString("char_name");
					player.sendMessage("Find character: " + charName + ", Access: " + accessLvl);
					if (accessLvl < 0)
					{
						PreparedStatement ps2 = con.prepareStatement("UPDATE `characters` SET `accesslevel`='0' WHERE `obj_Id` = ?");
						ps2.setInt(1, playerId);
						ps2.execute();
						ps2.close();
						player.sendMessage("Unban engine: Character " + charName + " has been unbanned success!");
					}
					if (AutoBan.checkIsBanned(playerId))
					{
						AutoBan.unBaned(playerId);
						player.sendMessage("Unban engine: Character " + charName + " has remove from bans table!");
					}

					// Also unbanchat and remove from jail
					AutoBan.RemoveFromJail(charName, "Donation");

					AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(accountName, 0, 0));
					player.sendMessage("Unban engine: Account " + accountName + " has been unbanned!");
					bool = true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bool;
	}
}
