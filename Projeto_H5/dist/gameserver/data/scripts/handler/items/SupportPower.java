package handler.items;

import l2f.gameserver.handler.items.ItemHandler;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.network.serverpackets.SkillList;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.tables.SkillTable;

public class SupportPower extends ScriptItemHandler implements ScriptFile
{
	private static final int[] ITEM_IDS = new int[]
	{
		24001
	};
	private static final int[] CLASS_IDS = new int[]
	{
		97,
		98,
		100,
		105,
		107,
		112,
		115,
		116
	};

	@Override
	public boolean pickupItem(Playable playable, ItemInstance item)
	{
		return true;
	}

	@Override
	public void onLoad()
	{
		ItemHandler.getInstance().registerItemHandler(this);
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}

	@Override
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}

	@SuppressWarnings("unused")
	private int[] getClassIds()
	{
		return CLASS_IDS;
	}

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if (playable == null || !playable.isPlayer())
		{
			return false;
		}

		Player player = playable.getPlayer();

		int itemId = item.getItemId();
		int classId = player.getBaseClassId();

		if (player.isInOlympiadMode())
		{
			player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
			return false;
		}

		if (player.getLevel() < 76)
		{
			player.sendMessage("Use only a third profession!");
			player.sendMessage("Use only on the main class!");
			return false;
		}

		if (player.getActiveClassId() != player.getBaseClassId())
		{
			player.sendMessage("Use only on the main class!");
			return false;
		}

		switch (classId)
		{
		case 97:// Cardinal
			player.addSkill(SkillTable.getInstance().getInfo(24001, 1), false);
			player.updateStats();
			player.sendPacket(new SkillList(player));
			break;
		case 98:// Hierophant
			player.addSkill(SkillTable.getInstance().getInfo(24002, 1), false);
			player.updateStats();
			player.sendPacket(new SkillList(player));
			break;
		case 100:// SwordMuse
			player.addSkill(SkillTable.getInstance().getInfo(24003, 1), false);
			player.updateStats();
			player.sendPacket(new SkillList(player));
			break;
		case 105:// EvaSaint
			player.addSkill(SkillTable.getInstance().getInfo(24004, 1), false);
			player.updateStats();
			player.sendPacket(new SkillList(player));
			break;
		case 107:// SpectralDancer
			player.addSkill(SkillTable.getInstance().getInfo(24005, 1), false);
			player.updateStats();
			player.sendPacket(new SkillList(player));
			break;
		case 112:// ShillienSaint
			player.addSkill(SkillTable.getInstance().getInfo(24006, 1), false);
			player.updateStats();
			player.sendPacket(new SkillList(player));
			break;
		case 115:// Dominator
			player.addSkill(SkillTable.getInstance().getInfo(24007, 1), false);
			player.updateStats();
			player.sendPacket(new SkillList(player));
			break;
		case 116:// Doomcryer
			player.addSkill(SkillTable.getInstance().getInfo(24008, 1), false);
			player.updateStats();
			player.sendPacket(new SkillList(player));
			break;
		default:
			return false;
		}
		return true;
	}
}