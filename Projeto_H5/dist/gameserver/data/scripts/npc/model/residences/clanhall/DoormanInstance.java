package npc.model.residences.clanhall;

import l2f.gameserver.model.entity.residence.Residence;
import l2f.gameserver.model.pledge.Clan;
import l2f.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 13:19/31.03.2011
 */
public class DoormanInstance extends npc.model.residences.DoormanInstance
{
	public DoormanInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public int getOpenPriv()
	{
		return Clan.CP_CH_ENTRY_EXIT;
	}

	@Override
	public Residence getResidence()
	{
		return getClanHall();
	}
}
