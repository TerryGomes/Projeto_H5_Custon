package services.petevolve;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.PetDataTable;
import l2mv.gameserver.tables.PetDataTable.L2Pet;
import l2mv.gameserver.templates.npc.NpcTemplate;

public class clanhall extends Functions
{
	// -- Pet ID --
	private static final int GREAT_WOLF = PetDataTable.GREAT_WOLF_ID;
	private static final int WHITE_WOLF = PetDataTable.WGREAT_WOLF_ID;
	private static final int FENRIR = PetDataTable.FENRIR_WOLF_ID;
	private static final int WHITE_FENRIR = PetDataTable.WFENRIR_WOLF_ID;
	private static final int WIND_STRIDER = PetDataTable.STRIDER_WIND_ID;
	private static final int RED_WIND_STRIDER = PetDataTable.RED_STRIDER_WIND_ID;
	private static final int STAR_STRIDER = PetDataTable.STRIDER_STAR_ID;
	private static final int RED_STAR_STRIDER = PetDataTable.RED_STRIDER_STAR_ID;
	private static final int TWILING_STRIDER = PetDataTable.STRIDER_TWILIGHT_ID;
	private static final int RED_TWILING_STRIDER = PetDataTable.RED_STRIDER_TWILIGHT_ID;

	// -- First Item ID --
	private static final int GREAT_WOLF_NECKLACE = L2Pet.GREAT_WOLF.getControlItemId();
	private static final int FENRIR_NECKLACE = L2Pet.FENRIR_WOLF.getControlItemId();
	private static final int WIND_STRIDER_ITEM = L2Pet.STRIDER_WIND.getControlItemId();
	private static final int STAR_STRIDER_ITEM = L2Pet.STRIDER_STAR.getControlItemId();
	private static final int TWILING_STRIDER_ITEM = L2Pet.STRIDER_TWILIGHT.getControlItemId();

	// -- Second Item ID --
	private static final int WHITE_WOLF_NECKLACE = L2Pet.WGREAT_WOLF.getControlItemId();
	private static final int WHITE_FENRIR_NECKLACE = L2Pet.WFENRIR_WOLF.getControlItemId();
	private static final int RED_WS_ITEM = L2Pet.RED_STRIDER_WIND.getControlItemId();
	private static final int RED_SS_ITEM = L2Pet.RED_STRIDER_STAR.getControlItemId();
	private static final int RED_TW_ITEM = L2Pet.RED_STRIDER_TWILIGHT.getControlItemId();

