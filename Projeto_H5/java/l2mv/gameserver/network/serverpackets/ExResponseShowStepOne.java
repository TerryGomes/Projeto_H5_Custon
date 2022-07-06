package l2mv.gameserver.network.serverpackets;

import java.util.Collection;

import l2mv.gameserver.data.xml.holder.PetitionGroupHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.petition.PetitionMainGroup;
import l2mv.gameserver.utils.Language;

/**
 * @author VISTALL
 */
public class ExResponseShowStepOne extends L2GameServerPacket
{
	private Language _language;

	public ExResponseShowStepOne(Player player)
	{
		_language = player.getLanguage();
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0xAE);
		Collection<PetitionMainGroup> petitionGroups = PetitionGroupHolder.getInstance().getPetitionGroups();
		writeD(petitionGroups.size());
		for (PetitionMainGroup group : petitionGroups)
		{
			writeC(group.getId());
			writeS(group.getName(_language));
		}
	}
}