package services.petevolve;

import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Summon;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.tables.PetDataTable;
import l2mv.gameserver.tables.PetDataTable.L2Pet;

/**
 * User: darkevil
 * Date: 07.06.2008
 * Time: 0:37:55
 */
public class ibkookaburra extends Functions
{
	private static final int BABY_KOOKABURRA = PetDataTable.BABY_KOOKABURRA_ID;
	private static final int BABY_KOOKABURRA_OCARINA = L2Pet.BABY_KOOKABURRA.getControlItemId();
	private static final int IN_KOOKABURRA_OCARINA = L2Pet.IMPROVED_BABY_KOOKABURRA.getControlItemId();

	public void evolve()
	{
		Player player = getSelf();
		NpcInstance npc = getNpc();
		if (player == null || npc == null)
		{
			return;
		}
		Summon pet = player.getPet();
		if (player.getInventory().getItemByItemId(BABY_KOOKABURRA_OCARINA) == null)
		{
			show("scripts/services/petevolve/no_item.htm", player, npc);
			return;
		}
		else if (pet == null || pet.isDead())
		{
			show("scripts/services/petevolve/evolve_no.htm", player, npc);
			return;
		}
		if (pet.getNpcId() != BABY_KOOKABURRA)
		{
			show("scripts/services/petevolve/no_pet.htm", player, npc);
			return;
		}
		if (Config.ALT_IMPROVED_PETS_LIMITED_USE && !player.isMageClass())
		{
			show("scripts/services/petevolve/no_class_m.htm", player, npc);
			return;
		}
		if (pet.getLevel() < 55)
		{
			show("scripts/services/petevolve/no_level.htm", player, npc);
			return;
		}

		int controlItemId = player.getPet().getControlItemObjId();
		player.getPet().unSummon();

		ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
		control.setItemId(IN_KOOKABURRA_OCARINA);
		control.setEnchantLevel(L2Pet.IMPROVED_BABY_KOOKABURRA.getMinLevel());
		control.setJdbcState(JdbcEntityState.UPDATED);
		control.update();
		player.sendItemList(false);

		show("scripts/services/petevolve/yes_pet.htm", player, npc);
	}
}