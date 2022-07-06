package npc.model.residences.clanhall;

import l2mv.gameserver.data.xml.holder.ResidenceHolder;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.residence.ClanHall;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.TimeUtils;

/**
 * @author VISTALL
 * @date 18:16/04.03.2011
 */
public class BrakelInstance extends NpcInstance
{
	public BrakelInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		ClanHall clanhall = ResidenceHolder.getInstance().getResidence(ClanHall.class, 21);
		if (clanhall == null)
		{
			return;
		}
		NpcHtmlMessage html = new NpcHtmlMessage(player, this);
		html.setFile("residence2/clanhall/partisan_ordery_brakel001.htm");
		html.replace("%next_siege%", TimeUtils.toSimpleFormat(clanhall.getSiegeDate().getTimeInMillis()));
		player.sendPacket(html);
	}
}
