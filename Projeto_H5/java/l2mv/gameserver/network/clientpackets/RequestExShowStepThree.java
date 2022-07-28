package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.petition.PetitionMainGroup;
import l2mv.gameserver.model.petition.PetitionSubGroup;
import l2mv.gameserver.network.serverpackets.ExResponseShowContents;

public class RequestExShowStepThree extends L2GameClientPacket
{
	private int _subId;

	@Override
	protected void readImpl()
	{
		this._subId = this.readC();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
		{
			return;
		}

		PetitionMainGroup group = player.getPetitionGroup();
		if (group == null)
		{
			return;
		}

		PetitionSubGroup subGroup = group.getSubGroup(this._subId);
		if (subGroup == null)
		{
			return;
		}

		player.sendPacket(new ExResponseShowContents(subGroup.getDescription(player.getLanguage())));
	}
}