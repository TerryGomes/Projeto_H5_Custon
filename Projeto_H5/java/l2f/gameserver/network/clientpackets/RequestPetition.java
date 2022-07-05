package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.instancemanager.PetitionManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.petition.PetitionMainGroup;
import l2f.gameserver.model.petition.PetitionSubGroup;

public final class RequestPetition extends L2GameClientPacket
{
	private String _content;
	private int _type;

	@Override
	protected void readImpl()
	{
		_content = readS();
		_type = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		if (Config.EX_NEW_PETITION_SYSTEM)
		{
			PetitionMainGroup group = player.getPetitionGroup();
			if (group == null)
			{
				return;
			}

			PetitionSubGroup subGroup = group.getSubGroup(_type);
			if (subGroup == null)
			{
				return;
			}

			subGroup.getHandler().handle(player, _type, _content);
		}
		else
		{
			PetitionManager.getInstance().handle(player, _type, _content);
		}
	}
}
