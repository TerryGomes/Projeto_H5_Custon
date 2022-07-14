package services.petevolve;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.Config;
import l2mv.gameserver.cache.Msg;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.InventoryUpdate;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.PetDataTable;
import l2mv.gameserver.tables.PetDataTable.L2Pet;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.Util;

public class exchange extends Functions
{
	/** Билеты для обмена **/
	private static final int PEticketB = 7583;
	private static final int PEticketC = 7584;
	private static final int PEticketK = 7585;

	/** Дудки для вызова петов **/
	private static final int BbuffaloP = 6648;
	private static final int BcougarC = 6649;
	private static final int BkookaburraO = 6650;

	public void exch_1()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (getItemCount(player, PEticketB) >= 1)
		{
			removeItem(player, PEticketB, 1, "Exchange$exch_1");
			addItem(player, BbuffaloP, 1, "Exchange$exch_1");
			return;
		}
		show("scripts/services/petevolve/exchange_no.htm", player);
	}

	public void exch_2()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (getItemCount(player, PEticketC) >= 1)
		{
			removeItem(player, PEticketC, 1, "Exchange$exch_2");
			addItem(player, BcougarC, 1, "Exchange$exch_2");
			return;
		}
		show("scripts/services/petevolve/exchange_no.htm", player);
	}

	public void exch_3()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (getItemCount(player, PEticketK) >= 1)
		{
			removeItem(player, PEticketK, 1, "Exchange$exch_3");
			addItem(player, BkookaburraO, 1, "Exchange$exch_3");
			return;
		}
		show("scripts/services/petevolve/exchange_no.htm", player);
	}

	public void showBabyPetExchange()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_EXCHANGE_BABY_PET_ITEM);
		String out = "";
		out += "<html><body>You can at any time to exchange your Improved Baby pet to a different type without losing experience. Pet should be summoned.";
		out += "<br>Cost: " + Util.formatAdena(Config.SERVICES_EXCHANGE_BABY_PET_PRICE) + " " + item.getName();
		out += "<br><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.petevolve.exchange:exToCougar\" value=\"Exchanged for Improved Cougar\">";
		out += "<br1><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.petevolve.exchange:exToBuffalo\" value=\"Exchanged for Improved Buffalo\">";
		out += "<br1><button width=250 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.petevolve.exchange:exToKookaburra\" value=\"Exchanged for Improved Kookaburra\">";
		out += "</body></html>";
		show(out, player);
	}

	public void showErasePetName()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_PET_NAME_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.SERVICES_CHANGE_PET_NAME_ITEM);
		String out = "";
		out += "<html><body>You can clear the name of a pet, in order to appoint a new one. Peter thus should be called.";
		out += "<br>Zero cost: " + Util.formatAdena(Config.SERVICES_CHANGE_PET_NAME_PRICE) + " " + item.getName();
		out += "<br><button width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h scripts_services.petevolve.exchange:erasePetName\" value=\"Zero the name\">";
		out += "</body></html>";
		show(out, player);
	}

	public void erasePetName()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_CHANGE_PET_NAME_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		Summon pl_pet = player.getPet();
		if (pl_pet == null || !pl_pet.isPet())
		{
			show("The pet must be called.", player);
			return;
		}
		if (player.getInventory().destroyItemByItemId(Config.SERVICES_CHANGE_PET_NAME_ITEM, Config.SERVICES_CHANGE_PET_NAME_PRICE, "Erasing Pet Name"))
		{
			pl_pet.setName(pl_pet.getTemplate().name);
			pl_pet.broadcastCharInfo();

			PetInstance _pet = (PetInstance) pl_pet;
			ItemInstance control = _pet.getControlItem();
			if (control != null)
			{
				control.setCustomType2(1);
				control.setJdbcState(JdbcEntityState.UPDATED);
				control.update();
				player.sendPacket(new InventoryUpdate().addModifiedItem(control));
			}
			show("Name erased.", player);
		}
		else if (Config.SERVICES_CHANGE_PET_NAME_ITEM == 57)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		}
	}

	public void exToCougar()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		Summon pl_pet = player.getPet();
		if (pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID))
		{
			show("Pet must be called.", player);
			return;
		}
		if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXCHANGE_BABY_PET_ITEM, Config.SERVICES_EXCHANGE_BABY_PET_PRICE, "exToCougar"))
		{
			ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
			control.setItemId(L2Pet.IMPROVED_BABY_COUGAR.getControlItemId());
			control.setJdbcState(JdbcEntityState.UPDATED);
			control.update();
			player.sendPacket(new InventoryUpdate().addModifiedItem(control));
			player.getPet().unSummon();
			show("Peter changed.", player);
		}
		else if (Config.SERVICES_EXCHANGE_BABY_PET_ITEM == 57)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		}
	}

	public void exToBuffalo()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		Summon pl_pet = player.getPet();
		if (pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_COUGAR_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID))
		{
			show("Pet must be called.", player);
			return;
		}
		if (Config.ALT_IMPROVED_PETS_LIMITED_USE && player.isMageClass())
		{
			show("This pet only for soldiers.", player);
			return;
		}
		if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXCHANGE_BABY_PET_ITEM, Config.SERVICES_EXCHANGE_BABY_PET_PRICE, "exToBuffalo"))
		{
			ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
			control.setItemId(L2Pet.IMPROVED_BABY_BUFFALO.getControlItemId());
			control.setJdbcState(JdbcEntityState.UPDATED);
			control.update();
			player.sendPacket(new InventoryUpdate().addModifiedItem(control));
			player.getPet().unSummon();
			show("Pet changed.", player);
		}
		else if (Config.SERVICES_EXCHANGE_BABY_PET_ITEM == 57)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		}
	}

	public void exToKookaburra()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}
		if (!Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			show("Service is disabled.", player);
			return;
		}
		Summon pl_pet = player.getPet();
		if (pl_pet == null || pl_pet.isDead() || !(pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_BUFFALO_ID || pl_pet.getNpcId() == PetDataTable.IMPROVED_BABY_COUGAR_ID))
		{
			show("Pet must be called.", player);
			return;
		}
		if (Config.ALT_IMPROVED_PETS_LIMITED_USE && !player.isMageClass())
		{
			show("This pet only for mages.", player);
			return;
		}
		if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXCHANGE_BABY_PET_ITEM, Config.SERVICES_EXCHANGE_BABY_PET_PRICE, "exToKookaburra"))
		{
			ItemInstance control = player.getInventory().getItemByObjectId(player.getPet().getControlItemObjId());
			control.setItemId(L2Pet.IMPROVED_BABY_KOOKABURRA.getControlItemId());
			control.setJdbcState(JdbcEntityState.UPDATED);
			control.update();
			player.sendPacket(new InventoryUpdate().addModifiedItem(control));
			player.getPet().unSummon();
			show("Peter changed.", player);
		}
		else if (Config.SERVICES_EXCHANGE_BABY_PET_ITEM == 57)
		{
			player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
		}
		else
		{
			player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
		}
	}

	public static String DialogAppend_30731(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30827(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30828(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30829(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30830(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30831(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_30869(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_31067(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_31265(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_31309(Integer val)
	{
		return getHtmlAppends(val);
	}

	public static String DialogAppend_31954(Integer val)
	{
		return getHtmlAppends(val);
	}

	private static String getHtmlAppends(Integer val)
	{
		String ret = "";
		if (val != 0)
		{
			return ret;
		}
		if (Config.SERVICES_CHANGE_PET_NAME_ENABLED)
		{
			ret = "<br>[scripts_services.petevolve.exchange:showErasePetName|Reset a pet name]";
		}
		if (Config.SERVICES_EXCHANGE_BABY_PET_ENABLED)
		{
			ret += "<br>[scripts_services.petevolve.exchange:showBabyPetExchange|Exchange Improved Baby pet]";
		}
		return ret;
	}
}