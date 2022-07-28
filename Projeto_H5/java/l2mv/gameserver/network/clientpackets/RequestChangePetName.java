package l2mv.gameserver.network.clientpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.PetInstance;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;

public class RequestChangePetName extends L2GameClientPacket
{
	private String _name;

	@Override
	protected void readImpl()
	{
		this._name = this.readS();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = this.getClient().getActiveChar();
		PetInstance pet = activeChar.getPet() != null && activeChar.getPet().isPet() ? (PetInstance) activeChar.getPet() : null;
		if (pet == null)
		{
			return;
		}

		if (pet.isDefaultName())
		{
			if (this._name.length() < 1 || this._name.length() > 25)
			{
				activeChar.sendPacket(SystemMsg.YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS_IN_LENGTH);
				return;
			}
			pet.setName("." + this._name);
			pet.broadcastCharInfo();
			pet.updateControlItem();
		}
	}
}