	public void evolve()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}
		show("scripts/services/petevolve/chamberlain.htm", player, npc);
	}

	public void greatsw(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if (player.getInventory().getCountOf(fwd ? GREAT_WOLF_NECKLACE : WHITE_WOLF_NECKLACE) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if (player.getPet() != null)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? GREAT_WOLF_NECKLACE : WHITE_WOLF_NECKLACE);
		if (collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if (npcId == 0)
		{
			return;
		}
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if (petTemplate == null)
		{
			return;
		}
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if (npcId != (fwd ? GREAT_WOLF : WHITE_WOLF))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if (pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_greatw.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? WHITE_WOLF_NECKLACE : GREAT_WOLF_NECKLACE);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage2.obtainItems((fwd ? WHITE_WOLF_NECKLACE : GREAT_WOLF_NECKLACE), 1, 0));
		show("scripts/services/petevolve/end_msg3_gwolf.htm", player, npc);
	}

	public void fenrir(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if (player.getInventory().getCountOf(fwd ? FENRIR_NECKLACE : WHITE_FENRIR_NECKLACE) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if (player.getPet() != null)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? FENRIR_NECKLACE : WHITE_FENRIR_NECKLACE);
		if (collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if (npcId == 0)
		{
			return;
		}
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if (petTemplate == null)
		{
			return;
		}
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if (npcId != (fwd ? FENRIR : WHITE_FENRIR))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if (pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_fenrir.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? WHITE_FENRIR_NECKLACE : FENRIR_NECKLACE);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage2.obtainItems((fwd ? WHITE_FENRIR_NECKLACE : FENRIR_NECKLACE), 1, 0));
		show("scripts/services/petevolve/end_msg2_fenrir.htm", player, npc);
	}

	public void fenrirW(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if (player.getInventory().getCountOf(fwd ? WHITE_WOLF_NECKLACE : WHITE_FENRIR_NECKLACE) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if (player.getPet() != null)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? WHITE_WOLF_NECKLACE : WHITE_FENRIR_NECKLACE);
		if (collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if (npcId == 0)
		{
			return;
		}
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if (petTemplate == null)
		{
			return;
		}
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if (npcId != (fwd ? WHITE_WOLF : WHITE_FENRIR))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if (pet.getLevel() < 70)
		{
			show("scripts/services/petevolve/no_level_gw.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? WHITE_FENRIR_NECKLACE : WHITE_WOLF_NECKLACE);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage2.obtainItems((fwd ? WHITE_FENRIR_NECKLACE : WHITE_WOLF_NECKLACE), 1, 0));
		show("scripts/services/petevolve/yes_wolf.htm", player, npc);
	}

	public void wstrider(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if (player.getInventory().getCountOf(fwd ? WIND_STRIDER_ITEM : RED_WS_ITEM) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if (player.getPet() != null)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? WIND_STRIDER_ITEM : RED_WS_ITEM);
		if (collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if (npcId == 0)
		{
			return;
		}
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if (petTemplate == null)
		{
			return;
		}
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if (npcId != (fwd ? WIND_STRIDER : RED_WIND_STRIDER))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if (pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_WS_ITEM : WIND_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage2.obtainItems((fwd ? RED_WS_ITEM : WIND_STRIDER_ITEM), 1, 0));
		show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}

	public void sstrider(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if (player.getInventory().getCountOf(fwd ? STAR_STRIDER_ITEM : RED_SS_ITEM) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if (player.getPet() != null)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? STAR_STRIDER_ITEM : RED_SS_ITEM);
		if (collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if (npcId == 0)
		{
			return;
		}
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if (petTemplate == null)
		{
			return;
		}
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if (npcId != (fwd ? STAR_STRIDER : RED_STAR_STRIDER))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if (pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_SS_ITEM : STAR_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage2.obtainItems((fwd ? RED_SS_ITEM : STAR_STRIDER_ITEM), 1, 0));
		show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}

	public void tstrider(String[] direction)
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}
		boolean fwd = Integer.parseInt(direction[0]) == 1;

		if (player.getInventory().getCountOf(fwd ? TWILING_STRIDER_ITEM : RED_TW_ITEM) > 1)
		{
			show("scripts/services/petevolve/error_3.htm", player, npc);
			return;
		}
		if (player.getPet() != null)
		{
			show("scripts/services/petevolve/error_4.htm", player, npc);
			return;
		}
		ItemInstance collar = player.getInventory().getItemByItemId(fwd ? TWILING_STRIDER_ITEM : RED_TW_ITEM);
		if (collar == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		int npcId = PetDataTable.getSummonId(collar);
		if (npcId == 0)
		{
			return;
		}
		NpcTemplate petTemplate = NpcHolder.getInstance().getTemplate(npcId);
		if (petTemplate == null)
		{
			return;
		}
		PetInstance pet = PetInstance.restore(collar, petTemplate, player);

		if (npcId != (fwd ? TWILING_STRIDER : RED_TWILING_STRIDER))
		{
			show("scripts/services/petevolve/error_2.htm", player, npc);
			return;
		}
		if (pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/error_lvl_strider.htm", player, npc);
			return;
		}

		collar.setItemId(fwd ? RED_TW_ITEM : TWILING_STRIDER_ITEM);
		collar.setJdbcState(JdbcEntityState.UPDATED);
		collar.update();
		player.sendItemList(false);
		player.sendPacket(SystemMessage2.obtainItems((fwd ? RED_TW_ITEM : TWILING_STRIDER_ITEM), 1, 0));
		show("scripts/services/petevolve/end_msg_strider.htm", player, npc);
	}
}