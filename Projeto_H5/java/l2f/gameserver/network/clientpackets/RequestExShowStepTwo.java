package l2f.gameserver.network.clientpackets;

import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.PetitionGroupHolder;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.petition.PetitionMainGroup;
import l2f.gameserver.network.serverpackets.ExResponseShowStepTwo;

/**
 * @author VISTALL
 */
public class RequestExShowStepTwo extends L2GameClientPacket
{
	private int _petitionGroupId;

	@Override
	protected void readImpl()
	{
		_petitionGroupId = readC();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
		{
			return;
		}

		PetitionMainGroup group = PetitionGroupHolder.getInstance().getPetitionGroup(_petitionGroupId);
		if (group == null)
		{
			return;
		}

		player.setPetitionGroup(group);
		player.sendPacket(new ExResponseShowStepTwo(player, group));
	}
}