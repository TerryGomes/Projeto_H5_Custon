package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.petition.PetitionMainGroup;
import l2f.gameserver.model.petition.PetitionSubGroup;
import l2f.gameserver.network.serverpackets.ExResponseShowContents;

public class RequestExShowStepThree extends L2GameClientPacket
{
	private int _subId;

	@Override
	protected void readImpl()
	{
		_subId = readC();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
		{
			return;
		}

		PetitionMainGroup group = player.getPetitionGroup();
		if (group == null)
		{
			return;
		}

		PetitionSubGroup subGroup = group.getSubGroup(_subId);
		if (subGroup == null)
		{
			return;
		}

		player.sendPacket(new ExResponseShowContents(subGroup.getDescription(player.getLanguage())));
	}
}