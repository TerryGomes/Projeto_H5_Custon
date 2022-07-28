package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.xml.holder.PetitionGroupHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.petition.PetitionMainGroup;
import l2mv.gameserver.network.serverpackets.ExResponseShowStepTwo;

/**
 * @author VISTALL
 */
public class RequestExShowStepTwo extends L2GameClientPacket
{
	private int _petitionGroupId;

	@Override
	protected void readImpl()
	{
		this._petitionGroupId = this.readC();
	}

	@Override
	protected void runImpl()
	{
		Player player = this.getClient().getActiveChar();
		if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
		{
			return;
		}

		PetitionMainGroup group = PetitionGroupHolder.getInstance().getPetitionGroup(this._petitionGroupId);
		if (group == null)
		{
			return;
		}

		player.setPetitionGroup(group);
		player.sendPacket(new ExResponseShowStepTwo(player, group));
	}
}