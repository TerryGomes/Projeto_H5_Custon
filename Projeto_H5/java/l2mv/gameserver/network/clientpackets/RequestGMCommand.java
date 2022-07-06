package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.ExGMViewQuestItemList;
import l2mv.gameserver.network.serverpackets.GMHennaInfo;
import l2mv.gameserver.network.serverpackets.GMViewCharacterInfo;
import l2mv.gameserver.network.serverpackets.GMViewItemList;
import l2mv.gameserver.network.serverpackets.GMViewPledgeInfo;
import l2mv.gameserver.network.serverpackets.GMViewQuestInfo;
import l2mv.gameserver.network.serverpackets.GMViewSkillInfo;
import l2mv.gameserver.network.serverpackets.GMViewWarehouseWithdrawList;
//import l2mv.gameserver.network.serverpackets.ShortCutInit;
//import l2mv.gameserver.network.serverpackets.SkillCoolTime;
//import l2mv.gameserver.network.serverpackets.SkillList;

public class RequestGMCommand extends L2GameClientPacket
{
	private String _targetName;
	private int _command;

	@Override
	protected void readImpl()
	{
		_targetName = readS();
		_command = readD();
		// readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		Player target = World.getPlayer(_targetName);
		if (player == null || target == null || !player.getPlayerAccess().CanViewChar)
		{
			return;
		}

		switch (_command)
		{
		case 1:
			player.sendPacket(new GMViewCharacterInfo(target));
			player.sendPacket(new GMHennaInfo(target));
			break;
		case 2:
			if (target.getClan() != null)
			{
				player.sendPacket(new GMViewPledgeInfo(target));
			}
			break;
		case 3:
			player.sendPacket(new GMViewSkillInfo(target));
			break;
		case 4:
			player.sendPacket(new GMViewQuestInfo(target));
			break;
		case 5:
			ItemInstance[] items = target.getInventory().getItems();
			int questSize = 0;
			for (ItemInstance item : items)
			{
				if (item.getTemplate().isQuest())
				{
					questSize++;
				}
			}
			player.sendPacket(new GMViewItemList(target, items, items.length - questSize));
			player.sendPacket(new ExGMViewQuestItemList(target, items, questSize));

			player.sendPacket(new GMHennaInfo(target));
			// TODO: This is a custom solution for not losing skill bar.
			// It should be fixed the proper way.
			// player.sendPacket(new ShortCutInit(player));
			// player.sendPacket(new SkillList(player));
			// player.sendPacket(new SkillCoolTime(player));
			break;
		case 6:
			player.sendPacket(new GMViewWarehouseWithdrawList(target));
			break;
		}
	}
}